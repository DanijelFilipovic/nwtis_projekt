package org.foi.nwtis.dfilipov.registerer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.servlets.AppServlet;
import org.foi.nwtis.dfilipov.web.entities.Roles;
import org.foi.nwtis.dfilipov.web.entities.Users;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;

public class Registerer extends Thread
{	
	private final AppServlet appServlet;
	
	private final String VALID_CONTENT = "^USER\\s([a-zA-Z0-9_\\-]+);\\sPASSWD\\s([a-zA-Z0-9_\\-]+);\\sADD\\s([a-zA-Z0-9_\\-]+);\\sPASSWD\\s([a-zA-Z0-9_\\-]+);\\sROLE\\s(ADMIN|USER);(.*)";
	private final ExtendedConfigManager config;
	private final int interval;
	
	private boolean running = true;
	
	public Registerer(AppServlet appServlet)
	{
		super("Registerer");
		this.config = (ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		this.interval = Integer.parseInt(config.getRegistererInterval());
		this.appServlet = appServlet;
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
				operationLength = System.currentTimeMillis();
				
				collectAndProcessMessages();
				
				operationLength = System.currentTimeMillis() - operationLength;
			}
			catch (InterruptedException ex) 
			{
				running = false;
			}
		}
	}

	private void collectAndProcessMessages()
	{
		String host = config.getMailHost();
		String port = config.getMailPortImap();
		String address = config.getMailAddress();
		String password = config.getMailPassword();
		
		Properties properties = System.getProperties();
		properties.put("mail.imap.host", host);
		properties.put("mail.imap.port", port);
		
		try 
		{
			Session session = Session.getInstance(properties, null);
			Store store = session.getStore("imap");
			store.connect(host, address, password);
			
			Folder root = store.getDefaultFolder();
			Folder inbox = root.getFolder("INBOX");
			Folder successful = root.getFolder(config.getMailFolderSuccessful());
			Folder unsuccessful = root.getFolder(config.getMailFolderUnsuccessful());
			Folder incorrect = root.getFolder(config.getMailFolderIncorrect());
			
			if (!successful.exists())
				successful.create(Folder.HOLDS_MESSAGES);
			
			if (!unsuccessful.exists())
				unsuccessful.create(Folder.HOLDS_MESSAGES);
			
			if (!incorrect.exists())
				incorrect.create(Folder.HOLDS_MESSAGES);
			
			inbox.open(Folder.READ_ONLY);
			Message[] messages = inbox.getMessages();
			
			List<Message> successfulMessages = new ArrayList<>();
			List<Message> unsuccessfulMessages = new ArrayList<>();
			List<Message> incorrectMessages = new ArrayList<>();
			
			for (Message m : messages)
			{
				int status = processMessage(m);
				switch (status)
				{
					case 1: 
					{
						unsuccessfulMessages.add(m);
						System.out.println("Message will be added to UNSUCCESSFUL folder.");
					} break;
						
					case 2:
					{
						incorrectMessages.add(m);
						System.out.println("Message will be added to INCORRECT folder.");
					} break;
						
					default:
					{
						successfulMessages.add(m);
						System.out.println("Message will be added to SUCCESSFUL folder.");
					} break;
				}
				m.setFlag(Flags.Flag.DELETED, true);
			}
			
			successful.appendMessages(successfulMessages.toArray(new Message[successfulMessages.size()]));
			unsuccessful.appendMessages(unsuccessfulMessages.toArray(new Message[unsuccessfulMessages.size()]));
			incorrect.appendMessages(incorrectMessages.toArray(new Message[incorrectMessages.size()]));
			
			inbox.close(true);			
			store.close();
		}
		catch (MessagingException | IOException ex) 
		{
			System.out.println("Unable to use mail server: " + ex.getMessage());
		}
	}

	private int processMessage(Message message) throws MessagingException, IOException
	{
		int status = 0;
		String requiredSubject = config.getMailSubject();
		
		String content = message.getContent().toString().trim();
		Pattern p = Pattern.compile(VALID_CONTENT, Pattern.DOTALL);
		Matcher m = p.matcher(content);
		boolean matches = m.matches();
		
		if (!message.getSubject().equals(requiredSubject)
			|| !message.isMimeType("text/plain")
			|| !matches)
		{
			status = 2;
		}
		else
		{
			String username = m.group(3);
			String password = m.group(4);
			String rolename = m.group(5);
			
			Roles role = appServlet.getRolesFacade().findByName(rolename);
			Users user = appServlet.getUsersFacade().findByUsernamePasswordAndRole(username, password, role);
			
			if (user != null && user.getIsWaiting())
			{
				user.setIsWaiting(false);
				appServlet.getUsersFacade().edit(user);
			}
			else
				status = 1;
		}
		
		return status;
	}
}
