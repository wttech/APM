/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.core.endpoints

import com.cognifide.apm.core.Apm
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.endpoints.response.ResponseEntity
import com.cognifide.apm.core.endpoints.response.badRequest
import com.cognifide.apm.core.endpoints.response.ok
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.commons.jcr.JcrUtil
import org.apache.commons.lang3.StringUtils
import org.apache.sling.api.resource.ModifiableValueMap
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.factory.ModelFactory
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import javax.jcr.Session
import javax.servlet.Servlet

@Component(
        service = [Servlet::class],
        property = [
            Property.PATH + "/bin/apm/scripts/move",
            Property.METHOD + "POST",
            Property.DESCRIPTION + "APM Script Move Servlet",
            Property.VENDOR
        ])
class ScriptMoveServlet : AbstractFormServlet<ScriptMoveForm>(ScriptMoveForm::class.java) {

    @Reference
    override fun setup(modelFactory: ModelFactory) {
        this.modelFactory = modelFactory
    }

    override fun doPost(form: ScriptMoveForm, resolver: ResourceResolver): ResponseEntity<Any> {
        return try {
            val session = resolver.adaptTo(Session::class.java)!!
            val dest = StringUtils.defaultIfEmpty(form.dest, StringUtils.substringBeforeLast(form.path, "/"))
            val rename = if (containsExtension(form.path)) {
                form.rename + if (containsExtension(form.rename)) "" else Apm.FILE_EXT
            } else {
                JcrUtil.createValidName(form.rename)
            }
            var destPath = "$dest/$rename"
            if (form.path != destPath) {
                destPath = createUniquePath(destPath, resolver)
                session.move(form.path, destPath)
                session.save()
            }
            if (!containsExtension(form.path)) {
                val valueMap = resolver.getResource(destPath)?.adaptTo(ModifiableValueMap::class.java)
                valueMap?.put(JcrConstants.JCR_TITLE, form.rename)
            }
            resolver.commit()
            ok {
                message = "Item successfully moved"
            }
        } catch (e: Exception) {
            badRequest {
                message = e.message ?: "Errors while moving item"
            }
        }
    }

    private fun containsExtension(path: String) = path.endsWith(Apm.FILE_EXT)

    private fun createUniquePath(pathWithExtension: String, resolver: ResourceResolver): String {
        val path = StringUtils.substringBeforeLast(pathWithExtension, Apm.FILE_EXT)
        val extension = if (containsExtension(pathWithExtension)) Apm.FILE_EXT else ""
        var counter = 0
        while (resolver.getResource(path + (if (counter > 0) counter else "") + extension) != null) {
            counter++
        }
        return path + (if (counter > 0) counter else "") + extension
    }

}