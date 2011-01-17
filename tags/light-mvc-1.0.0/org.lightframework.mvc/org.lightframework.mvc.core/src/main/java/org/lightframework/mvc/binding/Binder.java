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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lightframework.mvc.clazz.ClassUtils;
import org.lightframework.mvc.config.Default;
import org.lightframework.mvc.config.Format;
import org.lightframework.mvc.config.Name;
import org.lightframework.mvc.convert.DateConverter;
import org.lightframework.mvc.convert.IConverter;
import org.lightframework.mvc.convert.PrimitiveConverter;
import org.lightframework.mvc.render.json.JSONReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * utility class to bind {@link Argument}'s value by given parameters.
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public final class Binder {

	private static final Logger log = LoggerFactory.getLogger(Binder.class);

	private static final Map<Class<?>, IConverter> converters = new HashMap<Class<?>, IConverter>();
	static {
		register(new PrimitiveConverter(), converters);
		register(new DateConverter(), converters);
	}

	public static Argument[] resolveArguments(Method method) throws BindingException {
		try {
			return resolveArguments(ClassUtils.getMethodParameters(method));
		} catch (IOException e) {
			throw new BindingException("error resolving arguments of method '" + method.getName() + "'", e);

		}
	}

	public static Object[] binding(Method method, IBindingContext context) throws BindingException {
		Argument[] arguments = resolveArguments(method);
		Object[] executeArgs = new Object[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			executeArgs[i] = binding(arguments[i], context);
		}
		return executeArgs;
	}

	public static Object binding(Argument arg, IBindingContext context) throws BindingException {
		return binding(arg, context.getParameter(arg.getName()), context);
	}

	public static Object binding(Argument arg, Object value, IBindingContext context) throws BindingException {
		try {
			Class<?> type = arg.getType();

			// binging default value for primitive type
			if (null == value && arg.getType().isPrimitive()) {
				return ClassUtils.getDefaultValue(arg.getType());
			}

			// direct bind
			if (null != value && type.isAssignableFrom(value.getClass())) {
				return value;
			}

			String string = null == value ? null : value.toString();

			// external converters
			List<IConverter> contextConverters = context.getConverters();
			if (null != contextConverters) {
				for (IConverter converter : contextConverters) {
					if (converter.getSupportedTypes().contains(type)) {
						return converter.convertToObject(arg, string);
					}
				}
			}

			// built-in converters
			IConverter converter = converters.get(type);
			if (null != converter) {
				return null == value ? null : converter.convertToObject(arg, string);
			}

			// enum bind
			if (Enum.class.isAssignableFrom(type)) {
				return enumBinding(arg, value);
			}

			// array bind
			if (type.isArray()) {
				return arrayBinding(arg, value, context);
			}
			
			// list bind
			if(List.class.isAssignableFrom(type)){
				return listBinding(arg,value,context);
			}

			// map bind
			if (type.equals(Map.class)) {
				return mapBinding(arg, value, context);
			}

			// bean bind
			if (!ClassUtils.isJdkClass(type)) {
				return beanBinding(arg,value,context);
			}
		} catch (Exception e) {
			throw new BindingException("binding '" + arg.getName() + "' error", e);
		}
		return null;
	}

	private static Object arrayBinding(Argument arg, Object value, IBindingContext context) throws Exception {
		return arrayBinding(arg,arg.getType().getComponentType(),value,context);
	}
	
	@SuppressWarnings("unchecked")
    private static Object arrayBinding(Argument arg,Class<?> clazz, Object value, IBindingContext context) throws Exception {
		if (null == value) {
			return Array.newInstance(arg.getType().getComponentType(), 0);
		} else if(value.getClass().isArray()){
			Class<?> valueComponentType = value.getClass().getComponentType();
			if(clazz.isAssignableFrom(valueComponentType)){
				return value;
			}else{
				int length = Array.getLength(value);
				Argument type = new Argument(arg.getName(), clazz);
				Object array = Array.newInstance(clazz, length);
				for(int i=0;i<length;i++){
					Array.set(array, i, binding(type,Array.get(value, i),context));
				}
				return array;
			}
		} else {
			Object[] values = null;
			if (String.class == value.getClass()) {
				values = value.toString().split(",");
			} else if(ArrayList.class == value.getClass() || List.class == value.getClass()){
				values = ((List)value).toArray() ;
			}else {
				values = new Object[] { value.toString() };
			}

			Argument type = new Argument(arg.getName(), clazz);
			Object array = Array.newInstance(clazz, values.length);
			for (int i = 0; i < values.length; i++) {
				Array.set(array, i, binding(type, values[i], context));
			}
			return array;
		}
	}	
	
	@SuppressWarnings("unchecked")
	private static Object listBinding(Argument arg, Object value, IBindingContext context) throws Exception {
		if(!arg.isParameterizedType()){
			throw new BindingException("type of argument '" + arg.getName() + "' must be parameterized(see java.lang.reflect.ParameterizedType)");
		}
		
		if( value instanceof String ){
			value = JSONReader.decode((String)value) ;
		}
		
		Type actualType = arg.getActualTypeArguments()[0];
		if(actualType instanceof WildcardType){
			throw new BindingException("parameterized type of argument '" + arg.getName() + "' must not be List<?>");
		}
		
		Class<?> clazz = (Class<?>)actualType;
		Object   array = arrayBinding(arg,clazz,value,context);
		
		List list = new ArrayList();
		int length = Array.getLength(array);
		
		for(int i=0;i<length;i++){
			list.add(Array.get(array, i));
		}
		
		return list;
	}

	private static Map<String, Object> mapBinding(Argument arg, Object value, IBindingContext context) throws Exception {
		// XXX : REVIEW MAP BINDING
		Map<String, Object> params = new HashMap<String, Object>();
		params.putAll(context.getParameters());
		return params;
	}

	@SuppressWarnings("unchecked")
	private static Object enumBinding(Argument arg, Object value) {
		if (null != value) {
			if (value.getClass().equals(arg.getType())) {
				return value;
			} else {
				return Enum.valueOf(arg.getType().asSubclass(Enum.class), value.toString());
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Object beanBinding(Argument arg,Object data, IBindingContext context) throws Exception {
		Object bean = ClassUtils.newInstance(arg.getType());
		List<Field> fileds = ClassUtils.getDeclaredFields(arg.getType(), null);

		for (Field field : fileds) {
			Argument type = new Argument(field.getName(), field.getType(), field.getAnnotations());
			
			Object param = null;
			Object value = null;
			
			if(data instanceof Map){
				param = ((Map)data).get(type.getName());
			}else {
				param = getParameterValue(type, context);
			}
			
			value = binding(type,param,context);
			
			if (null != value) {
				Method setterMethod = getSetterMethod(arg.getType(), field.getName(), field.getType());
				if (null != setterMethod) {
					try {
						setterMethod.invoke(bean, value);
					} catch (Exception e) {
						log.error("[field:'{}'] -> set by method error : '{}'", field.getName(), e.getMessage());
						throw e;
					}
				} else if (!field.isSynthetic()) {
					if (!field.isAccessible()) {
						field.setAccessible(true);
					}
					try {
						field.set(bean, value);
					} catch (Exception e) {
						log.error("[field:'{}'] -> set by value error : '{}'", field.getName(), e.getMessage());
						throw e;
					}
				}
			}
		}
		return bean;
	}

	private static Method getSetterMethod(Class<?> beanClass, String fieldName, Class<?> fieldType) {
		return ClassUtils.findMethod(beanClass, "set" + Character.toUpperCase(fieldName.charAt(0))
		        + (fieldName.length() > 1 ? fieldName.substring(1) : ""), new Class[] { fieldType });
	}

	private static Argument[] resolveArguments(Argument[] args) {
		for (Argument arg : args) {
			resolveArgument(arg);
		}
		return args;
	}

	private static void resolveArgument(Argument arg) {
		for (Annotation config : arg.getConfigs()) {
			if (Format.class.equals(config.annotationType())) {
				arg.setFormat(((Format) config).value());
			} else if (Default.class.equals(config.annotationType())) {
				arg.setDefaultValue(((Default) config).value());
			} else if (Name.class.equals(config.annotationType())) {
				arg.setName(((Name) config).value());
			}
		}
	}

	private static Object getParameterValue(Argument arg, IBindingContext context) {
		// get arg's raw value
		Object value = context.getParameter(arg.getName());
		if (null == value) {
			value = arg.getDefaultValue();
		}
		return value;
	}

	private static void register(IConverter binder, Map<Class<?>, IConverter> map) {
		Set<Class<?>> types = binder.getSupportedTypes();
		if (null != types) {
			for (Class<?> type : types) {
				map.put(type, binder);
			}
		}
	}
}