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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import junit.framework.TestCase;

import org.lightframework.mvc.HTTP.Request;

import com.mockrunner.mock.web.MockFilterChain;
import com.mockrunner.mock.web.MockFilterConfig;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
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
	
	@Override
    protected void setUp() throws Exception {
		filter   = new MvcFilter();
		chain    = new MockFilterChain();
		config   = new MockFilterConfig();
		context  = new MockServletContext();
		request  = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		
		config.setupServletContext(context);
		request.setContextPath(CONTEXT_PATH);
    }

	public void testInit() throws Exception{
		config.setInitParameter(MvcFilter.INIT_PARAM_PACKAGE, "  ");
		try{
			filter.init(config);
			
			Module module = filter.module;
			assertNotNull(module);
			assertTrue(module.isStarted());
			assertEquals(1, module.getPackages().length);
			assertEquals(Module.DEFAULT_PACKAGE, module.getPackages()[0]);
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
			assertEquals(1, module.getPackages().length);
			assertEquals("demo", module.getPackages()[0]);
		}finally{
			filter.destroy();
		}
	}
	
	public void testInit3() throws Exception {
		config.setInitParameter(MvcFilter.INIT_PARAM_PACKAGE, "demo.app1 , \n demo.app2 ");
		try{
			filter.init(config);
			
			Module module = filter.module;
			assertNotNull(module);
			assertTrue(module.isStarted());
			assertEquals(2, module.getPackages().length);
			assertEquals("demo.app1", module.getPackages()[0]);
			assertEquals("demo.app2", module.getPackages()[1]);
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
	
	public void testRequestImpl() throws Exception {
		try{
			filter.init(config);

			request.setRequestURL("http://localhost:8080/mvc/hello?a=b");
			request.setRequestURI("/mvc/hello");
			request.setQueryString("a=b");
			request.setServerPort(8080);
			request.setRemoteAddr("localhost");
			
			filter.doFilter(request, response, chain);
			
			Request req = (Request)request.getAttribute(MvcFilter.ATTRIBUTE_MVC_REQUEST);
			assertNotNull(req);
			assertNotNull(req.getModule());
			assertNotNull(req.getAttribute(MvcFilter.ATTRIBUTE_MVC_REQUEST));
			assertEquals("a=b",req.getQueryString());
			assertEquals("/hello",req.getPath());
			assertEquals(request.getRequestURI(), req.getUriString());
			assertEquals(request.getRequestURL().toString(),req.getUrlString());
			assertEquals(request.getServerPort(), req.getUrl().getPort());
			assertEquals(request.getRemoteAddr(), req.getRemoteAddress());
			
			request.setMethod("POST");
			assertEquals(request.getMethod(), req.getMethod());
			
			request.setHeader("header", "hello");
			assertEquals(request.getHeader("header"), req.getHeader("header"));
			
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
