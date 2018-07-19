package com.cognifide.cq.cqsm.foundation.actions.deny;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.ActionFactory;
import com.cognifide.cq.cqsm.api.executors.Context;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.antlr.parameter.Parameters;
import com.cognifide.cq.cqsm.core.antlr.type.ApmValue;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.osgi.service.component.annotations.Component;

@Component(
    immediate = true,
    service = ActionFactory.class,
    property = {
        Property.DESCRIPTION + "APM Allow action factory",
        Property.VENDOR
    }
)
public class DenyActionFactory implements ActionFactory {

  @Override
  public String getName() {
    return "deny";
  }

  @Override
  public Action create(Context context, Parameters parameters) {
    String path = parameters.getString(0);
    String glob = parameters.getString(1);
    List<String> permissions = parameters.getList(2, Collections.emptyList()).stream()
        .filter(ApmValue::isApmString)
        .map(ApmValue::getString)
        .collect(Collectors.toList());
    boolean ignoreNotExistingPaths = "if_exists".equalsIgnoreCase(parameters.getString(3));
    return new Deny(path, glob, ignoreNotExistingPaths, permissions);
  }
}
