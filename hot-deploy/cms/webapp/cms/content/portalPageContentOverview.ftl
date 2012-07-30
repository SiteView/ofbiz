
<#-- code behind is getPortalPages.groovy -->

<div id="bigit-content-head" class="bigit-content-subtitle">

	<ul id="bigit-navigation-trail">
		<li><a class="" href="/">首页</a></li>
		<#list contentAncestorList?if_exists as contentAncestor>
			<li>
				<a class="" href="<@ofbizUrl>showPortalPage?portalPageId=${portalPageId}&contentId=${contentAncestor.contentId}</@ofbizUrl>">${contentAncestor.contentName}</a>
			</li>	
		</#list>
	</ul>
	
	<h1>云中心运营支撑系统</h1>
	<p><em>统一的IT资源和业务管理，将数据中心升级为云中心</em></p>
</div>

 

<div id="bigit-leadspace">
	<@loopSubContent contentId=portalPageId?if_exists viewIndex=0 viewSize=9999 contentAssocTypeId="IMAGE_THUMBNAIL" mapKey="main" orderBy="sequenceNum">
			<@renderContentAsText contentId="${subContentId}" ignoreTemplate="true" editRequestName="/content/control/EditElectronicText"/>
		<#-- <a href="https://www14.software.ibm.com/webapp/iwm/web/signup.do?source=swg-ilog&amp;S_PKG=500008618&amp;S_TACT=109KA5EW&amp;S_CMP=web_ibm_ws_ilg-vis_hero_java-ov"></a> -->
	</@loopSubContent>	
</div>

<div class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h2">Featured Product</li>
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
                    			<@renderContentAsText contentId="${subContentId}" ignoreTemplate="true"  editRequestName="/content/control/EditElectronicText"/>
                    			<@checkPermission entityOperation="_ADMIN" targetOperation="CONTENT_ADMIN" >
                    				<a href="/content/control/EditElectronicText?dataResourceId=${subContentId}">Edit</a>
                    			</@checkPermission>
                    		</a>	
               			</#if>
               			
      				</@loopSubContent> 
					</span><strong><a href="<@ofbizUrl>showPortalPage?portalPageId=${page.portalPageId}&contentId=${page.rootContentId}</@ofbizUrl>">${page.rootContentId}</a>
					</strong><br>
					<@loopSubContent contentId=page.rootContentId?if_exists viewIndex=0 viewSize=9999 contentAssocTypeId="SUMMARY" orderBy="sequenceNum">
                		<#if (subContentId?has_content)>
                    		<@renderContentAsText contentId="${subContentId}" ignoreTemplate="true"  editRequestName="/content/control/EditElectronicText"/>
                    		<@checkPermission entityOperation="_ADMIN" targetOperation="CONTENT_ADMIN" >
                    			<a href="/content/control/EditElectronicText?dataResourceId=${subContentId}">Edit</a>
                    		</@checkPermission>
               		 	</#if>
      				</@loopSubContent>
      			</p>
			</div>
		</div>
	</#if>
</#list>	


