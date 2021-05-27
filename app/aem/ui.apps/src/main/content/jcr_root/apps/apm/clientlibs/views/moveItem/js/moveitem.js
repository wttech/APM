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
    var MODAL_HTML =
        "<div class=\"coral-Modal-header\">"
		+ "<h2 class=\"coral-Modal-title coral-Heading coral-Heading--2\"></h2>"
		+ "<i class=\"coral-Modal-typeIcon coral-Icon coral-Icon--sizeS\"></i>"
		+ "<button type=\"button\" "
		+ "class=\"coral-MinimalButton coral-Modal-closeButton\" "
		+ "data-dismiss=\"modal\">"
		+ "<i class=\"coral-Icon coral-Icon--sizeXS coral-Icon--close "
		+ "coral-MinimalButton-icon\"></i>" + "</button>" + "</div>"
		+ "<div class=\"coral-Modal-body legacy-margins\"></div>"
		+ "<div class=\"coral-Modal-footer\"></div>";

    $(document).on("foundation-contentloaded", function () {
        var wizard = $("form#cq-apmadmin-item-move-form");
        wizard.on("submit", function (e) {
            e.preventDefault();
            submit(wizard);
        });
    });

    function submit(wizard) {
        var foundationContentPath = $(".foundation-content-path").data("foundationContentPath");
        var successMessage = "";
        var errorMessage = "";
        var path = $("input[name='path']").val();
        var redirect = "/apm/scripts.html" + path.substring(0, path.lastIndexOf('/'));

        var HTML5enabled = window.FormData !== undefined ? true: false;
        var data;
        data = wizard.serialize();        
        var processData = true;
        var contentType = wizard.prop("enctype");        
        successMessage = Granite.I18n.get("Item Moved Successfully");
        errorMessage = Granite.I18n.get("Failed to move item");
        $.ajax({
            type: wizard.prop("method"),
            url: "/bin/apm/scripts/move",
            data: data,
            processData: processData,
            contentType: contentType
        }).done(function (html) {
            if ($("#itemmove-success").length === 0) {
                var insertModal = $("<div>", {"class": "coral-Modal", "id": "itemmove-success", "style": "width:30rem"}).hide().html(MODAL_HTML);
                $(document.body).append(insertModal);
        	}
            var successModal = new CUI.Modal({
                element: '#itemmove-success',
                heading: Granite.I18n.get('Success'),
                type: 'success',
                content: successMessage,
                buttons: [{
                        label: Granite.I18n.get('OK'),
                        className: 'primary',
                        click: function (evt) {
                            this.hide();
                            location.href = redirect;
                        }
                    }
                ]
            });

        }).fail(function (xhr, error, errorThrown) {
        	if ($("#itemmove-failure").length === 0) {
                var insertModal = $("<div>", {"class": "coral-Modal", "id": "itemmove-failure", "style": "width:30rem"}).hide().html(MODAL_HTML);
                $(document.body).append(insertModal);
        	}
            var failureModal = new CUI.Modal({
                element: '#itemmove-failure',
                heading: Granite.I18n.get('Error'),
                type: 'error',
                content: errorMessage,
                buttons: [{
                        label: Granite.I18n.get('Close'),
                        className: 'primary',
                        click: function (evt) {
                            this.hide();
                            location.href = redirect;
                        }
                    }
                ]
            });
        });
    }

})(window, document, Granite, Granite.$);
