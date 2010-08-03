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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.helpers.MessageFormatter;

/**
 * i18n messages of mvc framework.
 *
 * @author fenghm (live.fenghm@gmail.com)
 */
public class I18n {
	private static final String BUNDLE_NAME = "org.lightframework.mvc.messages"; //$NON-NLS-1$
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * get i18n message by key
	 */
	public static String get(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static String get(String key,Object ... arguments){
		if(arguments.length > 0){
			return fmt(get(key), arguments);	
		}else{
			return get(key);
		}
	}
	
	public static String fmt(String message,Object ... arguments){
		return MessageFormatter.arrayFormat(message, arguments).getMessage();
	}
	
	/**
	 * the same as {@link #get(String)}
	 */
	public static String i18n(String key) {
		return get(key);
	}
	
	/**
	 * the same as {@link #get(String, Object...)}
	 */
	public static String i18n(String key,Object ... arguments){
		return get(key,arguments);
	}
}