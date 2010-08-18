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
package org.lightframework.mvc.test;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Case of {@link MockUrl}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestMockModule extends TestCase {
	private static final Logger log = LoggerFactory.getLogger(TestMockModule.class);

	@Override
    protected void setUp() throws Exception {
		String current = System.getProperty("user.dir");
		String webroot = current + "/target/webapp";
		String webinf  = webroot + "/WEB-INF";
		
		File webInfDir = new File(webinf);
		webInfDir.mkdirs();
    }
	
	@Override
    protected void tearDown() throws Exception {
		String current = System.getProperty("user.dir");
		String webroot = current + "/target/webapp";
		
		File webRootDir = new File(webroot);
		webRootDir.deleteOnExit();
    }

	public void testGetWebRootDir() {
		MockModule module = new MockModule();
	
		String webRootDir = module.getWebRootDir();
		assertNotNull(webRootDir);
		
		log.info("web root dir : {}",webRootDir);
		
		File webDir = new File(webRootDir);
		assertTrue(webDir.isDirectory());
		
		File webInfoDir = new File(webDir.getAbsoluteFile() + File.separator + "WEB-INF");
		assertTrue(webInfoDir.exists());
		assertTrue(webInfoDir.isDirectory());
	}
	
	public void testFindWebResources(){
		MockModule module = new MockModule();
		
		String thisPackage = TestMockModule.class.getPackage().getName();
		File thisFolder = new File(TestMockModule.class.getResource("/" + thisPackage.replaceAll("\\.", "/")).getFile());
		
		assertNotNull(thisFolder);
		
		module.setWebRootDir(thisFolder.getAbsolutePath());
		
		Collection<String> resources = module.findWebResources("/");
		File[] files = thisFolder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		});
		
		assertEquals(files.length, resources.size());
	}
}
