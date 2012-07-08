<#assign topMenus = delegator.findList("Menu", null, null, sequenceSort, null, true)>

<PortalPage portalPageId="${webSiteId}" sequenceNum="0" portalPageName="Ê×Ò³" description="The default ${webSiteId} portal page" ownerUserLoginId="_NA_"/>
   

<#list topMenus as topMenu>
	<!-- ---------------------- -->
    <!-- PortalPortlet Seed Data: ${topMenu.menuId} pages -->    
    <DataResource dataResourceId="${topMenu.menuId}" dataResourceTypeId="ELECTRONIC_TEXT" dataTemplateTypeId="FTL" isPublic="Y"/>  
    <Content contentId="${topMenu.menuId}" contentName="${topMenu.menuName}" contentTypeId="DOCUMENT" dataResourceId="${topMenu.menuId}" description="${topMenu.description?if_exists}"/>
    <ElectronicText dataResourceId="${topMenu.menuId}">
        <textData><![CDATA[This the main page for ${topMenu.menuId}.]]></textData>
    </ElectronicText>
    <ContentAttribute attrName="title" attrValue="${topMenu.title?if_exists}" contentId="${topMenu.menuId}"/>
    
    <!-- with a portalPageId, we should be able to find all its related information about this portalPage and its embedded portalPage  
    	PortalPageWorker.java: get PortalPageColumn, PortalPagePortlet, PortletAttribute
    							if portalPortletId="GenericPortalPage", get the embedded portalPage
    -->
    
    <PortalPage portalPageId="${topMenu.menuId}" superPortalPageId="IBM3COL" description="${topMenu.description?if_exists}" ownerUserLoginId="_NA_" parentPortalPageId="${webSiteId}" portalPageName="${topMenu.menuName}" rootContentId="${topMenu.menuId}" sequenceNum="${topMenu.sequenceNum}"/> 
    <PortletAttribute portalPageId="${topMenu.menuId}" portalPortletId="GenericPortalPage" portletSeqId="main02" attrName="portalPageId" attrValue="${topMenu.menuId}_SUB" /> 
    
    <PortalPage portalPageId="${topMenu.menuId}_SUB" description="${topMenu.description?if_exists}" ownerUserLoginId="_NA_" parentPortalPageId="" portalPageName="${topMenu.menuName}" rootContentId="${topMenu.menuId}" sequenceNum="100"/> 
    <PortalPageColumn portalPageId="${topMenu.menuId}_SUB" columnSeqId="mainarea"/> 
    <PortalPageColumn portalPageId="${topMenu.menuId}_SUB" columnSeqId="right" columnWidthPixels="170"/>
    
    <PortalPagePortlet portalPageId="${topMenu.menuId}_SUB" columnSeqId="mainarea" portalPortletId="GenericScreen" portletSeqId="main01" sequenceNum="8"/> 
    <PortletAttribute portalPageId="${topMenu.menuId}_SUB" portalPortletId="GenericScreen" portletSeqId="main01" attrName="screenLocation" attrValue="component://bigit/widget/ContentScreens.xml" /> 
    <PortletAttribute portalPageId="${topMenu.menuId}_SUB" portalPortletId="GenericScreen" portletSeqId="main01" attrName="screenName" attrValue="subPortalPageOverview" /> 
    
    <PortalPagePortlet portalPageId="${topMenu.menuId}_SUB" columnSeqId="right" portalPortletId="GenericScreen" portletSeqId="right01" sequenceNum="8"/>     
    <PortletAttribute portalPageId="${topMenu.menuId}_SUB" portalPortletId="GenericScreen" portletSeqId="right01" attrName="screenLocation" attrValue="component://bigit/widget/ForumScreens.xml" /> 
    <PortletAttribute portalPageId="${topMenu.menuId}_SUB" portalPortletId="GenericScreen" portletSeqId="right01" attrName="screenName" attrValue="forums" /> 
    
    
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

		    <PortalPage portalPageId="${subMenu.subMenuId}" description="${subMenu.description?if_exists}" ownerUserLoginId="_NA_" parentPortalPageId="${subMenu.menuId}" portalPageName="${subMenu.menuName}" rootContentId="${subMenu.subMenuId}" sequenceNum="100"/> 
		    <PortalPageColumn portalPageId="${subMenu.subMenuId}" columnSeqId="left" columnWidthPixels="180"/>
		    <PortalPageColumn portalPageId="${subMenu.subMenuId}" columnSeqId="mainarea"/> 
		    <PortalPageColumn portalPageId="${subMenu.subMenuId}" columnSeqId="right" columnWidthPixels="170"/> 
		
		    <PortalPagePortlet portalPageId="${subMenu.subMenuId}" columnSeqId="left" portalPortletId="GenericScreen" portletSeqId="left01" sequenceNum="10"/> 
		    <PortalPagePortlet portalPageId="${subMenu.subMenuId}" columnSeqId="left" portalPortletId="GenericScreen" portletSeqId="left02" sequenceNum="20"/> 
		    <PortletAttribute portalPageId="${subMenu.subMenuId}" portalPortletId="GenericScreen" portletSeqId="left01" attrName="screenLocation" attrValue="component://bigit/widget/ContentScreens.xml" /> 
		    <PortletAttribute portalPageId="${subMenu.subMenuId}" portalPortletId="GenericScreen" portletSeqId="left01" attrName="screenName" attrValue="portalPageContentList" /> 
		    <PortletAttribute portalPageId="${subMenu.subMenuId}" portalPortletId="GenericScreen" portletSeqId="left02" attrName="screenLocation" attrValue="component://bigit/widget/ForumScreens.xml" /> 
		    <PortletAttribute portalPageId="${subMenu.subMenuId}" portalPortletId="GenericScreen" portletSeqId="left02" attrName="screenName" attrValue="forums" /> 
		
		    <PortalPagePortlet portalPageId="${subMenu.subMenuId}" columnSeqId="mainarea" portalPortletId="GenericScreen" portletSeqId="main01" sequenceNum="10"/> 
		    <PortletAttribute portalPageId="${subMenu.subMenuId}" portalPortletId="GenericScreen" portletSeqId="main01" attrName="screenLocation" attrValue="component://bigit/widget/ContentScreens.xml" /> 
		    <PortletAttribute portalPageId="${subMenu.subMenuId}" portalPortletId="GenericScreen" portletSeqId="main01" attrName="screenName" attrValue="subPortalPageHead" /> 
		    
		<#assign contentList = subMenu.getRelated("includeSubMenuContent",sequenceSort)?if_exists>
		<#list contentList as content>
			<DataResource dataResourceId="${content.subMenuId}_${content.contentId}" dataResourceTypeId="ELECTRONIC_TEXT" dataTemplateTypeId="FTL" isPublic="Y"/>  
    		<Content contentId="${content.subMenuId}_${content.contentId}" contentName="${content.contentName}" contentTypeId="DOCUMENT" dataResourceId="${content.subMenuId}_${content.contentId}" description="${content.description}" />
    		<ElectronicText dataResourceId="${content.subMenuId}_${content.contentId}">
        			<textData><![CDATA[This the main page for ${content.subMenuId}_${content.contentId}.]]></textData>
    		</ElectronicText>
	
			<ContentAssoc contentId="${content.subMenuId}" contentAssocTypeId="SUB_CONTENT" contentIdTo="${content.subMenuId}_${content.contentId}" sequenceNum="${content.sequenceNum}" mapKey="${content.contentId}" fromDate="2001-01-01 00:00:00"/> 
		</#list>	
	</#list>  <#-- -->
</#list>



