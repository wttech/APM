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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException;
import com.cognifide.cq.cqsm.api.scripts.Script;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ScriptUtils {

	public static final Pattern DEFINITION = Pattern.compile("\\$\\{([^\\}]*)\\}");

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static boolean isAction(String line) {
		return !(StringUtils.isBlank(line) || ScriptUtils.isComment(line));
	}

	public static boolean isComment(final String line) {
		return StringUtils.trim(line).charAt(0) == '#';
	}

	public static String parseCommand(String command, Map<String, String> definitions)
			throws ActionCreationException {
		command = StringUtils.strip(command);

		Set<String> definitionNames = new HashSet<>();
		Matcher matcher = DEFINITION.matcher(command);
		while (matcher.find()) {
			String definitionName = matcher.group(1);
			definitionNames.add(definitionName);
		}

		for (String definitionName : definitionNames) {
			if (definitions == null) {
				throw new ActionCreationException("Definitions map is not specified");
			}
			String definitionValue = definitions.get(definitionName);
			if (definitionValue == null) {
				throw new ActionCreationException("Definition " + definitionName + " not found");
			}

			command = StringUtils.replace(command, String.format("${%s}", definitionName), definitionValue);
		}

		return command;
	}

	public static String toJson(List<Script> scripts) {
		final List<Map<String, Object>> results = convertToMaps(scripts);
		return GSON.toJson(results);
	}

	public static List<Map<String, Object>> convertToMaps(final List<Script> scripts) {
		final List<Map<String, Object>> results = new ArrayList<>();

		for (final Script script : scripts) {
			HashMap<String, Object> asMap = asMap(script);
			results.add(asMap);
		}
		return results;
	}

	public static HashMap<String, Object> asMap(final Script script) {
		final HashMap<String, Object> result = new HashMap<>();
		result.put("name", FilenameUtils.getBaseName(script.getPath()));
		result.put("path", script.getPath());
		result.put("verified", script.isValid());
		result.put("executionEnabled", script.isExecutionEnabled());
		result.put("executionMode", script.getExecutionMode());
		result.put("executionSchedule", script.getExecutionSchedule());
		return result;
	}
}
