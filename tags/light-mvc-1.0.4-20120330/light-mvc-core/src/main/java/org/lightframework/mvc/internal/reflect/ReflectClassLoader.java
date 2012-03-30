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
package org.lightframework.mvc.internal.reflect;

import java.lang.reflect.Method;

/**
 * <code>{@link ReflectClassLoader}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
final class ReflectClassLoader extends ClassLoader {
	ReflectClassLoader (ClassLoader parent) {
		super(parent);
	}

	protected synchronized java.lang.Class<?> loadClass (String name, boolean resolve) throws ClassNotFoundException {
	    if (name.equals(ReflectAccessor.class.getName())) {
		    return ReflectAccessor.class;
		}
		
		return super.loadClass(name, resolve);
	}

	Class<?> defineClass (String name, byte[] bytes) throws ClassFormatError {
		try {
			Method method = ClassLoader.class.getDeclaredMethod("defineClass", 
			                                                    new Class[] {String.class, byte[].class, int.class,int.class});
			
			method.setAccessible(true);
			
			return (Class<?>)method.invoke(getParent(), new Object[] {name, bytes, new Integer(0), new Integer(bytes.length)});
		} catch (Exception ignored) {
		    //do nothing
		}
		return defineClass(name, bytes, 0, bytes.length);
	}
}