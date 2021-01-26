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
package com.cognifide.apm.core.endpoints;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.event.jobs.Job;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.jobs.ScriptRunnerJobManager;
import com.cognifide.apm.core.scriptrunnerjob.JobProgressOutput;
import com.cognifide.apm.core.utils.ServletUtils;
import com.google.common.collect.ImmutableMap;

@Component(
		immediate = true,
		service = Servlet.class,
		property = {
				Property.PATH + "/bin/cqsm/run-background",
				Property.METHOD + "GET",
				Property.METHOD + "POST",
				Property.DESCRIPTION + "CQSM Servlet for running scripts in background and checking theirs status",
				Property.VENDOR
		}
)
/**
 * @deprecated use {@link ScriptExecutionServlet} instead
 */
@Deprecated
public class ScriptRunBackgroundServlet extends SlingAllMethodsServlet {

  private static final String BACKGROUND_RESPONSE_TYPE = "background";

  private static final String ERROR_RESPONSE_TYPE = "error";

  private static final String FILE_REQUEST_PARAMETER = "file";

  private static final String MODE_REQUEST_PARAMETER = "mode";

  private static final String ID_REQUEST_PARAMETER = "id";

  @Reference
  private ScriptRunnerJobManager scriptRunnerJobManager;

  @Reference
  private ScriptFinder scriptFinder;

  @Override
  protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
      throws IOException {

    final String searchPath = request.getParameter(FILE_REQUEST_PARAMETER);
    final ResourceResolver resolver = request.getResourceResolver();
    final Script script = scriptFinder.find(searchPath, resolver);

    final boolean isValid = script.isValid();
    final boolean isExecutable = script.isLaunchEnabled();

    if (!(isValid && isExecutable)) {
      ServletUtils.writeMessage(response, ERROR_RESPONSE_TYPE, String.format("Script '%s' cannot be processed. " +
              "Script needs to be executable and valid. Actual script status: valid - %s, executable - %s",
          searchPath, isValid, isExecutable));
      return;
    }

    BackgroundJobParameters parameters = getParameters(request, response);
    if (parameters == null) {
      return;
    }

    Job job = scriptRunnerJobManager.scheduleJob(parameters);
    ServletUtils.writeMessage(response, BACKGROUND_RESPONSE_TYPE, BACKGROUND_RESPONSE_TYPE, createMapWithJobIdKey(job));
  }

  @Override
  protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
      throws IOException {
    final String id = request.getParameter(ID_REQUEST_PARAMETER);
    if (id == null) {
      return;
    }
    JobProgressOutput jobProgressOutput = scriptRunnerJobManager.checkJobStatus(id);
    ServletUtils.writeJson(response, jobProgressOutput);
  }

  private BackgroundJobParameters getParameters(final SlingHttpServletRequest request,
      final SlingHttpServletResponse response) throws IOException {
    final String searchPath = request.getParameter(FILE_REQUEST_PARAMETER);
    final String modeName = request.getParameter(MODE_REQUEST_PARAMETER);
    final String userName = request.getResourceResolver().getUserID();

    if (StringUtils.isEmpty(searchPath)) {
      ServletUtils.writeMessage(response, ERROR_RESPONSE_TYPE,
          "Please set the script file name: -d \"file=[name]\"");
      return null;
    }

    if (StringUtils.isEmpty(modeName)) {
      ServletUtils.writeMessage(response, ERROR_RESPONSE_TYPE, "Running mode not specified.");
      return null;
    }

    return new BackgroundJobParameters(searchPath, modeName, userName);
  }

  private Map<String, Object> createMapWithJobIdKey(Job job) {
    return ImmutableMap.<String, Object>builder().put(ID_REQUEST_PARAMETER, job.getId()).build();
  }

}
