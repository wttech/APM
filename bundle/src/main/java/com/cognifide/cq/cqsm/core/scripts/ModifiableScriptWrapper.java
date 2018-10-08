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

import com.cognifide.cq.cqsm.api.scripts.ExecutionMode;
import com.cognifide.cq.cqsm.api.scripts.ModifiableScript;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.core.utils.ResourceMixinUtil;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public class ModifiableScriptWrapper implements ModifiableScript {

	private ResourceResolver resolver;

	private Script script;

	public ModifiableScriptWrapper(ResourceResolver resolver, Script script) {
		this.resolver = resolver;
		this.script = script;
	}

	@Override
	public void setExecutionSchedule(Date date) throws PersistenceException {
		setProperty(ScriptContent.CQSM_EXECUTION_SCHEDULE, date);
	}

	@Override
	public void setExecuted(Boolean flag) throws PersistenceException {
		setProperty(ScriptContent.CQSM_EXECUTION_LAST, flag ? new Date() : null);
		script.getChecksum().update(resolver, resolver.getResource(script.getPath()));
	}

	@Override
	public void setExecutionEnabled(Boolean flag) throws PersistenceException {
		setProperty(ScriptContent.CQSM_EXECUTION_ENABLED, flag);
	}

	@Override
	public void setDryRunStatus(Boolean flag) throws PersistenceException {
		setProperty(ScriptContent.CQSM_DRY_RUN_SUCCESSFUL, flag);
	}

	@Override
	public void setPublishRun(Boolean flag) throws PersistenceException {
		setProperty(ScriptContent.CQSM_PUBLISH_RUN, flag);
	}

	@Override
	public void setDryRunExecution(Date executionDate) throws PersistenceException {
		setProperty(ScriptContent.CQSM_DRY_RUN_LAST, executionDate);
	}

	@Override
	public void setReplicatedBy(String userId) throws PersistenceException {
		setProperty(ScriptContent.CQSM_REPLICATED_BY, userId);
	}

	@Override
	public void setExecutionMode(ExecutionMode mode) throws PersistenceException {
		setProperty(ScriptContent.CQSM_EXECUTION_MODE, mode.name());
	}

	@Override
	public void setValid(Boolean flag) throws PersistenceException {
		setProperty(ScriptContent.CQSM_VERIFIED, flag);
	}

	public void setProperty(String name, Object value) throws PersistenceException {
		Resource resource = resolver.getResource(script.getPath());
		ModifiableValueMap vm = resource.getChild(JcrConstants.JCR_CONTENT).adaptTo(ModifiableValueMap.class);
		ResourceMixinUtil.addMixin(vm, ScriptContent.CQSM_FILE);
		vm.put(name, convertValue(value));

		resource.getResourceResolver().commit();
	}

	private Object convertValue(Object obj) {
		if (obj instanceof Date) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date) obj);

			return calendar;
		}

		return obj;
	}

}
