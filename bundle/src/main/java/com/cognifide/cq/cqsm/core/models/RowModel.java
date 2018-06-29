/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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
package com.cognifide.cq.cqsm.core.models;

import com.cognifide.cq.cqsm.core.scripts.ScriptImpl;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Set;

@Model(adaptables = SlingHttpServletRequest.class)
public class RowModel {

    @Self
    private SlingHttpServletRequest request;

    private ScriptImpl script;

    private String scriptName;

    private boolean isFolder;

    private static final Set<String> FOLDER_TYPES = ImmutableSet.of(JcrConstants.NT_FOLDER, "sling:OrderedFolder");

    @PostConstruct
    public void init() {
        Resource resource = request.getResource();
        script = resource.adaptTo(ScriptImpl.class);
        scriptName = resource.getName();
        isFolder = FOLDER_TYPES.contains(resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, StringUtils.EMPTY));
    }

    public String getScriptName() {
        return scriptName;
    }

    public String getAuthor() {
        return script != null ? script.getAuthor() : "";
    }

    public Date getExecutionLast() {
        return script != null ? script.getExecutionLast() : null;
    }

    public Date getLastModified() {
        return script != null ? script.getLastModified() : null;
    }

    public Date getExecutionSchedule() {
        return script != null ? script.getExecutionSchedule() : null;
    }

    public Boolean isExecutionEnabled() {
        return script != null ? script.isExecutionEnabled() : null;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public String getResourceType() {
        return "apm/components/dashboard/row";
    }

    public Boolean isValid() {
        return script != null ? script.isValid() : null;
    }

}
