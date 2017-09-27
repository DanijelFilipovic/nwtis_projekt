package org.foi.nwtis.dfilipov.config;

import org.foi.nwtis.dfilipov.configmanager.XMLConfigManager;

public class ExtendedConfigManager extends XMLConfigManager
{
	public ExtendedConfigManager() {}

	public ExtendedConfigManager(String pathname)
	{
		super(pathname);
	}
	
	public String getDatabaseServer()
	{
		return getAttribute("database.server");
	}
	
	public String getDatabaseName()
	{
		return getAttribute("database.name");
	}
	
	public String getDatabaseUser()
	{
		return getAttribute("database.user");
	}
	
	public String getDatabasePass()
	{
		return getAttribute("database.pass");
	}
	
	public String getDatabaseDriver()
	{
		return getAttribute("database.driver");
	}
	
	public String getOWMApiKey()
	{
		return getAttribute("owm.apiKey");
	}
	
	public String getOWMDownloadInterval()
	{
		return getAttribute("owm.downloadInterval");
	}
	
	public String getBackgroundServerPort()
	{
		return getAttribute("backgroundServer.port");
	}
	
	public String getBackgroundServerThreadPoolSize()
	{
		return getAttribute("backgroundServer.threadPoolSize");
	}
	
	public String getMailAddressFrom()
	{
		return getAttribute("mail.address.from");
	}
	
	public String getMailPasswordFrom()
	{
		return getAttribute("mail.password.from");
	}
	
	public String getMailAddressTo()
	{
		return getAttribute("mail.address.to");
	}
	
	public String getMailPasswordTo()
	{
		return getAttribute("mail.password.to");
	}
	
	public String getMailPortSMTP()
	{
		return getAttribute("mail.port.smtp");
	}
	
	public String getMailHost()
	{
		return getAttribute("mail.host");
	}
	
	public String getMailSubject()
	{
		return getAttribute("mail.subject");
	}
	
	public String getTableRowCount()
	{
		return getAttribute("table.rowCount");
	}
}
