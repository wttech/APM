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
Cog.component.cqsmProgressHelper = (function($) {

	var api = {},
		initialized = false,
		defaults = {
			moduleName: 'progress'
		},
		listeners = {
			startProgress: 'startProgressListener',
			stopProgress: 'stopProgressListener'
		};

	
	function start(e) {
		var scope = e.eventData.context;
		scope.addClass('meter animate nostripes');
	};
	
	function stop(e) {
		var scope = e.eventData.context;
		scope.removeClass('meter animate nostripes');
		$("#progressBar").empty();
	};

	api.init = function($elements) {
		if(!initialized){
			initialized = true;
			Cog.addListener(defaults.moduleName, listeners.startProgress, start);
			Cog.addListener(defaults.moduleName, listeners.stopProgress, stop);
		}
	};

	return api;

}(COGjQuery));

Cog.register({
	name : 'cqsmProgressHelper',
	api : Cog.component.cqsmProgressHelper
});