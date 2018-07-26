/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 - 2018 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
(function (window, $) {
    $(document).on('cui-contentloaded', function () {

        // prototype pattern
        function SummaryDialog($el) {

            this.summaryTable = (function () {
                function createHeaderCell(text) {
                    return new Coral.Table.HeaderCell().set({
                        content: {
                            textContent: text
                        }
                    });
                }

                var headers = ["No", "Authorizable", "Action",
                    "Parameters", "Status", "Messages"];
                var headRow = new Coral.Table.Row();
                var headVar = new Coral.Table.Head().set({
                    sticky: true
                });
                headers.forEach(function (header) {
                    headRow.appendChild(createHeaderCell(header));
                });
                headVar.appendChild(headRow);
                var table = new Coral.Table().set({
                    head: headVar
                });
                return table;
            })();
            var contentVar = new Coral.Dialog.Content();
            contentVar.appendChild(this.summaryTable);
            var self = this;
            this.dialog = new Coral.Dialog().set({
                id: "summaryDialog",
                header: {},
                content: contentVar,
                footer: {
                    innerHTML: "<button is=\"coral-button\" icon=\"download\" iconsize=\"S\">\n"
                    + "Download\n"
                    + "</button><button is=\"coral-button\" variant=\"primary\" coral-close=\"\" class=\"coral-Button coral-Button--primary\" size=\"M\"><coral-button-label>Ok</coral-button-label></button>"
                },
                variant: "info"
            }).on("coral-overlay:close", function () {
                self.summaryTable.items.clear();
            });
            $el.append(this.dialog);
            this.executionSummaryButtons = $el.find(
                '.execution-summary-button');
            this.delegateEvents();
        };

        SummaryDialog.prototype = {

            showDialog: function (executionSummary, scriptName,
                executor, instance) {
                var self = this;
                if (self.summaryTable.items.length > 0) {
                    self.summaryTable.items.clear();
                }

                function appendRow(table, rowData) {
                    var row = table.items.add({});
                    rowData.forEach(function (rowDataValueHtml) {
                        row.appendChild(new Coral.Table.Cell().set({
                            content: {
                                innerHTML: rowDataValueHtml
                            }
                        }));
                    });
                }

                self.dialog.header.innerText = "The result of " + scriptName
                    + " script by " + executor + " on " + instance + " instance";

                executionSummary.forEach(function (element, index) {
                    var authorizable = element.authorizable || "";
                    var messageHtml = (function (rawMessages) {
                        var messageHtml = "";
                        rawMessages.forEach(function (rawMessage, index, arr) {
                            var span = $('<span />').html(rawMessage.text);
                            if (rawMessage.info) {
                                span.addClass(rawMessage.info);
                            }
                            if (index !== arr.length - 1) {
                                span.append('<br />');
                            }
                            messageHtml += span[0].outerHTML;
                        });
                        return messageHtml;
                    })(element.messages);
                    var rowData = [index + 1, authorizable, element.actionName,
                        element.parameters, element.status, messageHtml];
                    appendRow(self.summaryTable, rowData);
                });
                self.dialog.show();
            },

            delegateEvents: function () {
                var self = this;

                this.executionSummaryButtons.each(function (index) {
                    $(this).on("click", function () {
                        // For the boolean value
                        var executionSummaryJson = $(this).attr(
                            "executionSummaryJson");

                        if (executionSummaryJson) {
                            var executionSummary = JSON.parse(
                                executionSummaryJson);
                            var scriptName = $(this).attr(
                                "scriptName");
                            var executor = $(this).attr(
                                "executor");
                            var instance = $(this).attr("instanceType");
                            self.showDialog(executionSummary, scriptName,
                                executor, instance);
                        }
                    });
                });
            }
        };

        var summaryDialog = new SummaryDialog($('body'));

    });
})(window, jQuery);