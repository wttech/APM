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
package com.cognifide.cq.cqsm.core.utils.sling;

import com.google.common.collect.Maps;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

public final class SlingHelper {

	private static final Logger LOG = LoggerFactory.getLogger(SlingHelper.class);

	private static final String RESOLVE_ERROR_MESSAGE = "Error occurred while resolving data from repository.";

	private static final String OPERATE_ERROR_MESSAGE = "Error occurred while operating on data from repository.";

	private static final String SUBSERVICE_NAME = "cqsm";

    private static final String USER_JCR_SESSION = "user.jcr.session";

    private static final String CQSM_USER_ID = "cqsm-system-user";

	private SlingHelper() {
		// hidden constructor
	}

	/**
	 * Retrieve values from repository with wrapped impersonated session (automatically opened and closed).
	 */
	@SuppressWarnings("unchecked")
	public static <T> T resolve(ResourceResolverFactory factory, String userId, ResolveCallback callback)
			throws ResolveException {
		ResourceResolver resolver = null;
		try {
			resolver = getResourceResolverForUser(factory, userId);
			return (T) callback.resolve(resolver);
		} catch (Exception e) {
			throw new ResolveException(RESOLVE_ERROR_MESSAGE, e);
		} finally {
			if (resolver != null && resolver.isLive()) {
				resolver.close();
			}
		}
	}

	/**
	 * Retrieve values from repository with wrapped session (automatically opened and closed).
	 */
	public static <T> T resolveDefault(ResourceResolverFactory factory, ResolveCallback callback,
			T defaultValue) {
		return resolveDefault(factory, null, callback, defaultValue);
	}

	/**
	 * Retrieve values from repository with wrapped session (automatically opened and closed).
	 */
	public static <T> T resolveDefault(ResourceResolverFactory factory, String userId, ResolveCallback callback,
			T defaultValue) {
		try {
			return resolve(factory, userId, callback);
		} catch (ResolveException e) {
			LOG.error(RESOLVE_ERROR_MESSAGE, e);
		}

		return defaultValue;
	}

	/**
	 * Do some operation on repository (delete or update resource etc) with wrapped impersonated session
	 * (automatically opened and closed).
	 */
	public static void operate(ResourceResolverFactory factory, String userId, OperateCallback callback)
			throws OperateException {
		ResourceResolver resolver = null;
		try {
			resolver = getResourceResolverForUser(factory, userId);
			callback.operate(resolver);
			resolver.commit();
		} catch (Exception e) {
			throw new OperateException(OPERATE_ERROR_MESSAGE, e);
		} finally {
			if (resolver != null && resolver.isLive()) {
				resolver.close();
			}
		}
	}

	/**
	 * Do some operation on repository (delete or update resource etc) with wrapped session (automatically
	 * opened and closed).
	 */
	public static void operateTraced(ResourceResolverFactory factory, OperateCallback callback) {
		operateTraced(factory, null, callback);
	}

	/**
	 * Do some operation on repository (delete or update resource etc) with wrapped session (automatically
	 * opened and closed).
	 */
	public static void operateTraced(ResourceResolverFactory factory, String userId, OperateCallback callback) {
		try {
			operate(factory, userId, callback);
		} catch (OperateException e) {
			LOG.error(OPERATE_ERROR_MESSAGE, e);
		}
	}

    /**
     * Create a new session for specified user (impersonating).
     */
    public static ResourceResolver getResourceResolverForUser(ResourceResolverFactory factory, String userId)
            throws LoginException, RepositoryException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_NAME);
        ResourceResolver resolver = factory.getServiceResourceResolver(parameters);
        if (userId != null) {
            Session session = resolver.adaptTo(Session.class);
            addCqsmUserAsImpersonator(session, userId);
            resolver = impersonate(factory, userId, session);
        }

        return resolver;
    }

    private static void addCqsmUserAsImpersonator(Session session, String userId) throws RepositoryException {
        JackrabbitSession js = (JackrabbitSession) session;
        UserManager userManager = js.getUserManager();
        User user = ((User) userManager.getAuthorizable(userId));
        if (!cqsmUserIsImpersonator(user)) {
            User cqsmUser = ((User) userManager.getAuthorizable(CQSM_USER_ID));
            user.getImpersonation().grantImpersonation(cqsmUser.getPrincipal());
            session.save();
        }
    }

    private static boolean cqsmUserIsImpersonator(User user) throws RepositoryException {
        boolean isImpersonator = false;
        PrincipalIterator impersonators = user.getImpersonation().getImpersonators();
        while (impersonators.hasNext()) {
            if (impersonators.nextPrincipal().getName().equals(CQSM_USER_ID)) {
                isImpersonator = true;
                break;
            }
        }

        return isImpersonator;
    }

    private static ResourceResolver impersonate(ResourceResolverFactory factory, String userId, Session session)
            throws LoginException {
        Map<String, Object> authenticationInfo = Maps.newHashMap();
        authenticationInfo.put(USER_JCR_SESSION, session);
        authenticationInfo.put(ResourceResolverFactory.USER_IMPERSONATION, userId);

        return factory.getResourceResolver(authenticationInfo);
    }
}