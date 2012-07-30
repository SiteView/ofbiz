
<body>

    <div id="main">
      
        <div id="text" >
	       	<table cellpadding="10" cellspacing="10" align="center">
				<tr><td colspan="8"><h3 align="center">Welcome to Social Auth Demo</h3></td></tr>
				<tr><td colspan="8"><p align="center">Please click on any icon.</p></td></tr>
				<tr>
					<td>
						<a href="socialAuth.do?id=facebook"><img src="images/facebook_icon.png" alt="Facebook" title="Facebook" border="0"></img></a>
						<br/><br/>
						<c:if test="${facebook eq true}">
							<a href="socialAuth.do?id=facebook&mode=signout">Signout</a>
						</c:if>
						<c:if test="${facebook eq false}">
							<a href="socialAuth.do?id=facebook">Signin</a><br/>
						</c:if>
					</td>
					<td>
						<a href="socialAuth.do?id=twitter"><img src="images/twitter_icon.png" alt="Twitter" title="Twitter" border="0"></img></a>
						<br/><br/>
						<c:if test="${twitter eq true}">
							<a href="socialAuth.do?id=twitter&mode=signout">Signout</a>
						</c:if>
						<c:if test="${twitter eq false}">
							<a href="socialAuth.do?id=twitter">Signin</a><br/>
						</c:if>
					</td>
					<td>
						<a href="socialAuth.do?id=google"><img src="images/gmail-icon.jpg" alt="Gmail" title="Gmail" border="0"></img></a>
						<br/><br/>
						<c:if test="${google eq true}">
							<a href="socialAuth.do?id=google&mode=signout">Signout</a>
						</c:if>
						<c:if test="${google eq false}">
							<a href="socialAuth.do?id=google">Signin</a><br/>
						</c:if>
					</td>
					<td>
						<a href="socialAuth.do?id=yahoo"><img src="images/yahoomail_icon.jpg" alt="YahooMail" title="YahooMail" border="0"></img></a>
						<br/><br/>
						<c:if test="${yahoo eq true}">
							<a href="socialAuth.do?id=yahoo&mode=signout">Signout</a>
						</c:if>
						<c:if test="${yahoo eq false}">
							<a href="socialAuth.do?id=yahoo">Signin</a><br/>
						</c:if>
					</td>
					<td>
						<a href="socialAuth.do?id=hotmail"><img src="images/hotmail.jpeg" alt="HotMail" title="HotMail" border="0"></img></a>
						<br/><br/>
						<c:if test="${hotmail eq true}">
							<a href="socialAuth.do?id=hotmail&mode=signout">Signout</a>
						</c:if>
						<c:if test="${hotmail eq false}">
							<a href="socialAuth.do?id=hotmail">Signin</a><br/>
						</c:if>
					</td>
					<td>
						<a href="socialAuth.do?id=linkedin"><img src="images/linkedin.gif" alt="Linked In" title="Linked In" border="0"></img></a>
						<br/><br/>
						<c:if test="${linkedin eq true}">
							<a href="socialAuth.do?id=linkedin&mode=signout">Signout</a>
						</c:if>
						<c:if test="${linkedin eq false}">
							<a href="socialAuth.do?id=linkedin">Signin</a><br/>
						</c:if>
					</td>
					<td>
						<a href="socialAuth.do?id=foursquare"><img src="images/foursquare.jpeg" alt="FourSquare" title="FourSquare" border="0"></img></a>
						<br/><br/>
						<c:if test="${foursquare eq true}">
							<a href="socialAuth.do?id=foursquare&mode=signout">Signout</a>
						</c:if>
						<c:if test="${foursquare eq false}">
							<a href="socialAuth.do?id=foursquare">Signin</a><br/>
						</c:if>
					</td>
					<td>
						<a href="socialAuth.do?id=myspace"><img src="images/myspace.jpeg" alt="MySpace" title="MySpace" border="0"></img></a>
						<br/><br/>
						<c:if test="${myspace eq true}">
							<a href="socialAuth.do?id=myspace&mode=signout">Signout</a>
						</c:if>
						<c:if test="${myspace eq false}">
							<a href="socialAuth.do?id=myspace">Signin</a><br/>
						</c:if>
					</td>
					<td>
						<a href="socialAuth.do?id=mendeley"><img src="images/mendeley.jpg" alt="Mendeley" title="Mendeley" border="0"></img></a>
						<br/><br/>
						<c:if test="${mendeley eq true}">
							<a href="socialAuth.do?id=mendeley&mode=signout">Signout</a>
						</c:if>
						<c:if test="${mendeley eq false}">
							<a href="socialAuth.do?id=mendeley">Signin</a><br/>
						</c:if>
					</td>
					<td>
						<a href="socialAuth.do?id=yammer"><img src="images/yammer.jpg" alt="Yammer" title="Yammer" border="0"></img></a>
						<br/><br/>
						<c:if test="${yammer eq true}">
							<a href="socialAuth.do?id=yammer&mode=signout">Signout</a>
						</c:if>
						<c:if test="${yammer eq false}">
							<a href="socialAuth.do?id=yammer">Signin</a><br/>
						</c:if>
					</td>
				</tr>
				<tr>
					<td colspan="8" align="center">
						<form action="socialAuth.do" onsubmit="return validate(this);">
							or enter OpenID url: <input type="text" value="" name="id"/>
							<input type="submit" value="Submit"/> 
						</form>
					</td>
				</tr>
				
			</table>

</body>
</html>
