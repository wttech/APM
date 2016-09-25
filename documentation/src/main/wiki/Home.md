![logo](https://github.com/Cognifide/APM/raw/master/misc/apmLogo.png)
## Getting started

### What is APM
APM (**A**EM **P**ermission **M**anagement) is an AEM based tool focused on streamlining the permission configuration. It provides a rich UX console tailored for administrators. They can write human readable scripts that handle user/group creation/deletion and permissions application, both in bulk. Through it's flexible grammar, exposed API, and high extensibility it vastly improves **permission**-based implementations.

### Why APM
Creating and managing user groups is often time-consuming and vulnerable. Original solution provided in AEM, that is user admin panel, is a nice tool for ad-hoc maintenance, but is not suitable for multilayer architecture, where application needs to be installed on developers' instances first, then on QA machines, finally being maintained by client on live instance.

Having a solution that provides an easy way to migrate the ACL changes to other environments is required by all projects that are taking benefits of access restrictions.

Key features:
* DSL (Domain Specific Language) that makes the script human readable,
* Auditability for all permission scheme changes,
* Support for glob regexp access, that's not available ootb,
* Bulk changes for whole permission schemes,
* HTTP API access for better CI/CD implementations,
* Backend Java API for use of permission-based project features.
