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
package com.cognifide.cq.cqsm.core.servlets.run;

import com.cognifide.cq.cqsm.api.scriptrunnerjob.JobProgressOutput;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.jobs.ScriptRunnerJobManager;
import com.cognifide.cq.cqsm.core.servlets.run.ScriptRunParameters;
import com.cognifide.cq.cqsm.core.utils.ServletUtils;
import com.google.common.collect.ImmutableMap;
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
public class ScriptRunBackgroundServlet extends SlingAllMethodsServlet {

  private static final String BACKGROUND_RESPONSE_TYPE = "background";

  private static final String ERROR_RESPONSE_TYPE = "error";

  private static final String ID_REQUEST_PARAMETER = "id";

  @Reference
  private ScriptRunnerJobManager scriptRunnerJobManager;

  @Reference
  private ScriptFinder scriptFinder;

  @Override
  protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
      throws IOException {


    final ScriptRunParameters parameters = getParameters(request);

    if (!parameters.areValid()) {
      updateResponse(parameters, response);
      return;
    }

    final ResourceResolver resolver = request.getResourceResolver();
    final Script script = scriptFinder.find(parameters.getSearchPath(), resolver);

    final boolean isValid = script.isValid();
    final boolean isExecutable = script.isExecutionEnabled();

    if (!(isValid && isExecutable)) {
      ServletUtils.writeMessage(response, ERROR_RESPONSE_TYPE, String.format("Script '%s' cannot be processed. " +
              "Script needs to be executable and valid. Actual script status: valid - %s, executable - %s",
          parameters.getSearchPath(), isValid, isExecutable));
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

  private ScriptRunParameters getParameters(final SlingHttpServletRequest request) {
    final String searchPath = request.getParameter(ScriptRunParameters.SCRIPT_PATH_PROPERTY_NAME);
    final String modeName = request.getParameter(ScriptRunParameters.MODE_NAME_PROPERTY_NAME);
    final String userName = request.getUserPrincipal().getName();

    return new ScriptRunParameters(searchPath, modeName, userName);
  }

  private void updateResponse(ScriptRunParameters params, final SlingHttpServletResponse response)  throws IOException{
    if (StringUtils.isEmpty(params.getSearchPath())) {
      ServletUtils.writeMessage(response, ERROR_RESPONSE_TYPE,
              "Please set the script file name: -d \"file=[name]\"");
    }

    if (StringUtils.isEmpty(params.getModeName())) {
      ServletUtils.writeMessage(response, ERROR_RESPONSE_TYPE, "Running mode not specified.");
    }
  }

  private Map<String, Object> createMapWithJobIdKey(Job job) {
    return ImmutableMap.<String, Object>builder().put(ID_REQUEST_PARAMETER, job.getId()).build();
  }

}
