## Authorizable Actions
Authorizable actions are used for the purpose of authorizable maintenance. They cover CRUD operations on Groups and Users as well as relationship configuration.

### Non-contextual actions
Following action can be executed with no context user preserved by using `FOR` action. The operations are used for basic user or group creation or removal.

* `CREATE GROUP` - creates new authorizable of group type,
* `CREATE USER` - creates new authorizable of user type,
* `CREATE SYSTEM USER` - creates new system user for the service login purpose,
* `REMOVE GROUP` - removes the selected group,
* `REMOVE USER` - removes the selected user.

Create operations will fail if the authorizable exists already. If the script is intended to be idempotent it's encouraged to add the `IF NOT EXISTS` modifier at the end of the action. This way the script will pass through the execution even if the group cannot be created due to id conflict.

```
CREATE GROUP acme-authors IF NOT EXISTS
```

### Contextual actions
#### How to configure the membership?
The membership operation can be executed either from user or group context. Depending on the action selected the operation will either cut all membership links or just some.

* `ADD TO GROUP` - adds selected group to a authorizable that's in the execution context,
* `REMOVE FROM GROUP` - removes selected group from a authorizable that's in the execution context.

```bash
FOR GROUP acme-authors
    # applies access to basic AEM functionality like Sites, Tags, ...
    ADD TO GROUP platform-base

FOR USER john-doe
    # or using list parameter
    ADD TO GROUP [platform-base, acme-base]
```

* `INCLUDE` - includes (adds) selected authorizables into the group that's in the execution context,
* `EXCLUDE` - excludes (removes) selected authorizables from the group that's in the execution context.

```bash
FOR GROUP platform-base
    EXCLUDE john-doe
```

* `CLEAR FROM GROUPS` equal to `CLEAR FROM GROUPS ALL-PARENTS` - for the context of an authorizable removes all existing groups that are members of it,
* `CLEAR GROUPS` equal to `CLEAR FROM GROUPS ALL-CHILDREN` - for the context of a group removes all it's members.

`CLEAR GROUPS` is considered deprecated so `CLEAR FROM GROUPS ALL CHILDREN`

```bash
FOR GROUP acme-authors
    # clears acme-authors from all groups it is part of
    CLEAR FROM GROUPS ALL-PARENTS

FOR GROUP platform-base
    # removes all members of platform-base group
    CLEAR FROM GROUPS ALL-CHILDREN
```

#### How to configure the authorizable properties?
To add or remove a property for the authorizable within a context of execution two actions should be considered:
* `SET PROPERTY`
* `REMOVE PROPERTY`

```
FOR USER john-doe
    SET PROPERTY profile/givenName 'John Doe'
```

#### How to setup user password?
Creating users would not make any sense if those could not login. For such purpose the `SET PASSWORD` action should be used. Bear in mind keeping actual passwords in scripts is a security vulnerability and should be avoided. This operation should be used sparely and user should be forced to login and change the password as quickly as possible.

```
FOR USER john-doe
    SET PASSWORD magic!42
```
