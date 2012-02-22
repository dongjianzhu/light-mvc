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
package org.lightframework.mvc.render.json;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import org.lightframework.mvc.internal.reflect.ReflectField;
import org.lightframework.mvc.internal.reflect.ReflectType;


/**
 * <code>{@link JSONEncoder}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class JSONEncoder {
    
    private JSONWriter writer;
    private boolean   ignoreNull;
    private boolean   ignoreBlank;
    
    public JSONEncoder(){
        this(new JSONSettings());
    }
    
    public JSONEncoder(JSONSettings settings){
        this.writer = new JSONWriter(settings);
        this.setting(settings);
    }
    
    private void setting(JSONSettings settings){
        this.ignoreNull  = settings.isIgnoreNull();
        this.ignoreBlank = settings.isIgnoreBlank();
    }
    
    public String encode(Object value){
        if (null == value) {
            return encodeNull();
        } else {
            StringBuilder out = new StringBuilder();
            encode(null,value, out);
            return out.toString();
        }
    }

    private String encodeNull() {
        StringBuilder out = new StringBuilder();
        writer.writeNull(out);
        return out.toString();
    }

    private void encode(String name,Object value, StringBuilder out) {
        if (null == value) {
            writer.writeNull(out);
        } else if (value instanceof String) {
            writer.writeString((String) value, out);
        } else if (value instanceof Byte) {
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
            encode(name,(Object[]) value, out);
        } else if (value.getClass().isArray()) {
            encodeArray(name,value, out);
        } else if (value instanceof Map<?, ?>) {
            encode(name,(Map<?, ?>) value, out);
        } else if (value instanceof Iterable<?>) {
            encode(name,(Iterable<?>) value, out);
        } else if (value instanceof Enumeration<?>) {
            encode(name,(Enumeration<?>) value, out);
        } else if (value instanceof Enum<?>) {
            writer.writeString(((Enum<?>) value).name(), out);
        } else {
            encodeBean(name,value, out);
        }
    }

    private void encode(String name,Object[] array, StringBuilder out) {
        writer.openArray(out);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                writer.writeArrayValueSeperator(out);
            }
            encode(name,array[i], out);
        }
        writer.closeArray(out);
    }

    private void encodeArray(String name,Object array, StringBuilder out) {
        writer.openArray(out);
        int len = Array.getLength(array);
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                writer.writeArrayValueSeperator(out);
            }
            encode(name,Array.get(array, i), out);
        }
        writer.closeArray(out);
    }
    
    private void encode(String name,Iterable<?> iterable, StringBuilder out) {
        writer.openArray(out);
        int index = 0;
        for (Object value : iterable) {
            if (index == 0) {
                index++;
            } else {
                writer.writeArrayValueSeperator(out);
            }
            encode(name,value, out);
        }
        writer.closeArray(out);
    }

    private void encode(String name,Enumeration<?> enumeration, StringBuilder out) {
        writer.openArray(out);
        
        int index = 0;
        while (enumeration.hasMoreElements()) {
            if (index == 0) {
                index++;
            } else {
                writer.writeArrayValueSeperator(out);
            }
            encode(name,enumeration.nextElement(), out);
        }
        writer.closeArray(out);
    }

    private void encode(String name,Map<?, ?> map, StringBuilder out) {
        writer.openObject(out);

        int index = 0;
        for (Object key : map.keySet()) {
            String prop = String.valueOf(key);
            Object propValue = map.get(key);
            
            if(null == propValue && ignoreNull){
                continue;
            }
            
            if(ignoreBlank && (propValue instanceof String) && ((String)propValue).trim().equals("")){
                continue;
            }
            
            if (index == 0) {
                index++;
            } else {
                writer.writePropertyValueSeperator(out);
            }

            encodeNamedValue(prop, map.get(key), out);
        }

        writer.closeObject(out);
    }

    private void encodeBean(String name,Object bean, StringBuilder out) {
        Class<?> clazz = bean.getClass();
        writer.openObject(out);
        
        boolean upperAll    = false;
        boolean lowerAll    = false;
        boolean upperFirst  = false;    
        
        try {
            ReflectType type = ReflectType.get(clazz);

            int index = 0;
            for(ReflectField field : type.getPublicFields()){
                if(field.isPublic() || field.hasGetter()){
                    String propName = field.getName();
                    
                    boolean ignore      = false;
                    String defaultValue  = null;
                    
                    if(ignore){
                        continue;
                    }
                    
                    if(upperFirst){
                        propName = propName.length() > 0 ? propName.substring(0,1).toUpperCase() + propName.substring(1) : "";
                    }else if(upperAll){
                        propName = propName.toUpperCase();
                    }else if(lowerAll){
                        propName = propName.toLowerCase();
                    }
                    
                    Object propValue = field.getValue(bean);
                    
                    if(null == propValue){
                        propValue = defaultValue;
                    }
                    
                    if(null == propValue && ignoreNull){
                        continue;
                    }
                    
                    if(ignoreBlank && (propValue instanceof String) && ((String)propValue).trim().equals("")){
                        continue;
                    }
                    
                    if (index == 0) {
                        index++;
                    } else {
                        writer.writePropertyValueSeperator(out);
                    }

                    encodeNamedValue(propName, propValue, out);
                }
            }
        } catch (Exception e) {
            throw new JSONException("error encoding for value : " + bean.getClass().getName(), e);
        }
        
        writer.closeObject(out);
    }

    private void encodeNamedValue(String name, Object value, StringBuilder out) {
        writer.openName(out);
        writer.writeName(name, out);
        writer.closeName(out);

        writer.openValue(name, out);
        encode(name,value, out);
        writer.closeValue(name, out);
    }
}
