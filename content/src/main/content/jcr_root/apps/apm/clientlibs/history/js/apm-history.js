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
    "use strict";
    $(document).on('cui-contentloaded', function () {

        // prototype pattern
        var SummaryHandling = function ($el) {
            this.$rootElement = $el;
            this.executionSummaryButtons = this.$rootElement.find(
                '.execution-summary-button');
            this.delegateEvents();
        };

        SummaryHandling.prototype = {
            showDialog: function (scriptPath) {
                var self = this;
                $.ajax({
                    type: "GET",
                    url: "/apps/apm/summary/jcr:content/summary.html"
                        + scriptPath,
                    dataType: "html",
                    success: function (data) {

                        if (self.$rootElement.has(self.summaryDialog).length) {
                            self.summaryDialog.remove();
                        }
                        self.summaryDialog = $.parseHTML($.trim(data))[0]; // trick to remove problematic spaces created by sightly tags
                        self.summaryDialog.on("coral-overlay:close",
                            function () {
                                this.remove();
                            });
                        self.$rootElement.append(self.summaryDialog);
                        self.summaryDialog.show();
                    }
                });
            },
            delegateEvents: function () {
                var self = this;

                this.executionSummaryButtons.each(function () {
                    $(this).on("click", function () {
                        var scriptResourcePath = $(this).attr(
                            "scriptResourcePath");
                        self.showDialog(scriptResourcePath);
                    });
                });
            }
        };

        var summaryHandling = new SummaryHandling($('body'));
    });
})(window, jQuery);