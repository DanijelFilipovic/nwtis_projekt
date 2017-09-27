package org.foi.nwtis.dfilipov.web.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.web.entities.Users;
import org.foi.nwtis.dfilipov.web.entities.beans.UsersFacade;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;

@Named(value = "registerRequestsBean")
@SessionScoped
public class RegisterRequestsBean implements Serializable
{
	@EJB
	private UsersFacade usersFacade;
	
	private final List<Users> waitingUsers = new ArrayList<>();
	
	int errorCode = 0;
	boolean reload = true;
	
	public RegisterRequestsBean()
	{
	}
	
	public List<Users> getWaitingUsers()
	{
		if (reload)
		{
			waitingUsers.clear();
			waitingUsers.addAll(usersFacade.findWaitingUsers());
			reload = false;
		}
		return waitingUsers;
	}

	public int getErrorCode()
	{
		return errorCode;
	}
	
	public void reload()
	{
		reload = true;
	}
	
	public void declineUser(int id)
	{
		Users user = usersFacade.find(id);
		user.setIsRejected(Boolean.TRUE);
		usersFacade.edit(user);
		waitingUsers.remove(user);
	}
	
	public void acceptUser(int id, Users admin)
	{
		Users user = usersFacade.find(id);
		String command = String.format(
			"USER %s; PASSWD %s; ADD %s; PASSWD %s; ROLE %s;",
			admin.getUsername(),
			admin.getPassword(),
			user.getUsername(),
			user.getPassword(),
			user.getRole().getName()
		);
		
		errorCode = sendAddUserCommand(command);
		if (errorCode == 0)
			waitingUsers.remove(user);
	}

	private int sendAddUserCommand(String command)
	{
		int status = 0;
		
		ExtendedConfigManager config =
			(ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		
		String host = config.getPrimitiveServerHost();
		int port = Integer.parseInt(config.getPrimitiveServerPort());
		
		try (Socket clientSocket = new Socket(host, port);
			 InputStream is = clientSocket.getInputStream();
			 OutputStream os = clientSocket.getOutputStream())
		{
			os.write(command.getBytes("UTF-8"));
			os.flush();
			clientSocket.shutdownOutput();
			
			StringBuilder response = new StringBuilder();
			
			int _byte;
			while ((_byte = is.read()) != -1)
				response.append((char)_byte);
			clientSocket.shutdownInput();
			
			if (!response.toString().equals("OK 10;"))
				status = 1;
		}
		catch (IOException ex) 
		{
			status = 2;
		}
		
		return status;
	}
}
