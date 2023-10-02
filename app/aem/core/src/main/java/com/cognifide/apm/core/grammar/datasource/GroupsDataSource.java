package com.cognifide.apm.core.grammar.datasource;

import com.cognifide.apm.core.grammar.ApmList;
import com.cognifide.apm.core.grammar.ApmString;
import com.cognifide.apm.core.grammar.ApmType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

@Component
public class GroupsDataSource implements DataSource {

  @Override
  public String getName() {
    return "GROUPS";
  }

  @Override
  public ApmType determine(ResourceResolver resolver, List<ApmType> parameters) {
    String value = parameters.get(0).getString();
    String regex = parameters.get(1).getString();
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(value);
    List<ApmType> list = new ArrayList<>();
    if (matcher.matches()) {
      for (int i = 0; i <= matcher.groupCount(); i++) {
        list.add(new ApmString(matcher.group(i)));
      }
    }
    return new ApmList(list);
  }
}
