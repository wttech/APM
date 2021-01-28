/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 - 2016 Wunderman Thompson Technology
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

  $(window).adaptTo('foundation-registry').register('foundation.collection.action.action', {
    name: 'viewer.back',
    handler: function (name, el, config, collection, selections) {
      let url = window.location.href;
      url = url.substring(url.indexOf('/apm/viewer.html'));
      url = url.replace('/apm/viewer.html', '');
      url = url.substring(0, url.lastIndexOf('/'));
      if (url.startsWith('/var/apm/history')) {
        window.location.href = '/apm/history.html';
      } else {
        window.location.href = '/apm/scripts.html' + url;
      }
    }
  });

  $(document).on('cui-contentloaded', function () {

    function Console($el) {
      this.$el = $el;
      this.$textArea = this.$el.find('#cqsm').eq(0);
      this.$fileName = this.$el.find('#fname').eq(0);
      this.initialValue = this.$textArea.val();
      this.editor = this.initEditor();
    }

    Console.prototype = {
      isFileNameLocked: function () {
        return this.$fileName.is('[readonly="readonly"]');
      },
      changeFileName: function (name) {
        this.$fileName.val(name);
        this.$fileName.attr('readonly', 'readonly');
      },
      getFileName: function () {
        return this.$fileName.val() + '.cqsm';
      },
      getOverwrite: function () {
        return this.isFileNameLocked() ? 'true' : 'false';
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
        editor.setReadOnly(true);

        return editor;
      },
    };
    new Console($('body'));
  });
})(window, jQuery);
