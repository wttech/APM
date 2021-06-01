<!--
~ ========================LICENSE_START=================================
~ AEM Permission Management
~ %%
~ Copyright (C) 2013 Wunderman Thompson Technology
~ %%
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~       http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
~ =========================LICENSE_END==================================
-->
<%@page import="com.adobe.granite.ui.components.AttrBuilder,
                com.adobe.granite.ui.components.Config,
                org.apache.sling.api.resource.ValueMap,
                com.adobe.granite.ui.components.ds.ValueMapResource,
                java.util.HashMap,
                org.apache.sling.api.wrappers.ValueMapDecorator"
        session="false" %>
<%@include file="/libs/granite/ui/global.jsp" %>
<%
    Config cfg = cmp.getConfig();
    String item = slingRequest.getRequestPathInfo().getSuffix();

    Resource suffixResource = resourceResolver.getResource(item);

    String name = suffixResource.getName();
    String path = suffixResource.getPath();
    String title = suffixResource.getValueMap().get("jcr:title", "");
    if (title.isEmpty()) {
        title = name;
    }

    AttrBuilder attrs = cmp.consumeTag().getAttrs();
    attrs.add("title", i18n.get("Select Parent Path"));

    ValueMap pathBrowserProperties = new ValueMapDecorator(new HashMap<String, Object>());
    pathBrowserProperties.put("name", "dest");
    pathBrowserProperties.put("pickerMultiselect", false);
    pathBrowserProperties.put("pickerTitle", i18n.get("Select Parent Path"));
    pathBrowserProperties.put("icon", cfg.get("icon", "icon-browse"));
    pathBrowserProperties.put("rootPath", "/conf/apm/scripts");
    pathBrowserProperties.put("crumbRoot", "APM Scripts");
    ValueMapResource pathBrowser = new ValueMapResource(resourceResolver, suffixResource.getPath(), "granite/ui/components/foundation/form/pathbrowser", pathBrowserProperties);
%>

<div class="foundation-content-path hidden" data-foundation-content-path="<%= xssAPI.encodeForHTMLAttr(item) %>"></div>
<section class="rename-container coral-FixedColumn">
    <div class="coral-FixedColumn-column">
        <section class="coral-Form-fieldset">
            <input class="coral-Form-field coral-Textfield" name="path" type="hidden"
                   value="<%= xssAPI.encodeForHTMLAttr(path) %>">
            <h3 class="coral-Form-fieldset-legend"><%= i18n.get("Move {0}", null, xssAPI.filterHTML(title)) %>
            </h3>
            <label class="coral-Form-fieldlabel "><%= i18n.get("Rename to") %>
            </label>
            <input class="coral-Form-field coral-Textfield moveitem-rename-to" type="text" name="rename"
                   value="<%= xssAPI.encodeForHTMLAttr(name) %>" aria-required="true">
            <label class="coral-Form-fieldlabel "><%= i18n.get("Move to") %>
            </label>
            <div <%= attrs.build() %>>
                <sling:include resource="<%= pathBrowser %>"/>
            </div>
        </section>
    </div>
</section>
