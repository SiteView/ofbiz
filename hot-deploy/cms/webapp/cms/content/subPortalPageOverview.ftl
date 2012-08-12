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
      <li class="h2">Ϊʲôѡ�� SITEVIEW ��</li>
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
				<p>
					<span class="bigit-inset-img-caption">
						<a href="http://www-01.ibm.com/software/integration/visualization/jviews/enterprise/">
						<img border="0" height="105" width="170" src="http://www-01.ibm.com/software/integration/visualization/java/images/jviews_140x78.jpg" alt=""></a>
					</span>
					<strong><a href="http://www-01.ibm.com/software/integration/visualization/jviews/enterprise/">SITEVIEW - ȫ��λIT��άƽ̨</a></strong>
					<br>����${Static["java.lang.Integer"].valueOf(nowTimestamp?string("yyyy"))-2000}��ĳ����з�Ͷ��ͼ�ǧ�ͻ���ʵ��ʹ������Ļ��ۣ�SITEVIEW����ƽ̨���������豸�������������Ӧ�á������նˡ���IT����,�������IT����Ч�ʣ����͹��Ϻ���Ӫ���գ���Ϊ�����������Ϲ����û�ʹ��ϰ�ߵ�IT��Ӫ����ƽ̨�� 
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
	

