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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link MvcServlet} is the same as {@link MvcFilter}, use to config as {@link Servlet} but {@link Filter}.
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class MvcServlet extends MvcFilter implements Servlet {

    private static final long serialVersionUID = -5501605125611318665L;
    
    private ServletConfig  config;
    private ServletContext context;

    @SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
		this.config  = config;
		this.context = config.getServletContext();
		
		Map<String, String> params = new HashMap<String, String>();
		Enumeration<String> names  = config.getInitParameterNames();
		while(names.hasMoreElements()){
			String name = names.nextElement();
			params.put(name, config.getInitParameter(name));
		}
		
		doInit(config.getServletContext(), params);		
    }

	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		HttpServletRequest  servletRequest  = (HttpServletRequest)request;
		HttpServletResponse servletResponse = (HttpServletResponse)response;
		
		doHandle(servletRequest, servletResponse, null);
    }
	
	@Override
    protected void doNotHandled(HttpServletRequest servletRequest, 
    		                    HttpServletResponse servletResponse, Object context) throws IOException, ServletException {
		servletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

	public ServletConfig getServletConfig() {
	    return config;
    }
	
	public ServletContext getServletContext(){
		return context;
	}

	public String getServletInfo() {
	    return config.getServletContext().getServerInfo();
    }
}
