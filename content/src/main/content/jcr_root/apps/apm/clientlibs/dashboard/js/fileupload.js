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
(function(window, $, Coral) {
  "use strict";

  let dragCounter = 0,
      fileUploader;

  function init() {
    fileUploader = new Coral.FileUpload();
    fileUploader.accept = ".cqsm,.apm";
    fileUploader.action = "/bin/cqsm/fileUpload";
    fileUploader.async = true;
    fileUploader.multiple = true;
    fileUploader.name = "file";
    fileUploader._className = fileUploader._className + " coral-fileupload-dropzone";
    fileUploader
      .on("coral-fileupload:fileadded", null, function(event) {
        let filename = event.detail.item.file.name;
        fileUploader.upload(filename);
      }, true)
      .on("coral-fileupload:load", null, function(event) {
        fileUploader.uploadQueue.forEach(function(item, index) {
          let filename = event.detail.item.file.name;
          if (item.file.name === filename) {
            fileUploader.uploadQueue.splice(index, 1);
          }
        });
        if (fileUploader.uploadQueue.length === 0) {
          _reload();
        }
      }, true);

    $("coral-shell-content")[0]
      .on("drop", null, function(event) {
        dragCounter = 0;
        event.preventDefault();
        _dropZoneDrop();
        fileUploader._onInputChange(event);
      }, false)
      .on("dragenter", null, function(event) {
        event.preventDefault();
        dragCounter++;
        _dropZoneDragEnter();
      }, false)
      .on("dragover", null, function(event) {
        event.preventDefault();
      }, false)
      .on("dragleave", null, function() {
        dragCounter--;
        if (dragCounter === 0) {
          _dropZoneDragLeave();
        }
      }, false);
  }

  function _reload() {
    location.reload();
  }

  function _dropZoneDragEnter() {
    let message = Granite.I18n.get("Drag and drop to upload"),
        dragAndDropMessage = $('<div class=\"drag-drop-message\" style="text-align: center;"><h1 > <span>{</span>' + message + '<span>}</span></h1></div>');
    $('.foundation-collection').overlayMask('show', dragAndDropMessage);
  }

  function _dropZoneDragLeave() {
    $('.foundation-collection').overlayMask('hide');
  }

  function _dropZoneDrop () {
    $('.foundation-collection').overlayMask('hide');
  }

  $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
    name: "dashboard.upload",
    handler: function() {
      fileUploader._showFileDialog();
    }
  });

  $(document).on("foundation-contentloaded", function () {
    init();
  });



})(window, Granite.$, Coral);