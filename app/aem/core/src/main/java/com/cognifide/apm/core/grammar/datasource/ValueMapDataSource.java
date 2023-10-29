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

import com.cognifide.apm.core.grammar.ApmType;
import com.cognifide.apm.core.grammar.ApmType.ApmEmpty;
import com.cognifide.apm.core.grammar.ApmType.ApmInteger;
import com.cognifide.apm.core.grammar.ApmType.ApmList;
import com.cognifide.apm.core.grammar.ApmType.ApmMap;
import com.cognifide.apm.core.grammar.ApmType.ApmPair;
import com.cognifide.apm.core.grammar.ApmType.ApmString;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
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
  public ApmType determine(ResourceResolver resolver, List<Object> parameters) {
    String path = (String) parameters.get(0);
    String regex = parameters.size() > 1 ? (String) parameters.get(1) : null;
    Pattern pattern = StringUtils.isNotEmpty(regex) ? Pattern.compile(regex) : null;
    Resource resource = resolver.getResource(path);
    if (resource == null) {
      return new ApmEmpty();
    }
    ValueMap valueMap = resource.getValueMap();
    Map<String, ApmType> map = valueMap.entrySet()
        .stream()
        .filter(entry -> pattern == null || pattern.matcher(entry.getKey()).matches())
        .map(entry -> new ApmPair(entry.getKey(), determineValue(entry.getValue())))
        .collect(Collectors.toMap(ApmPair::getKey, ApmPair::getValue));
    return new ApmMap(map);
  }

  private ApmType determineValue(Object value) {
    ApmType result;
    if (value instanceof Object[]) {
      List<ApmType> list = Arrays.stream((Object[]) value)
          .map(this::determineValue)
          .collect(Collectors.toList());
      result = new ApmList(list);
    } else if (value instanceof Integer) {
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
