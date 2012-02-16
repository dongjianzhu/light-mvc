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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * represents current version of mvc framework.
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public final class Version {
	
	private static final String VERSION_FILE_NAME = 
		Version.class.getPackage().getName().replaceAll("\\.", "/") + "/version";
	
	private static final String VERSION_CLASS_NAME = 
		Version.class.getName().replaceAll("\\.", "/") + ".class";	
	
	public static final String version_name   = getVersionName();
	public static final String version_string = version_name + "[build time:" + getBuildTime() + "]";
	
	private Version(){
		
	}

	private static String getVersionName(){
		try {
	        return Utils.readFromResource(VERSION_FILE_NAME).trim();
        } catch (IOException e) {
        	throw new MvcException("error reading version",e);
        }
	}
	
	private static String getBuildTime(){
		URL url = Thread.currentThread().getContextClassLoader().getResource(VERSION_CLASS_NAME);
		if(null == url){
			url = Version.class.getResource(VERSION_CLASS_NAME);
		}
		
		//create file from url
		String fileName = url.getFile();
		int index = fileName.indexOf("!/");//jar file separator
		if(index > 0){
			fileName = fileName.substring(0,index);
		}
		if(fileName.startsWith("file:")){
			fileName = fileName.substring("file:".length());
		}
		if(fileName.contains(":/") && fileName.startsWith("/")){
			fileName = fileName.substring(1);
		}
		
		File file = new File(fileName);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()));
	}
}
