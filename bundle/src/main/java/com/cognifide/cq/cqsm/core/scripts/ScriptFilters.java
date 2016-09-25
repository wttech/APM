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
package com.cognifide.cq.cqsm.core.scripts;

import com.cognifide.cq.cqsm.api.scripts.ExecutionMode;
import com.cognifide.cq.cqsm.api.scripts.Script;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AllPredicate;
import org.apache.commons.collections.functors.OrPredicate;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Due to the ResourceResolver dependency these filters should not be used lazy
 * i.e. in a context where the resolver passed as an argument is no longer alive.
 */
public class ScriptFilters {

	public static Predicate filterByExecutionMode(final ExecutionMode mode) {
		return filterByExecutionMode(Collections.singletonList(mode));
	}

	public static Predicate filterExecutionEnabled(final boolean flag) {
		return new Predicate() {
			@Override
			public boolean evaluate(Object o) {
				return ((Script) o).isExecutionEnabled() == flag;
			}
		};
	}

	public static Predicate filterByExecutionMode(final List<ExecutionMode> modes) {
		return new Predicate() {
			@Override
			public boolean evaluate(Object o) {
				return modes.contains(((Script) o).getExecutionMode());
			}
		};
	}

	public static Predicate filterOnSchedule(final Date date) {
		return new AllPredicate(new Predicate[]{filterExecutionEnabled(true),
				filterByExecutionMode(ExecutionMode.ON_SCHEDULE), new Predicate() {
			@Override
			public boolean evaluate(Object o) {
				final Script script = (Script) o;
				return (script.getExecutionLast() == null) && script.getExecutionSchedule().before(date);
			}
		}});
	}

	public static Predicate filterOnModify(final ResourceResolver resolver) {
		return new AllPredicate(new Predicate[]{filterExecutionEnabled(true),
				filterByExecutionMode(ExecutionMode.ON_MODIFY), new Predicate() {
			@Override
			public boolean evaluate(Object o) {
				return ((Script) o).isContentModified(resolver);
			}
		}});
	}

	public static Predicate filterOnStart(final ResourceResolver resolver) {
		return new AllPredicate(new Predicate[]{filterExecutionEnabled(true),
				new OrPredicate(filterByExecutionMode(ExecutionMode.ON_START), filterOnModify(resolver))});
	}
}
