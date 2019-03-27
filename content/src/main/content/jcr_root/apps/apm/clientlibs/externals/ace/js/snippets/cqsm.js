/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 - 2016 Cognifide Limited
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
ace.define("ace/snippets/cqsm",["require","exports","module"], function(e, t, n) {
	"use strict";

	function loadJson(path, success, error)
	{
		var xhr = new XMLHttpRequest();

		xhr.open("GET", path, false);
		xhr.send();

		if (xhr.status === 200) {
			return JSON.parse(xhr.responseText);
		} else {
			return null;
		}
	}

	function generateReference() {
		var references = loadJson("/etc/cqsm/pages/reference/jcr:content.action.json");

		var snippetText = "";
		for (var i = 0; i < references.length; i++) {
			var reference = references[i];

			for (var j = 0; j < reference.commands.length; j++) {
				var command = reference.commands[j];
				var header = "snippet " + command;
				var body = command;

				for (var k = 0; k < reference.args.length; k++) {
					var arg = reference.args[k];

					body = body.replace(arg, "${" + (k + 1) + ":" + arg + "}");
				}

				snippetText += (header + "\n" + "\t" + body + "\n");
			}
		}

		return snippetText;
	}

	function generateDefinitions() {
		var definitions = loadJson("/etc/cqsm/pages/definitions/jcr:content.action.json");
		
		var snippetText = "";
		for (var name in definitions) {
			if (!definitions.hasOwnProperty(name)) {
				continue;
			}

			var value = definitions[name];
			var header = "snippet " + name,
				body = "\\${" + name + "}";

			snippetText += (header + "\n" + "\t" + body + "\n");
		}

		return snippetText;
	}

	t.snippetText = generateReference() + "\n" + generateDefinitions();
	t.scope = "cqsm";
})