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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * configuration class of mvc framework
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 0.1
 */
public class Config {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.PARAMETER,ElementType.FIELD,ElementType.METHOD})
	public static @interface Format {
		public static final String DEFAULT_DATE      = "yyyy-MM-dd";
		public static final String DEFAULT_TIME      = "HH:mm:ss";
		public static final String DEFAULT_DATETIME  = "yyyy-MM-dd HH:mm:ss";
		public static final String DEFAULT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";
		
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Default {
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Before {
		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface After {
		
	}
}
