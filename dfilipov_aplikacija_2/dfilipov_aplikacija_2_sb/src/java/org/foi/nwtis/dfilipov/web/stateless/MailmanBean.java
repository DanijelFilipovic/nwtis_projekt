package org.foi.nwtis.dfilipov.web.stateless;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import org.foi.nwtis.dfilipov.web.data.NewAddressInfo;

@Stateless
@LocalBean
public class MailmanBean
{
	@Resource(mappedName = "jms/NWTiS_QF_dfilipov")
	private ConnectionFactory connectionFactory;
	
	@Resource(mappedName = "jms/NWTiS_dfilipov_2")
	private Queue addressQueue;
	
	public boolean sendNewAddress(String username, String password, String address)
	{
		boolean status = true;
		
		try (Connection connection = connectionFactory.createConnection(); 
			 Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); 
			 MessageProducer messageProducer = session.createProducer(addressQueue)) 
		{
			ObjectMessage message = session.createObjectMessage();
			NewAddressInfo addressInfo = new NewAddressInfo(username, password, address);
			message.setObject(addressInfo);
			messageProducer.send(message);
		}
		catch (JMSException ex)
		{
			System.out.println("Unable to send JMS message: " + ex.getMessage());
			status = false;
		}
		
		return status;
	}
}
