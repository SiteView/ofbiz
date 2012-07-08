  <div id="bigit-header">
    <div id="left">
      <#if sessionAttributes.overrideLogo?exists>
        <img src="<@ofbizContentUrl>${sessionAttributes.overrideLogo}</@ofbizContentUrl>" alt="Logo"/>
      <#elseif catalogHeaderLogo?exists>
        <img src="<@ofbizContentUrl>${catalogHeaderLogo}</@ofbizContentUrl>" alt="Logo"/>
      <#elseif layoutSettings.VT_HDR_IMAGE_URL?has_content>
        <img src="<@ofbizContentUrl>${layoutSettings.VT_HDR_IMAGE_URL.get(0)}</@ofbizContentUrl>" alt="Logo"/>
      </#if>
    </div>
    <div id="middle">
      <#if !userLogin?has_content>
        <h2>欢迎<u><a href="<@ofbizUrl>newcustomer</@ofbizUrl>">${uiLabelMap.EcommerceRegister}</a></u>，获取更多信息</h2>
      <#else>
      	<h2><u><a href="<@ofbizUrl>viewprofile</@ofbizUrl>">${uiLabelMap.CommonProfile}</a></u>个性化内容 </h2>  
      </#if>
      <div id="welcome-message">
        <#if sessionAttributes.autoName?has_content>
          ${uiLabelMap.CommonWelcome}&nbsp;${sessionAttributes.autoName?html}!
          (${uiLabelMap.CommonNotYou}?&nbsp;<a href="<@ofbizUrl>autoLogout</@ofbizUrl>" class="linktext">${uiLabelMap.CommonClickHere}</a>)
        <#else/>
          ${uiLabelMap.CommonWelcome}!
        </#if>
      </div>
    </div>
    <div id="right">
      demo
    </div>
  </div>


  <div id="bigit-header-bar">
    <ul id="left-links">
      <!-- <li id="header-bar-main"><a href="<@ofbizUrl>main</@ofbizUrl>">首页</a></li>
      <li id="header-bar-main"><a href="<@ofbizUrl>product</@ofbizUrl>">产品功能</a></li>
      <li id="header-bar-main"><a href="<@ofbizUrl>solutions</@ofbizUrl>">解决方案</a></li>
      <li id="header-bar-main"><a href="<@ofbizUrl>documents</@ofbizUrl>">培训文档</a></li>
      <li id="header-bar-main"><a href="<@ofbizUrl>documents</@ofbizUrl>">支持与服务</a></li>
      <li id="header-bar-main"><a href="<@ofbizUrl>downloads</@ofbizUrl>">免费下载</a></li>  
      <li id="header-bar-contactus">
        <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
          <a href="<@ofbizUrl>contactus</@ofbizUrl>">${uiLabelMap.CommonContactUs}</a></li>
        <#else>
          <a href="<@ofbizUrl>AnonContactus</@ofbizUrl>">${uiLabelMap.CommonContactUs}</a></li>
        </#if>
        -->
      <@loopSubContent contentId="WebStoreCONTENT"  viewIndex=0 viewSize=9999 orderBy="contentName">
        <li id="header-bar-main">
          <!-- a href="<@ofbizUrl>showcontenttree?contentId=${subContentId}</@ofbizUrl>">${content.contentName}</a>  -->
          <a href="cms/${subContentId}">${content.contentName}</a>
        </li>
      </@loopSubContent> 
    </ul>
    <ul id="right-links">
      <!-- NOTE: these are in reverse order because they are stacked right to left instead of left to right -->
      <#if !userLogin?has_content || (userLogin.userLoginId)?if_exists != "anonymous">
        
        <!-- li id="header-bar-ListQuotes"><a href="<@ofbizUrl>ListQuotes</@ofbizUrl>">${uiLabelMap.OrderOrderQuotes}</a></li>
        <li id="header-bar-ListRequests"><a href="<@ofbizUrl>ListRequests</@ofbizUrl>">${uiLabelMap.OrderRequests}</a></li>
        <li id="header-bar-editShoppingList"><a href="<@ofbizUrl>editShoppingList</@ofbizUrl>">${uiLabelMap.EcommerceShoppingLists}</a></li>
        <li id="header-bar-orderhistory"><a href="<@ofbizUrl>orderhistory</@ofbizUrl>">${uiLabelMap.EcommerceOrderHistory}</a></li>  -->
      	<#if userLogin?has_content && userLogin.userLoginId != "anonymous">
        	<li id="header-bar-logout"><a href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a></li>
        	<li id="header-bar-ListMessages"><a href="<@ofbizUrl>messagelist</@ofbizUrl>">${uiLabelMap.CommonMessages}</a></li>
        	<li id="header-bar-viewprofile"><a href="<@ofbizUrl>viewprofile</@ofbizUrl>">${uiLabelMap.CommonProfile}</a></li>
      	<#else/>
        <li id="header-bar-register"><a href="<@ofbizUrl>newcustomer</@ofbizUrl>">${uiLabelMap.EcommerceRegister}</a></li>
        <li id="header-bar-login"><a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a></li>
        <@loopSubContent contentId="BigitHeadRightBar"  viewIndex=0 viewSize=9999 orderBy="contentName">
        	<li id="header-bar-main">
          		<!-- a href="<@ofbizUrl>showcontenttree?contentId=${subContentId}</@ofbizUrl>">${content.contentName}</a> -->
          		<a href="<@ofbizUrl>ViewSimpleContent?contentId=${subContentId}</@ofbizUrl>">${content.contentName}</a>  -->
          		<a href="content/control/ViewSimpleContent?contentId=${subContentId}">${content.contentName}:${content.subContentId}</a>
        	</li>
      	</@loopSubContent> 
      </#if>
      </#if>
    </ul>
  </div>