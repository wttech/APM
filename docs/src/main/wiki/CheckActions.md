## Check actions
Check actions can be used for the purpose of validation if the permission scheme, either applied by the APM tool or manually, is according to the agreements. All the operations are best-effort based i.e. they will never change anything in the repository but the tradeoff means they may not be right.

### How to use check actions?
Check action behavior in simulation and real execution modes is as follows:
* For dry run: error message is logged, when assertion fails but script execution proceeds.
* For run: error message is logged and execution exception is thrown. Script execution is interrupted.

The action list consists of:
* `CHECK-GROUP-EXISTS`
* `CHECK-USER-EXISTS`
* `CHECK-INCLUDES`
* `CHECK-EXCLUDES`
* `CHECK-ALLOW`
* `CHECK-DENY`
* `CHECK-NOT-EXISTS`
* `CHECK-PASSWORD`
* `CHECK-PROPERTY`
