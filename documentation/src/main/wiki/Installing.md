## Installation guide

This is a quick guide showing how to setup APM on an AEM instance.

### Prerequisites
The tool is delivered as a typical AEM (CRX) package ready to be installed using [Package Manager](https://docs.adobe.com/docs/en/aem/6-2/administer/content/package-manager.html#Installing Packages)

You need to download following package
* [APM 2.0](foo/bar)

### Sanity check
In order to validate that the installation was successful:
* open [OSGi Felix Console](http://localhost:4502/system/console) and make sure the bundle foo/bar is Active
* open [Import page](http://localhost:4502/etc/cqsm.html) and verify if the UI loads fine

### AEM compatibility
The tool is heavily dependent on the AEM instance version. The main development version supports the latest AEM version released

| AEM version | Support | Last stable release |
| -------- | ------------ |
| AEM 6.2 | Supported in development version (master) | |
| AEM 6.1 | Supported, released on demand (master-aem61)| |
| AEM 6.0 and earlier | not supported | N/A |
