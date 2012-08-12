import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*

context.sequenceSort = ['sequenceNum'];

//get the menu lists

context.topMenus = delegator.findList("Menu", EntityCondition.makeCondition([webSiteId : request.getParameter("webSiteId")]), null, ['sequenceNum'], null, true);

//get the root content
webSiteContents = delegator.findList("WebSiteContent", EntityCondition.makeCondition([webSiteId : request.getParameter("webSiteId"), webSiteContentTypeId:"PUBLISH_POINT"]), null, null, null, true);
context.webSiteContent = webSiteContents[0]; 




//surveyWrapper = new SurveyWrapper(delegator, surveyResponseId, partyId, surveyId, null);
//surveyWrapper.setEdit(true);
//
//templateUrl = UtilURL.fromOfbizHomePath("hot-deploy/bigit/webapp/bigit/WebSiteTemplate.ftl");
//if (templateUrl) {
//	writer = new StringWriter();
//	surveyWrapper.render(templateUrl, writer);
//	context.surveyString = writer.toString();
//}




