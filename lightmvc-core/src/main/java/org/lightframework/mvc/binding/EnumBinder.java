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

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Action.Argument;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.exceptions.BindingException;

/**
 * {@link Binder} for type {@link Enum}
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public class EnumBinder extends Binder {

	@Override
	@SuppressWarnings("unchecked")
	public Object binding(Request request, Action action, Argument arg) throws BindingException {
		if(Enum.class.isAssignableFrom(arg.getType())){
			Object value = getStringValue(request, action, arg);
			if(null != value){
				if(value.getClass().equals(arg.getType())){
					return value;
				}else{
					return Enum.valueOf(arg.getType().asSubclass(Enum.class), getString(value));
				}
			}
		}
		return null;
	}
}
