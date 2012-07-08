<#assign portalPageId = requestParameters.portalPageId?if_exists>
  
<div id="bigit-navigation">

	<ul id="bigit-primary-links">
		<li id="bigit-overview"><a href="<@ofbizUrl>showPortalPage?portalPageId=${portalPageId}</@ofbizUrl>">${uiLabelMap.ProductBrowseForums}</a></li>
	
      <#list forums as forum>
        <li>
          <a href="<@ofbizUrl>showforum?forumId=${forum.contentId}&portalPageId=${portalPageId}</@ofbizUrl>">${forum.contentName!forum.contentId}</a>
        </li>
      </#list>
    </ul>
</div>