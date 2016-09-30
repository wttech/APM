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
Cog.component.cqsmView = (function ($) {

    var api = {};

    function handleData(data) {
        $("#data").append(data);
    }

    function getFilePath() {
        return $("#filePath").text();
    };

    function getFileName() {
        return $("#fileName").text();
    };

    api.init = function ($elements) {

        $elements.each(function(){

                var filePath = getFilePath();
                var fileName = getFileName();
                $.ajax({
                    type: "GET",
                    data: { "mode": "view", "filename": fileName, "filepath": filePath},
                    dataType: 'html',
                    url: "/bin/cqsm/fileDownload",
                    success: handleData
                });
            });

    };

    return api;
}(COGjQuery));

Cog.register({
    name: 'cqsmView',
    api: Cog.component.cqsmView,
    selector: '#cqsmViewPage'
});