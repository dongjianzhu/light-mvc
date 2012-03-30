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

/**
 * <code>{@link JSONSettings}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
class JSONSettings {
    
    static final int COMPRESS      = 1;
    static final int KEY_NON_QUOTE = 2;
    static final int IGNORE_NULL   = 4;
    static final int IGNORE_BLANK  = 8;
    
    private final boolean isCompress;
    private final boolean isKeyQuoted;
    private final boolean isIgnoreNull;
    private final boolean isIgnoreBlank;
    
    JSONSettings() {
        isCompress    = false;
        isKeyQuoted   = true;
        isIgnoreNull  = false;
        isIgnoreBlank = false;
    }

    JSONSettings(int setting){
        this.isCompress    = (setting & COMPRESS) > 0; 
        this.isKeyQuoted   = (setting & KEY_NON_QUOTE) == 0;
        this.isIgnoreNull  = (setting & IGNORE_NULL) > 0;
        this.isIgnoreBlank = (setting & IGNORE_BLANK) > 0;
    }

    public boolean isCompress() {
        return isCompress;
    }

    public boolean isKeyQuoted() {
        return isKeyQuoted;
    }

    public boolean isIgnoreNull() {
        return isIgnoreNull;
    }

    public boolean isIgnoreBlank() {
        return isIgnoreBlank;
    }
}