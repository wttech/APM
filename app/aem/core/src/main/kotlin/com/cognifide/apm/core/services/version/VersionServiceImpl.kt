/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.core.services.version

import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.api.services.ScriptFinder
import com.cognifide.apm.core.Property
import com.cognifide.apm.core.grammar.ReferenceFinder
import com.cognifide.apm.core.grammar.ScriptExecutionException
import com.cognifide.apm.core.scripts.MutableScriptWrapper
import com.cognifide.apm.core.scripts.ScriptNode
import com.day.cq.commons.jcr.JcrUtil
import com.day.crx.JcrConstants
import org.apache.commons.codec.digest.DigestUtils
import org.apache.jackrabbit.commons.JcrUtils
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.slf4j.LoggerFactory
import javax.jcr.Node
import javax.jcr.RepositoryException
import javax.jcr.Session

@Component(
        service = [VersionService::class],
        property = [
            Property.DESCRIPTION + "APM Version Service",
            Property.VENDOR
        ])
class VersionServiceImpl : VersionService {

    private val logger = LoggerFactory.getLogger(VersionServiceImpl::class.java)

    @Reference
    @Transient
    private lateinit var scriptFinder: ScriptFinder

    override fun getScriptVersion(resolver: ResourceResolver, script: Script): ScriptVersion {
        val scriptVersionPath = getScriptVersionPath(script)
        return resolver.getResource(scriptVersionPath)?.adaptTo(ScriptVersionModel::class.java)
                ?: ScriptVersionModel(script.path)
    }

    override fun getVersionPath(script: Script): String {
        return "$versionsRoot/${script.normalizedPath()}/${script.checksum}/$scriptNodeName"
    }

    override fun countChecksum(root: Iterable<Script>): String {
        val checksums = root
                .asSequence()
                .map { it.data }
                .map { DigestUtils.md5Hex(it) }
                .reduce { previous, current -> previous + current }
        return DigestUtils.md5Hex(checksums)
    }

    override fun updateVersionIfNeeded(resolver: ResourceResolver, vararg scripts: Script) {
        val referenceFinder = ReferenceFinder(scriptFinder, resolver)
        scripts.forEach { script ->
            try {
                val subtree = referenceFinder.findReferences(script)
                val checksum = countChecksum(subtree)
                val scriptVersion = getScriptVersion(resolver, script)
                if (checksum != script.checksum) {
                    MutableScriptWrapper(script).apply {
                        setChecksum(checksum)
                    }
                }
                if (checksum != scriptVersion.lastChecksum) {
                    createVersion(resolver, script)
                }
            } catch (e: ScriptExecutionException) {
                logger.error(e.message)
            }
        }
    }

    private fun createVersion(resolver: ResourceResolver, script: Script) {
        try {
            val session = resolver.adaptTo(Session::class.java)!!
            val scriptNode = createScriptNode(script, session)
            val versionNode = createVersionNode(scriptNode, script, session)
            copyScriptContent(versionNode, script, session)
            session.save()
            resolver.commit()
        } catch (e: Exception) {
            logger.error("Issues with saving to repository while logging script execution", e)
        }
    }

    @Throws(RepositoryException::class)
    private fun createScriptNode(script: Script, session: Session): Node {
        val path = getScriptVersionPath(script)
        val scriptHistory = JcrUtils.getOrCreateByPath(path, "sling:OrderedFolder", JcrConstants.NT_UNSTRUCTURED, session, true)
        scriptHistory.setProperty("scriptPath", script.path)
        scriptHistory.setProperty("lastChecksum", script.checksum)
        return scriptHistory
    }

    private fun getScriptVersionPath(script: Script) = "$versionsRoot/${script.normalizedPath()}"

    @Throws(RepositoryException::class)
    private fun createVersionNode(parent: Node, script: Script, session: Session): Node {
        val path = parent.path + "/" + script.checksum
        return JcrUtils.getOrCreateByPath(path, "sling:OrderedFolder", "sling:OrderedFolder", session, true)
    }

    @Throws(RepositoryException::class)
    private fun copyScriptContent(parent: Node, script: Script, session: Session): Node {
        if (!parent.hasNode(scriptNodeName)) {
            val source = session.getNode(script.path)
            val file = JcrUtil.copy(source, parent, scriptNodeName)
            file.addMixin(ScriptNode.APM_SCRIPT)
            return file
        }
        return parent.getNode(scriptNodeName)
    }

    private fun Script.normalizedPath(): String {
        return path.replace("/", "_").substring(1)
    }

    companion object {
        const val versionsRoot = "/var/apm/versions"
        const val scriptNodeName = "script"
    }
}