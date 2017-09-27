package org.foi.nwtis.dfilipov.web.data;

import java.util.Date;

public class Log
{
	private long id;
	private String method;
	private String parameters;
	private String username;
	private int status;
	private String webServiceType;
	private Date datetime;

	public Log() {}

	public Log(long id, String method, String parameters, String username, int status, String webServiceType, Date datetime)
	{
		this.id = id;
		this.method = method;
		this.parameters = parameters;
		this.username = username;
		this.status = status;
		this.webServiceType = webServiceType;
		this.datetime = datetime;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public String getParameters()
	{
		return parameters;
	}

	public void setParameters(String parameters)
	{
		this.parameters = parameters;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
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

	public Date getDatetime()
	{
		return datetime;
	}

	public void setDatetime(Date datetime)
	{
		this.datetime = datetime;
	}
}
