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

  const RunStatus = {
    ERROR: 'ERROR',
    WARNING: 'WARNING',
    SUCCESS: 'SUCCESS',
  };

  const RowStatus = {
    NEW: 'new',
    RUNNING: 'running',
    FINISHED: 'finished',
  };

  const Mode = {
    DRY_RUN: 'DRY_RUN',
    RUN: 'RUN',
  };

  const Notifier = $(window).adaptTo('foundation-ui');

  var RowProcessor = function () {
    this.rows = [];
  }

  RowProcessor.prototype = {
    addRow: function (row) {
      function withScriptPath(item) {
        return item.scriptPath === row.scriptPath;
      }

      if (this.rows.filter(withScriptPath).length === 0) {
        this.rows.push(row);
        return true;
      } else {
        Notifier.notify('warning', 'Script is already running', 'warning');
        return false;
      }
    },

    removeFinishedRows: function () {
      this.rows = this.rows.filter(function (row) {
        return row.status !== RowStatus.FINISHED;
      });
    },

    updateRows: function () {
      const self = this;
      this.rows.forEach(function(row) {
        self.updateRow(row);
      });
      this.removeFinishedRows();
    },

    updateRow: function (row) {
      if (row.status === RowStatus.NEW) {
        this.runScript(row);
      } else if(row.status === RowStatus.RUNNING) {
        this.checkStatus(row);
      }
      return this.status;
    },

    runScript: function (row) {
      row.status = RowStatus.RUNNING;
      row.showWait();
      $.ajax({
        type: 'POST',
        url: '/bin/cqsm/run-background?file=' + row.scriptPath + '&mode=' + row.mode,
        dataType: 'json'
      })
      .done(function (data) {
        row.job = {
          id: data.id,
          message: data.message,
        };
      });
    },

    checkStatus: function (row) {
      $.ajax({
        type: 'GET',
        url: '/bin/cqsm/run-background?id=' + row.job.id,
        dataType: 'json'
      })
      .done(function (data) {
        if (data.type === 'finished') {
          row.status = RowStatus.FINISHED;
          const runStatus = getRunStatus(data);
          showMessageOnFinished(row.mode, runStatus);
          row.showRunStatus(runStatus !== RunStatus.ERROR, data.path);
        } else if (data.type === 'unknown') {
          row.status = RowStatus.FINISHED;
          showMessageOnUnknown(row.mode, row.job.message);
          row.showRunStatus(false, '');
        }
      });
    },
  }

  var Row = function(mode, element) {
    this.scriptPath = element.attributes['data-path'].value;
    this.mode = mode;
    this.type = mode === Mode.RUN ? 'runOnAuthor' : 'dryRun';
    this.status = RowStatus.NEW;
    this.$element = $(element);
  }

  Row.prototype = {
    showWait: function() {
      this.$element.find('[data-run-type="' + this.type + '"]')
        .html('<coral-wait/>');
    },

    showRunStatus: function(success, path) {
      let icon = success ? 'check' : 'close';
      let href = path && path.length && path.length > 0 ? '/apm/summary.html' + path : '/apm/history.html';
      this.$element.find('[data-run-type="' + this.type + '"]')
        .html('<a data-sly-test="${run.time}" '
              + 'is="coral-anchorbutton" '
              + 'iconsize="S" '
              + 'icon="' + icon + '"'
              + 'href="' + href + '"></a>'
            + '<time>1 second ago</time>');
    }
  }

  const rowProcessor = new RowProcessor();
  const rowsUpdater = setInterval(function() {rowProcessor.updateRows()}, 1000);

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.activecondition', {
        name: 'is-not-folder',
        handler: function (name, el, config, collection, selections) {
          return selections.filter(isFolder).length === 0;
        }
      });

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.activecondition', {
        name: 'is-available',
        handler: function (name, el, config, collection, selections) {
          if (selections.filter(isFolder).length > 0) {
            return false;
          }
          if (selections.filter(isScriptInvalidOrNonExecutable).length > 0) {
            return false;
          }
          return true;
        }
      });

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.action', {
        name: 'scripts.dryrun',
        handler: function (name, el, config, collection, selections) {
          selections.forEach(function (selection) {
            rowProcessor.addRow(new Row(Mode.DRY_RUN, selection));
          });
        }
      });

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.action', {
        name: 'scripts.runonauthor',
        handler: function (name, el, config, collection, selections) {
          selections.forEach(function (selection) {
            rowProcessor.addRow(new Row(Mode.RUN, selection));
          });
        }
      });

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.action', {
        name: 'scripts.runonpublish',
        handler: function (name, el, config, collection, selections) {
          const selected = selections[0].attributes['data-path'].value;
          runOnPublish(selected);
        }
      });

  function runOnPublish(fileName) {
    $.ajax({
      type: 'GET',
      url: '/bin/cqsm/replicate?run=publish&fileName=' + fileName,
      dataType: 'json'
    })
    .done(function (data) {
      console.log('publish response: ' + JSON.stringify(data));
      Notifier.notify('info', 'Run on publish executed successfully', 'info');
    })
    .fail(function (data) {
      console.log('publish  response: ' + JSON.stringify(data));
      Notifier.notify('error', 'Run on publish wasn\'t executed successfully: '
          + data.responseJSON.message, 'error');
    });
  }

  function showMessageOnFinished(mode, status) {
    let title;

    switch (mode) {
      case Mode.DRY_RUN:
        title = 'Dry Run';
        break;
      case Mode.RUN:
        title = 'Run on author';
        break;
    }

    switch (status) {
      case RunStatus.ERROR:
        Notifier.notify('error', title + ' executed with errors', 'error');
        break;
      case RunStatus.WARNING:
        Notifier.notify('warning', title + ' executed with warnings', 'notice');
        break;
      case RunStatus.SUCCESS:
        Notifier.notify('success', title + ' executed successfully', 'success');
        break;
    }
  }

  function showMessageOnUnknown(mode, jobMessage) {
    switch (mode) {
      case Mode.DRY_RUN:
        Notifier.notify('error', 'Dry Run wasn\'t executed successfully: ' + jobMessage, 'error');
        break;
      case Mode.RUN:
        Notifier.notify('error', 'Run on author wasn\'t executed successfully: ' + jobMessage, 'error');
        break;
    }
  }

  function getRunStatus(data) {
    function toRunStatus(entry) {
      return entry.status;
    }

    let statuses = new Set(data.entries.map(toRunStatus)),
        result = RunStatus.SUCCESS;
    if (statuses.has(RunStatus.ERROR)) {
      result = RunStatus.ERROR;
    } else if (statuses.has(RunStatus.WARNING)) {
      result = RunStatus.WARNING;
    }
    return result;
  }

  function isFolder(selection) {
    return selection.items._container.innerHTML.includes('folder');
  }

  function isScriptInvalidOrNonExecutable(selection) {
    return selection.items._container.innerHTML.includes('script-is-invalid');
  }

})(window, jQuery);
