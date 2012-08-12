<#-- code behind: getPortalPages.groovy -->

<#assign portalPageId = requestParameters.portalPageId?if_exists>
<#assign selectedContentId = requestParameters.contentId?if_exists>

<div id="bigit-navigation">
	<ul id="bigit-primary-links">
		<li class="" id="bigit-overview"><a href="<@ofbizUrl>showPortalPage?portalPageId=${portalPageId}</@ofbizUrl>">${portalPage.portalPageName}</a></li>
		<#list portalPages as page>
        	<li>
                <#if page.portalPageId != portalPageId>
                	<a class="" href="<@ofbizUrl>showPortalPage?portalPageId=${page.portalPageId}&parentPortalPageId=${portalPageId}</@ofbizUrl>">${page.portalPageName}</a>
                </#if>	
        	</li>
        </#list>
       
       <#-- if no portal page, use the following -->
      <@loopSubContent contentId=portalPage.rootContentId?if_exists viewIndex=0 viewSize=9999 contentAssocTypeId="SUB_CONTENT" orderBy="sequenceNum">
        <li>
        	<#if (portalPages.size() - 1 == 0)>
	            <#if selectedContentId == subContentId>
	                <a class="selected" href="<@ofbizUrl>showPortalPage?portalPageId=${portalPageId}&contentId=${subContentId}</@ofbizUrl>">${content.contentName}</a>
	            <#else>
	            	<a class="" href="<@ofbizUrl>showPortalPage?portalPageId=${portalPageId}&contentId=${subContentId}</@ofbizUrl>">${content.contentName}</a>
	            </#if>                
            </#if>                
        </li>
      </@loopSubContent> 
    </ul>
</div>