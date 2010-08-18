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

/**
 * Test Case of {@link I18n}
 *
 * @author fenghm (live.fenghm@gmail.com)
 */
public class TestI18n extends TestCase {

	public void testFormt(){
		assertEquals("Hello xiaoming,you are welcome", I18n.fmt("Hello {},{}", "xiaoming","you are welcome"));
	}
	
}
