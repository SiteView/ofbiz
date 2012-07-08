<#-- code behind is getPortalPages.groovy -->




<div id="bigit-leadspace">
	<@loopSubContent contentId=portalPageId?if_exists viewIndex=0 viewSize=9999 contentAssocTypeId="IMAGE_THUMBNAIL" mapKey="main" orderBy="sequenceNum">
			<@renderContentAsText contentId="${subContentId}" ignoreTemplate="true"/>
		<#-- <a href="https://www14.software.ibm.com/webapp/iwm/web/signup.do?source=swg-ilog&amp;S_PKG=500008618&amp;S_TACT=109KA5EW&amp;S_CMP=web_ibm_ws_ilg-vis_hero_java-ov"></a> -->
	</@loopSubContent>	
</div>

<div class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h2">ÎªÊ²Ã´Ñ¡Ôñ SITEVIEW £¿</li>
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
				<p><span class="bigit-inset-img-caption">
					<a href="http://www-01.ibm.com/software/integration/visualization/jviews/enterprise/">
					<img border="0" height="105" width="170" src="http://www-01.ibm.com/software/integration/visualization/java/images/jviews_140x78.jpg" alt=""></a>
					</span><strong><a href="http://www-01.ibm.com/software/integration/visualization/jviews/enterprise/">IBM ILOG JViews Enterprise</a>
					</strong><br>Enables Java and Web developers to add a full spectrum of rich graphical </p>
  </div>
</div>

<#list portalPages as page>
	<#if page.portalPageId != portalPageId>
		<div class="screenlet"> 
		  <div class="screenlet-title-bar">
		    <ul>
		      <li class="h2"><a href="<@ofbizUrl>showPortalPage?portalPageId=${page.portalPageId}&contentId=${page.rootContentId}</@ofbizUrl>">${page.portalPageName}</a></li>
		    </ul>
		    <br class="clear"/>
		  </div>
			<div class="screenlet-body">
				<p><span class="bigit-inset-img-caption">
					<@loopSubContent contentId=page.rootContentId?if_exists viewIndex=0 viewSize=9999 contentAssocTypeId="IMAGE_THUMBNAIL" mapKey="summary" orderBy="sequenceNum">
                		<#if (subContentId?has_content)>
                    		<a href="<@ofbizUrl>showPortalPage?portalPageId=${page.portalPageId}&contentId=${page.rootContentId}</@ofbizUrl>">
                    			<@renderContentAsText contentId="${subContentId}" ignoreTemplate="true"/>
                    		</a>	
               			</#if>               			
      				</@loopSubContent> 
					</span><strong><a href="<@ofbizUrl>showPortalPage?portalPageId=${page.portalPageId}&contentId=${page.rootContentId}</@ofbizUrl>">${page.rootContentId}</a>
					</strong><br>
					<@loopSubContent contentId=page.rootContentId?if_exists viewIndex=0 viewSize=9999 contentAssocTypeId="SUMMARY" orderBy="sequenceNum">
                		<#if (subContentId?has_content)>
                    		<@renderContentAsText contentId="${subContentId}" ignoreTemplate="true"/>
               		 	</#if>
      				</@loopSubContent>
      			</p>
			</div>
		</div>
	</#if>
</#list>	


