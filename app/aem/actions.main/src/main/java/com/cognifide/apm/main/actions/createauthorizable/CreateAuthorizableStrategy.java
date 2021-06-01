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
package com.cognifide.apm.main.actions.createauthorizable;

import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.main.RandomPasswordGenerator;
import java.security.Principal;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;

public enum CreateAuthorizableStrategy {

  GROUP {
    @Override
    public Group create(final String id, final String password, final String path, final Context context,
        final ActionResult actionResult, boolean simulate) throws RepositoryException {
      final Principal namePrincipal = context.getAuthorizableManager().createMockPrincipal(id);
      Group group;
      if (!simulate) {
        group = context.getAuthorizableManager().createGroup(id, namePrincipal, path);
      } else {
        group = context.getAuthorizableManager().createMockGroup(id);
      }

      actionResult.logMessage(String.format("Group with id: %s created", id));
      return group;
    }
  },

  USER {
    @Override
    public User create(String id, String password, String path, Context context,
        ActionResult actionResult, boolean simulate) throws RepositoryException {
      final RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
      final Principal namePrincipal = context.getAuthorizableManager().createMockPrincipal(id);
      User user;
      if (!simulate) {
        user = context.getAuthorizableManager().createUser(
            id, StringUtils.isBlank(password) ? randomPasswordGenerator.getRandomPassword() : password,
            namePrincipal, path);
      } else {
        user = context.getAuthorizableManager().createMockUser(id);
      }

      actionResult.logMessage(String.format("User with id: %s created", id));
      return user;
    }
  },

  SYSTEM_USER {
    @Override
    public User create(String id, String password, String path, Context context,
        ActionResult actionResult, boolean simulate) throws RepositoryException {
      User user;
      if (!simulate) {
        user = context.getAuthorizableManager().createSystemUser(id, path);
      } else {
        user = context.getAuthorizableManager().createMockUser(id);
      }

      actionResult.logMessage(String.format("System user with id: %s created", id));
      return user;
    }
  };

  public abstract Authorizable create(final String id, final String password, final String path,
      final Context context, final ActionResult actionResult, boolean simulate)
      throws RepositoryException;

}
