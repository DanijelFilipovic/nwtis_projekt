package org.foi.nwtis.dfilipov.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.foi.nwtis.dfilipov.web.entities.Users;
import org.foi.nwtis.dfilipov.web.entities.beans.UsersFacade;
import org.foi.nwtis.dfilipov.web.singleton.ActiveUsersBean;

@Named(value = "loginBean")
@SessionScoped
public class LoginBean implements Serializable
{
	@EJB
	private ActiveUsersBean activeUsersBean;
	
	@EJB
	private UsersFacade usersFacade;
	
	private String username;
	private String password;
	private final List<Integer> errorCodes = new ArrayList<>();
	
	public LoginBean() {}

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

	public List<Integer> getErrorCodes()
	{
		return errorCodes;
	}
	
	public String processLogin()
	{
		String result = null;
		errorCodes.clear();
		
		if (username == null || username.isEmpty())
			errorCodes.add(3);
		
		if (password == null || password.isEmpty())
			errorCodes.add(4);
		
		if (errorCodes.isEmpty())
		{
			Users user = usersFacade.findByUsernameAndPassword(username, password);
			
			if (user == null)
				errorCodes.add(0);
			else if (user.getIsRejected())
				errorCodes.add(1);
			else if (user.getIsWaiting())
				errorCodes.add(2);
			else
			{
				ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
				context.getSession(true);
				context.getSessionMap().put("USER", user);
				activeUsersBean.addUser(user);
				result = "INDEX";
			}
		}
		
		return result;
	}
}
