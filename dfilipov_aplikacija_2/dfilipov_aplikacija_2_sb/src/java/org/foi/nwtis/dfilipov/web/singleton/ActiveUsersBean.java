package org.foi.nwtis.dfilipov.web.singleton;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Singleton;
import org.foi.nwtis.dfilipov.web.entities.Users;

@Singleton
public class ActiveUsersBean
{
	private static final List<Users> activeUsers = new ArrayList<>();
	
	public static List<Users> getActiveUsers()
	{
		return activeUsers;
	}
	
	public void addUser(Users user)
	{
		activeUsers.add(user);
		System.out.println("Number of active users: " + activeUsers.size());
	}
	
	public void removeUser(Users user)
	{
		activeUsers.remove(user);
		System.out.println("Number of active users: " + activeUsers.size());
	}
}
