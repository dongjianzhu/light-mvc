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
package org.lightframework.mvc.json;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * json encoder and decoder.
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public final class JSON {
	
    public static final char[] HEX_CHARS = new char[]{
        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };  	
	
    public static final String NULL_STRING = "null";
    
    public final static SimpleDateFormat RFC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");    
    
    private boolean trimSpace   = true;
    private boolean ignoreNull  = true;
    private boolean ignoreEmpty = true;
    
    private Map<String, String> aliases  = new HashMap<String, String>();
    private Map<String,Object>  ignores  = new HashMap<String,Object>();
    private Map<String,Object>  includes = new HashMap<String, Object>();
    
    public JSON(){
        
    }
    
    public void alias(String alias,String property){
        this.aliases.put("prop$" + property, alias);
    }
    
    public void alias(String alias,Class<?> type,String property){
        this.aliases.put(type.getName() + "$" + property, alias);
    }
    
    public void alias(String alias,Class<?> type){
        this.aliases.put("type$" + type.getName(), alias);
    }
    
    public void ignore(String property){
        this.ignores.put("prop$" + property,null);
    }
    
    public void ignore(Class<?> type,String property){
        this.ignores.put(type.getName() + "$" + property,null);
    }
    
    public void ignore(Class<?> type){
        this.ignores.put("type$" + type.getName(),null);
    }
    
    public void ignore(String property,Object value){
        this.ignores.put("prop$" + property,value);
    }
    
    public void ignore(Class<?> type,Object value){
        this.ignores.put("type$" + type.getName(),value);
    }  
    
    public void ignore(Class<?> parent,Class<?> type){
        this.ignores.put("parent$" + parent.getName() + ",type$" + type.getName(),null);
    }
    
    public void ignore(Class<?> type,String property,Object value){
        this.ignores.put(type.getName() + "$" + property,value);
    } 
    
    public void include(String property){
        this.includes.put("prop$" + property,null);
    }
    
    public void include(Class<?> type,String property){
        this.includes.put(type.getName() + "$" + property,null);
    }
    
    public void include(Class<?> type){
        this.includes.put("type$" + type.getName(),null);
    }
    
    public void include(String property,Object value){
        this.includes.put("prop$" + property,value);
    }
    
    public void include(Class<?> type,Object value){
        this.includes.put("type$" + type.getName(),value);
    }   
    
    public void include(Class<?> parent,Class<?> type){
        this.includes.put("parent$" + parent.getName() + ",type$" + type.getName(),null);
    }    
    
    public void include(Class<?> type,String property,Object value){
        this.includes.put(type.getName() + "$" + property,value);
    }     
    
    protected String getAlias(Class<?> objectType,Class<?> propType,String prop){
        String alias = aliases.get(objectType.getName() + "$" + prop);
        if(null == alias){
            alias = aliases.get("prop$" + prop);
            if(null == alias){
                alias = aliases.get("type$" + propType.getName());
            }
        }
        return alias;
    }
    
    protected boolean isInlcude(Class<?> objectType,Class<?> propType,String propName,Object propValue){
        return isMatch(includes, objectType, propType, propName, propValue);
    }
    
    protected boolean isIgnore(Class<?> objectType,Class<?> propType,String propName,Object propValue){
        boolean include = isInlcude(objectType, propType, propName, propValue);
        if(include){
            return false;
        }else{
            return isMatch(ignores, objectType, propType, propName, propValue);    
        }
    }
    
    protected boolean isMatch(Map<String, Object> map, Class<?> objectType,Class<?> propType,String propName,Object propValue){
        String matchKey = objectType.getName() + "$" + propName;
        boolean isMatch = map.containsKey(matchKey);
        if(!isMatch){
            matchKey = "prop$" + propName;
            isMatch  = map.containsKey(matchKey);
            if(!isMatch){
                matchKey = "parent$" + objectType.getName() + ",type$" + propType.getName();
                isMatch  = map.containsKey(matchKey);
                if(!isMatch){
                    matchKey = "type$" + propType.getName();
                    isMatch  = map.containsKey(matchKey);
                }
            }
        }
        
        if(isMatch){
            Object value = map.get(matchKey);
            if(null != value && !value.equals(propValue)){
                isMatch = false;
            }
        }
        
        return isMatch;  
    }
    
    protected String getPropName(Class<?> objectType,Class<?> propType,String prop){
        String alias = getAlias(objectType, propType, prop);
        return alias == null ? prop : alias;
    }
    
    public void clear(){
    	aliases.clear();
    	ignores.clear();
    	includes.clear();
    }
    
    @SuppressWarnings("unchecked")
    public String encode(Object object){
        if(null != object){
            Class<?> objectType = object.getClass();
            if(object instanceof Collection){
                return encodeColletion((Collection)object);
            }else if(object instanceof Map){
                return encodeMap((Map)object);
            }else if(object.getClass().isArray()){
                return encodeArray(object);
            }else if(isNumberType(objectType)){
                return encodeNumber(object);
            }else if(isBooleanType(objectType)){
                return encodeBoolean(object);
            }else if(isByteType(objectType)){
                return encodeByte(object);
            }else if(isStringType(objectType)){
                return encodeString(object);
            }else if(isDateType(objectType)){
            	return encodeDate((Date)object);
            }else{
                Writer json = new Writer();
                json.startObject();
                try {
                    Class<?> clazz = object.getClass();
                    PropertyDescriptor[] propDescs = Introspector.getBeanInfo(clazz, Introspector.USE_ALL_BEANINFO).getPropertyDescriptors();
                    for (PropertyDescriptor propDesc : propDescs){
                        Method getter = propDesc.getReadMethod();
                        
                        if(null != getter){
                            String propName = propDesc.getName();
                            if(!propName.equalsIgnoreCase("class")){
                                Class<?> type    = propDesc.getPropertyType();
                                Object propValue = getter.invoke(object);
                                
                                if(!(ignoreNull && null == propValue) && !isIgnore(object.getClass(), type, propName, propValue)){
                                    String key   = quote(getPropName(clazz, type, propName));
                                    String value = encode(propValue);
                                    if(!ignoreEmpty || !(value.equals("''") || value.equals("\"\""))){
                                        json.setPropertyQuoted(key, value);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(),e);
                }
                json.endObject();
                return json.toString();
            }   
        }else{
            return NULL_STRING;
        }
    }
    
    protected String encodeArray(Object array){
        Writer json = new Writer();
        json.startArray();        
        int len = Array.getLength(array);
        for(int i=0;i<len;i++){
            Object o = Array.get(array, i);
            if(null != o){
                json.setValueQuoted(encode(o));
            }
        }
        json.endArray();
        return json.toString();
    }
    
    protected String encodeColletion(Collection<Object> col){
        Writer json = new Writer();
        json.startArray();
        
        for(Object o : col){
            if(null != o){
                json.setValueQuoted(encode(o));    
            }
        }
        json.endArray();
        return json.toString();        
    }
    
    protected String encodeMap(Map<Object,Object> map){
        Set<Object> keySet = map.keySet();
        
        Writer json = new Writer();
        json.startObject();      
        for(Object o : keySet){
            Object key = o.toString();
            Object value = map.get(o);
            if(!(ignoreNull && null == value)){
                String prop = getPropName(map.getClass(), key.getClass(), key.toString());
                json.setPropertyQuoted(quote(prop), encode(value));    
            }
        }     
        json.endObject();
        return json.toString();
    }
    
    protected String encodeString(Object value){
        return null == value ? "''" : quote((trimSpace ? value.toString().trim() : value.toString()));
    }
    
    protected String encodeDate(Date date){
    	return null == date ? NULL_STRING : quote(toRfcString(date));
    }
    
    protected String encodeNumber(Object value){
        return null == value ? "0" : String.valueOf(value);
    }
    
    protected String encodeBoolean(Object value){
        return null == value ? "false" : String.valueOf(value).toLowerCase();
    }    
    
    protected String encodeByte(Object value){
        return "0x" + (null == value ? "0" : toHexString(((Byte)value).byteValue()));
    }
    
    static boolean isNumberType(Class<?> type){
        return type == Integer.TYPE || type == Integer.class || 
                    type == Short.TYPE || type == Short.class ||
                    type == Long.TYPE || type == Long.class ||
                    type == Float.TYPE || type == Float.class ||
                    type == Double.TYPE || type == Double.class;
    }
    
    static boolean isBooleanType(Class<?> type){
        return type == Boolean.TYPE || type == Boolean.class;
    }
    
    static boolean isByteType(Class<?> type){
        return type == Byte.TYPE || type == Byte.class;
    }
    
    static boolean isStringType(Class<?> type){
        return type == String.class || type == Character.TYPE || type == Character.class;
    }
    
    static boolean isDateType(Class<?> type){
    	return Date.class.isAssignableFrom(type);
    }
    
    //convert to rfc3339 date format
    public static String toRfcString(Date date){
        StringBuffer buf = new StringBuffer(RFC_DATE_FORMAT.format(date));
        buf.insert(buf.length() - 2, ':');
        return buf.toString();
    }    
    
    public static String toHexString(byte b){
        return String.valueOf(toHexChars(new byte[]{b}));
    }
    
    public static char[] toHexChars(byte[] bytes){
        char[] chars = new char[bytes.length * 2];
        int index = 0;
        for(int i=0;i<bytes.length;i++){
            chars[index++] = HEX_CHARS[(bytes[i] >>> 4) & 0x0F];
            chars[index++] = HEX_CHARS[bytes[i] & 0x0F];
        }
        return chars;
    }
    
    public static String quote(String string,boolean trim){
        if(trim){
            return quote(null == string ? null : string.trim());
        }else{
            return quote(string);
        }
    }

    public static String quote(String string){
        if (string == null || string.length() == 0) {
            return "''";
        }
        char          c = 0;
        int           len = string.length();
        StringBuilder sb = new StringBuilder(len + 10);
        sb.append("'");
        for (int i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
            case '\\':
                sb.append("\\\\");
                break;
            case '\'':
                sb.append("\\'");
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                sb.append(c);
            }
        }
        sb.append("'");
        return sb.toString();        
    }    
    
    public static final class Writer {

        private StringBuilder buf = new StringBuilder();
        
        @Override
        public String toString() {
            return buf.toString();
        }
        
        public void startObject(){
            buf.append("{");
        }
        
        public void endObject(){
            deleteSeparator();
            buf.append("}");
        }
        
        public void startArray(){
            buf.append("[");
        }
        
        public void endArray(){
            deleteSeparator();
            buf.append("]");
        }
        
        public Writer add(String key,String value){
        	setPropertyQuoted(key, value);
        	return this;
        }
        
        void setValueQuoted(String value){
            buf.append(value).append(",");
        }
        
        void setPropertyQuoted(String name,String value){
        	buf.append(name).append(":").append(value).append(",");
        }
        
        private void deleteSeparator(){
            if(buf.length() > 0){
                if(buf.charAt(buf.length()-1) == ','){
                    buf.deleteCharAt(buf.length()-1);
                }else if(buf.length() > 1 && buf.charAt(buf.length()-2) == ',' && buf.charAt(buf.length()-1) == '}'){
                    buf.deleteCharAt(buf.length()-2);
                }
            }
        }
    }    
}
