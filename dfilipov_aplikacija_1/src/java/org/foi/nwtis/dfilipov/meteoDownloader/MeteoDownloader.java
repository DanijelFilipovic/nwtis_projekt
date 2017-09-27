package org.foi.nwtis.dfilipov.meteoDownloader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.database.DatabaseConnector;
import org.foi.nwtis.dfilipov.database.DatabaseInfo;
import org.foi.nwtis.dfilipov.web.data.Address;
import org.foi.nwtis.dfilipov.web.data.Meteo;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;
import org.foi.nwtis.dfilipov.web.rest.client.OpenWeatherMapsRESTClient;

public class MeteoDownloader extends Thread
{
	private final AppListener appListener;
	
	private final int interval;
	private final String apiKey;
	private final SimpleDateFormat sdf;
	private DatabaseConnector dbConnector;
	
	private boolean running = true;
	private boolean downloadingMeteoData = true;
	
	private final String SQL_INSERT_METEO = 
		"INSERT INTO meteo_data VALUES (DEFAULT, %d, %s, %s, %s, %s, '%s', STR_TO_DATE('%s', '%%d.%%m.%%Y. %%H:%%i:%%s'), STR_TO_DATE('%s', '%%d.%%m.%%Y. %%H:%%i:%%s'), 0)";
	
	private final String SQL_INSERT_WEATHER_BASE = "INSERT INTO meteo_data VALUES ";
	private final String SQL_INSERT_WEATHER_VALUES = 
		"(DEFAULT, %d, %s, %s, %s, %s, '%s', STR_TO_DATE('%s', '%%d.%%m.%%Y. %%H:%%i:%%s'), STR_TO_DATE('%s', '%%d.%%m.%%Y. %%H:%%i:%%s'), 1)";
	
	public MeteoDownloader(AppListener appListener)
	{
		super("MeteoDownloader");
		this.appListener = appListener;
		
		ExtendedConfigManager config = 
			(ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		
		interval = Integer.parseInt(config.getOWMDownloadInterval());
		apiKey = config.getOWMApiKey();
		sdf = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
		
		DatabaseInfo dbInfo = new DatabaseInfo(
			config.getDatabaseServer(),
			config.getDatabaseName(),
			config.getDatabaseUser(),
			config.getDatabasePass(),
			config.getDatabaseDriver()
		);
		
		try {
			dbConnector = new DatabaseConnector(dbInfo);
		}
		catch (ClassNotFoundException | SQLException ex) 
		{
			System.out.println("Cannot create DatabaseConnector instance: " + ex.getMessage());
			running = false;
		}
	}
	
	@Override
	public void run()
	{
		long operationLength = 0;
		while (running)
		{
			try 
			{
				sleep(Math.abs((interval * 1000) - operationLength));
				if (downloadingMeteoData)
				{
					operationLength = System.currentTimeMillis();
					downloadLatestMeteoData();
					downloadWeatherForecast();
					operationLength = System.currentTimeMillis() - operationLength;
				}
			}
			catch (InterruptedException ex) 
			{
				running = false;
			}
		}
	}

	private void downloadLatestMeteoData() throws InterruptedException
	{
		try (ResultSet result = dbConnector.select("SELECT * FROM address");)
		{
			List<Address> addresses = new ArrayList<>();
			while (result.next())
			{
				Address address = new Address(
					result.getLong("id"),
					result.getString("address"),
					result.getString("latitude"),
					result.getString("longitude")
				);
				addresses.add(address);
			}
			result.close();
				
			OpenWeatherMapsRESTClient owmClient = new OpenWeatherMapsRESTClient(apiKey);
			
			for (Address a : addresses) 
			{
				Meteo latestMeteoData = owmClient.getMeteoData(a.getLatitude(), a.getLongitude());
				
				if (latestMeteoData != null)
				{
					String sqlInsert = String.format(
						SQL_INSERT_METEO,
						a.getID(),
						latestMeteoData.getTemperature(),
						latestMeteoData.getPressure(),
						latestMeteoData.getHumidity(),
						latestMeteoData.getWindSpeed(),
						latestMeteoData.getWeather(),
						sdf.format(latestMeteoData.getLastUpdate()),
						sdf.format(latestMeteoData.getLastDownload())
					);

					dbConnector.update(sqlInsert);
					System.out.println("Stored latest meteo data.");
					sleep(500);
				}
			}
		}
		catch (SQLException ex) 
		{
			System.out.println("Unable to operate with database (meteo): " + ex.getMessage());
		}
	}

	private void downloadWeatherForecast() throws InterruptedException
	{
		try (ResultSet result = dbConnector.select("SELECT * FROM address");)
		{
			List<Address> addresses = new ArrayList<>();
			while (result.next())
			{
				Address address = new Address(
					result.getLong("id"),
					result.getString("address"),
					result.getString("latitude"),
					result.getString("longitude")
				);
				addresses.add(address);
			}
			result.close();
				
			OpenWeatherMapsRESTClient owmClient = new OpenWeatherMapsRESTClient(apiKey);
			
			for (Address a : addresses) 
			{
				try
				{
					List<Meteo> weatherForecast = owmClient.getWeatherForecast(a.getLatitude(), a.getLongitude());
					StringBuilder sqlBuilder = new StringBuilder();

					sqlBuilder.append(SQL_INSERT_WEATHER_BASE);
					String separator = "";
					for (Meteo m : weatherForecast) 
					{
						sqlBuilder.append(separator);
						sqlBuilder.append(String.format(
							SQL_INSERT_WEATHER_VALUES,
							a.getID(),
							m.getTemperature(),
							m.getPressure(),
							m.getHumidity(),
							m.getWindSpeed(),
							m.getWeather(),
							sdf.format(m.getLastUpdate()),
							sdf.format(m.getLastDownload())
						));
						separator = ", ";
					}

					dbConnector.update(sqlBuilder.toString());
					System.out.println("Stored latest weather forecast.");
					sleep(500);
				}
				catch (SQLException ex)
				{
					System.out.println("Unable to store latest weather forecast: " + ex.getMessage());
				}
			}
		}
		catch (SQLException ex) 
		{
			System.out.println("Unable to operate with database (forecast): " + ex.getMessage());
		}
	}

	public boolean isRunning()
	{
		return running;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	public boolean isDownloadingMeteoData()
	{
		return downloadingMeteoData;
	}

	public void setDownloadingMeteoData(boolean downloadingMeteoData)
	{
		this.downloadingMeteoData = downloadingMeteoData;
	}
}
