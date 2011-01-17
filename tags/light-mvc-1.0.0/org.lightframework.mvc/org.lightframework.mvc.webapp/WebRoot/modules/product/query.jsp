<%@page import="org.lightframework.mvc.webapp.product.Product"%>
<%@page pageEncoding="UTF-8"%>
<%
	Product product = (Product)request.getAttribute("result.value");
	System.out.println(product.getName());
	out.write(product.getName());
%>
