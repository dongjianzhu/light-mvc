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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * defines http request and response 
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public final class HTTP {
	//---http header constants
	public static final String HEADER_NAME_AJAX_REQUEST  = "x-requested-with";
    public static final String HEADER_VALUE_AJAX_REQUEST = "XMLHttpRequest";
    public static final String HEADER_NAME_USER_AGENT    = "User-Agent";

	//---http content type constatns
	public static final String CONTENT_TYPE_TEXT       = "text/plain";
	public static final String CONTENT_TYPE_HTML       = "text/html";
	public static final String CONTENT_TYPE_CSS   	   = "text/css";
	public static final String CONTENT_TYPE_JAVASCRIPT = "text/javascript";
	public static final String CONTENT_TYPE_JSON_RFC   = "application/json"; //rfc4627
	public static final String CONTENT_TYPE_JSON_TEXT  = "text/plain";
	public static final String CONTENT_TYPE_XML        = "text/xml";

	/**
	 * http user agent
	 * @since 1.0.0
	 */
	public static class UserAgent {
		
		public static final UserAgent UNKNOW = new UserAgent("Unknow");
		
		protected String description;
		
		public UserAgent(){
			
		}
		
		public UserAgent(String description){
			this.description = description;
		}
		
		public boolean isMozilla(){
			//XXX : Mozilla User Agent
			return null != description && description.startsWith("Mozilla/");
		}
	}
	
	/**
	 * a http cookie
	 * @since 1.0.0
	 */
	public static class Cookie{
		protected String  name;
		protected String  domain;
		protected String  path;
		protected String  value;
		protected int     maxAge = -1;
		protected boolean secure = false;
		
		public Cookie(){
			
		}
		
		public Cookie(String name,String value){
			this.name  = name;
			this.value = value;
		}
		
		public Cookie(String name,String value,String domain,String path){
			this(name,value);
			this.domain = domain;
			this.path   = path;
		}
		
		public String getName() {
	    	return name;
	    }

		public String getDomain() {
	    	return domain;
	    }

		public String getPath() {
	    	return path;
	    }

		public String getValue() {
	    	return value;
	    }

		/**
		 * Returns the maximum age of the cookie, specified in seconds, By default, -1 indicating the cookie will persist until browser shutdown.
		 * 
		 * @return an integer specifying the maximum age of the cookie in seconds; if negative, means the cookie persists until browser shutdown
		 */
		public int getMaxAge() {
	    	return maxAge;
	    }

		public boolean isSecure(){
			return secure;
		}
	}
	
	/**
	 * a http url
	 * @since 1.0.0
	 */
	public static class Url {
		protected String urlString;   //full url 
		protected int    port;
		protected String uriString;   //path without 'http://server:port' and query string
		protected String protocol;    //url protocol such as 'http',no version
		protected String queryString;
		
		public String getUrlString() {
        	return urlString;
        }
		
		public int getPort() {
        	return port;
        }

		public String getUriString() {
        	return uriString;
        }
		
		public String getProtocol() {
        	return protocol;
        }
		
		public String getQueryString() {
        	return queryString;
        }
	}
	
	/**
	 * a http request
	 * @since 1.0.0
	 */
	public static class Request {
	    protected static final InheritableThreadLocal<Request> current = new InheritableThreadLocal<Request>();

	    protected Url      url;
	    protected String   context = "";
	    protected String   path    = "";
	    protected String   remoteAddress;
	    protected String   contentType;
	    protected String   method;
	    protected boolean  secure;
	    protected Response response;
	    protected Module   module;
	    protected Action   action;	    
	    protected Result   result;

	    protected UserAgent             userAgent;
	    protected Map<String, String>   headers;
	    protected Map<String, Cookie>   cookies;
	    protected Map<String, String[]> parameters;
	    protected Map<String, Object>   attributes;

	    public static Request current(){
	    	return current.get();
	    }
	    
	    static void reset(){
	    	current.set(null);
	    }
	    
	    protected Request(){
	    	
	    }
	    
		public Url getUrl() {
	    	return url;
	    }
		
		public String getUrlString(){
			return null != url ? url.getUrlString() : null;
		}
		
		public String getUriString(){
			return null != url ? url.getUriString() : null;
		}
		
		public String getContext() {
	    	return context;
	    }

		/**
		 * path startwith '/'
		 * @return url path without context and query string of this request
		 */
		public String getPath() {
	    	return path;
	    }
		
		public String getQueryString() {
	    	return null != url ? url.getQueryString() : null;
	    }

		public String getRemoteAddress() {
	    	return remoteAddress;
	    }

		public String getContentType() {
	    	return contentType;
	    }

		public String getMethod() {
	    	return method;
	    }

		public boolean isSecure() {
	    	return secure;
	    }
		
		public boolean isAjax(){
			//jQuery.ajax will send a header "X-Requested-With=XMLHttpRequest"
			return HEADER_VALUE_AJAX_REQUEST.equals(getHeader(HEADER_NAME_AJAX_REQUEST));
		}
		
		public Object getAttribute(String name){
			return getAttributes().get(name);
		}
		
		public void setAttribute(String name,Object value){
			getAttributes().put(name, value);
		}
		
		public Map<String,Object> getAttributes(){
			if(null == attributes){
				attributes = new HashMap<String, Object>();
			}
			return attributes;
		}
		
		public String getParameter(String name){
			String[] values = getParameters().get(name);
			if(values == null){
				return null;
			}else if(values.length == 1){
				return values[0];
			}else{
				return Utils.arrayToString(values);
			}
		}
		
		public String[] getParameterValues(String name){
			return getParameters().get(name);
		}
		
        public String[] getParameterNames(){
        	return (String[])getParameters().keySet().toArray(new String[]{});
        }
		
		public Map<String, String[]> getParameters() {
			if(null == parameters){
				parameters = new HashMap<String, String[]>();
			}
	    	return parameters;
	    }
		
		public String getHeader(String name){
			return getHeaders().get(name);
		}

		public Map<String, String> getHeaders() {
			if(null == headers){
				headers = new HashMap<String, String>();
			}
	    	return headers;
	    }

		public Cookie getCookie(String name){
			return getCookies().get(name);
		}

		public Map<String, Cookie> getCookies() {
			if(null == cookies){
				cookies = new HashMap<String, Cookie>();
			}
	    	return cookies;
	    }
		
		public UserAgent getUserAgent(){
			if(null == userAgent){
				String description = getHeader(HEADER_NAME_USER_AGENT);
				if(null == description){
					userAgent = UserAgent.UNKNOW;
				}else{
					userAgent = new UserAgent(description);
				}
			}
			return userAgent;
		}

		public Module getModule() {
	    	return module;
	    }
		
		public Action getAction(){
			return action;
		}
		
		public Result getResult(){
			return result;
		}
		
		public Response getResponse(){
			return response;
		}
	}
	
	/**
	 * a http response
	 * @since 1.0.0
	 */
	public static class Response {

	    protected int          status = 200;
	    protected String       encoding = Module.DEFAULT_ENCODING;
	    protected String       contentType;
	    protected OutputStream out;
	    
	    protected Map<String, String> headers;
	    protected Map<String, Cookie> cookies;
	    
	    protected Response(){
	    	
	    }
	    
	    public int getStatus() {
	    	return status;
	    }

	    public void setStatus(int status){
	    	this.status = status;
	    }
	    
		public String getContentType() {
	    	return contentType;
	    }

		public void setContentType(String contentType) {
	    	this.contentType = contentType;
	    }

		public String getHeader(String name){
			return getHeaders().get(name);
		}
		
		public void setHeader(String name,String value){
			getHeaders().put(name, value);
		}

		public Map<String, String> getHeaders() {
			if(null == headers){
				headers = new HashMap<String, String>();
			}
	    	return headers;
	    }

		public Cookie getCookie(String name){
			return getCookies().get(name);
		}
		
	    public void setCookie(String name, String value) {
	        setCookie(name, value, (Integer)null);
	    }

	    public void setCookie(String name, String value, Integer maxAge) {
	        setCookie(name, value, null, null, maxAge);
	    }
	    
	    public void setCookie(String name, String value, String domain, String path, Integer maxAge) {
	    	Cookie cookie = getCookie(name);
	        if (null != cookie) {
	            if(null != value ) { cookie.value  = value;  }
	            if(null != domain) { cookie.domain = domain; }
	            if(null != path  ) { cookie.path   = path;   }
	            if(null != maxAge) { cookie.maxAge = maxAge; }
	        } else {
	            cookie = new Cookie();
	            cookie.name   = name;
	            cookie.domain = domain;
	            cookie.path   = (null == path || "".equals(path) ? "/" : path);
	            cookie.maxAge = null == maxAge ?  -1 : maxAge;
	            cookies.put(name, cookie);
	        }
	    }

		public Map<String, Cookie> getCookies() {
			if(null == cookies){
				cookies = new HashMap<String, Cookie>();
			}
	    	return cookies;
	    }
		
		public final void redirect(String url){
			//TODO : parse url
			redirectTo(url);
		}
		
		public final void forward(String path){
			forwardTo(path);
		}
		
		public void write(String content) throws IOException{
			if(null != content){
	            getOut().write(content.getBytes(encoding));
			}
		}
		
		public void writeln(String content) throws IOException{
			if(null != content){
				getOut().write(content.getBytes(encoding + "\n"));
			}
		}
		
		public OutputStream getOut(){
			if(null == out){
				out = new ByteArrayOutputStream();
			}
			return out;
		}
		
		protected void forwardTo(String forwardPath){
			
		}
		
		protected void redirectTo(String redirectUrl){
			
		}
	}	
}
