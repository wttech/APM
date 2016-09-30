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
%><sling:adaptTo adaptable="${slingRequest}" adaptTo="com.cognifide.cq.cqsm.core.models.ImportInitModel" var="model"/><%
%><c:set var="filesParentPath" value="${model.currentPath}" />
<div class="inner" id="cqsmImportPage">
	<div class="navigation">
		<a class="cqsm-button" href="/etc/cqsm/history.html"><div class="link-icon history">Show history</div></a>
	</div>

	<h3>Actions</h3>
	<form method="POST" action="/bin/cqsm/fileUpload?redirect=true" enctype="multipart/form-data" id="form">
		<input type="file" name="file" multiple class="hidden" id="fileToUpload" />
		<button id="uploadButton" class="cqsm-button" type="button"><div class="icon upload">Upload scripts</div></button>
		<button class="cqsm-button" type="button" id="open-console"><div class="icon console">Open Console</div></button>
		<button class="cqsm-button" type="button" id="remove-all"><div class="icon remove">Remove all scripts</div></button>
	</form>

	<h3>Scripts</h3>
	<table id="cqsm-files" class="tablesorter files-table">
		<thead>
		<tr>
			<th width="30%">File</th>
			<th width="10%">Author</th>
			<th width="8%">Created at</th>
			<th width="8%">Last run on author</th>
			<th width="4%">Dry run</th>
			<th width="40%">Actions</th>
		</tr>
		</thead>
		<tbody>
		<c:if test="${empty model.files}">
			<tr>
				<td colspan="6">No files found</td>
			</tr>
		</c:if>
		<c:forEach var="file" items="${model.files}" varStatus="status">
			<tr id="cqsm-file-${status.index}" class="cqsm-file row <c:if test="${not file.valid}">not-valid</c:if>">
				<td><a href="${file.path}" download="${file.fileName}" <c:if test="${not file.valid}">class="not-valid"</c:if>>${file.fileName}</a></td>
				<td>${file.author}</td>
				<td><c:if test="${not empty file.lastModified}"><fmt:formatDate value="${file.lastModified}" type="both" /></c:if></td>
				<td class="last-run-author">
					<c:if test="${not empty file.lastExecuted}"><fmt:formatDate value="${file.lastExecuted}" type="both" /></c:if>
					<c:if test="${empty file.lastExecuted}">never</c:if>
				</td>
				<c:set var="class" value="${file.dryRunExecuted ? (file.dryRunSuccessful ? 'class=\"dryRunSuccess\"' : 'class=\"dryRunFailed\"') : ''}"/>
				<td class="dryRunStatus"><div ${class}></div>
				</td>
				<td>
					<c:choose>
						<c:when test="${file.executionMode == 'on_demand'}">
							<button class="cqsm-button action-view" type="button"><div class="icon search">&nbsp;</div></button>
							<button class="cqsm-button mode-dry-run" type="button"><div class="icon dry_run">Dry run</div></button>
							<c:choose>
								<c:when test="${model.onAuthor}">
									<button class="cqsm-button mode-import" type="button" <c:if test="${not file.valid}">disabled</c:if>>
										<div class="icon run">Run on author</div>
									</button>
									<button class="cqsm-button mode-run-publish" type="button" <c:if test="${not file.valid}">disabled</c:if>>
										<div class="icon run">Run on publish</div>
										</button>
								</c:when>
								<c:otherwise>
									<button class="cqsm-button mode-import" type="button" <c:if test="${not file.valid}">disabled</c:if>>Run</button>
								</c:otherwise>
							</c:choose>
							<button class="cqsm-button action-edit" type="button"><div class="icon edit">&nbsp;</div></button>
							<button class="cqsm-button action-remove" type="button"><div class="icon remove-small">&nbsp;</div></button>
						</c:when>
						<c:otherwise>
							<button class="cqsm-button action-view" type="button"><div class="icon search">&nbsp;</div></button>

							<label class="execution-mode">
								<input class="execution-enabled" type="checkbox" value="1" ${file.executionEnabled ? 'checked' : ''}>
								<c:choose>
									<c:when test="${file.executionMode == 'on_start'}">Automatic execution (on every instance start)</c:when>
									<c:when test="${file.executionMode == 'on_modify'}">Automatic execution (on script content change)</c:when>
									<c:when test="${file.executionMode == 'on_schedule'}">Automatic execution (after date: ${file.executionSchedule})</c:when>
								</c:choose>
							</label>

							<button class="cqsm-button action-edit" type="button"><div class="icon edit">&nbsp;</div></button>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>

	<div class="navigation">
		<a href="#" class="cqsm-button" id="prev"><div class="link-icon-single prev">&nbsp;</div></a>
		<a href="#" class="cqsm-button" id="next"><div class="link-icon-single next">&nbsp;</div></a>
	</div>

	<div class="status-message" id="first-status-message"></div>
	<div id="import-summary"></div>
	<div class="status-message" id="second-status-message"></div>
</div>