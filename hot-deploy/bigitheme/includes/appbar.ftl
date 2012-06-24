
<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#if (externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
<#assign contextPath = request.getContextPath()>
<#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "main")>
<#assign displaySecondaryApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "public")>
<#assign displayPublicApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "public")>

<#if userLogin?has_content>
  <div id="main-navigation">
    <ul>
      <#assign appCount = 0>
      <#assign firstApp = true>
      <#list displayApps as display>
        <#assign thisApp = display.getContextRoot()>
        <#assign permission = true>
        <#assign selected = false>
        <#assign permissions = display.getBasePermission()>
        <#list permissions as perm>
          <#if (perm != "NONE" && !security.hasEntityPermission(perm, "_VIEW", session) && !authz.hasPermission(session, perm, requestParameters))>
            <#-- User must have ALL permissions in the base-permission list -->
            <#assign permission = false>
          </#if>
        </#list>
        <#if permission == true>
          <#if thisApp == contextPath || contextPath + "/" == thisApp>
            <#assign selected = true>
          </#if>
          <#assign thisApp = StringUtil.wrapString(thisApp)>
          <#assign thisURL = thisApp>
          <#if thisApp != "/">
            <#assign thisURL = thisURL + "/control/main">
          </#if>
          <#if layoutSettings.suppressTab?exists && display.name == layoutSettings.suppressTab>
            <#-- do not display this component-->
          <#else>
            <#if appCount % 4 == 0>
              <#if firstApp>
                <li class="first">
                <#assign firstApp = false>
              <#else>
                </li>
                <li>
              </#if>
            </#if>
            <a href="${thisURL}${externalKeyParam}"<#if selected> class="selected"</#if><#if uiLabelMap?exists> title="${uiLabelMap[display.description]}">${uiLabelMap[display.title]}<#else> title="${display.description}">${display.title}</#if></a>
            <#assign appCount = appCount + 1>
          </#if>
        </#if>
      </#list>
      <#list displaySecondaryApps as display>
      <#assign thisApp = display.getContextRoot()>
      <#assign permission = true>
      <#assign selected = false>
      <#assign permissions = display.getBasePermission()>
      <#list permissions as perm>
        <#if (perm != "NONE" && !security.hasEntityPermission(perm, "_VIEW", session) && !authz.hasPermission(session, perm, requestParameters))>
          <#-- User must have ALL permissions in the base-permission list -->
          <#assign permission = false>
        </#if>
      </#list>
      <#if permission == true>
        <#if thisApp == contextPath || contextPath + "/" == thisApp>
          <#assign selected = true>
        </#if>
        <#assign thisApp = StringUtil.wrapString(thisApp)>
        <#assign thisURL = thisApp>
        <#if thisApp != "/">
          <#assign thisURL = thisURL + "/control/main">
        </#if>
        <#if appCount % 4 == 0>
          <#if firstApp>
            <li class="first">
            <#assign firstApp = false>
          <#else>
            </li>
            <li>
          </#if>
        </#if>
        <a href="${thisURL}${externalKeyParam}"<#if selected> class="selected"</#if><#if uiLabelMap?exists> title="${uiLabelMap[display.description]}">${uiLabelMap[display.title]}<#else> title="${display.description}">${display.title}</#if></a>
        <#assign appCount = appCount + 1>
      </#if>
    </#list>
    <#if appCount != 0>
      </li>
      <li class="last">appCount: ${appCount}</li>
    </#if>
    </ul>
  </div>
</#if>
