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

import java.net.URL;
import java.util.Set;

import org.lightframework.mvc.internal.clazz.ClassFinder;
import org.lightframework.mvc.internal.clazz.ClazzLoader;

import test.resources.Resource1;

import junit.framework.TestCase;

/**
 * Test Case of {@link ClassFinder}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TetClassFinder extends TestCase {
	
	private ClazzLoader loader = new ClazzLoader();

	public final void testFindClassNamesInClasses() throws Exception{
		String packagee = TetClassFinder.class.getPackage().getName();
		URL url = loader.getResources(packagee.replace('.', '/')).nextElement();
		assertNotNull(url);
		Set<String> found = ClassFinder.findClassNames(url, packagee,false);
		assertNotNull(found);
		assertTrue(found.size() >= 1);
		
		boolean foundThis = false;
		for(String name : found){
			if(name.equals(this.getClass().getName())){
				foundThis = true;
				break;
			}
		}
		assertTrue(foundThis);
	}
	
	public final void testFindClassNamesInJar() throws Exception{
		String packagee = "test.resources";
		String resource = Resource1.class.getName();
		
		URL url = loader.getResource(resource.replace('.', '/') + ".class");
		assertNotNull(url);
		Set<String> found = ClassFinder.findClassNames(url, packagee,true);
		assertNotNull(found);
		assertTrue(found.size() == 5);
	}
	
	public final void testFindClassNamesInJarNotDeep() throws Exception{
		String packagee = "test.resources";
		String resource = Resource1.class.getName();
		
		URL url = loader.getResource(resource.replace('.', '/') + ".class");
		assertNotNull(url);
		Set<String> found = ClassFinder.findClassNames(url, packagee,false);
		assertNotNull(found);
		assertTrue(found.size() == 2);
	}
}
