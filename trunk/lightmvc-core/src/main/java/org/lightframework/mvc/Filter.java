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
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.HTTP.Url;

/**
 * mvc application http filter,configed in web.xml
 * 
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public class Filter implements javax.servlet.Filter {

	protected ServletContext servletContext;
	protected Application    application;
	
	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();
		application    = new Application();
		application.servletContext = servletContext;
		
		Framework.start(application);
	}
	
	public void destroy() {
		Framework.stop(application);
	}

	public void doFilter(final ServletRequest servletRequest,final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
		//create mvc framework http request and response
		Request request   = new RequestWrapper((HttpServletRequest)servletRequest,application);
		Response response = new ResponseWrapper((HttpServletRequest)servletRequest,(HttpServletResponse)servletResponse,application);
		
		try{
			boolean managed = false;

			//is current request ignored or managed by mvc framework ?
			if(!Framework.ignore(request)){
				managed = Framework.handle(request, response);
			}
			
			if(!managed){
				filterChain.doFilter(servletRequest, servletResponse);
			}
		}catch(Throwable e){
			if(e instanceof ServletException){
				throw (ServletException)e;
			}else if(e instanceof IOException){
				throw (IOException)e;
			}else{
				throw new ServletException(e.getMessage(),e);
			}
		}
	}

	public static final class RequestWrapper extends Request {
		private final HttpServletRequest request;
		
		public RequestWrapper(HttpServletRequest request,Application application){
			this.request      = request;
			this.application  = application;
			
			this.url             = new Url();
			this.url.port        = request.getServerPort(); //TODO : is server port right ?
			this.url.protocol    = request.getProtocol();
			this.url.urlString   = request.getRequestURL().toString();
			this.url.uriString   = request.getRequestURI();
			this.url.queryString = request.getQueryString();
		}
		
		@Override
        public String getContext() {
			if(null == context){
				context = getContextPath(request);
			}
			return context;
        }
		
		@Override
        public String getPath() {
			if(null == path){
				path = getUriString().substring(getContext().length());
				if(!path.startsWith("/")){
					path = "/" + path;
				}
			}
	        return super.getPath();
        }
		
		@Override
        public String getContentType() {
	        return request.getContentType();
        }
		
		@Override
        public String getParameter(String name) {
	        return request.getParameter(name);
        }

		@Override
		@SuppressWarnings("unchecked")
        public Map<String, String[]> getParameters() {
	        return request.getParameterMap();
        }
		
		@Override
        public String[] getParameterValues(String name) {
	        return request.getParameterValues(name);
        }

		@Override
        public String getMethod() {
	        return request.getMethod();
        }

		@Override
        public boolean isSecure() {
	        return request.isSecure();
        }

		@Override
        public String getRemoteAddress() {
	        return request.getRemoteAddr();
        }

		@Override
        public Object getAttribute(String name) {
	        return request.getAttribute(name);
        }

		@Override
        public void setAttribute(String name, Object value) {
	        request.setAttribute(name, value);
        }
	}
	
	public final class ResponseWrapper extends Response{
		private final HttpServletRequest  request;
        private final HttpServletResponse response;
		
		public ResponseWrapper(HttpServletRequest request,HttpServletResponse response,Application application){
			this.request  = request;
			this.response = response;
			this.encoding = application.getEncoding();
		}

		@Override
        public void setStatus(int status) {
			response.setStatus(status);
        }

		@Override
        public void forward(String path) {
			try {
	            request.getRequestDispatcher(path).forward(request, response);
            } catch (Throwable e) {
            	throw new MVCException(e);
            }
        }

		@Override
        public void redirect(String url) {
			try {
	            response.sendRedirect(url);
            } catch (Throwable e) {
            	throw new MVCException(e);
            }
        }
	}
	
	private static String getContextPath(HttpServletRequest request){
		String contextPath = request.getContextPath();
		if("/".equals(contextPath)){
			contextPath = "";
		}
		return contextPath;
	}
}
