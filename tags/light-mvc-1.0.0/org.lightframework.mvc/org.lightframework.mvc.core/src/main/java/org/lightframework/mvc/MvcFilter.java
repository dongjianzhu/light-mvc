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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.lightframework.mvc.HTTP.Cookie;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.HTTP.Session;
import org.lightframework.mvc.gzip.GzipResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mvc web module http filter,configed in web.xml
 * 
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class MvcFilter implements javax.servlet.Filter {
	
	private static final Logger log = LoggerFactory.getLogger(MvcFilter.class);

	public static final String INIT_PARAM_PACKAGE        = "package";
	public static final String INIT_PARAM_MODULE         = "module";
	public static final String ATTRIBUTE_MVC_REQUEST     = Request.class.getName();
	public static final String ATTRIBUTE_SERLVET_REQUEST = HttpServletRequest.class.getName();
	 
	protected ServletContext context;      //current servlet context
	protected ModuleImpl     module;       //current application' root module	
	protected Application    application;  //current application
	
	protected String 		 gzipPattern ;
	
	@SuppressWarnings("unchecked")
	public void init(FilterConfig config) throws ServletException {
		context = config.getServletContext();
		
		Map<String, String> params = new HashMap<String, String>();
		Enumeration<String> names  = config.getInitParameterNames();
		while(names.hasMoreElements()){
			String name = names.nextElement();
			params.put(name, config.getInitParameter(name));
		}
		
		doInit(config.getServletContext(), params);
	}
	
	public final void destroy() {
		try{
			Application.setCurrent(application);
			doDestroy();
		}finally{
			Application.setCurrent(null);	
		}
	}

	public void doFilter(final ServletRequest servletRequest,final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
		doHandle((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse, filterChain);
	}
	
	protected void doHandle(HttpServletRequest servletRequest,HttpServletResponse servletResponse,Object context) throws IOException, ServletException {
		
		try{
			servletRequest.setCharacterEncoding(application.getEncoding());
			Application.setCurrent(application);
			
			//create mvc framework http request and response
			RequestImpl  request  = new RequestImpl((HttpServletRequest)servletRequest,application,module);
			ResponseImpl response = new ResponseImpl((HttpServletRequest)servletRequest,(HttpServletResponse)servletResponse,module);
			SessionImpl  session  = new SessionImpl(servletRequest.getSession(true));
			
			request.response = response;
			request.session  = session;
			response.request = request;
			
			servletRequest.setAttribute(ATTRIBUTE_MVC_REQUEST, request);
			servletRequest.setAttribute(ATTRIBUTE_SERLVET_REQUEST, servletRequest);
			
			//gzip support start
			boolean isSupportGzip = false ;
			GzipResponseWrapper wrappedResponse = null ;
			if( null != gzipPattern && isMatchPath(request, gzipPattern)){
	            String ae = request.getHeader("accept-encoding");
	            if (ae != null && ae.indexOf("gzip") != -1) {
	            	isSupportGzip = true ;
	                wrappedResponse =  new GzipResponseWrapper(servletResponse);
	                request.response.externalResponse = wrappedResponse ;
	            }
			}
			
			boolean managed = false ;

			//is current request ignored or managed by mvc framework ?
			if(!Framework.ignore(request)){
				try{
					managed = Framework.handle(request, response);
				}finally{
					Framework.handleFinally(request, response);
				}
			}
			
			if(!managed){
				doNotHandled(servletRequest, servletResponse,context);
			}
			
			if(isSupportGzip){
				 wrappedResponse.finishResponse();
			}
		}catch(Throwable e){
			if(e instanceof ServletException){
				throw (ServletException)e;
			}else if(e instanceof IOException){
				throw (IOException)e;
			}else{
				throw new ServletException(e.getMessage(),e);
			}
		}finally{
			servletRequest.removeAttribute(ATTRIBUTE_SERLVET_REQUEST);
			Application.setCurrent(null);
		}
	}
	
	protected final void doInit(ServletContext context, Map<String,String> params){
		Framework.initialize();
		module      = new ModuleImpl(context);
		application = Application.currentOf(context);
		
		gzipPattern = params.get("gzipPattern") ;//获取Gzip压缩
		
		boolean root = true;  //is root module ?
		if(null == application){
			log.info("[mvc] -> create mvc application");
			application = new Application(context,module);
		}else{
			root = false;
		}
		
		Application.setCurrent(application);		
		
		doConfig(params);
		
		//config packages
		String packagee = params.get(INIT_PARAM_PACKAGE);
		if(null != packagee && !"".equals(packagee = packagee.trim())){
			log.info("[mvc] -> set module package to '{}'",packagee);
			module.packagee = packagee;
		}
		
		//config module name
		String moduleName = params.get(INIT_PARAM_MODULE);
		if(null != moduleName && !"".equals(moduleName = moduleName.trim())){
			log.info("[mvc] -> set module name to '{}'",moduleName);
			module.name = moduleName;
		}
		
		//add plugins to module
		module.getPlugins().addAll(PluginManager.getPlugins());
		
		Framework.start(module);
		
		doInited();
		
		if(!root){
			//XXX : load child module of application
			application.getChildModules().add(module);
			log.info("[mvc] -> load a child module '{}'",module.getName());
		}
	}
	
	protected void doConfig(Map<String, String> params){
		
	}
	
	protected void doInited() {
		
	}
	
	protected void doNotHandled(HttpServletRequest servletRequest,HttpServletResponse servletResponse,Object context) throws IOException, ServletException {
		((FilterChain)context).doFilter(servletRequest, servletResponse);
	}
	
	protected void doDestroy(){
		Framework.stop(module);
	}
	
	/**
	 * @since 1.0.0
	 */
	public static final class ModuleImpl extends Module {
		
		private final ServletContext context;
		
		ModuleImpl(ServletContext context){
			this.context = context;
		}
		
		@Override
        protected URL findWebResource(String path) {
	        try {
	            return context.getResource(path);
            } catch (MalformedURLException e) {
            	throw new MvcException("error servletContext.getResource('" + path + "')",e);
            }
        }

		@Override
		@SuppressWarnings("unchecked")
        protected Collection<String> findWebResources(String path) {
			if(!path.endsWith("/")){
				path = path + "/";
			}
			return context.getResourcePaths(path);
        }
	}

	/**
	 * @since 1.0.0
	 */
	public static final class RequestImpl extends Request {
		private final HttpServletRequest request;
		RequestImpl(HttpServletRequest request,Application application, Module module){
			this.request         = request;
			this.module  		 = module;
			this.application     = application;
			this.externalRequest = request;
			this.url             = new HTTP.Url();
			this.url.port        = request.getServerPort(); //TODO : is server port right ?
			this.url.protocol    = request.getProtocol();
			this.url.urlString   = request.getRequestURL().toString();
			this.url.uriString   = request.getRequestURI();
			this.url.queryString = request.getQueryString();
			this.init();
		}
		
		@Override
		@SuppressWarnings("unchecked")
        public Map<String, String[]> getHeaders() {
			if(null == headers){
				super.getHeaders(); //create headers
				Enumeration<String> names = request.getHeaderNames();
				while(names.hasMoreElements()){
					String name = names.nextElement();
					ArrayList<String> header = new ArrayList<String>();
					Enumeration<String> values = request.getHeaders(name);
					while(values.hasMoreElements()){
						header.add(values.nextElement());
					}
					headers.put(name, header.toArray(new String[]{}));
				}
			}
			return headers;
        }
		
		@Override
        public Map<String, Cookie> getCookies() {
			if(null == cookies){
				super.getCookies(); //create cookies
				javax.servlet.http.Cookie[] servletCookies = request.getCookies();
				if(null != servletCookies ){
					for(javax.servlet.http.Cookie servletCookie : servletCookies){
						Cookie cookie = new Cookie(servletCookie.getName(),servletCookie.getValue());
						cookie.domain = servletCookie.getDomain();
						cookie.maxAge = servletCookie.getMaxAge();
						cookie.path   = servletCookie.getPath();
						cookie.secure = servletCookie.getSecure();
						
						cookies.put(cookie.name, cookie);
					}
				}
			}
	        return super.getCookies();
        }

		@Override
        public String getContentType() {
	        return request.getContentType();
        }
		
		@Override
		@SuppressWarnings("unchecked")
        public Map<String, String[]> getParameters() {
        	if(null == parameters){
        		parameters = request.getParameterMap();
        	}
	        return parameters;
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
        public String getContent() {
			if(null == content){
				try {
	                BufferedReader reader = request.getReader();
	                StringBuilder  buffer = new StringBuilder();
	                String line = null;
	                while((line = reader.readLine()) != null){
	                	buffer.append(line).append("\n");
	                }
	                content = buffer.toString();
                } catch (IOException e) {
                	throw new MvcException(e.getMessage(),e);
                }
			}
	        return super.getContent();
        }

		@Override
        public InputStream getInputStream() {
	        try {
	            return request.getInputStream();
            } catch (IOException e) {
            	throw new MvcException(e.getMessage(),e);
            }
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
		
		private void init(){
			context = getContextPath(request);			

			path = getUriString().substring(context.length());
			if(!path.startsWith("/")){
				path = "/" + path;
			}
		}
	}
	
	/**
	 * @since 1.0.0
	 */
	public final class ResponseImpl extends Response{
		private final HttpServletRequest  servletRequest;
        private final HttpServletResponse servletResponse;
		
		ResponseImpl(HttpServletRequest request,HttpServletResponse response,Module module){
			this.servletRequest  = request;
			this.servletResponse = response;
			this.externalResponse = response;
			this.encoding = module.getEncoding();
		}

		@Override
        public void setStatus(int status) {
			servletResponse.setStatus(status);
			super.setStatus(status);
        }
		
		@Override
        protected void forwardTo(String forwardPath) {
			try {
				servletRequest.getRequestDispatcher(forwardPath).forward(servletRequest, servletResponse);
            } catch (Exception e) {
            	throw new MvcException(e);
            }
        }

		@Override
        protected void redirectTo(String redirectUrl) {
			try {
	            servletResponse.sendRedirect(redirectUrl);
            } catch (Exception e) {
            	throw new MvcException(e);
            }
        }

		@Override
        public OutputStream getOut() {
			try {
				return servletResponse.getOutputStream();
			}catch(IOException e){
				throw new MvcException(e);
			}
        }

		@Override
        public void setContentType(String contentType) {
			servletResponse.setContentType(contentType);
        }

		@Override
        public String getContentType() {
			return servletResponse.getContentType();
		}

		@Override
        public void setHeader(String name, String value) {
			servletResponse.setHeader(name, value);
			super.setHeader(name, value);
        }

		@Override
        public void setCookie(String name, String value, String domain, String path, Integer maxAge) {
			javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(name, value);
			if(null != domain)cookie.setDomain(domain);
			if(null != path)cookie.setPath(path);
			if(null != maxAge)cookie.setMaxAge(maxAge);
			servletResponse.addCookie(cookie);
	        super.setCookie(name, value, domain, path, maxAge);
        }
	}
	
	public static final class SessionImpl extends Session {
		
		private final HttpSession session;
		
		SessionImpl(HttpSession session){
			this.session         = session;
			this.externalSession = session;
		}

		@Override
        public Object getAttribute(String key) {
	        return session.getAttribute(key);
        }

		@Override
        public String getId() {
	        return session.getId();
        }

		@Override
        public void removeAttribute(String key) {
			session.removeAttribute(key);
        }

		@Override
        public void setAttribute(String key, Object value) {
			session.setAttribute(key, value);
        }
	}
	
	private boolean isMatchPath(Request request , String pattern){
		if( null == pattern )
			return false ;
	
		return Pattern.matches(pattern.trim(), request.getPath().toLowerCase()) ;
	}
	
	private static String getContextPath(HttpServletRequest request){
		String contextPath = request.getContextPath();
		if("/".equals(contextPath)){
			contextPath = "";
		}
		return contextPath;
	}
}
