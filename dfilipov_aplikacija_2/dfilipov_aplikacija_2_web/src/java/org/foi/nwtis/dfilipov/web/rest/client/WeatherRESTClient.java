package org.foi.nwtis.dfilipov.web.rest.client;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.foi.nwtis.dfilipov.web.data.Meteo;
import org.foi.nwtis.dfilipov.web.soap.client.Address;

public class WeatherRESTClient
{
	private WebTarget webTarget;
	private Client client;
	private static final String BASE_URI = "http://localhost:8084/dfilipov_aplikacija_1/REST";
	private SimpleDateFormat sdf;

	public WeatherRESTClient()
	{
		client = javax.ws.rs.client.ClientBuilder.newClient();
		webTarget = client.target(BASE_URI).path("weather");
		
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public List<Address> getAddresses(String password, String username) throws ClientErrorException
	{
		WebTarget resource = webTarget;
		if (password != null) {
			resource = resource.queryParam("password", password);
		}
		if (username != null) {
			resource = resource.queryParam("username", username);
		}
		resource = resource.path("addresses");
		String jsonString = resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
		
		List<Address> addresses = new ArrayList<>();
		
		try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString)))
		{
			JsonObject jsonMain = jsonReader.readObject();
			String status = jsonMain.getString("status");
			if (status.equals("OK"))
			{
				JsonArray jsonArray = jsonMain.getJsonArray("addresses");
				for (int i = 0; i < jsonArray.size(); i++)
				{
					JsonObject jsonAddress = jsonArray.getJsonObject(i);
					Address address = new Address();
					address.setID(jsonAddress.getInt("id"));
					address.setAddress(jsonAddress.getString("address"));
					address.setLatitude(jsonAddress.getString("latitude"));
					address.setLongitude(jsonAddress.getString("longitude"));
					addresses.add(address);
				}
			}
		}
		
		return addresses;
	}

	public List<Meteo> getLatestForecast(String password, String addressID, String username) 
	throws ClientErrorException, ParseException, DatatypeConfigurationException
	{
		WebTarget resource = webTarget;
		if (password != null) {
			resource = resource.queryParam("password", password);
		}
		if (addressID != null) {
			resource = resource.queryParam("addressID", addressID);
		}
		if (username != null) {
			resource = resource.queryParam("username", username);
		}
		resource = resource.path("forecast/latest");
		String jsonString = resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
		
		List<Meteo> forecast = new ArrayList<>();
		
		try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString)))
		{
			JsonObject jsonMain = jsonReader.readObject();
			String status = jsonMain.getString("status");
			if (status.equals("OK"))
			{
				JsonArray jsonArray = jsonMain.getJsonArray("forecast");
				for (int i = 0; i < jsonArray.size(); i++)
				{
					JsonObject jsonMeteo = jsonArray.getJsonObject(i);
					Meteo meteo = new Meteo();
					meteo.setTemperature(jsonMeteo.getString("temperature"));
					meteo.setPressure(jsonMeteo.getString("pressure"));
					meteo.setHumidity(jsonMeteo.getString("humidity"));
					meteo.setWindSpeed(jsonMeteo.getString("windSpeed"));
					meteo.setWeather(jsonMeteo.getString("weather"));
					
					meteo.setLastUpdate(sdf.parse(jsonMeteo.getString("dateTime")));
					meteo.setLastDownload(sdf.parse(jsonMeteo.getString("lastDownload")));
					
					forecast.add(meteo);
				}
			}
		}
		
		return forecast;
	}

	public String getForecastsFromDate(String date, String password, String addressID, String username) throws ClientErrorException
	{
		WebTarget resource = webTarget;
		if (date != null) {
			resource = resource.queryParam("date", date);
		}
		if (password != null) {
			resource = resource.queryParam("password", password);
		}
		if (addressID != null) {
			resource = resource.queryParam("addressID", addressID);
		}
		if (username != null) {
			resource = resource.queryParam("username", username);
		}
		resource = resource.path("forecast/fromDate");
		return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
	}

	public void close()
	{
		client.close();
	}
}
