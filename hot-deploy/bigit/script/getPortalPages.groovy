import org.ofbiz.widget.PortalPageWorker;
import org.ofbiz.content.content.ContentWorker;

def contentAncestorIdList = []
def contentAncestorList = []
def rootContentAttrList = []

contentIdPara = request.getParameter("contentId");
portalPageId = request.getParameter("portalPageId");

context.portalPage= PortalPageWorker.getPortalPage(portalPageId,context);
context.portalPages= PortalPageWorker.getPortalPages(portalPageId,context);

contentId= (contentIdPara==null) ? portalPage.rootContentId:contentIdPara;

ContentWorker.getContentAncestryAll(delegator, contentId, "DOCUMENT", "from", contentAncestorIdList);

contentAncestorIdList.each { contentAncestorId -> contentAncestorList.add(ContentWorker.getContentCache(delegator, contentAncestorId)) }

context.contentAncestorList = contentAncestorList;
context.content = ContentWorker.getContentCache(delegator, contentId);
context.rootContent = ContentWorker.getContentCache(delegator, portalPage.rootContentId);

context.rootContentAttrList = delegator.findByAnd("ContentAttribute", [contentId : portalPage.rootContentId], null, true);


attrList = delegator.findByAnd("ContentAttribute", [contentId : portalPage.rootContentId, attrName : "title"], null, true);
title = null;
if (attrList) {
	contentAttribute = attrList.get(0);
	context.title  = contentAttribute.attrValue;
}

attrList = delegator.findByAnd("ContentAttribute", [contentId : portalPage.rootContentId, attrName : "subtitle"], null, true);
subtitle = null;
if (attrList) {
	contentAttribute = attrList.get(0);
	context.subtitle  = contentAttribute.attrValue;
}



