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

import java.util.List;

import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a spring bean stores the {@link Plugin} list and register them to mvc {@link PluginManager} 
 * @author fenghm (fenghm@bingosoft.net)
 * @since 1.0.0
 */
public class PluginRegistry {
	private static final Logger log = LoggerFactory.getLogger(PluginRegistry.class);
	
	protected List<Plugin> plugins;
	
	public void register(){
		if(null != plugins){
			log.debug("[mvc:spring] -> registering {} plugins to mvc framework...",plugins.size());
			int i=1;
			for(Plugin plugin : plugins){
				log.debug("[mvc:spring] -> #{} : {}",i++,plugin.getName());
				PluginManager.registerFirst(plugin);
			}
		}
	}

	public List<Plugin> getPlugins() {
    	return plugins;
    }

	public void setPlugins(List<Plugin> plugins) {
    	this.plugins = plugins;
    }
}
