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
package org.lightframework.mvc.binding;

import java.lang.annotation.Annotation;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Utils;
import org.lightframework.mvc.Action.Argument;
import org.lightframework.mvc.Config.Default;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.exceptions.BindingException;

/**
 * binding value to a {@link Argument}
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public abstract class Binder {

	/**
	 * translate the given value to the value of the given type. 
	 * @return the value translated of the given type
	 */
	public abstract Object binding(Request request,Action action,Argument arg) throws BindingException;

	protected Object getObjectValue(Request request,Action action,Argument arg) {
		//get value from action's scope
		Object value = action.getParameter(arg.getName());
		if(null == value){
			//then get from request's scope
			String[] values = request.getParameterValues(arg.getName());
			
			if(null == values || values.length == 0){
				for(Annotation annotation : arg.getConfigs()){
					if(Default.class.equals(annotation.annotationType())){
						value = ((Default)annotation).value();
					}
				}
			}else{
				value = values;
			}
		}
		return value;
	}
	
	protected String getStringValue(Request request,Action action,Argument arg){
		return getString(getObjectValue(request, action, arg));
	}
	
	protected String getString(Object value){
		if(null != value && value.getClass().isArray()){
			return Utils.arrayToString((Object[])value);
		}else{
			return null == value ? null : value.toString();
		}
	}
}