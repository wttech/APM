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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = { Resource.class })
public class DashboardRowModel {

    @Self
    private SlingHttpServletRequest request;

    private Resource resource;

    private ResourceResolver resolver;


    private String folder = "folder";
    private String resourceType = "rt";
    private String title = "title";

    private String author = "author";
    private String valid = "valid";
    private String executable = "exe";
    private String lastUpdated = "lu";
    private String lastRun = "lr";
    private String lastDryRun = "ldr";

    protected void postConstruct() {
        this.resource = request.getResource();
        this.resolver = request.getResourceResolver();
    }

    public String getFolder() {
        return folder;
    }

    public String getResourceType() {
        return resource.getResourceType();
    }

    public String getTitle() {
        return resource.getName();
    }

    public String getAuthor() {
        return author;
    }

    public String getValid() {
        return valid;
    }

    public String getExecutable() {
        return executable;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getLastRun() {
        return lastRun;
    }

    public String getLastDryRun() {
        return lastDryRun;
    }



}
