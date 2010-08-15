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
package org.lightframework.mvc.render;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.lightframework.mvc.clazz.ClassUtils;
import org.lightframework.mvc.config.Default;
import org.lightframework.mvc.config.Ignore;
import org.lightframework.mvc.config.Name;
import org.lightframework.mvc.config.UpperCase;

/**
 * abstract implementation of {@link IDataRender}
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public abstract class DataRender implements IDataRender {
	
	private static final Class<?>[] EMPTY_TYPES = new Class<?>[]{};

	public String encode(Object value, IRenderContext context) {
		IRenderWriter writer = context.getWriter();
		if (null == value) {
			return encodeNull(context, writer);
		} else {
			StringBuilder out = new StringBuilder();
			encode(null,value, context, writer, out);
			return out.toString();
		}
	}

	protected String encodeNull(IRenderContext context, IRenderWriter writer) {
		StringBuilder out = new StringBuilder();
		writer.writeNull(out);
		return out.toString();
	}

	protected void encode(String name,Object value, IRenderContext context, IRenderWriter writer, StringBuilder out) {
		if (null == value) {
			writer.writeNull(out);
		} else if (value instanceof String) {
			writer.writeString((String) value, out);
		} else if (value instanceof Byte) {
			// byte is Number type
			writer.writeByte((Byte) value, out);
		} else if (value instanceof Number) {
			writer.writeNumber((Number) value, out);
		} else if (value instanceof Boolean) {
			writer.writeBoolean((Boolean) value, out);
		} else if (value instanceof Character) {
			writer.writeCharacter((Character) value, out);
		} else if (value instanceof Class<?>) {
			writer.writeString(((Class<?>) value).getName(), out);
		} else if (value instanceof Date) {
			writer.writeDate((Date) value, out);
		} else if (value instanceof Object[]) {
			encode(name,(Object[]) value, context, writer, out);
		} else if (value.getClass().isArray()) {
			encodeArray(name,value, context, writer, out);
		} else if (value instanceof Map<?, ?>) {
			encode(name,(Map<?, ?>) value, context, writer, out);
		} else if (value instanceof Iterable<?>) {
			encode(name,(Iterable<?>) value, context, writer, out);
		} else if (value instanceof Enumeration<?>) {
			encode(name,(Enumeration<?>) value, context, writer, out);
		} else if (value instanceof Enum<?>) {
			writer.writeString(((Enum<?>) value).name(), out);
		} else if (value instanceof IRenderable){
			((IRenderable)value).encode(context, writer, out);
		} else {
			encodeBean(name,value, context, writer, out);
		}
	}

	protected void encode(String name,Object[] array, IRenderContext context, IRenderWriter writer, StringBuilder out) {
		writer.openArray(out);
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				writer.writeArrayValueSeperator(out);
			}
			encode(name,array[i], context, writer, out);
		}
		writer.closeArray(out);
	}

	protected void encodeArray(String name,Object array, IRenderContext context, IRenderWriter writer, StringBuilder out) {
		writer.openArray(out);
		int len = Array.getLength(array);
		for (int i = 0; i < len; i++) {
			if (i > 0) {
				writer.writeArrayValueSeperator(out);
			}
			encode(name,Array.get(array, i), context, writer, out);
		}
		writer.closeArray(out);
	}

	protected void encode(String name,Iterable<?> iterable, IRenderContext context, IRenderWriter writer, StringBuilder out) {
		writer.openArray(out);
		int index = 0;
		for (Object value : iterable) {
			if (index == 0) {
				index++;
			} else {
				writer.writeArrayValueSeperator(out);
			}
			encode(name,value, context, writer, out);
		}
		writer.closeArray(out);
	}

	protected void encode(String name,Enumeration<?> enumeration, IRenderContext context, IRenderWriter writer, StringBuilder out) {
		writer.openArray(out);
		
		int index = 0;
		while (enumeration.hasMoreElements()) {
			if (index == 0) {
				index++;
			} else {
				writer.writeArrayValueSeperator(out);
			}
			encode(name,enumeration.nextElement(), context, writer, out);
		}
		writer.closeArray(out);
	}

	protected void encode(String name,Map<?, ?> map, IRenderContext context, IRenderWriter writer, StringBuilder out) {
		writer.openObject(out);
		context.beforeEncodeBegin(name, map, out);
		int index = 0;
		for (Object key : map.keySet()) {
			if (index == 0) {
				index++;
			} else {
				writer.writePropertyValueSeperator(out);
			}

			String prop = String.valueOf(key);

			encodeNamedValue(prop, map.get(key), context, writer, out);
		}

		writer.closeObject(out);
	}

	protected void encodeBean(String name,Object bean, IRenderContext context, IRenderWriter writer, StringBuilder out) {
		//XXX : cache bean information to improve performance
		Class<?> type = bean.getClass();
		writer.openObject(out);
		
		context.beforeEncodeBegin(name, bean, out);
		
		try {
			List<Field> fields = ClassUtils.getDeclaredFields(type, null);

			int index = 0;
			for(Field field : fields){
				if(field.isSynthetic() && !Modifier.isStatic(field.getModifiers())){
					continue;
				}else if(field.getName().equals("class")){
					continue;
				}
				
				Method getter = getGetterMethod(type,field.getName(),field.getType());

				if (null != getter) {
					String propName = field.getName();
					
					boolean ignore      = false;
					boolean upperAll    = false;
					boolean upperFirst  = false;
					String defaultValue = null;
					
					for(Annotation annotation : field.getAnnotations()){
						if (Default.class.equals(annotation.annotationType())) {
							defaultValue = (((Default) annotation).value());
						} else if (Name.class.equals(annotation.annotationType())) {
							propName = (((Name) annotation).value());
						} else if (Ignore.class.equals(annotation.annotationType())){
							ignore = true;
							break;
						} else if (UpperCase.class.equals(annotation.annotationType())){
							if(((UpperCase)annotation).firstCharOnly()){
								upperFirst = true;
							}else{
								upperAll = true; 
							}
						}
					}
					
					if(ignore){
						continue;
					}
					
					if(upperFirst){
						propName = propName.length() > 0 ? propName.substring(0,1).toUpperCase() + propName.substring(1) : "";
					}else if(upperAll){
						propName = propName.toUpperCase();
					}
					
					if (index == 0) {
						index++;
					} else {
						writer.writePropertyValueSeperator(out);
					}
					
					Object propValue =  getter.invoke(bean);
					
					if(null == propValue){
						propValue = defaultValue;
					}

					encodeNamedValue(propName, propValue, context, writer, out);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("error render json of java bean : " + bean.getClass().getName(), e);
		}
		
		context.beforeEncodeEnd(name, bean, out);
		
		writer.closeObject(out);
	}

	protected void encodeNamedValue(String name, Object value, IRenderContext context, IRenderWriter writer, StringBuilder out) {
		if(context.isLowerCaseName()){
			name = name.toLowerCase();
		}else if(context.isUpperCaseName()){
			name = name.toUpperCase();
		}
		
		writer.openName(out);
		writer.writeName(name, out);
		writer.closeName(out);

		writer.openValue(name, out);
		encode(name,value, context, writer, out);
		writer.closeValue(name, out);
	}
	
	private static Method getGetterMethod(Class<?> beanClass, String fieldName,Class<?> fieldType) {
		String name   = Character.toUpperCase(fieldName.charAt(0)) + (fieldName.length() > 1 ? fieldName.substring(1) : "");
		Method method =
			ClassUtils.findMethod(beanClass, "get" + name, EMPTY_TYPES);
		
		if(null == method && (fieldType == Boolean.class || fieldType == Boolean.TYPE)){
			method = ClassUtils.findMethod(beanClass, "is" + name, EMPTY_TYPES);
		}
		return method;
	}
	
	
}
