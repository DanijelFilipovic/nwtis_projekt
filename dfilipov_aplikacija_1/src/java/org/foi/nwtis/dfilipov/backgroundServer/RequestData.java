package org.foi.nwtis.dfilipov.backgroundServer;


public interface RequestData
{
	public int getRequestType();
	public void setRequestType(int requestType);
	
	public String getUsername();
	public void setUsername(String username);
	
	public String getPassword();
	public void setPassword(String password);
}
