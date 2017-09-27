package org.foi.nwtis.dfilipov.web.filters;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import javax.ejb.EJB;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.dfilipov.web.entities.Logs;
import org.foi.nwtis.dfilipov.web.entities.Users;
import org.foi.nwtis.dfilipov.web.entities.beans.LogsFacade;

@WebFilter(filterName = "LogFilter", urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST})
public class LogFilter implements Filter
{
	@EJB
	private LogsFacade logsFacade;
	
	private static final boolean debug = true;
	private FilterConfig filterConfig = null;
	
	private long operationLength;
	private String requestURI;
	private Users user;
	
	private boolean doLog;
	
	public LogFilter() {}	
	
	private void doBeforeProcessing(ServletRequest request, ServletResponse response) throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		if (!httpRequest.getRequestURI().endsWith(".css") && !httpRequest.getRequestURI().endsWith(".js"))
		{
			doLog = true;
			
			operationLength = System.currentTimeMillis();
			requestURI = httpRequest.getRequestURI();

			HttpSession session = httpRequest.getSession(false);
			if (session != null )
				user = (Users) session.getAttribute("USER");
			else
				user = null;
		}
		else
			doLog = false;
	}	
	
	private void doAfterProcessing(ServletRequest request, ServletResponse response) throws IOException, ServletException
	{
		if (doLog)
		{
			operationLength = System.currentTimeMillis() - operationLength;
			Logs log = new Logs();
			log.setLength((int) operationLength);
			log.setRequest(requestURI);
			log.setUser(user);
			log.setDatetime(new Date());
			logsFacade.create(log);
		}
	}

	/**
	 *
	 * @param request The servlet request we are processing
	 * @param response The servlet response we are creating
	 * @param chain The filter chain we are processing
	 *
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a servlet error occurs
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		doBeforeProcessing(request, response);
		
		Throwable problem = null;
		try {
			chain.doFilter(request, response);
		}
		catch (Throwable t) {
			problem = t;
			t.printStackTrace();
		}
		
		doAfterProcessing(request, response);

		if (problem != null) {
			if (problem instanceof ServletException) {
				throw (ServletException) problem;
			}
			if (problem instanceof IOException) {
				throw (IOException) problem;
			}
			sendProcessingError(problem, response);
		}
	}

	/**
	 * Return the filter configuration object for this filter.
	 * @return FilterConfig
	 */
	public FilterConfig getFilterConfig()
	{
		return (this.filterConfig);
	}

	/**
	 * Set the filter configuration object for this filter.
	 *
	 * @param filterConfig The filter configuration object
	 */
	public void setFilterConfig(FilterConfig filterConfig)
	{
		this.filterConfig = filterConfig;
	}

	/**
	 * Destroy method for this filter
	 */
	@Override
	public void destroy()
	{		
	}

	/**
	 * Init method for this filter
	 */
	@Override
	public void init(FilterConfig filterConfig)
	{		
		this.filterConfig = filterConfig;
		if (filterConfig != null) {
			if (debug) {				
				log("LogFilter:Initializing filter");
			}
		}
	}

	/**
	 * Return a String representation of this object.
	 */
	@Override
	public String toString()
	{
		if (filterConfig == null) {
			return ("LogFilter()");
		}
		StringBuffer sb = new StringBuffer("LogFilter(");
		sb.append(filterConfig);
		sb.append(")");
		return (sb.toString());
	}
	
	private void sendProcessingError(Throwable t, ServletResponse response)
	{
		String stackTrace = getStackTrace(t);		
		
		if (stackTrace != null && !stackTrace.equals("")) {
			try {
				response.setContentType("text/html");
				PrintStream ps = new PrintStream(response.getOutputStream());
				PrintWriter pw = new PrintWriter(ps);				
				pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n");

				pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");				
				pw.print(stackTrace);				
				pw.print("</pre></body>\n</html>");
				pw.close();
				ps.close();
				response.getOutputStream().close();
			}
			catch (Exception ex) {
			}
		}
		else {
			try {
				PrintStream ps = new PrintStream(response.getOutputStream());
				t.printStackTrace(ps);
				ps.close();
				response.getOutputStream().close();
			}
			catch (Exception ex) {
			}
		}
	}
	
	public static String getStackTrace(Throwable t)
	{
		String stackTrace = null;
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			pw.close();
			sw.close();
			stackTrace = sw.getBuffer().toString();
		}
		catch (Exception ex) {
		}
		return stackTrace;
	}
	
	public void log(String msg)
	{
		filterConfig.getServletContext().log(msg);		
	}	
}
