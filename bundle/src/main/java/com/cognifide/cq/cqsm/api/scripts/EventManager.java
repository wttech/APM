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
package com.cognifide.cq.cqsm.api.scripts;

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.logger.Progress;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

/**
 * Responsible for tracking Script lifecycle. Collects listeners that can hook into a lifetime of scripts.
 */
public class EventManager {

	@Getter
	private final Map<Event, Set<EventListener>> listeners = new ConcurrentHashMap<>();

	/**
	 * Register new {@link EventListener} to be called whenever {@link Event} happens
	 */
	public void addListener(Event event, EventListener listener) {
		if (!listeners.containsKey(event)) {
			listeners.put(event, Collections.synchronizedSet(new HashSet<EventListener>()));
		}

		Set<EventListener> eventListeners = listeners.get(event);
		if (!eventListeners.contains(listener)) {
			eventListeners.add(listener);
		}
	}

	/**
	 * Deregister an existing {@link EventListener} that responds to {@link Event}
	 */
	public void removeListener(Event event, EventListener listener) {
		if (!listeners.containsKey(event)) {
			return;
		}

		final Set<EventListener> eventListeners = listeners.get(event);
		if (eventListeners.contains(listener)) {
			eventListeners.remove(listener);
		}
	}

	public void trigger(Event event) {
		trigger(event, null);
	}

	public void trigger(Event event, Script script) {
		trigger(event, script, null, null);
	}

	public void trigger(Event event, Script script, Mode mode, Progress progress) {
		if (!listeners.containsKey(event)) {
			return;
		}

		for (EventListener listener : listeners.get(event)) {
			listener.handle(script, mode, progress);
		}
	}
}
