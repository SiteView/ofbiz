<#-- code behind is getPortalPages.groovy -->

<div id="bigit-content-head" class="bigit-content-subtitle">

	<ul id="bigit-navigation-trail">
		<li><a class="" href="/">Ê×Ò³</a></li>
		<#list contentAncestorList?if_exists as contentAncestor>
			<li>
				<a class="" href="<@ofbizUrl>showPortalPage?portalPageId=${contentAncestor.contentId}&contentId=${contentAncestor.contentId}</@ofbizUrl>">${contentAncestor.contentName}</a>
			</li>	
		</#list>
	</ul>
	
	<h1>${title?if_exists}</h1>
	<p><em>${rootContent.description?if_exists}</em></p>

</div>