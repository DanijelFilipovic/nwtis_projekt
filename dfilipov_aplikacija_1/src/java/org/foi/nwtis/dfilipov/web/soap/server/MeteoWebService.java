package org.foi.nwtis.dfilipov.web.soap.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.database.DatabaseConnector;
import org.foi.nwtis.dfilipov.database.DatabaseInfo;
import org.foi.nwtis.dfilipov.actionLogger.ActionLogger;
import org.foi.nwtis.dfilipov.web.data.Address;
import org.foi.nwtis.dfilipov.web.data.Meteo;
import org.foi.nwtis.dfilipov.web.data.AddressCount;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;

@WebService(serviceName = "MeteoWebService")
public class MeteoWebService
{
	private final SimpleDateFormat dateWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat justDate = new SimpleDateFormat("dd.mm.YYYY.");
	
	@WebMethod(operationName = "getLatestMeteoData")
	public Meteo getLatestMeteoData(@WebParam(name = "address") String address, @WebParam(name = "username") String username, @WebParam(name = "password") String password)
	{
		boolean status = false;
		
		Meteo meteo = null;
		Address meteoAddress = new Address();
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(getDatabaseInfo()))
		{
			if (userExistsInDatabase(username, password, dbConnector))
			{
				String sqlMeteo = String.format(
					"SELECT m.* FROM meteo_data m, address a WHERE m.address=a.id AND a.address='%s' AND m.forecast=0 ORDER BY id DESC",
					address
				);
				try (ResultSet resultMeteo = dbConnector.select(sqlMeteo)) 
				{
					if (resultMeteo.next()) 
					{
						meteo = new Meteo();
						meteo.setTemperature(resultMeteo.getString("temperature"));
						meteo.setPressure(resultMeteo.getString("pressure"));
						meteo.setHumidity(resultMeteo.getString("humidity"));
						meteo.setWindSpeed(resultMeteo.getString("wind_speed"));
						meteo.setWeather(resultMeteo.getString("weather"));
						meteo.setLastUpdate(dateWithTime.parse(resultMeteo.getString("date_and_time")));
						meteo.setLastDownload(dateWithTime.parse(resultMeteo.getString("last_download")));
						
						meteoAddress.setID(resultMeteo.getLong("address"));
						meteoAddress.setAddress(address);
						
						String sqlAddress = String.format("SELECT * FROM address WHERE id=%d", meteoAddress.getID());
						try (ResultSet resultAddress = dbConnector.select(sqlAddress)) {
							if (resultAddress.next())
							{
								meteoAddress.setLatitude(resultAddress.getString("latitude"));
								meteoAddress.setLongitude(resultAddress.getString("longitude"));
								meteo.setAddress(meteoAddress);
							}
						}
						status = true;
					}
				}
			}
		}
		catch (ClassNotFoundException | SQLException ex) 
		{
			System.out.println("Cannot create DatabaseConnector instance for operation getLatestMeteoData: " + ex.getMessage());
		}
		catch (ParseException ex) 
		{
			System.out.println("Unable to parse date (getLatestMeteoData): " + ex.getMessage());
		}
		
		ActionLogger logger = new ActionLogger();
		logger.setUsername(username);
		logger.setPassword(password);
		logger.setRequestMethod("getLatestMeteoData");
		logger.setRequestParams(String.format("username=%s&password=%s&address=%s", username, password, address));
		logger.setStatus(status);
		logger.setWebServiceType("SOAP");
		logger.log();
		
		return meteo;
	}
	
	@WebMethod(operationName = "getAddedAddresses")
	public List<Address> getAddedAddresses(@WebParam(name = "username") String username, @WebParam(name = "password") String password)
	{
		boolean status = false;
		
		List<Address> addedAddresses = new ArrayList<>();
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(getDatabaseInfo()))
		{
			if (userExistsInDatabase(username, password, dbConnector))
			{
				String sql = String.format(
					"SELECT a.* FROM address a, user u WHERE a.user=u.id AND u.username='%s' AND u.password='%s'",
					username,
					password
				);
				
				try (ResultSet result = dbConnector.select(sql)) 
				{
					while (result.next())
					{
						Address address = new Address();
						address.setID(result.getLong("id"));
						address.setAddress(result.getString("address"));
						address.setLatitude(result.getString("latitude"));
						address.setLongitude(result.getString("longitude"));
						addedAddresses.add(address);
						status = true;
					}
				}
			}
		}
		catch (ClassNotFoundException | SQLException ex) 
		{
			System.out.println("Cannot create DatabaseConnector instance for operation getAddedAddresses: " + ex.getMessage());
		}
		
		ActionLogger logger = new ActionLogger();
		logger.setUsername(username);
		logger.setPassword(password);
		logger.setRequestMethod("getAddedAddresses");
		logger.setRequestParams(String.format("username=%s&password=%s", username, password));
		logger.setStatus(status);
		logger.setWebServiceType("SOAP");
		logger.log();
		
		return addedAddresses;
	}
	
	@WebMethod(operationName = "getRankedAddresses")
	public List<AddressCount> getRankedAddresses(@WebParam(name = "n") int n, @WebParam(name = "username") String username, @WebParam(name = "password") String password)
	{
		boolean status = false;
		
		List<AddressCount> addressCountList = new ArrayList<>();
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(getDatabaseInfo()))
		{
			if (userExistsInDatabase(username, password, dbConnector))
			{
				String sql = String.format(
					"SELECT * FROM (SELECT a.*, count(m.address) as n_address FROM address a, meteo_data m WHERE m.address=a.id AND forecast=0 GROUP BY address ORDER BY n_address DESC, address ASC) AS temp LIMIT %d",
					n
				);
				
				try (ResultSet result = dbConnector.select(sql)) 
				{
					while (result.next())
					{
						Address address = new Address();
						address.setID(result.getLong("id"));
						address.setAddress(result.getString("address"));
						address.setLatitude(result.getString("latitude"));
						address.setLongitude(result.getString("longitude"));
						int count = result.getInt("n_address");
						
						addressCountList.add(new AddressCount(count, address));
						status = true;
					}
				}
			}
		}
		catch (ClassNotFoundException | SQLException ex) 
		{
			System.out.println("Cannot create DatabaseConnector instance for operation getRankedAddresses: " + ex.getMessage());
		}
		
		ActionLogger logger = new ActionLogger();
		logger.setUsername(username);
		logger.setPassword(password);
		logger.setRequestMethod("getRankedAddresses");
		logger.setRequestParams(String.format("username=%s&password=%s&n=%d", username, password, n));
		logger.setStatus(status);
		logger.setWebServiceType("SOAP");
		logger.log();
		
		return addressCountList;
	}
	
	@WebMethod(operationName = "getLastNMeteoData")
	public List<Meteo> getLastNMeteoData(@WebParam(name = "address") String address, @WebParam(name = "n") int n, @WebParam(name = "username") String username, @WebParam(name = "password") String password)
	{
		boolean status = false;
		
		List<Meteo> meteoList = new ArrayList<>();
		Address meteoAddress = new Address();
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(getDatabaseInfo()))
		{
			if (userExistsInDatabase(username, password, dbConnector))
			{
				String sqlMeteo = String.format(
					"SELECT m.* FROM meteo_data m, address a WHERE m.address=a.id AND a.address='%s' AND m.forecast=0 ORDER BY id DESC LIMIT %d",
					address,
					n
				);
				
				try (ResultSet resultMeteo = dbConnector.select(sqlMeteo))
				{
					while (resultMeteo.next())
					{
						Meteo meteo = new Meteo();
						meteo.setTemperature(resultMeteo.getString("temperature"));
						meteo.setPressure(resultMeteo.getString("pressure"));
						meteo.setHumidity(resultMeteo.getString("humidity"));
						meteo.setWindSpeed(resultMeteo.getString("wind_speed"));
						meteo.setWeather(resultMeteo.getString("weather"));
						meteo.setLastUpdate(dateWithTime.parse(resultMeteo.getString("date_and_time")));
						meteo.setLastDownload(dateWithTime.parse(resultMeteo.getString("last_download")));
						
						meteoList.add(meteo);
						status = true;
					}
					
					String sqlAddress = String.format("SELECT * FROM address WHERE address='%s'", address);
					try (ResultSet resultAddress = dbConnector.select(sqlAddress)) 
					{
						if (resultAddress.next()) 
						{
							meteoAddress.setAddress(address);
							meteoAddress.setID(resultAddress.getLong("id"));
							meteoAddress.setLatitude(resultAddress.getString("latitude"));
							meteoAddress.setLongitude(resultAddress.getString("longitude"));
						}
					}
					
					for (Meteo m : meteoList)
						m.setAddress(meteoAddress);
				}
			}
		}
		catch (ClassNotFoundException | SQLException ex) 
		{
			System.out.println("Cannot create DatabaseConnector instance for operation getLastNMeteoData: " + ex.getMessage());
		}
		catch (ParseException ex) 
		{
			System.out.println("Unable to parse date (getLastNMeteoData): " + ex.getMessage());
		}
		
		meteoList.sort((m1, m2) -> m1.getLastDownload().compareTo(m2.getLastDownload()));
		
		ActionLogger logger = new ActionLogger();
		logger.setUsername(username);
		logger.setPassword(password);
		logger.setRequestMethod("getLastNMeteoData");
		logger.setRequestParams(String.format("username=%s&password=%s&n=%d&address=%s", username, password, n, address));
		logger.setStatus(status);
		logger.setWebServiceType("SOAP");
		logger.log();
		
		return meteoList;
	}
	
	@WebMethod(operationName = "getMeteoDataFromDateRange")
	public List<Meteo> getMeteoDataFromDateRange(@WebParam(name = "address") String address, @WebParam(name = "beginDate") String beginDate, @WebParam(name = "endDate") String endDate, @WebParam(name = "username") String username, @WebParam(name = "password") String password)
	{
		boolean status = false;
		
		List<Meteo> meteoList = new ArrayList<>();
		Address meteoAddress = new Address();
		
		try 
		{
			justDate.parse(beginDate);
			justDate.parse(endDate);

			try (DatabaseConnector dbConnector = new DatabaseConnector(getDatabaseInfo())) 
			{
				if (userExistsInDatabase(username, password, dbConnector)) 
				{
					String sqlMeteo = String.format("SELECT m.* FROM meteo_data m, address a WHERE m.address=a.id AND a.address='%s' AND forecast=0 AND last_download BETWEEN STR_TO_DATE('%s', '%%d.%%m.%%Y.') AND STR_TO_DATE('%s', '%%d.%%m.%%Y.')",
						address,
						beginDate,
						endDate
					);
					
					System.out.println(sqlMeteo);

					try (ResultSet resultMeteo = dbConnector.select(sqlMeteo)) 
					{
						while (resultMeteo.next()) 
						{
							Meteo meteo = new Meteo();
							meteo.setTemperature(resultMeteo.getString("temperature"));
							meteo.setPressure(resultMeteo.getString("pressure"));
							meteo.setHumidity(resultMeteo.getString("humidity"));
							meteo.setWindSpeed(resultMeteo.getString("wind_speed"));
							meteo.setWeather(resultMeteo.getString("weather"));
							meteo.setLastUpdate(dateWithTime.parse(resultMeteo.getString("date_and_time")));
							meteo.setLastDownload(dateWithTime.parse(resultMeteo.getString("last_download")));

							meteoList.add(meteo);
							status = true;
						}

						String sqlAddress = String.format("SELECT * FROM address WHERE address='%s'", address);
						try (ResultSet resultAddress = dbConnector.select(sqlAddress)) 
						{
							if (resultAddress.next()) 
							{
								meteoAddress.setAddress(address);
								meteoAddress.setID(resultAddress.getLong("id"));
								meteoAddress.setLatitude(resultAddress.getString("latitude"));
								meteoAddress.setLongitude(resultAddress.getString("longitude"));
							}
						}

						for (Meteo m : meteoList)
							m.setAddress(meteoAddress);
					}
				}
			}
			catch (ClassNotFoundException | SQLException ex) 
			{
				System.out.println("Cannot create DatabaseConnector instance for operation getLastNMeteoData: " + ex.getMessage());
			}
		}
		catch (ParseException ex) 
		{
			System.out.println("Unable to parse date (getMeteoDataFromDateRange): " + ex.getMessage());
		}
		
		ActionLogger logger = new ActionLogger();
		logger.setUsername(username);
		logger.setPassword(password);
		logger.setRequestMethod("getMeteoDataFromDateRange");
		logger.setRequestParams(String.format("username=%s&password=%s&address=%s&beginDate=%s&endDate=%s", username, password, address, beginDate, endDate));
		logger.setStatus(status);
		logger.setWebServiceType("SOAP");
		logger.log();
		
		return meteoList;
	}
	
	private DatabaseInfo getDatabaseInfo()
	{
		ExtendedConfigManager config
			= (ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");

		return new DatabaseInfo
		(
			config.getDatabaseServer(),
			config.getDatabaseName(),
			config.getDatabaseUser(),
			config.getDatabasePass(),
			config.getDatabaseDriver()
		);
	}
	
	private boolean userExistsInDatabase(String username, String password, DatabaseConnector dbConnector) throws SQLException
	{
		String sql = String.format("SELECT * FROM user WHERE username='%s' AND password='%s'", username, password);
		boolean userExists;
		try (ResultSet result = dbConnector.select(sql)) {
			userExists = result.next();
		}
		return userExists;
	}
}
