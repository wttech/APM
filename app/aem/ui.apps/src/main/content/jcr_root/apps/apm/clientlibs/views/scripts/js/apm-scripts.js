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

  const RunStatus = {
    ERROR: 'ERROR',
    WARNING: 'WARNING',
    SUCCESS: 'SUCCESS',
  };

  const ScriptStatus = {
    NEW: 'NEW',
    RUNNING: 'RUNNING',
    FINISHED: 'FINISHED',
  };

  const Mode = {
    DRY_RUN: 'DRY_RUN',
    RUN: 'RUN',
  };

  const Notifier = $(window).adaptTo('foundation-ui');

  var ScriptProcessor = function () {
    this.scripts = [];
  }

  ScriptProcessor.prototype = {
    addScript: function (row) {
      function withScriptPath(item) {
        return item.scriptPath === row.scriptPath;
      }

      if (this.scripts.filter(withScriptPath).length === 0) {
        this.scripts.push(row);
        return true;
      } else {
        Notifier.notify('warning', 'Script is already running', 'warning');
        return false;
      }
    },

    removeFinishedScripts: function () {
      this.scripts = this.scripts.filter(function (row) {
        return row.status !== ScriptStatus.FINISHED;
      });
    },

    updateScripts: function () {
      this.scripts.forEach(function (script) {
        script.updateScript();
      });
      this.removeFinishedScripts();
    },
  }

  var LocalScript = function (mode, element) {
    this.scriptPath = element.attributes['data-path'].value;
    this.mode = mode;
    this.status = ScriptStatus.NEW;
    this.$element = $(element);
    const type = mode === Mode.RUN ? 'runOnAuthor' : 'dryRun';
    this.$cell = this.$element.find('[data-run-type="' + type + '"]')
  }

  LocalScript.prototype = {
    showWait: function () {
      this.$cell.html('<coral-wait/>');
    },

    showRunStatus: function (success, summaryPath) {
      let icon = success ? 'check' : 'close';
      let href = '/bin/cqsm/lastSummary.local' + self.mode + '.html' + self.scriptPath;
      if (summaryPath && summaryPath.length && summaryPath.length > 0) {
        href = '/apm/summary.html' + summaryPath;
      }
      this.$cell.html('<a data-sly-test="${run.time}" '
          + 'is="coral-anchorbutton" '
          + 'iconsize="S" '
          + 'icon="' + icon + '"'
          + 'href="' + href + '"></a>'
          + '<time>1 second ago</time>');
    },

    updateScript: function () {
      if (this.status === ScriptStatus.NEW) {
        this.runScript();
      } else if (this.status === ScriptStatus.RUNNING) {
        this.checkStatus();
      }
    },

    runScript: function () {
      const self = this;
      this.status = ScriptStatus.RUNNING;
      this.showWait();
      $.ajax({
        type: 'POST',
        url: '/bin/apm/scripts/exec',
        data: {
          script: this.scriptPath,
          executionMode: this.mode,
          async: true
        },
        dataType: 'json'
      })
      .done(function (data) {
        self.job = {
          id: data.id,
          message: data.message,
        };
      });
    },

    handleResponse: function (data) {
      if (data.status === 'finished') {
        this.status = ScriptStatus.FINISHED;
        const runStatus = getRunStatus(data);
        showMessageOnFinished(this.mode, runStatus);
        this.showRunStatus(runStatus !== RunStatus.ERROR, data.path);
      } else if (data.type === 'unknown') {
        this.status = ScriptStatus.FINISHED;
        showMessageOnUnknown(this.mode, self.job.message);
        this.showRunStatus(false, '');
      }
    },

    checkStatus: function () {
      const self = this;
      $.ajax({
        type: 'GET',
        url: '/bin/apm/scripts/exec?id=' + this.job.id,
        dataType: 'json'
      })
      .done(function (data) {
        self.handleResponse(data);
      })
      .fail(function(xhr) {
        if (xhr.responseJSON) {
          self.handleResponse(xhr.responseJSON);
        }
      });
    },
  }

  var RemoteScript = function (element) {
    this.scriptPath = element.attributes['data-path'].value;
    this.status = ScriptStatus.NEW;
    this.$element = $(element);
    this.$cell = this.$element.find('[data-run-type="runOnPublish"]');
  }

  RemoteScript.prototype = {
    showWait: function () {
      this.$cell.html('<coral-wait/>');
    },

    showRunStatus: function () {
      this.status = ScriptStatus.FINISHED;
      this.$cell.html('Script started on publish 1 second ago');
    },

    updateScript: function () {
      if (this.status === ScriptStatus.NEW) {
        this.runScript();
      }
    },

    runScript: function () {
      const self = this;
      this.status = ScriptStatus.RUNNING;
      this.showWait();
      $.ajax({
        type: 'GET',
        url: '/bin/cqsm/replicate?run=publish&fileName=' + this.scriptPath,
        dataType: 'json'
      })
      .done(function (data) {
        console.log('publish response: ' + JSON.stringify(data));
        Notifier.notify('info', 'Script was successfully started on publish', 'info');
        self.showRunStatus();
      })
      .fail(function (data) {
        console.log('publish  response: ' + JSON.stringify(data));
        Notifier.notify('error', 'Script wasn\'t started on publish: ' + data.responseJSON.message, 'error');
        self.showRunStatus();
      });
    },
  }

  const scriptProcessor = new ScriptProcessor();
  const scriptUpdater = setInterval(function () {
    scriptProcessor.updateScripts()
  }, 1000);

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
            scriptProcessor.addScript(new LocalScript(Mode.DRY_RUN, selection));
          });
        }
      });

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.action', {
        name: 'scripts.runonauthor',
        handler: function (name, el, config, collection, selections) {
          selections.forEach(function (selection) {
            scriptProcessor.addScript(new LocalScript(Mode.RUN, selection));
          });
        }
      });

  $(window).adaptTo('foundation-registry').register(
      'foundation.collection.action.action', {
        name: 'scripts.runonpublish',
        handler: function (name, el, config, collection, selections) {
          selections.forEach(function (selection) {
            scriptProcessor.addScript(new RemoteScript(selection));
          });
        }
      });

  function showMessageOnFinished(mode, status) {
    let title;

    switch (mode) {
      case Mode.DRY_RUN:
        title = 'Dry Run';
        break;
      case Mode.RUN:
        title = 'Run on Author';
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
        Notifier.notify('error', 'Dry Run finished with status: ' + jobMessage, 'error');
        break;
      case Mode.RUN:
        Notifier.notify('error', 'Run on Author finished with status: ' + jobMessage, 'error');
        break;
    }
  }

  function getRunStatus(data) {
    function toRunStatus(entry) {
      return entry.status;
    }

    let statuses = new Set(data.output.map(toRunStatus)),
        result = RunStatus.SUCCESS;
    if (statuses.has(RunStatus.ERROR)) {
      result = RunStatus.ERROR;
    } else if (statuses.has(RunStatus.WARNING)) {
      result = RunStatus.WARNING;
    }
    return result;
  }

  function isFolder(selection) {
    return selection.items._container.innerHTML.indexOf('folder') > -1;
  }

  function isScriptInvalidOrNonExecutable(selection) {
    const innerHTML = selection.items._container.innerHTML;
    return innerHTML.indexOf('script-is-invalid') > -1 || innerHTML.indexOf('execution-disabled') > -1;
  }

})(window, jQuery);
