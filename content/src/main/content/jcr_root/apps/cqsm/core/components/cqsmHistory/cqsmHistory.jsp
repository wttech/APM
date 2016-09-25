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
<%@page import="org.apache.commons.lang.StringEscapeUtils"%><%
%><%@page contentType="text/html" pageEncoding="utf-8"%><%
%><%@include file="/apps/cqsm/global.jsp"%><%
%><sling:adaptTo adaptable="${slingRequest}" adaptTo="com.cognifide.cq.cqsm.core.models.ExecutionHistoryModel" var="model"/><%
%><div class="inner" id="cqsmHistoryPage">
	<div class="navigation">
		<a href="/etc/cqsm.html" class="cqsm-button"><div class="link-icon import">Go to import</div></a>
	</div>

	<h3>History</h3>
	<form action="history.html" method="get">
		<select class="action-filter-history cqsm-button" name="filter">
			<option value="">All</option>
			<option ${param.filter == 'automatic run' ? 'selected="selected"' : ''} value="automatic run">Automatic run</option>
			<option ${param.filter == 'author' ? 'selected="selected"' : ''} value="author">Run on author</option>
			<option ${param.filter == 'publish' ? 'selected="selected"' : ''} value="publish">Run on publish</option>
		</select>
		<button class="cqsm-button" type="submit"><div class="icon filter">Filter</div></button>
	</form>

	<table id="cqsm-executions" class="tablesorter files-table">
		<thead>
		<tr>
			<th width="15%">File</th>
			<th width="12%">Author</th>
			<th width="12%">Uploaded at</th>
			<th width="12%">Executor</th>
			<th width="12%">Executed at</th>
			<th width="10%">Instance</th>
			<th width="30%">Actions</th>
		</tr>
		</thead>
		<tbody>
		<c:if test="${empty model.executions}">
			<tr>
				<td colspan="6">Nothing was executed yet</td>
			</tr>
		</c:if>
		<c:forEach var="execution" items="${model.executions}" varStatus="status">
			<tr id="cqsm-execution-${status.index}" class="cqsm-execution row">
				<td><a href="${execution.filePath}" download="${execution.fileName}">${execution.fileName}</a></td>
				<td>${execution.author}</td>
				<td><fmt:formatDate value="${execution.uploadTime}" type="both" /></td>
				<td>${execution.executor}</td>
				<td><fmt:formatDate value="${execution.executionTime}" type="both" /></td>
				<td>
					${execution.instanceType}
					<c:if test="${not empty execution.instanceHostname}">
						<span class="text-muted text-small">(${execution.instanceHostname})</span>
					</c:if>
				</td>
				<td>
					<button class="action-show-summary cqsm-button" type="button" path="${execution.path}">
						<div class="icon summary">Show summary</div>
					</button>
					<button class="action-view cqsm-button" type="button"><div class="icon search">&nbsp;</div></button>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<a href="#" class="cqsm-button" id="prev"><div class="link-icon-single prev">&nbsp;</div></a>
	<a href="#" class="cqsm-button" id="next"><div class="link-icon-single next">&nbsp;</div></a>
	
	<div class="status-message"></div>
	<div id="import-summary"></div>

</div>