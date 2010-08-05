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
package org.lightframework.mvc.core;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Result;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;

/**
 * core plugin of mvc framework
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class CorePlugin extends Plugin{

	protected RoutePlugin   router   = new RoutePlugin();
	protected BindingPlugin binder   = new BindingPlugin();
	protected ExecutePlugin executor = new ExecutePlugin();
	protected ResolvePlugin resolver = new ResolvePlugin();
	protected RenderPlugin  renderer = new RenderPlugin();
	protected ErrorPlugin   error    = new ErrorPlugin();
	
	@Override
    public boolean request(Request request, Response response) throws Exception {
	    return super.request(request, response);
    }

	@Override
    public Action route(Request request, Response response) throws Exception {
	    return router.route(request, response);
    }

	@Override
    public boolean resolve(Request request, Response response, Action action) throws Exception {
	    return resolver.resolve(request, response, action);
    }
	
	@Override
    public boolean binding(Request request, Response response, Action action) throws Exception {
	    return binder.binding(request, response, action);
    }
	
	@Override
    public Result execute(Request request, Response response, Action action) throws Exception {
	    return executor.execute(request, response, action);
    }

	@Override
    public boolean render(Request request, Response response, Result render) throws Exception {
	    return renderer.render(request, response, render);
    }

	@Override
    public boolean error(Request request, Response response, Throwable exception) {
	    return error.error(request, response, exception);
    }
}
