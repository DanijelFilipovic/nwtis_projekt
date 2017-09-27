package org.foi.nwtis.dfilipov.web.rest.client;

import com.sun.xml.bind.StringInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.foi.nwtis.dfilipov.web.data.Address;
import org.foi.nwtis.dfilipov.web.data.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ActiveUsersRESTClient
{
	private WebTarget webTarget;
	private Client client;
	private static final String BASE_URI = "http://localhost:8080/dfilipov_aplikacija_2_web/REST";

	public ActiveUsersRESTClient()
	{
		client = javax.ws.rs.client.ClientBuilder.newClient();
		webTarget = client.target(BASE_URI).path("activeUsers");
	}

	public List<Address> getActiveUserAddedAddresses(String id) throws ClientErrorException
	{
		WebTarget resource = webTarget;
		resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
		String xmlString = resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(String.class);
		
		List<Address> addresses = new ArrayList<>();
		
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new StringInputStream(xmlString));
			doc.normalize();
			
			NodeList addressNodes = doc.getElementsByTagName("address-data");
			
			for (int i = 0; i < addressNodes.getLength(); i++)
			{
				Node node = addressNodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element elem = (Element) node;
					Address address = new Address();

					address.setID(Integer.parseInt(elem.getElementsByTagName("id").item(0).getTextContent()));
					address.setAddress(decodeCroatianLetters(elem.getElementsByTagName("address").item(0).getTextContent()));
					address.setLatitude(elem.getElementsByTagName("latitude").item(0).getTextContent());
					address.setLongitude(elem.getElementsByTagName("longitude").item(0).getTextContent());

					addresses.add(address);
				}
			}
		}
		catch (ParserConfigurationException | SAXException | IOException ex) 
		{
			System.out.println("Unable to parse XML (getActiveUserAddedAddresses): " + ex.getMessage());
		}
		
		return addresses;
	}

	public List<User> getActiveUsers() throws ClientErrorException
	{
		WebTarget resource = webTarget;
		String xmlString = resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(String.class);
		
		List<User> activeUsers = new ArrayList<>();
		
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new StringInputStream(xmlString));
			
			NodeList userNodes = doc.getElementsByTagName("user");
			for (int i = 0; i < userNodes.getLength(); i++)
			{
				Node node = userNodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element elem = (Element) node;
					User user = new User();
					
					user.setId(Integer.parseInt(elem.getElementsByTagName("id").item(0).getTextContent()));
					user.setUsername(decodeCroatianLetters(elem.getElementsByTagName("username").item(0).getTextContent()));
					
					activeUsers.add(user);
				}
			}
		}
		catch (ParserConfigurationException | SAXException | IOException ex) 
		{
			System.out.println("Unable to parse XML (getActiveUsers): " + ex.getMessage());
		}
		
		return activeUsers;
	}

	public void close()
	{
		client.close();
	}
	
	private String decodeCroatianLetters(String text)
	{
		String convertedText = text.replace("-ch-", "ć");
		convertedText = convertedText.replace("-chh-", "č");
		convertedText = convertedText.replace("-dh-", "đ");
		convertedText = convertedText.replace("-sh-", "š");
		convertedText = convertedText.replace("-zh-", "ž");
		convertedText = convertedText.replace("-Ch-", "Ć");
		convertedText = convertedText.replace("-Chh-", "Č");
		convertedText = convertedText.replace("-Dh-", "Đ");
		convertedText = convertedText.replace("-Sh-", "Š");
		convertedText = convertedText.replace("-Zh-", "Ž");
		return convertedText;
	}
}
