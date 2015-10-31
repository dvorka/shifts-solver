<%@ page contentType="text/html;charset=UTF-8" language="java"
  import="com.google.appengine.api.users.User,
  com.google.appengine.api.users.UserService,
  com.google.appengine.api.users.UserServiceFactory,
  com.mindforger.shiftsolver.server.security.ShiftSolverSecurity" 
%><%
    boolean isLogged=false;
    boolean isAdmin=false;

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
    	isLogged=userService.isUserLoggedIn();
    	if(isLogged) {
        	isAdmin=ShiftSolverSecurity.isAdmin(user.getEmail());    		
    	}
    }
%><%

  if(isLogged && isAdmin) {

%><!doctype html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link rel="icon" href="./favicon.ico">
    <link type="text/css" rel="stylesheet" href="css/style.css">
    <title>Shifts Solver</title>
    <script type="text/javascript" language="javascript" src="shifts_solver/shifts_solver.nocache.js"></script>
  </head>

  <body>
    <!-- ensures RIA history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled in order for this application to display correctly.
      </div>
    </noscript>


	<table class="mf-applicationPanel">
	  <tr>
	    <td colspan="1"><div id="solverStatusLineContainer" class="mf-statusProgress">Loading ShiftsSolver...</div></td>
	    <td align="right" style="padding-right: 27px;">
	      <span id="solverPrintButton"></span>
	      <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>" 
	         class="signOutButton" style="color: #555" >Logout</a>
	    </td>
	  </tr>
	  <tr>
		<td id="solverTitleLogo" width="190px">
          <span class="s2-titleLogo">Shift<span class="s2-titleCodename">Solver</span></span>
		</td>
		<td>
		<table width="100%">
			<tr>
				<td id="searchContainer"></td>
				<td id="pageTitleContainer"></td>
			</tr>
		</table>
		</td>
	  </tr>	
	  <tr>
	    <td id="solverLeftMenuContainer" class="mf-leftMenubarContainer" valign="top"></td>
	    <td valign="top">
		  <table>
		    <tr><td id="solverHomeContainer" style="display: none;"></td></tr>
		    <tr><td id="solverEmployeesTableContainer" style="display: none;"></td></tr>
		    <tr><td id="solverEmployeeEditorContainer" style="display: none;"></td></tr>
		    <tr><td id="solverDlouhanTableContainer" style="display: none;"></td></tr>
		    <tr><td id="solverDlouhanEditorContainer" style="display: none;"></td></tr>		    
		    <tr><td id="solverSolutionTableContainer" style="display: none;"></td></tr>
		    <tr><td id="solverSolutionViewContainer" style="display: none;"></td></tr>		    
		    <tr><td id="solverProgressContainer" style="display: none;"></td></tr>		    
		    <tr><td id="solverNoSolution" style="display: none;"></td></tr>		    
		    <tr><td id="solverSettingsContainer" style="display: none;"></td></tr>
		    <tr><td id="solverSolutionLoadingContainer"><!-- img src="/images/loading-s2.png"/ --></td></tr>		    
		  </table>
	    <td>
	  </tr>
	</table>

    <center>
      <br/>
      <div id="creditsDiv" 
           style="width: 35em; display: none; font-style: italic; text-align: justify; border: solid 1px #ccc; background-color: white; cursor: hand; cursor: pointer; margin-bottom: 2em;" 
           onclick="javascript:document.getElementById('creditsDiv').style.display='none';">
        <pre>
   Credits  

     Written by Martin Dvorak

   Acknowledgements to reviewers
   
     Mari
      
   Platform, Services and 3rd party libraries
   
     Google App Engine
     Google Gson
     Google Web Toolkit
     Gravatar
     
     Eclipse
       </pre>
     </div>

         <div id="privacyPolicyDiv"
           style="font-family: monospace; border: solid 1px #aaa; width: 35em; padding: 2em 4em 1em 4em; margin-bottom: 1em; text-align: justify; cursor: hand; cursor: pointer; display:none;"  
           onclick="javascript:document.getElementById('privacyPolicyDiv').style.display='none';">
SHIFTS SOLVER PRIVACY POLICY
<br/>
<br/>  
Your name, email address, or any other personally identifiable information will not be disclosed to any third parties unless required to by law.
<br/>
<br/>    
ShiftSolver application uses cookies to store very small amounts of non-personally identifiable data on your computer to improve your experience. Enabling cookies is recommended, but not required. Most browsers have options or extensions that allow you block cookies for this or any other site if you so wish.
<br/>
<br/>    
Google Analytics are used to monitor your activity on the website.
<br/>
<br/>
In the future ShiftSolver may share keywords from your data with third parties for the purposes of serving relevant advertising. No personally identifiable information or full text data will be shared with any such parties.
<br/>
<br/>
In the future ShiftSolver may aggregate application data between users for the purposes of providing useful public or private value added services. No personally identifiable information will be used for this purpose.
<br/>
<br/>
Since ShiftSolver runs on Google infrastructure and uses Google authentication, you should also be aware of <a style="color: #777; font-weight: normal;" href="http://www.google.com/privacypolicy.html">Google account privacy policy</a>.
<br/>
<br/>
If you have questions or comments about this policy, please don't hesitate to <a style="color: #777; font-weight: normal;" href="mailto:info@mindforger.com">contact us</a>.
<br/>
<br/>
END OF POLICY     
     </div>
     

     <div style="color: #777; width: 99%;">
       <div id="mfFootnoteId" style="display: none; float: right;">
         <a style="color: #777;" href='mailto:feedback@mindforger.com?subject=ShiftSolver Testimonial&body=If ShiftSolver have helped you, I would be thankful to receive your testimonial. When you add a testimonial, you are providing your peers with an honest assessment of ShiftSolver and helping them understand how they can benefit from it. Thank you for your time!%0A%0AYour Name:%0A...%0A%0AYour Web:%0A...%0A%0ATestimonial Details:%0A...%0A%0A' title="Send a testimonial">Add Testimonial</a> |
          
         <a style="color: #777;" href="mailto:feedback@mindforger.com?subject=[ShiftSolver] Feedback" title="Provide a feedback">Feedback</a> |
         <a style="color: #777;" href="mailto:feedback@mindforger.com?subject=[ShiftSolver] Feature Request" title="Request a feature">Request&nbsp;Feature</a> | 
         
         <a style="font-weight: normal; cursor: pointer;" onclick="javascript:document.getElementById('creditsDiv').style.display='block';document.getElementById('privacyPolicyDiv').style.display='none';" title="Credits and acknowledgements">Credits</a> |
         <a style="font-weight: normal; cursor: pointer;" onclick="javascript:document.getElementById('privacyPolicyDiv').style.display='block';document.getElementById('creditsDiv').style.display='none';" title="Privacy Policy">Privacy</a>
         
       </div>
       <div style="float: left; margin-top: 0.3em;"></div>
     </div>
    </center>
  </body>
</html><%



  } else {



%><!doctype html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link rel="icon" href="./favicon.ico">
    <link type="text/css" rel="stylesheet" href="css/style.css">
    <title>Shifts Solver</title>
  </head>
  <body>
	<table class="mf-applicationPanel">
	  <tr>
	    <td colspan="2"><div id="solverStatusLineContainer" class="mf-statusProgress"></div></td>
	    <td><div style="height: 10px;"></div></td>
	  </tr>
	  <tr>
		<td id="solverTitleLogo" width="190px">
          <span class="s2-titleLogo">Shift<span class="s2-titleCodename">Solver</span></span>
		</td>
		<td>
		<table width="100%">
			<tr>
			    <td><% if(isLogged) { %>
			       You are not authorized to use this application.
			       <% } %>
			    </td>
				<td align="right">
			       <% if(!isLogged) { %>
			         <a href="<%= userService.createLoginURL(request.getRequestURI()) %>" class="signInButton">Login</a>
			       <% } else { %>
			         <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>" class="signOutButton">Logout</a>
			       <% } %>
				</td>
			</tr>
		</table>
		</td>
	  </tr>	
	  <tr>
	    <td id="solverLeftMenuContainer" class="mf-leftMenubarContainer" valign="top"></td>
	    <td valign="top">
		  <table>
		    <tr><td><!-- img src="/images/loading-s2.png"/ --></td></tr>		    
		  </table>
	    <td>
	  </tr>
	</table>

    <center>
      <br/>
      <div id="creditsDiv" 
           style="width: 35em; display: none; font-style: italic; text-align: justify; border: solid 1px #ccc; background-color: white; cursor: hand; cursor: pointer; margin-bottom: 2em;" 
           onclick="javascript:document.getElementById('creditsDiv').style.display='none';">
        <pre>
   Credits  

     Written by Martin Dvorak

   Acknowledgements to reviewers
   
     Mari
      
   Platform, Services and 3rd party libraries
   
     Google App Engine
     Google Gson
     Google Web Toolkit
     Gravatar
     
     Eclipse
       </pre>
     </div>

         <div id="privacyPolicyDiv"
           style="font-family: monospace; border: solid 1px #aaa; width: 35em; padding: 2em 4em 1em 4em; margin-bottom: 1em; text-align: justify; cursor: hand; cursor: pointer; display:none;"  
           onclick="javascript:document.getElementById('privacyPolicyDiv').style.display='none';">
SHIFTS SOLVER PRIVACY POLICY
<br/>
<br/>  
Your name, email address, or any other personally identifiable information will not be disclosed to any third parties unless required to by law.
<br/>
<br/>    
ShiftSolver application uses cookies to store very small amounts of non-personally identifiable data on your computer to improve your experience. Enabling cookies is recommended, but not required. Most browsers have options or extensions that allow you block cookies for this or any other site if you so wish.
<br/>
<br/>    
Google Analytics are used to monitor your activity on the website.
<br/>
<br/>
In the future ShiftSolver may share keywords from your data with third parties for the purposes of serving relevant advertising. No personally identifiable information or full text data will be shared with any such parties.
<br/>
<br/>
In the future ShiftSolver may aggregate application data between users for the purposes of providing useful public or private value added services. No personally identifiable information will be used for this purpose.
<br/>
<br/>
Since ShiftSolver runs on Google infrastructure and uses Google authentication, you should also be aware of <a style="color: #777; font-weight: normal;" href="http://www.google.com/privacypolicy.html">Google account privacy policy</a>.
<br/>
<br/>
If you have questions or comments about this policy, please don't hesitate to <a style="color: #777; font-weight: normal;" href="mailto:info@mindforger.com">contact us</a>.
<br/>
<br/>
END OF POLICY     
     </div>
     

     <div style="color: #777; width: 99%;">
       <div id="mfFootnoteId" style="display: none; float: right;">
         <a style="color: #777;" href='mailto:feedback@mindforger.com?subject=ShiftSolver Testimonial&body=If ShiftSolver have helped you, I would be thankful to receive your testimonial. When you add a testimonial, you are providing your peers with an honest assessment of ShiftSolver and helping them understand how they can benefit from it. Thank you for your time!%0A%0AYour Name:%0A...%0A%0AYour Web:%0A...%0A%0ATestimonial Details:%0A...%0A%0A' title="Send a testimonial">Add Testimonial</a> |
          
         <a style="color: #777;" href="mailto:feedback@mindforger.com?subject=[ShiftSolver] Feedback" title="Provide a feedback">Feedback</a> |
         <a style="color: #777;" href="mailto:feedback@mindforger.com?subject=[ShiftSolver] Feature Request" title="Request a feature">Request&nbsp;Feature</a> | 
         
         <a style="font-weight: normal; cursor: pointer;" onclick="javascript:document.getElementById('creditsDiv').style.display='block';document.getElementById('privacyPolicyDiv').style.display='none';" title="Credits and acknowledgements">Credits</a> |
         <a style="font-weight: normal; cursor: pointer;" onclick="javascript:document.getElementById('privacyPolicyDiv').style.display='block';document.getElementById('creditsDiv').style.display='none';" title="Privacy Policy">Privacy</a>
         
       </div>
       <div style="float: left; margin-top: 0.3em;"></div>
     </div>
    </center>
  </body>
</html><%

  }

%>