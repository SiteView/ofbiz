<#-- code behind is getPortalPages.groovy -->

<#-- image, featured, detail lists -->

<div id="bigit-leadspace">
	<@loopSubContent contentId=portalPageId?if_exists viewIndex=0 viewSize=9999 contentAssocTypeId="IMAGE_THUMBNAIL" mapKey="main" orderBy="sequenceNum">
			<@renderContentAsText contentId="${subContentId}" ignoreTemplate="true"/>
		<#-- <a href="https://www14.software.ibm.com/webapp/iwm/web/signup.do?source=swg-ilog&amp;S_PKG=500008618&amp;S_TACT=109KA5EW&amp;S_CMP=web_ibm_ws_ilg-vis_hero_java-ov"></a> -->
	</@loopSubContent>	
</div>


<#if featuredContentId?if_exists> <@renderContentAsText contentId="${featuredContentId.contentId}" ignoreTemplate="true"/> </#if>

<div class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h2">为什么选择 SITEVIEW ？</li>
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
				<p>
					<span class="bigit-inset-img-caption">
						<a href="http://www-01.ibm.com/software/integration/visualization/jviews/enterprise/">
						<img border="0" height="105" width="170" src="http://www-01.ibm.com/software/integration/visualization/java/images/jviews_140x78.jpg" alt=""></a>
					</span>
					<strong><a href="http://www-01.ibm.com/software/integration/visualization/jviews/enterprise/">SITEVIEW - 全方位IT运维平台</a></strong>
					<br>经历${Static["java.lang.Integer"].valueOf(nowTimestamp?string("yyyy"))-2000}年的持续研发投入和几千客户的实际使用需求的积累，SITEVIEW管理平台覆盖网络设备、服务器、软件应用、桌面终端、和IT服务,大幅提升IT管理效率，降低故障和运营风险，成为技术精深，最符合国内用户使用习惯的IT运营管理平台。 
				</p>
  </div>
</div>


<#list portalPages as page> 

	<#if page.portalPageId != portalPageId>
		<div class="screenlet"> 
		  <div class="screenlet-title-bar">
		    <ul>
		      <li class="h2"><a href="<@ofbizUrl>showPortalPage?portalPageId=${page.portalPageId}&contentId=${page.rootContentId}</@ofbizUrl>"><u>${page.portalPageName}</u></a></li>
		    </ul>
		    <br class="clear"/>
		  </div>
			<div class="screenlet-body">
				<p><span class="bigit-inset-img-caption">
					<@loopSubContent contentId=page.rootContentId?if_exists viewIndex=0 viewSize=9999 contentAssocTypeId="IMAGE_THUMBNAIL" mapKey="summary" orderBy="sequenceNum">
                		<#if (subContentId?has_content)>
                    		<a href="<@ofbizUrl>showPortalPage?portalPageId=${page.portalPageId}&contentId=${page.rootContentId}</@ofbizUrl>">
                    			<@renderContentAsText contentId="${subContentId}" ignoreTemplate="true" editRequestName="/content/control/EditElectronicText?dataResourceId=${subContentId}"/>
                    		</a>	
               			</#if>               			
      				</@loopSubContent> 
					</span><strong><a href="<@ofbizUrl>showPortalPage?portalPageId=${page.portalPageId}&contentId=${page.rootContentId}</@ofbizUrl>">${page.rootContentId}</a>
					</strong><br>
					<@loopSubContent contentId=page.rootContentId?if_exists viewIndex=0 viewSize=9999 contentAssocTypeId="SUMMARY" orderBy="sequenceNum">
						${subContentId}, ${page.rootContentId}
                		<#if (subContentId?has_content)>
                    		<@renderContentAsText contentId="${subContentId}" ignoreTemplate="true" editRequestName="${editRequestName}"/>
               		 	</#if>
      				</@loopSubContent>
      			</p>
			</div>
		</div>
	</#if>
	
</#list>	

<#if rootContent.contentId != pageContent.contentId >
	    <br class="clear"/>
		<@renderContentAsText contentId="${pageContent.contentId}" ignoreTemplate="true"/>
		
		<@loopSubContent contentId=portalPage.rootContentId?if_exists viewIndex=0 viewSize=9999 contentAssocTypeId="TREE_CHILD" mapKey="detail" orderBy="sequenceNum">
	        <li>
	            <#if (subContentId?has_content)>
                    	<@renderContentAsText contentId="${subContentId}" ignoreTemplate="true" editRequestName="${editRequestName}"/> 
               	</#if>             
	        </li>
      </@loopSubContent>
</#if>
	

