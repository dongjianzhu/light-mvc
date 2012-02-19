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
package org.lightframework.mvc.demo;

import org.lightframework.mvc.ApplicationContext;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>{@link Application}</code>
 *
 * @author fenghm (fenghm@bingosoft.net)
 */
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    
    public void start(ApplicationContext context){
        log.info("application start");
    }
    
    public void end(ApplicationContext context){
        log.info("application end");
    }
    
    public void error(ApplicationContext context,Request request,Response response,Throwable cause) throws Throwable {
        log.info("application error");
        
        response.write("error : " + cause.getMessage());
    }
}