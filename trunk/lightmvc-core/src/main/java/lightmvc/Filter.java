/**
 * file created at 2010-2-26 下午03:22:46
 */
package lightmvc;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * TODO : document me
 *
 * @author light.wind
 */
public class Filter implements javax.servlet.Filter {

	protected ServletContext servletContext = null;
	
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

	}

	public void init(FilterConfig config) throws ServletException {
		this.servletContext = config.getServletContext();
	}
	
	public void destroy() {

	}
}
