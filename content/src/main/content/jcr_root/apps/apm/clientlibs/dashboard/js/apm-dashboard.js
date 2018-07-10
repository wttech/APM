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

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "dashboard.view",
        handler: function(name, el, config, collection, selections) {
            var selected = selections[0].attributes['data-path'].value;
            console.info('Implement Me: view CLICK ' + selected);
        }
    });

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "dashboard.dryrun",
        handler: function(name, el, config, collection, selections) {
            var selected = selections[0].attributes['data-path'].value;
            console.info('Implement Me: dryrun CLICK ' + selected);
        }
    });

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "dashboard.runonauthor",
        handler: function(name, el, config, collection, selections) {
            var selected = selections[0].attributes['data-path'].value;
            console.info('Implement Me: runonauthor CLICK ' + selected);
        }
    });

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "dashboard.runonpublish",
        handler: function(name, el, config, collection, selections) {
            var selected = selections[0].attributes['data-path'].value;
            console.info('Implement Me: runonpublish CLICK ' + selected);
        }
    });

})(window, jQuery);
