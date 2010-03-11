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

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Action.Argument;
import org.lightframework.mvc.Config.Default;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.Lang.Type;
import org.lightframework.mvc.binding.DateBinder;
import org.lightframework.mvc.binding.ITypeBinder;
import org.lightframework.mvc.binding.PrimitiveBinder;
import org.lightframework.mvc.utils.BeanUtils;
import org.lightframework.mvc.utils.ClassUtils;

/**
 * the core plugin to binding parameters in request of action method.
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public class BindingPlugin extends Plugin {
	
	private static final Map<Class<?>, ITypeBinder> binders = new HashMap<Class<?>, ITypeBinder>();
	static {
		register(new PrimitiveBinder(),binders);
		register(new DateBinder(),binders);
	}
	
	@Override
    public boolean binding(Request request, Response response, Action action) throws Throwable{
		for(Argument arg : action.getArguments()){
			if(!arg.isBinded()){
				arg.binding(bind(request,action,arg,getParamValue(request, action, arg)));
			}
		}
		return true;
    }
	
	private static Object bind(Request request,Action action,Type arg,Object value) throws Throwable{
		Class<?> type = arg.getType();
		
		//binging default value for primitive type
		if(null == value && arg.getType().isPrimitive()){
			return ClassUtils.getDefaultValue(arg.getType());
		}
		
		//direct bind
		if(null != value && type.isAssignableFrom(value.getClass())){
			return value;
		}
		
		//type bind
		ITypeBinder binder = binders.get(type);
		if(null != binder){
			return null == value ? null : binder.bind(arg, value.toString());
		}
		
		//enum bind
		if(Enum.class.isAssignableFrom(type)){
			return enumBind(arg,value);
		}
		
		//array bind
		if(type.isArray()){
			return arrayBind(request,action,arg,value);
		}
		
		//bean bind
		if(!ClassUtils.isJdkClass(type)){
			return beanBind(request,action,arg);
		}

		return null;
	}
	
	private static Object arrayBind(Request request,Action action,Type arg,Object value) throws Throwable{
		Class<?> clazz = arg.getType().getComponentType();
		if(null == value){
			return Array.newInstance(arg.getType().getComponentType(), 0);
		}else{
			Object[] values = null;
			if(String.class == value.getClass()){
				values = value.toString().split(",");
			}else{
				values = new Object[]{value.toString()};
			}
			
			Type type = new Type(arg.getName(),clazz);
			Object array = Array.newInstance(arg.getType().getComponentType(), values.length);
			for(int i=0;i<values.length;i++){
				Array.set(array, i, bind(request,action,type,values[i]));
			}
			return array;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Object enumBind(Type arg,Object value){
		if(null != value){
			if(value.getClass().equals(arg.getType())){
				return value;
			}else{
				return Enum.valueOf(arg.getType().asSubclass(Enum.class), value.toString());
			}
		}
		return null;
	}
	
	private static Object beanBind(Request request,Action action,Type arg) throws Throwable{
		Object      bean   = ClassUtils.newInstance(arg.getType());
		List<Field> fileds = ClassUtils.getDeclaredFields(arg.getType(), null);
		
        for(Field field : fileds){
        	Type   type  = new Type(field.getName(),field.getType(),field.getAnnotations());
        	Object param = getParamValue(request, action, type);
        	Object value = bind(request,action,type,param);
        	
        	if(null != value){
        		Method setterMethod = getSetterMethod(arg.getType(),field.getName(),field.getType());
	        	if(null != setterMethod){
	        		setterMethod.invoke(bean, value);
	        	}else{
	        		if(!field.isAccessible()){
	        			field.setAccessible(true);
	        		}
	        		field.set(bean, value);
	        	}
        	}
        }		
		return bean;
	}
	
    private static Method getSetterMethod(Class<?> beanClass,String fieldName,Class<?> fieldType) {
    	return BeanUtils.findMethod(beanClass, "set" + Character.toUpperCase(fieldName.charAt(0)) + (fieldName.length() > 1 ? fieldName.substring(1) : ""), new Class[]{fieldType});
    }
	
	private static Object getParamValue(Request request,Action action,Type arg) throws Throwable{
		//get arg's raw value
		Object value = action.getParameter(arg.getName());
		if(null == value){
			value = request.getParameter(arg.getName());
			if(null == value){
				for(Annotation annotation : arg.getConfigs()){
					if(Default.class.equals(annotation.annotationType())){
						value = ((Default)annotation).value();
					}
				}
			}
		}
		return value;
	}
	
	private static void register(ITypeBinder binder,Map<Class<?>, ITypeBinder> map){
		Set<Class<?>> types = binder.getSupportedTypes();
		if(null != types){
			for(Class<?> type : types){
				map.put(type, binder);
			}
		}
	}
}