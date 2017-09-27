package org.foi.nwtis.dfilipov.backgroundServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.database.DatabaseConnector;
import org.foi.nwtis.dfilipov.database.DatabaseInfo;
import org.foi.nwtis.dfilipov.actionLogger.ActionLogger;
import org.foi.nwtis.dfilipov.web.data.Address;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;
import org.foi.nwtis.dfilipov.web.rest.client.GoogleMapsRESTClient;

public class BackgroundRequestProcessor implements Runnable
{
	private final BackgroundServer backgroundServer;
	private final Socket client;
	private final DatabaseInfo dbInfo;
	
	private Date requestTime;
	
	public BackgroundRequestProcessor(BackgroundServer backgroundServer, Socket client)
	{
		this.backgroundServer = backgroundServer;
		this.client = client;
		
		ExtendedConfigManager config = 
			(ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		
		dbInfo = new DatabaseInfo
		(
			config.getDatabaseServer(),
			config.getDatabaseName(),
			config.getDatabaseUser(),
			config.getDatabasePass(),
			config.getDatabaseDriver()
		);
	}
	
	@Override
	public void run()
	{
		try (InputStreamReader is = new InputStreamReader(client.getInputStream(), "UTF-8"); OutputStream os = client.getOutputStream())
		{
			StringBuilder request = new StringBuilder();
			
			int _byte;
			while ((_byte = is.read()) != -1)
			{
				request.append((char)_byte);
			}
			
			requestTime = new Date();
			
			String response = processRequest(request.toString());
			os.write(response.getBytes());
		}
		catch (IOException ex) 
		{
			System.out.println("Unable to process request.");
		}
	}

	private synchronized String processRequest(String request)
	{
		String response;
		
		if (Pattern.matches(UserRequestData.REQUEST_PATTERN, request))
		{
			response = processUserRequest(request);
		}
		else if (Pattern.matches(AdminRequestData.REQUEST_PATTERN, request))
		{
			response = processAdminRequest(request);
		}
		else
		{
			response = "INVALID REQUEST";
		}
		
		return response;
	}
	
	private synchronized String processUserRequest(String request)
	{
		UserRequestData data = new UserRequestData(request);
		String response;
		
		if (userExistsInDatabase(data, false, true))
		{
			if (backgroundServer.isAcceptingUserCommands())
			{
				switch (data.getRequestType())
				{
					case UserRequestData.REQUEST_TYPE_TEST:
						response = processUserRequestTEST(data);
						break;

					case UserRequestData.REQUEST_TYPE_ADD:
						response = processUserRequestADD(data);
						break;

					default:
						response = "OK 10;";
				}
			}
			else
				response = "ERR 10;";
		}
		else
			response = "ERR 20;";
		
		ActionLogger logger = new ActionLogger();
		logger.setUsername(data.getUsername());
		logger.setPassword(data.getPassword());
		logger.setRequestMethod(request);
		logger.setRequestParams(" ");
		logger.setStatus(response.equals("OK 10;"));
		logger.setWebServiceType("USER COMMAND");
		logger.log();
		
		return response;
	}
	
	private synchronized String processAdminRequest(String request)
	{
		AdminRequestData data = new AdminRequestData(request);
		String response;
		
		if (userExistsInDatabase(data, true, true))
		{
			switch (data.getRequestType())
			{
				case AdminRequestData.REQUEST_TYPE_START:
					response = processAdminRequestSTART();
					break;
					
				case AdminRequestData.REQUEST_TYPE_PAUSE:
					response = processAdminRequestPAUSE();
					break;
					
				case AdminRequestData.REQUEST_TYPE_STOP:
					response = processAdminRequestSTOP();
					break;
					
				case AdminRequestData.REQUEST_TYPE_STATUS:
					response = processAdminRequestSTATUS();
					break;
					
				case AdminRequestData.REQUEST_TYPE_ADD:
					response = processAdminRequestADD(data);
					break;
					
				case AdminRequestData.REQUEST_TYPE_UP:
					response = processAdminRequestUPDOWN(data, true);
					break;
					
				case AdminRequestData.REQUEST_TYPE_DOWN:
					response = processAdminRequestUPDOWN(data, false);
					break;
				
				default:
					response = "OK 10;";
			}
		}
		else
		{
			response = "ERR 21;";
		}
		
		ActionLogger logger = new ActionLogger();
		logger.setUsername(data.getUsername());
		logger.setPassword(data.getPassword());
		logger.setRequestMethod(request);
		logger.setRequestParams(" ");
		logger.setStatus(response.equals("OK 10;"));
		logger.setWebServiceType("ADMIN COMMAND");
		logger.log();
		
		return response;
	}
	
	private synchronized String processUserRequestTEST(UserRequestData data)
	{
		String response;
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo)) 
		{
			String sql = String.format("SELECT * FROM address WHERE address='%s'", data.getAddress());
			try (ResultSet result = dbConnector.select(sql)) 
			{
				response = result.next() ? "OK 10;" : "ERR 42;";
			}
		}
		catch (ClassNotFoundException | SQLException ex) 
		{
			System.out.println("Unable to connect to the database (TEST): " + ex.getMessage());
			response = "ERROR (TEST): Unable to connect to the database";
		}
		
		return response;
	}
	
	private synchronized String processUserRequestADD(UserRequestData data)
	{
		String response;
		GoogleMapsRESTClient gmClient = new GoogleMapsRESTClient();
		Address newAddress = gmClient.getAddressData(data.getAddress());
		
		if (!addressExistsInDatabase(data.getAddress()))
		{
			try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
			{
				String sql2 = String.format
				(
					"INSERT INTO ADDRESS(address, latitude, longitude, user) VALUES ('%s', %s, %s, %d)",
					newAddress.getAddress(),
					newAddress.getLatitude(),
					newAddress.getLongitude(),
					data.getId()
				);

				int rowsAffected = dbConnector.update(sql2);
				if (rowsAffected > 0)
					response = "OK 10;";
				else 
					response = "ERROR: Address failed to insert";
			}
			catch (ClassNotFoundException | SQLException ex) 
			{
				System.out.println("Unable to connect to the database (ADD_ADDRESS): " + ex.getMessage());
				response = "ERROR (ADD_ADDRESS): Unable to connect to the database";
			}
		}
		else
		{
			response = "ERR 41;";
		}
		
		return response;
	}
	
	private synchronized String processAdminRequestSTART()
	{
		String response;
		
		if (backgroundServer.getAppListener().getMeteoDownloader().isRunning()
			&& !backgroundServer.getAppListener().getMeteoDownloader().isDownloadingMeteoData())
		{
			backgroundServer.getAppListener().getMeteoDownloader().setDownloadingMeteoData(true);
			response = "OK 10;";
		}
		else
			response = "ERR 31;";
		
		return response;
	}
	private  synchronized String processAdminRequestPAUSE()
	{
		String response;
		
		if (backgroundServer.getAppListener().getMeteoDownloader().isRunning() 
			&& backgroundServer.getAppListener().getMeteoDownloader().isDownloadingMeteoData())
		{
			backgroundServer.getAppListener().getMeteoDownloader().setDownloadingMeteoData(false);
			response = "OK 10;";
		}
		else
			response = "ERR 30;";
		
		return response;
	}
	
	private synchronized String processAdminRequestSTOP()
	{
		String response;
		
		if (backgroundServer.getAppListener().getMeteoDownloader().isRunning()
			&& backgroundServer.isAcceptingUserCommands())
		{
			backgroundServer.getAppListener().getMeteoDownloader().setRunning(false);
			backgroundServer.setAcceptingUserCommands(false);
			response = "OK 10;";
		}
		else
			response = "ERR 32;";
		
		return response;
	}
	
	private synchronized String processAdminRequestSTATUS()
	{
		String response;
		
		if (!backgroundServer.getAppListener().getMeteoDownloader().isRunning()
			&& !backgroundServer.isAcceptingUserCommands())
			response = "OK 02;";
		else if (backgroundServer.getAppListener().getMeteoDownloader().isRunning()
				 && !backgroundServer.getAppListener().getMeteoDownloader().isDownloadingMeteoData())
			response = "OK 00;";
		else if (backgroundServer.getAppListener().getMeteoDownloader().isRunning()
				 && backgroundServer.getAppListener().getMeteoDownloader().isDownloadingMeteoData())
			response = "OK 01;";
		else
			response = "Something is odd...";
		
		return response;
	}
	
	private synchronized String processAdminRequestADD(AdminRequestData data)
	{
		String response;
		
		if (!userExistsInDatabase(data, false, false))
		{
			try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
			{
				String sql = String.format
				(
					"INSERT INTO user(username, password, role, category) VALUES ('%s', '%s', %d, 1)",
					data.getParamUsername(),
					data.getParamPassword(),
					data.getParamRole().equals("USER") ? 1 : 2
				);
				
				int affectedRows = dbConnector.update(sql);
				if (affectedRows > 0)
				{
					try 
					{
						sendMail(dbConnector, data.getRequest());
					}
					catch (MessagingException ex) 
					{
						System.out.println("Unable to send mail: " + ex.getMessage());
					}
					response = "OK 10;";
				}
				else
				{
					response = "ERROR: User failed to insert";
				}
			}
			catch (ClassNotFoundException | SQLException ex) 
			{
				System.out.println("Unable to connect to the database (ADD_USER): " + ex.getMessage());
				response = "ERROR (ADD_USER): Unable to connect to the database";
			}
		}
		else
		{
			response = "ERR 33;";
		}
		
		return response;
	}
	
	private synchronized String processAdminRequestUPDOWN(AdminRequestData data, boolean up)
	{
		String response;
		
		if (userExistsInDatabase(data, false, false))
		{
			if (canChangeCategory(data.getParamUsername(), up))
			{
				try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
				{
					String sql = String.format
					(
						"UPDATE user SET category=category%s1 WHERE username='%s'",
						up ? "+" : "-",
						data.getParamUsername()
					);
					
					int affectedRows = dbConnector.update(sql);
					if (affectedRows > 0)
					{
						response = "OK 10;";
					}
					else 
					{
						response = "ERROR: User failed to update";
					}
				}
				catch (ClassNotFoundException | SQLException ex) 
				{
					System.out.println("Unable to connect to the database (UPDOWN): " + ex.getMessage());
					response = "ERROR (UPDOWN): Unable to connect to the database";
				}
			}
			else
				response = "ERR 34;";
		}
		else
			response = "ERR 35;";
		
		return response;
	}
	
	private synchronized boolean userExistsInDatabase(RequestData data, boolean checkAdmin, boolean authenticate)
	{
		boolean userExists;
		String username;
		String password;
		
		
		if (data instanceof AdminRequestData && !authenticate)
		{
			username = ((AdminRequestData) data).getParamUsername();
			password = ((AdminRequestData) data).getParamPassword();
		}
		else
		{
			username = data.getUsername();
			password = data.getPassword();
		}
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
		{
			String sql;
			
			if (authenticate)
			{
				sql = checkAdmin
					? String.format("SELECT * FROM user WHERE username='%s' AND password='%s' AND role=2", username, password)
					: String.format("SELECT * FROM user WHERE username='%s' AND password='%s'", username, password);
			}
			else
			{
				sql = String.format("SELECT * FROM user WHERE username='%s'", username);
			}
			
			try (ResultSet result = dbConnector.select(sql)) 
			{
				if (result.next())
				{
					userExists = true;
					if (data instanceof UserRequestData && authenticate)
						((UserRequestData) data).setId(result.getInt("id"));
				}
				else
					userExists = false;
			}
		}
		catch (SQLException | ClassNotFoundException ex) 
		{
			System.out.println("Unable to connect to the database (userExists): " + ex.getMessage());
			userExists = false;
		}
		
		return userExists;
	}
	
	private synchronized  boolean addressExistsInDatabase(String address)
	{
		boolean addressExists;
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
		{
			String sql = String.format("SELECT * FROM address WHERE address='%s'", address);
			try (ResultSet result = dbConnector.select(sql)) 
			{
				addressExists = result.next();
			}
		}
		catch (ClassNotFoundException | SQLException ex) 
		{
			System.out.println("Unable to connect to the database (addressExists): " + ex.getMessage());
			addressExists = false;
		}
		
		return addressExists;
	}
	
	private synchronized boolean canChangeCategory(String username, boolean up)
	{
		boolean canChange;
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
		{
			String sql = String.format("SELECT category FROM user WHERE username='%s'", username);
			ResultSet result = dbConnector.select(sql);
			result.next();
			
			int category = result.getInt("category");
			canChange = !((category == 5 && up) || (category == 1 && !up));
			
			result.close();
		}
		catch (ClassNotFoundException | SQLException ex) 
		{
			System.out.println("Unable to connect to the database (canChangeCategory): " + ex.getMessage());
			canChange = false;
		}
		
		return canChange;
	}

	private synchronized void sendMail(DatabaseConnector dbConnector, String request) throws SQLException, MessagingException
	{
		int total = 0;
		int totalUsers = 0;
		int totalAdmins = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
		
		ResultSet result1 = dbConnector.select("SELECT COUNT(*) AS total FROM user");
		if (result1.next())
			total = result1.getInt("total");
		result1.close();
		
		ResultSet result2 = dbConnector.select("SELECT COUNT(*) AS total FROM user WHERE role=1");
		if (result2.next())
			totalUsers = result2.getInt("total");
		result2.close();
		
		ResultSet result3 = dbConnector.select("SELECT COUNT(*) AS total FROM user WHERE role=2");
		if (result3.next())
			totalAdmins = result3.getInt("total");
		result3.close();
		
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append(request);
		messageBuilder.append("\n\n");
		messageBuilder.append("Request time: ");
		messageBuilder.append(sdf.format(requestTime));
		messageBuilder.append("\n");
		messageBuilder.append("Total number of users: ");
		messageBuilder.append(total);
		messageBuilder.append("\n");
		messageBuilder.append("Total number of users with role USER: ");
		messageBuilder.append(totalUsers);
		messageBuilder.append("\n");
		messageBuilder.append("Total number of users with role ADMIN: ");
		messageBuilder.append(totalAdmins);
		
		ExtendedConfigManager config = 
			(ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", config.getMailHost());
		
		Session session = Session.getInstance(properties, null);
		InternetAddress addressFrom = new InternetAddress(config.getMailAddressFrom(), true);
		InternetAddress addressTo = new InternetAddress(config.getMailAddressTo(), true);
		
		MimeMessage mimeMessage = new MimeMessage(session);
		mimeMessage.setFrom(addressFrom);
		mimeMessage.setRecipient(Message.RecipientType.TO, addressTo);
		mimeMessage.setSubject(config.getMailSubject());
		mimeMessage.setText(messageBuilder.toString(), "utf-8", "plain");
		
		System.out.println(messageBuilder.toString());
		Transport.send(mimeMessage);
	}
}
