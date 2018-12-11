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

import com.cognifide.actions.api.ActionSendException;
import com.cognifide.actions.api.ActionSubmitter;
import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.history.Entry;
import com.cognifide.cq.cqsm.api.history.InstanceDetails;
import com.cognifide.cq.cqsm.api.history.ModifiableEntryBuilder;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.progress.ProgressHelper;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.utils.InstanceTypeProvider;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.scripts.ScriptContent;
import com.cognifide.cq.cqsm.core.utils.sling.OperateCallback;
import com.cognifide.cq.cqsm.core.utils.sling.ResolveCallback;
import com.cognifide.cq.cqsm.core.utils.sling.SlingHelper;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
		immediate = true,
		service = History.class,
		property = {
				Property.DESCRIPTION + "CQSM History Service",
				Property.VENDOR
		}
)
public class HistoryImpl implements History {

	private static final Logger LOG = LoggerFactory.getLogger(HistoryImpl.class);

	private static final String HISTORY_PATH = "/conf/apm/history";

	private static final String HISTORY_COMPONENT = "cqsmHistory";

	private static final String HISTORY_COMPONENT_RESOURCE_TYPE = "cqsm/core/components/cqsmHistory";

	private static final String ENTRY_PATH = "/conf/apm/history/jcr:content/cqsmHistory";

	public static final String REPLICATE_ACTION = "com/cognifide/actions/cqsm/history/replicate";

	@Reference
	private ActionSubmitter actionSubmitter;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private InstanceTypeProvider instanceTypeProvider;

	@Override
	public Entry log(Script script, Mode mode, Progress progressLogger) {
		InstanceDetails.InstanceType instanceDetails = instanceTypeProvider.isOnAuthor() ?
				InstanceDetails.InstanceType.AUTHOR :
				InstanceDetails.InstanceType.PUBLISH;
		return log(script, mode, progressLogger, instanceDetails, getHostname(), Calendar.getInstance());
	}

	@Override
	public Entry logRemote(Script script, Mode mode, Progress progressLogger, InstanceDetails instanceDetails,
			Calendar executionTime) {
		return log(script, mode, progressLogger, instanceDetails.getInstanceType(),
				instanceDetails.getHostname(), executionTime);
	}

	@Override
	public List<Entry> findAll() {
		return SlingHelper.resolveDefault(resolverFactory, new ResolveCallback<List<Entry>>() {
			@Override
			public List<Entry> resolve(ResourceResolver resolver) {
				return findAllResource(resolver)
						.stream()
						.map(resource -> resource.adaptTo(Entry.class))
						.collect(Collectors.toList());
			}
		}, Collections.<Entry>emptyList());
	}

	@Override
	public List<Resource> findAllResource(ResourceResolver resourceResolver) {
		final Resource historyCatalogResource = resourceResolver.getResource(HistoryImpl.ENTRY_PATH);
		return Optional.ofNullable(historyCatalogResource).map(resource -> {
			List<Resource> result = Lists.newLinkedList();
			resource.listChildren()
					.forEachRemaining(result::add);
			return result;
		}).orElseGet(() -> {
			LOG.warn("History resource can't be found at: {}", HistoryImpl.ENTRY_PATH);
			return Collections.emptyList();
		});
	}

	@Override
	public void replicate(final Entry entry, String userId) throws RepositoryException {
		if (actionSubmitter == null) {
			LOG.warn(String.format("History entry '%s' replication cannot be performed on author instance",
					entry.getPath()));
			return;
		}
		SlingHelper.operateTraced(resolverFactory, userId, new OperateCallback() {
			@Override
			public void operate(ResourceResolver resolver) throws Exception {
				Resource resource = resolver.getResource(entry.getPath());
				if (resource != null) {
					try {
						LOG.warn("Sending action {} to action submitter", REPLICATE_ACTION);
						Map<String, Object> properties = new HashMap<>(resource.getValueMap());
						properties.put(ReplicationAction.PROPERTY_USER_ID, resolver.getUserID());
						actionSubmitter.sendAction(REPLICATE_ACTION, properties);
						LOG.warn("Action {} was sent to action submitter", REPLICATE_ACTION);
					} catch (ActionSendException e) {
						LOG.info("Cannot send action", e);
					}
				}
			}
		});
	}

	@Override
	public Entry find(final String path) {
		return SlingHelper.resolveDefault(resolverFactory, new ResolveCallback<Entry>() {
			@Override
			public Entry resolve(ResourceResolver resolver) {
				Entry result = null;
				for (String resourcePath : Arrays.asList(path, path.replace("_cqsm", ".cqsm"))) {
					Resource resource = resolver.getResource(resourcePath);
					if (resource != null) {
						result = resource.adaptTo(Entry.class);
						break;
					}
				}
				return result;
			}
		}, null);
	}

	private Entry log(final Script script, final Mode mode, final Progress progressLogger,
			final InstanceDetails.InstanceType instanceType, final String hostname,
			final Calendar executionTime) {
		return SlingHelper
				.resolveDefault(resolverFactory, progressLogger.getExecutor(), new ResolveCallback<Entry>() {
					@Override
					public Entry resolve(ResourceResolver resolver) {
						Entry result = null;
						Resource source = resolver.getResource(script.getPath());
						ValueMap values = source.getValueMap();
						try {
							Page historyPage = getOrCreateLogDir(resolver);
							Resource historyComponent = historyPage.getContentResource()
									.getChild(HISTORY_COMPONENT);
							if (historyComponent == null) {
								historyComponent = createHistoryComponent(historyPage);
							}
							String uniqueName = ResourceUtil
									.createUniqueChildName(historyComponent, source.getName());
							Resource child = resolver
									.create(historyComponent, uniqueName, new HashMap<String, Object>());

							String executor = getExecutor(resolver, mode);
							ModifiableEntryBuilder builder = new ModifiableEntryBuilder(child);
							fillEntryProperties(//
									builder, mode, progressLogger, instanceType, //
									hostname, executionTime, source, values, executor//
							);

							//easier to use JCR API here due to jcr:uuid copy constraints
							Node file = JcrUtil
									.copy(source.adaptTo(Node.class), child.adaptTo(Node.class), "script");
							file.addMixin(ScriptContent.CQSM_FILE);
							resolver.commit();
							result = resolver.getResource(child.getPath()).adaptTo(Entry.class);
						} catch (RepositoryException | WCMException | PersistenceException e) {
							LOG.error("Issues with saving to repository while logging script execution", e);
						}

						return result;
					}

			private Resource createHistoryComponent(Page historyPage) throws PersistenceException {
				ResourceResolver resourceResolver = historyPage.getContentResource().getResourceResolver();
				Map<String, Object> props = ImmutableMap.<String, Object> builder()//
						.put(ResourceResolver.PROPERTY_RESOURCE_TYPE, HISTORY_COMPONENT_RESOURCE_TYPE) //
						.build();
				return resourceResolver.create(historyPage.getContentResource(), HISTORY_COMPONENT, props);
			}
		}, null);
	}

	private void fillEntryProperties(ModifiableEntryBuilder entryBuilder, Mode mode, Progress progressLogger,
			InstanceDetails.InstanceType instanceType, String hostname, Calendar executionTime,
			Resource source, ValueMap values, String executor) {
		entryBuilder.setFileName(source.getName()) //
				.setFilePath(source.getPath()) //
				.setMode(mode.toString()) //
				.setProgressLog(ProgressHelper.toJson(progressLogger.getEntries())) //
				.setExecutionTime(executionTime) //
				.setAuthor(values.get(JcrConstants.JCR_CREATED_BY, StringUtils.EMPTY)) //
				.setUploadTime(values.get(JcrConstants.JCR_CREATED, StringUtils.EMPTY)) //
				.setInstanceType(instanceType.getInstanceName()) //
				.setInstanceHostname(hostname);
		if (StringUtils.isNotBlank(executor)) {
			entryBuilder.setExecutor(executor);
		}
		entryBuilder.save();
	}

	private Page getOrCreateLogDir(ResourceResolver resolver) throws WCMException {
		PageManager pageManager = resolver.adaptTo(PageManager.class);
		Page historyPage = pageManager.getPage(HISTORY_PATH);
		if (historyPage == null) {
			boolean autoCommit = true;
			historyPage = pageManager
					.create("/conf/apm", "history", "/apps/cqsm/core/templates/historyTemplate", "History",
							autoCommit);
		}
		return historyPage;
	}

	private String getExecutor(ResourceResolver resourceResolver, Mode mode) {
		String executor = null;
    if (mode.isRun()) {
			executor = resourceResolver.getUserID();
		}
		return executor;
	}

	private String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return null;
		}
	}
}