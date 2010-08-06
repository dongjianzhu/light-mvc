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

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Utils;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * core plugin to resolve action's method
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class ResolvePlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(ResolvePlugin.class);

	@Override
    public boolean resolve(Request request, Response response, Action action) throws Exception{
		String controller = action.getControllerName();
		String methodName = action.getSimpleName();
		
		for(String pkg : request.getModule().getPackages()){
			if(resolve(request,response,action,controller,methodName,pkg)){
				return true;
			}
		}
		
		return false;
    }
	
	protected boolean resolve(Request request,Response response,Action action,String controller,String methodName,String pkg) throws Exception{
		//replace all '_' characters to '' characters
		controller = Utils.replace(controller, "_", "");
		methodName = Utils.replace(methodName, "_", "");
		
		//append "." to the end of _package if not empty 
		pkg = null == pkg || "".equals(pkg) ? "" : pkg + ".";
		
		//search controller class such as
		if(resolveControllerClass(request,pkg,controller,action)){
			//resolve method and arguments
			Method method = ClassUtils.findMethodIgnoreCase(action.getControllerClass(), methodName);
			
			if(null != method){
				if(!ClassUtils.isStatic(method)){
					Action.Setter.setControllerObject(action,ClassUtils.newInstance(action.getControllerClass()));
				}
				Action.Setter.setMethod(action,method);
				action.setArguments(ClassUtils.getMethodParameters(method));
				
				return true;
			}else{
				if(log.isTraceEnabled()){
					log.trace("[action:'{}'] -> method '{}' not exists in controller '{}'",
							  new Object[]{action.getName(),methodName,action.getControllerClass().getName()}
					);
				}
			}
		}
		
		return false;
	}
	
	private static boolean resolveControllerClass(Request request,String pkg,String controller,Action action) throws Exception{
		/*
        example : {user}.{list}
		 1. {module-package}.UserController        -> {module-package}.{controller}Controller
		 2. {module-package}.UserService           -> {module-package}.{controller}Service
		 3. {module-package}.user.UserController   -> {module-package}.{controller}.{controller}Controller
		 4. {module-package}.user.UserService      -> {module-package}.{controller}.{controller}Service
		 5. {module-package}.User                  -> {module-package}.{controller}
		 
		 example : {product.category}.{list}
		 1. {module-package}.product.CategoryController
		 2. {module-package}.product.CategoryService
		 3. {module-package}.product.category.CategoryController
		 4. {module-package}.product.category.CategoryService
		 5. {module-package}.product.Category
		 */
		
		String guessName1 = pkg + upperClassName(controller) + "Controller";
		String guessName2 = pkg + upperClassName(controller) + "Service";
		String guessName3 = pkg + controller + "." + upperClassName(controller) + "Controller";
		String guessName4 = pkg + controller + "." + upperClassName(controller) + "Service";
		String guessName5 = pkg + upperClassName(controller);
		
		if(log.isTraceEnabled()){
			log.trace("[action:'{}'] -> guess the names by orders :",action.getName());
			log.trace(" 1. '{}'",guessName1);
			log.trace(" 2. '{}'",guessName2);
			log.trace(" 3. '{}'",guessName3);
			log.trace(" 4. '{}'",guessName4);
			log.trace(" 5. '{}'",guessName5);
		}
		
		Class<?> clazz = 
			request.getModule().findClass(new String[]{guessName1,guessName2,guessName3,guessName4,guessName5});
		
		if(null != clazz){
			Action.Setter.setControllerClass(action, clazz);
			return true;
		}

		return false;
	}
	
	private static String upperClassName(String string){
		int index = string.lastIndexOf(".");
		if(index > 0){
			String pkg  = string.substring(0,index + 1);
			String name = string.substring(index + 1);
			return pkg + name.substring(0,1).toUpperCase() + name.substring(1);	 
		}else{
			return string.substring(0,1).toUpperCase() + string.substring(1);	
		}
	}
}
