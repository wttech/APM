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
package com.cognifide.cq.cqsm.api.actions;

import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import com.cognifide.cq.cqsm.api.exceptions.ActionCreationException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingFormatArgumentException;

@Mapper("")
public abstract class BasicActionMapper implements ActionMapper {

	public static final String SPACE = "[^\\S\\r\\n]+";

	public static final String DASH = "(?:[^\\S\\r\\n]+|[\\-])";

	public static final String STRING = "([^\\s\\[\\]]+)";

	public static final String QUOTED = "'(.*)'";

	public static final String PATH = STRING;

	public static final String GLOB = STRING;

	public static final String LIST = "\\[([^\\]]*)\\]";

	public static final String HOME_PATH = "(/home/[^\\s\\[\\]]+)";

	@Override
	public Object mapParameter(String value, Type type) throws ActionCreationException {
		if (type instanceof ParameterizedType) { // as list
			return toList(value, type);
		} else { // as object
			return toObject(value, type);
		}
	}

	@Override
	public List<String> referMapping(Mapping mapping) {
		final List<String> commands = new LinkedList<>();

		for (String regex : mapping.value()) {
			regex = regex.replace(DASH, " ").replace(SPACE, " ");

			try {
				final String format = regex.replaceAll("\\(.*?\\)", "%s").replace("\\", "");

				commands.add(String.format(format, mapping.args()));
			} catch (MissingFormatArgumentException e) {
				commands.add(regex);
			}
		}

		return commands;
	}

	private List<String> toList(String value) {
		return Arrays.asList(value.trim().split("\\s*,\\s*"));
	}

	private List<?> toList(String values, Type type) {
		List<Object> list = new ArrayList<>();
		for (String value : toList(values)) {
			list.add(toObject(value, ((ParameterizedType) type).getActualTypeArguments()[0]));
		}

		return list;
	}

	private static Object toObject(String value, Type type) {
		Class<?> clazz = (Class<?>) type;

		if (Boolean.class == clazz) {
			return Boolean.parseBoolean(value);
		}
		if (Byte.class == clazz) {
			return Byte.parseByte(value);
		}
		if (Short.class == clazz) {
			return Short.parseShort(value);
		}
		if (Integer.class == clazz) {
			return Integer.parseInt(value);
		}
		if (Long.class == clazz) {
			return Long.parseLong(value);
		}
		if (Float.class == clazz) {
			return Float.parseFloat(value);
		}
		if (Double.class == clazz) {
			return Double.parseDouble(value);
		}
		return value;
	}
}
