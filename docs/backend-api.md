<p align="center">
    <img src="wtt-logo.png" style="vertical-align: middle">
</p><p align="center">
    <img src="apm-logo.png" alt="APM Logo" style="width: 128px; vertical-align: middle">
</p>

# Backend API
The rich UX console is friendly and preferred in most cases, yet sometimes there may be a need to execute the script from the backend code. Site creation wizards or other automated tools that bootstrap the site creation can benefit from APM as well.

### How to find and run a script using Java API?
The script execution using Java API relies heavily on two services: `ScriptFinder` that can be used for locating the script file based on path and `ScriptManager` that can execute the script. Both implementations are available as OSGi services so can be easily injected.

While using the backend API, the script to be executed may also use placeholders. They can be provided as part of the script execution being a `Map<String, String>` key-value map.

The resolver that is passed to APM objects is to take user permissions. This way, if a logged user should not see the script, the API will not return a proper object.

```java
@Reference
private ScriptFinder scriptFinder;

@Reference
private ScriptManager scriptManager;

private void runScript(String scriptPath, ResourceResolver resolver) {
    Script script = scriptManager.find(scriptPath, resolver);
    private Map<String, String> createPlaceholdersDefinitions();
    Progress scriptProgress = scriptManager.process(script, Mode.RUN, definitions, resolver);

    if (scriptProgress.isSuccess()) {
      // well done!
    }
}

private Map<String, String> createPlaceholdersDefinitions() {
    ...
}
```
