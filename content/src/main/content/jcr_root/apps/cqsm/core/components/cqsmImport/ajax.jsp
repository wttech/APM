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
<%@page contentType="text/html" pageEncoding="utf-8"%>
<%@include file="/apps/cqsm/global.jsp"%>
<sling:adaptTo adaptable="${slingRequest}" adaptTo="com.cognifide.cq.cqsm.core.models.ImportSummaryModel" var="model"/>
<div id="status-message-type" style="visibility:hidden">${model.messageType}</div>
<h3>Summary</h3>
<table id="import-summary-table" class="tablesorter">
	<thead>
		<tr>
			<th>No</th>
			<th>Authorizable</th>
			<th>Command</th>
			<th>Status</th>
			<th>Messages</th>
	</tr>
	</thead>
	<tbody>
		<c:forEach var="pe" items="${model.progressEntries}" varStatus="peStatus">
			<tr class="row">
				<td>${peStatus.index + 1}</td>
				<td>${pe.authorizable}</td>
				<td>${pe.command}</td>
				<td class="status">
					<c:choose>
						<c:when test="${pe.status eq 'ERROR'}">
							<c:set var="img" value="error.gif" />
						</c:when>
						<c:when test="${pe.status eq 'WARNING'}">
							<c:set var="img" value="warning.gif" />
						</c:when>
						<c:otherwise>
							<c:set var="img" value="check.gif" />
						</c:otherwise>
					</c:choose>
					<img src="/etc/designs/cqsm/clientlibs/img/${img}" >
				</td>
				<td>
					<c:forEach var="message" items="${pe.messages}" varStatus="messageStatus">
						<span class="${message.type}">${message.text}</span><c:if test="${not messageStatus.last}"><br></c:if>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<form method="POST" action="/bin/cqsm/executionResultDownload" >
	<input type="hidden" name="filename" value="${model.fileName}"/>
	<input type="hidden" name="content" value='${model.json}'/>
	<input class="cqsm-button" type="Submit" value="Export to file" />
</form>