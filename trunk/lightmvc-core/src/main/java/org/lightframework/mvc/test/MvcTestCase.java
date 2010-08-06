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

import javassist.ClassPool;
import javassist.CtClass;
import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * junit {@link TestCase} for mvc framework
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public abstract class MvcTestCase extends TestCase {
	private static final Logger log = LoggerFactory.getLogger(MvcTestCase.class);
	
	private static boolean setUpOnce;

	protected MockModule    module;
	protected MockRequest   request;
	protected MockResponse  response;
	protected boolean       ignored;
	protected boolean       managed;
	
	@Override
    protected final void setUp() throws Exception {
		
		log.info("===========================BEGIN==============================");
		
		module = new MockModule();
		
		reset();
		
		if(!setUpOnce){
			setUpOnlyOnce();
			setUpOnce = true;
		}
		
	    setUpEveryTest();
	    
	    MockFramework.mockStart(module);
    }
	
	@Override
    protected final void tearDown() throws Exception {
		MockFramework.mockStop(module);
		
		log.info("============================END===============================");
		log.info("EOT");//add blank line for viewing log better. (EOT : END OF TEST)
    }
	
	protected void setUpOnlyOnce() throws Exception{
		
	}

	protected void setUpEveryTest() throws Exception{
		
	}
	
	protected void tearDownEveryTest() throws Exception{
		
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
	
	protected final Class<?> newCopiedClass(Class<?> originalClass,String newClassName) throws Exception{
		Class<?> clazz = module.loadClassForName(newClassName);
		
		if(null == clazz){
			CtClass ctclass = ClassPool.getDefault().getAndRename(originalClass.getName(), newClassName);
			clazz  = ctclass.toClass();
		}
		
		module.addClassName(newClassName);
		
		return clazz;
	}
	
	protected final Class<?> newChildClass(Class<?> superClass,String childClassName) throws Exception{
		Class<?> clazz = module.loadClassForName(childClassName);
		
		if(null == clazz){
			CtClass ctSuperClass = ClassPool.getDefault().get(superClass.getName());
			CtClass newClass = ClassPool.getDefault().makeClass(childClassName,ctSuperClass);
			clazz = newClass.toClass();
		}

		module.addClassName(childClassName);
		
		return clazz;
	}
	
	protected final void removeClass(Class<?> clazz){
		module.removeClassName(clazz.getName());
	}
	
	protected final void removeClass(String className){
		module.removeClassName(className);
	}
}
