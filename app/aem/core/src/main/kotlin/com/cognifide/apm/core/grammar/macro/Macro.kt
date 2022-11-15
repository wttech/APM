package com.cognifide.apm.core.grammar.macro

import com.cognifide.apm.core.grammar.antlr.ApmLangParser.BodyContext

class Macro(val macroName: String, val variableList: List<String>, val body: BodyContext)