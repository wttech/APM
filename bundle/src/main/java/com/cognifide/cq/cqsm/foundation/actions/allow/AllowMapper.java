/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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
package com.cognifide.cq.cqsm.foundation.actions.allow;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.BasicActionMapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException;
import java.util.Collections;
import java.util.List;

public class AllowMapper extends BasicActionMapper {

  public static final String REFERENCE = "Add allow permissions for current authorizable on specified path.";

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + LIST},
      args = {"path", "permissions"},
      reference = REFERENCE
  )
  public Action mapAction(String path, List<String> permissions) throws ActionCreationException {
    return mapAction(path, permissions, null, null, null, false);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + "ITEMS" + SPACE + LIST + SPACE + LIST},
      args = {"path", "itemNames", "permissions"},
      reference = REFERENCE
  )
  public Action allowWithItems(String path, List<String> itemNames, List<String> permissions)
      throws ActionCreationException {
    return mapAction(path, permissions, null, null, itemNames, false);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + "TYPES" + SPACE + LIST + SPACE + LIST},
      args = {"path", "itemNames", "permissions"},
      reference = REFERENCE
  )
  public Action allowWithTypes(String path, List<String> ntNames, List<String> permissions)
      throws ActionCreationException {
    return mapAction(path, permissions, null, ntNames, null, false);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + STRING},
      args = {"path", "permission"},
      reference = REFERENCE
  )
  public Action mapAction(String path, String permission) throws ActionCreationException {
    return mapAction(path, Collections.singletonList(permission), null, null, null, false);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + "ITEMS" + SPACE + LIST + SPACE + STRING},
      args = {"path", "itemNames", "permission"},
      reference = REFERENCE
  )
  public Action mapAction(String path, List<String> itemNames, String permission) throws ActionCreationException {
    return mapAction(path, Collections.singletonList(permission), null, null, itemNames, false);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + LIST + SPACE + ("IF" + DASH + "EXISTS")},
      args = {"path", "permissions"},
      reference = REFERENCE
  )
  public Action mapActionWithIfExists(String path, List<String> permissions) {
    return mapAction(path, permissions, null, null, null, true);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + "ITEMS" + LIST + SPACE + LIST + SPACE + ("IF" + DASH + "EXISTS")},
      args = {"path", "itemNames", "permissions"},
      reference = REFERENCE
  )
  public Action mapActionWithIfExists(String path, List<String> itemNames, List<String> permissions) {
    return mapAction(path, permissions, null, null, itemNames, true);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + GLOB + SPACE + LIST},
      args = {"path", "glob", "permissions"},
      reference = REFERENCE
  )
  public Action mapAction(String path, String glob, List<String> permissions)
      throws ActionCreationException {
    return mapAction(path, permissions, glob, null, null, false);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + GLOB + SPACE + "ITEMS" + SPACE + LIST + SPACE + LIST},
      args = {"path", "glob", "itemNames", "permissions"},
      reference = REFERENCE
  )
  public Action allowWithGlobAndItems(String path, String glob, List<String> itemNames, List<String> permissions)
      throws ActionCreationException {
    return mapAction(path, permissions, glob, null, itemNames, false);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + GLOB + SPACE + "TYPES" + SPACE + LIST + SPACE + LIST},
      args = {"path", "glob", "itemNames", "permissions"},
      reference = REFERENCE
  )
  public Action allowWithGlobAndTypes(String path, String glob, List<String> ntNames, List<String> permissions)
      throws ActionCreationException {
    return mapAction(path, permissions, glob, ntNames, null, false);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + GLOB + SPACE + STRING},
      args = {"path", "glob", "permission"},
      reference = REFERENCE
  )
  public Action mapAction(String path, String glob, String permission)
      throws ActionCreationException {
    return mapAction(path, Collections.singletonList(permission), glob, null, null, false);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + GLOB + SPACE + "ITEMS" + LIST + SPACE + STRING},
      args = {"path", "glob", "itemNames", "permission"},
      reference = REFERENCE
  )
  public Action mapAction(String path, String glob, List<String> itemNames, String permission)
      throws ActionCreationException {
    return mapAction(path, Collections.singletonList(permission), glob, null, itemNames, false);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + GLOB + SPACE + LIST + SPACE + ("IF" + DASH + "EXISTS")},
      args = {"path", "glob", "permissions"},
      reference = REFERENCE
  )
  public Action mapActionWithIfExists(String path, String glob, List<String> permissions) {
    return mapAction(path, permissions, glob, null, null, true);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + GLOB + SPACE + "ITEMS" + LIST + SPACE + LIST + SPACE + ("IF" + DASH
          + "EXISTS")},
      args = {"path", "glob", "itemNames", "permissions"},
      reference = REFERENCE
  )
  public Action allowWithGlobItemsAndIfExists(String path, String glob, List<String> itemNames,
      List<String> permissions) {
    return mapAction(path, permissions, glob, null, itemNames, true);
  }

  @Mapping(
      value = {"ALLOW" + SPACE + PATH + SPACE + GLOB + SPACE + "TYPES" + LIST + SPACE + LIST + SPACE + ("IF" + DASH
          + "EXISTS")},
      args = {"path", "glob", "ntNames", "permissions"},
      reference = REFERENCE
  )
  public Action allowWithGlobTypesAndIfExists(String path, String glob, List<String> ntNames,
      List<String> permissions) {
    return mapAction(path, permissions, glob, ntNames, null, true);
  }

  private Action mapAction(String path, List<String> permissions, String glob, List<String> ntNames,
      List<String> itemNames, Boolean ifExists) {
    return new Allow(path, permissions, glob, ntNames, itemNames, ifExists);
  }
}
