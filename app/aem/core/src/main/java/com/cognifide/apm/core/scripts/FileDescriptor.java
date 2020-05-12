/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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
import org.apache.commons.lang.StringUtils;

@Getter
@RequiredArgsConstructor
class FileDescriptor {

  private final String path;
  private final String name;
  private final InputStream inputStream;

  public static FileDescriptor createFileDescriptor(String originalFileName, String savePath, InputStream inputStream) {
    String path = getPathFromOriginalFileName(savePath, originalFileName);
    String fileName = getFileNameFromOriginalFileName(originalFileName);
    return new FileDescriptor(path, fileName, inputStream);
  }

  private static String getPathFromOriginalFileName(String savePath, String originalFileName) {
    if (originalFileName.contains("/")) {
      String subPath = StringUtils.substringBeforeLast(originalFileName, "/");
      if (subPath.startsWith(savePath)) {
        subPath = StringUtils.substringAfter(subPath, savePath);
      }
      return savePath + (subPath.startsWith("/") ? subPath : "/" + subPath);
    }
    return savePath;
  }

  private static String getFileNameFromOriginalFileName(String originalFileName) {
    if (originalFileName.contains("/")) {
      return StringUtils.substringAfterLast(originalFileName, "/");
    }
    return originalFileName;
  }
}
