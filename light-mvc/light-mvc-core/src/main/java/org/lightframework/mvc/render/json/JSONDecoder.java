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

import java.io.Reader;

/**
 * <code>{@link JSONDecoder}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class JSONDecoder {
    
    public JSONDecoder(){
        this(new JSONSettings());
    }
    
    public JSONDecoder(JSONSettings settings){
        
    }

    public Object decode(String string){
        return new JSONParser(JSONParser.MODE_PERMISSIVE).parse(string);
    }
    
    public Object decode(Reader reader){
        return new JSONParser(JSONParser.MODE_PERMISSIVE).parse(reader);
    }
}