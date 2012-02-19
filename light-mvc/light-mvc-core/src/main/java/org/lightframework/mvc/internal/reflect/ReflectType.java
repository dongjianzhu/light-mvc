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

import static org.lightframework.mvc.internal.reflect.ReflectUtils.getDeclaredFields;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.lightframework.mvc.internal.utils.ArrayUtils;


/**
 * <code>{@link ReflectType}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
public class ReflectType {
    
    private static ConcurrentHashMap<Class<?>, ReflectType> cache = new ConcurrentHashMap<Class<?>, ReflectType>();
    
    public static ReflectType get(Class<?> clazz){
        ReflectType type = cache.get(clazz);
        if(null == type){
            synchronized (cache) {
                checkClass(clazz);
                type = new ReflectType(clazz);
                cache.put(clazz, type);
            }
        }
        return type;
    }
    
    public static boolean isBeanType(Class<?> clazz){
        String name = clazz.getName();
        if(clazz.isPrimitive() || clazz.isEnum() || clazz.isAnnotation() || clazz.isInterface() || 
           name.startsWith("java.") || name.startsWith("javax.")){
            return false;
        }
        return true;
    }
    
    private Class<?>               type;
    private boolean               checkConstructor;
    private ReflectField[]         allFields                = null;
    private Iterable<ReflectField> allFieldsIterable        = null;
    private String[]               allFieldNames            = null;
    private Iterable<String>       allFieldNamesIterable    = null;
    private ReflectField[]         publicFields             = null;
    private Iterable<ReflectField> publicFieldsIterable     = null;
    private String[]               publicFieldNames         = null;
    private Iterable<String>       publicFieldNamesIterable = null;
    private ReflectAccessor        accessor                 = null;
    
    protected ReflectType(Class<?> type){
        this.type = type;
        this.initialize();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T newInstance(){
        if(!checkConstructor){
            checkConstructor();
        }
        return (T)accessor.newInstance();
    }
    
    public Class<?> getActualType(){
        return type;
    }
    
    public ReflectField[] getPublicFields(){
        return publicFields;
    }
    
    public Iterable<ReflectField> getPublicFieldsIterable(){
        if(null == publicFieldsIterable){
            publicFieldsIterable = ArrayUtils.toList(publicFields);
        }
        return publicFieldsIterable;
    }
    
    public String[] getPublicFieldNames(){
        if(null == publicFieldNames){
            publicFieldNames = new String[publicFields.length];
            
            for(int i=0;i<publicFieldNames.length;i++){
                publicFieldNames[i] = publicFields[i].getName();
            }
        }
        return publicFieldNames;
    }
    
    public Iterable<String> getPublicFieldNamesIterable(){
        if(null == publicFieldNamesIterable){
            publicFieldNamesIterable = ArrayUtils.toList(getPublicFieldNames());
        }
        return publicFieldNamesIterable;
    }
    
    public ReflectField[] getAllFields(){
        return allFields;
    }
    
    public Iterable<ReflectField> getAllFieldsIterable(){
        if(null == allFieldsIterable){
            allFieldsIterable = ArrayUtils.toList(allFields);
        }
        return allFieldsIterable;
    }
    
    public String[] getAllFieldNames(){
        if(null == allFieldNames){
            allFieldNames = new String[allFields.length];
            
            for(int i=0;i<allFieldNames.length;i++){
                allFieldNames[i] = allFields[i].getName();
            }
        }
        return allFieldNames;
    }
    
    public Iterable<String> getAllFieldNamesIterable(){
        if(null == allFieldNamesIterable){
            allFieldNamesIterable = ArrayUtils.toList(getAllFieldNames());
        }
        return allFieldNamesIterable;
    }
    
    public ReflectField getField(String name){
        for(ReflectField field : allFields){
            if(field.getName().equals(name)){
                return field;
            }
        }
        return null;
    }
    
    public ReflectField findField(String name){
        for(ReflectField field : allFields){
            if(field.getName().equalsIgnoreCase(name)){
                return field;
            }
        }
        return null;
    }
    
    public ReflectMethod findMethod(String name,Class<?>... argumentTypes){
        Method method = ReflectUtils.findMethod(type, name, argumentTypes);
        if(null == method){
            return null;
        }else{
            return new ReflectMethod(accessor, method);
        }
    }
    
    private void initialize(){
        this.accessor = ReflectAccessor.createFor(type);
        
        List<Field> fields = getDeclaredFields(type, Object.class);
        
        List<ReflectField> allFields  = new ArrayList<ReflectField>();
        List<ReflectField> publicFields = new ArrayList<ReflectField>();
        
        for(Field field : fields){
            if(field.isSynthetic() || Modifier.isStatic(field.getModifiers()) || field.getName().equals("class")){
                continue;
            }
            
            ReflectField reflectField = new ReflectField(accessor,field);
            
            allFields.add(reflectField);
            
            if(reflectField.isPublic() || reflectField.hasPublicGetterSetter()){
                publicFields.add(reflectField);
            }
        }
        
        this.allFields    = allFields.toArray(new ReflectField[]{});
        this.publicFields = publicFields.toArray(new ReflectField[]{});
    }
    
    private void checkConstructor(){
        try {
             type.getConstructor((Class[])null);
        } catch (NoSuchMethodException e) {
            throw new ReflectException("no default constructor in class '" + type.getName());
        }
        checkConstructor = true;
    }
    
    private static void checkClass(Class<?> clazz){
        if(!isBeanType(clazz)){
            throw new IllegalArgumentException("not supported type '" + clazz.getName() + "',should be an pojo entity type");
        }
    }
}
