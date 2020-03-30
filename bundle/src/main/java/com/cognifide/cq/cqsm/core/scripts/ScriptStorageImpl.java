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

import static com.cognifide.cq.cqsm.core.scripts.FileDescriptor.createFileDescriptor;
import static com.cognifide.cq.cqsm.core.scripts.ScriptContent.CQSM_EXECUTION_ENABLED;
import static com.cognifide.cq.cqsm.core.scripts.ScriptContent.CQSM_EXECUTION_ENVIRONMENT;
import static com.cognifide.cq.cqsm.core.scripts.ScriptContent.CQSM_EXECUTION_HOOK;
import static com.cognifide.cq.cqsm.core.scripts.ScriptContent.CQSM_EXECUTION_MODE;
import static com.cognifide.cq.cqsm.core.scripts.ScriptContent.CQSM_EXECUTION_SCHEDULE;
import static com.cognifide.cq.cqsm.core.scripts.ScriptContent.CQSM_FILE;
import static java.lang.String.format;

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.scripts.Event;
import com.cognifide.cq.cqsm.api.scripts.ExecutionMetadata;
import com.cognifide.cq.cqsm.api.scripts.Script;
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder;
import com.cognifide.cq.cqsm.api.scripts.ScriptManager;
import com.cognifide.cq.cqsm.api.scripts.ScriptStorage;
import com.cognifide.cq.cqsm.core.Cqsm;
import com.cognifide.cq.cqsm.core.Property;
import com.day.cq.commons.jcr.JcrConstants;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    immediate = true,
    service = ScriptStorage.class,
    property = {
        Property.DESCRIPTION + "Storage accessor for scripts",
        Property.VENDOR
    }
)
public class ScriptStorageImpl implements ScriptStorage {

  private static final Logger LOG = LoggerFactory.getLogger(ScriptStorageImpl.class);

  private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[\\da-zA-Z\\-]+\\.cqsm");

  private static final Pattern PATH_PATTERN = Pattern.compile("/[\\da-zA-Z\\-/]+");

  private static final String SCRIPT_PATH = "/conf/apm/scripts";

  private static final Charset SCRIPT_ENCODING = StandardCharsets.UTF_8;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
  private volatile ScriptManager scriptManager;

  @Reference
  private ScriptFinder scriptFinder;

  @Override
  public void remove(final Script script, ResourceResolver resolver) throws RepositoryException {
    scriptManager.getEventManager().trigger(Event.BEFORE_REMOVE, script);
    final Session session = resolver.adaptTo(Session.class);
    final String path = script.getPath();
    if (path != null) {
      session.removeItem(path);
      session.save();
    }
  }

  @Override
  public Script save(String fileName, InputStream input, ExecutionMetadata executionMetadata, boolean overwrite,
      ResourceResolver resolver) throws RepositoryException, PersistenceException {

    FileDescriptor fileDescriptor = createFileDescriptor(fileName, getSavePath(), input);

    validate(Collections.singletonList(fileDescriptor));

    Script script = saveScript(fileDescriptor, executionMetadata, overwrite, resolver);
    scriptManager.process(script, Mode.VALIDATION, resolver);
    scriptManager.getEventManager().trigger(Event.AFTER_SAVE, script);
    return script;
  }

  @Override
  public String getSavePath() {
    return SCRIPT_PATH;
  }

  private Script saveScript(FileDescriptor descriptor, ExecutionMetadata executionMetadata, boolean overwrite,
      ResourceResolver resolver) {
    Script result = null;
    try {
      final Session session = resolver.adaptTo(Session.class);
      final ValueFactory valueFactory = session.getValueFactory();
      final Binary binary = valueFactory.createBinary(descriptor.getInputStream());
      final Node saveNode = session.getNode(descriptor.getPath());

      final Node fileNode, contentNode;
      if (overwrite && saveNode.hasNode(descriptor.getName())) {
        fileNode = saveNode.getNode(descriptor.getName());
        contentNode = fileNode.getNode(JcrConstants.JCR_CONTENT);
      } else {
        fileNode = saveNode.addNode(generateFileName(descriptor.getName(), saveNode), JcrConstants.NT_FILE);
        contentNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
      }

      contentNode.addMixin(CQSM_FILE);
      contentNode.setProperty(JcrConstants.JCR_DATA, binary);
      contentNode.setProperty(JcrConstants.JCR_ENCODING, SCRIPT_ENCODING.name());
      contentNode.setProperty(CQSM_EXECUTION_ENABLED, executionMetadata.isExecutionEnabled());
      setOrRemoveProperty(contentNode, CQSM_EXECUTION_MODE, executionMetadata.getExecutionMode());
      setOrRemoveProperty(contentNode, CQSM_EXECUTION_ENVIRONMENT, executionMetadata.getExecutionEnvironment());
      setOrRemoveProperty(contentNode, CQSM_EXECUTION_HOOK, executionMetadata.getExecutionHook());
      setOrRemoveProperty(contentNode, CQSM_EXECUTION_SCHEDULE, executionMetadata.getExecutionSchedule());
      removeProperty(contentNode, ScriptContent.CQSM_EXECUTION_LAST);
      JcrUtils.setLastModified(contentNode, Calendar.getInstance());
      session.save();
      result = scriptFinder.find(fileNode.getPath(), resolver);
    } catch (RepositoryException e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  private void setOrRemoveProperty(Node node, String name, Object value) throws RepositoryException {
    if (value == null) {
      removeProperty(node, name);
    } else if (value instanceof LocalDateTime) {
      LocalDateTime localDateTime = (LocalDateTime) value;
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(),
          localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
      node.setProperty(name, calendar);
    } else {
      node.setProperty(name, value.toString());
    }
  }

  private void removeProperty(Node contentNode, String propName) throws RepositoryException {
    if (contentNode.hasProperty(propName)) {
      contentNode.getProperty(propName).remove();
    }
  }

  private String generateFileName(String fileName, Node saveNode) throws RepositoryException {
    String baseName = FilenameUtils.getBaseName(fileName);
    int num = 1;
    do {
      fileName = baseName + ((num > 1) ? ("-" + num) : "") + Cqsm.FILE_EXT;
      num++;
    } while (saveNode.hasNode(fileName));

    return fileName;
  }

  private void validate(Collection<FileDescriptor> fileDescriptors) {
    List<String> validationErrors = fileDescriptors.stream()
        .flatMap(fileDescriptor -> validate(fileDescriptor).stream())
        .collect(Collectors.toList());
    if (!validationErrors.isEmpty()) {
      throw new ScriptStorageException("Script errors", validationErrors);
    }
  }

  private List<String> validate(FileDescriptor file) {
    List<String> errors = new ArrayList<>();
    ensurePropertyMatchesPattern(errors, "file name", file.getName(), FILE_NAME_PATTERN);
    ensurePropertyMatchesPattern(errors, "file path", file.getPath(), PATH_PATTERN);
    return errors;
  }

  private static void ensurePropertyMatchesPattern(List<String> errors, String property, String value,
      Pattern pattern) {
    if (!pattern.matcher(value).matches()) {
      errors.add(format("Invalid %s: \"%s\"", property, value));
    }
  }
}
