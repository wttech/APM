package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.apm.antlr.ApmLangParser.GenericCommandContext;
import com.cognifide.cq.cqsm.core.antlr.parameter.Parameters;
import java.util.List;

public interface ActionInvoker {

  List<String> runAction(GenericCommandContext ctx, String stringCommand, String commandName, Parameters parameters);
}
