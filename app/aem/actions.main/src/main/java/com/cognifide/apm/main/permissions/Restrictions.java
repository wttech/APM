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

package com.cognifide.apm.main.permissions;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jcr.PropertyType;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
public class Restrictions {

  private static final String STRICT = "STRICT";

  private final String glob;
  private final List<String> ntNames;
  private final List<String> itemNames;

  public Restrictions(String glob, List<String> ntNames, List<String> itemNames) {
    this.glob = glob;
    this.ntNames = notNullCopy(ntNames);
    this.itemNames = notNullCopy(itemNames);
  }

  private List<String> notNullCopy(List<String> strings) {
    return strings != null ? ImmutableList.copyOf(strings) : Collections.emptyList();
  }

  public Map<String, Value> getSingleValueRestrictions(ValueFactory valueFactory) {
    Map<String, Value> result = new HashMap<>();
    if (StringUtils.isNotBlank(glob)) {
      result.put("rep:glob", normalizeGlob(valueFactory));
    }
    return result;
  }

  private Value normalizeGlob(ValueFactory valueFactory) {
    if (STRICT.equalsIgnoreCase(glob)) {
      return valueFactory.createValue(StringUtils.EMPTY);
    }
    return valueFactory.createValue(glob);
  }

  public Map<String, Value[]> getMultiValueRestrictions(ValueFactory valueFactory)
      throws ValueFormatException {

    Map<String, Value[]> result = new HashMap<>();
    addRestrictions(valueFactory, result, "rep:ntNames", ntNames);
    addRestrictions(valueFactory, result, "rep:itemNames", itemNames);
    return result;
  }

  private void addRestrictions(ValueFactory valueFactory, Map<String, Value[]> result, String key, List<String> names)
      throws ValueFormatException {

    if (names != null && !names.isEmpty()) {
      result.put(key, createRestrictions(valueFactory, names));
    }
  }

  private Value[] createRestrictions(ValueFactory valueFactory, List<String> names)
      throws ValueFormatException {

    Value[] values = new Value[names.size()];
    for (int index = 0; index < names.size(); index++) {
      values[index] = valueFactory.createValue(names.get(index), PropertyType.NAME);
    }
    return values;
  }
}
