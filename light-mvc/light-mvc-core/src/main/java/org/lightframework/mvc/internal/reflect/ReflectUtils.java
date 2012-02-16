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
package org.lightframework.mvc.internal.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;


/**
 * <code>{@link ReflectUtils}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
public class ReflectUtils {
    
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object object,Class<T> fieldType,String fieldName) throws ReflectException {
        return (T)getFieldValue(object, fieldName);
    }
    
    public static Object getFieldValue(Object object,String field) throws ReflectException {
        ReflectType type = ReflectType.get(object.getClass());
        
        ReflectField prop = type.getField(field);
        
        if(null == prop){
            throw new ReflectException("field '" + field + "' not found in type '" + type.getActualType().getName());
        }
        
        return prop.getValue(object);
    }
    
    public static Class<?> getActualType(Type genericType){
        if(genericType instanceof WildcardType){
            return Object.class;
        }else if(genericType instanceof ParameterizedType){
            return getActualType(((ParameterizedType)genericType).getActualTypeArguments()[0]);
        }else if(genericType instanceof Class<?>){
            Class<?> clazz = (Class<?>)genericType;
            return clazz.isArray() ? clazz.getComponentType() : clazz;
        }else if(genericType instanceof TypeVariable<?>){
            return getActualType(((TypeVariable<?>)genericType).getBounds()[0]);
        }else{
            throw new ReflectException("unsupported generic type '" + genericType.getClass().getName() + "'");
        }
    }
    
    public static Type[] getActualTypes(Type genericType){
        if(genericType instanceof WildcardType){
            return new Type[]{Object.class};
        }else if(genericType instanceof ParameterizedType){
            return ((ParameterizedType)genericType).getActualTypeArguments();
        }else if(genericType instanceof Class<?>){
            Class<?> clazz = (Class<?>)genericType;
            return new Type[]{clazz.isArray() ? clazz.getComponentType() : clazz};
        }else if(genericType instanceof TypeVariable<?>){
                return ((TypeVariable<?>)genericType).getBounds();
        }else{
            throw new ReflectException("unsupported generic type '" + genericType.getClass().getName() + "'");
        }
    }
    
    public static List<Field> getDeclaredFields(Class<?> clazz,Class<?> stopClass){
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            if (stopClass != null && c == stopClass) {
                break;
            }else{
                for(Field field : c.getDeclaredFields()){
                    fields.add(field) ;
                }
            }
        }
        return fields;
    }
    
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            return findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }
    
    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }
    
    public static Method findGetter(Field field){
        Class<?> clazz  = field.getDeclaringClass();
        String   name   = field.getName();
        
        Method   method = findMethod(clazz, 
                                     "get" + Character.toUpperCase(name.charAt(0))  + (name.length() > 1 ? name.substring(1) : ""), 
                                     null); 
        
        if(null == method && (field.getType() == Boolean.TYPE || field.getType() == Boolean.class)){
            if(name.startsWith("is")){
                method = findMethod(clazz, name, null);
            }else{
                method = findMethod(clazz, 
                                    "is" + Character.toUpperCase(name.charAt(0))  + (name.length() > 1 ? name.substring(1) : ""), 
                                    null);
            }
        }

        /* TODO : check return type ?
        if(null != method && !method.getReturnType().equals(field.getType())){
            return null;
        }
        */
        
        return method;
    }
    
    public static Method findSetter(Field field){
        Class<?> clazz = field.getDeclaringClass();
        String   name  = field.getName();
        
        Method  method = findMethod(clazz, 
                           "set" + Character.toUpperCase(name.charAt(0))  + (name.length() > 1 ? name.substring(1) : ""), 
                           new Class[] { field.getType() });        
        
        if(null == method && name.startsWith("is") && (field.getType() == Boolean.TYPE || field.getType() == Boolean.class)){
            method = findMethod(clazz, 
                                "set" + name.substring(2), 
                                new Class[] { field.getType() });                 
        }
        
        return method;
    }
}
