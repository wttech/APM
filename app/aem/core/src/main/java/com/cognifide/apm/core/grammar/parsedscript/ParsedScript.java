/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.cognifide.apm.core.grammar.parsedscript;

import com.cognifide.apm.api.scripts.Script;
import com.cognifide.apm.core.grammar.antlr.ApmLangParser;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParsedScript {

  private final Script script;

  private final ApmLangParser.ApmContext apm;

  private final String path;

  private ParsedScript(Script script, ApmLangParser.ApmContext apm) {
    this.script = script;
    this.apm = apm;
    this.path = script.getPath();
  }

  public Script getScript() {
    return script;
  }

  public ApmLangParser.ApmContext getApm() {
    return apm;
  }

  public String getPath() {
    return path;
  }

  public static ParsedScript create(Script script) {
    Logger LOGGER = LoggerFactory.getLogger(ParsedScript.class);
    LOGGER.warn("Script parsing {}", script.getPath());
    ApmLangParser apmLangParser = ApmLangParserFactory.createParserForScriptContent(script.getData());
    return new ParsedScript(script, apmLangParser.apm());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ParsedScript) {
      ParsedScript that = (ParsedScript) obj;
      return Objects.equals(path, that.path);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(path);
  }
}
