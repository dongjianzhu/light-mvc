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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Action.Argument;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.exceptions.BindingException;
import org.lightframework.mvc.utils.BeanUtils;
import org.lightframework.mvc.utils.ClassUtils;

/**
 * TODO : document me
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public class BeanBinder extends Binder {

	@Override
	public Object binding(Request request, Action action, Argument arg) throws BindingException {
		//获取参数类型并构造参数实例
		Class<?> argType = arg.getType();
		Object target = null;
		try {
			target = ClassUtils.newInstance(argType);
			//获取参数对象定义的属性并进行属性绑定
	        List<Field> fileds = ClassUtils.getDeclaredFields(argType, null);
	        for(Field f : fileds){
	        	//获取参数值
	        	Object fieldValue = getArgumentValue(request, arg, f);
	        	if(null !=fieldValue){
	        		//设置参数对象属性,以方法设置优先,其次以属性反射的方式设置
	        		Method setterMethod = getSetterMethod(f.getName(),f.getType(),argType);
		        	if(null != setterMethod){
		        		setterMethod.invoke(target, fieldValue);
		        	}else{
		        		if(!f.isAccessible()){
		        			f.setAccessible(true);
		        		}
		        		f.set(target, fieldValue);
		        	}
	        	}
	        }
        } catch (Exception e) {
	        throw new RuntimeException("处理参数绑定时出现错误",e);
        }
		return target;
	}


	/**
	 * 查找指定属性的设置方法
     * @param f
     * @param argType
     * @return
     */
    private Method getSetterMethod(String fieldName,Class<?> fieldType, Class<?> argType) {
    	return BeanUtils.findMethod(argType, "set" + Character.toUpperCase(fieldName.charAt(0)) + (fieldName.length() > 1 ? fieldName.substring(1) : ""), new Class[]{fieldType});
    }


	/**
	 * 获取参数的值
     * @param request
     * @param arg
     * @param f
     */
    private Object getArgumentValue(Request request, Argument arg, Field f) {
	    Object value = null;
	    Class<?> type = f.getType();
	    String filedName = f.getName();
	    if(f.getType().isArray()){//数组类型处理
	    	String[] paramValues = request.getParameterValues(filedName);
	    	if(null == paramValues){
	    		String paramValue = request.getParameter(filedName);
	    		if(null != paramValue && !"".equals(paramValue.trim())){
	    			paramValues = paramValue.split(",");
	    		}
	    	}
	    	if(null == paramValues){//参数值为空,构造一个空的数组
	    		value = Array.newInstance(f.getType().getComponentType(), 0);
	    	}else{//参数值不为空,构造数组并填充值
	    		value = Array.newInstance(f.getType().getComponentType(), paramValues.length);
				for(int i=0;i<paramValues.length;i++){
					Object arrayElement = convertParamToTargetType(paramValues[i],type);
					if(arrayElement != null){
						Array.set(value, i, arrayElement);
					}
				}
	    	}
	    }else{//基本类型处理 1.取值 2.转换
	    	String paramValue = request.getParameter(filedName);
	    	value = convertParamToTargetType(paramValue,type);
	    }
	    return value;
    }


	/**
	 * 将参数值转换为指定参数类型
     * @param paramValue
     * @param type
     * @return
     */
    protected Object convertParamToTargetType(String paramValue,Class<?> type) {
    	Object value = paramValue;
	    if(!type.isInstance(paramValue) && paramValue != null && !"".equals(paramValue.trim())){
	    	if (type == Integer.TYPE || type == Integer.class) {
	            value = Integer.parseInt(paramValue);
	        } else if (type == Short.TYPE || type == Short.class) {
	            value = Short.parseShort(paramValue);
	        } else if (type == Long.TYPE || type == Long.class) {
	            value = Long.parseLong(paramValue);
	        } else if (type == Boolean.TYPE || type == Boolean.class) {
	            value = Boolean.parseBoolean(paramValue);
	        } else if (type == Float.TYPE || type == Float.class) {
	            value = Float.parseFloat(paramValue);
	        } else if (type == Double.TYPE || type == Double.class) {
	            value = Double.parseDouble(paramValue);
	        } else if (type == Character.TYPE || type == Character.class && paramValue.length() == 0) {
	            value = paramValue.charAt(0);
	        } else if (type == Byte.TYPE || type == Byte.class) {
	            value = Byte.parseByte(paramValue);
	        } else if (type.isAssignableFrom(Date.class)){
	        	value = BeanUtils.parseDate(paramValue);
	        }
	    }
	    return value;
    }
}
