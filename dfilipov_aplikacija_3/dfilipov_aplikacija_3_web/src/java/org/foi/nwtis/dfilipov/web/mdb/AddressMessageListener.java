package org.foi.nwtis.dfilipov.web.mdb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.foi.nwtis.dfilipov.web.data.NewAddressInfo;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;
import org.foi.nwtis.dfilipov.web.singleton.MessageBufferBean;

@MessageDriven(activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_dfilipov_2"),
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class AddressMessageListener implements MessageListener
{
	@EJB
	private MessageBufferBean messageBufferBean;
	
	public AddressMessageListener() {}
	
	@Override
	public void onMessage(Message message)
	{
		if (message instanceof ObjectMessage)
		{
			try 
			{
				ObjectMessage objectMessage = (ObjectMessage) message;
				NewAddressInfo addressInfo = (NewAddressInfo) objectMessage.getObject();
				
				String commandTEST = String.format(
					"USER %s; PASSWD %s; TEST \"%s\";",
					addressInfo.getUsername(),
					addressInfo.getPassword(),
					addressInfo.getAddress()
				);
				String responseTEST = sendSocketCommand(commandTEST);
				
				switch (responseTEST) 
				{
					case "ERR 42;": 
						String commandADD = String.format("USER %s; PASSWD %s; ADD \"%s\";",
							addressInfo.getUsername(),
							addressInfo.getPassword(),
							addressInfo.getAddress()
						);
						String responseADD = sendSocketCommand(commandADD);
						if (responseADD.equals("OK 10;"))
							System.out.println(addressInfo.getUsername() + " added new address: " + addressInfo.getAddress());
						else
							System.out.println("Unable to add new address...");
						break;
					
					case "OK 10;": 
						System.out.println("Address already exists.");
						break;
					
					default:
						System.out.println("Unable to process the command.");
				}
				
				messageBufferBean.addMessage(addressInfo);
			}
			catch (JMSException ex) 
			{
				System.out.println("Unable to read JMS message: " + ex.getMessage());
			}
		}
	}
	
	private String sendSocketCommand(String command)
	{
		StringBuilder response = new StringBuilder();
		
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
			
			int _byte;
			while ((_byte = is.read()) != -1)
				response.append((char)_byte);
			clientSocket.shutdownInput();
		}
		catch (IOException ex) 
		{
			System.out.println("Exception in socket I/O: " + ex.getMessage());
		}
		
		return response.toString();
	}
	
}
