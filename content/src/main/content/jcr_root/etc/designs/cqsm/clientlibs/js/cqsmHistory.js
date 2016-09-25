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
Cog.component.cqsmHistory = (function ($) {

	var api = {};
	
	var helper = Cog.component.cqsmHelper;
	
	function getExecutionSummaryPath(jObject) {
		return $(jObject).attr("path");
	};
	
	function getSummary(path) {
		$.ajax({
			type: "GET",
			url:"/etc/cqsm/history/jcr:content/cqsmHistory.details.html" + path,
			dataType: "html",
			success: function(data) {
				$("#import-summary").empty().append(data);
			}
		});
	};
	
	api.init = function ($elements) {
		$elements.each(function(){
			helper.setMaxRows(12);
			helper.processList();

			$(document).ajaxStart(function() {
				$(".status-message").each(function(){$(this).hide().removeClass().addClass("status-message").empty()});
			});
			$(document).ajaxError(function(event, jqxhr, settings, exception) {
				helper.showMessage("Unexpected Error Happend", "error",false);
			});
			
			$(".action-show-summary").click(function() {
				var path = getExecutionSummaryPath(this);
				getSummary(path);
			});

			$(".action-view").click(function() {
				var fileName = helper.getFileName(this);
				var filePath = helper.getFilePath(this);
				var win = helper.openWindow("/etc/cqsm/pages/viewCqsmFile.html?wcmmode=disabled&filename="+ fileName + "&filepath="+filePath,"CQSM file",200,170);		  
				win.focus();
			});

			$(".action-filter-history").change(function() {
				$(this).closest('form').trigger('submit');
			});
		});
	}
	

	return api;
}(COGjQuery));

Cog.register({
	name: 'cqsmHistory',
	api: Cog.component.cqsmHistory,
	selector: '#cqsmHistoryPage'
});

