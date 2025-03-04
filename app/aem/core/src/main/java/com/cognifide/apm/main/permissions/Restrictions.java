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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.jcr.PropertyType;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import org.apache.commons.lang3.StringUtils;

public class Restrictions {

  private static final String STRICT = "STRICT";

  private static final String REP_GLOB_PROPERTY = "rep:glob";

  private static final String REP_GLOBS_PROPERTY = "rep:globs";

  private static final String REP_NT_NAMES_PROPERTY = "rep:ntNames";

  private static final String REP_ITEM_NAMES_PROPERTY = "rep:itemNames";

  private static final Set<String> MULTI_VALUE_REP_PROPERTIES;
  static {
    Set<String> tempSet = new HashSet<>();
    tempSet.add(REP_NT_NAMES_PROPERTY);
    tempSet.add(REP_ITEM_NAMES_PROPERTY);
    tempSet.add(REP_GLOBS_PROPERTY);
    tempSet.add("rep:prefixes");
    tempSet.add("rep:current");
    tempSet.add("rep:subtrees");
    tempSet.add("sling:resourceTypes");
    tempSet.add("sling:resourceTypesWithDescendants");
    MULTI_VALUE_REP_PROPERTIES = Collections.unmodifiableSet(tempSet);
  }

  private final String glob;

  private final List<String> ntNames;

  private final List<String> itemNames;

  private final Map<String, Object> customRestrictions;

  public Restrictions(String glob, List<String> ntNames, List<String> itemNames, Map<String, Object> customRestrictions) {
    this.glob = glob;
    this.ntNames = ntNames;
    this.itemNames = itemNames;
    this.customRestrictions = Optional.ofNullable(customRestrictions)
        .orElse(Collections.emptyMap());
  }

  public Map<String, Value> getSingleValueRestrictions(ValueFactory valueFactory) throws ValueFormatException {
    Map<String, Value> result = new HashMap<>();
    addRestriction(valueFactory, result, REP_GLOB_PROPERTY, glob);
    for (Map.Entry<String, Object> entry : customRestrictions.entrySet()) {
      if (!isMultiValue(entry)) {
        String value;
        if (entry.getValue() instanceof String) {
          value = (String) entry.getValue();
        } else {
          List<String> values = (List<String>) entry.getValue();
          value = values.isEmpty() ? "" : values.get(0);
        }
        addRestriction(valueFactory, result, entry.getKey(), value);
      }
    }
    return result;
  }

  private void addRestriction(ValueFactory valueFactory, Map<String, Value> result, String key, String value) throws ValueFormatException {
    if (value != null) {
      result.put(key, createValue(valueFactory, key, value));
    }
  }

  private Value createValue(ValueFactory valueFactory, String key, String value) throws ValueFormatException {
    if (StringUtils.equalsAny(key, REP_GLOB_PROPERTY, REP_GLOBS_PROPERTY)) {
      return normalizeGlob(valueFactory);
    } else {
      return valueFactory.createValue(value, determinePropertyType(key));
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
    addRestrictions(valueFactory, result, REP_NT_NAMES_PROPERTY, ntNames);
    addRestrictions(valueFactory, result, REP_ITEM_NAMES_PROPERTY, itemNames);
    for (Map.Entry<String, Object> entry : customRestrictions.entrySet()) {
      if (isMultiValue(entry)) {
        List<String> values;
        if (entry.getValue() instanceof String) {
          String value = (String) entry.getValue();
          values = value.isEmpty() ? Collections.emptyList() : Collections.singletonList(value);
        } else {
          values = (List<String>) entry.getValue();
        }
        addRestrictions(valueFactory, result, entry.getKey(), values);
      }
    }
    return result;
  }

  private void addRestrictions(ValueFactory valueFactory, Map<String, Value[]> result, String key, List<String> names) throws ValueFormatException {
    if (names != null) {
      result.put(key, createRestrictions(valueFactory, key, names));
    }
  }

  private Value[] createRestrictions(ValueFactory valueFactory, String key, List<String> names) throws ValueFormatException {
    Value[] values = new Value[names.size()];
    for (int index = 0; index < names.size(); index++) {
      values[index] = createValue(valueFactory, key, names.get(index));
    }
    return values;
  }

  private int determinePropertyType(String key) {
    if (StringUtils.equalsAny(key, REP_NT_NAMES_PROPERTY, REP_ITEM_NAMES_PROPERTY)) {
      return PropertyType.NAME;
    } else {
      return PropertyType.STRING;
    }
  }

  private boolean isMultiValue(Map.Entry<String, Object> entry) {
    boolean result;
    if (REP_GLOB_PROPERTY.equals(entry.getKey())) {
      result = false;
    } else if (MULTI_VALUE_REP_PROPERTIES.contains(entry.getKey())) {
      result = true;
    } else {
      result = entry.getValue() instanceof List;
    }
    return result;
  }

  public String getGlob() {
    return glob;
  }

  public List<String> getNtNames() {
    return ntNames;
  }

  public List<String> getItemNames() {
    return itemNames;
  }

  public Map<String, Object> getCustomRestrictions() {
    return customRestrictions;
  }
}
