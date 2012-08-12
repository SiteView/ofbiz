<div class="page-title"><span>${uiLabelMap.ContentImportToDataSource}</span></div>
<p>${uiLabelMap.ContentXMLImportInfo}</p>
<hr />


  <form method="post" action="<@ofbizUrl>createWebSiteAllContentsSvr?webSiteId=${webSiteId}</@ofbizUrl>">
    <br />
    <textarea rows="15" cols="85" name="fulltext">${fulltext?default("<entity-engine-xml>\n</entity-engine-xml>")}</textarea>
    <div class="button-bar"><input type="submit" value="${uiLabelMap.ContentImportText}"/></div>
  </form>
  <#if messages?exists>
      <hr />
      <h3>${uiLabelMap.ContentResults}:</h3>
      <#list messages as message>
          <p>${message}</p>
      </#list>
  </#if>
