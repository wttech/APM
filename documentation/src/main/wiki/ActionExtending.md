## Developing new action
Although the tool provides a rich set of available actions, it may be required to implement a custom one that will be specific to the case. Development can be easily done outside the tool as part of the project implementation.

### Adding new @Mapper object
In order to add a new action, first a class annotated with `@Mapper` marker needs to be implemented. It should eventually implement all the methods coming from `com.cognifide.cq.cqsm.api.actions.ActionMapper` interface. There is also a `BasicActionMapper` abstract implementation of such that already carries on the annotation as well as basic implementation of the required methods.

Moreover, the configuration of `sling-bundle-plugin` needs to be enhanced with the `<CQ-Security-Management-Actions>` configuration that will point to a package that should be crawled to find all `@Mapper` implementations

```xml
<plugin>
    <groupId>org.apache.felix</groupId>
    <artifactId>maven-bundle-plugin</artifactId>
    <extensions>true</extensions>
    <configuration>
        <instructions>
            ...
            <CQ-Security-Management-Actions>com.cognifide.cq.cqsm.foundation.actions</CQ-Security-Management-Actions>
        </instructions>
    </configuration>
</plugin>
```

### Implementing new @Mapping method
The actions available within the tool are collected based on the methods annotated with `@Mapping` that are placed within the `@Mapper` annotated class.

Adding new action, or it's variation means implementing new method that will return `com.cognifide.cq.cqsm.api.actions.Action` interface object.

```java
@Mapping(
    value = {"ALLOW" + SPACE + PATH + SPACE + LIST},
    args = {"path", "permissions"},
    reference = REFERENCE
)
public Action mapAction(String path, List<String> permissions) throws ActionCreationException {
  return mapAction(path, null, permissions, false);
}
```

The `value` property defines the regular expression that will be used to match the action. There are some utility placeholders defined already in the `BasicActionMapper` like `PATH`, `QUOTED` or others. Constructing new action is much easier this way.

The `args` property defines the paramaters to be injected into the method. They are derived from the previous regexp value.

The `reference` property is used for the on-the-fly documentation provided within the tool. It's recommended to provide the description so that the documentation is quickly available whevener required.

### Implementing new Action object
com.cognifide.cq.cqsm.api.actions.Action
`Action` is an interface for the action class responsible for actual action execution. Distinction between `simulate()` and `execute()` is introduced to address both execution modes:
* `simulate` for dry-run,
* `execute` for regular run.

The method `isGeneric()` method must always return true when implemented action is not a context one and should not persist a previously set context, false otherwise.

### Other utility classes
In order to follow APM best practices and benefit from it's internal mechanisms, it is strongly recommended to use `Context` and `AuthorizableUtils` classes.

##### `com.cognifide.cq.cqsm.api.Context`
`Context` class allows to interact with the script that is being executed. It provides current authorizable helper methods along with the session saving settings.
One can also access progress logger via `Context` class. It is recommended to provide it with meaningful messages after proper action execution. Any errors will be logged automatically after `ActionExecutionException` is thrown.

##### `com.cognifide.cq.cqsm.api.AuthorizableUtils`
`AuthorizableUtils` class provides options to interact with authorizables other then current one accessible via `Contex` class.
