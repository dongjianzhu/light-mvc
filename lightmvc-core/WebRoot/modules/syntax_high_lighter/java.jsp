<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
String path = request.getContextPath();
request.setAttribute("path",path);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Light MVC Feature Demo Code </title>

<link type="text/css" rel="stylesheet" href="${path}/modules/syntax_high_lighter/Styles/SyntaxHighlighter.css"/>
<link type="text/css" rel="stylesheet" href="${path}/modules/syntax_high_lighter/Styles/TestPages.css"/>
<script>
function showSource(){
	var path = document.getElementById("codePath").value;
	var currentUrl = window.location.href;
	var dotIndex = currentUrl.indexOf('?');
	if(dotIndex != -1){
		currentUrl = currentUrl.substring(0,dotIndex);  
	}
	if(path != ''){
		window.location.href = currentUrl + "?sourcePath="+path;
	}
}
window.onload = function(){
	var h2 = document.getElementById("codeDiv").clientHeight - 45;
	if(h2 > 100){
		document.getElementById("navDiv").style.height = h2;
	}
};

</script>
</head>

<body>

<h1 style="text-align: center">Light MVC Feature Demo Code </h1>


<div class="layout" style="float: none;">

	<div class="column1" style="height: 100%" id="navDiv">
		<h3>Significant Improvement:</h3>
		<ol>
			
			<li><a href="#">Plugin Based</a></li>
			<li><a href="#">contract based</a></li>
			<li><a href="#">Zero configuration</a></li>
			<li><a href="#">Intelligent param binding</a></li>
			<li><a href="#">Automatic path scanning and match</a></li>
			
		</ol>
		<h3>Features Demos:</h3>
		<ol>
			<li><a href="#">Request Routing</a></li>
			<li><a href="#">Param Binding</a></li>
			<li><a href="#">Auto path scan</a></li>
		</ol>
		<h3>Quick Start:</h3>
		<ol>
			<li><a href="#">Create a Controller (POJO)</a></li>
			<li><a href="#">Add an Action method </a></li>
			<li><a href="#">Create a view page</a></li>
		</ol>
		<h3>Core Code Search:</h3>
		<span style="padding-left: 20px;">
			<input type="text" name="codePath" ></input>
			<input type="submit" value="查询" onclick="showSource();"></input>
		</span>
		<h3>Suggestion:</h3>
		<ol>
			<li><a href="mailto:lightworld.me@bingosoft.net">lightworld.me@gmail.com</a></li>
			<li><a href="mailto:rain.yangdy@bingosoft.net">rain.yangdy@gmail.com </a></li>
		</ol>
	</div>
	
	<div class="column2" id="codeDiv">
		<textarea name="code" class="java" rows="15" cols="100">
		${javaSourceCode}
		</textarea>
	</div>
</div>



<script class="javascript" src="${path}/modules/syntax_high_lighter/Scripts/shCore.js"></script>
<script class="javascript" src="${path}/modules/syntax_high_lighter/Scripts/shBrushJava.js"></script>
<script class="javascript">
dp.SyntaxHighlighter.HighlightAll('code');
</script>
<div class="footer">Copyright 2010-2020 lightframework.org<br />
All rights reserved.</div>
</body>
</html>
