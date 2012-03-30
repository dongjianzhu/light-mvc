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

import static org.lightframework.mvc.internal.reflect.ReflectUtils.findGetter;
import static org.lightframework.mvc.internal.reflect.ReflectUtils.findSetter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;



/**
 * <code>{@link ReflectField}</code>
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
public final class ReflectField {

    private static Object[] EMPTY_ARGS = new Object[]{};
    
    private final Field    field;
    private final String   name;
    private final Class<?> type;
    private final Type     genericType;
    private final Type[]   actualTypes;
    private final Class<?> componentType;
    private final Method   getter;
    private final Method   setter;
    
    private final ReflectAccessor accessor;
    private final int         fieldIndex;
    private final int         setterIndex;
    private final int         getterIndex;
    
    private final boolean    isArray;
    private final boolean    isIterable;
    
    ReflectField(ReflectAccessor accessor, java.lang.reflect.Field field){
        this.field         = field;
        this.name          = field.getName();
        this.type          = field.getType();
        this.genericType   = field.getGenericType();
        this.setter        = findSetter(field);
        this.getter        = findGetter(field);
        this.accessor      = accessor;
        this.fieldIndex    = accessor.getFieldIndex(field);
        this.componentType = ReflectUtils.getActualType(genericType);
        this.actualTypes   = ReflectUtils.getActualTypes(genericType);
        
        if(null != setter){
            setterIndex = accessor.getMethodIndex(setter);
        }else{
            setterIndex = -1;
        }
        
        if(null != getter){
            getterIndex = accessor.getMethodIndex(getter);
        }else{
            getterIndex = -1;
        }
        
        this.isArray    = type.isArray();
        this.isIterable = Iterable.class.isAssignableFrom(type);
    }
    
    public java.lang.reflect.Field field(){
        return field;
    }
    
    public String getName(){
        return name;
    }
    
    public Class<?> getType(){
        return type;
    }
    
    public Type getGenericType(){
        return genericType;
    }
    
    public Type[] getActualTypes(){
        return actualTypes;
    }
    
    public Class<?> getCompoenentType() {
        return componentType;
    }
    
    public boolean isArray() {
        return isArray;
    }

    public boolean isIterable() {
        return isIterable;
    }

    public boolean isPublic(){
        return Modifier.isPublic(field.getModifiers());
    }
    
    public boolean hasPublicGetterSetter(){
        return hasGetter() && Modifier.isPublic(getter.getModifiers()) && hasSetter() && Modifier.isPublic(setter.getModifiers());
    }
    
    public boolean hasGetter(){
        return null != getter;
    }
    
    public boolean hasSetter(){
        return null != setter;
    }
    
    public void setValue(Object instance, Object value) throws ReflectException{
        try {
            if(null != setter){
                accessor.invokeMethod(instance, setterIndex, value);
            }else if(fieldIndex >= 0){
                accessor.setField(instance, fieldIndex, value);
            }else{
                if(!field.isAccessible()){
                    field.setAccessible(true);
                }
                field.set(instance, value);
            }
        } catch (Exception e) {
            throw new ReflectException("set value of property '" + field.getDeclaringClass().getSimpleName() + "." + name + "' error",e);
        }
    }
    
    public Object getValue(Object instance) throws ReflectException {
        try {
            if(null != getter){
                return accessor.invokeMethod(instance, getterIndex, EMPTY_ARGS);
            }else if(fieldIndex >= 0){
                return accessor.getField(instance, fieldIndex);
            }else {
                if(!field.isAccessible()){
                    field.setAccessible(true);
                }
                return field.get(instance);
            }
        } catch (Exception e) {
            throw new ReflectException("get the value of property '" + name + "." + field.getDeclaringClass().getSimpleName() + "' error",e);
        }
    }
}