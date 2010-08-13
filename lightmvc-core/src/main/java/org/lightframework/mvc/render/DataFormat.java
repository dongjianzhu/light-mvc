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
package org.lightframework.mvc.render;

/**
 * defines data format
 * 
 * @author fenghm (fenghm@bingosoft.net)
 * @since 1.0.0
 */
public final class DataFormat {

	public static final String JSON_FORMAT = "json";
	public static final String XML_FORMAT  = "xml";

	private static final String SUPPORTED_FORMATS = JSON_FORMAT + "," + XML_FORMAT;
	
	public static boolean isSupport(String format){
		return JSON_FORMAT.equals(format) || XML_FORMAT.equals(format);
	}
	
	public static String getSupportedFormats(){
		return SUPPORTED_FORMATS;
	}
	
	public static boolean isJson(String format){
		return JSON_FORMAT.equals(format);
	}
	
	public static boolean isXml(String format){
		return XML_FORMAT.equals(format);
	}
	
}
