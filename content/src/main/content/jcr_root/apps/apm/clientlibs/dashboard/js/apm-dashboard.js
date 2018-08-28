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
(function (window, document, $, Granite) {

    let uiHelper = $(window).adaptTo("foundation-ui");

    $(window).adaptTo("foundation-registry").register(
        "foundation.collection.action.activecondition", {
        name: "is-not-folder",
        handler: function(name, el, config, collection, selections) {
            return !isFolder(selections);
        }
    });

    $(window).adaptTo("foundation-registry").register(
        "foundation.collection.action.activecondition", {
            name:"is-available",
            handler: function(name, el, config, collection, selections) {
                if (isFolder(selections)) {
                    return false;
                }

                el.disabled = isScriptInvalidOrNonExecutable(selections);
                return true;
            }
        });

    $(window).adaptTo("foundation-registry").register(
        "foundation.collection.action.action", {
            name: "dashboard.dryrun",
            handler: function (name, el, config, collection, selections) {
                const selected = selections[0].attributes['data-path'].value;
                runOnAuthor(selected, "DRY_RUN");
            }
        });

    $(window).adaptTo("foundation-registry").register(
        "foundation.collection.action.action", {
            name: "dashboard.runonauthor",
            handler: function (name, el, config, collection, selections) {
                const selected = selections[0].attributes['data-path'].value;
                runOnAuthor(selected, "RUN");
            }
        });

    $(window).adaptTo("foundation-registry").register(
        "foundation.collection.action.action", {
            name: "dashboard.runonpublish",
            handler: function (name, el, config, collection, selections) {
                const selected = selections[0].attributes['data-path'].value;
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
                const parsedJSON = JSON.parse(data);
                const jobId = parsedJSON.id;
                const jobMessage = parsedJSON.message;

                (function checkStatus(jobId) {
                    $.ajax({
                        type: "GET",
                        url: "/bin/cqsm/run-background?id=" + jobId,
                        dataType: "html",
                        success: function (data) {
                            const dataObject = JSON.parse(data);
                            if (dataObject.type === 'running') {
                                setTimeout(function () {
                                    checkStatus(jobId)
                                }, 1000);
                            } else if (dataObject.type === 'finished') {
                                console.log(scriptPath + " finished: " + JSON.stringify(dataObject.entries));
                                switch(mode){
                                    case 'DRY_RUN':
                                        uiHelper.notify('info', 'Dry Run executed successfully', 'Info');
                                        break;
                                    case 'RUN':
                                        uiHelper.notify('info', 'Run on author executed successfully', 'Info');
                                        break;
                                }
                            } else if (dataObject.type === 'unknown') {
                                switch(mode) {
                                    case 'DRY_RUN':
                                        uiHelper.alert('Dry Run wasn\'t executed successfully', jobMessage, 'error');
                                        break;
                                    case 'RUN':
                                        uiHelper.alert('Run on author wasn\'t executed successfully', jobMessage, 'error');
                                        break;
                                }
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
                uiHelper.notify('info', 'Run on publish executed successfully', 'info');
            },
            error: function (data) {
                console.log("publish  response: " + JSON.stringify(data));
                uiHelper.alert('Run on publish wasn\'t executed successfully', data.responseJSON.message, 'error');
            }
        });
    }
    
    function isFolder(selections) {
        return selections[0].items._container.innerHTML.includes("folder");
    }

    function isScriptInvalidOrNonExecutable(selections) {
        return selections[0].items._container.innerHTML.includes("close");
    }

    const COMMAND_URL = Granite.HTTP.externalize("/bin/wcmcommand");
    const deleteText = "Delete";
    const cancelText = "Cancel";

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "dashboard.delete",
        handler: function(name, el, config, collection, selections) {
            const message = createEl("div");

            const intro = createEl("p").appendTo(message);
            if (selections.length === 1) {
                intro.text(Granite.I18n.get("You are going to delete the following " + (isFolder(selections) ? "folder:" : "script:")));
            } else {
                intro.text(Granite.I18n.get("You are going to delete the following {0} items:", selections.length));
            }

            const list = [];
            const maxCount = Math.min(selections.length, 12);
            for (let i = 0, ln = maxCount; i < ln; i++) {
                const title = selections[i].attributes["data-title"].value;
                list.push(createEl("b").text(title).prop("outerHTML"));
            }
            if (selections.length > maxCount) {
                list.push("&#8230;");
            }

            createEl("p").html(list.join("<br>")).appendTo(message);

            uiHelper.prompt(deleteText, message.html(), "notice", [{
                text: cancelText
            }, {
                text: deleteText,
                warning: true,
                handler: function() {
                    const paths = selections.map(function(v) {
                        return $(v).data("foundationCollectionItemId");
                    });

                    deletePages($(collection), paths, false, true);
                }
            }]);
        }
    });

    function createEl(name) {
        return $(document.createElement(name));
    }

    function deletePages(collection, paths, force, checkChildren) {
        uiHelper.wait();

        $.ajax({
            url: COMMAND_URL,
            type: "POST",
            data: {
                _charset_: "UTF-8",
                cmd: "deletePage",
                path: paths,
                force: !!force,
                checkChildren: !!checkChildren
            },
            success: function() {
                uiHelper.clearWait();

                const api = collection.adaptTo("foundation-collection");

                if (api && "reload" in api) {
                    api.reload();
                    return;
                }

                const contentApi = $(".foundation-content").adaptTo("foundation-content");
                if (contentApi) {
                    contentApi.refresh();
                }
            },
            error: function(xhr) {
                uiHelper.clearWait();

                const message = Granite.I18n.getVar($(xhr.responseText).find("#Message").html());

                if (xhr.status === 412) {
                    uiHelper.prompt(deleteText, message, "notice", [{
                        text: cancelText
                    }, {
                        text: Granite.I18n.get("Force Delete"),
                        warning: true,
                        handler: function() {
                            deletePages(collection, paths, true);
                        }
                    }]);
                    return;
                }

                uiHelper.alert(Granite.I18n.get("Error"), message, "error");
            }
        });
    }

})(window, document, Granite.$, Granite);