package com.cognifide.apm.main.actions.internal.datasources;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.apm.main.actions.ActionGroup;
import org.apache.commons.lang3.NotImplementedException;

@Mapper(value = "GROUPS", group = ActionGroup.DATASOURCE)
public class GroupsMapper {

  @Mapping(
      examples = "GROUPS('en_GB', '(..)_(..)')",
      reference = "Provide groups from regex matching given string"
  )
  public Action mapAction(
      @Required(value = "value", description = "string") String value,
      @Required(value = "regex", description = "Regex expression") String regex) {
    throw new NotImplementedException("");
  }
}
