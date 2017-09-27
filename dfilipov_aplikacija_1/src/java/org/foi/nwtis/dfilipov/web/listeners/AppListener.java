package org.foi.nwtis.dfilipov.web.listeners;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.dfilipov.backgroundServer.BackgroundServer;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.configmanager.MissingConfigurationFile;
import org.foi.nwtis.dfilipov.meteoDownloader.MeteoDownloader;

@WebListener
public class AppListener implements ServletContextListener
{
	private static ServletContext servletContext;
	
	private BackgroundServer backgroundServer;
	private MeteoDownloader meteoDownloader;
	
	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		servletContext = sce.getServletContext();
		
		try {
			loadConfiguration();
			startBackgroundServer();
			startMeteoDownloader();
		}
		catch (MissingConfigurationFile ex) {
			System.out.println("Unable to load the configuration file. Background server won't start with this error");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		if (backgroundServer != null && backgroundServer.isAlive())
			backgroundServer.setRunning(false);
		
		if (meteoDownloader != null && meteoDownloader.isAlive())
			meteoDownloader.interrupt();
	}
	
	private void loadConfiguration() throws MissingConfigurationFile
	{
		String filename = servletContext.getInitParameter("config");
		String filepath = servletContext.getRealPath("/WEB-INF") + File.separator;
		ExtendedConfigManager config = new ExtendedConfigManager(filepath + filename);
		config.load();
		servletContext.setAttribute("config", config);
	}
	
	private void startBackgroundServer()
	{
		backgroundServer = new BackgroundServer(this);
		backgroundServer.start();
	}
	
	private void startMeteoDownloader()
	{
		meteoDownloader = new MeteoDownloader(this);
		meteoDownloader.start();
	}

	public BackgroundServer getBackgroundServer()
	{
		return backgroundServer;
	}

	public MeteoDownloader getMeteoDownloader()
	{
		return meteoDownloader;
	}
	
	public static ServletContext getServletContext()
	{
		return servletContext;
	}
}
