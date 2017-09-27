package org.foi.nwtis.dfilipov.web.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.net.Socket;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.web.entities.Users;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;

@Named(value = "pscBean")
@SessionScoped
public class PrimitiveServerControlsBean implements Serializable
{
	private int serverMessageCode = -1;
	
	private final int serverPort;
	private final String serverHost;
	
	public PrimitiveServerControlsBean() 
	{
		ExtendedConfigManager config = 
			(ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		
		serverPort = Integer.parseInt(config.getPrimitiveServerPort());
		serverHost = config.getPrimitiveServerHost();
	}

	public int getServerMessageCode()
	{
		return serverMessageCode;
	}

	public void sendStartCommand(Users user)
	{
		String command = String.format("USER %s; PASSWD %s; START;", user.getUsername(), user.getPassword());
		String response = sendSocketCommand(command);
		
		switch (response)
		{
			case "OK 10;":
				serverMessageCode = 1;
				break;
			
			case "ERR 21;":
				serverMessageCode = 0;
				break;
			
			case "ERR 31;":
				serverMessageCode = 4;
				break;
				
			default:
				serverMessageCode = 10;
		}
	}

	public void sendPauseCommand(Users user)
	{
		String command = String.format("USER %s; PASSWD %s; PAUSE;", user.getUsername(), user.getPassword());
		String response = sendSocketCommand(command);
		
		switch (response)
		{
			case "OK 10;":
				serverMessageCode = 2;
				break;
			
			case "ERR 21;":
				serverMessageCode = 0;
				break;
			
			case "ERR 30;":
				serverMessageCode = 5;
				break;
				
			default:
				serverMessageCode = 10;
		}
	}

	public void sendStopCommand(Users user)
	{
		String command = String.format("USER %s; PASSWD %s; STOP;", user.getUsername(), user.getPassword());
		String response = sendSocketCommand(command);
		
		switch (response)
		{
			case "OK 10;":
				serverMessageCode = 3;
				break;
				
			case "ERR 21;":
				serverMessageCode = 0;
				break;
				
			case "ERR 32;":
				serverMessageCode = 6;
				break;
				
			default:
				serverMessageCode = 10;
		}
	}

	public void sendStatusCommand(Users user)
	{
		String command = String.format("USER %s; PASSWD %s; STATUS;", user.getUsername(), user.getPassword());
		String response = sendSocketCommand(command);
		
		switch (response)
		{
			case "ERR 21;":
				serverMessageCode = 0;
				break;
				
			case "OK 00;":
				serverMessageCode = 7;
				break;
				
			case "OK 01;":
				serverMessageCode = 8;
				break;
				
			case "OK 02;":
				serverMessageCode = 9;
				break;
				
			default:
				serverMessageCode = 10;
		}
	}
	
	private String sendSocketCommand(String command)
	{
		StringBuilder response = new StringBuilder();
		
		try (Socket clientSocket = new Socket(serverHost, serverPort);
			 OutputStream os = clientSocket.getOutputStream();
			 InputStream is = clientSocket.getInputStream())
		{
			os.write(command.getBytes("UTF-8"));
			System.out.println(command);
			os.flush();
			clientSocket.shutdownOutput();
			
			int _byte;
			while ((_byte = is.read()) != -1)
				response.append((char)_byte);
			clientSocket.shutdownInput();
		}
		catch (IOException ex) 
		{
			System.out.println("Error occured while creatingclient socket: " + ex.getMessage());
		}
		
		return response.toString();
	}
}
