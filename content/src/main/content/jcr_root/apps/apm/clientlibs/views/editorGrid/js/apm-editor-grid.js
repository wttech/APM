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

  const EditorModes = {
    viewMode: 'apm-editor-view-editorMode',
    editMode: 'apm-editor-edit-editorMode',
  };

  const Fields = {
    executionMode: 'cqsm:executionMode',
    executionEnvironment: 'cqsm:executionEnvironment',
    executionHook: 'cqsm:executionHook',
  };

  const ExecutionModes = {
    onHook: 'ON_HOOK',
  };

  class EditorGrid {
    constructor($element) {
      this.$element = $element;
      this.$fields = EditorGrid.toObjectWithJQueryFields($element.find(':-foundation-submittable'));
      this.fields = EditorGrid.toObjectWithCoralFields($element.find(':-foundation-submittable'));
      this.editorMode = this.$element.hasClass(EditorModes.viewMode) ? EditorModes.viewMode : EditorModes.editMode;

      this.init();
    }

    init() {
      if (this.editorMode === EditorModes.viewMode) {
        this.disableFields(this.fields, true);
      }
      this.$fields[Fields.executionMode].on('foundation-field-change', this.updateFields.bind(this));
    }

    disableFields(fieldNames, disabled) {
      fieldNames.forEach((fieldName) => this.fields[fieldName].setDisabled(disabled));
    }

    updateFields() {
      this.disableFields([Fields.executionEnvironment, Fields.executionHook], true);
      const executionMode = this.fields[Fields.executionMode].getValue();
      if (executionMode === ExecutionModes.onHook) {
        this.disableFields([Fields.executionEnvironment, Fields.executionHook], false)
      }
    }

    static toObjectWithCoralFields($fields) {
      const fields = {};
      $fields.each((index, element) => {
        const $element = $(element);
        if ($element.hasClass('coral-Form-field')) {
          const field = $element.adaptTo('foundation-field');
          let name = field.getName();
          if (!name) {
            name = $element.find('input').attr('name') || '';
            name = name.replace('@Delete', '');
          }
          if (name !== '') {
            fields[name] = field;
          }
        }
      });
      return fields;
    }

    static toObjectWithJQueryFields($fields) {
      const fields = {};
      $fields.each((index, element) => {
        const $element = $(element);
        if ($element.hasClass('coral-Form-field')) {
          const field = $element.adaptTo('foundation-field');
          let name = field.getName();
          if (!name) {
            name = $element.find('input').attr('name') || '';
            name = name.replace('@Delete', '');
          }
          if (name !== '') {
            fields[name] = $element;
          }
        }
      });
      return fields;
    }
  }

  $(document).ready(() => {
    const editorGrids = [];
    $('.apm-editor-grid').each(() => editorGrids.push(new EditorGrid($('.apm-editor-grid'))));
  });
})(window, jQuery);
