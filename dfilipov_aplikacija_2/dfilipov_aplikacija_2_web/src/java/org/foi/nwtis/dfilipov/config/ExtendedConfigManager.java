package org.foi.nwtis.dfilipov.config;

import org.foi.nwtis.dfilipov.configmanager.XMLConfigManager;

public class ExtendedConfigManager extends XMLConfigManager
{	
	public ExtendedConfigManager(String pathname)
	{
		super(pathname);
	}
	public String getDatabaseServer()
	{
		return configuration.getProperty("database.server");
	}
	
	public String getDatabaseName()
	{
		return configuration.getProperty("database.name");
	}
	
	public String getDatabaseUsername()
	{
		return configuration.getProperty("database.user");
	}
	
	public String getDatabasePassword()
	{
		return configuration.getProperty("database.pass");
	}
	
	public String getDatabaseDriver()
	{
		return configuration.getProperty("database.driver");
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
	
	public String getRegistererInterval()
	{
		return configuration.getProperty("registerer.interval");
	}
	
	public String getMailHost()
	{
		return configuration.getProperty("mail.host");
	}
	
	public String getMailAddress()
	{
		return configuration.getProperty("mail.address");
	}
	
	public String getMailPassword()
	{
		return configuration.getProperty("mail.password");
	}
	
	public String getMailPortImap()
	{
		return configuration.getProperty("mail.port.imap");
	}
	
	public String getMailSubject()
	{
		return configuration.getProperty("mail.subject");
	}
	
	public String getMailFolderSuccessful()
	{
		return configuration.getProperty("mail.folder.successful");
	}
	
	public String getMailFolderUnsuccessful()
	{
		return configuration.getProperty("mail.folder.unsuccessful");
	}
	
	public String getMailFolderIncorrect()
	{
		return configuration.getProperty("mail.folder.incorrect");
	}
}
