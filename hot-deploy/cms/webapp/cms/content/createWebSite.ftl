<#-- assign topMenus = delegator.findList("Menu", null, null, sequenceSort, null, true) -->

<!-- one webSiteId is mapped to one components in web.xml -->
<PortalPage portalPageId="${webSiteContent.webSiteId}" sequenceNum="0" portalPageName="Ê×Ò³" description="The default ${webSiteContent.webSiteId} portal page" ownerUserLoginId="_NA_"/>
   

<#list topMenus as topMenu>
	<!-- ---------------------- -->
    <!-- PortalPortlet Seed Data: ${topMenu.menuId} pages -->    
    <DataResource dataResourceId="${topMenu.menuId}" dataResourceTypeId="ELECTRONIC_TEXT" dataTemplateTypeId="FTL" isPublic="Y"/>  
    <Content contentId="${topMenu.menuId}" contentName="${topMenu.menuName}" contentTypeId="DOCUMENT" dataResourceId="${topMenu.menuId}" description="${topMenu.description?if_exists}"/>
    <ElectronicText dataResourceId="${topMenu.menuId}">
        <textData><![CDATA[This the main page for ${topMenu.menuId}.]]></textData>
    </ElectronicText>
    <ContentAttribute attrName="title" attrValue="${topMenu.title?if_exists}" contentId="${topMenu.menuId}"/>
    <ContentAssoc contentId="${webSiteContent.contentId}" contentIdTo="${topMenu.menuId}" contentAssocTypeId="SUB_CONTENT" sequenceNum="${topMenu.sequenceNum}" mapKey="${topMenu.menuId}" fromDate="2001-05-13 12:00:00.000"/>
  
	<#assign subMenuList = topMenu.getRelated("includeSubMenu",sequenceSort)>
	<#list subMenuList as subMenu>
    		<!-- PortalPortlet Seed Data: ${subMenu.subMenuId} pages -->    
    		
    		<DataResource dataResourceId="${subMenu.subMenuId}" dataResourceTypeId="ELECTRONIC_TEXT" dataTemplateTypeId="FTL" isPublic="Y"/>  
		    <Content contentId="${subMenu.subMenuId}" contentName="${subMenu.menuName}" contentTypeId="DOCUMENT" dataResourceId="${subMenu.subMenuId}" description="${subMenu.description?if_exists}"/>
		    <ElectronicText dataResourceId="${subMenu.subMenuId}">
		        <textData><![CDATA[This the main page for ${subMenu.subMenuId}.]]></textData>
		    </ElectronicText>
    		<ContentAttribute attrName="title" attrValue="${subMenu.title?if_exists}" contentId="${subMenu.subMenuId}"/> 
			<ContentAssoc contentId="${subMenu.menuId}" contentAssocTypeId="SUB_CONTENT" contentIdTo="${subMenu.subMenuId}" mapKey="${subMenu.subMenuId}" sequenceNum="${subMenu.sequenceNum?if_exists}" fromDate="2001-01-01 00:00:00"/> 
		    
		    <!-- creating the content -->
			<#assign contentList = subMenu.getRelated("includeSubMenuContent",sequenceSort)?if_exists>
			<#list contentList as content>
				<DataResource dataResourceId="${content.subMenuId}_${content.contentId}" dataResourceTypeId="ELECTRONIC_TEXT" dataTemplateTypeId="FTL" isPublic="Y"/>  
	    		<Content contentId="${content.subMenuId}_${content.contentId}" contentName="${content.contentName}" contentTypeId="DOCUMENT" dataResourceId="${content.subMenuId}_${content.contentId}" description="${content.description}" />
	    		<ElectronicText dataResourceId="${content.subMenuId}_${content.contentId}">
	    				<!-- Should read from Words XML to fill in the textData, based on the ${content.subMenuId}_${content.contentId}. -->
	        			<textData><![CDATA[This the main page for ${content.subMenuId}_${content.contentId}.]]></textData>
	    		</ElectronicText>
		
				<ContentAssoc contentId="${content.subMenuId}" contentAssocTypeId="SUB_CONTENT" contentIdTo="${content.subMenuId}_${content.contentId}" sequenceNum="${content.sequenceNum}" mapKey="${content.contentId}" fromDate="2001-01-01 00:00:00"/>
				
				<#-- creating the sub content for each tree node of the content, e.g. the sub content of feature like title, sub title content tail: subPortalPageHead.ftl
						title: subPortalPageHead.ftl
						subtitle: subPortalPageHead.ftl
						
						image
						lead content: featured content
						details: a list or sub_content ? with screen-let title and body, a container
						
						summary
						related content: related screenshots, related download, related forum and blog, related products
						descriptions	
				-->
				 
			</#list>	
			
			<DataResource dataResourceId="${subMenu.subMenuId}_summary" dataResourceTypeId="ELECTRONIC_TEXT" dataTemplateTypeId="FTL" isPublic="Y"/>  
	    	<Content contentId="${subMenu.subMenuId}_summary" contentName="${subMenu.menuName}_summary" contentTypeId="DOCUMENT" dataResourceId="${subMenu.subMenuId}_summary" description="${subMenu.description?if_exists} summary" />
	    	<ElectronicText dataResourceId="${subMenu.subMenuId}_summary">
	        		<textData><![CDATA[This the main page for ${subMenu.subMenuId}_summary.]]></textData>
	    	</ElectronicText>		
			<ContentAssoc contentId="${subMenu.subMenuId}" contentAssocTypeId="SUMMARY" contentIdTo="${subMenu.subMenuId}_summary" sequenceNum="10" mapKey="summary" fromDate="2001-01-01 00:00:00"/> 

			<DataResource dataResourceId="${subMenu.subMenuId}_detail" dataResourceTypeId="ELECTRONIC_TEXT" dataTemplateTypeId="FTL" isPublic="Y"/>  
	    	<Content contentId="${subMenu.subMenuId}_detail" contentName="${subMenu.menuName}_detail" contentTypeId="DOCUMENT" dataResourceId="${subMenu.subMenuId}_detail" description="${subMenu.description?if_exists} detail" />
	    	<ElectronicText dataResourceId="${subMenu.subMenuId}_detail">
	        		<textData><![CDATA[This the main page for ${subMenu.subMenuId}_summary.]]></textData>
	    	</ElectronicText>		
			<ContentAssoc contentId="${subMenu.subMenuId}" contentAssocTypeId="DESCRIPTION" contentIdTo="${subMenu.subMenuId}_detail" sequenceNum="10" mapKey="detail" fromDate="2001-01-01 00:00:00"/> 

	</#list>
</#list>


<#list topMenus as topMenu>
	<!-- ---------------------- -->
    <!-- PortalPortlet Seed Data: ${topMenu.menuId} pages -->    
	<PortalPage portalPageId="${topMenu.menuId}" superPortalPageId="${topMenu.superPortalPage}" description="${topMenu.description?if_exists}" ownerUserLoginId="_NA_" parentPortalPageId="${webSiteContent.webSiteId}" portalPageName="${topMenu.menuName}" rootContentId="${topMenu.menuId}" sequenceNum="${topMenu.sequenceNum}"/> 
    <PortletAttribute portalPageId="${topMenu.menuId}" portalPortletId="GenericPortalPage" portletSeqId="main02" attrName="portalPageId" attrValue="${topMenu.menuId}_SUB" /> 
    <PortletAttribute portalPageId="${topMenu.menuId}" portalPortletId="GenericPortalPage" portletSeqId="main02" attrName="pageId" attrValue="${topMenu.menuId}_SUB" /> 
    <PortletAttribute portalPageId="${topMenu.menuId}" portalPortletId="GenericPortalPage" attrName="submit" attrValue="Submit*" portletSeqId="main02"/> 
    
    <PortalPage portalPageId="${topMenu.menuId}_SUB" superPortalPageId="IBM3COL_SUB" description="${topMenu.description?if_exists}" ownerUserLoginId="_NA_" parentPortalPageId="" portalPageName="${topMenu.menuName}" rootContentId="${topMenu.menuId}" sequenceNum="100"/> 
    
	<#assign subMenuList = topMenu.getRelated("includeSubMenu",sequenceSort)>
	<#list subMenuList as subMenu>
    		<!-- PortalPortlet Seed Data: ${subMenu.subMenuId} pages -->    
    		<#if subMenu.portalPageId?exists>
				<PortalPage portalPageId="${subMenu.portalPageId}" superPortalPageId="${subMenu.superPortalPage}" description="${subMenu.description?if_exists}" ownerUserLoginId="_NA_" parentPortalPageId="${subMenu.menuId}" portalPageName="${subMenu.menuName}" rootContentId="${subMenu.subMenuId}" sequenceNum="100"/> 
			    <PortletAttribute portalPageId="${subMenu.portalPageId}" portalPortletId="GenericPortalPage" portletSeqId="main02" attrName="portalPageId" attrValue="${subMenu.subMenuId}_SUB" /> 
	    		<PortletAttribute portalPageId="${subMenu.portalPageId}" portalPortletId="GenericPortalPage" portletSeqId="main02" attrName="pageId" attrValue="${subMenu.subMenuId}_SUB" /> 
	    		<PortletAttribute portalPageId="${subMenu.portalPageId}" portalPortletId="GenericPortalPage" attrName="submit" attrValue="Submit*" portletSeqId="main02"/> 
			    <PortletAttribute portalPageId="${subMenu.portalPageId}" portalPortletId="GenericScreen" portletSeqId="left01" attrName="screenName" attrValue="subPortalPageList" /> 
	
	    		<PortalPage portalPageId="${subMenu.subMenuId}_SUB" superPortalPageId="IBM3COL_SUB" description="${subMenu.description?if_exists}" ownerUserLoginId="_NA_" parentPortalPageId="" portalPageName="${subMenu.menuName}" rootContentId="${subMenu.subMenuId}" sequenceNum="100"/>
    		</#if>
		    
	</#list>
</#list>






