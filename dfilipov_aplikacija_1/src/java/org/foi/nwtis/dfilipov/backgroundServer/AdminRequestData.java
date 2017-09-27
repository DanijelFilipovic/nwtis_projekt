package org.foi.nwtis.dfilipov.backgroundServer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminRequestData implements RequestData
{
	public static final String REQUEST_PATTERN = "^USER ([a-zA-Z0-9_\\-]+); PASSWD ([a-zA-Z0-9_\\-]+);( PAUSE;| START;| STOP;| STATUS;| ADD ([a-zA-Z0-9_\\-]+); PASSWD ([a-zA-Z0-9_\\-]+); ROLE (ADMIN|USER);| (UP|DOWN) ([a-zA-Z0-9_\\-]+);)?$";
	
	public static final int REQUEST_TYPE_AUTHENTICATE	= 0;
	public static final int REQUEST_TYPE_START			= 1;
	public static final int REQUEST_TYPE_PAUSE			= 2;
	public static final int REQUEST_TYPE_STOP			= 3;
	public static final int REQUEST_TYPE_STATUS			= 4;
	public static final int REQUEST_TYPE_ADD			= 5;
	public static final int REQUEST_TYPE_UP				= 6;
	public static final int REQUEST_TYPE_DOWN			= 7;
	
	private final String request;
	
	private int requestType;
	private String username;
	private String password;
	private String paramUsername;
	private String paramPassword;
	private String paramRole;

	public AdminRequestData(String request) 
	{
		this.request = request;
		
		Pattern p = Pattern.compile(REQUEST_PATTERN);
		Matcher m = p.matcher(request);
		boolean matches = m.matches();
		
		if (matches)
		{
			username = m.group(1);
			password = m.group(2);
			
			String thirdGroup = m.group(3);
			if (thirdGroup != null)
			{
				if (thirdGroup.startsWith(" PAUSE"))
				{
					requestType = REQUEST_TYPE_PAUSE;
				}
				else if (thirdGroup.startsWith(" START"))
				{
					requestType = REQUEST_TYPE_START;
				}
				else if (thirdGroup.startsWith(" STOP"))
				{
					requestType = REQUEST_TYPE_STOP;
				}
				else if (thirdGroup.startsWith(" STATUS"))
				{
					requestType = REQUEST_TYPE_STATUS;
				}
				else if (thirdGroup.startsWith(" ADD"))
				{
					requestType = REQUEST_TYPE_ADD;
					paramUsername = m.group(4);
					paramPassword = m.group(5);
					paramRole = m.group(6);
				}
				else if (thirdGroup.startsWith(" UP"))
				{
					requestType = REQUEST_TYPE_UP;
					paramUsername = m.group(8);
				}
				else
				{
					requestType = REQUEST_TYPE_DOWN;
					paramUsername = m.group(8);
				}
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

	public String getParamUsername()
	{
		return paramUsername;
	}

	public void setParamUsername(String paramUsername)
	{
		this.paramUsername = paramUsername;
	}

	public String getParamPassword()
	{
		return paramPassword;
	}

	public void setParamPassword(String paramPassword)
	{
		this.paramPassword = paramPassword;
	}

	public String getParamRole()
	{
		return paramRole;
	}

	public void setParamRole(String paramRole)
	{
		this.paramRole = paramRole;
	}
	
	public String getRequest()
	{
		return request;
	}
}
