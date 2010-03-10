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

import java.lang.reflect.Array;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Action.Argument;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.exceptions.BindingException;
import org.lightframework.mvc.utils.ClassUtils;

/**
 * binder for array type
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public class ArrayBinder extends TypeBinder {

	@Override
	public Object binding(Request request, Action action, Argument arg) throws BindingException {
		if(arg.getType().isArray()){
			Class<?> type = arg.getType().getComponentType();
			if(type.isPrimitive()){
				type = ClassUtils.getWrapperType(type);
			}
			
			Object value = getObjectValue(request, action, arg);
			if(null == value || (value.getClass().isArray() && ((Object[])value).length == 0)){
				return Array.newInstance(arg.getType().getComponentType(), 0);
			}else{
				int length = value.getClass().isArray() ? ((Object[])value).length : 1;
				Object array = Array.newInstance(arg.getType().getComponentType(), length);
				if(length == 1){
					Array.set(array, 0, binding(arg,type,value));
				}else{
					Object[] values = (Object[])value;
					for(int i=0;i<length;i++){
						Array.set(array, i, binding(arg,type,values[i]));
					}
				}
				return array;
			}
		}
		return null;
	}
}
