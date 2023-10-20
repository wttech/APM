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

package com.cognifide.apm.core.grammar;

import com.cognifide.apm.core.crypto.DecryptionService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class ApmType {

  public Object getArgument(DecryptionService decryptionService) {
    return null;
  }

  protected String toPrettyString(int depth, int prefixDepth) {
    return null;
  }

  public Integer getInteger() {
    return null;
  }

  public String getString() {
    return null;
  }

  public List<ApmType> getList() {
    return null;
  }

  public Map<String, ApmType> getMap() {
    return null;
  }

  @Override
  public String toString() {
    return toPrettyString(0, 0);
  }

  public static class ApmInteger extends ApmType {

    private final Integer value;

    public ApmInteger(Integer value) {
      this.value = value;
    }

    @Override
    public Object getArgument(DecryptionService decryptionService) {
      return value;
    }

    @Override
    public Integer getInteger() {
      return value;
    }

    @Override
    public String getString() {
      return value.toString();
    }

    @Override
    public String toPrettyString(int depth, int prefixDepth) {
      return StringUtils.repeat('\t', Math.min(depth, prefixDepth)) + value;
    }
  }

  public static class ApmString extends ApmType {

    private final String value;

    public ApmString(String value) {
      this.value = value;
    }

    @Override
    public Object getArgument(DecryptionService decryptionService) {
      return decryptionService.decrypt(value);
    }

    @Override
    public String getString() {
      return value;
    }

    @Override
    protected String toPrettyString(int depth, int prefixDepth) {
      return StringUtils.repeat('\t', Math.min(depth, prefixDepth)) + "\"" + value + "\"";
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj instanceof ApmString) {
        ApmString that = (ApmString) obj;
        return Objects.equals(value, that.value);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }

  public static class ApmList extends ApmType {

    private final List<ApmType> values;

    public ApmList(List<ApmType> values) {
      this.values = values;
    }

    @Override
    public Object getArgument(DecryptionService decryptionService) {
      return values.stream()
          .map(value -> value.getArgument(decryptionService))
          .collect(Collectors.toList());
    }

    @Override
    public List<ApmType> getList() {
      return values;
    }

    @Override
    protected String toPrettyString(int depth, int prefixDepth) {
      boolean simpleList = values.stream()
          .allMatch(value -> value instanceof ApmInteger || value instanceof ApmString);
      if (values.isEmpty() || simpleList) {
        return values.stream()
            .map(ApmType::toString)
            .collect(Collectors.joining(
                ", ",
                StringUtils.repeat('\t', prefixDepth) + "[",
                "]"
            ));
      } else {
        return values.stream()
            .map(value -> value.toPrettyString(depth + 1, depth + 1))
            .collect(Collectors.joining(
                ",\n",
                StringUtils.repeat('\t', prefixDepth) + "[\n",
                "\n" + StringUtils.repeat('\t', depth) + "]"
            ));
      }
    }
  }

  public static class ApmMap extends ApmType {

    private final Map<String, ApmType> values;

    public ApmMap(Map<String, ApmType> values) {
      this.values = values;
    }

    @Override
    public Object getArgument(DecryptionService decryptionService) {
      return values.entrySet()
          .stream()
          .collect(Collectors.toMap(
              Map.Entry::getKey,
              entry -> entry.getValue().getArgument(decryptionService)
          ));
    }

    @Override
    public Map<String, ApmType> getMap() {
      return values;
    }

    @Override
    protected String toPrettyString(int depth, int prefixDepth) {
      ApmType firstEntry = values.values()
          .stream()
          .findFirst()
          .orElse(new ApmEmpty());
      boolean simpleList = MapUtils.emptyIfNull(firstEntry.getMap())
          .values()
          .stream()
          .allMatch(value -> value instanceof ApmInteger || value instanceof ApmString);
      if (values.isEmpty() || values.size() == 1 && (firstEntry instanceof ApmInteger || firstEntry instanceof ApmString || simpleList)) {
        return values.entrySet()
            .stream()
            .map(ApmPair::new)
            .map(ApmType::toString)
            .collect(Collectors.joining(
                ", ",
                StringUtils.repeat('\t', prefixDepth) + "{",
                "}"
            ));
      } else {
        return values.entrySet()
            .stream()
            .map(ApmPair::new)
            .map(pair -> pair.toPrettyString(depth + 1, depth + 1))
            .collect(Collectors.joining(
                ",\n",
                StringUtils.repeat('\t', prefixDepth) + "{\n",
                "\n" + StringUtils.repeat('\t', depth) + "}"
            ));
      }
    }
  }

  public static class ApmPair extends ApmType {

    private final String key;

    private final ApmType value;

    public ApmPair(String key, ApmType value) {
      this.key = key;
      this.value = value;
    }

    public ApmPair(Map.Entry<String, ApmType> entry) {
      this.key = entry.getKey();
      this.value = entry.getValue();
    }

    public String getKey() {
      return key;
    }

    public ApmType getValue() {
      return value;
    }

    @Override
    protected String toPrettyString(int depth, int prefixDepth) {
      return StringUtils.repeat('\t', depth) + key + ": " + value.toPrettyString(depth, 0);
    }
  }

  public static class ApmEmpty extends ApmType {

  }
}
