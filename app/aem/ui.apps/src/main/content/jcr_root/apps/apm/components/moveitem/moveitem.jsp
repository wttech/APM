<%--
  ADOBE CONFIDENTIAL

  Copyright 2013 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@page import="com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  org.apache.sling.api.resource.ValueMap,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
                  java.util.HashMap,
                  org.apache.sling.api.wrappers.ValueMapDecorator"
          session="false" %><%
%><%@include file="/libs/granite/ui/global.jsp"%><%

    Config cfg = cmp.getConfig();
    String item = slingRequest.getRequestPathInfo().getSuffix();

    Resource res1 = resourceResolver.getResource(item);
    ValueMap vm1 = res1.adaptTo(ValueMap.class);

    String name = res1.getName();
    String path = res1.getPath();
    String title = vm1.get("jcr:title", "");
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
    ValueMapResource pathBrowser = new ValueMapResource(resourceResolver, resource.getPath(), "granite/ui/components/foundation/form/pathbrowser", pathBrowserProperties);
%>

<div class="foundation-content-path hidden" data-foundation-content-path="<%= xssAPI.encodeForHTMLAttr(item) %>"></div>
<section class="rename-container coral-FixedColumn">
    <div class="coral-FixedColumn-column">
        <section>
            <section class="coral-Form-fieldset">
                <input class="coral-Form-field coral-Textfield" name="path" type="hidden" value="<%= xssAPI.encodeForHTMLAttr(path) %>">
                <h3 class="coral-Form-fieldset-legend"><%= i18n.get("Move {0}", null, xssAPI.filterHTML(title)) %></h3>
                <label class="coral-Form-fieldlabel "><%= i18n.get("Rename to") %></label>
                <input class="coral-Form-field coral-Textfield moveitem-rename-to" type="text" name="rename" value="<%= xssAPI.encodeForHTMLAttr(name) %>" aria-required="true">
                <label class="coral-Form-fieldlabel "><%= i18n.get("Move to") %></label>
                <div <%= attrs.build() %>>
                    <sling:include resource="<%= pathBrowser %>"/>
                </div>
            </section>
        </section>
    </div>
</section>
