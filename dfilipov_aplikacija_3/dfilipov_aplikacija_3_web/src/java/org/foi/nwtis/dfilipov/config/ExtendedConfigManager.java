package org.foi.nwtis.dfilipov.config;

import org.foi.nwtis.dfilipov.configmanager.XMLConfigManager;

public class ExtendedConfigManager extends XMLConfigManager
{	
	public ExtendedConfigManager(String pathname)
	{
		super(pathname);
	}
	
	public String getTableRowCount()
	{
		return configuration.getProperty("table.rowCount");
	}
	
	public String getPrimitiveServerPort()
	{
		return configuration.getProperty("primitiveServer.port");
	}
	
	public String getPrimitiveServerHost()
	{
		return configuration.getProperty("primitiveServer.host");
	}
	
	public String getWebServiceUsername()
	{
		return configuration.getProperty("web.service.username");
	}
	
	public String getWebServicePassword()
	{
		return configuration.getProperty("web.service.password");
	}
}
