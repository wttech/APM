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
(function (window, document, $) {
  function deletePages(collection, paths) {
    const ui = $(window).adaptTo('foundation-ui');
    ui.wait();
    $.ajax({
      url: '/bin/apm/scripts/delete',
      type: 'POST',
      data: {
        paths: paths
      },
      dataType: 'json',
      success: function () {
        ui.clearWait();
        const api = collection.adaptTo('foundation-collection');
        if (api && 'reload' in api) {
          api.reload();
          return
        }
        const contentApi = $('.foundation-content').adaptTo('foundation-content');
        if (contentApi) {
          contentApi.refresh();
        }
      },
      error: function () {
        ui.clearWait();
        ui.alert('Error', 'Errors while deleting item(s)', 'error');
      }
    });
  }

  function createEl(name) {
    return $(document.createElement(name));
  }

  $(window).adaptTo('foundation-registry').register('foundation.collection.action.action', {
    name: 'com.cognifide.apm.delete',
    handler: function (name, el, config, collection, selections) {
      const message = createEl('div');
      const intro = createEl('p').appendTo(message);
      if (selections.length === 1) {
        intro.text('You are going to delete the following item:');
      } else {
        intro.text('You are going to delete the following ' + selections.length + ' items:');
      }
      let list = [];
      const maxCount = Math.min(selections.length, 12);
      for (let i = 0; i < maxCount; i++) {
        const title = $(selections[i]).find('.foundation-collection-item-title').text();
        const time = $(selections[i]).find('.foundation-collection-item-time').text();
        list.push(createEl('b').text(time ? title + ' (' + time.trim() + ')' : title).prop('outerHTML'))
      }
      if (selections.length > maxCount) {
        list.push('\x26#8230;');
      }
      createEl('p').html(list.join('\x3cbr\x3e')).appendTo(message);
      const ui = $(window).adaptTo('foundation-ui');
      ui.prompt('Delete', message.html(), 'notice', [{
        text: 'Cancel'
      }, {
        text: 'Delete',
        warning: true,
        handler: function () {
          const paths = selections.map(function (value) {
            return $(value).data('foundationCollectionItemId');
          });
          deletePages($(collection), paths);
        }
      }]);
    }
  });
})(window, document, Granite.$);
