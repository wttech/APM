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

import java.util.Calendar;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = { Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HistoryRowModel {

    @Inject
    private String scriptName;

    @Inject
    private String authorName;

    @Inject
    private Calendar lastRun;

    @Inject
    private Calendar lastDryRun;

    @Inject
    private Calendar lastModified;

    public String getScriptName() {
        return scriptName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Calendar getLastRun() {
        return lastRun;
    }

    public Calendar getLastDryRun() {
        return lastDryRun;
    }

    public Calendar getLastModified() {
        return lastModified;
    }
}
