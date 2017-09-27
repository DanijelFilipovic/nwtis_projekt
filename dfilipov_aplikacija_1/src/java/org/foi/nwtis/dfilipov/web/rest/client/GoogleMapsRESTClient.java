package org.foi.nwtis.dfilipov.web.rest.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.foi.nwtis.dfilipov.web.data.Address;

public class GoogleMapsRESTClient
{
	private static final String BASE_URL = "https://maps.google.com/maps/api/geocode/json?sensor=false";
	
	public GoogleMapsRESTClient() {}
	
	public Address getAddressData(String addressString)
	{
		Address address = null;
		
		try {
			URL url = new URL(String.format("%s&address=%s", BASE_URL, URLEncoder.encode(addressString, "UTF-8")));
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			
			InputStream response = conn.getInputStream();
			StringBuilder jsonResponse = new StringBuilder();
			int _byte;
			while ((_byte = response.read()) != -1)
			{
				jsonResponse.append((char)_byte);
			}
			
			JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse.toString()));
			JsonObject main = jsonReader.readObject();
			
			if (main.getString("status").equals("OK"))
			{
				JsonObject location = main.getJsonArray("results").getJsonObject(0).getJsonObject("geometry").getJsonObject("location");
				String latitude = location.getJsonNumber("lat").toString();
				String longitude = location.getJsonNumber("lng").toString();
				address = new Address(addressString, latitude, longitude);
			}
			
			jsonReader.close();
		}
		catch (IOException ex) 
		{
			System.out.println("Cannot access Google Maps API: " + ex.getMessage());
		}
		
		return address;
	}
}
