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

### How to use predefined placeholders?

```
FOR GROUP acme-authors
    ALLOW ${Websites} READ
```
Available predefined placeholders:

| Name                     | Value                                                    |
| ------------------------ | -------------------------------------------------------- |
| Backup                   | /libs/cq/core/content/welcome/features/backup            |
| CRXDE Lite               | /libs/cq/core/content/welcome/features/crxde             |
| Campaigns                | /libs/mcm/content/admin                                  |
| Cloud Services           | /libs/cq/core/content/welcome/resources/cloudservices    |
| Clustering	           | /libs/cq/core/content/welcome/features/cluster           |
| Communities	           | /libs/collab/core/content/admin                          |
| Developer Resources      | /libs/cq/core/content/welcome/docs/dev                   |
| Digital Assets           | /libs/wcm/core/content/damadmin                          |
| Documentation	           | /libs/cq/core/content/welcome/docs/docs                  |
| Inbox	                   | /libs/cq/workflow/content/inbox                          |
| Manuscripts	           | /libs/cq/core/content/welcome/resources/manuscriptsadmin |
| OSGi Console	           | /libs/cq/core/content/welcome/features/config            |
| OSGi Console Status Dump | /libs/cq/core/content/welcome/features/statusdump        |
| Package Share	           | /libs/cq/core/content/welcome/features/share             |
| Packages	           | /libs/cq/core/content/welcome/features/packages          |
| Publications	           | /libs/cq/core/content/welcome/resources/publishingadmin  |
| Replication	           | /libs/cq/core/content/welcome/resources/replication      |
| Reports                  | /libs/cq/core/content/welcome/resources/reports          |
| Tagging                  | /libs/cq/tagging/content/tagadmin                        |
| Task Management          | /libs/cq/core/content/welcome/resources/taskmanager      |
| Tools	                   | /libs/wcm/core/content/misc                              |
| Users                    | /libs/cq/security/content/admin                          |
| Websites                 | /libs/wcm/core/content/siteadmin                         |
| Workflows	           | /libs/cq/core/content/welcome/resources/workflows        |

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
