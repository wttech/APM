/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.cognifide.apm.core.scripts;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class FileDescriptor {

  private static final String SCRIPT_PATH = "/conf/apm/scripts";

  private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[0-9a-zA-Z_\\-]+\\.apm");

  private static final Pattern PATH_PATTERN = Pattern.compile("(/[0-9a-zA-Z_\\-]+)+");

  private final String path;

  private final String name;

  private final InputStream inputStream;

  public FileDescriptor(String path, String name, InputStream inputStream) {
    this.path = path;
    this.name = name;
    this.inputStream = inputStream;
  }

  public static FileDescriptor createFileDescriptor(String originalFileName, String savePath, InputStream inputStream) {
    String path = getPathFromOriginalFileName(savePath, originalFileName);
    String fileName = getFileNameFromOriginalFileName(originalFileName);
    return new FileDescriptor(path, fileName, inputStream);
  }

  private static String getPathFromOriginalFileName(String savePath, String originalFileName) {
    String path = savePath;
    if (originalFileName.contains("/")) {
      String subPath = StringUtils.substringBeforeLast(originalFileName, "/");
      if (subPath.startsWith(savePath)) {
        subPath = StringUtils.substringAfter(subPath, savePath);
      }
      if (!subPath.isEmpty()) {
        path = savePath + (subPath.startsWith("/") ? "" : "/") + subPath;
      }
    }
    if (!path.startsWith("/")) {
      path = SCRIPT_PATH + (path.startsWith("/") ? "" : "/") + path;
    }
    return path;
  }

  private static String getFileNameFromOriginalFileName(String originalFileName) {
    String fileName = originalFileName;
    if (originalFileName.contains("/")) {
      fileName = StringUtils.substringAfterLast(originalFileName, "/");
    }
    return fileName;
  }

  public String getPath() {
    return path;
  }

  public String getName() {
    return name;
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public List<String> validate() {
    List<String> errors = new ArrayList<>();
    ensurePropertyMatchesPattern(errors, "file name", name, FILE_NAME_PATTERN);
    ensurePropertyMatchesPattern(errors, "file path", path, PATH_PATTERN);
    return errors;
  }

  private void ensurePropertyMatchesPattern(List<String> errors, String property, String value, Pattern pattern) {
    if (!pattern.matcher(value).matches()) {
      errors.add(String.format("Invalid %s: \"%s\"", property, value));
    }
  }
}
