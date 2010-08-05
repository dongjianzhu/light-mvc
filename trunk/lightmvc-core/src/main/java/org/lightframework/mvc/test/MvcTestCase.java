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
package org.lightframework.mvc.test;

import java.io.ByteArrayInputStream;

import javassist.ClassPool;
import javassist.CtClass;
import junit.framework.TestCase;

/**
 * junit {@link TestCase} for mvc framework
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public abstract class MvcTestCase extends TestCase {

	protected MockModule    module;
	protected MockRequest   request;
	protected MockResponse  response;
	protected boolean       ignored;
	protected boolean       managed;
	
	@Override
    protected final void setUp() throws Exception {
		module = new MockModule();
		
		reset();
		
	    setUpTest();
	    
	    MockFramework.mockStart(module);
    }
	
	@Override
    protected final void tearDown() throws Exception {
		MockFramework.mockStop(module);
    }

	protected void setUpTest() throws Exception{
		
	}
	
	protected void tearDownTest() throws Exception{
		
	}
	
	protected final void execute() throws Exception {
		if(MockFramework.mockIgnore(request)){
			ignored = true;
		}else{
			managed = MockFramework.mockHandle(request, response);	
		}
	}
	
	protected final boolean isManaged(){
		return managed;
	}
	
	protected final boolean isIgnored(){
		return ignored;
	}
	
	protected final void reset (){
		managed   = false;
		ignored   = false;
		request   = new MockRequest(module);
		response  = new MockResponse();
	}
	
	protected final Class<?> loadClass(String className) throws Exception {
		try {
	        return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
        	return null;
        }
	}
	
	protected final Class<?> renameClass(Class<?> originalClass,String newClassName) throws Exception{
		CtClass ctclass = ClassPool.getDefault().getAndRename(originalClass.getName(), newClassName);
		Class<?> clazz  = ctclass.toClass();
		module.addClassName(clazz.getName());
		return clazz;
	}
	
	protected final Class<?> copyNewClass(Class<?> originalClass,String newClassName) throws Exception{
		CtClass oldClass = ClassPool.getDefault().get(originalClass.getName());
		ByteArrayInputStream classStream = new ByteArrayInputStream(oldClass.toBytecode());
		CtClass newClass = ClassPool.getDefault().makeClass(classStream);
		Class<?> clazz   = newClass.toClass();
		module.addClassName(clazz.getName());
		return clazz;
	}
	
	protected final void removeClass(Class<?> clazz){
		module.removeClassName(clazz.getName());
	}
	
	protected final void removeClass(String className){
		module.removeClassName(className);
	}
}
