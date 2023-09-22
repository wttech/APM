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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

  private static final String REP_GLOB_PROPERTY = "rep:glob";

  private static final String REP_NTNAMES_PROPERTY = "rep:ntNames";

  private static final String REP_ITEMNAMES_PROPERTY = "rep:itemNames";

  private static final Set<String> MULTIVALUE_REP_PROPERTIES = ImmutableSet.of(
      REP_NTNAMES_PROPERTY, REP_ITEMNAMES_PROPERTY, "rep:prefixes", "rep:current", "rep:globs",
      "rep:subtrees", "sling:resourceTypes", "sling:resourceTypesWithDescendants"
  );

  private final String glob;

  private final List<String> ntNames;

  private final List<String> itemNames;

  private final Map<String, Object> customRestrictions;

  public Restrictions(String glob, List<String> ntNames, List<String> itemNames, Map<String, Object> customRestrictions) {
    this.glob = glob;
    this.ntNames = notNullCopy(ntNames);
    this.itemNames = notNullCopy(itemNames);
    this.customRestrictions = notNullCopy(customRestrictions);
  }

  private List<String> notNullCopy(List<String> strings) {
    return strings != null ? ImmutableList.copyOf(strings) : Collections.emptyList();
  }

  private Map<String, Object> notNullCopy(Map<String, Object> items) {
    return items != null ? ImmutableMap.copyOf(items) : Collections.emptyMap();
  }

  public Map<String, Value> getSingleValueRestrictions(ValueFactory valueFactory) throws ValueFormatException {
    Map<String, Value> result = new HashMap<>();
    addRestriction(valueFactory, result, REP_GLOB_PROPERTY, glob);
    for (Map.Entry<String, Object> entry : customRestrictions.entrySet()) {
      if (!isMultivalue(entry)) {
        String value;

        if (entry.getValue() instanceof String) {
          value = (String) entry.getValue();
        } else {
          value = ((List<String>) entry.getValue()).get(0);
        }
        addRestriction(valueFactory, result, entry.getKey(), value);
      }
    }
    return result;
  }

  private void addRestriction(ValueFactory valueFactory, Map<String, Value> result, String key, String value) throws ValueFormatException {
    if (StringUtils.isNotBlank(value)) {
      if (REP_GLOB_PROPERTY.equals(key)) {
        result.put(key, normalizeGlob(valueFactory));
      } else {
        result.put(key, valueFactory.createValue(value, PropertyType.NAME));
      }
    }
  }

  private Value normalizeGlob(ValueFactory valueFactory) {
    if (STRICT.equalsIgnoreCase(glob)) {
      return valueFactory.createValue(StringUtils.EMPTY);
    }
    return valueFactory.createValue(glob);
  }

  public Map<String, Value[]> getMultiValueRestrictions(ValueFactory valueFactory) throws ValueFormatException {
    Map<String, Value[]> result = new HashMap<>();
    addRestrictions(valueFactory, result, REP_NTNAMES_PROPERTY, ntNames);
    addRestrictions(valueFactory, result, REP_ITEMNAMES_PROPERTY, itemNames);
    for (Map.Entry<String, Object> entry : customRestrictions.entrySet()) {
      if (isMultivalue(entry)) {
        List<String> values;
        if (entry.getValue() instanceof String) {
          values = Collections.singletonList((String) entry.getValue());
        } else {
          values = (List<String>) entry.getValue();
        }
        addRestrictions(valueFactory, result, entry.getKey(), values);
      }
    }
    return result;
  }

  private void addRestrictions(ValueFactory valueFactory, Map<String, Value[]> result, String key, List<String> names) throws ValueFormatException {
    if (names != null && !names.isEmpty()) {
      result.put(key, createRestrictions(valueFactory, names));
    }
  }

  private Value[] createRestrictions(ValueFactory valueFactory, List<String> names) throws ValueFormatException {
    Value[] values = new Value[names.size()];
    for (int index = 0; index < names.size(); index++) {
      values[index] = valueFactory.createValue(names.get(index), PropertyType.NAME);
    }
    return values;
  }

  private boolean isMultivalue(Map.Entry<String, Object> entry) {
    boolean result;
    if (REP_GLOB_PROPERTY.equals(entry.getKey())) {
      result = false;
    } else if (MULTIVALUE_REP_PROPERTIES.contains(entry.getKey())) {
      result = true;
    } else {
      result = entry.getValue() instanceof List;
    }
    return result;
  }
}
