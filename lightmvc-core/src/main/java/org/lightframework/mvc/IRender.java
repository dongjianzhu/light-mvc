package org.lightframework.mvc;

import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;

/**
 * <coee>interface</code> used to render output content to {@link Response} 
 * 
 * this interface called at {@link Plugin#render(Request, Response, Result)}
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0 
 */
public interface IRender {
	
	/**
	 * render output content to {@link Response}
	 * 
	 * <p>
	 * 
	 * you can get context data from {@link Request}, such as {@link Request#getAction()}
	 *  
	 */
	void render(Request request,Response response) throws Exception;
	
}