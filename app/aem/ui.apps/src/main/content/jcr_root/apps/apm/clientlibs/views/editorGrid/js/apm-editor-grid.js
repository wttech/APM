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
(function (window, $, Coral) {

  const EditorModes = {
    viewMode: 'apm-editor-view-mode',
    editMode: 'apm-editor-edit-mode',
  };

  const FieldNames = {
    executionEnabled: 'apm:executionEnabled',
    executionMode: 'apm:executionMode',
    executionEnvironment: 'apm:executionEnvironment',
    executionHook: 'apm:executionHook',
    executionSchedule: 'apm:executionSchedule',
  };

  const ExecutionModes = {
    onInstall: 'ON_INSTALL',
    onInstallModified: 'ON_INSTALL_MODIFIED',
    onSchedule: 'ON_SCHEDULE',
  };

  class EditorGrid {
    constructor($element, fields) {
      this.$element = $element;
      this.editorMode = this.$element.hasClass(EditorModes.viewMode) ? EditorModes.viewMode : EditorModes.editMode;
      this.fields = {
        executionEnabled: fields[FieldNames.executionEnabled],
        executionMode: fields[FieldNames.executionMode],
        executionEnvironment: fields[FieldNames.executionEnvironment],
        executionHook: fields[FieldNames.executionHook],
        executionSchedule: fields[FieldNames.executionSchedule],
      };

      this.init();
    }

    init() {
      if (this.editorMode === EditorModes.editMode) {
        this.fields.executionEnabled.on('foundation-field-change', this.updateFields.bind(this));
        this.fields.executionMode.on('foundation-field-change', this.updateFields.bind(this));
      } else {
        this.disableFields(this.fields, true);
      }
      // timeout is needed because field.getValue() doesn't return any value at start
      const timeout = setTimeout(this.updateFields.bind(this), 500);
    }

    disableFields(fields, disabled) {
      $.each(fields, (index, field) => field.setDisabled(disabled));
    }

    showFields(fields) {
      const showFields = $.map(fields, (value) => value);
      $.each(this.fields, (index, field) => {
        if (showFields.includes(field)) {
          field.show();
          field.setDisabled(this.editorMode === EditorModes.editMode ? false : true);
        } else {
          field.hide();
          field.setDisabled(true);
        }
      });
    }

    updateFields() {
      const executionEnabled = this.fields.executionEnabled.getValue();
      if (executionEnabled === 'false') {
        this.showFields([this.fields.executionEnabled]);
      } else {
        const executionMode = this.fields.executionMode.getValue();
        if (executionMode === ExecutionModes.onInstall || executionMode === ExecutionModes.onInstallModified) {
          this.showFields([this.fields.executionEnabled, this.fields.executionMode, this.fields.executionEnvironment,
            this.fields.executionHook])
        } else if (executionMode === ExecutionModes.onSchedule) {
          this.showFields([this.fields.executionEnabled, this.fields.executionMode, this.fields.executionSchedule]);
        } else {
          this.showFields([this.fields.executionEnabled, this.fields.executionMode]);
        }
      }
    }
  }

  class Field {
    constructor($element, name) {
      this.$element = $element;
      this.name = name;
      this.field = $element.adaptTo('foundation-field');
    }

    on(event, callback) {
      this.$element.on(event, callback);
    }

    setDisabled(disabled) {
      this.field.setDisabled(disabled);
    }

    hide() {
      this.$element.parent('.coral-Form-fieldwrapper').hide();
    }

    show() {
      this.$element.parent('.coral-Form-fieldwrapper').show();
    }

    getValue() {
      return this.field.getValue();
    }

    static getName($element) {
      const field = $element.adaptTo('foundation-field');
      return field.getName();
    }

    static loadFields($fields, fieldNames, callback) {
      const names = $.map(fieldNames, (value) => value);
      const fields = {};
      $fields.each((index, element) => {
        const $element = $(element);
        if ($element.hasClass('coral-Form-field')) {
          Coral.commons.ready(element, () => {
            const field = Field.toField($element);
            if (field != null && names.includes(field.name)) {
              fields[field.name] = field;
              if (Object.keys(fields).length === names.length) {
                callback(fields)
              }
            }
          });
        }
      });
      return fields;
    }

    static toField($element) {
      if ($element.hasClass('coral-Form-field')) {
        const name = Field.getName($element);
        if (name !== undefined && name !== '') {
          return new Field($element, name)
        }
      }
      return null;
    }
  }

  const editorGrids = [];

  $(document).on('foundation-contentloaded', (event) => {
    $('.apm-editor-grid', event.target).each((index, element) => {
      const $element = $(element);
      const $fields = $element.find(':-foundation-submittable');
      Field.loadFields($fields, FieldNames, (fields) => editorGrids.push(new EditorGrid($element, fields)));
    });
  });
})(window, jQuery, Coral);
