import org.ofbiz.content.survey.SurveyWrapper
import org.ofbiz.base.util.*

context.sequenceSort = ['sequenceNum'];

//should be read from the calling component, set for now
//should be set in web.xml
context.webSiteId  = 'BigitWeb';


//surveyWrapper = new SurveyWrapper(delegator, surveyResponseId, partyId, surveyId, null);
//surveyWrapper.setEdit(true);
//
//templateUrl = UtilURL.fromOfbizHomePath("hot-deploy/bigit/webapp/bigit/WebSiteTemplate.ftl");
//if (templateUrl) {
//	writer = new StringWriter();
//	surveyWrapper.render(templateUrl, writer);
//	context.surveyString = writer.toString();
//}




