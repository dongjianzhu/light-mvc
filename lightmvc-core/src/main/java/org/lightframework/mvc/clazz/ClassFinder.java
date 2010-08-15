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
package org.lightframework.mvc.clazz;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * utility class to find classes or resources
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class ClassFinder {
	
	public static Set<String> findClassNames(URL url,String packagee) throws IOException{
		return findClassNames(url, packagee, true);
	}
	
	public static Set<String> findClassNames(URL url,String packagee,boolean deep) throws IOException{
		URLConnection conn = url.openConnection();
		if(conn instanceof JarURLConnection){
			return findClassNames(((JarURLConnection)conn).getJarFile(),packagee,deep);
		}
		
		String urlProtocol = url.getProtocol();
		String urlFileName = url.getFile();
		int jarStringIndex = urlFileName.indexOf("!/"); //such as xxx.jar!/org/apache/....
		
		if(jarStringIndex > 0){
			urlFileName = urlFileName.substring(0,jarStringIndex);
		}
		
		if(urlProtocol.equalsIgnoreCase("jar") || urlProtocol.equalsIgnoreCase("jar") || 
				 urlFileName.endsWith(".jar") || urlFileName.endsWith(".zip")){
			
			//some application servers such as jetty return the encoded url
			String jarFileName = URLDecoder.decode(urlFileName,System.getProperty("file.encoding"));
			File file = new File(jarFileName);
			if(file.exists()){
				//some application servers such as weblogic return not exists resource
				return findClassNames(new JarFile(file),packagee,deep);
			}
		}else{
			String fileName = URLDecoder.decode(urlFileName,System.getProperty("file.encoding"));
			File file = new File(fileName);
			if(file.exists()){
				return findClassNames(file,packagee,deep);
			}
		}
	
		return new HashSet<String>();
	}
	
	public static Set<String> findClassNames(JarFile jar,String packagee,boolean deep){
		String      path  = packagee.replace('.', '/') + "/";
		Set<String> names = new HashSet<String>();
		
		Enumeration<JarEntry> entries = jar.entries();
		while(entries.hasMoreElements()){
			JarEntry entry = entries.nextElement();
			String   name  = entry.getName();
			
			if(name.startsWith(path) ){
				if(!deep && name.indexOf("/",path.length()) > 0){
					break;
				}
				
	            int index = name.lastIndexOf(".class");
	            if(index > 0){
	            	names.add(name.substring(0,index).replace('/', '.'));
	            }
			}
		}
		return names;
	}
	
	public static Set<String> findClassNames(File dir,String packagee,boolean deep){
		Set<String> names = new LinkedHashSet<String>();
		for(File file : dir.listFiles()){
            String name = file.getName();
            if(name.charAt(0) == '.'){
            	//ignore .file such as .svn
            	continue;
            }
            
            int index = name.lastIndexOf(".class");
            if(index > 0){
            	names.add(packagee + "." + name.substring(0,index));
            	continue;
            }
            
            if(deep && file.isDirectory()){
            	names.addAll(findClassNames(file, packagee + "/" + file.getName(),deep));
            }
		}
		return names;
	}
}
