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
package com.cognifide.cq.cqsm.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.sling.api.SlingHttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServletUtils {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void writeJson(SlingHttpServletResponse response, Object obj) throws IOException {
		writeJson(response, GSON.toJson(obj));
	}

	public static void writeJson(SlingHttpServletResponse response, String json) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	public static void writeMessage(SlingHttpServletResponse response, String type, String text) throws IOException {
		writeMessage(response, type, text, new HashMap<String, Object>());
	}

	public static void writeMessage(SlingHttpServletResponse response, String type, String text,
			Map<String, Object> context) throws IOException {
		Map<String, Object> map = new HashMap<>();
		map.put("type", type);
		map.put("message", text);
		map.putAll(context);

		writeJson(response, GSON.toJson(map));
	}
}
