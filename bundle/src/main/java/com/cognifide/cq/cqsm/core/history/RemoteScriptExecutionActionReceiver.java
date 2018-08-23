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
package com.cognifide.cq.cqsm.core.history;

import static com.cognifide.cq.cqsm.core.history.HistoryImpl.REPLICATE_ACTION;

import com.cognifide.actions.api.ActionReceiver;
import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.history.InstanceDetails;
import com.cognifide.cq.cqsm.api.history.ModifiableEntryBuilder;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.logger.ProgressEntry;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.utils.InstanceTypeProvider;
import com.cognifide.cq.cqsm.core.progress.ProgressHelper;
import com.cognifide.cq.cqsm.core.progress.ProgressImpl;
import com.cognifide.cq.cqsm.core.scripts.ScriptImpl;
import com.cognifide.cq.cqsm.core.utils.sling.OperateCallback;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;
import com.day.cq.replication.ReplicationAction;
import com.google.common.base.Preconditions;
import java.util.Calendar;
import java.util.List;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;

@Service
@Component(immediate = true)
public class RemoteScriptExecutionActionReceiver implements ActionReceiver {

	@Reference
	private History history;

	@Reference
	private InstanceTypeProvider instanceTypeProvider;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	public void handleAction(final ValueMap valueMap) {
		Preconditions
				.checkState(instanceTypeProvider.isOnAuthor(), "Action Receiver has to be called in author");
		String userId = valueMap.get(ReplicationAction.PROPERTY_USER_ID, String.class);
		SlingHelper.operateTraced(resolverFactory, userId, new OperateCallback() {
			@Override
			public void operate(ResourceResolver resolver) throws Exception {
				//FIXME would be lovely to cast ValueMap -> ModifiableEntryBuilder
				String scriptLocation = valueMap.get(ModifiableEntryBuilder.FILE_PATH_PROPERTY, String.class);
				Resource scriptResource = resolver.getResource(scriptLocation);
				Script script = scriptResource.adaptTo(ScriptImpl.class);
				InstanceDetails instanceDetails = getInstanceDetails(valueMap);
				Progress progress = getProgress(valueMap, resolver.getUserID());
				Calendar executionTime = getCalendar(valueMap);
				Mode mode = getMode(valueMap);
				history.logRemote(script, mode, progress, instanceDetails, executionTime);
			}
		});
	}

	@Override
	public String getType() {
		return REPLICATE_ACTION;
	}

	private Mode getMode(ValueMap valueMap) {
		return Mode.fromString(valueMap.get(ModifiableEntryBuilder.EXECUTOR_PROPERTY, String.class),
				Mode.AUTOMATIC_RUN);
	}

	private Calendar getCalendar(ValueMap valueMap) {
		return valueMap.get(ModifiableEntryBuilder.EXECUTION_TIME_PROPERTY, Calendar.class);
	}

	private Progress getProgress(ValueMap valueMap, String userID) {
		List<ProgressEntry> progressEntries = ProgressHelper
				.fromJson(valueMap.get(ModifiableEntryBuilder.PROGRESS_LOG_PROPERTY, String.class));
		return new ProgressImpl(userID, progressEntries);
	}

	private InstanceDetails getInstanceDetails(ValueMap valueMap) {
		InstanceDetails.InstanceType instanceType = InstanceDetails.InstanceType
				.fromString(valueMap.get(ModifiableEntryBuilder.INSTANCE_TYPE_PROPERTY, String.class));
		return new InstanceDetails(
				valueMap.get(ModifiableEntryBuilder.INSTANCE_HOSTNAME_PROPERTY, String.class), instanceType);
	}
}