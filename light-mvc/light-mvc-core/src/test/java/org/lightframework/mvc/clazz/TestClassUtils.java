package org.lightframework.mvc.clazz;

import java.util.Date;
import java.util.List;

import org.lightframework.mvc.internal.clazz.ClassUtils;

import junit.framework.TestCase;

public class TestClassUtils extends TestCase {

	/**
	 * 获取参数名称-字节码
	 * 
	 * @throws Exception
	 */
	public void testGetMethodParameterNames() throws Exception{
		String[] paramsName = ClassUtils.getMethodParameterNames(User.class.getMethods()[0]) ;
		assertEquals(paramsName[0], "_userId_") ;
		assertEquals(paramsName[1], "userName") ;
		assertEquals(paramsName[2], "birth") ;
		assertEquals(paramsName[3], "age") ;
		assertEquals(paramsName[4], "firends") ;
	}
	
	public void testExtractPackageName() throws Exception{
		String packName = ClassUtils.extractPackageName(TestClassUtils.class.getName()) ;
		assertEquals(packName, "org.lightframework.mvc.clazz") ;
	}
	
	public void testFindAllClassNames() throws Exception{
		List<String> classNames = ClassUtils.findAllClassNames("org.lightframework.mvc.clazz") ;
		assertTrue(classNames.contains("org.lightframework.mvc.clazz.TestClassUtils")) ;
	}
	
	public static class User{
		public void addUser(String _userId_, String userName , Date birth , int age , List<User> firends ){
			
		}
	}

}
