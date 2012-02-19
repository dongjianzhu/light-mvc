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
package org.lightframework.mvc.plugin.cache;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.HTTP.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO : document me
 * 
 * @author lixiaohong
 * @since 1.x.x
 */
@Deprecated
public class CachePlugin extends Plugin {
	private static final Logger log = LoggerFactory
			.getLogger(CachePlugin.class);
	private long lastExpiresCalculated = 0;
	private static final int EXPIRES_HEADER_PRECISION = 60 * 60 * 24 * 1000; // one
																				// day
	private String lastExpiresFormatted;

	private String pattern;
	
	//day
	private Long  expires  = 365L;

	@Override
	public boolean ignore(Request request) throws Throwable {
		if (isValidRequest4Cache(request)) {

			log.debug(">>>>>>Cache URI {}", request.getUriString());

			request.getResponse().setHeader("Cache-Control", "Public");
			request.getResponse().setHeader("Expires", getExpiresFormatted());
			//request.addHeader("Expires", getExpiresFormatted());
			return true;
		}
		return false;
	}

	private boolean isValidRequest4Cache(Request request) {
		
		if( null == pattern )
			return false ;
		
		return Pattern.matches(pattern, request.getPath()) ;
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
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
     * @param expires the expires to set
     */
    public void setExpires(Long expires) {
    	this.expires = expires;
    }
}

