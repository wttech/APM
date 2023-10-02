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
package com.cognifide.apm.core.grammar.datasource;

import com.cognifide.apm.core.grammar.ApmInteger;
import com.cognifide.apm.core.grammar.ApmList;
import com.cognifide.apm.core.grammar.ApmMap;
import com.cognifide.apm.core.grammar.ApmString;
import com.cognifide.apm.core.grammar.ApmType;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

@Component
public class ValueMapDataSource implements DataSource {

  @Override
  public String getName() {
    return "VALUEMAP";
  }

  @Override
  public ApmType determine(ResourceResolver resolver, List<ApmType> parameters) {
    String path = parameters.get(0).getString();
    ValueMap valueMap = resolver.getResource(path).getValueMap();
    Map<String, ApmType> map = new HashMap<>();
    for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Object[]) {
        List<ApmType> list = Arrays.stream((Object[]) value)
            .map(this::determineValue)
            .collect(Collectors.toList());
        map.put(key, new ApmList(list));
      } else {
        map.put(key, determineValue(value));
      }
    }
    return new ApmMap(map);
  }

  private ApmType determineValue(Object value) {
    ApmType result;
    if (value instanceof Integer) {
      result = new ApmInteger((Integer) value);
    } else if (value instanceof Calendar) {
      result = new ApmString((((Calendar) value).toInstant()).toString());
    } else if (value instanceof InputStream) {
      result = new ApmString("(binary)");
    } else {
      result = new ApmString(value.toString());
    }
    return result;
  }
}
