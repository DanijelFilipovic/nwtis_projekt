package org.foi.nwtis.dfilipov.actionLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.database.DatabaseConnector;
import org.foi.nwtis.dfilipov.database.DatabaseInfo;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;

public class ActionLogger
{
	private String username;
	private String password;
	private String requestMethod;
	private String requestParams;
	private boolean status;
	private String webServiceType;

	public ActionLogger() 
	{
		this(null, null, null, null, false, null);
	}

	public ActionLogger(String username, String password, String requestMethod, String requestParams, boolean status, String webServiceType)
	{
		this.username = username;
		this.password = password;
		this.requestMethod = requestMethod;
		this.requestParams = requestParams;
		this.status = status;
		this.webServiceType = webServiceType;
	}
	
	public boolean log()
	{
		ExtendedConfigManager config = 
			(ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		
		DatabaseInfo dbInfo = new DatabaseInfo(
			config.getDatabaseServer(),
			config.getDatabaseName(),
			config.getDatabaseUser(),
			config.getDatabasePass(),
			config.getDatabaseDriver()
		);
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
		{
			String sqlUser = String.format("SELECT id FROM user WHERE username='%s' AND password='%s'", username, password);
			try (ResultSet resultUser = dbConnector.select(sqlUser))
			{
				String userID = "null";
				if (resultUser.next())
					userID = resultUser.getString("id");
				
				String sqlLog = String.format(
					"INSERT INTO log VALUES (DEFAULT, '%s', '%s', %s, %d, '%s', DEFAULT)",
					requestMethod,
					requestParams,
					userID,
					status ? 1 : 0,
					webServiceType
				);

				int affectedRows = dbConnector.update(sqlLog);
				return affectedRows > 0;
			}
		}
		catch (ClassNotFoundException | SQLException ex) 
		{
			System.out.println("Unable to operate with database (Logger): " + ex.getMessage());
			return false;
		}
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getRequestMethod()
	{
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod)
	{
		this.requestMethod = requestMethod;
	}

	public String getRequestParams()
	{
		return requestParams;
	}

	public void setRequestParams(String requestParams)
	{
		this.requestParams = requestParams;
	}

	public boolean isStatus()
	{
		return status;
	}

	public void setStatus(boolean status)
	{
		this.status = status;
	}

	public String getWebServiceType()
	{
		return webServiceType;
	}

	public void setWebServiceType(String webServiceType)
	{
		this.webServiceType = webServiceType;
	}
}
