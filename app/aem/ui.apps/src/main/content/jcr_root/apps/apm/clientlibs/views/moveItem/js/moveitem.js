/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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
(function (window, document, Granite, $) {
    "use strict";

    $(document).on("foundation-contentloaded", function () {
        var wizard = $("form#cq-apmadmin-item-move-form");
        wizard.on("submit", function (e) {
            e.preventDefault();
            submit(wizard);
        });
    });

    function submit(wizard) {
        var uiHelper = $(window).adaptTo("foundation-ui");
        var path = $("input[name='path']").val();
        var parent = path.substring(0, path.lastIndexOf("/"));
        var dest = $("input[name='dest']").val();
        if (typeof dest === "undefined" || dest === "") {
            dest = parent;
        }
        var rename = $("input[name='rename']").val();
        var data = wizard.serialize();
        var processData = true;
        var contentType = wizard.prop("enctype");

        $.ajax({
            type: wizard.prop("method"),
            url: "/bin/apm/scripts/move",
            data: data,
            processData: processData,
            contentType: contentType
        }).done(function (html) {
            var message = "Item moved successfully";
            uiHelper.notify("info",  message, "success");
            location.href = "/apm/scripts.html" + dest;
        }).fail(function (xhr, error, errorThrown) {
            var message = "Failed to move item";
            uiHelper.notify("error", message, "error");
            location.href = "/apm/scripts.html" + parent;
        });
    }

})(window, document, Granite, Granite.$);
