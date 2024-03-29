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
package org.lightframework.mvc.internal.convert;

/**
 * <code>{@link ConvertUnsupportedException}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
public class ConvertUnsupportedException extends ConvertException {

    private static final long serialVersionUID = 7541898939274957125L;

    public ConvertUnsupportedException() {
        super();
    }

    public ConvertUnsupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertUnsupportedException(String message) {
        super(message);
    }

    public ConvertUnsupportedException(Throwable cause) {
        super(cause);
    }
}
