## Roadmap
Although the tool has been used for years with lot of success it's not a finalized implementation. As the AEM platform keeps upgrading, there is room for improvements. This section gives high level insights of what is considered on the development roadmap.

| Feature | Description |
| -------- | ------------ |
| Omnisearch | As the AEM 6.2 provides capability of quick search, the tool should support that by exposing most commonly used screens. |
| Look&feel | The look&feel of the tool has been created for the CQ 5.4 purpose and is not reflecting current TouchUI standards |
| Testing | The testing (unit & functional) is limited and should be improved vastly.|
| Markup improvements | The tool from the very beginning focused on the functionality thus creating some technical debt on the markup side. |
| ActionMapper injectors | The usage of `Context` object as a global accessible object makes the design impact. Having the possibility of simple injection would help it. |
| API, Core, Foundation split | There are still some violations in terms of dependencies between packages. This shows some misdesign decisions. |
| User guides | More direct an visually rich user tutorials to guide through specific APM features. |
| Modules extraction | Currently, automatic executor and run on publish are quite seprated functionality that can and may be extracted to at least separated maven module. |
| AEM platform compatibility | The approach to make the tool usable on wider set of AEM versions. |
