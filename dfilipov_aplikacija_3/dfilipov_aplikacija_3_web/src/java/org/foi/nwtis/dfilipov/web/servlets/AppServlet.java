package org.foi.nwtis.dfilipov.web.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;
import org.foi.nwtis.dfilipov.web.singleton.MessageBufferBean;

@WebServlet(loadOnStartup = 1)
public class AppServlet extends HttpServlet
{
	@EJB
	private MessageBufferBean messageBufferBean;
	
	@Override
	public void init() throws ServletException
	{		
		String baseFolderName = AppListener.getServletContext().getRealPath("/WEB-INF") + File.separator;
		String messageFolderName = "messages" + File.separator;
		
		File messageFolder = new File(baseFolderName + messageFolderName);
		
		try
		{
			DirectoryStream<Path> files = Files.newDirectoryStream(Paths.get(messageFolder.toURI()));
			Iterator<Path> iter = files.iterator();
			
			while (iter.hasNext())
			{
				File messageFile = iter.next().toFile();
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(messageFile)))
				{
					Serializable message = (Serializable) ois.readObject();
					messageBufferBean.addMessage(message);
				}
				catch (IOException | ClassNotFoundException ex)
				{
					System.out.println("Unable to read the JMS file.");
				}
			}
			
			files.close();
		}
		catch (IOException ex) 
		{
			System.out.println("Unable to read JMS directory.");
		}
		
		System.out.println("INIT - MessageBufferBean size: " + messageBufferBean.getMessageBuffer().size());
	}

	@Override
	public void destroy()
	{
		System.out.println("DESTROY - MessageBufferBean size: " + messageBufferBean.getMessageBuffer().size());
		
		String baseFolderName = AppListener.getServletContext().getRealPath("/WEB-INF") + File.separator;
		String messageFolderName = "messages" + File.separator;
		String messageFile = baseFolderName + messageFolderName + "msg_";
		
		List<Serializable> messages = messageBufferBean.getMessageBuffer();
		for (int i = 0; i < messages.size(); i++)
		{
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(messageFile + i)))
			{
				oos.writeObject(messages.get(i));
			}
			catch (IOException ex) 
			{
				System.out.println("Unable to write the JMS message.");
			}
		}
	}
	
}
