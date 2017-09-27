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

public final class ShowRequestsBean implements Serializable
{
	private final List<Log> requests = new ArrayList<>();
	private final ExtendedConfigManager config;
	private final SimpleDateFormat sqlToJava = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
	
	private final int rowCount;
	private int maxPage;
	private int currentPage = 1;
	
	private String filterAddress;
	private String filterDateFrom;
	private String filterDateTo;
	
	public ShowRequestsBean()
	{
		config = (ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		rowCount = Integer.parseInt(config.getTableRowCount());
		loadLogs();
	}
	
	public List<Log> getRequests()
	{
		List<Log> requestsByPage = new ArrayList<>();
		
		if (currentPage < 1)
			currentPage = 1;
		
		for (int i = (currentPage - 1) * rowCount; i < (rowCount * currentPage) && i < requests.size(); i++)
			requestsByPage.add(requests.get(i));
		
		return requestsByPage;
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

	public String getFilterAddress()
	{
		return filterAddress;
	}

	public void setFilterAddress(String filterAddress)
	{
		this.filterAddress = filterAddress;
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
		requests.clear();
		
		DatabaseInfo dbInfo = new DatabaseInfo(
			config.getDatabaseServer(),
			config.getDatabaseName(),
			config.getDatabaseUser(),
			config.getDatabasePass(),
			config.getDatabaseDriver()
		);
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
		{	
			String sql = "SELECT l.id, l.method, u.username, l.status, l.ws_type, l.datetime FROM log l LEFT JOIN user u ON l.user=u.id WHERE l.ws_type<>'REST' AND l.ws_type<>'SOAP'";
			
			if (filterAddress != null && !filterAddress.isEmpty() && !filterAddress.equals(" "))
				sql += String.format(" AND l.method LIKE '%% \"%s\";'", filterAddress);
			
			if (filterDateFrom != null && !filterDateFrom.isEmpty())
				sql += String.format(" AND l.datetime > STR_TO_DATE('%s 00:00:00', '%%d.%%m.%%Y. %%H:%%i:%%s')", filterDateFrom);
			
			if (filterDateTo != null && !filterDateTo.isEmpty())
				sql += String.format(" AND l.datetime < STR_TO_DATE('%s 23:59:59', '%%d.%%m.%%Y. %%H:%%i:%%s')", filterDateTo);
			
			sql += " ORDER BY l.id";
			
			try (ResultSet result = dbConnector.select(sql))
			{
				while (result.next())
				{
					Log request = new Log();
					request.setId(result.getLong("id"));
					request.setMethod(result.getString("method"));
					request.setUsername(result.getString("username"));
					request.setStatus(result.getInt("status"));
					request.setWebServiceType(result.getString("ws_type"));
					
					try 
					{
						request.setDatetime(sqlToJava.parse(result.getString("datetime")));
					}
					catch (ParseException ex) 
					{
						request.setDatetime(new Date(0));
					}
					
					requests.add(request);
				}
			}
		}
		catch (SQLException | ClassNotFoundException ex) 
		{
			System.out.println("Unable to operate with database (ShowRequestsBean): " + ex.getMessage());
		}
		
		calculateMaxPage();
	}
	
	private void calculateMaxPage()
	{
		double doubleMaxPage = ((double)requests.size()) / ((double)rowCount);
		maxPage = (int) Math.ceil(doubleMaxPage);
	}
	
	public String formatDate(Date date)
	{
		return dateFormat.format(date);
	}
}
