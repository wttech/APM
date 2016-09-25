## Context Actions
Among all actions there is also an action that does not alter any data in the repository but rather change the context of the subsequent actions exection.

### How to change authorizable context?
Whenever `FOR USER` or `FOR GROUP` action is found, the current authorizable context is changed. All following operations that require the context will be using the authorizable configured beforehand

```
FOR USER ${siteName}-author-es_US
    CLEAR GROUPS
```

Although it may look like a function, known in other programming languages, the intendetion is used here just for the readability. As mentioned in [[Actions|Actions]] section, all whitespaces are ignored. Nevertheless **using intendation is considered best practice standard**
