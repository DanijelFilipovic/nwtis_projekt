package org.foi.nwtis.dfilipov.web.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.web.entities.Logs;
import org.foi.nwtis.dfilipov.web.entities.beans.LogsFacade;
import org.foi.nwtis.dfilipov.web.entities.beans.UsersFacade;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;

@Named(value = "logBean")
@SessionScoped
public class LogBean implements Serializable
{
	@EJB
	private LogsFacade logsFacade;
	
	@EJB
	private UsersFacade usersFacade;
	
	private String request;
	private String user;
	private Date dateFrom;
	private Date dateTo;
	
	private int currentPage = 1;
	private final int rowCount;
	
	public LogBean() 
	{
		ExtendedConfigManager config = 
			(ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		
		rowCount = Integer.parseInt(config.getTableRowCount());
	}

	public String getRequest()
	{
		return request;
	}

	public void setRequest(String request)
	{
		this.request = request;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public Date getDateFrom()
	{
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom)
	{
		this.dateFrom = dateFrom;
	}

	public Date getDateTo()
	{
		return dateTo;
	}

	public void setDateTo(Date dateTo)
	{
		this.dateTo = dateTo;
	}
	
	public int getCurrentPage()
	{
		return currentPage;
	}
	
	public List<Logs> getLogs()
	{
		int firstRow = (currentPage - 1) * rowCount;
		return logsFacade.findWithFilter(firstRow, rowCount, request, usersFacade.findByUsername(user), dateFrom, dateTo);
	}
	
	public void resetPage()
	{
		currentPage = 1;
	}
	
	public void incrementPage()
	{
		currentPage++;
	}
	
	public void decrementPage()
	{
		if (currentPage > 1)
			currentPage--;
	}
}
