## Using includes
For the better script maintenance, splitting scripts into smaller, functional blocks is preffered. The APM tool provides ways to control that.

### How to define and use placeholders?
To define a reusable string variable a `DEFINE` action needs to be used.

```
DEFINE siteName acme-company
```

The variable then can be used whenever a string parameter is provided. It could be easily used to construct more complex strings as well. E.g.

```
FOR GROUP acme-authors
    ALLOW /content/${siteName} ALL
    DENY /content/${siteName}-demo READ
```

**The good practice is to write scripts according to the reusability possibility. If the same script can be executed in a context of different path/user - make the user or path parameterized.**

This is especially useful when splitting the scripts according to the next section.

### How to split scripts into small functional blocks?
The `IMPORT` action can be used to import a full copy of the referenced script.

```
DEFINE siteName acme-company
```

```
IMPORT variables.cqsm
FOR GROUP acme-authors
    ALLOW /content/${siteName} ALL
    DENY /content/${siteName}-demo READ

```
