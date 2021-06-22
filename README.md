<p align="center">
    <img src="docs/wtt-logo.png" style="vertical-align: middle">
</p><p align="center">
    <img src="docs/apm-logo.png" alt="APM Logo" style="width: 128px; vertical-align: middle">
</p>

[![Build Status](https://github.com/wttech/APM/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/wttech/APM/actions/workflows/gradle.yml)
[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/cognifide/apm.svg?label=License)](http://www.apache.org/licenses/)

# AEM Permission Management
## About
APM (**A**EM **P**ermission **M**anagement) is an AEM based tool focused on streamlining the permission configuration. It provides a rich UX console tailored for administrators. They can write human readable scripts that handle user/group creation/deletion and permissions application, both in bulk. Through it's flexible grammar, exposed API, and high extensibility it vastly improves **permission**-based implementations.

### Features
* DSL (Domain Specific Language) that makes the script human readable.
* Auditability for all permission scheme changes.
* Support for glob regexp access, that's not available ootb.
* Bulk changes for whole permission schemes.
* HTTP API access for better CI/CD implementations.
* Backend Java API for use of permission-based project features.

### Compatibility
This page identifies the versions of Adobe Experience Manager with which a particular version of AEM Permission Management is compatible.

| | AEM 6.3 | AEM 6.4 | AEM 6.5 |
| --- | :---: | :---: | :---: |
| APM 5.x.x | | x | x |
| APM 4.x.x | x | x | x |
| APM 3.2.x |   | x | |
| APM 3.1.x |   | x | |
| APM 3.0.x |  x | | |

## Getting started
To start using **APM** an AEM in version at least 6.3 is required.
The latest AEM packages are available [here](https://github.com/wttech/APM/releases/latest). Download package *apm-all*, and install it using [CRX Package Manager](http://localhost:4502/crx/packmgr).

### How to use
Open **APM** dashboard [http://localhost:4502/apm.html](http://localhost:4502/apm.html), and start using the tool. For more information visit [user guide](docs/ui.md).

### Documentation
* [UI](docs/ui.md) - quick tour for APM user's interface.
* [Grammar](docs/grammar.md) - syntax of APM scripts, and description of main actions.
* [Permissions](docs/permissions.md) - actions used for adding and revoking access to resources. 
* [Launchers](docs/launchers.md) - configuring auto execution of scripts.
* [Backend API](docs/backend-api.md) - executing scripts from backend services.
* [Custom actions](docs/custom-actions.md) - implement your own action.

## What's new?
### APM 5.0.0
* Introduction of ANTLR 4.
* New way of registering actions (no regex, just friendly annotations).
* Improvements in UI (especially in script's editor and viewer).
* Introduction of new launchers (auto execution of scripts after package installation).
* Code separated in several modules (cleaner API).

## How to contribute
See details in [Contributing section](https://github.com/wttech/APM/blob/master/CONTRIBUTING.md)

## License
**APM** is licensed under [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt)

## Commercial Support

Technical support can be made available if needed. Please [contact us](mailto:labs-support@cognifide.com) for more details.

We can:

* prioritize your feature request,
* tailor the product to your needs,
* provide a training for your engineers,
* support your development teams.
