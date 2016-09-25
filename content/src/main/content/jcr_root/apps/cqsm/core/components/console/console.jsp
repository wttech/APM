<%--
  ========================LICENSE_START=================================
  AEM Permission Management
  %%
  Copyright (C) 2013 - 2016 Cognifide Limited
  %%
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  =========================LICENSE_END==================================
  --%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="utf-8"%>
<%@include file="/apps/cqsm/global.jsp"%>
<sling:adaptTo adaptable="${slingRequest}" adaptTo="com.cognifide.cq.cqsm.core.models.ScriptEditModel" var="model"/>

<div class="inner">
	<div id="dialog-form">
		<br><br>
		<label for="fname">File name:</label>
		<input type="text" value="${model.fileName}" name="fname" id="fname" ${model.edit ? 'readonly="readonly"' : ''}><label>.cqsm</label>
		<br><br>
		<label for="cqsm">Content:</label><br>
		<div id="ace"></div>
		<textarea rows="12" cols="80" name="cqsm" id="cqsm">${model.content}</textarea><br/>
		<button id="showReference" class="cqsm-button"><div class="icon file">Show reference</div></button>
		<button id="validateButton" type="button" class="cqsm-button"><div class="icon valid">Validate</div></button>
		<button id="uploadButton" type="button" class="cqsm-button"><div class="icon upload">Save</div></button>
		<span class="lastSavedOn"></span>
	</div>
</div>


