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
(function ($) {
  $(document).on('cui-contentloaded', function () {

    function Console($el) {
      this.$el = $el;
      this.$textArea = this.$el.find("#cqsm").eq(0);
      this.$fileName = this.$el.find('#fname').eq(0);
      this.$showReference = this.$el.find('#showReference').eq(0);
      this.$validateButton = this.$el.find('#validateButton').eq(0);
      this.$uploadButton = this.$el.find('#uploadButton').eq(0);
      this.$lastSavedOn = this.$el.find('.lastSavedOn').eq(0);
      this.initialValue = this.$textArea.val();
      //this.editor = this.initEditor();
      this.delegateEvents();
    }

    Console.prototype = {
      isFileNameLocked: function () {
        return this.$fileName.is('[readonly="readonly"]');
      },
      changeFileName: function (name) {
        this.$fileName.val(name);
        this.$fileName.attr('readonly','readonly');
      },
      hasChanged: function () {
        return this.initialValue !== this.$textArea.val();
      },
      getFileName: function () {
        return this.$fileName.val() + ".cqsm";
      },
      getOverwrite: function () {
        return this.isFileNameLocked() ? 'true' : 'false';
      },
      fileUpload: function () {
        var self = this,
            boundary = '-----------------------------' +
                Math.floor(Math.random() * Math.pow(10, 8)),
            value = this.$textArea.val(),
            params = {
              file: {
                type: 'text/plain',
                filename: this.getFileName(),
                content: value
              }
            },
            content = [],
            file,
            mimeHeader;

        for (file in params) {
          if (params.hasOwnProperty(file)) {
            mimeHeader = 'Content-Disposition: form-data; name="' + file + '"; ';
            content.push('--' + boundary);

            if (params[file].filename) {
              mimeHeader += 'filename="' + params[file].filename + '";';
            }
            content.push(mimeHeader);

            if (params[file].type) {
              content.push('Content-Type: ' + params[file].type);
            }

            content.push('');
            content.push(params[file].content || params[file]);
            content.push('--' + boundary);
          }
        }

        $.ajax({
          type: "POST",
          async: false,
          url: "/bin/cqsm/fileUpload?overwrite=" + this.getOverwrite(),
          dataType: "json",
          processData: false,
          contentType: 'multipart/form-data; boundary=' + boundary,
          data: content.join('\r\n'),
          success: function (data) {
            if (data.length > 0 && typeof data[0].name !== 'undefined') {
              if (!self.isFileNameLocked()) {
                self.changeFileName(data[0].name);
              }
              self.initialValue = value;
              self.$lastSavedOn.text('Last saved on: ' + new Date().toLocaleString());
              helper.refreshParentWindow();
            } else {
              alert('Error while saving: ' + self.getFileName());
            }
          }
        });
      },
      // initEditor: function () {
      //   var editor = null;
      //
      //   ace.config.set("basePath", "/etc/designs/cqsm/clientlibs/js/ace");
      //   this.$textArea.hide();
      //   editor = ace.edit("ace");
      //
      //   editor.setTheme("ace/theme/chrome");
      //   editor.getSession().setMode("ace/mode/cqsm");
      //   editor.getSession().setValue(this.initialValue);
      //   ace.require(["ace/token_tooltip"], function (o) {
      //     editor.tokenTooltip = new o.TokenTooltip(editor);
      //   });
      //
      //   ace.require(["ace/ext/language_tools"], function () {
      //     editor.setOptions({
      //       enableBasicAutocompletion: true,
      //       enableSnippets: true,
      //       enableLiveAutocompletion: true
      //     });
      //   });
      //
      //   return editor;
      // },
      delegateEvents: function () {
        var self = this;

        // this.editor.getSession().on('change', function () {
        //   self.$textArea.val(self.editor.getSession().getValue());
        // });

        this.$showReference.click(function () {
          var win = helper.openWindow("/etc/cqsm/pages/reference.html", "Reference", 760, 0, 800);
          win.focus();
        });

        this.$validateButton.click(function () {
          $.ajax({
            type: "POST",
            async: false,
            url: "/bin/cqsm/validate",
            data: {
              content: self.$textArea.val()
            },
            success: function (response) {
              var text = response.message;
              if (response.error) {
                text += "\n" + response.error;
              }

              alert(text);
              helper.refreshParentWindow();
            }
          });
        });

        this.$uploadButton.click(function () {
          self.fileUpload();
        });

        $(document).ready(function () {
          $(document).keydown(function (e) {
            var S_CHARACTER_CODE = 83;
            if (e.ctrlKey && e.keyCode === S_CHARACTER_CODE) {
              e.stopPropagation();
              e.preventDefault();
              self.fileUpload();
            }
          });

          $(window).on('beforeunload', function () {
            if (self.hasChanged()) {
              return "You have unsaved changes";
            }
          });
        });
      }
    };

    const console = new Console($('body'));
  });
})(jQuery);
