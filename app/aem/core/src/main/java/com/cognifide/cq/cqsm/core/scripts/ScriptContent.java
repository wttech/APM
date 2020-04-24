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
package com.cognifide.cq.cqsm.core.scripts;

import com.day.cq.commons.jcr.JcrConstants;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
@Getter
public class ScriptContent {

  public static final String APM_SCRIPT = "apm:Script";

  public static final String APM_EXECUTION_ENABLED = "apm:executionEnabled";

  public static final String APM_EXECUTION_MODE = "apm:executionMode";

  public static final String APM_EXECUTION_ENVIRONMENT = "apm:executionEnvironment";

  public static final String APM_EXECUTION_HOOK = "apm:executionHook";

  public static final String APM_EXECUTION_SCHEDULE = "apm:executionSchedule";

  public static final String APM_EXECUTION_LAST = "apm:executionLast";

  public static final String APM_CHECKSUM = "apm:checksum";

  public static final String APM_PUBLISH_RUN = "apm:publishRun";

  public static final String APM_REPLICATED_BY = "apm:replicatedBy";

  public static final String APM_VERIFIED = "apm:verified";

  @Inject
  @Named(APM_EXECUTION_ENABLED)
  @Optional
  private Boolean executionEnabled;

  @Inject
  @Named(APM_EXECUTION_MODE)
  @Optional
  private String executionMode;

  @Inject
  @Named(APM_EXECUTION_ENVIRONMENT)
  @Optional
  private String executionEnvironment;

  @Inject
  @Named(APM_EXECUTION_HOOK)
  @Optional
  private String executionHook;

  @Inject
  @Named(APM_EXECUTION_SCHEDULE)
  @Optional
  private Date executionSchedule;

  @Inject
  @Named(APM_EXECUTION_LAST)
  @Optional
  private Date executionLast;

  @Inject
  @Named(APM_CHECKSUM)
  @Optional
  private String checksum;

  @Inject
  @Named(APM_PUBLISH_RUN)
  @Optional
  private Boolean publishRun;

  @Inject
  @Named(APM_REPLICATED_BY)
  @Optional
  private String replicatedBy;

  @Inject
  @Named(APM_VERIFIED)
  @Optional
  private Boolean verified;

  @Inject
  @Named(JcrConstants.JCR_LASTMODIFIED)
  @Optional
  private Date lastModified;

  @Inject
  @Named(JcrConstants.JCR_DATA)
  @Optional
  private String data; //FIXME lazy load would be better here

}
