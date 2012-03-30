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
package org.lightframework.mvc.internal.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lightframework.mvc.internal.convert.Converter;

import junit.framework.TestCase;

/**
 * <code>{@link TestConvertOgnl}</code>
 *
 * @author fenghm (fenghm@bingosoft.net)
 *
 * @since 1.1.0
 */
public class TestConvertOgnl extends TestCase {

	public void testSimple(){
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("id",       "1");
		map.put("name",     "xiaoming");
		map.put("notfound", "notfound");
		
		map.put("child.id",   "2");
		map.put("child.name", "lisi");
		
		map.put("child.child.id",   "3");
		map.put("child.child.name", "zhangsan");

		OGUser user = (OGUser)Converter.convert(OGUser.class, map);	
		
		assertNotNull(user);
		
		OGUser child = user.child;
		
		assertNotNull(child);
		
		OGUser childChild = child.child;
		
		assertNotNull(child);
		
		OGUser childChildChild = child.child.child;
		
		assertNull(childChildChild);
		
		assertEquals("1", user.id);
		assertEquals("xiaoming", user.name);
		
		assertEquals("2", child.id);
		assertEquals("lisi", child.name);
		
		assertEquals("3", childChild.id);
		assertEquals("zhangsan", childChild.name);
	}
	
	public void testList(){
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("id",       "1");
		map.put("name",     "xiaoming");
		map.put("notfound", "notfound");
		
		map.put("child.id",   "2");
		map.put("child.name", "lisi");
		
		map.put("child.child.id",   "3");
		map.put("child.child.name", "zhangsan");
		
		map.put("childMap[id]", "5");
		map.put("childMap[name]", "xiaohua");	
		
		//string list
		map.put("strings[0]", "s1");
		map.put("strings[2]", "s3");
		
		//integer list
		map.put("integers[0]", "1");
		map.put("integers[1]", "2");
		
		//object list
		map.put("values[0]", "1");
		map.put("values[2]", "s2");
		
		//bean list
		map.put("childs[0].id",  "4");
		map.put("childs[0].name","xiaoming1");
		map.put("childs[2].id",  "5");
		map.put("childs[2].name","xiaoming2");
		
		//bean's bean 
		map.put("childs[0].child.id",      "6");
		map.put("childs[0].childs[0].id",  "7");
		
		//string array
		map.put("stringsArray[0]", "s1");
		map.put("stringsArray[2]", "s3");
		
		//integer array
		map.put("integersArray[0]", "1");
		map.put("integersArray[1]", "2");
		
		//object array
		map.put("valuesArray[0]", "1");
		map.put("valuesArray[2]", "s2");
		
		//bean array
		map.put("childsArray[0].id",  "4");
		map.put("childsArray[0].name","xiaoming1");
		map.put("childsArray[2].id",  "5");
		map.put("childsArray[2].name","xiaoming2");
		
		
		//string map
		map.put("stringsMap[id]",     "s1");
		map.put("stringsMap['name']", "s3");
		
		//integer map
		map.put("integersMap['int1']", "1");
		map.put("integersMap['int2']", "2");
		
		//object map
		map.put("valuesMap[o1]", "1");
		map.put("valuesMap[o2]", "s2");
		
		//bean map
		map.put("childsMap['u1'].id",  "4");
		map.put("childsMap['u1'].name","xiaoming1");
		map.put("childsMap[u2].id",  "5");
		map.put("childsMap[u2].name","xiaoming2");
		
//		long start = System.currentTimeMillis();
//		
//		for(int i=0;i<10000;i++){
//			OGUser user = (OGUser)Converter.convert(OGUser.class, map);			
//		}
//		
//		long end = System.currentTimeMillis();
//		
//		System.out.println((end - start) + "ms");
		
		OGUser user = (OGUser)Converter.convert(OGUser.class, map);		
		
		assertNotNull(user);
		
		OGUser child = user.child;
		
		assertNotNull(child);
		
		OGUser childChild = child.child;
		
		assertNotNull(child);
		
		OGUser childChildChild = child.child.child;
		
		assertNull(childChildChild);
		
		assertEquals("1", user.id);
		assertEquals("xiaoming", user.name);
		
		assertEquals("2", child.id);
		assertEquals("lisi", child.name);
		
		assertEquals("3", childChild.id);
		assertEquals("zhangsan", childChild.name);
		
		//string list
		assertEquals(3, user.strings.size());
		assertEquals("s1", user.strings.get(0));
		assertNull(user.strings.get(1));
		assertEquals("s3", user.strings.get(2));
		
		//integer list
		assertEquals(2, user.integers.size());
		assertEquals(new Integer(1), user.integers.get(0));
		assertEquals(new Integer(2), user.integers.get(1));		
		
		//object list
		assertEquals(3, user.values.size());
		assertEquals("1", user.values.get(0));
		assertNull(user.values.get(1));
		assertEquals("s2", user.values.get(2));
		
		//bean list
		assertEquals(3, user.childs.size());
		assertEquals("4", user.childs.get(0).id);
		assertEquals("xiaoming1", user.childs.get(0).name);
		assertNull(user.childs.get(1));
		assertEquals("5", user.childs.get(2).id);
		assertEquals("xiaoming2", user.childs.get(2).name);		
		
		//bean's bean
		OGUser childsChild0 = user.childs.get(0).child;
		OGUser childsChildsChild0 = user.childs.get(0).childs.get(0);
		assertEquals("6", childsChild0.id);
		assertEquals("7", childsChildsChild0.id);
		
		//string array
		assertEquals(3, user.stringsArray.length);
		assertEquals("s1", user.stringsArray[0]);
		assertNull(user.stringsArray[1]);
		assertEquals("s3", user.stringsArray[2]);
		
		//integer array
		assertEquals(2, user.integersArray.length);
		assertEquals(1, user.integersArray[0]);
		assertEquals(2, user.integersArray[1]);		
		
		//object array
		assertEquals(3, user.valuesArray.length);
		assertEquals("1", user.valuesArray[0]);
		assertNull(user.valuesArray[1]);
		assertEquals("s2", user.valuesArray[2]);
		
		//bean array
		assertEquals(3,   user.childsArray.length);
		assertEquals("4", user.childsArray[0].id);
		assertEquals("xiaoming1", user.childsArray[0].name);
		assertNull(user.childsArray[1]);
		assertEquals("5", user.childsArray[2].id);
		assertEquals("xiaoming2", user.childsArray[2].name);
		
		//childMap
		Map<String, Object> childMap = user.childMap;
		assertNotNull(childMap);
		assertEquals("5", childMap.get("id"));
		assertEquals("xiaohua", childMap.get("name"));
		
		//string map
		assertEquals(2, user.stringsMap.size());
		assertEquals("s1", user.stringsMap.get("id"));
		assertEquals("s3", user.stringsMap.get("name"));
		
		//integer map
		assertEquals(2, user.integersMap.size());
		assertEquals(new Integer(1), user.integersMap.get("int1"));
		assertEquals(new Integer(2), user.integersMap.get("int2"));		
		
		//object map
		assertEquals(2, user.valuesMap.size());
		assertEquals("1", user.valuesMap.get("o1"));
		assertEquals("s2", user.valuesMap.get("o2"));
		
		//bean map
		assertEquals(2,   user.childsMap.size());
		assertEquals("4", user.childsMap.get("u1").id);
		assertEquals("xiaoming1", user.childsMap.get("u1").name);
		assertEquals("5", user.childsMap.get("u2").id);
		assertEquals("xiaoming2", user.childsMap.get("u2").name);		
	}	
	
	public static final class OGUser {
		public String id;
		
		public String name;
		
		public List<String> strings;
		
		public List<Integer> integers;
		
		public List<Object> values;

		public List<OGUser> childs;
		
		public String[] stringsArray;
		
		public int[] integersArray;
		
		public Object[] valuesArray;
		
		public OGUser[] childsArray;

		public OGUser child;
		
		public Map<String, Object> childMap;
		
		public Map<String, String> stringsMap;
		
		public Map<String, Integer> integersMap;
		
		public Map<String, Object> valuesMap;
		
		public Map<String,OGUser> childsMap;
	}
}
