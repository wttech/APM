/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 - 2016 Cognifide Limited
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
    $(window).adaptTo("foundation-registry").register(
        "foundation.collection.action.action", {
            name: "dashboard.dryrun",
            handler: function (name, el, config, collection, selections) {
                var selected = selections[0].attributes['data-path'].value;
                runOnAuthor(selected, "DRY_RUN");
            }
        });

    $(window).adaptTo("foundation-registry").register(
        "foundation.collection.action.action", {
            name: "dashboard.runonauthor",
            handler: function (name, el, config, collection, selections) {
                var selected = selections[0].attributes['data-path'].value;
                runOnAuthor(selected, "RUN");
            }
        });

    $(window).adaptTo("foundation-registry").register(
        "foundation.collection.action.action", {
            name: "dashboard.runonpublish",
            handler: function (name, el, config, collection, selections) {
                var selected = selections[0].attributes['data-path'].value;
                runOnPublish(selected);
            }
        });

    function runOnAuthor(scriptPath, mode) {

        $.ajax({
            type: "POST",
            url: "/bin/cqsm/run-background?file=" + scriptPath + "&mode="
            + mode,
            dataType: "html",
            success: function (data) {
                var jobId = JSON.parse(data).id;

                (function checkStatus(jobId) {
                    $.ajax({
                        type: "GET",
                        url: "/bin/cqsm/run-background?id=" + jobId,
                        dataType: "html",
                        success: function (data) {
                            var dataObject = JSON.parse(data);
                            if (dataObject.type === 'running') {
                                setTimeout(function () {
                                    checkStatus(jobId)
                                }, 1000);
                            } else if (dataObject.type === 'finished') {
                                console.log(scriptPath + " finished: " + JSON.stringify(dataObject.entries));
                                console.log(
                                    "Implement me: provide more sophisticated way of displaying results after finishing the script")
                            } else if (dataObject.type === 'unknown') {
                                console.log(
                                    "Implement me: handle \"unknown\" case");
                            }
                        }
                    });
                })(jobId);
            }
        });
    }

    function runOnPublish(fileName) {

        $.ajax({
            type: "GET",
            url: "/bin/cqsm/replicate?run=publish&fileName=" + fileName,
            dataType: "json",
            success: function (data) {
                console.log("publish response: " + JSON.stringify(data));
                console.log("Implement me: handle \'publish\' case");
            }
        });
    }

})(window, jQuery);
