<p align="center">
    <img src="wtt-logo.png" style="vertical-align: middle">
</p><p align="center">
    <img src="apm-logo.png" alt="APM Logo" style="width: 128px; vertical-align: middle">
</p>

# Launchers
The script can be configured to be launched automatically whenever prerequisites are met. This feature could be useful whenever scheduled permission maintenance is in place or just as part of the deployment procedure.

## How to configure the script to be automatically launched?
To launch script automatically, the script needs to be placed under `/conf/apm/scripts` path. Furthermore, the `apm:launchEnabled` needs to be set to `true`, and `apm:launchMode` needs to have a value of one of the following:
* `ON_STARTUP` - to launch the script on every bundle activation without any other conditions,
* `ON_STARTUP_IF_MODIFIED` - to launch the script on bundle activation if the file's changed,
* `ON_SCHEDULE` - to launch the script on specific date, which needs to be set in property `apm:launchSchedule`,
* `ON_CRON_EXPRESSION` - to launch the script on specific CRON expression, which needs to be set in property `apm:launchCronExpression`,
* `ON_INSTALL` - to launch the script after package installation (additional configuration required, see [Install hook](#install-hook)),
* `ON_INSTALL_IF_MODIFIED` - to launch the script after package installation if the file's changed (additional configuration required, see [Install hook](#install-hook)).

An additional way to automatically launch scripts is to use `AEM Permission Management - Install Launcher Configuration` (PID `com.cognifide.apm.install.launchers.ApmInstallService`) service.

## Install hook
### One package
If you want to use `ON_INSTALL` or `ON_INSATLL_IF_MODIFIED` options, you need to configure install hook in your package. Just add entry to the `properties.xml` file:
```
<entry key="installhook.apm.class">com.cognifide.apm.core.tools.ApmInstallHook</entry> 
```
### Multiple packages
When you have different packages with apm scripts, you may set different names for install hooks:
```
<entry key="installhook.cherry.class">com.cognifide.apm.core.tools.ApmInstallHook</entry> 
```
```
<entry key="installhook.orange.class">com.cognifide.apm.core.tools.ApmInstallHook</entry> 
```
Then add properties to your scripts:
```
  apm:launchMode="on_install"
  apm:launchHook="cherry"
```
```
  apm:launchMode="on_install"
  apm:launchHook="orange"
```
First script will be launched after installation of first package, and the other one, after installation of second package.        

Packages with install hook can only be deployed after deployed package *apm-ui.apps*.
