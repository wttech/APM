## Run on publish
For some cases it's required for the script to be executed on the publish side as well. This feature overlaps with the AEM [Closed User Management](https://docs.adobe.com/docs/en/aem/6-2/administer/security/cug.html) so should be used wisely.

### How to run a script on publish?
Running the script on publish is as easy as clicking the `Run on publish` button. The script replication, execution and pushing back may take a while. Once it's done, the summary will be available in the [[History|History]] section.

![run-on-publish](assets/screens/runOnPublish.png)

### When to use the feature?
CUGs are working well for pages and assets. For the resources lying outside this structure a separated treating is required. This is particulary useful whenever:

* fine-grained permission management for **tags** is required,
* **authorizable creation** on publish instance is required e.g. for cug admitted groups purpose,
* execution summary is crucial.

### Technical notes
All the scripts replicated to the publish instance and are verified  will be executed. The result of the execution will be then pushed back to author. For the purpose of publish to author communication the [CQ-Actions](https://github.com/Cognifide/CQ-Actions) library is used.
