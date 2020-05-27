<p align="center">
    <img src="https://assets.cognifide.com/github/cognifide-logo.png" style="vertical-align: middle">
</p><p align="center">
    <img src="apm-logo.png" alt="APM Logo" style="width: 128px; vertical-align: middle">
</p>

# Developing new action
Although the tool provides a rich set of available actions, it may be required to implement a custom one that will be specific to the case. Development can be easily done outside the tool as part of the project implementation.

## Adding new @Mapper object
In order to add a new action, first a class annotated with `@Mapper` marker needs to be implemented. The class has to contain at least one method annotated with `@Mapping` annotation. 

Moreover, the configuration of `sling-bundle-plugin` needs to be enhanced with the `<APM-Actions>` configuration that will point to a package that should be crawled to find all `@Mapper` implementations

```xml
<plugin>
    <groupId>org.apache.felix</groupId>
    <artifactId>maven-bundle-plugin</artifactId>
    <extensions>true</extensions>
    <configuration>
        <instructions>
            ...
            <APM-Actions>com.cognifide.apm.main.actions</APM-Actions>
        </instructions>
    </configuration>
</plugin>
```

## Implementing new @Mapping method
The actions available within the tool are collected based on the methods annotated with `@Mapping` that are placed within the `@Mapper` annotated class.

Adding new action, or it's variation means implementing new method that will return `com.cognifide.apm.api.actions.Action` interface object.

```java
@Mapper(value = "allow", group = ActionGroup.CORE)
public class AllowMapper {

  public static final String REFERENCE = "Add allow permissions for current authorizable on specified path. "
      + "Script fails if path doesn't exist.";

  @Mapping(
      examples = {
          "ALLOW '/content/dam' [READ]",
          "ALLOW '/content/dam' properties= ['jcr:title'] [MODIFY]",
          "ALLOW '/content/dam' properties= ['nt:folder'] [MODIFY]",
          "ALLOW '/content/dam/domain' [READ, MODIFY] --IF-EXISTS"
      },
      reference = REFERENCE
  )
  public Action create(
      @Required(value = "path", description = "e.g.: '/content/dam'") String path,
      @Required(value = "permissions", description = "e.g.: [READ, 'jcr:all']") List<String> permissions,
      @Named(value = "glob", description = "regular expression to narrow set of paths") String glob,
      @Named(value = "types", description = "list of jcr types which will be affected") List<String> types,
      @Named(value = "properties", description = "list of properties which will be affected ") List<String> items,
      @Flags(@Flag(value = IF_EXISTS, description = "script doesn't fail if path doesn't exist")) List<String> flags) {
    return new Allow(path, permissions, glob, types, items, flags.contains(IF_EXISTS));
  }
}
```

The `examples` and `reference` properties are used for the on-the-fly documentation provided within the tool. It's recommended to provide the description so that the documentation is quickly available whevener required.

Let's take script fragment:
```
ALLOW '/content' types= ['nt:folder'] --IF-EXISTS ['ALL']
```

Command from above will map to action like this: 
* `'/content'` - first required parameter, it is mapped to first argument with annotation `@Required`. 
* `types= ['nt:folder']` - named parameter with name `types`, it is mapped to argument with annotation `@Named("types")`  
* `--IF-EXISTS` - is a flag (all flags uses prefix `--`), and it is mapped to argument wit annotation `@Flags`. 
* `['ALL']` - second required parameter, it is mapped to second argument with annotation `@Required`.

> Required arguments are resolved depending on their order. Value of `@Required` annotation is for documentation purpose only.

> All flags go to argument mapped with `@Flags` annotation as `List<String>`. Annotation `@Flag` is for documentation purpose only.

## Implementing new Action object
com.cognifide.apm.api.actions.Action
`Action` is an interface for the action class responsible for actual action execution. Distinction between `simulate()` and `execute()` is introduced to address both execution modes:
* `simulate` for dry-run,
* `execute` for regular run.

The method `isGeneric()` method must always return true when implemented action is not a context one and should not persist a previously set context, false otherwise.

## Other utility classes
In order to follow APM best practices and benefit from its internal mechanisms, it is strongly recommended to use `Context` and `AuthorizableManager` classes.

### `com.cognifide.apm.api.actions.Context`
`Context` class allows to interact with the script that is being executed. It provides current authorizable helper methods along with the session saving settings.
One can also access progress logger via `Context` class. It is recommended to provide it with meaningful messages after proper action execution. Any errors will be logged automatically after `ActionExecutionException` is thrown.

### `com.cognifide.apm.api.actions.AuthorizableManager`
`AuthorizableManager` class provides options to interact with authorizables other than current one accessible via `Contex` class. To get instance of `AuthorizableManager` invoke `context.getAuthorizableManager()`.
