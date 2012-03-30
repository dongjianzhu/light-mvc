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
package org.lightframework.mvc.binder;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.lightframework.mvc.binding.Argument;
import org.lightframework.mvc.binding.Binder;

/**
 * TODO : document me
 *
 * @author lixiaohong
 * @since 1.x.x
 */
public class TestBinder extends TestCase {
	//private Binder binder = new Binder() ;

	public  final void  testResolveArguments() throws Exception{
		Argument[] arguments = Binder.resolveArguments(ArgBinder.class.getMethods()[0]) ;
		assertNotNull(arguments) ;
		assertTrue(arguments.length == 5);
		assertTrue(arguments[0].getName().equals("paramString")) ;
		assertTrue(arguments[1].getName().equals("paramInt")) ;
		assertTrue(arguments[2].getName().equals("paramDouble")) ;
		assertTrue(arguments[3].getName().equals("paramDate")) ;
		assertTrue(arguments[4].getName().equals("listUser")) ;
		assertTrue( arguments[0].getType() == String.class );
		assertTrue( arguments[1].getType() == int.class );
		assertTrue( arguments[3].getType() == Date.class );
		assertTrue( arguments[2].getType() == double.class );
		assertTrue( arguments[4].getType() == List.class );
	
	}

	//public  final void  testBindingMethodIBindingContext()  throws Exception{
	//	fail("Not yet implemented");
	//}
	/*
	public  final void  testBindingArgumentIBindingContext() throws Exception{
		fail("Not yet implemented");
	}

	public  final void  testBindingArgumentObjectIBindingContext()  throws Exception{
		fail("Not yet implemented");
	}
	*/
	public static class ArgBinder{
		
		public void primType(String paramString , int paramInt , double paramDouble,Date paramDate ,List<User> listUser){
			//do nothing
		}
	}
	
	public static class User{
	}
	
}
