package org.foi.nwtis.dfilipov.web.data;

public class Address
{
	private long id;
	private String address;
	private String latitude;
	private String longitude;
	
	public Address() 
	{
		this(0, null, null, null);
	}

	public Address(String address, String latitude, String longitude)
	{
		this(0, address, latitude, longitude);
	}
	
	public Address(long id, String address, String latitude, String longitude)
	{
		this.id = id;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public long getID()
	{
		return id;
	}

	public void setID(long id)
	{
		this.id = id;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getLatitude()
	{
		return latitude;
	}

	public void setLatitude(String latitude)
	{
		this.latitude = latitude;
	}

	public String getLongitude()
	{
		return longitude;
	}

	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
	}
}
