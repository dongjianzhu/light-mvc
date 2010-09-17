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
package org.lightframework.mvc.render.json;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * TODO : document me
 *
 * @author lixiaohong
 * @since 1.x.x
 */
public class TestJsonUtil extends TestCase {
	
	/*
	 *测试 Map ——> Bean的绑定
	 * */
	public void testSetValuesForBean() throws Exception{
		Map<String,Object>  map = new HashMap<String, Object>() ;
		
		Date date = new Date() ;
	
		map.put("userId", "testUserId") ;
		map.put("userName", "testUserName") ;
		map.put("birth", "2001-01-01") ;
		map.put("age", 30) ;
		
		User user = new User() ;
		JSONUtil.setValues(user, map) ;
		
		assertNotNull(user) ;
		assertEquals(user.getUserId(), "testUserId") ;
		assertEquals(user.getUserName(), "testUserName") ;
		//assertEquals(user.getBirth(), date) ;
		assertEquals(user.getAge(), 30) ;
		
		Object age = JSONUtil.getValue(user, "age") ;
		assertEquals(Integer.parseInt(String.valueOf(age)),30) ;
		
	}
	
	public static class User{
		String 	userId 	;
		String 	userName;
		Date   	birth 	;
		int 	age 	;
		/**
         * @return the userId
         */
        public String getUserId() {
        	return userId;
        }
		/**
         * @param userId the userId to set
         */
        public void setUserId(String userId) {
        	this.userId = userId;
        }
		/**
         * @return the userName
         */
        public String getUserName() {
        	return userName;
        }
		/**
         * @param userName the userName to set
         */
        public void setUserName(String userName) {
        	this.userName = userName;
        }
		/**
         * @return the birth
         */
        public Date getBirth() {
        	return birth;
        }
		/**
         * @param birth the birth to set
         */
        public void setBirth(Date birth) {
        	this.birth = birth;
        }
		/**
         * @return the age
         */
        public int getAge() {
        	return age;
        }
		/**
         * @param age the age to set
         */
        public void setAge(int age) {
        	this.age = age;
        }
		
	}
}
