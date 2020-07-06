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
package com.cognifide.apm.core.scripts;

import static java.lang.String.format;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.api.services.ScriptFinder;
import com.cognifide.apm.core.Apm;
import com.cognifide.apm.core.Property;
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

  private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[\\da-zA-Z\\-]+\\.apm");

  private static final Pattern PATH_PATTERN = Pattern.compile("/[\\da-zA-Z\\-/]+");

  private static final String SCRIPT_PATH = "/conf/apm/scripts";

  private static final Charset SCRIPT_ENCODING = StandardCharsets.UTF_8;

  @Reference
  private ScriptFinder scriptFinder;

  @Override
  public void remove(final Script script, ResourceResolver resolver) throws RepositoryException {
    final Session session = resolver.adaptTo(Session.class);
    final String path = script.getPath();
    if (path != null) {
      session.removeItem(path);
      session.save();
    }
  }

  @Override
  public Script save(String fileName, InputStream input, LaunchMetadata launchMetadata, boolean overwrite,
      ResourceResolver resolver) throws RepositoryException, PersistenceException {

    FileDescriptor fileDescriptor = FileDescriptor.createFileDescriptor(fileName, getSavePath(), input);

    validate(Collections.singletonList(fileDescriptor));

    return saveScript(fileDescriptor, launchMetadata, overwrite, resolver);
  }

  @Override
  public String getSavePath() {
    return SCRIPT_PATH;
  }

  private Script saveScript(FileDescriptor descriptor, LaunchMetadata launchMetadata, boolean overwrite,
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

      contentNode.setProperty(JcrConstants.JCR_DATA, binary);
      contentNode.setProperty(JcrConstants.JCR_ENCODING, SCRIPT_ENCODING.name());
      fileNode.addMixin(ScriptNode.APM_SCRIPT);
      fileNode.setProperty(ScriptNode.APM_LAUNCH_ENABLED, launchMetadata.isExecutionEnabled());
      setOrRemoveProperty(fileNode, ScriptNode.APM_LAUNCH_MODE, launchMetadata.getLaunchMode());
      setOrRemoveProperty(fileNode, ScriptNode.APM_LAUNCH_ENVIRONMENT, launchMetadata.getLaunchEnvironment());
      setOrRemoveProperty(fileNode, ScriptNode.APM_LAUNCH_HOOK, launchMetadata.getExecutionHook());
      setOrRemoveProperty(fileNode, ScriptNode.APM_LAUNCH_SCHEDULE, launchMetadata.getExecutionSchedule());
      removeProperty(fileNode, ScriptNode.APM_LAST_EXECUTED);
      JcrUtils.setLastModified(fileNode, Calendar.getInstance());
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
      fileName = baseName + ((num > 1) ? ("-" + num) : "") + Apm.FILE_EXT;
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
