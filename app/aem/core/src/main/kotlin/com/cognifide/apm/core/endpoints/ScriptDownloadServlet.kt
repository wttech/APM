package com.cognifide.apm.core.endpoints

import com.cognifide.apm.core.services.version.VersionServiceImpl
import org.apache.commons.lang.StringUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.osgi.service.component.annotations.Component
import org.slf4j.LoggerFactory
import java.net.URLEncoder
import javax.jcr.RepositoryException
import javax.jcr.Session
import javax.servlet.Servlet
import com.cognifide.apm.core.Property

@Component(
        immediate = true,
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/scripts/fileDownload",
            Property.METHOD + "GET",
            Property.DESCRIPTION + "APM File Download Servlet",
            Property.VENDOR
        ])
class ScriptDownloadServlet : SlingAllMethodsServlet() {

    private val logger = LoggerFactory.getLogger(ScriptDownloadServlet::class.java)

    override fun doGet(request: SlingHttpServletRequest, response: SlingHttpServletResponse) {
        val fileName = request.getParameter("filename")
        val filePath = request.getParameter("filepath")
        val mode = request.getParameter("mode")

        try {
            val resourceResolver = request.resourceResolver
            val session = resourceResolver.adaptTo(Session::class.java)!!

            if (!("view").equals(mode)) {
                response.setContentType("application/octet-stream")
                response.setHeader("Content-Disposition",
                        "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"))
            }

            val path = StringUtils.replace(filePath, "_jcr_content", "jcr:content")
            val jcrContent = session.getNode("$path/jcr:content")
            val input = jcrContent.getProperty("jcr:data").getBinary().getStream()

            session.save()
            var read : Int
            val bytes = ByteArray(ScriptDownloadServlet.BYTES_DOWNLOAD)
            val os = response.outputStream

            while (input.read(bytes).also { read = it } != -1) {
                os.write(bytes, 0, read)
            }
            input.close()
            os.flush()
            os.close()
        } catch (e: RepositoryException) {
            logger.error(e.message, e)
            //todo change redirect /apm/?
            response.sendRedirect("/etc/cqsm.html")
        }
    }

    companion object {
        const val BYTES_DOWNLOAD = 1024
    }
}
