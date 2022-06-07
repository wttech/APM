/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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
package com.cognifide.apm.core.actions;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.exceptions.ActionCreationException;
import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.grammar.argument.Arguments;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

@Component(
    property = {
        Property.DESCRIPTION + "Action factory service",
        Property.VENDOR
    }
)
public class ActionFactoryImpl implements ActionFactory {

  public static final String CORE_GROUP = "core";

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private ActionMapperRegistry registry;

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private MapperContext mapperContext;

  public ActionDescriptor evaluate(String command, Arguments arguments) throws ActionCreationException {
    Optional<MapperDescriptor> mapper = registry.getMapper(command);
    if (mapper.isPresent()) {
      return new ActionDescriptor(command, tryToEvaluateCommand(mapper.get(), arguments), arguments);
    }
    throw new ActionCreationException(String.format("Cannot find action for command: %s", command));
  }

  private Action tryToEvaluateCommand(MapperDescriptor mapper, Arguments arguments)
      throws ActionCreationException {
    if (mapper.handles(arguments)) {
      return mapper.handle(arguments, mapperContext);
    }
    throw new ActionCreationException("Mapper cannot handle given arguments: " + arguments);
  }

  @Override
  public List<CommandDescription> getCommandDescriptions() {
    final List<CommandDescription> commandDescriptions = new ArrayList<>();

    for (MapperDescriptor mapper : registry.getMappers()) {
      for (MappingDescriptor mapping : mapper.getMappingDescriptors()) {
        commandDescriptions.add(
            new CommandDescription(mapping.getName(), mapping.getGroup(),
                mapping.getExamples(), mapping.getDescription(),
                mapping.getArguments()));
      }
    }

    sortReferences(commandDescriptions);

    return commandDescriptions;
  }

  private void sortReferences(List<CommandDescription> references) {
    Collections.sort(references, Comparator.comparing(CommandDescription::getGroup, (o1, o2) -> {
      if (CORE_GROUP.equals(o1) && CORE_GROUP.equals(o2)) {
        return 0;
      }
      if (CORE_GROUP.equals(o1)) {
        return -1;
      }
      if (CORE_GROUP.equals(o2)) {
        return 1;
      }
      return Comparator.<String>naturalOrder().compare(o1, o2);
    }).thenComparing(CommandDescription::getName));
  }
}
