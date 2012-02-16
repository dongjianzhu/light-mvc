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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * {@link ClazzLoader}
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class ClazzLoader extends java.lang.ClassLoader {

	private Class<?> callingClass;

	public ClazzLoader() {

	}

	public ClazzLoader(ClazzLoader parent) {
		super(parent);
	}

	public ClazzLoader(ClazzLoader parent, Class<?> callingClass) {
		this.callingClass = callingClass;
	}

	public ClazzLoader(Class<?> callingClass) {
		this.callingClass = callingClass;
	}

	@Override
	public final Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> foundClass = null;

		ClassLoader loader1 = Thread.currentThread().getContextClassLoader();
		if (null != loader1 && !loader1.equals(this)) {
			foundClass = loadClass(name, loader1);
		}

		if (null == foundClass && null != callingClass) {
			ClassLoader loader2 = callingClass.getClassLoader();
			if (!loader2.equals(loader1)) {
				foundClass = loadClass(name, loader2);
			}

			if (null == foundClass) {
				ClassLoader loader3 = ClazzLoader.class.getClassLoader();
				if (!loader3.equals(loader2) && !loader3.equals(loader1)) {
					foundClass = loadClass(name, loader3);
				}
			}
		}
		
		if(null == foundClass){
			foundClass = findClass(name);
		}

		return foundClass;
	}
	
	@Override
    public URL getResource(String name) {
		URL url = null;

		ClassLoader loader1 = Thread.currentThread().getContextClassLoader();
		if (null != loader1 && !loader1.equals(this)) {
			url = getResource(name,loader1);
		}

		if (null == url && null != callingClass) {
			ClassLoader loader2 = callingClass.getClassLoader();
			if (!loader2.equals(loader1)) {
				url = getResource(name,loader2);
			}

			if (null == url) {
				ClassLoader loader3 = ClazzLoader.class.getClassLoader();
				if (!loader3.equals(loader2) && !loader3.equals(loader1)) {
					url = getResource(name,loader3);
				}
			}
		}
		
		if(null == url && !name.startsWith("/")){
			url = getResource("/" + name);
		}

		return url;
    }

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		//XXX : just call Thread.currentThread().getContextClassLoader()
		return Thread.currentThread().getContextClassLoader().getResources(name);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		throw new ClassNotFoundException("class '" + name + "' not found");
	}
	
	private Class<?> loadClass(String name, ClassLoader loader) throws ClassNotFoundException {
		if (loader.getClass().isAssignableFrom(this.getClass())) {
			return ((ClazzLoader) loader).findClass(name);
		} else {
			try {
				return loader.loadClass(name);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
	}
	
	private URL getResource(String name,ClassLoader loader) {
		if (loader.getClass().isAssignableFrom(this.getClass())) {
			return ClazzLoader.class.getResource(name);
		} else {
			return loader.getResource(name);
		}
	}
}
