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
    </ul>
</div>

