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
package org.lightframework.mvc;

import java.util.LinkedList;
import java.util.List;

/**
 * manage the {@link Plugin}s in mvc framework.
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * @since 1.0.0
 */
public final class PluginManager {
	
	private static final LinkedList<Plugin> plugins = new LinkedList<Plugin>();

	/**
	 * register a {@link Plugin} to current {@link Application} and order first
	 */
	public static void registerFirst(Plugin plugin){
		//XXX : register plugin to root module of current application ?
		getPlugins().addFirst(plugin);
	}
	
	/**
	 * register a plugin to current {@link Application} and order last
	 */
	public static void registerLast(Plugin plugin){
		getPlugins().addLast(plugin);
	}
	
	/**
	 * register a {@link Plugin} to current {@link Application} and set order to the given index.
	 */
	public static void register(int index,Plugin plugin){
		getPlugins().add(index,plugin);
	}

	/**
	 * register a list of {@link Plugin} to current {@link Application}
	 */
	public static void register(List<Plugin> plugins){
		getPlugins().addAll(0, plugins);
	}
	
	protected static LinkedList<Plugin> getPlugins(){
		return plugins;
	}
}
