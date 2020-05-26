## Permission actions
Permission actions are used for the purpose of ACL maintenance. They cover adding or revoking access to resources as well as purging them. All of them have to be invoked within a context of authorizable so a `FOR USER` or `FOR GROUP` action needs to precede them.

### Operations
#### How to clear permissions configuration?
For the purpose of clearing permissions, the `PURGE` operation exist. **It is highly recommended for the scripts to be idempotent** i.e. to be able to execute them with no preparation beforehand. For this purpose, the permissions on the subtree of interest should be purged at the beginning of the script execution.

```
FOR USER john-doe
    PURGE /content
```

If the ACL clearance should affect only selected path the `REMOVE ALL` should be used instead.
```bash
FOR USER john-doe
    # affects only selected page
    PURGE /content/geometrixx
```

> Purging and removing will remove all applied permissions, including those added manually.

#### How to add access to a resource?
Adding an access to a resource could be achieved using the `ALLOW` action. Technically speaking, `ALLOW` action adds a new node under `rep:policy` node with the `rep:GrantACE` nodetype. It's always added as the last one in the existing Access Control List (ACL). Due to implementation specific, if the permission exist already, the operation will not be effective.

```
FOR USER john-doe
    ALLOW /content READ
    ALLOW /content/geometrixx [READ, MODIFY]
    ALLOW /content/geometrixx-outdoors men [READ]
```

#### How to revoke access to a resource?
The `DENY` action is complementary to `ALLOW` but rather adds a `rep:DenyACE` nodetype thus restricting the access to the subtree.

```
FOR USER john-doe
    DENY /content READ
    DENY /content/geometrixx [READ, MODIFY]
    DENY /content/geometrixx-outdoors men [READ]
```

### What are the available access control modifiers?
APM allows to use every access modifier registered in AccessControlManager. Additionally, there are some predefined privilege groups:  

| Modifier | JCR/AEM equivalent |
| -------- | ------------ |
| READ | Privilege.JCR_READ |
| MODIFY | Privilege.JCR_MODIFY_PROPERTIES, Privilege.JCR_LOCK_MANAGEMENT, Privilege.JCR_VERSION_MANAGEMENT |
| MODIFY_PAGE | Privilege.JCR_REMOVE_NODE, Privilege.JCR_REMOVE_CHILD_NODES, Privilege.JCR_NODE_TYPE_MANAGEMENT, Privilege.JCR_ADD_CHILD_NODES |
| CREATE | Privilege.JCR_ADD_CHILD_NODES, Privilege.JCR_NODE_TYPE_MANAGEMENT |
| DELETE | Privilege.JCR_REMOVE_NODE, Privilege.JCR_REMOVE_CHILD_NODES |
| REPLICATE | "crx:replicate" |
| ALL | Privilege.JCR_READ, Privilege.JCR_WRITE, Privilege.JCR_LOCK_MANAGEMENT, Privilege.JCR_VERSION_MANAGEMENT, Privilege.JCR_NODE_TYPE_MANAGEMENT, "crx:replicate" |
| READ_ACL | Privilege.JCR_READ_ACCESS_CONTROL |
| MODIFY_ACL | Privilege.JCR_MODIFY_ACCESS_CONTROL |
| DELETE_CHILD_NODES | Privilege.JCR_REMOVE_CHILD_NODES |

### What are Glob patterns?
The Access Control Entries (ACEs) can be either applied directly on the path selected or a `rep:glob` restriction can be used. This restriction does not take an immediate effect but rather uses a regexp pattern provided to match the target node. If the requested resource path matches the regexp and the resource is lying below the path where the regexp was configured, the actual modifier is effective.

```bash
# Will only match pages named home
ALLOW /content/geometrixx home [ALL]

# Will only match jcr:titles properties
ALLOW /content /jcr:content/jcr:title [ALL]
```

If the permission should be applied (or revoked) on the strictly selected path only, while not affecting the subtree as it is by default a glob pattern with empty regexp should be used. In order to recognize the empty pattern a `STRICT` keyword is used.

```bash
ALLOW /content/geometrixx STRICT [ALL]
```

Although it may look tempting to use this feature, the **best practice is to prefer the usual permission application over glob restriction**. Glob restriction make the permission scheme harder to read giving also some non-minimal performance overhead while calculating the effective permissions.

On the other hand, glob patterns are inevitable whenever we'd like to apply a permission scheme on not-yet-finalized content. Regexp matching makes it possible to treat the future content additions with no permission management effort.

[Read more in the OAK documentation](https://jackrabbit.apache.org/oak/docs/security/authorization/restriction.html)
