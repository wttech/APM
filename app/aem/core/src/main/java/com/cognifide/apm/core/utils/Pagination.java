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

package com.cognifide.apm.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Pagination {

  private final int offset;
  private final int limit;

  public Pagination(int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }

  public <T> List<T> getPage(List<T> list) {
    int fromIndex = offset;
    int toIndex = offset + limit;
    if (toIndex > list.size()) {
      toIndex = list.size();
    }
    if (fromIndex < list.size()) {
      return list.subList(fromIndex, toIndex);
    } else {
      return Collections.emptyList();
    }
  }

  public <T> List<T> getPage(Iterator<T> iterator) {
    int index = 0;
    List<T> results = new ArrayList<>();
    while (iterator.hasNext() && index < offset + limit) {
      T result = iterator.next();
      if (index >= offset) {
        results.add(result);
      }
      index++;
    }
    return results;
  }

  public int getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }
}
