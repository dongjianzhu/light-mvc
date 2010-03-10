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

import java.util.ArrayList;
import java.util.List;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Action.Argument;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.binding.ArrayBinder;
import org.lightframework.mvc.binding.BeanBinder;
import org.lightframework.mvc.binding.Binder;
import org.lightframework.mvc.binding.EnumBinder;
import org.lightframework.mvc.binding.TypeBinder;
import org.lightframework.mvc.utils.ClassUtils;

/**
 * the core plugin to binding parameters in request of action method.
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public class BindingPlugin extends Plugin {
	
	private static final List<Binder> binders = new ArrayList<Binder>();
	
	static {
		binders.add(new ArrayBinder());
		binders.add(new TypeBinder());
		binders.add(new EnumBinder());
		binders.add(new BeanBinder());
	}
	
	@Override
    public boolean binding(Request request, Response response, Action action) throws Throwable{
		for(Argument arg : action.getArguments()){
			if(!arg.isBinded()){
				binding(request,action,arg);
			}
		}
		return true;
    }
	
	private static void binding(Request request,Action action,Argument arg){
		Object value = null;
		for(Binder binder : binders){
			if(null != (value = binder.binding(request, action, arg))){
				arg.binding(value);
				break ;
			}
		}
		
		if(!arg.isBinded()){
			arg.binding(null);
		}
		
		//binging default value for primitive type
		if(arg.getType().isPrimitive() && null == arg.getValue()){
			arg.binding(ClassUtils.getDefaultValue(arg.getType()));
		}
	}
}