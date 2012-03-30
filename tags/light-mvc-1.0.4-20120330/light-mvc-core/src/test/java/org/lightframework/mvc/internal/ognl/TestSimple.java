/*
 * Copyright 2012 the original author or authors.
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
package org.lightframework.mvc.internal.ognl;

import junit.framework.TestCase;

import static org.lightframework.mvc.internal.ognl.SimpleOgnl.*;

/**
 * <code>{@link TestSimple}</code>
 *
 * @author fenghm (fenghm@bingosoft.net)
 *
 * @since 1.1.0
 */
public class TestSimple extends TestCase {

	public void testSimpleOgnl(){
		assertEquals(null ,parse("name"));
		assertEquals("name[1]" ,deparse(parse("name[1]")));
		assertEquals("name.name" ,deparse(parse("name.name")));
		assertEquals("name.name.name" ,deparse(parse("name.name.name")));
		assertEquals("name[0].name.name" ,deparse(parse("name[0].name.name")));
		assertEquals("name[0].name.name[name]" ,deparse(parse("name[0].name.name[name]")));
		assertEquals("name[name].name.name[1]" ,deparse(parse("name['name'].name.name[1]")));
		assertEquals("name[0].name[1].name[name]" ,deparse(parse("name[0].name[1].name[name]")));
	}
	
}
