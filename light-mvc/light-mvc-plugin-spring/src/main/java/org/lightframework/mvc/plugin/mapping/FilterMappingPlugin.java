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
package org.lightframework.mvc.plugin.mapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.HTTP.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 设置MvcFilter过滤器Mapping匹配规则
 * 
 *
 * @author lixiaohong
 * @since 1.x.x
 */
public class FilterMappingPlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(FilterMappingPlugin.class);
	
	private long lastExpiresCalculated = 0;
	private static final int EXPIRES_HEADER_PRECISION = 60 * 60 * 24 * 1000; // one
	private String lastExpiresFormatted;
	private Long  expires  = 365L;//day
	
	private String includesPattern ;
	private String excludesPattern ;
	private String cachePattern ;


	@Override
	public boolean ignore(Request request) throws Throwable {
		
		if(isCacheRequest(request)){
			request.getResponse().setHeader("Cache-Control", "Public");
			request.getResponse().setHeader("Expires", getExpiresFormatted());
		}
		
		if( isIncludeRequest(request) ){
			return false ;
		}
		
		if( isExcludeRequest(request) ){
			log.debug(" URL {} ignored ",request.getPath()) ;
			return true ;
		}
		
		return false ;
	}
	
	private boolean isCacheRequest(Request request){
		if( null == cachePattern )
				return false ;
		
		return Pattern.matches(cachePattern.trim(), request.getPath().toLowerCase()) ;
	}

	private boolean isIncludeRequest(Request request){
		if( null == includesPattern )
				return false ;
		
		return Pattern.matches(includesPattern.trim(), request.getPath().toLowerCase()) ;
	}
	
	private boolean isExcludeRequest(Request request){
		if( null == excludesPattern )
			return false ;
	
		return Pattern.matches(excludesPattern.trim(), request.getPath().toLowerCase()) ;
	}
	
	private String getExpiresFormatted() {
		long now = System.currentTimeMillis();
		if (lastExpiresCalculated + EXPIRES_HEADER_PRECISION > now)
			return lastExpiresFormatted;

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss Z", java.util.Locale.ENGLISH);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		lastExpiresCalculated = now;
		lastExpiresFormatted = simpleDateFormat.format(new Date(System
				.currentTimeMillis()
				+ expires* 60 * 60 * 24 * 1000));
		return lastExpiresFormatted;
	}
	/**
     * @return the includesPattern
     */
    public String getIncludesPattern() {
    	return includesPattern;
    }

	/**
     * @param includesPattern the includesPattern to set
     */
    public void setIncludesPattern(String includesPattern) {
    	this.includesPattern = includesPattern;
    }

	/**
     * @return the excludesPattern
     */
    public String getExcludesPattern() {
    	return excludesPattern;
    }

	/**
     * @param excludesPattern the excludesPattern to set
     */
    public void setExcludesPattern(String excludesPattern) {
    	this.excludesPattern = excludesPattern;
    }
	
	public String getCachePattern() {
    	return cachePattern;
    }

	public void setCachePattern(String cachePattern) {
    	this.cachePattern = cachePattern;
    }
	
	/**
     * @param expires the expires to set
     */
    public void setExpires(Long expires) {
    	this.expires = expires;
    }

}
