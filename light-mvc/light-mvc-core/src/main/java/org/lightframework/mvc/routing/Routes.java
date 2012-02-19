/*
 * Copyright 2012 the original author or authors.
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
package org.lightframework.mvc.routing;

import java.util.LinkedList;

/**
 * <code>{@link Routes}</code>
 *
 * @author fenghm (fenghm@bingosoft.net)
 *
 * @since 1.1.0
 */
public class Routes extends LinkedList<Route> {

    private static final long serialVersionUID = -1813877865417877675L;

    public void addFirst(String path,String action){
    	this.addFirst(Route.compile("*", path, action));
    }
    
    public void addFirst(String method,String path,String action){
    	this.addFirst(Route.compile(method, path, action));
    }

    public void add(String path,String action){
    	this.add(Route.compile("*", path, action));
    }
    
    public void add(String method,String path,String action){
    	this.add(Route.compile(method, path, action));
    }
    
    public void addLast(String path,String action){
    	this.addLast(Route.compile("*", path, action));
    }
    
    public void addLast(String method,String path,String action){
    	this.addLast(Route.compile(method, path, action));
    }
}