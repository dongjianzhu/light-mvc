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

import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;

/**
 * the plugin interface class of mvc framework.
 * 
 * @author fenghm(live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public abstract class Plugin {
	
	protected static final Action[] EMPTY_ACTIONS = new Action[]{};
	
	/**
	 * @return the display name of this plugin
	 */
	public String getName() {
		return this.getClass().getName();
	}

	/**
	 * called at framework initializing and this plugin had been loading.<p>
	 * 
	 * <p/>
	 * if return false or exception occurs, mvc framework will not load this plugin.<p>
	 * 
	 * @return true if this plugin should be loaded by framework
	 */
	protected boolean load() throws Throwable{
		return true;
	}
	
	/**
	 * called at framework destroying and this plugin had been unloading by framework.
	 */	
	protected void unload()  throws Throwable{
	    
    }
	
	/**
	 * called at {@link Framework#ignore(Request)} to determine that should current request be ignored?
	 * @param request http request
	 * @return true if ignore current request,else return false
	 */
	public boolean ignore(Request request) throws Throwable {
		return false;
	}

	/**
	 * called at framework handling a http request
	 * @param request  http request
	 * @param response http response
	 * @return true if this plugin manage current request
	 */	
	public boolean request(Request request, Response response) throws Throwable {
		return false;
	}
	
	/**
	 * routing http request to action.<p>
	 * 
	 * <p/>
	 * called if no plugin managed current request after {@link #request} method executed
	 * 
	 * @param request  http request
	 * @param response http response
	 * @return {@link Action} array which reprensents any actions if matched,else return empty {@link Action}[]
	 */	
	public Action[] route(Request request, Response response) throws Throwable {
		return EMPTY_ACTIONS;
	}

	/**
	 * resolving the action method and args if needed.<p>
	 * 
	 * <p/>
	 * called if action found after {@link #route} method executed.
	 * 
	 * @param request   http request
	 * @param response  http response
	 * @param action    {@link Action} object
	 * @return true if this plugin aleady resolved the action method
	 */	
	public boolean resolve(Request request, Response response, Action action)  throws Throwable {
	    return false;
    }
	
	/**
	 * binding the parameters of action's method of {@link Action#method}.<p>
	 * 
	 * <p/>
	 * called if action method had been resolved after {@link #resolve} method executed
	 * 
	 * @param request  http request
	 * @param response http response
	 * @param action {@link Action} object
	 * @return true if this plugin managed the binding of action parameters
	 */	
	public boolean binding(Request request, Response response, Action action)  throws Throwable {
	    return false;
    }

	/**
	 * execute an action after {@link #resolve} and {@link #binding}
	 * @param request  http request
	 * @param response http response
	 * @param action   {@link Action} object
	 * @return {@link Result} object if action executed by this plugin , else return null
	 */	
	public Result execute(Request request, Response response, Action action) throws Throwable {
	    return null;
    }

	/**
	 * render result for displaying something to user.<p>
	 * 
	 * <p/>
	 * called at {@link #execute} method executed and returned <code>not null</code> {@link Result} object
	 * 
	 * @param request  http request
	 * @param response http response
	 * @param result   {@link Result} object
	 * @return true if rendered by this plugin
	 */	
	public boolean render(Request request, Response response, Result result) throws Throwable {
	    return false;
    }

	/**
	 * called at error occurs while handling a http request
	 * @param request   http request
	 * @param response  http response
	 * @param error {@link org.lightframework.mvc.Result.ErrorResult} object
	 * @return true if this plugin handled this exception
	 */	
	public boolean error(Request request, Response response, Result.ErrorResult error) throws Throwable {
		return false;
	}
	
	/**
	 * called after {@link #render(Request, Response, Result)} or {@link #error(Request, Response, Result.ErrorResult)}.
	 * 
	 * <br/>
	 * 
	 * please note that response is committed after render.
	 * 
	 * @param request   http request
	 * @param response  http response
	 * @param result {@link org.lightframework.mvc.Result} object
	 * @return true if this plugin handled and stop executing other plutins
	 */	
	public boolean response(Request request,Response response, Result result) throws Throwable {
		return false;
	}
}