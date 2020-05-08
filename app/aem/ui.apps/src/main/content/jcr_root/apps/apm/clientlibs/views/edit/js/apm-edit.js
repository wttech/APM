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
  $(document).on('cui-contentloaded', function () {

    const SHOW_REFERENCES_URL = '/apm/references.html';

    const fieldNames = [
      'apm:launchEnabled',
      'apm:launchMode',
      'apm:launchEnvironment',
      'apm:launchHook',
      'apm:launchSchedule',
    ];

    function Console($el) {
      this.uiHelper = $(window).adaptTo('foundation-ui');
      this.$el = $el;
      this.$form = $el.find('#script-form');
      this.$launchEnabled = this.$form.find('coral-checkbox[name="apm:launchEnabled"]');
      this.$textArea = this.$el.find('#cqsm').eq(0);
      this.$fileName = this.$el.find('#fname').eq(0);
      this.$showReference = this.$el.find('#showReference').eq(0);
      this.$validateButton = this.$el.find('#validateButton').eq(0);
      this.$saveButton = this.$el.find('#saveButton').eq(0);
      this.$saveAndCloseButton = this.$el.find('#saveAndCloseButton').eq(0);
      this.$cancelButton = this.$el.find('#cancelButton').eq(0);
      this.$logger = this.$el.find('.apm-console-logger');
      this.initialValue = this.$textArea.val();
      this.savePath = this.$el.find('#script-form').attr('action');
      this.editor = this.initEditor();
      this.delegateEvents();
    }

    Console.prototype = {
      isFileNameLocked: function () {
        return this.$fileName.is(':disabled');
      },
      changeFileName: function (name) {
        this.$fileName.val(name);
        this.$fileName.attr('disabled', 'disabled');
      },
      hasChanged: function () {
        return this.initialValue !== this.$textArea.val();
      },
      getFileName: function () {
        return this.$fileName.val() + '.apm';
      },
      getOverwrite: function () {
        return this.isFileNameLocked() ? 'true' : 'false';
      },
      getLaunchEnabled: function () {
        return !this.$launchEnabled.prop('checked');
      },
      fileUpload: function () {
        const self = this;
        const value = this.$textArea.val();

        let fileName = this.getFileName();
        if (this.savePath.endsWith('/' + fileName)) {
          fileName = this.savePath;
        } else {
          fileName = this.savePath + '/' + fileName;
        }

        const originalFormData = new FormData(this.$form.get(0));

        const formData = new FormData();
        $.each(fieldNames, (index, fieldName) => {
          const originalValue = originalFormData.get(fieldName);
          if (originalValue) {
            formData.set(fieldName, originalValue);
          }
        });
        formData.set('apm:launchEnabled', this.getLaunchEnabled());
        formData.set('overwrite', this.getOverwrite());
        formData.set('file', new Blob([value], {type: 'text/plain'}), fileName);

        $.ajax({
          type: 'POST',
          async: false,
          url: '/bin/apm/script/upload',
          dataType: 'json',
          processData: false,
          contentType: false,
          data: formData,
          success: function (data) {
            const script = data.uploadedScript;
            if (!self.isFileNameLocked()) {
              self.changeFileName(script.name);
            }
            self.initialValue = value;
            self.createMode = false;
            self.showInfo(data);
          },
          error: function (response) {
            self.showError(response.responseJSON);
          }
        });
      },

      initEditor: function () {
        let editor = null;

        ace.config.set('basePath', '/apps/apm/clientlibs/externals/ace/js');
        this.$textArea.hide();
        editor = ace.edit('ace');

        editor.setTheme('ace/theme/chrome');
        editor.getSession().setMode('ace/mode/cqsm');
        editor.getSession().setValue(this.initialValue);
        ace.require(['ace/token_tooltip'], function (o) {
          editor.tokenTooltip = new o.TokenTooltip(editor);
        });

        ace.require(['ace/ext/language_tools'], function () {
          editor.setOptions({
            enableBasicAutocompletion: true,
            enableSnippets: true,
            enableLiveAutocompletion: true
          });
        });

        return editor;
      },
      delegateEvents: function () {
        const self = this;

        this.editor.getSession().on('change', function () {
          self.$textArea.val(self.editor.getSession().getValue());
        });

        this.$showReference.click(function () {
          window.open(SHOW_REFERENCES_URL, '_blank');
        });

        this.$cancelButton.click(function () {
          window.location.href = document.referrer;
        });

        this.showInfo = function (response) {
          this.$validateButton.blur();

          self.uiHelper.notify('info', response.message, 'success');
          self.log('info', response.message);
        };

        this.showError = function (response) {
          this.$validateButton.blur();

          let message = response.message;
          if (response.errors) {
            message += '<ul>';
            response.errors.forEach((error) => message += '<li>' + error);
            message += '</ul>';
          }
          self.uiHelper.notify('error', message, 'error');
          self.log('error', message);
        };

        this.log = function (type, message) {
          this.$logger.prepend(`<div class="apm-console-log apm-console-log-${type}">${message}</div>`);
          if (this.$logger.hasClass('hidden')) {
            this.$logger.removeClass('hidden');
          }
        };

        this.$validateButton.click(function () {
          $.ajax({
            type: 'POST',
            async: false,
            url: '/bin/apm/script/validate',
            data: {
              path: self.savePath,
              content: self.$textArea.val()
            },
            success: function (response) {
              if (response.valid) {
                self.showInfo(response);
              } else {
                self.showError(response);
              }
            },
            error: function (response) {
              self.showError(response.responseJSON);
            }
          });
        });

        this.$saveButton.click(function () {
          self.fileUpload();
        });

        this.$saveAndCloseButton.click(function () {
          self.fileUpload();
          window.location.href = document.referrer;
        });

        $(document).ready(function () {
          $(document).keydown(function (e) {
            const S_CHARACTER_CODE = 83;
            if (e.ctrlKey && e.keyCode === S_CHARACTER_CODE) {
              e.stopPropagation();
              e.preventDefault();
              self.fileUpload();
            }
          });

          $(window).on('beforeunload', function () {
            if (self.hasChanged()) {
              return 'You have unsaved changes';
            }
          });
        });
      }
    };
    new Console($('body'));
  });
})(window, jQuery);
