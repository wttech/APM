/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2014 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
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

        if (path === (dest + "/" + rename)) {
            location.href = "/apm/scripts.html" + parent;
            return;
        }

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
