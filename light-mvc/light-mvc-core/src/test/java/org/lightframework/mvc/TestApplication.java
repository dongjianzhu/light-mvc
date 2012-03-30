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

import junit.framework.TestCase;

import org.lightframework.mvc.test.MockModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Case of {@link Framework}
 * 
 * @author fenghm (live.fenghm@gmail.com)
 */
public class TestApplication extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(TestApplication.class);
	
	public void testGetVersion(){
		log.info("mvc version:{}",Version.version_name);
		assertNotNull(Version.version_name);
	}
	
	public void testGetApplication(){
		assertNotNull(Application.current());
		
		Object context = new Object();
		Application application = new Application(context,new MockModule());
		assertNotNull(application.getContext());
		assertNotNull(application.getRootModule());
		Application.setCurrent(application);
		
		assertNotNull(Application.current());
		assertEquals(application, Application.current());
		assertEquals(application, Application.currentOf(context));
		
		Application.setCurrent(null);
		assertNotSame(application, Application.current());
	}
}
