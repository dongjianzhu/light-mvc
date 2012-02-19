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
package org.lightframework.mvc.internal.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <code>{@link ClassUtils}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
public class ClassUtils {
    
    private static final Map<Class<?>, Class<?>> wrapperTypes   = new HashMap<Class<?>, Class<?>>();
    private static final Map<String, Class<?>>   primitiveTypes = new HashMap<String, Class<?>>();
    
    static {
        wrapperTypes.put(Boolean.TYPE, Boolean.class);
        wrapperTypes.put(Character.TYPE, Character.class);
        wrapperTypes.put(Byte.TYPE, Byte.class);
        wrapperTypes.put(Short.TYPE, Short.class);
        wrapperTypes.put(Integer.TYPE, Integer.class);
        wrapperTypes.put(Long.TYPE, Long.class);
        wrapperTypes.put(Float.TYPE, Float.class);
        wrapperTypes.put(Double.TYPE, Double.class);
        
        primitiveTypes.put(boolean.class.getName().toLowerCase(),Boolean.TYPE);
        primitiveTypes.put(char.class.getName().toLowerCase(),Character.TYPE);
        primitiveTypes.put(byte.class.getName().toLowerCase(),Byte.TYPE);
        primitiveTypes.put(short.class.getName().toLowerCase(),Short.TYPE);
        primitiveTypes.put(int.class.getName().toLowerCase(),Integer.TYPE);
        primitiveTypes.put(long.class.getName().toLowerCase(),Long.TYPE);
        primitiveTypes.put(float.class.getName().toLowerCase(),Float.TYPE);
        primitiveTypes.put(double.class.getName().toLowerCase(),Double.TYPE);        
    }
    
    public static Class<?> forPrimitiveName(String name){
        return primitiveTypes.get(name.toLowerCase());
    }
    
    public static Class<?> forPrimitiveOrClassName(String name,Class<?> loaderClass){
        Class<?> clazz = primitiveTypes.get(name.toLowerCase());
        if(null == clazz){
            clazz = forNameNotCheck(name,loaderClass);
        }
        return clazz;
    }
    
    public static Class<?> toWrapperType(Class<?> type){
        Class<?> wrapperType = wrapperTypes.get(type);
        return wrapperType == null ? type : wrapperType;
    }
    
    public static Class<?> tryForName(String name){
        try {
            return forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    public static Class<?> forName(String name) throws ClassNotFoundException {
        return forName(name,ClassUtils.class);
    }
    
    public static Class<?> forNameNotCheck(String name,Class<?> loaderClass) {
        try {
            return forName(name,ClassUtils.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("ClassNotFound : " + e.getMessage(),e);
        }
    }

    public static Class<?> forName(String name,Class<?> loaderClass) throws ClassNotFoundException {
        Class<?> clazz = null;
        
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        try {
            clazz = contextLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            //do nothing
        } catch(NoClassDefFoundError e){
            //do nothing
        }
        
        if(null == clazz && contextLoader != loaderClass.getClassLoader()){
            try {
                clazz = loaderClass.getClassLoader().loadClass(name);
            } catch (NoClassDefFoundError e) {
                //do nothing
            }
        }
        
        if(null == clazz){
            throw new ClassNotFoundException("class '" + name + "' not found");
        }
        
        return clazz;
    }
    
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassUtils.class.getClassLoader();
		}
		return cl;
	}    
    
    public static Object newInstance(Class<?> clazz){
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }    
    
    public static Object newInstance(String className) throws ClassNotFoundException {
        return newInstance(className,ClassUtils.class);
    }
    
    public static Object newInstance(String className,Class<?> classForLoader) throws ClassNotFoundException{
        try {
            return forName(className,classForLoader).newInstance();
        } catch (ClassNotFoundException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("error creating instance of class '" + className + "'",e);
        }
    }
    
    public static Object newInstanceNotCheck(String className,Class<?> classForLoader) {
        try {
            return forName(className,classForLoader).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
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
    
    public static List<Field> getDeclaredFields(Class<?> clazz,Class<?> stopClass){
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            if (stopClass != null && c == stopClass) {
                break;
            }else{
                for(Field field : c.getDeclaredFields()){
                    //exclude final and static field
                    if(!( Modifier.isFinal(field.getModifiers())
                            ||Modifier.isStatic(field.getModifiers()))){
                        fields.add(field) ;
                    }
                }
                
                //fields.addAll(Arrays.asList(c.getDeclaredFields()));
            }
        }
        return fields;
    }
}
