package org.foi.nwtis.dfilipov.web.beans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.ws.rs.ClientErrorException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.foi.nwtis.dfilipov.web.entities.Users;
import org.foi.nwtis.dfilipov.web.rest.client.WeatherRESTClient;
import org.foi.nwtis.dfilipov.web.soap.client.Address;
import org.foi.nwtis.dfilipov.web.soap.client.Meteo;
import org.foi.nwtis.dfilipov.web.soap.client.MeteoWSClient;
import org.foi.nwtis.dfilipov.web.stateless.MailmanBean;

@Named(value = "addressesBean")
@SessionScoped
public class AddressesBean implements Serializable
{
	@EJB
	private MailmanBean mailmanBean;
	
	private final List<Address> addresses;
	private final List<Address> ownAddresses;
	private final List<Meteo> latestMeteoData;
	private final List<org.foi.nwtis.dfilipov.web.data.Meteo> latestForecast;
	
	private long[] chosenAddressIDs;
	
	private boolean reload = true;
	
	private boolean showingOwnAddresses = false;
	private boolean showingMeteoData = false;
	private boolean showingLatestForecast = false;
	
	private String newAddress;
	
	public AddressesBean() 
	{
		addresses = new ArrayList<>();
		ownAddresses = new ArrayList<>();
		latestMeteoData = new ArrayList<>();
		latestForecast = new ArrayList<>();
	}
	
	public List<Address> getAddresses()
	{
		if (reload)
		{
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			if (context.getSession(false) != null)
			{
				Users loggedUser = (Users) context.getSessionMap().get("USER");
				WeatherRESTClient wrc = new WeatherRESTClient();
				addresses.clear();
				addresses.addAll(wrc.getAddresses(loggedUser.getPassword(), loggedUser.getUsername()));
				wrc.close();
			}
			reload = false;
		}
		return addresses;
	}

	public boolean isShowingOwnAddresses()
	{
		return showingOwnAddresses;
	}

	public boolean isShowingMeteoData()
	{
		return showingMeteoData;
	}

	public boolean isShowingLatestForecast()
	{
		return showingLatestForecast;
	}

	public List<Address> getOwnAddresses()
	{
		return ownAddresses;
	}

	public List<Meteo> getLatestMeteoData()
	{
		return latestMeteoData;
	}

	public List<org.foi.nwtis.dfilipov.web.data.Meteo> getLatestForecast()
	{
		return latestForecast;
	}

	public long[] getChosenAddressIDs()
	{
		return chosenAddressIDs;
	}

	public void setChosenAddressIDs(long[] chosenAddressIDs)
	{
		this.chosenAddressIDs = chosenAddressIDs;
	}

	public String getNewAddress()
	{
		return newAddress;
	}

	public void setNewAddress(String newAddress)
	{
		this.newAddress = newAddress;
	}
	
	public void reload()
	{
		reload = true;
	}
	
	public void addNewMessage(Users user)
	{
		mailmanBean.sendNewAddress(user.getUsername(), user.getPassword(), newAddress);
	}
	
	public void toggleShowOwnAddresses(Users user)
	{
		if (!showingOwnAddresses)
		{
			MeteoWSClient meteoClient = new MeteoWSClient();
			ownAddresses.clear();
			ownAddresses.addAll(meteoClient.getAddedAddresses(user.getUsername(), user.getPassword()));
			showingOwnAddresses = true;
		}
		else
			showingOwnAddresses = false;
	}
	
	public void toggleShowMeteoData(Users user)
	{
		if (!showingMeteoData)
		{
			latestMeteoData.clear();
			MeteoWSClient meteoClient = new MeteoWSClient();
			for (long id : chosenAddressIDs)
			{
				Address address = findAddressByID(id);
				Meteo meteo = meteoClient.getLatestMeteoData(address.getAddress(), user.getUsername(), user.getPassword());
				latestMeteoData.add(meteo);
			}
			showingMeteoData = true;
		}
		else
			showingMeteoData = false;
	}
	
	public void toggleShowForecast(Users user)
	{
		if (chosenAddressIDs.length == 1)
		{
			if (!showingLatestForecast)
			{
				latestForecast.clear();
				WeatherRESTClient weatherClient = new WeatherRESTClient();
				try 
				{
					latestForecast.addAll(
						weatherClient.getLatestForecast(
							user.getPassword(), 
							String.valueOf(chosenAddressIDs[0]), 
							user.getUsername()
						)
					);
				}
				catch (ClientErrorException | ParseException | DatatypeConfigurationException ex) 
				{
					System.out.println("Error retrieving weather forecast: " + ex.getMessage());
				}
				weatherClient.close();
				showingLatestForecast = true;
			}
			else
				showingLatestForecast = false;
		}
	}
	
	private Address findAddressByID(long id)
	{
		boolean found = false;
		Address address = null;
		for (int i = 0; i < addresses.size() && !found; i++)
		{
			if (addresses.get(i).getID() == id)
			{
				address = addresses.get(i);
				found = true;
			}
		}
		return address;
	}
}
