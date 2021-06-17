/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.apm.core.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

public class ResourceMixinUtil {

  private ResourceMixinUtil() {
    // empty constructor
  }

  public static void addMixin(ModifiableValueMap valueMap, String mixin) {
    Set<String> mixins = new HashSet<>(Arrays.asList(valueMap.get(JcrConstants.JCR_MIXINTYPES, new String[0])));
    mixins.add(mixin);
    valueMap.put(JcrConstants.JCR_MIXINTYPES, mixins.toArray(new String[]{}));
  }

  public static boolean containsMixin(Resource resource, String mixin) {
    ValueMap valueMap = resource.getValueMap();
    Set<String> mixins = new HashSet<>(Arrays.asList(valueMap.get(JcrConstants.JCR_MIXINTYPES, new String[0])));
    return mixins.contains(mixin);
  }

}
