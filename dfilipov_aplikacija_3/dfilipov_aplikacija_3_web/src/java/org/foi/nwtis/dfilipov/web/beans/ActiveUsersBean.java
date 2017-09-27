package org.foi.nwtis.dfilipov.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.web.data.User;
import org.foi.nwtis.dfilipov.web.data.Address;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;
import org.foi.nwtis.dfilipov.web.rest.client.ActiveUsersRESTClient;
import org.foi.nwtis.dfilipov.web.soap.client.Meteo;
import org.foi.nwtis.dfilipov.web.soap.client.MeteoWSClient;

@Named(value = "activeUsersBean")
@SessionScoped
public class ActiveUsersBean implements Serializable
{
	private final List<User> activeUsers;
	private final List<Address> addresses;
	
	private Integer chosenUserID;
	private String chosenAddress;
	
	boolean reload = true;
	boolean showingAddresses = false;
	boolean showingMeteo = false;
	
	private final String webServiceUsername;
	private final String webServicePassword;
	
	public ActiveUsersBean() 
	{
		this.activeUsers = new ArrayList<>();
		this.addresses = new ArrayList<>();
		
		ExtendedConfigManager config =
			(ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		
		webServiceUsername = config.getWebServiceUsername();
		webServicePassword = config.getWebServicePassword();
	}
	
	public List<User> getActiveUsers()
	{
		if (reload)
		{
			ActiveUsersRESTClient auClient = new ActiveUsersRESTClient();
			activeUsers.clear();
			activeUsers.addAll(auClient.getActiveUsers());
			auClient.close();
			reload = false;
		}
		return activeUsers;
	}

	public List<Address> getAddresses()
	{
		addresses.clear();
		if (chosenUserID != null)
		{
			ActiveUsersRESTClient auClient = new ActiveUsersRESTClient();
			addresses.clear();
			addresses.addAll(auClient.getActiveUserAddedAddresses(chosenUserID.toString()));
			auClient.close();
		}
		return addresses;
	}

	public Integer getChosenUserID()
	{
		return chosenUserID;
	}

	public Meteo getMeteo()
	{
		if (chosenAddress == null || chosenAddress.isEmpty())
			return null;
		
		MeteoWSClient meteoClient = new MeteoWSClient();
		return meteoClient.getLatestMeteoData(chosenAddress, webServiceUsername, webServicePassword);
	}
	
	public void setChosenUserID(Integer chosenUserID)
	{
		this.chosenUserID = chosenUserID;
	}

	public String getChosenAddress()
	{
		return chosenAddress;
	}

	public void setChosenAddress(String chosenAddress)
	{
		this.chosenAddress = chosenAddress;
	}

	public boolean isShowingAddresses()
	{
		return showingAddresses;
	}

	public boolean isShowingMeteo()
	{
		return showingMeteo;
	}
	
	public void reload()
	{
		reload = true;
	}
	
	public void toggleAddresses()
	{
		showingAddresses = !showingAddresses && chosenUserID != null;
	}
	
	public void toggleMeteo()
	{
		showingMeteo = !showingMeteo;
		System.out.println(showingMeteo);
	}
}
