## Session saving mode
By default, the repository operations are committed after each and every action execution. This policy could be changed and session saving can be managed manually.

### How to configure session saving policy?
Following modes are available for the session save policy configuration:
* EVERY-ACTION - the default policy, commits the changes with every operation,
* SINGLE - the changes will be applied once at the end of the script execution,
* ON-DEMAND - the changes will never be applied automatically but rather `SAVE` action must be used,
* NEVER - the changes will never be applied, either automatically or manually.

```
SESSION SAVE ON-DEMAND
```

### How to save the session?
Saving the session i.e. committing all changes that are stored in-memory is as easy as invoking

```
SAVE
```
