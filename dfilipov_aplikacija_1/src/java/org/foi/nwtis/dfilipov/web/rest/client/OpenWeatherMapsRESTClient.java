package org.foi.nwtis.dfilipov.web.rest.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.foi.nwtis.dfilipov.web.data.Meteo;

public class OpenWeatherMapsRESTClient
{
	private static final String URL_TEMPLATE = 
		"http://api.openweathermap.org/data/2.5/%s?lat=%s&lon=%s&units=metric&lang=hr&APIKEY=%s";

	private final String apiKey;
	
	public OpenWeatherMapsRESTClient(String apiKey)
	{
		this.apiKey = apiKey;
	}
	
	public Meteo getMeteoData(String latitude, String longitude)
	{
		Meteo meteo = null;
		
		try {
			URL url = new URL(String.format(URL_TEMPLATE, "weather", latitude, longitude, apiKey));
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
			
			if (main.getJsonNumber("cod").intValue() == 200)
			{
				meteo = new Meteo();
				meteo.setTemperature(main.getJsonObject("main").getJsonNumber("temp").toString());
				meteo.setPressure(main.getJsonObject("main").getJsonNumber("pressure").toString());
				meteo.setHumidity(main.getJsonObject("main").getJsonNumber("humidity").toString());
				meteo.setWeather(main.getJsonArray("weather").getJsonObject(0).getString("description"));
				meteo.setWindSpeed(main.getJsonObject("wind").getJsonNumber("speed").toString());
				meteo.setLastUpdate(new Date(main.getJsonNumber("dt").longValue() * 1000L));
				meteo.setLastDownload(new Date());
			}

			jsonReader.close();
		}
		catch (Exception ex) 
		{
			System.out.println("Cannot access OpenWeatherMaps API: " + ex.getMessage());
		}
		
		return meteo;
	}
	
	public List<Meteo> getWeatherForecast(String latitude, String longitude)
	{
		List<Meteo> weatherForecast = new ArrayList<>();
		
		try {
			URL url = new URL(String.format(URL_TEMPLATE, "forecast", latitude, longitude, apiKey));
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			
			InputStream response = conn.getInputStream();
			StringBuilder jsonResponse = new StringBuilder();
			int _byte;
			while ((_byte = response.read()) != -1)
			{
				jsonResponse.append((char)_byte);
			}
			
			Date downloadTime = new Date();
			
			JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse.toString()));
			JsonObject main = jsonReader.readObject();
			
			if (main.getString("cod").equals("200"))
			{
				JsonArray forecastList = main.getJsonArray("list");
				for (int i = 0; i < forecastList.size(); i++)
				{
					JsonObject forecast = forecastList.getJsonObject(i);
					
					Meteo meteo = new Meteo();
					meteo.setTemperature(forecast.getJsonObject("main").getJsonNumber("temp").toString());
					meteo.setPressure(forecast.getJsonObject("main").getJsonNumber("pressure").toString());
					meteo.setHumidity(forecast.getJsonObject("main").getJsonNumber("humidity").toString());
					meteo.setWeather(forecast.getJsonArray("weather").getJsonObject(0).getString("description"));
					meteo.setWindSpeed(forecast.getJsonObject("wind").getJsonNumber("speed").toString());
					meteo.setLastUpdate(new Date(forecast.getJsonNumber("dt").longValue() * 1000L));
					meteo.setLastDownload(downloadTime);
					
					weatherForecast.add(meteo);
				}
			}
			
			jsonReader.close();
		}
		catch (IOException ex) 
		{
			System.out.println("Cannot access OpenWeatherMaps API: " + ex.getMessage());
		}
		
		return weatherForecast;
	}
}
