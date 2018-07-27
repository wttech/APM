<%--
  ADOBE CONFIDENTIAL

  Copyright 2015 Adobe Systems Incorporated
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
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="java.io.UnsupportedEncodingException,
                  java.security.MessageDigest,
                  java.security.NoSuchAlgorithmException,
                  java.net.URLDecoder,
                  java.net.URLEncoder,
                  java.util.ArrayList,
                  java.util.Iterator,
                  java.util.List,
                  javax.servlet.http.Cookie,
                  org.apache.commons.codec.binary.Base64,
                  org.apache.commons.collections.IteratorUtils,
                  org.apache.commons.lang.CharSetUtils,
                  org.apache.commons.lang.StringUtils,
                  org.apache.jackrabbit.api.security.user.Authorizable,
                  org.apache.jackrabbit.util.Text,
                  org.apache.sling.api.SlingHttpServletRequest,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.commons.json.io.JSONStringer,
                  org.apache.sling.resourcemerger.api.ResourceMergerService,
                  com.adobe.granite.i18n.LocaleUtil,
                  com.adobe.granite.security.user.UserProperties,
                  com.adobe.granite.security.user.UserPropertiesManager,
                  com.adobe.granite.security.user.UserPropertiesService,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ExpressionHelper,
                  com.adobe.granite.ui.components.ExpressionResolver,
                  com.adobe.granite.ui.components.FilteringResourceWrapper,
                  com.adobe.granite.ui.components.Tag,
                  com.adobe.granite.ui.components.ds.DataSource"%><%--###
CollectionPage
==============

.. granite:servercomponent:: /libs/granite/ui/components/shell/collectionpagewithoutheader

   The page to render collection pattern.

   It also supports :doc:`resource filtering using FilteringResourceWrapper </jcr_root/libs/granite/ui/docs/server/resourcehiding>` and acts as its container.

   It has the following content structure:

   .. gnd:gnd::

      [granite:ShellCollectionPage]

      /**
       * A general purpose ID to uniquely identify the console.
       *
       * The recommended value is hierarchical separated by "-".
       * e.g. "cq-commerce-report"
       */
      - consoleId (StringEL)

      /**
       * The base title of the page.
       *
       * e.g. "AEM Sites"
       */
      - jcr:title (String)

      /**
       * The URI Template of the page. It is used to generate the new URL when navigating the collection.
       *
       * It supports the following variables:
       *
       *    id
       *       The id of the collection (:doc:`[data-foundation-collection-id] </jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/vocabulary/collection>`).
       *
       * e.g. ``/sites.html{+id}``
       */
      - pageURITemplate (StringEL)

      /**
       * This property is the equivalence of ``pageURITemplate`` for absolute path.
       *
       * For example if your template is ``{+id}.html``, since it is not starting with "/",
       * the server is unable to know if it is an absolute path.
       * So use this property if you want to add the context path regardless.
       */
      - 'pageURITemplate.abs' (StringEL)

      /**
       * The value of :doc:`[data-foundation-mode-group] </jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/vocabulary/mode>`
       * that the collection is part of.
       */
      - modeGroup (String)

      /**
       * The selector to the collection.
       */
      - targetCollection (String)
      
      /**
       * The path to the omnisearch configuration associated with the console.
       *
       * e.g. ``/libs/granite/omnisearch/content/metadata/site``
       */
      - omnisearchLocationPath (StringEL)

      /**
       * To redirect the page, this resource can be specified.
       * It will be included, where the redirect can be performed.
       */
      + redirector

      /**
       * A folder to specify the content of ``<head>`` of the page.
       * Its child resources are iterated and included as is.
       */
      + head

      /**
       * The folder for the available views (i.e. the rendering) of the collection,
       * where each can any component implementing :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/vocabulary/collection`.
       *
       * At least one view needs to be provided.
       * If there are at least two views, the following properties are needed to be able to switch the view:
       *
       * icon (String)
       *    The icon of the view.
       * jcr:title (String)
       *    The title of the view.
       * src (StringEL)
       *    The URI Template that is returning the HTML response of the new view.
       *    It supports the following variables:
       *
       *    offset
       *       The item offset of the current request.
       *    limit
       *       The item limit of the pagination.
       *    id
       *       The id of the collection (:doc:`[data-foundation-collection-id] </jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/vocabulary/collection>`).
       */
      + views mandatory

      /**
       * The header area just above the collection view.
       * Any component can be used here.
       */
      + header

      /**
       * The footer area just below the collection view.
       * Any component can be used here.
       */
      + footer

      + breadcrumbs

      /**
       * The component to render the title.
       *
       * Either this resource or ``breadcrumbs`` needs to be specified.
       * Use this resource instead of ``breadcrumbs`` when your resource is flat (not hierarchical).
       * If neither is specified, the value of ``jcr:title`` is used.
       *
       * If the title is just a simple string, :doc:`../title/index` can be used.
       *
       * The only requirement of the component is to generate a simple text without any wrapping markup.
       * E.g. To have a title of "My Page", just make the component do something like ``out.print("My Page")``.
       */
      + title

      /**
       * The folder for the actions applicable in the context of the collection.
       */
      + actions (granite:ShellCollectionPageActions)

      /**
       * A folder to specify the panels of the rail.
       */
      + rails (granite:ShellCollectionPageRails)

      [granite:ShellCollectionPageActions]

      /**
       * The folder for primary actions.
       *
       * The action can be any action component such as :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/button/index`,
       * :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/anchorbutton/index`,
       * :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/pulldown/index`,
       * :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/collection/index`.
       *
       * The ``actionBar`` variant of the components above SHOULD be used, unless ``primary`` variant is used.
       *
       * Usually the action is implementing :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/js/collection/action/index`,
       * with ``relScope`` = ``collection``.
       *
       * The actions are wrapped inside :doc:`.foundation-collection-actionbar </jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/js/collection/action/index>`
       * element where ``[data-foundation-collection-actionbar-target]`` is set as the value of ``targetCollection`` property.
       * This way setting the ``target`` property at individual action is not required.
       */
      + primary

      /**
       * The folder for secondary actions.
       *
       * The action can be any action component such as :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/button/index`,
       * :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/anchorbutton/index`,
       * :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/pulldown/index`,
       * :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/collection/index`.
       *
       * The ``actionBar`` variant of the components above SHOULD be used, unless ``primary`` variant is used.
       *
       * Usually the action is implementing :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/js/collection/action/index`,
       * with ``relScope`` = ``collection``.
       *
       * The actions are wrapped inside :doc:`.foundation-collection-actionbar </jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/js/collection/action/index>`
       * element where ``[data-foundation-collection-actionbar-target]`` is set as the value of ``targetCollection`` property.
       * This way setting the ``target`` property at individual action is not required.
       */
      + secondary

      /**
       * The folder for actions applicable during selection mode. (e.g. when one of the collection item is selected)
       *
       * The action can be any action component such as :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/button/index`,
       * :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/anchorbutton/index`,
       * :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/pulldown/index`,
       * :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/collection/index`.
       *
       * The ``actionBar`` variant of the components above SHOULD be used, unless ``primary`` variant is used.
       *
       * Usually the action is implementing :doc:`/jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/js/collection/action/index`,
       * with ``relScope`` = ``item``.
       *
       * The actions are wrapped inside :doc:`.foundation-collection-actionbar </jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/js/collection/action/index>`
       * element where ``[data-foundation-collection-actionbar-target]`` is set as the value of ``targetCollection`` property.
       * This way setting the ``target`` property at individual action is not required.
       */
      + selection
      
      [granite:ShellCollectionPageRails]
      
      /**
       * ``true`` to activate the rail initially.
       *
       * Note that you need to also set ``active`` property of the rail panel to ``true`` accordingly.
       */
      - active (BooleanEL)

      /**
       * The child resources are considered as the panels, where each MUST be a :doc:`../../coral/foundation/panel/railpanel/index` (or its derivative).
       */
      + '*' (granite:PanelRailPanel)

   Example::

      + /apps/my/page
        - sling:resourceType = "granite/ui/components/shell/collectionpage"
        - jcr:title = "AEM Sites"
        - modeGroup = "cq-siteadmin-admin-childpages"
        - pageURITemplate = "/sites.html{+id}"
        - targetCollection = "#cq-siteadmin-admin-childpages"
        + views
          + card
            - sling:resourceType = "my/card"
            - granite:id = "cq-siteadmin-admin-childpages"
            - icon = "viewCard"
            - jcr:title = "Card View"
            - modeGroup = "cq-siteadmin-admin-childpages"
            - src = "/mnt/overlay/my/page/views/card{.offset,limit}.html{+id}"
            - offset = "${requestPathInfo.selectors[0]}"
            - limit = "${empty requestPathInfo.selectors[1] ? "20" : requestPathInfo.selectors[1]}"
            - path = "${requestPathInfo.suffix}"
          + list
            - sling:resourceType = "my/list"
            - granite:id = "cq-siteadmin-admin-childpages"
            - icon = "viewList"
            - jcr:title = "List View"
            - modeGroup = "cq-siteadmin-admin-childpages"
            - src = "/mnt/overlay/my/page/views/list{.offset,limit}.html{+id}"
            - offset = "${requestPathInfo.selectors[0]}"
            - limit = "${empty requestPathInfo.selectors[1] ? "20" : requestPathInfo.selectors[1]}"
            - path = "${requestPathInfo.suffix}"
###--%><%

Config cfg = cmp.getConfig();
ExpressionHelper ex = cmp.getExpressionHelper();

if (!cfg.get("noMerge", false)) {
    ResourceMergerService resourceMerger = sling.getService(ResourceMergerService.class);
    if (resourceMerger != null) {
        Resource uiResource = resourceMerger.getMergedResource(resource);
        if (uiResource != null) {
            resource = uiResource;
            cfg = new Config(resource);
        }
    }
}

resource = new FilteringResourceWrapper(resource, sling.getService(ExpressionResolver.class), slingRequest);

Resource redirector = resource.getChild("redirector");
if (redirector != null) {
    %><sling:include resource="<%= redirector %>" /><%

    if (response.isCommitted()) {
        return;
    }
}

String targetCollection = cfg.get("targetCollection", String.class);
String consoleId = StringUtils.trimToNull(ex.getString(cfg.get("consoleId", String.class)));

// GRANITE-8258: Force the header to bypass the compatibility mode on intranet sites
response.setHeader("X-UA-Compatible", "IE=edge");

AttrBuilder htmlAttrs = new AttrBuilder(request, xssAPI);
htmlAttrs.addClass("skipCoral2Validation");
htmlAttrs.add("lang", LocaleUtil.toRFC4646(request.getLocale()).toLowerCase());
htmlAttrs.add("data-i18n-dictionary-src", request.getContextPath() + "/libs/cq/i18n/dict.{+locale}.json");

%><!DOCTYPE html>
<html <%= htmlAttrs %>>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
    <link rel="shortcut icon" href="<%= request.getContextPath() %>/libs/granite/core/content/login/favicon.ico"><%

    String title = i18n.getVar(cfg.get("jcr:title", String.class));
    if (title != null) {
        String pageURITemplate = handleURITemplate(cfg, "pageURITemplate", ex, request);

        AttrBuilder titleAttrs = new AttrBuilder(request, xssAPI);
        titleAttrs.addClass("granite-collection-pagetitle");
        titleAttrs.add("data-granite-collection-pagetitle-target", targetCollection);
        titleAttrs.add("data-granite-collection-pagetitle-src", pageURITemplate);
        titleAttrs.add("data-granite-collection-pagetitle-base", title);

        %><title <%= titleAttrs %>><%= xssAPI.encodeForHTML(title) %></title><%
    }

    String[] pageHierarchy = getPageHierarchy(consoleId);

    if (pageHierarchy == null) {
        String defaultTitle = title != null ? title : resource.getPath();
        pageHierarchy = new String[] { defaultTitle, defaultTitle };
    }
    
    String trackingPage = new JSONStringer()
        .object()
            .key("type").value("collection")
            .key("hierarchy").value(pageHierarchy[0])
            .key("name").value(pageHierarchy[1])
        .endObject()
        .toString();

    %><meta name="foundation.tracking.page" content="<%= xssAPI.encodeForHTMLAttr(trackingPage) %>"><%

    Resource globalHead = resourceResolver.getResource("/mnt/overlay/granite/ui/content/globalhead");
    if (globalHead != null) {
        for (Iterator<Resource> it = globalHead.listChildren(); it.hasNext();) {
            %><sling:include resource="<%= it.next() %>" /><%
        }
    }

    Authorizable auth = resourceResolver.adaptTo(Authorizable.class);
    UserPropertiesManager upm = resourceResolver.adaptTo(UserPropertiesManager.class);

    %><meta name="granite.shell.showonboarding" content="<%= xssAPI.encodeForHTMLAttr(getShowOnboarding(auth, upm, "true")) %>"><%

    String onboardingSrcPath = request.getContextPath() + "/mnt/overlay/granite/ui/content/shell/onboarding.html";
    %><meta class="granite-shell-onboarding-src" data-granite-shell-onboarding-src="<%= onboardingSrcPath %>">
    <meta name="user.preferences.winmode" content="<%= xssAPI.encodeForHTMLAttr(getWinMode(auth, upm, "multi")) %>"><%

    if (cfg.get("coral2", false)) {
        %><ui:includeClientLib categories="coralui2,granite.ui.coral.foundation,granite.ui.coral.foundation.addon.coral2,granite.ui.shell" /><%
    } else {
        %><ui:includeClientLib categories="coralui3,granite.ui.coral.foundation,granite.ui.shell" /><%
    }

    Resource head = resource.getChild("head");
    if (head != null) {
        for (Iterator<Resource> it = head.listChildren(); it.hasNext();) {
            %><sling:include resource="<%= it.next() %>" /><%
        }
    }

    %><meta class="granite-omnisearch-src" data-granite-omnisearch-src="<%= xssAPI.getValidHref(request.getContextPath() + "/mnt/overlay/granite/ui/content/shell/omnisearch.html") %>"><%

    String omnisearchLocationPath = ex.getString(cfg.get("omnisearchLocationPath", String.class));
    Resource omnisearchConfigResource = resourceResolver.getResource(omnisearchLocationPath);
    if (omnisearchConfigResource != null) {
        ValueMap props = omnisearchConfigResource.getValueMap();
        
        %><meta class="granite-omnisearch-location"
                data-granite-omnisearch-location-value="<%= xssAPI.encodeForHTMLAttr(omnisearchConfigResource.getName())%>"
                data-granite-omnisearch-location-label="<%= xssAPI.encodeForHTMLAttr(i18n.getVar(props.get("jcr:title", "")))%>"><%
    }
%></head><%
// Flush head so that the browser can start downloading the clientlibs
response.flushBuffer();

String modeGroup = cfg.get("modeGroup", String.class);

String navigationId = consoleId;

if (consoleId == null) {
    consoleId = generateConsoleId(resource);
}

String targetViewName = getTargetViewName(slingRequest, consoleId);

List<Resource> viewCache = new ArrayList<Resource>();
Resource currentView = null;

int i = 0;
for (Iterator<Resource> it = resource.getChild("views").listChildren(); it.hasNext(); i++) {
    Resource item = it.next();

    if (i == 0 || item.getName().equals(targetViewName)) {
        currentView = item;
    }

    viewCache.add(item);
}

String cookieConfig = new JSONStringer()
    .object()
        .key("name").value(consoleId)
        .key("expires").value(7)
        .key("path").value(request.getContextPath() + "/")
    .endObject()
    .toString();

AttrBuilder viewAttrs = new AttrBuilder(request, xssAPI);
viewAttrs.addClass("coral--light");
viewAttrs.addClass("shell-collectionpage-view");
viewAttrs.add("data-shell-collectionpage-view-target", targetCollection);
viewAttrs.add("data-shell-collectionpage-view-cookie", cookieConfig);

%><body <%= viewAttrs %>>

    <coral-shell role="main">
        <coral-shell-content id="coral-shell-content">
			<script>
     		 var element = document.getElementById("coral-shell-content");
     		 element.classList.remove("coral-Shell-content");
            </script>
        <div id="granite-shell-content" class="foundation-layout-panel">
            <div class="foundation-layout-panel-header"><%
                AttrBuilder actionBarAttrs = new AttrBuilder(request, xssAPI);
                actionBarAttrs.add("id", "granite-shell-actionbar");
                actionBarAttrs.addClass("foundation-collection-actionbar");
                actionBarAttrs.add("data-foundation-collection-actionbar-target", targetCollection);

                %><betty-titlebar <%= actionBarAttrs %>>
                    <betty-titlebar-title><%
                        Resource breadcrumbs = resource.getChild("breadcrumbs");
                        if (breadcrumbs != null) {
                            List<?> crumbs = IteratorUtils.toList(cmp.asDataSource(breadcrumbs).iterator());

                            if (!crumbs.isEmpty()) {
                                AttrBuilder navigatorAttrs = new AttrBuilder(request, xssAPI);
                                navigatorAttrs.addClass("granite-collection-navigator");
                                navigatorAttrs.add("id", "granite-collection-breadcrumbs-toggle");
                                navigatorAttrs.add("data-granite-collection-navigator-target", targetCollection);

                                %><betty-breadcrumbs <%= navigatorAttrs %>><%
                                    for (int j = 0; j < crumbs.size(); j++) {
                                        Resource item = (Resource) crumbs.get(j);
                                        Config itemCfg = new Config(item);

                                        AttrBuilder itemAttrs = new AttrBuilder(request, xssAPI);
                                        String navigatorCollectionid = itemCfg.get("path", String.class);
                                        if (navigatorCollectionid != null) {
                                            itemAttrs.add("data-granite-collection-navigator-collectionid", navigatorCollectionid);
                                        } else {
                                            itemAttrs.add("data-granite-collection-navigator-href", itemCfg.get("href", String.class));
                                        }
                                        %><betty-breadcrumbs-item <%= itemAttrs %>><%= xssAPI.encodeForHTML(itemCfg.get("title")) %></betty-breadcrumbs-item><%
                                    }
                                %></betty-breadcrumbs><%
                            }
                        } else {
                            Resource titleRes = resource.getChild("title");
                            if (titleRes != null) {
                                %><span class="granite-title" role="heading" aria-level="1"><sling:include resource="<%= titleRes %>" /></span><%
                            } else {
                                %><span class="granite-title" role="heading" aria-level="1"><%= xssAPI.encodeForHTML(title) %></span><%
                            }
                        }
                    %></betty-titlebar-title>
                    <betty-titlebar-primary><%
                        Resource rails = resource.getChild("rails");
                        if (rails != null) {
                                %><coral-cyclebutton id="shell-collectionpage-rail-toggle" class="granite-toggleable-control" icon="railLeft" displaymode="icontext">
                                    <coral-cyclebutton-item
                                        displaymode="icon"
                                        data-granite-toggleable-control-target="#shell-collectionpage-rail"
                                        data-granite-toggleable-control-action="hide"><%= i18n.get("Content Only") %></coral-cyclebutton-item><%

                                    for (Iterator<Resource> it = rails.listChildren(); it.hasNext();) {
                                        Resource item = it.next();
                                        if (cmp.getRenderCondition(item, true).check()) {
                                            Config itemCfg = new Config(item);
                                            AttrBuilder itemAttrs = new AttrBuilder(request, xssAPI);

                                            if (item.getName().equals("omnisearchfilter")) {
                                                itemAttrs.add("data-granite-toggleable-control-action", "hide");
                                                itemAttrs.add("data-granite-toggleable-control-target", "#shell-collectionpage-rail");
                                                itemAttrs.add("data-granite-omnisearch-filter", "");
                                            }
                                            else {
                                                String href = ex.getString(itemCfg.get("href", String.class));

                                                itemAttrs.add("icon", itemCfg.get("icon", String.class));

                                                if (href != null) {
                                                    itemAttrs.add("data-granite-toggleable-control-action", "navigate");
                                                    itemAttrs.addHref("data-granite-toggleable-control-href", href);
                                                } else {
                                                    String railPanelTarget = ".shell-collectionpage-rail-panel[data-shell-collectionpage-rail-panel='" + item.getName() + "']";

                                                    itemAttrs.add("data-granite-toggleable-control-action", "show");
                                                    itemAttrs.add("data-granite-toggleable-control-target", railPanelTarget);
                                                }
                                                
                                                itemAttrs.addSelected(itemCfg.get("active", false));
                                            }

                                            %><coral-cyclebutton-item <%= itemAttrs %>><%= outVar(xssAPI, i18n, itemCfg.get("jcr:title", String.class)) %></coral-cyclebutton-item><%
                                        }
                                    }
                                %></coral-cyclebutton><%
                        }

                        Resource primary = resource.getChild("actions/primary");
                        if (primary != null) {
                            for (Iterator<Resource> it = primary.listChildren(); it.hasNext();) {
                                Resource item = it.next();
                                %><sling:include resource="<%= item %>" /><%
                            }
                        }
                    %></betty-titlebar-primary>
                    <betty-titlebar-secondary><%
                        Resource secondary = resource.getChild("actions/secondary");
                        if (secondary != null) {
                            for (Iterator<Resource> it = secondary.listChildren(); it.hasNext();) {
                                Resource item = it.next();
                                if ("create".equals(item.getName())) {
                                    AttrBuilder createAttrs = new AttrBuilder(request, xssAPI);
                                    createAttrs.addClass("granite-collection-create");
                                    createAttrs.add("data-granite-collection-create-target", targetCollection);

                                    cmp.include(item, new Tag(createAttrs));
                                } else {
                                    %><sling:include resource="<%= item %>" /><%
                                }
                            }
                        }

                        if (viewCache.size() > 1) {
                            AttrBuilder switcherAttrs = new AttrBuilder(request, xssAPI);
                            switcherAttrs.addClass("granite-collection-switcher");
                            switcherAttrs.add("id", "granite-collection-switcher-toggle");
                            switcherAttrs.add("data-granite-collection-switcher-target", targetCollection);

                            %><coral-cyclebutton <%= switcherAttrs %>><%
                                for (Resource item : viewCache) {
                                    Config itemCfg = new Config(item);

                                    String src = ex.getString(itemCfg.get("src", String.class));

                                    AttrBuilder itemAttrs = new AttrBuilder(request, xssAPI);
                                    itemAttrs.add("data-granite-collection-switcher-src", handleURITemplate(src, request));
                                    itemAttrs.add("icon", itemCfg.get("icon", String.class));
                                    itemAttrs.addSelected(item.getName().equals(currentView.getName()));

                                    %><coral-cyclebutton-item <%= itemAttrs %>><%= outVar(xssAPI, i18n, itemCfg.get("jcr:title", String.class)) %></coral-cyclebutton-item><%
                                }
                            %></coral-cyclebutton><%
                        }
                    %></betty-titlebar-secondary>
                </betty-titlebar><%

                Resource header = resource.getChild("header");
                if (header != null) {
                    %><sling:include resource="<%= header %>" /><%
                }
            %></div>
            <div class="foundation-layout-panel-bodywrapper">
                <div class="foundation-layout-panel-body"><%
                    if (rails != null) {
                        AttrBuilder railAttrs = new AttrBuilder(request, xssAPI);
                        railAttrs.add("id", "shell-collectionpage-rail");
                        railAttrs.addClass("foundation-toggleable foundation-layout-panel-rail granite-rail");

                        if (cmp.getExpressionHelper().getBoolean(rails.getValueMap().get("active", "false"))) {
                            railAttrs.addClass("foundation-layout-panel-rail-active");
                        }

                        %><div <%= railAttrs %>>
                            <coral-panelstack maximized><%
                                for (Iterator<Resource> it = rails.listChildren(); it.hasNext();) {
                                    Resource item = it.next();
                                    AttrBuilder itemAttrs = new AttrBuilder(request, xssAPI);
                                    itemAttrs.addClass("shell-collectionpage-rail-panel");
                                    itemAttrs.add("data-shell-collectionpage-rail-panel", item.getName());
                                    cmp.include(item, new Tag(itemAttrs));
                                }
                            %></coral-panelstack>
                        </div><%
                    }
                    %><div class="foundation-layout-panel-content"><%
                        Config curConfig = new Config(currentView);
                        String selector = curConfig.get("selector", String.class);
                    if (StringUtils.isNotEmpty(selector)) {
                        %><sling:include resource="<%= currentView %>" addSelectors="<%=selector%>"/><%
                    } else {
                        %><sling:include resource="<%= currentView %>" /><%
                    }
                    %></div>
                </div>
            </div><%

            Resource footer = resource.getChild("footer");
            if (footer != null) {
                %><div class="foundation-layout-panel-footer">
                    <sling:include resource="<%= footer %>" />
                </div><%
            }
        %></div>
    </coral-shell-content>
</coral-shell>
<%

AttrBuilder selectionAttrs = new AttrBuilder(request, xssAPI);
selectionAttrs.addClass("granite-collection-selectionbar");
selectionAttrs.addClass("foundation-mode-switcher");
selectionAttrs.add("data-foundation-mode-switcher-group", modeGroup);

%><div <%= selectionAttrs %>>
    <div class="foundation-mode-switcher-item" data-foundation-mode-switcher-item-mode="selection"><%
        AttrBuilder selectionBarAttrs = new AttrBuilder(request, xssAPI);
        selectionBarAttrs.addClass("betty-ActionBar betty-ActionBar--large");
        selectionBarAttrs.addClass("foundation-collection-actionbar");
        selectionBarAttrs.add("data-foundation-collection-actionbar-target", targetCollection);

        %><coral-actionbar <%= selectionBarAttrs %>>
            <coral-actionbar-primary><%
                Resource selection = resource.getChild("actions/selection");
                if (selection != null) {
                    for (Iterator<Resource> it = selection.listChildren(); it.hasNext();) {
                        Resource item = it.next();

                        if (!cmp.getRenderCondition(item, true).check()) {
                            continue;
                        }
                        %><coral-actionbar-item><%
                            AttrBuilder selectionItemAttrs = new AttrBuilder(request, xssAPI);
                            selectionItemAttrs.addClass("betty-ActionBar-item");
                            cmp.include(item, new Tag(selectionItemAttrs));
                        %></coral-actionbar-item><%
                    }
                }
            %></coral-actionbar-primary>
            <coral-actionbar-secondary>
                <coral-actionbar-item><%
                    AttrBuilder deselectAttrs = new AttrBuilder(request, xssAPI);
                    deselectAttrs.add("type", "button");
                    deselectAttrs.addClass("betty-ActionBar-item");
                    deselectAttrs.addClass("granite-collection-deselect");
                    deselectAttrs.add("data-granite-collection-deselect-target", targetCollection);
                    deselectAttrs.addClass("coral-Button coral-Button--quiet");

                    AttrBuilder counterAttrs = new AttrBuilder(request, xssAPI);
                    counterAttrs.addClass("foundation-admin-selectionstatus");
                    counterAttrs.add("data-foundation-admin-selectionstatus-template", i18n.get("{0} selected", null, "{{count}}"));
                    counterAttrs.add("data-foundation-admin-selectionstatus-target", targetCollection);

                    %><button <%= deselectAttrs %>><span <%= counterAttrs %>></span><coral-icon class="granite-collection-deselect-icon" icon="close" size="XS"></coral-icon></button>
                </coral-actionbar-item>
            </coral-actionbar-secondary>
        </coral-actionbar>
    </div>
</div><%
Resource globalFooter = resourceResolver.getResource("/mnt/overlay/granite/ui/content/globalfooter");
if (globalFooter != null) {
    for (Iterator<Resource> it = globalFooter.listChildren(); it.hasNext();) {
        %><sling:include resource="<%= it.next() %>" /><%
    }
}
%>
</body>
</html><%!

private String generateConsoleId(Resource resource) {
    try {
        MessageDigest md = MessageDigest.getInstance("md5");
        byte[] b = md.digest(resource.getPath().getBytes("utf-8"));
        return Base64.encodeBase64URLSafeString(b).substring(0, 22); // cut off last "==".
    } catch (NoSuchAlgorithmException impossible) {
        throw new RuntimeException(impossible);
    } catch (UnsupportedEncodingException impossible) {
        throw new RuntimeException(impossible);
    }
}

private String getTargetViewName(SlingHttpServletRequest request, String consoleId) {
    try {
        consoleId = URLEncoder.encode(consoleId, "utf-8");
        Cookie cookie = request.getCookie(consoleId);

        if (cookie == null) {
            return null;
        }

        return URLDecoder.decode(cookie.getValue(), "utf-8");
    } catch (UnsupportedEncodingException impossible) {
        throw new RuntimeException(impossible);
    }
}

private String handleURITemplate(String src, HttpServletRequest request) {
    if (src != null && src.startsWith("/")) {
        return request.getContextPath() + src;
    }
    return src;
}

private String handleURITemplate(Config cfg, String name, ExpressionHelper ex, HttpServletRequest request) {
    String value = ex.getString(cfg.get(name, String.class));

    if (value != null) {
        if (value.startsWith("/")) {
            return request.getContextPath() + value;
        } else {
            return value;
        }
    }

    value = ex.getString(cfg.get(name + ".abs", String.class));

    if (value != null) {
        return request.getContextPath() + value;
    } else {
        return value;
    }
}

private String getWinMode(Authorizable auth, UserPropertiesManager upm, String defaultValue) throws Exception {
    if (upm != null) {
        UserProperties props = upm.getUserProperties(auth, UserPropertiesService.PREFERENCES_PATH);
        if (props != null) {
            return props.getProperty("winMode", defaultValue, String.class);
        }
    }
    return defaultValue;
}

private String getShowOnboarding(Authorizable auth, UserPropertiesManager upm, String defaultValue) throws Exception {
    if (upm != null) {
        UserProperties props = upm.getUserProperties(auth, UserPropertiesService.PREFERENCES_PATH);
        if (props != null) {
            return props.getProperty("granite.shell.showonboarding620", defaultValue, String.class);
        }
    }
    return defaultValue;
}

/**
 * Return the hierarchy and name of the current page based on consoleId.
 *
 * If the first part of the consoleId is "cq" or "granite", the part is ignored.
 *
 * Example
 *
 * ----------------------------------------------
 * consoleId    | result
 * ----------------------------------------------
 * cq-sites     | hierarchy = sites, name = sites
 * cq-sites-abc | hierarchy = sites, name = abc
 * ----------------------------------------------
 */
private String[] getPageHierarchy(String consoleId) {
    if (StringUtils.isBlank(consoleId)) {
        return null;
    }

    String[] parts = consoleId.split("-");

    int startIndex = 0;

    String namespace = parts[0];
    if (namespace.equals("cq") || namespace.equals("granite")) {
        startIndex = 1;
    }

    if (startIndex >= parts.length) {
        return null;
    }

    String name = parts[parts.length - 1];

    String hierarchy;
    if (startIndex == parts.length - 1) {
        hierarchy = name;
    } else {
        hierarchy = StringUtils.join(parts, ':', startIndex, parts.length - 1);
    }

    return new String[] { hierarchy, name };
}
%>
