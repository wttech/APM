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
<%@page contentType="text/html" pageEncoding="utf-8" %>
<%@include file="/libs/foundation/global.jsp" %>

<div class="inner">
    <div class="reference-section variable-definitions">
        <h3>Syntax</h3>
        <h4>Define</h4>
        <table>
            <tr>
                <td>
                    DEFINE name value
                </td>
                <td>
                    <code>DEFINE String '/content'</code>
                    <code>DEFINE Number 10</code>
                    <code>DEFINE Boolean FALSE</code>
                    <code>DEFINE Array ['/content', 'etc']</code>
                </td>
            </tr>
        </table>
        <h4>Foreach</h4>
        <table>
            <tr>
                <td>
                    FOREACH item IN items BEGIN<br>
                    ...<br>
                    END
                </td>
                <td>
                    <code>
                        FOREACH Path IN ['/content', '/etc'] BEGIN<br>
                        ALLOW $Path [ALL]<br>
                        END
                    </code>
                </td>
            </tr>
        </table>
        <h4>Define Macro</h4>
        <table>
            <tr>
                <td>
                    DEFINE MACRO name param1 ... paramN BEGIN<br>
                    ...<br>
                    END
                </td>
                <td>
                    <code>
                        DEFINE MACRO Macro User Path BEGIN<br>
                        FOR_USER $User<br>
                        ALLOW $Path [ALL]<br>
                        END
                    </code>
                </td>
            </tr>
        </table>
        <h4>Use Macro</h4>
        <table>
            <tr>
                <td>
                    USE MACRO name param1 ... paramN
                </td>
                <td>
                    <code>USE MACRO Macro 'User' '/contnet'</code>
                </td>
            </tr>
        </table>
        <h4>Import</h4>
        <table>
            <tr>
                <td>
                    IMPORT name
                </td>
                <td>
                    <code>IMPORT AEMPaths.cqsm</code>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    IMPORT imports script in isolated variable context, that means variables defined in scripts are
                    not visible to other script.
                </td>
            </tr>
            <tr>
                <td>
                    IMPORT INPLACE name
                </td>
                <td>
                    <code>IMPORT INPLACE AEMPaths.cqsm</code>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    IMPORT INPLACE imports script in current variable context, that means all variables defined
                    in imported script are available in importing script, and vice versa.<br>
                    A good practise is to use IMPORT INPLACE, to import scripts which only define variables or macros.
                </td>
            </tr>
        </table>
    </div>

    <div id="action-reference" class="reference-section tablesorter">
        <c:set var="references"
               value="<%=sling.getService(com.cognifide.cq.cqsm.api.actions.ActionFactory.class).refer() %>"/>

        <h3>Available commands</h3>
        <table>
            <thead>
            <tr>
                <th style="width: 35%;">Command syntax</th>
                <th style="width: 20%;">Arguments</th>
                <th style="width: 45%;">Description</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="reference" items="${references}">
                <tr>
                    <td>
                        <c:forEach var="command" items="${reference.commands}">
                            ${command}<br>
                        </c:forEach>
                    </td>
                    <td>${fn:join(reference.args, ', ')}</td>
                    <td>${reference.reference}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="reference-section">
        <h3>Permissions</h3>

        <div class="usage">
            For commands: ALLOW, DENY you should specify an argument which is a list of permissions.<br/><br/>
            Possible values (you may use them without apostrophes):
            <ul class="permission-list">
                <li>READ,</li>
                <li>CREATE,</li>
                <li>MODIFY,</li>
                <li>MODIFY_PAGE,</li>
                <li>DELETE,</li>
                <li>DELETE_CHILD_NODES,</li>
                <li>REPLICATE,</li>
                <li>ALL.</li>
            </ul>
            You can also use jcr privileges identifiers (you <strong>must</strong> use them within apostrophes):
            <ul class="permission-list">
                <li>jcr:readAccessControl,</li>
                <li>jcr:versionManagement,</li>
                <li>jcr:lockManagement,</li>
                <li>jcr:nodeTypeManagement,</li>
                <li>...</li>
            </ul>
            Wrap them into square brackets like in examples below.
        </div>

        <table>
            <tbody>
            <tr>
                <td>FOR_USER 'foo'</td>
            </tr>
            <tr>
                <td>ALLOW '/content' [ALL]</td>
            </tr>
            <tr>
                <td>ALLOW '/etc' [READ]</td>
            </tr>
            <tr>
                <td>DENY '/content/bar' [MODIFY, DELETE]</td>
            </tr>
            <tr>
                <td>ALLOW '/content/bar' ['jcr:nodeTypeManagement']</td>
            </tr>
            </tbody>
        </table>

        <div class="usage">
            The default ACL effects the whole subtree of the target node, you can limit it by adding a glob parameter,
            which defines a pattern to restrict the subtree.
            This affects performance so please use it carefully. Apply privileges directly on target node whenever you
            can.<br/><br/>
            You can use STRICT keyword as a glob pattern to match a target node only. See examples below:
        </div>

        <table>
            <tbody>
            <tr>
                <td>FOR_USER 'foo'</td>
            </tr>
            <tr>
                <td>ALLOW '/content' 'bar' [ALL]</td>
            </tr>
            <tr>
                <td>DENY '/content' 'bar' ['jcr:nodeTypeManagement']</td>
            </tr>
            <tr>
                <td>ALLOW '/content/foo' STRICT [ALL]</td>
            </tr>
            </tbody>
        </table>
    </div>
</div>


