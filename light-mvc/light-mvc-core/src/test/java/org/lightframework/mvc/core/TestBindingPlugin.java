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
package org.lightframework.mvc.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.lightframework.mvc.HTTP;
import org.lightframework.mvc.Result;
import org.lightframework.mvc.config.Format;
import org.lightframework.mvc.test.MvcTestCase;

/**
 * Test Case of {@link BindingPlugin}
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class TestBindingPlugin extends MvcTestCase {

	@Override
	protected void setUpEveryTest() throws Exception {
		module.setPackagee(packagee);
		createSubClass(BindingController.class, packagee + "." + BindingController.class.getSimpleName());

	}

	public void testNumberPrimitiveBinding() throws Throwable {
		request.setPath("/binding/number_primitive");
		request.setParameter("intParam", "1");
		request.setParameter("longParam", "2");
		request.setParameter("floatParam", "3.1");
		request.setParameter("doubleParam", "4.2");
		request.setParameter("bigDecimal", "5");
		request.setParameter("bigInt", "6");

		Result result = execute();

		assertNotNull(result);
		assertEquals(true, result.getValue());
	}

	public void testNumberObjectBinding() throws Throwable {
		request.setPath("/binding/number_object");
		request.setParameter("intParam", "1");
		request.setParameter("longParam", "2");
		request.setParameter("floatParam", "3.1");
		request.setParameter("doubleParam", "4.2");

		Result result = execute();

		assertNotNull(result);
		assertEquals(true, result.getValue());
	}
	
	public void testStringBinding() throws Throwable {
		request.setPath("/binding/string");
		request.setParameter("stringParam", "string");
		request.setParameter("charParam1", "c");
		request.setParameter("charParam2", "d");
		
		Result result = execute();
		
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}
	
	public void testBoolAndByteBinding() throws Throwable {
		request.setPath("/binding/bool_and_byte");
		request.setParameter("ok1", "true");
		request.setParameter("ok2", "0");
		request.setParameter("b1", "01");
		request.setParameter("b2", "0x0F");
		
		Result result = execute();
		
		assertNotNull(result);
		assertEquals(true, result.getValue());		
	}
	
	public void testDateBinding() throws Throwable {
		request.setPath("/binding/date");
		request.setParameter("time", "10:10:10");
		request.setParameter("sqlDate", "2010-10-10");
		request.setParameter("date", "2010-10-10 10:10:10.111");
		request.setParameter("date2","20101010");
		request.setParameter("timestamp", "2011-11-11 11:11:11.222");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}
	
	public void testArrayBinding() throws Throwable {
		request.setPath("/binding/array");
		request.setParameter("values", "1,2,3");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
		
		request.setParameter("values", new String[]{"1","2","3"});
		result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
		
		request.setPath("/binding/array3");
		request.removeParameter("values");
		result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}
	
	public void testDirectArrayBinding() throws Throwable {
		request.setPath("/binding/array1");
		request.setMethod(HTTP.METHOD_POST);
		request.setContentType(HTTP.CONTENT_TYPE_JSON);
		request.setContent("{values:['1','2','3']}");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}	
	
	public void testBeanArrayBinding() throws Throwable {
		request.setPath("/binding/array2");
		request.setMethod(HTTP.METHOD_POST);
		request.setContentType(HTTP.CONTENT_TYPE_JSON);
		request.setContent("{users:[{name:'n1',age:100},{name:'n2',age:200}]}");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}	
	
	public void testBeanArrayBinding1() throws Throwable {
		request.setPath("/binding/array2");
		request.setMethod(HTTP.METHOD_POST);
		request.setContentType(HTTP.CONTENT_TYPE_JSON);
		request.setContent("[{name:'n1',age:100},{name:'n2',age:200}]");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}
	
	public void testBeanListBinding() throws Throwable {
		request.setPath("/binding/list2");
		request.setMethod(HTTP.METHOD_POST);
		request.setContentType(HTTP.CONTENT_TYPE_JSON);
		request.setContent("{users:[{name:'n1',age:100},{name:'n2',age:200}]}");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}
	
	public void testCombine1() throws Throwable {
		request.setPath("/binding/combine1");
		request.setMethod(HTTP.METHOD_POST);
		request.setContentType(HTTP.CONTENT_TYPE_JSON);
		request.setContent("{size:3,users:[{name:'n1',age:100},{name:'n2',age:200}],values:[1,2,3]}");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}	
	
	public void testEnumBinding() throws Throwable {
		request.setPath("/binding/enums");
		request.setParameter("color", "RED");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());		
	}
	
	public void testMapBinding() throws Throwable {
		request.setPath("/binding/map");
		request.setParameter("hello", "you are welcome");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());	
	}
	
	public void testBeanBinding() throws Throwable {
		request.setPath("/binding/bean");
		request.setParameter("name", "xiaoming");
		request.setParameter("age", "100");
		request.setParameter("birthday", "2010-10-10");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}
	
	public void testOgnlBinding() throws Throwable {
		request.setPath("/binding/ognl");
		request.setParameter("name", "xiaoming");
		request.setParameter("age", "100");
		request.setParameter("birthday", "2010-10-10");
		request.setParameter("child.name", "xiaohua");
		request.setParameter("childs[0].name", "zhangsan");
		request.setParameter("childs[2].name", "lisi");		
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}
	
	public void testOgnlsBinding() throws Throwable {
		request.setPath("/binding/ognls");

		request.setParameter("user1.name", "xiaoming");
		request.setParameter("user1.age", "100");
		request.setParameter("user1.birthday", "2010-10-10");
		request.setParameter("user1.child.name", "xiaohua");
		
		request.setParameter("user2.name", 		 "xiaohua");
		request.setParameter("user2.age", 		 "200");
		request.setParameter("user2.child.name", "xiaoming");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}	
	
	public void testOgnls1Binding() throws Throwable {
		request.setPath("/binding/ognls1");

		request.setParameter("name", "xiaoming");
		request.setParameter("age", "100");
		request.setParameter("user1.birthday", "2010-10-10");
		request.setParameter("child.name", "xiaohua");
		request.setParameter("user1.child.age", "200");
		
		request.setParameter("id", 		 "1,2");
		
		request.setParameter("users[0].name", "xxx1");
		request.setParameter("users[1].name", "xxx2");
		
		Result result = execute();
		assertNotNull(result);
		assertEquals(true, result.getValue());
	}

	public static class BindingController {

		public static boolean numberPrimitive(int intParam, long longParam, float floatParam, double doubleParam, BigDecimal bigDecimal, BigInteger bigInt) {

			if (intParam == 1 && longParam == 2 && floatParam == 3.1f && doubleParam == 4.2 && bigDecimal.intValue() == 5 && bigInt.intValue() == 6) {

				return true;
			} else {
				return false;
			}
		}

		public static boolean numberObject(Integer intParam, Long longParam, Float floatParam, Double doubleParam) {
			if (intParam == 1 && longParam == 2 && floatParam == 3.1f && doubleParam == 4.2) {
				return true;
			} else {
				return false;
			}
		}
		
		public static boolean string(String stringParam,char charParam1,Character charParam2) {
			if(stringParam.equals("string") && charParam1 == 'c' && charParam2 == 'd'){
				return true;
			}else{
				return false;
			}
		}
		
		public static boolean date(Time time,java.sql.Date sqlDate,
				                   java.util.Date date,@Format("yyyyMMdd") java.util.Date date2,Timestamp timestamp) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			
			if(formatter.format(time).equals("19700101101010000") && 
			   formatter.format(sqlDate).equals("20101010000000000") && 
			   formatter.format(date).equals("20101010101010111") && 
			   formatter.format(date2).equals("20101010000000000") &&
			   formatter.format(timestamp).equals("20111111111111222")) {
				return true;
			}
			return false;
		}
		
		public static boolean boolAndByte(boolean ok1,Boolean ok2,byte b1,Byte b2){
			if(ok1 && (null != ok2 && ok2 == false) && b1 == 1 && b2 == 15){
				return true;
			}else{
				return false;
			}
		}
		
		public static boolean array(int[] values){
			if(values.length == 3 && values[0] == 1 && values[1] == 2 && values[2] == 3){
				return true;
			}
			return false;
		}
		
		public static boolean array3(int[] values){
			return values.length == 0;
		}
		
		public static boolean array1(int[] values){
			if(values.length == 3 && values[0] == 1 && values[1] == 2 && values[2] == 3){
				return true;
			}
			return false;
		}	
		
		public static boolean array2(User[] users){
			if(null != users && users.length == 2){
				User user1 = users[0];
				User user2 = users[1];
				
				if(user1.getName().equals("n1") && user1.getAge() == 100 &&
				   user2.getName().equals("n2") && user2.getAge() == 200){
					
					return true;
				}
				
			}
			return false;
		}	
		
		public static boolean list2(List<User> users){
			if(null != users && users.size() == 2){
				User user1 = users.get(0);
				User user2 = users.get(1);
				
				if(user1.getName().equals("n1") && user1.getAge() == 100 &&
				   user2.getName().equals("n2") && user2.getAge() == 200){
					
					return true;
				}
				
			}
			return false;
		}
		
		public static boolean combine1(int size,int[] values,List<User> users){
			return size == 3 && array(values) && list2(users);
		}		
		
		public static boolean enums(Color color){
			if(null != color && color == Color.RED){
				return true;
			}
			return false;
		}
		
		public static boolean map(Map<String,Object> params){
			return null != params && params.size() == 1 && (params.get("hello").equals("you are welcome"));
		}
		
		public static boolean bean(User user){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			if(null != user && 
			   "xiaoming".equals(user.getName()) && 
			   100 == user.getAge() && 
			   "20101010".equals(formatter.format(user.getBirthday()))){
				return true;
			}
			return false;
		}
		
		public static boolean ognl(User user){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			if(null != user && 
			   "xiaoming".equals(user.getName()) && 
			   100 == user.getAge() && 
			   "20101010".equals(formatter.format(user.getBirthday()))){
				
				User child = user.child;

				if(null == child || !"xiaohua".equals(child.getName())){
					return false;
				}
				
				List<User> childs = user.childs;
				
				if(null == childs || childs.size() != 3){
					return false;
				}
				
				User childs0 = childs.get(0);
				User childs1 = childs.get(1);
				User childs2 = childs.get(2);
				
				if(null == childs0 || null != childs1 || null == childs2){
					return false;
				}
				
				if(!"zhangsan".equals(childs0.getName())){
					return false;
				}
				
				if(!"lisi".equals(childs2.getName())){
					return false;
				}
				
				return true;
			}
			return false;
		}
		
		public static boolean ognls(User user1,User user2){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			if(null != user1 && 
			   "xiaoming".equals(user1.getName()) && 
			   100 == user1.getAge() && 
			   "20101010".equals(formatter.format(user1.getBirthday()))){
				
				User child = user1.child;

				if(null == child || !"xiaohua".equals(child.getName())){
					return false;
				}
				
				if(null == user2){
					return false;
				}
				
				if(!"xiaohua".equals(user2.getName())){
					return false;
				}
				
				if(200 != user2.getAge()){
					return false;
				}
				
				if(!"xiaoming".equals(user2.child.getName())){
					return false;
				}
				
				return true;
			}
			return false;
		}
		
		public static boolean ognls1(User user1,String[] id,User[] users){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			if(null != user1 && 
			   "xiaoming".equals(user1.getName()) && 
			   100 == user1.getAge() && 
			   "20101010".equals(formatter.format(user1.getBirthday()))){
				
				User child = user1.child;

				if(null == child || !"xiaohua".equals(child.getName())){
					return false;
				}
				
				if(null != users && users.length == 2){
					if(users[0].name.equals("xxx1") && users[1].name.equals("xxx2")){
						return true;	
					}
				}
			}
			return false;
		}
	}
	
	public static class User {
		
		private String name;
		private int    age;
		private Date   birthday;
		
		public User child;
		
		public List<User> childs;
		
		public String getName() {
        	return name;
        }
		public void setName(String name) {
        	this.name = name;
        }
		public int getAge() {
        	return age;
        }
		public void setAge(int age) {
        	this.age = age;
        }
		public Date getBirthday() {
        	return birthday;
        }
	}
	
	public static enum Color {
		RED,
		BLUE,
		YELLOW;
	}
}
