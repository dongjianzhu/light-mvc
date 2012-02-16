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

import java.io.InputStream;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>{@link IOUtils}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
public final class IOUtils {
    
    private static final Logger log = LoggerFactory.getLogger(IOUtils.class);

    public static void close(InputStream in){
        try {
            if(null != in){
                in.close();    
            }
        } catch (Throwable e) {
            log.warn("close input stream error",e);
        }
    }
    
    public static void close(Reader reader){
        try {
            if(null != reader){
                reader.close();    
            }
        } catch (Throwable e) {
            log.warn("close reader error",e);
        }
    }
    
    public static InputStream getResourceAsStream(String path){
        return getResourceAsStream(path,IOUtils.class);
    }
    
    public static InputStream getResourceAsStream(String path,Class<?> loaderClass){
        InputStream stream = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        
        if(null != loader){
            stream = loader.getResourceAsStream(path);
        }
        
        if(null == stream && loaderClass.getClassLoader() != loader){
            stream = loaderClass.getResourceAsStream(path);
        }
        
        return stream;
    }
}
