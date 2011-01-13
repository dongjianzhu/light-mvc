/*
 * Copyright 2011 the original author or authors.
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

import junit.framework.TestCase;

/**
 * TODO : document me
 *
 * @author User
 * @since 1.x.x
 */
public class TestDataFormat  extends TestCase {
	public void testDataFormat(){
		//DataFormat 目前没用上
		assertTrue(DataFormat.isJson(DataFormat.JSON_FORMAT));
		assertTrue(DataFormat.isXml(DataFormat.XML_FORMAT));
		assertEquals(DataFormat.getSupportedFormats(),"json,xml");
		assertTrue(DataFormat.isSupport(DataFormat.JSON_FORMAT));
		assertTrue(DataFormat.isSupport(DataFormat.XML_FORMAT));
	}
}
