package org.foi.nwtis.dfilipov.web.listeners;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.configmanager.MissingConfigurationFile;

public class AppListener implements ServletContextListener
{
	private static ServletContext servletContext;
	
	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		servletContext = sce.getServletContext();
		try 
		{
			loadConfiguration();
		}
		catch (MissingConfigurationFile ex) 
		{
			System.out.println("Unable to load the configuration file.");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {}
	
	private void loadConfiguration() throws MissingConfigurationFile
	{
		String filename = servletContext.getInitParameter("config");
		String filepath = servletContext.getRealPath("/WEB-INF") + File.separator;
		ExtendedConfigManager config = new ExtendedConfigManager(filepath + filename);
		config.load();
		servletContext.setAttribute("config", config);
	}
	
	public static ServletContext getServletContext()
	{
		return servletContext;
	}
}
