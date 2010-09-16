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

import junit.framework.TestCase;

import org.lightframework.mvc.PluginManager;
import org.lightframework.mvc.Result;
import org.lightframework.mvc.clazz.ClassUtils;
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
	
	private static boolean javassisted;
	private static boolean setUpOnce;

	protected static MockApplication application;
	
	protected MockModule      module;
	protected MockSession     session;
	protected MockRequest     request;
	protected MockResponse    response;
	protected boolean         ignored;
	protected boolean         managed;
	protected String          packagee;
	
	@Override
    protected final void setUp() throws Exception {
		
		log.info("===========================BEGIN:{}==============================",getName());
		
		this.packagee = this.getClass().getPackage().getName();
		
		if(null == module){
			module = new MockModule();
		}
		
		if(null == session){
			session = new MockSession();
		}
		
		if(null == application){
			application = new MockApplication(new Object(),module);
		}
		
		reset();
		
		MockApplication.mockSetCurrent(application);		
		if(!setUpOnce){
			setUpOnlyOnce();
			setUpOnce = true;
		}
		
	    setUpEveryTest();
	    
	    module.getPlugins().addAll(PluginManager.getPlugins());
	    MockFramework.mockStart(module);
    }
	
	@Override
    protected final void tearDown() throws Exception {
		tearDownEveryTest();
		
		MockFramework.mockHandleFinally(request, response);
		MockFramework.mockStop(module);
		MockApplication.mockSetCurrent(null);
		
		log.info("============================END:{}===============================",getName());
		log.info("EOT");//add blank line for viewing log better. (EOT : END OF TEST)
    }
	
	protected void setUpOnlyOnce() throws Exception{
		
	}

	protected void setUpEveryTest() throws Exception{
		
	}
	
	protected void tearDownEveryTest() throws Exception{
		
	}
	
	protected final Result request(String path) throws Exception {
		request.setPath(path);
		return execute();
	}
	
	protected final Result execute() throws Exception {
		MockApplication.mockSetCurrent(application);
		if(MockFramework.mockIgnore(request)){
			ignored = true;
			return null;
		}else{
			managed = MockFramework.mockHandle(request, response);
			return request.getResult();
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
		request   = new MockRequest(application,module);
		response  = new MockResponse();
		
		request.setResponse(response);
		request.setSession(session);
	}
	
	protected final Class<?> loadClass(String className) throws Exception {
		try {
	        return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
        	return null;
        }
	}
	
	protected final Class<?> createSubClass(Class<?> superClass,String subClassName) throws Exception{
		checkJavassist();
		
		log.debug("[mvc-test-case] -> create sub class '{}' of '{}'",subClassName,superClass.getName());
		Class<?> clazz = null;//module.loadClassForName(newClassName);
		
		if(null == clazz){
			javassist.ClassPool classPool  = javassist.ClassPool.getDefault();
			Thread.currentThread().setContextClassLoader(new Loader(classPool));
			
			javassist.CtClass ctSuperClass = classPool.get(superClass.getName());
			javassist.CtClass ctclass      = classPool.makeClass(subClassName,ctSuperClass);
			clazz  = ctclass.toClass();
			ctclass.detach();
			log.debug("[mvc-test-case] -> create sub class '{}'",subClassName);
		}
		
		module.addClassName(subClassName);
		
		log.debug("[mvc-test-case] -> sub class created");
		
		return clazz;
	}
	
	protected final void removeClass(Class<?> clazz){
		module.removeClassName(clazz.getName());
	}
	
	protected final void removeClass(String className){
		module.removeClassName(className);
	}
	
	protected final String getCurrentPackage(){
		return packagee;
	}
	
	private static void checkJavassist() throws ClassNotFoundException{
		if(!javassisted && null == ClassUtils.forName("javassist.ClassPool")){
			throw new ClassNotFoundException("javassist library required");
		}
	}
	
	public static final class Loader extends ClassLoader {
		
		private javassist.ClassPool pool;
		
		private Loader(javassist.ClassPool pool){
			this.pool = pool;
		}

		@Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
			javassist.CtClass ctClass = pool.getOrNull(name);
			if(null != ctClass){
	            try {
	                byte[] b = ctClass.toBytecode();
	                return defineClass(name, b, 0, b.length);
                } catch (Exception e) {
                	throw new RuntimeException(e.getMessage(),e);
                }
			}
	        return super.findClass(name);
        }
	}
}
