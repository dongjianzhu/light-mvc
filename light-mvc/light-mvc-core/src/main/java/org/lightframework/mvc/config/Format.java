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
package org.lightframework.mvc.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

/**
 * represents a format string of {@link Object} such {@link Date}.
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD,ElementType.METHOD})
public @interface Format {
	public static final String DEFAULT_DATE      = "yyyy-MM-dd";
	public static final String DEFAULT_TIME      = "HH:mm:ss";
	public static final String DEFAULT_DATETIME  = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DEFAULT_RFC_DATE  = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	String value();
}