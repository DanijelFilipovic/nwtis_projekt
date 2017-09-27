package org.foi.nwtis.dfilipov.web.data;

import java.util.Date;
import org.foi.nwtis.dfilipov.web.soap.client.Address;

public class Meteo
{
	private Address address;
	private String temperature;
	private String pressure;
	private String humidity;
	private String windSpeed;
	private String weather;
	private Date lastUpdate;
	private Date lastDownload;

	public Meteo()
	{
	}

	public Meteo(Address address, String temperature, String pressure, String humidity, String windSpeed, String weather, Date lastUpdate, Date lastDownload)
	{
		this.address = address;
		this.temperature = temperature;
		this.pressure = pressure;
		this.humidity = humidity;
		this.windSpeed = windSpeed;
		this.weather = weather;
		this.lastUpdate = lastUpdate;
		this.lastDownload = lastDownload;
	}

	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}

	public String getTemperature()
	{
		return temperature;
	}

	public void setTemperature(String temperature)
	{
		this.temperature = temperature;
	}

	public String getPressure()
	{
		return pressure;
	}

	public void setPressure(String pressure)
	{
		this.pressure = pressure;
	}

	public String getHumidity()
	{
		return humidity;
	}

	public void setHumidity(String humidity)
	{
		this.humidity = humidity;
	}

	public String getWindSpeed()
	{
		return windSpeed;
	}

	public void setWindSpeed(String windSpeed)
	{
		this.windSpeed = windSpeed;
	}

	public String getWeather()
	{
		return weather;
	}

	public void setWeather(String weather)
	{
		this.weather = weather;
	}

	public Date getLastUpdate()
	{
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate)
	{
		this.lastUpdate = lastUpdate;
	}

	public Date getLastDownload()
	{
		return lastDownload;
	}

	public void setLastDownload(Date lastDownload)
	{
		this.lastDownload = lastDownload;
	}
}
