package com.cognifide.cq.cqsm.core.antlr;

import com.cognifide.apm.antlr.ApmLangParser.GenericCommandContext;
import java.util.List;

public interface ActionInvoker {

  List<String> runAction(GenericCommandContext ctx, String stringCommand);
}
