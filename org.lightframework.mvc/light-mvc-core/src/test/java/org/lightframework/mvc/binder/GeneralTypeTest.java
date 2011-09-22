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
package org.lightframework.mvc.binder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class GeneralTypeTest<E> {
	private int primitive_type;
	private Map<String, ? extends Integer> parameterized_type; // include
															   // WildcardType
	private E type_variable;
	private E[] array_type;
	
	private List<UserInfo> list ;
	
	public void getUserInfo(List<UserInfo> list){
	}

	public static void main(String... strings) {
		Class pojoClass = GeneralTypeTest.class;
		try { // get primitive type
			System.out.println("get primitive type-->");
			Field primitive_type_field = pojoClass.getDeclaredField("primitive_type");
			Type primitive_type = primitive_type_field.getGenericType();
			System.out.println("Type Class: " + primitive_type.getClass() + " Type: " + primitive_type); // get
																										 // parameterized
																										 // type
			System.out.println("\nget list type-->");
			Method method = pojoClass.getMethods()[1] ;
			
			Type[] types = method.getGenericParameterTypes() ;
			
			
			System.out.println("Method Name :" + method.getName());
			//Type parameterized_type = parameterized_type_field.getGenericType();
			System.out.println("Type Class: " + types[0]);
			
			System.out.println("\nget parameterized type-->");
			Field parameterized_type_field = pojoClass.getDeclaredField("parameterized_type");
			Type parameterized_type = parameterized_type_field.getGenericType();
			System.out.println("Type Class: " + parameterized_type.getClass() + " Type: " + parameterized_type); // get
																												 // WildcardType
			System.out.println("get actual types-->");
			ParameterizedType real_parameterized_type = (ParameterizedType) parameterized_type;
			Type[] actualTypes = real_parameterized_type.getActualTypeArguments();
			for (Type type : actualTypes) {
				System.out.println("Type Class: " + type.getClass() + " Type: " + type);
			} // get type variables
			System.out.println("\nget type variables-->");
			Field type_variable_field = pojoClass.getDeclaredField("type_variable");
			Type type_variable = type_variable_field.getGenericType();
			System.out.println("Type Class: " + type_variable.getClass() + " Type: " + type_variable); // get
																									   // array
																									   // type
			System.out.println("\nget array type-->");
			Field array_type_field = pojoClass.getDeclaredField("array_type");
			Type array_type = array_type_field.getGenericType();
			System.out.println("Type Class: " + array_type.getClass() + " Type: " + array_type);
		} catch (SecurityException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static class UserInfo{
		
	}
}