package org.foi.nwtis.dfilipov.web.rest.server;

import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.dfilipov.web.entities.Users;
import org.foi.nwtis.dfilipov.web.singleton.ActiveUsersBean;
import org.foi.nwtis.dfilipov.web.soap.client.Address;
import org.foi.nwtis.dfilipov.web.soap.client.MeteoWSClient;

@Path("activeUsers")
public class ActiveUsersREST
{
	@Context
	private UriInfo context;

	public ActiveUsersREST() {}
	
	@GET
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8")
	public String getActiveUsers()
	{
		List<Users> activeUsers = ActiveUsersBean.getActiveUsers();
		StringBuilder xmlBuilder = new StringBuilder();
		
		xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlBuilder.append("<users>");
		for (Users u : activeUsers)
		{
			xmlBuilder.append("<user>");
			xmlBuilder.append("<id>");
			xmlBuilder.append(u.getId());
			xmlBuilder.append("</id>");
			xmlBuilder.append("<username>");
			xmlBuilder.append(u.getUsername());
			xmlBuilder.append("</username>");
			xmlBuilder.append("</user>");
		}
		xmlBuilder.append("</users>");
		
		return encodeCroatianLetters(xmlBuilder.toString());
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8")
	public String getActiveUserAddedAddresses(@PathParam("id") int id) throws UnsupportedEncodingException
	{
		List<Users> activeUsers = ActiveUsersBean.getActiveUsers();
		StringBuilder xmlBuilder = new StringBuilder();
		MeteoWSClient meteoClient = new MeteoWSClient();
		
		xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlBuilder.append("<addresses>");
		boolean found = false;
		for (int i = 0; i < activeUsers.size() && !found; i++)
		{
			Users u = activeUsers.get(i);
			if (u.getId() == id)
			{
				List<Address> addresses = meteoClient.getAddedAddresses(u.getUsername(), u.getPassword());
				for (Address a : addresses)
				{
					xmlBuilder.append("<address-data>");
					
					xmlBuilder.append("<id>");
					xmlBuilder.append(a.getID());
					xmlBuilder.append("</id>");
					
					xmlBuilder.append("<address>");
					xmlBuilder.append(a.getAddress());
					xmlBuilder.append("</address>");
					
					xmlBuilder.append("<latitude>");
					xmlBuilder.append(a.getLatitude());
					xmlBuilder.append("</latitude>");
					
					xmlBuilder.append("<longitude>");
					xmlBuilder.append(a.getLongitude());
					xmlBuilder.append("</longitude>");
					
					xmlBuilder.append("</address-data>");
				}
				found = true;
			}
		}
		xmlBuilder.append("</addresses>");
		
		return encodeCroatianLetters(xmlBuilder.toString());
	}
	
	private String encodeCroatianLetters(String text)
	{
		String convertedText = text.replace("ć", "-ch-");
		convertedText = convertedText.replace("č", "-chh-");
		convertedText = convertedText.replace("đ", "-dh-");
		convertedText = convertedText.replace("š", "-sh-");
		convertedText = convertedText.replace("ž", "-zh-");
		convertedText = convertedText.replace("Ć", "-Ch-");
		convertedText = convertedText.replace("Č", "-Chh-");
		convertedText = convertedText.replace("Đ", "-Dh-");
		convertedText = convertedText.replace("Š", "-Sh-");
		convertedText = convertedText.replace("Ž", "-Zh-");
		return convertedText;
	}
}
