![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# How to contribute to AEM Permission Management
Thank you for taking the time to contribute!
We appreciate all commits and improvements, feel free to join APM community and contribute.

## How to start
Please refer to [Installation guide](https://github.com/Cognifide/apm/wiki/Installing) section in the wiki space.
After setting up APM on your AEM instance you may check if it works properly entering [http://localhost:4502/etc/cqsm.html](http://localhost:4502/etc/cqsm.html) (when running default AEM port configuration).

The project uses default [AEM archetype](https://docs.adobe.com/docs/en/aem/6-2/develop/dev-tools/ht-projects-maven.html). To build the project use:

```bash
# to install whole AEM package (Java + content)
$ mvn clean install -PautoInstallPackage

# to install Java code (bundle) only
$ mvn clean install -PautoInstallBundle
```

## APM Contributor License Agreement
Project License: [Apache License Version 2.0](https://github.com/Cognifide/apm/blob/master/LICENSE)
- You will only Submit Contributions where You have authored 100% of the content.
- You will only Submit Contributions to which You have the necessary rights. This means that if You are employed You have received the necessary permissions from Your employer to make the Contributions.
- Whatever content You Contribute will be provided under the Project License(s).

## Commit Messages
When writing a commit message, please follow the guidelines in [How to Write a Git Commit Message](http://chris.beams.io/posts/git-commit/).

## Pull Requests
Please add the following lines to your pull request description:

```

---

I hereby agree to the terms of the APM Contributor License Agreement.
```

## Documentation
All APM documentation is in the same repository as codebase in [documentation](https://github.com/Cognifide/apm/tree/master/documentation) module.
This documentation after update is ported to [APM wiki](https://github.com/Cognifide/apm/wiki).
When updating documentation please update proper markdown pages in [documentation](https://github.com/Cognifide/apm/tree/master/documentation) module following [instructions](https://github.com/Cognifide/apm/blob/master/documentation/README.md) and include it with your pull request.
After your pull request is merged, wiki pages will be updated. **Please do not update wiki pages directly because your changes will be lost.**

## Coding Conventions
Below is short list of things that will help us maintaining APM quality and accept pull requests:
- Follow [Google Style Guide](https://github.com/google/styleguide) code formatting,
- write javadoc, especially for interfaces and abstract methods,
- update [documentation](https://github.com/Cognifide/apm/tree/master/documentation) and include changes in the same pull request which modifies the code,
- when committing an improvement, try to show it in local demo example,
- when logging use proper levels: `INFO` and `WARNING` should log only very important messages.
