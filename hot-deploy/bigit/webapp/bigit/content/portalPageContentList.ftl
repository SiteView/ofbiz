<#assign portalPageId = requestParameters.portalPageId?if_exists>
<#assign selectedContentId = requestParameters.contentId?if_exists>

<div id="bigit-navigation">
	<ul id="bigit-primary-links">
		<li class="" id="bigit-overview"><a href="<@ofbizUrl>showPortalPage?portalPageId=${portalPageId}</@ofbizUrl>">${portalPage.portalPageName}</a></li>
      <@loopSubContent contentId=portalPage.rootContentId?if_exists viewIndex=0 viewSize=9999 contentAssocTypeId="SUB_CONTENT" orderBy="sequenceNum">
        <li>
            <#if selectedContentId == subContentId>
                <a class="selected" href="<@ofbizUrl>showPortalPage?portalPageId=${portalPageId}&contentId=${subContentId}</@ofbizUrl>">${content.contentName}</a>
            <#else>
            	<a class="" href="<@ofbizUrl>showPortalPage?portalPageId=${portalPageId}&contentId=${subContentId}</@ofbizUrl>">${content.contentName}</a>
            </#if>                
        </li>
      </@loopSubContent>
    </ul>
</div>

