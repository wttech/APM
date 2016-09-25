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
Cog.component.cqsmLoader = (function($) {

	var api = {},
		initialized = false,
		defaults = {
			moduleName : 'loader'
		}, listeners = {
			hideLoader : 'hideLoaderListener',
			showLoader : 'showLoaderListener'
		};

	function hide(e) {
		var scope = e.eventData.context,
			animation = e.eventData.animation,
			docWidth = $(document).width(),
			docHeight = $(document).height();

		scope.append("<div id='overlay'></div>");
		scope.addClass("grey-out");
		$("#overlay").height(docHeight).width(docWidth).css({
			'position' : 'absolute',
			'top' : '0',
			'left' : '0'
		});
		if(animation){
			$("#overlay").addClass('loader');
		}
	}

	function show(e) {
		var scope = e.eventData.context; 

		$("#overlay").remove();
		scope.removeClass('grey-out');
		
	}

	api.init = function($elements) {
		if (!initialized) {
			initialized = true;
			Cog.addListener(defaults.moduleName, listeners.showLoader, show);
			Cog.addListener(defaults.moduleName, listeners.hideLoader, hide);
		}
	};

	return api;

}(COGjQuery));

Cog.register({
	name : 'cqsmLoader',
	api : Cog.component.cqsmLoader
});