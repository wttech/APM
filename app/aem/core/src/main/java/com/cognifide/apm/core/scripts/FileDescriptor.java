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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
class FileDescriptor {

  private static final String SCRIPT_PATH = "/conf/apm/scripts";

  private final String path;

  private final String name;

  private final InputStream inputStream;

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
    if (!path.startsWith(SCRIPT_PATH)) {
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

}
