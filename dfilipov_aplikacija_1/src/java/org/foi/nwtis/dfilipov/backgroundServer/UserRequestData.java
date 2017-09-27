package org.foi.nwtis.dfilipov.backgroundServer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRequestData implements RequestData
{
	public static final String REQUEST_PATTERN = "^USER ([a-zA-Z0-9_\\-]+); PASSWD ([a-zA-Z0-9_\\-]+);( (TEST|GET|ADD) \"([a-zA-Z0-9čČćĆšŠđĐžŽ\\-\\ ,\\.]+)\";)?$";
	
	public static final int REQUEST_TYPE_AUTHENTICATE	= 0;
	public static final int REQUEST_TYPE_TEST			= 1;
	public static final int REQUEST_TYPE_GET			= 2;
	public static final int REQUEST_TYPE_ADD			= 3;
	
	private int requestType;
	private String username;
	private String password;
	private String address;
	
	private int id;
	
	public UserRequestData(String request)
	{
		Pattern p = Pattern.compile(REQUEST_PATTERN);
		Matcher m = p.matcher(request);
		boolean matches = m.matches();
		
		if (matches)
		{
			username = m.group(1);
			password = m.group(2);
			if (m.group(3) != null)
			{
				String command = m.group(4);
				
				switch (command) 
				{
					case "TEST":
						requestType = REQUEST_TYPE_TEST;
						break;
					case "GET":
						requestType = REQUEST_TYPE_GET;
						break;
					default:
						requestType = REQUEST_TYPE_ADD;
				}
				
				address = m.group(5);
			}
			else
			{
				requestType = REQUEST_TYPE_AUTHENTICATE;
			}
		}
	}

	@Override
	public int getRequestType()
	{
		return requestType;
	}

	@Override
	public void setRequestType(int requestType)
	{
		this.requestType = requestType;
	}

	@Override
	public String getUsername()
	{
		return username;
	}

	@Override
	public void setUsername(String username)
	{
		this.username = username;
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	@Override
	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
}
