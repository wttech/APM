<p align="center">
    <img src="https://assets.cognifide.com/github/cognifide-logo.png" style="vertical-align: middle">
</p><p align="center">
    <img src="apm-logo.png" alt="APM Logo" style="width: 128px; vertical-align: middle">
</p>

# Grammar
Starting from version 5.0.0 APM uses [ANTLR 4](https://www.antlr.org/) to parse scripts. ANTLR was introduced due to limitation of original approach e.g.:
* Script was analyzed line by line - it was very hard to introduce changes like blocks of code etc.
* Each action had its own regexs to match single script line - there was no way to guarantee a consistent way of processing parameters for the actions.

Thanks to ANTLR, APM has new layer of abstraction, which solves these and other problems. 

## Basics
### Definitions
```
DEFINE baseGroup 'group-base'

CREATE-GROUP $baseGroup # usage of definition baseGroup
CREATE-GROUP $baseGroup + '-2' # simple concatenation of string
```

### For Loop
```
FOR-EACH i IN ['a', 'b', 'c', 'd'] BEGIN # starts block of code executed for each iteration of loop
    CREATE-GROUP 'group-' + $i
END # end of block
```

### Comments
There are only single-line comments in APM. Comments start with `#`.
```
# =========================================
# Creates base groups required by authors
# =========================================

CREATE-GROUP $baseGroup # creates base group
```

### Import 
`IMORT` reads all definitions from given script and puts them in current context.
```
# /apm/conf/scripts/definitions.apm

DEFINE baseGroup 'group-base'
```

```
# /apm/conf/scripts/main.apm

IMPORT /apm/conf/scripts/definitions.apm # you may use both relative or absolute path in import
IMPORT definitions.apm AS def # you may use namespace to avoid overriding of your definitions

CREATE-GROUP $def.baseGroup + '-1'
CREATE-GROUP $baseGroup + '-2'
```
### Run
`RUN` executes given script. The given script has separate context, so if you want it to use any of definitions from main script, you must pass them explicitly.
```
# /apm/conf/scripts/author.apm

REQUIRE locale # execution fails if definition locale is not present

CREATE-GROUP 'authors-' + $locale
``` 

```
# /apm/conf/scripts/main.apm

RUN /apm/conf/scripts/author.apm locale= 'fr_fr' # you may use both relative or absolute path in run
RUN author.apm locale= 'en_us'
```
## Authorizable Actions
Authorizable actions are used for the purpose of authorizable maintenance. They cover CRUD operations on Groups and Users as well as relationship configuration.

### Non-contextual actions
Following action can be executed with no context user preserved by using `FOR-*` action. The operations are used for basic user or group creation or removal.
* `CREATE-GROUP` - creates new authorizable of group type.
* `CREATE-USER` - creates new authorizable of user type.
* `CREATE-SYSTEM-USER` - creates new system user for the service login purpose.
* `DELETE-GROUP` - removes the selected group.
* `DELETE-USER` - removes user from assigned groups, given permission and user itself.

Create operations will fail if the authorizable exists already. If the script is intended to be idempotent it's encouraged to add the `--IF-NOT-EXISTS` modifier at the end of the action. This way the script will pass through the execution even if the group cannot be created due to id conflict.

```
CREATE-GROUP 'acme-authors' --IF-NOT-EXISTS
```

### Contextual actions
#### Configure the membership
The membership operation can be executed either from user or group context. Depending on the action selected the operation will either cut all membership links or just some.

* `ADD-PARENTS` - current authorizable becomes child (member) of given groups.
* `REMOVE-PARENTS` - current authorizable stops being child (member) of given groups.

```bash
FOR-GROUP 'acme-authors' BEGIN
    ADD-PARENTS 'platform-base' # acme-authors becomes member of platform-base (platform-base is parent of acme-authors) 
END

FOR-USER 'john-doe' BEGIN
    ADD-PARENTS ['platform-base', 'acme-base'] # john-doe becomes member of platform-base and acme-base groups
END
```

* `ADD-CHILDREN` - adds selected authorizables into the group that's in the execution context.
* `REMOVE-CHILDREN` - removes selected authorizables from the group that's in the execution context.

```bash
FOR-GROUP 'platform-base' BEGIN
    REMOVE-CHILDREN 'john-doe'
END
```

* `REMOVE-PARENTS-GROUPS` - for the context of an authorizable removes all existing groups that are members of it.
* `REMOVE-CHILDREN-GROUPS` - for the context of a group removes all it's members.

```bash
FOR-GROUP 'acme-authors' BEGIN
    # clears acme-authors from all groups it is part of
    REMOVE-PARENTS-GROUPS
END

FOR-GROUP 'platform-base' BEGIN
    # removes all members of platform-base group
    REMOVE-CHILDREN-GROUPS
END
```

#### Set property
To add or remove a property for the authorizable within a context of execution two actions should be considered:
* `SET-PROPERTY`
* `REMOVE-PROPERTY`

```
FOR-USER 'john-doe' BEGIN
    SET-PROPERTY 'profile/givenName' 'John Doe'
END
```

#### Set password
Creating users would not make any sense if those could not login. For such purpose the `SET PASSWORD` action should be used. Bear in mind keeping actual passwords in scripts is a security vulnerability and should be avoided. This operation should be used sparely and user should be forced to login and change the password as quickly as possible.

```
FOR-USER 'john-doe' BEGIN
    SET-PASSWORD 'magic!42'
END
```
