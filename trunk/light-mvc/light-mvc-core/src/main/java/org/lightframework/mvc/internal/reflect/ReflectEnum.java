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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lightframework.mvc.exceptions.InvalidValueException;
import org.lightframework.mvc.internal.convert.ConvertException;
import org.lightframework.mvc.internal.convert.Converter;


/**
 * <code>{@link ReflectEnum}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
public final class ReflectEnum {
    
    private static final Map<Class<?>, ReflectEnum> cache = new ConcurrentHashMap<Class<?>, ReflectEnum>();
    private static final String VALUE_FIELD_NAME = "value";

    @SuppressWarnings("unchecked")
    public static final <T> T valueOf(Class<?> enumType,Object value) throws InvalidValueException {
        return (T)get(enumType).valueOf(value);
    }
    
    public static final Object toValue(Object enumObject) {
        if(null == enumObject){
            return null;
        }
        return get(enumObject.getClass()).getValue(enumObject);
    }
    
    static ReflectEnum get(Class<?> enumType){
        if(!enumType.isEnum()){
            throw new ReflectException("class '" + enumType.getName() + "' is not an enum type");
        }
        
        ReflectEnum reflectEnum = cache.get(enumType);
        if(null == reflectEnum){
            synchronized (cache) {
                reflectEnum = new ReflectEnum(enumType);
                cache.put(enumType, reflectEnum);
            }
        }
        return reflectEnum;
    }
    
    private Class<?>     type;
    private ReflectField valueField;
    private Class<?>     valueType;
    private boolean     hasValueField;
    
    private ReflectEnum(Class<?> enumType){
        this.type = enumType;
        this.init();
    }
    
    public Object getValue(Object e){
        if(null == e){
            return null;
        }
        
        if(hasValueField){
            return valueField.getValue(e);
        }else{
            return e.toString();
        }
    }
    
    @SuppressWarnings("unchecked")
    public Enum valueOf(Object value) {
        if(null == value){
            return null;
        }
        
        String s = value.toString().trim();
        
        if(s.equals("")){
            return null;
        }
        
        Object[] values = type.getEnumConstants();
        
        if(null == values){
            return null;
        }
        
        for(Object e : type.getEnumConstants()){
            if(e.toString().equalsIgnoreCase(s)){
                return (Enum)e;
            }
        }
        
        if(hasValueField){
            Object valueFieldValue;
            try {
                valueFieldValue = Converter.convert(valueType, value);
                
                for(Object e : type.getEnumConstants()){
                    Object v = valueField.getValue(e);
                    
                    if(valueFieldValue.equals(v)){
                        return (Enum)e;
                    }
                }                
            } catch (ConvertException e) {
                
            }
        }
        
        throw new InvalidValueException("invalid enum value '" + s + "' of type '" + type.getName() + "'"); 
    }
    
    private void init(){
        ReflectType reflectType = new ReflectType(type);
        
        for(ReflectField field : reflectType.getAllFields()){
            if(field.getName().equalsIgnoreCase(VALUE_FIELD_NAME)){
                valueField    = field;
                valueType     = field.getType();
                hasValueField = true;
                break;
            }
        }
        
        reflectType = null;
    }
}
