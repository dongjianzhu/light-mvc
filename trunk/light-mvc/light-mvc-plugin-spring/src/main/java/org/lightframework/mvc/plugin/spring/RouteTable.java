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
package org.lightframework.mvc.plugin.spring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.lightframework.mvc.MvcException;
import org.lightframework.mvc.RouteManager;
import org.lightframework.mvc.routing.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * route table of action mapping
 * 
 * @author fenghm (fenghm@bingosoft.net)
 * @since 1.0.0
 */
public class RouteTable {
	private static final Logger log = LoggerFactory.getLogger(RouteTable.class);

	protected List<Route> routes = new ArrayList<Route>();

	public void register() {
		if(null != routes){
			log.debug("[mvc:spring] -> registering {} routes to mvc framework...",routes.size());
			
			log.debug("[mvc:spring] -> method\tpath\t\taction");
			for(Route route : routes){
				if(log.isDebugEnabled()){
					log.debug("[mvc:spring] -> {}\t{}\t{}",
							  new Object[]{route.getMethod(),route.getPath(),route.getAction()});
				}
				RouteManager.add(route);
			}
		}
	}
	
	public List<Route> routes(){
		return routes;
	}
	
	public void setRoutes(String routes){
		if(null != routes){
			BufferedReader reader = new BufferedReader(new StringReader(routes.trim()));
			
			try {
				String line = null;
	            while((line = reader.readLine()) != null){
	            	if(!"".equals(line = line.trim())){
		            	StringTokenizer tokenizer = new StringTokenizer(line);
		            	if(tokenizer.countTokens() != 3){
		            		throw new MvcException("invalid route : " + line);
		            	}
		            	
		            	this.routes.add(Route.compile(tokenizer.nextToken().trim(),tokenizer.nextToken().trim(),tokenizer.nextToken().trim()));
	            	}
	            }
            } catch (IOException e) {
            	throw new RuntimeException(e.getMessage(),e);
            } finally {
            	try {
	                reader.close();
                } catch (IOException e) {
                	; // do nothing
                }
            }
		}
	}
}
