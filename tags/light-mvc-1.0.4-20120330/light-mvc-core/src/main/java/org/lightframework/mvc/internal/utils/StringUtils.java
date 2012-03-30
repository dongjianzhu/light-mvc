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

import java.util.StringTokenizer;


/**
 * <code>{@link StringUtils}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
public class StringUtils{

    /**
     * @return 指定的字符串是否为<code>null</code>或者空字符串（包含空白字符也被认为是空字符串）
     */
    public static boolean isEmpty(String string){
        return null == string || "".equals(string.trim());
    }
    
    /**
     * @return 返回结果与{@link #isEmpty(String)}相反
     */
    public static boolean isNotEmpty(String string){
        return null != string && !"".equals(string.trim());
    }
    
    /**
     * 替换一个字符中指定的内容为另外一部分内容（非正则表达式）
     */
    public static String replace(String text, String replace, String replaceTo)
    {
        if (text == null || replace == null || replaceTo == null || replace.length() == 0)
        {
            return text;
        }

        StringBuffer buf = new StringBuffer(text.length());
        int searchFrom = 0;
        while (true)
        {
            int foundAt = text.indexOf(replace, searchFrom);
            if (foundAt == -1)
            {
                break;
            }

            buf.append(text.substring(searchFrom, foundAt)).append(replaceTo);
            searchFrom = foundAt + replace.length();
        }
        buf.append(text.substring(searchFrom));

        return buf.toString();
    }
    
    public static String replaceOnce(String string, String placeholder, String replacement) {
        if ( string == null ) {
            return string; // returnign null!
        }
        
        int loc = string.indexOf( placeholder );
        
        if ( loc < 0 ) {
            return string;
        } else {
            return new StringBuilder( string.substring( 0, loc ) )
                            .append( replacement )
                            .append( string.substring( loc + placeholder.length() ) )
                            .toString();
        }
    }    
    
    public static String concat(String... strings){
        if(strings.length == 0){
            return "";
        }
        
        StringBuilder buf = new StringBuilder();
        
        for(String str : strings){
            if(null != str){
                buf.append(str);
            }
        }
        
        return buf.toString();
    }
    
    /**
     * 把对象数值中的对象转换为字符串并通过逗号连接成为一个字符串
     */
    public static String join(Object[] objects){
        return join(objects,",");
    }
    
    /**
     * 把对象数值中的对象转换为字符串并通过指定的连接符号连接成为一个字符串
     */
    public static String join(Object[] objects,char seperator){
        return join(objects,String.valueOf(seperator));
    }
    
    /**
     * 把对象数值中的对象转换为字符串并通过指定的连接符号连接成为一个字符串
     */    
    public static String join(Object[] objects,String seperator){
        if(null == objects || objects.length == 0){
            return "";
        }
        
        if(objects.length == 1){
            return objects[0] == null ? "" : objects[0].toString();
        }
        
        StringBuilder string = new StringBuilder();
        
        for(Object object : objects){
            string.append(seperator).append(object);
        }
        
        return string.substring(seperator.length()).toString();
    }
    
    /**
     * 按逗号分隔字符串并转换为字符串数组
     */    
    public static String[] split(String string){
        return split(string,",");
    }
    
    /**
     * 和{@link String#split(String)}相同的功能，只是分隔字符串不需要使用正则表达式的写法
     */
    public static String[] split(String string,String seperator){
        if(null == string){
            return new String[]{};
        }
        
        StringTokenizer tokens = new StringTokenizer(string.trim(), seperator, false);
        String[] result = new String[ tokens.countTokens() ];
        int i=0;
        while ( tokens.hasMoreTokens() ) {
            result[i++] = tokens.nextToken().trim();
        }
        return result;        
    }
    
    /**
     * 把字符串转换为大写，如果传入的字符串是<code>null</code>则直接返回<code>null</code>
     */
    public static String upperCase(String string){
        if(null == string){
            return null;
        }
        return string.toUpperCase();
    }
}
