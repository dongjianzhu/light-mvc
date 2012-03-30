/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lightframework.mvc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.lightframework.mvc.HTTP.Cookie;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.gzip.GzipResponseWrapper;

import com.mockrunner.mock.web.MockFilterChain;
import com.mockrunner.mock.web.MockFilterConfig;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletContext;

/**
 * Test Case of {@link MvcFilter}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestMvcFilter extends TestCase {
	
	private static final String CONTEXT_PATH = "/mvc";
	
	private MvcFilter               filter;
	private MockFilterChain         chain;
	private MockFilterConfig        config;
	private MockServletContext      context;
	private MockHttpServletRequest  request;
	private MockHttpServletResponse response;
	private MockHttpSession         session;
	
	@Override
    protected void setUp() throws Exception {
		filter   = new MvcFilter();
		chain    = new MockFilterChain();
		config   = new MockFilterConfig();
		context  = new MockServletContext();
		request  = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		session  = new MockHttpSession();
		
		config.setupServletContext(context);
		request.setContextPath(CONTEXT_PATH);
		request.setSession(session);
		
		//set gizp config
		config.setInitParameter("gzipPattern", "^.*(.*.js|.*.css)$") ;
		
    }
	
	public void testInit() throws Exception{
		config.setInitParameter(MvcFilter.INIT_PARAM_PACKAGE, "  ");
		try{
			filter.init(config);
			
			Application application = Application.current();
			assertNotNull(application);
			assertNotNull(application.getExternalContext());
			assertEquals(context, application.getExternalContext());
			
			Module module = filter.module;
			assertNotNull(module);
			assertTrue(module.isStarted());
			assertEquals(Module.DEFAULT_PACKAGE, module.getPackagee());
		}finally{
			filter.destroy();
		}
	}
	
	public void testInit2() throws Exception {
		config.setInitParameter(MvcFilter.INIT_PARAM_PACKAGE, "demo ");
		try{
			filter.init(config);
			
			Module module = filter.module;
			assertNotNull(module);
			assertTrue(module.isStarted());
			assertEquals("demo", module.getPackagee());
		}finally{
			filter.destroy();
		}
	}
	
	public void testDoFilter1() throws Exception {
		try{
			filter.init(config);

			request.setRequestURL("http://localhost:8080/mvc");
			request.setRequestURI("/mvc");
			
			ChainFilter chainFilter = new ChainFilter();
			chain.addFilter(chainFilter);
			
			filter.doFilter(request, response, chain);

			assertTrue(chainFilter.executed);
		}finally{
			filter.destroy();
		}
	}
	
	public void testDoFilter2() throws Exception {
		try{
			config.setInitParameter(MvcFilter.INIT_PARAM_PACKAGE, TestMvcFilter.class.getPackage().getName());
			filter.init(config);

			request.setRequestURL("http://localhost:8080/mvc");
			request.setRequestURI("/mvc");
			
			ChainFilter chainFilter = new ChainFilter();
			chain.addFilter(chainFilter);
			
			filter.doFilter(request, response, chain);

			assertFalse(chainFilter.executed);
		}finally{
			filter.destroy();
		}
	}
	
	public void testGzip() throws Exception {
		try{
			filter.init(config);
			//test gizp
			request.setHeader("accept-encoding", "gzip") ;
			request.setRequestURL("http://localhost:8080/test/123.js");
			request.setRequestURI("/test/123.js");
			request.setQueryString("x-format=json");
			request.setServerPort(8080);
			request.setRemoteAddr("localhost");
			
			
			filter.doFilter(request, response, chain);
			Request req = (Request)request.getAttribute(MvcFilter.ATTRIBUTE_MVC_REQUEST);
			GzipResponseWrapper gzipResp = (GzipResponseWrapper)req.getResponse().getExternalResponse() ;
			
			ServletOutputStream out = gzipResp.getOutputStream() ;
			assertNotNull(out) ;
			out.write("hello".getBytes()) ;
			out.write(123) ;
			gzipResp.flushBuffer() ;
			
			assertNotNull(gzipResp.getWriter()) ;
			gzipResp.finishResponse() ;
		}finally{
			filter.destroy();
		}
	}
	
	public void testRequestImpl() throws Exception {
		try{
			filter.init(config);

			request.setRequestURL("http://localhost:8080/mvc/hello?a=b&x-format=json&bool=true&int=123");
			request.setRequestURI("/mvc/hello");
			request.setQueryString("a=b&x-format=json&bool=true&int=123");
			request.setServerPort(8080);
			request.setRemoteAddr("localhost");
			request.setMethod("POST");

			response.setContentType("text/html") ;
			
			filter.doFilter(request, response, chain);
			
			Request req = (Request)request.getAttribute(MvcFilter.ATTRIBUTE_MVC_REQUEST);
			assertNotNull(req);
			assertNotNull(req.getApplication());
			assertNotNull(req.getSession());
			assertNotNull(req.getResponse());
			assertNotNull(req.getModule());
			assertTrue(req.getExternalRequest() instanceof HttpServletRequest);
			assertTrue(req.getResponse().getExternalResponse() instanceof HttpServletResponse);
			assertTrue(req.getSession().getExternalSession() instanceof HttpSession);
			assertNotNull(req.getAttribute(MvcFilter.ATTRIBUTE_MVC_REQUEST));
			
			assertNotNull( req.getContent() ) ;
			assertNotNull( req.getInputStream() ) ;
			assertNotNull( req.getSession().getId() ) ;
			
			assertEquals("a=b&x-format=json&bool=true&int=123",req.getQueryString());
			request.setupAddParameter("a", "b");
			request.setupAddParameter("bool", "true");
			request.setupAddParameter("int", "123");
			assertEquals("b",req.getStringParam("a")) ;
			assertEquals("b",req.getStringParam("a1","b")) ;
			assertEquals(Boolean.TRUE,req.getBooleanParam("bool")) ;
			assertEquals(Boolean.FALSE,req.getBooleanParam("bool1",false)) ;
			assertEquals(new Integer(123),req.getIntParam("int")) ;
			assertEquals(new Integer(123),req.getIntParam("int1",123)) ;
			assertEquals(new Long(123),req.getLongParam("int")) ;
			assertEquals(new Long(123),req.getLongParam("int1",123l)) ;
			
			assertEquals("/hello",req.getPath());
			assertEquals(request.getRequestURI(), req.getUriString());
			assertEquals(request.getRequestURL().toString(),req.getUrlString());
			assertEquals(request.getServerPort(), req.getUrl().getPort());
			assertEquals(request.getRemoteAddr(), req.getRemoteAddress());
			
			assertEquals(request.getMethod(), req.getMethod());
			
			
			req.getResponse().setHeader("header", "hello");
			assertEquals(request.getHeader("header"), req.getHeader("header"));
			
			req.getResponse().setStatus(200) ;
			assertEquals(response.getStatusCode(),200);
			
			assertNotNull( req.getResponse().getOut() ) ;
			assertEquals("text/html", req.getResponse().getContentType() ) ;
			
			//forward redirect
			req.getResponse().forward("/mvc/testForward") ;
			//assertEquals ???
			req.getResponse().redirect("~/mvc/testRedirect") ;
			//assertEquals ???
			
			//cookie
			req.getResponse().setCookie("cookieName", "hello, cookie") ;
			
			Cookie cookie = new Cookie("c_name_1","value1") ;
			Cookie cookie2 = new Cookie("c_name_2","value2","example.com","/") ;
			
			req.getResponse().setCookie(cookie) ;
			req.getResponse().setCookie(cookie2) ;
			
			assertNotNull( req.getResponse().getCookies() ) ;
			assertNotNull( response.getCookies() ) ;
			assertNotNull( req.getCookies() ) ;
			assertEquals( ((javax.servlet.http.Cookie)response.getCookies().get(0)).getValue() ,"hello, cookie") ;
			
			assertEquals( req.getResponse().getCookie("c_name_1").getValue() ,"value1" ) ;
			
			assertEquals( ((javax.servlet.http.Cookie)response.getCookies().get(1)).getValue() ,"value1") ;
			assertEquals( ((javax.servlet.http.Cookie)response.getCookies().get(2)).getValue() ,"value2") ;
			
			//update cookie
			req.getResponse().setCookie("cookieName", "hello, cookie updated") ;
			assertEquals( ((javax.servlet.http.Cookie)response.getCookies().get(3)).getValue() ,"hello, cookie updated") ;
			
			//session
			req.getSession().setAttribute("sessionName", "hello , session") ;
			assertNotNull( request.getSession().getAttribute("sessionName") ) ;
			assertEquals( (String)request.getSession().getAttribute("sessionName") , "hello , session" ) ;
			assertEquals( (String)req.getSession().getAttribute("sessionName") , "hello , session" ) ;
			
		}finally{
			filter.destroy();
		}
	}
	
	public void testRequestPath() throws Exception {
		try{
			filter.init(config);

			request.setRequestURL("http://localhost:8080/mvc/hello;jsessionid=xxxxxxx?a=b&x-format=json&bool=true&int=123");
			request.setRequestURI("/mvc/hello;jsessionid=xxxxxxx");
			request.setQueryString("a=b&x-format=json&bool=true&int=123");
			request.setServerPort(8080);
			request.setRemoteAddr("localhost");
			response.setContentType("text/html") ;
			
			filter.doFilter(request, response, chain);	
			Request req = (Request)request.getAttribute(MvcFilter.ATTRIBUTE_MVC_REQUEST);
			assertEquals("/hello", req.getPath());
			
			request.setRequestURI("/mvc/hel;lo/abcd;jsessionid=xxxxxxx");
			filter.doFilter(request, response, chain);	
			req = (Request)request.getAttribute(MvcFilter.ATTRIBUTE_MVC_REQUEST);
			assertEquals("/hel;lo/abcd", req.getPath());
			
			request.setRequestURI("/mvc/hello/abcd;aa;bb");
			filter.doFilter(request, response, chain);	
			req = (Request)request.getAttribute(MvcFilter.ATTRIBUTE_MVC_REQUEST);
			assertEquals("/hello/abcd", req.getPath());
		}finally{
			filter.destroy();
		}
	}
	
	private static class ChainFilter implements Filter {
		boolean executed;

		public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
	        this.executed = true;
        }

		public void init(FilterConfig arg0) throws ServletException {
        }
		public void destroy() {
        }
	}
}
