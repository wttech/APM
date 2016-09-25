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
Cog.component.cqsmHelper = (function($) {

	var api = {};

	api.init = function($elements) {

	}

	var defaults = {
		maxValues : 5,
		currentPart : 0,
		total: 0
	};
	
	function displayNav() {
		$('#prev').show();
		$('#next').show();
		
		if (defaults.currentPart == 0) {
			$('#prev').hide();
		}
		
		if (defaults.currentPart + defaults.maxValues >= defaults.total) {
			$('#next').hide();
		}
	}

	api.processList = function() {
		var trs = $('.files-table tbody tr');

		trs.each(function() {
			$(this).hide();
		});
		
		defaults.total = trs.size();

		if (defaults.currentPart === defaults.total) {
			defaults.currentPart = defaults.currentPart - defaults.maxValues;
		}

		for (var i = defaults.currentPart; i < defaults.currentPart + defaults.maxValues; i++) {
			trs.eq(i).show();
		}
		
		displayNav();
		
		$('#prev').click(function(e) {

			for (var i = defaults.currentPart; i < defaults.currentPart + defaults.maxValues; i++) {
				trs.eq(i).hide();
			}
			
			defaults.currentPart = defaults.currentPart - defaults.maxValues;
			
			for (var i = defaults.currentPart; i < defaults.currentPart + defaults.maxValues; i++) {
				trs.eq(i).show();
			}

			displayNav();
			
			e.preventDefault();
		});
		
		$('#next').click(function(e) {
			for (var i = defaults.currentPart; i < defaults.currentPart + defaults.maxValues; i++) {
				trs.eq(i).hide();
			}
			
			defaults.currentPart = defaults.currentPart + defaults.maxValues;

			for (var i = defaults.currentPart; i < defaults.currentPart + defaults.maxValues; i++) {
				trs.eq(i).show();
			}
			
			displayNav();
			
			e.preventDefault();
		});
	};

	api.showMessage = function(message, type, doubleBox) {
		$(".status-message").each(function(){$(this).empty().removeClass().addClass("status-message").addClass(type).append(message).show()});
		if (!doubleBox) {
			$("#second-status-message").hide();
		}
	};

	api.getFileName = function(jObject) {
		return $(jObject).closest('tr').find('td:first-child').children('a').html();
	};

	api.getFilePath = function(jObject) {
		return $(jObject).closest('tr').find('td:first-child').children('a').attr("href");
	};
	
	api.getFileRowId = function(jObject) {
		return $(jObject).closest('tr').attr("id");
	};
	
	api.openWindow = function(url, winName, xOffset, yOffset, width, height) {
		var x = (window.screenX || window.screenLeft || 0) + (xOffset || 0);
		var y = (window.screenY || window.screenTop || 0) + (yOffset || 0);

		if (width === undefined) {
			width = 750;
		}
		if (height === undefined) {
			height = 480;
		}

		return window.open(url, winName, 'top=' +y+ ',left=' +x+ ",menubar=1,width=" + width + ",height="
			+ height + ",scrollbars=1");
	};

	api.refreshParentWindow = function() {
		if (typeof window.opener !== 'undefined') {
			window.opener.location.reload();
		}
	};

	api.setMaxRows = function(rowNumber) {
		defaults.maxValues = rowNumber;
	};

	return api;

}(COGjQuery));

Cog.register({
	name : 'cqsmHelper',
	api : Cog.component.cqsmHelper
});