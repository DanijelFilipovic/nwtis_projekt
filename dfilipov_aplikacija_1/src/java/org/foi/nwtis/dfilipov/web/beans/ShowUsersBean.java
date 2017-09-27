package org.foi.nwtis.dfilipov.web.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.database.DatabaseConnector;
import org.foi.nwtis.dfilipov.database.DatabaseInfo;
import org.foi.nwtis.dfilipov.web.data.User;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;

public final class ShowUsersBean implements Serializable
{
	private final List<User> users = new ArrayList<>();
	private final ExtendedConfigManager config;
	
	private final int rowCount;
	private int maxPage;
	private int currentPage = 1;
	
	private String filterRole;
	
	public ShowUsersBean()
	{
		config = (ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		rowCount = Integer.parseInt(config.getTableRowCount());
		loadUsers();
	}
	
	public List<User> getUsers()
	{
		List<User> usersByPage = new ArrayList<>();
		
		if (currentPage < 1)
			currentPage = 1;
		
		for (int i = (currentPage - 1) * rowCount; i < (rowCount * currentPage) && i < users.size(); i++)
			usersByPage.add(users.get(i));
		
		return usersByPage;
	}
	
	public int getMaxPage()
	{
		return maxPage;
	}

	public int getCurrentPage()
	{
		return currentPage;
	}

	public void setCurrentPage(int currentPage)
	{
		this.currentPage = currentPage;
	}

	public String getFilterRole()
	{
		return filterRole;
	}

	public void setFilterRole(String filterRole)
	{
		this.filterRole = filterRole;
	}
		
	public void loadUsers()
	{
		users.clear();
		
		DatabaseInfo dbInfo = new DatabaseInfo(
			config.getDatabaseServer(),
			config.getDatabaseName(),
			config.getDatabaseUser(),
			config.getDatabasePass(),
			config.getDatabaseDriver()
		);
		
		try (DatabaseConnector dbConnector = new DatabaseConnector(dbInfo))
		{
			String sql = "SELECT u.id, u.username, u.password, r.name AS role, u.category FROM user u, role r WHERE u.role = r.id";
			if (filterRole != null && !filterRole.isEmpty() && !filterRole.equals(" "))
				sql += String.format(" AND r.name='%s'", filterRole);
			
			try (ResultSet result = dbConnector.select(sql))
			{
				while (result.next())
				{
					User user = new User();
					user.setId(result.getLong("id"));
					user.setUsername(result.getString("username"));
					user.setPassword(result.getString("password"));
					user.setRole(result.getString("role"));
					user.setCategory(result.getInt("category"));
					users.add(user);
				}
			}
		}
		catch (SQLException | ClassNotFoundException ex) 
		{
			System.out.println("Unable to operate with database (ShowUsersBean): " + ex.getMessage());
		}
		
		calculateMaxPage();
	}
	
	private void calculateMaxPage()
	{
		double doubleMaxPage = ((double)users.size()) / ((double)rowCount);
		maxPage = (int) Math.ceil(doubleMaxPage);
	}
}
