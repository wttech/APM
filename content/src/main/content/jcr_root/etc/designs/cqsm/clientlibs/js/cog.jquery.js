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
/***
 * This code hides jQuery (removes jQuery and $ variables) and sets it into COGjQuery variable.
 * It have to be executed at the end of the js-framework.
 */
(function() {
	window.COGjQuery = jQuery.noConflict(true);
	window.COGNIFIDE = window.COGNIFIDE || {};
	window.COGNIFIDE.COGeval = eval;
}(jQuery, window));