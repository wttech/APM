## Hooking into script processing
The script processing pipeline can be used if any extra operation should be taken. A simple eventing mechanism exists to support hooking into various phases.

### How to write custom listener?
In order to hook into the processing lifecycle simply create an OSGi `@Component` that will implement the `EventListener` interface. The actual `EventManager` object is accessible through the `ScriptManager` OSGi component that can be injected to the custom component.

```java
@Component
public class PredefinedDefinitionsListener implements EventListener {

    @Reference
    private ScriptManager scriptManager;
 
    @Activate
    private void activate() {
        scriptManager.getEventManager().addListener(Event.INIT_DEFINITIONS, this);
    }
 
    @Override
    public void handle(Script script, Context context, Mode mode, Progress progress) {
        ...
    }
}
```

### What are the phases available to hook?

* Script execution related
  * `BEFORE_EXECUTE`,
  * `AFTER_EXECUTE`,
* Publish instance related
  * `BEFORE_REPLICATE`
  * `AFTER_REPLICATE`
* Script lifecycle related
  * `BEFORE_REMOVE`,
  * `AFTER_SAVE`,
* Script manager internals
  * `INIT_DEFINITIONS`.
