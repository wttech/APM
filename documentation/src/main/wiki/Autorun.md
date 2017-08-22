## Auto Execution
The script can be configured to be executed automatically whenever prerequisites are met. This feature could be useful whenever scheduled permission maintenance is in place or just as part of the deployment procedure

### How to configure the script to be autoexecuted?
To use automatic script the script needs to be placed under `/etc/cqsm/import/jcr:content/cqsmImport` path. Furthermore the `cqsm:executionMode` needs to have a value of one of the following:
* `ON_MODIFY` - to run the script on bundle activation if the file was changed,
* `ON_SCHEDULE` - to run the script periodically according to the `cqsm:executionSchedule` date,
* `ON_START` - to run the script on every bundle activation without any other conditions.

### How to configure scripts to take run modes into account?
Under the `/etc/cqsm/import/jcr:content/cqsmImport` path, add a configuration folder, depending on which instance you want to run script, ie` config.author`

Scripts should be under config path and contains `cqsm:executionMode = ON_START`

Example:
Script path - `/etc/cqsm/import/jcr:content/cqsmImport/config.author/script.cqsm`
Script content:
cqsm:executionMode = ON_STARTUP
instance run modes: author
RESULT: script will be run

Script path - `/etc/cqsm/import/jcr:content/cqsmImport/config.publish/script.cqsm`
Script content:
cqsm:executionMode = ON_STARTUP
instance run modes: author
RESULT: script won't be run

The modification condition is validated based on the `MD5` checksum that is stored within `/var/cqsm/scripts/version` node.

If the autorun should be disabled by any reason an additional flag `cqsm:executionEnabled` should be configured with `false` string value.
