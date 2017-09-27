package org.foi.nwtis.dfilipov.web.rest.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.database.DatabaseConnector;
import org.foi.nwtis.dfilipov.database.DatabaseInfo;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;

@Path("weather")
public class WeatherREST
{
	private final DatabaseInfo dbInfo;
	
	@Context
	private UriInfo context;

	public WeatherREST()
	{
		ExtendedConfigManager config
			= (ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		
		dbInfo = new DatabaseInfo
		(
			config.getDatabaseServer(),
			config.getDatabaseName(),
			config.getDatabaseUser(),
			config.getDatabasePass(),
			config.getDatabaseDriver()
		);
	}

	@Path("addresses")
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public String getAddresses(@QueryParam("username") String username, @QueryParam("password") String password)
	{
		JsonObjectBuilder jsonMain = Json.createObjectBuilder();
		try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
		{
			if (userExistsInDatabase(username, password, dbConnector))
			{
				try (ResultSet result = dbConnector.select("SELECT * FROM address"))
				{
					JsonArrayBuilder jsonArray = Json.createArrayBuilder();
					while (result.next())
					{
						JsonObjectBuilder jsonAddress = Json.createObjectBuilder();
						jsonAddress.add("id", result.getLong("id"));
						jsonAddress.add("address", result.getString("address"));
						jsonAddress.add("latitude", result.getString("latitude"));
						jsonAddress.add("longitude", result.getString("longitude"));
						jsonArray.add(jsonAddress);
					}
					
					jsonMain.add("addresses", jsonArray.build());
					jsonMain.add("status", "OK");
				}
			}
			else
				jsonMain.add("status", "ERROR");
		}
		catch (SQLException | ClassNotFoundException ex) 
		{
			System.out.println("Cannot create DatabaseConnector instance for REST operation getAddresses: " + ex.getMessage());
			jsonMain.add("status", "ERROR");
		}
		
		return jsonMain.build().toString();
	}
	
	@Path("forecast/latest")
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public String getLatestForecast(@QueryParam("addressID") long addressID, @QueryParam("username") String username, @QueryParam("password") String password)
	{
		JsonObjectBuilder jsonMain = Json.createObjectBuilder();
		try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
		{
			if (userExistsInDatabase(username, password, dbConnector) && addressID != 0)
			{
				String sql = String.format(
					"SELECT m.* FROM meteo_data m, (SELECT last_download FROM meteo_data WHERE address=%d AND forecast=1 ORDER BY id DESC LIMIT 1) AS ld WHERE m.last_download=ld.last_download AND m.address=%d",
					addressID,
					addressID
				);
				try (ResultSet result = dbConnector.select(sql))
				{
					JsonArrayBuilder jsonArray = Json.createArrayBuilder();
					while (result.next())
					{
						JsonObjectBuilder jsonMeteo = Json.createObjectBuilder();
						jsonMeteo.add("temperature", result.getString("temperature"));
						jsonMeteo.add("pressure", result.getString("pressure"));
						jsonMeteo.add("humidity", result.getString("humidity"));
						jsonMeteo.add("windSpeed", result.getString("wind_speed"));
						jsonMeteo.add("weather", result.getString("weather"));
						jsonMeteo.add("dateTime", result.getString("date_and_time"));
						jsonMeteo.add("lastDownload", result.getString("last_download"));
						jsonArray.add(jsonMeteo);
					}
					
					jsonMain.add("forecast", jsonArray.build());
					jsonMain.add("status", "OK");
				}
			}
			else
				jsonMain.add("status", "ERROR");
		}
		catch (SQLException | ClassNotFoundException ex) 
		{
			System.out.println("Cannot create DatabaseConnector instance for REST operation getLatestForecast: " + ex.getMessage());
			jsonMain.add("status", "ERROR");
		}
		
		return jsonMain.build().toString();
	}
	
	@Path("forecast/fromDate")
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public String getForecastsFromDate(@QueryParam("addressID") long addressID, @QueryParam("date") String date, @QueryParam("username") String username, @QueryParam("password") String password)
	{
		JsonObjectBuilder jsonMain = Json.createObjectBuilder();
		SimpleDateFormat justDate = new SimpleDateFormat("dd.MM.yyyy.");
		try
		{
			justDate.parse(date);
			try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
			{
				if (userExistsInDatabase(username, password, dbConnector) && addressID != 0)
				{
					String sql = String.format(
						"SELECT * FROM meteo_data WHERE address=%d AND forecast=1 AND date_and_time BETWEEN STR_TO_DATE('%s 00:00:00', '%%d.%%m.%%Y. %%H:%%i:%%s') AND STR_TO_DATE('%s 23:59:59', '%%d.%%m.%%Y. %%H:%%i:%%s')",
						addressID,
						date,
						date
					);
					try (ResultSet result = dbConnector.select(sql))
					{
						JsonArrayBuilder jsonArray = Json.createArrayBuilder();
						while (result.next())
						{
							JsonObjectBuilder jsonMeteo = Json.createObjectBuilder();
							jsonMeteo.add("temperature", result.getString("temperature"));
							jsonMeteo.add("pressure", result.getString("pressure"));
							jsonMeteo.add("humidity", result.getString("humidity"));
							jsonMeteo.add("windSpeed", result.getString("wind_speed"));
							jsonMeteo.add("weather", result.getString("weather"));
							jsonMeteo.add("dateTime", result.getString("date_and_time"));
							jsonMeteo.add("lastDownload", result.getString("last_download"));
							jsonArray.add(jsonMeteo);
						}

						jsonMain.add("forecast", jsonArray.build());
						jsonMain.add("status", "OK");
					}
				}
				else
					jsonMain.add("status", "ERROR");
			}
			catch (SQLException | ClassNotFoundException ex) 
			{
				System.out.println("Cannot create DatabaseConnector instance for REST operation getForecastsFromDate: " + ex.getMessage());
				jsonMain.add("status", "ERROR");
			}
		}
		catch (ParseException | NullPointerException ex) 
		{
			System.out.println("Unable to parse date (getForecastsFromDate): " + ex.getMessage());
			jsonMain.add("status", "ERROR");
		}
		
		return jsonMain.build().toString();
	}
	
	private boolean userExistsInDatabase(String username, String password, DatabaseConnector dbConnector) throws SQLException
	{
		String sql = String.format("SELECT * FROM user WHERE username='%s' AND password='%s'", username, password);
		boolean userExists;
		try (ResultSet result = dbConnector.select(sql)) 
		{
			userExists = result.next();
		}
		return userExists;
	}
}
