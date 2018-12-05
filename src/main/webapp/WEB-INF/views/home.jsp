<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title>Salesforce-Heroku Service</title>
	<link rel="icon" href="resources/images/favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="resources/images/favicon.ico" type="image/x-icon" />	
	<link rel="stylesheet" href="resources/styles/style.css" type="text/css" media="screen" />
</head>

<body leftmargin="0px" topmargin="0px" marginwidth="0px" marginheight="0px">
<!-- header -->
	<div class="header">
            <div class="tittle-dhtmlx">Salesfroce Attchment Services Hosted on Heroku <a href="http://www.heroku.com/" target="_blank"><img src="resources/images/heroku_logo_small.png" border="no"/></a></div>
    </div>
<!-- /header -->
<div id="container" class="clearfix">
<h1>
	Welcome to Salesforce Lightning Platform Services!  
</h1>

<P>  This is general Services framework providing hosted services for Force.com hosted by Heroku. <a href="http://www.heroku.com/" target="_blank"><img src="resources/images/heroku_logo_small.png" border="no"/></a></P>
<p>Archive attachments to zip and send user download stream service - Provides a web interface to package any Attachments into a zip and deliver to user's browser on request.<br/>
Parameters:<br/>
id - provide CSV list or single Attachment IDs<br/>
zip - optional zip file name to return to user<br/>
sessionid = SFDC logged in user session commonly derived form APEX or Visual Force globals as {!$Api.Session_ID}<br/>
serverurl = End point URL from Enterprise API {!$Api.Enterprise_Server_URL_230}<br/>
The link format: Heroku App URL/zipattachment?id="CSV list of SFDC attachment IDs"&zip="Optional zip file name"&sessionid={!$Api.Session_ID}&serverurl={!$Api.Enterprise_Server_URL_230}<br/>
</p>
</div> <!-- container --> 
<!-- footer --> 
    <div class="footer">
        <div class="footer-logo"></div>
        <div class="copyright">Copyright &copy; 2015 Igor Androsov. All rights reserved.</div>
    </div>
<!-- /footer -->

</body>
</html>
