package org.foi.nwtis.dfilipov.web.data;

public class User
{
	private long id;
	private String username;
	private String password;
	private String role;
	private int category;

	public User() {}

	public User(long id, String username, String password, String role, int category)
	{
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.category = category;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getRole()
	{
		return role;
	}

	public void setRole(String role)
	{
		this.role = role;
	}

	public int getCategory()
	{
		return category;
	}

	public void setCategory(int category)
	{
		this.category = category;
	}
}
