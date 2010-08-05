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

import java.lang.reflect.Method;
import java.util.Collection;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Assert;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Utils;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.utils.ClassUtils;

/**
 * core plugin to resolve action's method
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class ResolvePlugin extends Plugin {

	@Override
    public boolean resolve(Request request, Response response, Action action) throws Exception{
		String fullActionName = action.getName();
		String[] actionNames  = fullActionName.split(",");
		for(String name : actionNames){
			//replace all '_' characters to '' characters
			name = Utils.replace(name, "_", "");
			if(resolve(request,response,action,name)){
				action.setName(name);
				return true;
			}
		}
		return false;
	}
	
	protected boolean resolve(Request request,Response response,Action action,String fullActionName) throws Exception{
		//resolve controller and action method
		int lastDotIndex = fullActionName.lastIndexOf(".");
		
		Assert.isTrue(lastDotIndex > 0, "@ActionName.NoControllerDefined",fullActionName);
		
		String controller = fullActionName.substring(0,lastDotIndex);
		String methodName = fullActionName.substring(lastDotIndex + 1);
		String _package   = request.getModule().getPackage();
		
		//append "." to the end of _package if not empty 
		_package = null == _package || "".equals(_package) ? "" : _package + ".";
		
		//search controller class
		/*
		 search for classes such as :
		 1. app.controllers.{controller}Controller -> app.controllers.ProductController | app.controllers.product.CategoryController
		 2. app.controllers.{controller}           -> app.controllers.Product           | app.controllers.product.Category
		 3. app.{controller}Controller             -> app.ProductController   | demo.product.CategoryController 
		 4. app.{controller}.Controller            -> app.product.Controller  | demo.product.category.Controller
		 */
		boolean resolved = false;
		if(find1and2(request,_package,controller,action)){
			//resolve method and arguments
			Method method = ClassUtils.findMethodIgnoreCase(action.getClazz(), methodName);
			
			if(null != method){
				if(!ClassUtils.isStatic(method)){
					action.setController(ClassUtils.newInstance(action.getClazz()));
				}
				action.setMethod(method);
				action.setArguments(ClassUtils.getMethodParameters(method));
				resolved = true;
			}
		}
		
		return resolved;
    }
	
	private static boolean find1and2(Request request,String prefix,String controller,Action action) throws Exception{
		//{app-package}.controllers.{controller}Controller
		String guessClassName1 = prefix + "controllers." + controller + "Controller";
		
		//{app-package}.controllers.{controller}
		String guessClassName2 = prefix + "controllers." + controller;
		
		String pkgName = ClassUtils.extractPackageName(guessClassName1);
		Collection<String> classes = request.getModule().getClassNames(pkgName, false);
		
		for(String className : classes){
			if(className.equalsIgnoreCase(guessClassName1)){
				action.setClazz(ClassUtils.forName(className));
				return true;
			}else if(className.equalsIgnoreCase(guessClassName2)){
				action.setClazz(ClassUtils.forName(className));
				return true;
			}
		}
		return false;
	}
	
	/*
	private static boolean find3(Request request,String pkg,String controller,Action action) throws Exception{
		//{app-package}.{controller}Controller
		String guessClassName  = pkg + controller + "Controller";
		Clazz  controllerClazz = request.getApplication().getClazz(guessClassName);
		if(null != controllerClazz){
			action.setClazz(controllerClazz.Class());
			return true;
		}
		return false;
	}
	
	private static boolean find4(Request request,String pkg,String controller,Action action) throws Exception{
		//{app-package}.{controller}Controller
		String guessClassName = pkg + controller + ".Controller";
		String realClassName  = ClassUtils.findClassNameIgnoreCase(guessClassName);
		if(null != realClassName){
			action.setClazz(ClassUtils.forName(realClassName));
			return true;
		}
		return false;
	}
	*/
}
