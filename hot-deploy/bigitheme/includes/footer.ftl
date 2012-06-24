<div id="footer">
    <ul>
        <li class="first">${nowTimestamp?datetime?string.short} - <a href="<@ofbizUrl>ListTimezones</@ofbizUrl>">${timeZone.getDisplayName(timeZone.useDaylightTime(), Static["java.util.TimeZone"].LONG, locale)}</a></li>
        <li><a href="<@ofbizUrl>ListLocales</@ofbizUrl>">${locale.getDisplayName(locale)}</a></li>
        <#if userLogin?has_content>
        	<li class="last"><a href="<@ofbizUrl>ListVisualThemes</@ofbizUrl>">${uiLabelMap.CommonVisualThemes}</a></li>
        </#if>	
    </ul>
  <p>
  ${uiLabelMap.CommonCopyright} (c) 2001-${nowTimestamp?string("yyyy")} <a href="/" target="_blank">BIGIT.COM</a>. 
  ${uiLabelMap.CommonPoweredBy} <a href="/" target="_blank">BIGIT CMS</a>
  </p>
</div>
</div>
<#if layoutSettings.VT_FTR_JAVASCRIPT?has_content>
  <#list layoutSettings.VT_FTR_JAVASCRIPT as javaScript>
    <script type="text/javascript" src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
  </#list>
</#if>
</body>
</html>
