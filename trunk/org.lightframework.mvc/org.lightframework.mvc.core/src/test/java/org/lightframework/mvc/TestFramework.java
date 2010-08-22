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
public class TestFramework extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(TestFramework.class);
	
	public void testGetVersion(){
		log.info("mvc version:{}",Version.version_name);
		assertNotNull(Version.version_name);
	}
	
	public void testGetApplication(){
		assertNotNull(Framework.getApplication());
		
		Object context = new Object();
		Application application = new Application(context,new MockModule());
		assertNotNull(application.getContext());
		assertNotNull(application.getRootModule());
		Framework.setThreadLocalApplication(application);
		
		assertNotNull(Framework.getApplication());
		assertEquals(application, Framework.getApplication());
		assertEquals(application, Framework.getApplication(context));
		
		Framework.setThreadLocalApplication(null);
		assertNotSame(application, Framework.getApplication());
	}
}
