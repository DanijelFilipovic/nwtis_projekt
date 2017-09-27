package org.foi.nwtis.dfilipov.web.filters;

import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.dfilipov.web.entities.Users;

@WebFilter(
	filterName = "RedirectFilter", 
	urlPatterns = {"/faces/user/*", "/faces/admin/*"}, 
	dispatcherTypes = {DispatcherType.REQUEST})
public class RedirectFilter implements Filter
{
	public RedirectFilter() {}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
	throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession(false);
		
		if (session != null && session.getAttribute("USER") != null)
		{
			Users user = (Users) session.getAttribute("USER");
			if (httpRequest.getRequestURI().contains("/faces/admin") && !user.getRole().getName().equals("ADMIN"))
				httpResponse.sendRedirect("../index.xhtml");
			else
				chain.doFilter(request, response);
		}
		else
			httpResponse.sendRedirect("../login.xhtml");
	}

	@Override
	public void destroy() {}
}
