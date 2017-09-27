package org.foi.nwtis.dfilipov.web.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.database.DatabaseConnector;
import org.foi.nwtis.dfilipov.database.DatabaseInfo;
import org.foi.nwtis.dfilipov.web.data.Log;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;

public final class ShowLogBean implements Serializable
{
	private final List<Log> logs = new ArrayList<>();
	private final ExtendedConfigManager config;
	private final SimpleDateFormat sqlToJava = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
	
	private final int rowCount;
	private int maxPage;
	private int currentPage = 1;
	
	private String filterType;
	private String filterUsername;
	private String filterMethod;
	private String filterDateFrom;
	private String filterDateTo;
	
	public ShowLogBean()
	{
		config = (ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		rowCount = Integer.parseInt(config.getTableRowCount());
		loadLogs();
	}
	
	public List<Log> getLogs()
	{
		List<Log> logsByPage = new ArrayList<>();
		
		if (currentPage < 1)
			currentPage = 1;
		
		for (int i = (currentPage - 1) * rowCount; i < (rowCount * currentPage) && i < logs.size(); i++)
			logsByPage.add(logs.get(i));
		
		return logsByPage;
	}
	
	public int getMaxPage()
	{
		return maxPage;
	}

	public int getCurrentPage()
	{
		return currentPage;
	}

	public void setCurrentPage(int currentPage)
	{
		this.currentPage = currentPage;
	}

	public String getFilterType()
	{
		return filterType;
	}

	public void setFilterType(String filterType)
	{
		this.filterType = filterType;
	}

	public String getFilterUsername()
	{
		return filterUsername;
	}

	public void setFilterUsername(String filterUsername)
	{
		this.filterUsername = filterUsername;
	}

	public String getFilterMethod()
	{
		return filterMethod;
	}

	public void setFilterMethod(String filterMethod)
	{
		this.filterMethod = filterMethod;
	}

	public String getFilterDateFrom()
	{
		return filterDateFrom;
	}

	public void setFilterDateFrom(String filterDateFrom)
	{
		this.filterDateFrom = filterDateFrom;
	}

	public String getFilterDateTo()
	{
		return filterDateTo;
	}

	public void setFilterDateTo(String filterDateTo)
	{
		this.filterDateTo = filterDateTo;
	}
		
	public void loadLogs()
	{
		logs.clear();
		
		DatabaseInfo dbInfo = new DatabaseInfo(
			config.getDatabaseServer(),
			config.getDatabaseName(),
			config.getDatabaseUser(),
			config.getDatabasePass(),
			config.getDatabaseDriver()
		);
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
		{	
			String sql = "SELECT l.id, l.method, l.parameters, u.username, l.status, l.ws_type, l.datetime FROM log l LEFT JOIN user u ON l.user=u.id WHERE l.ws_type<>'USER COMMAND' AND l.ws_type<>'ADMIN COMMAND'";
			
			if (filterType != null && !filterType.isEmpty() && !filterType.equals(" "))
				sql += String.format(" AND l.ws_type='%s'", filterType);
			
			if (filterUsername != null && !filterUsername.isEmpty() && !filterUsername.equals(" "))
				sql += String.format(" AND u.username='%s'", filterUsername);
			
			if (filterMethod != null && !filterMethod.isEmpty() && !filterMethod.equals(" "))
				sql += String.format(" AND l.method='%s'", filterMethod);
			
			if (filterDateFrom != null && !filterDateFrom.isEmpty())
				sql += String.format(" AND l.datetime > STR_TO_DATE('%s 00:00:00', '%%d.%%m.%%Y. %%H:%%i:%%s')", filterDateFrom);
			
			if (filterDateTo != null && !filterDateTo.isEmpty())
				sql += String.format(" AND l.datetime < STR_TO_DATE('%s 23:59:59', '%%d.%%m.%%Y. %%H:%%i:%%s')", filterDateTo);
			
			sql += " ORDER BY l.id";
			
			System.out.println(sql);
			try (ResultSet result = dbConnector.select(sql))
			{
				while (result.next())
				{
					Log log = new Log();
					log.setId(result.getLong("id"));
					log.setMethod(result.getString("method"));
					log.setParameters(result.getString("parameters"));
					log.setUsername(result.getString("username"));
					log.setStatus(result.getInt("status"));
					log.setWebServiceType(result.getString("ws_type"));
					
					try 
					{
						log.setDatetime(sqlToJava.parse(result.getString("datetime")));
					}
					catch (ParseException ex) 
					{
						log.setDatetime(new Date(0));
					}
					
					logs.add(log);
				}
			}
		}
		catch (SQLException | ClassNotFoundException ex) 
		{
			System.out.println("Unable to operate with database (ShowUsersBean): " + ex.getMessage());
		}
		
		calculateMaxPage();
	}
	
	private void calculateMaxPage()
	{
		double doubleMaxPage = ((double)logs.size()) / ((double)rowCount);
		maxPage = (int) Math.ceil(doubleMaxPage);
	}
	
	public String formatDate(Date date)
	{
		return dateFormat.format(date);
	}
}
