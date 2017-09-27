package org.foi.nwtis.dfilipov.web.soap.client;

public class MeteoWSClient
{
	public MeteoWSClient() {}

	public java.util.List<org.foi.nwtis.dfilipov.web.soap.client.Address> getAddedAddresses(java.lang.String username, java.lang.String password)
	{
		org.foi.nwtis.dfilipov.web.soap.client.MeteoWebService_Service service = new org.foi.nwtis.dfilipov.web.soap.client.MeteoWebService_Service();
		org.foi.nwtis.dfilipov.web.soap.client.MeteoWebService port = service.getMeteoWebServicePort();
		return port.getAddedAddresses(username, password);
	}

	public Meteo getLatestMeteoData(java.lang.String address, java.lang.String username, java.lang.String password)
	{
		org.foi.nwtis.dfilipov.web.soap.client.MeteoWebService_Service service = new org.foi.nwtis.dfilipov.web.soap.client.MeteoWebService_Service();
		org.foi.nwtis.dfilipov.web.soap.client.MeteoWebService port = service.getMeteoWebServicePort();
		return port.getLatestMeteoData(address, username, password);
	}
}
