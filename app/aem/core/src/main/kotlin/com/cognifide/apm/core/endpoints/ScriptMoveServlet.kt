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

import com.cognifide.apm.core.Property
import com.cognifide.apm.core.endpoints.response.ResponseEntity
import com.cognifide.apm.core.endpoints.response.badRequest
import com.cognifide.apm.core.endpoints.response.ok
import com.day.cq.commons.jcr.JcrUtil
import org.apache.commons.lang.StringUtils
import org.apache.sling.api.resource.ModifiableValueMap
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.factory.ModelFactory
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import javax.jcr.Session
import javax.servlet.Servlet

@Component(
        immediate = true,
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

    override fun doPost(form: ScriptMoveForm, resourceResolver: ResourceResolver): ResponseEntity<Any> {
        return try {
            val session = resourceResolver.adaptTo(Session::class.java)!!
            val dest = if (form.dest.isEmpty()) {
                StringUtils.substringBeforeLast(form.path, "/")
            } else {
                form.dest
            }
            val rename = if (form.path.endsWith(".apm")) {
                form.rename + if (form.rename.endsWith(".apm")) "" else ".apm"
            } else {
                JcrUtil.createValidName(form.rename)
            }
            session.move(form.path, "$dest/$rename")
            session.save()
            if (!form.path.endsWith(".apm")) {
                val valueMap = resourceResolver.getResource("$dest/$rename")?.adaptTo(ModifiableValueMap::class.java)
                valueMap?.put("jcr:title", form.rename)
            }
            resourceResolver.commit()
            ok {
                message = "Item successfully moved"
            }
        } catch (e: Exception) {
            badRequest {
                message = e.message ?: "Errors while moving item"
            }
        }
    }

}