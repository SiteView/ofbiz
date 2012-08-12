import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL
import org.ofbiz.base.util.template.FreeMarkerWorker;

import freemarker.template.Configuration;
import freemarker.template.Template;

context.sequenceSort = ['sequenceNum'];

//get the menu lists

topMenus = delegator.findList("Menu", EntityCondition.makeCondition([webSiteId : request.getParameter("webSiteId")]), null, ['sequenceNum'], null, true);

//get the root content
webSiteContents = delegator.findList("WebSiteContent", EntityCondition.makeCondition([webSiteId : request.getParameter("webSiteId"), webSiteContentTypeId:"PUBLISH_POINT"]), null, null, null, true);
context.webSiteContent = webSiteContents[0]; 


//<PortalPage portalPageId="${webSiteContent.webSiteId}" sequenceNum="0" portalPageName="Ê×Ò³" description="The default ${webSiteContent.webSiteId} portal page" ownerUserLoginId="_NA_"/>

//surveyWrapper = new SurveyWrapper(delegator, null, null, null, null);
//surveyWrapper.setEdit(true);
//

Map<String, Object> templateContext = null;

if (templateContext == null) {
	templateContext = FastMap.newInstance();
}

templateContext.put("webSiteId", request.getParameter("webSiteId"));
templateContext.put("topMenus",  topMenus);
templateContext.put("webSiteContent",  webSiteContents[0]);
templateContext.put("sequenceSort", ['sequenceNum']);

templateUrl = UtilURL.fromOfbizHomePath("hot-deploy/cms/webapp/cms/content/createWebSite.ftl");
if (templateUrl) {
	writer = new StringWriter();
	Configuration config = FreeMarkerWorker.getDefaultOfbizConfig();
	
	Template template = null;
	try {
			InputStream templateStream = templateUrl.openStream();
			InputStreamReader templateReader = new InputStreamReader(templateStream);
			template = new Template(templateUrl.toExternalForm(), templateReader, config);
		} catch (IOException e) {
			Debug.logError(e, "Unable to get template from URL :" + templateUrl.toExternalForm(), module);
		}
//	surveyWrapper.render(templateUrl, writer);
	FreeMarkerWorker.renderTemplate(template, templateContext, writer);
	context.fulltext = writer.toString();
}




