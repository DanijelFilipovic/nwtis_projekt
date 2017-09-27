package org.foi.nwtis.dfilipov.web.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import org.foi.nwtis.dfilipov.web.entities.Users;
import org.foi.nwtis.dfilipov.web.entities.beans.RolesFacade;
import org.foi.nwtis.dfilipov.web.entities.beans.UsersFacade;

@Named(value = "registerBean")
@RequestScoped
public class RegisterBean
{
	@EJB
	private UsersFacade usersFacade;
	
	@EJB
	private RolesFacade rolesFacade;
	
	private static final int USER_TYPE_USER = 1;
	private static final int USER_TYPE_ADMIN = 2;
	
	private String username;
	private String surname;
	private String password;
	private String passwordAgain;
	private String email;
	private int userType;
	
	private final List<Integer> errorCodes = new ArrayList<>();

	public RegisterBean() {}	

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getPasswordAgain()
	{
		return passwordAgain;
	}

	public void setPasswordAgain(String passwordAgain)
	{
		this.passwordAgain = passwordAgain;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public int getUserType()
	{
		return userType;
	}

	public void setUserType(int userType)
	{
		this.userType = userType;
	}
	
	public List<Integer> getErrorCodes()
	{
		return errorCodes;
	}
	
	public String processRegistration()
	{
		String result = null;
		errorCodes.clear();
		
		// Process username
		if (username == null || username.isEmpty())
			errorCodes.add(0);
		
		// Process surname
		if (surname == null || surname.isEmpty())
			errorCodes.add(1);
		
		// Process password
		if (password == null || password.isEmpty())
			errorCodes.add(2);
		
		// Process repeated password
		if (passwordAgain == null || passwordAgain.isEmpty())
			errorCodes.add(3);
		
		// Compare password and repeated password
		if (password != null && passwordAgain != null && !password.equals(passwordAgain))
			errorCodes.add(4);
		
		// Process e-mail
		if (email == null || email.isEmpty())
			errorCodes.add(5);
		else
		{
			if (!Pattern.matches("^[a-zA-Z0-9_\\-\\.]+@[a-zA-Z0-9_\\-\\.]+\\.[a-zA-Z0-9]+$", email))
				errorCodes.add(6);
		}
			
		
		// Add user into the database and process the result
		if (errorCodes.isEmpty())
		{
			boolean usernameExists = usersFacade.findByAttribute(UsersFacade.ATTR_USERNAME, username) != null;
			boolean emailExists = usersFacade.findByAttribute(UsersFacade.ATTR_EMAIL, email) != null;
			boolean updated = false;
			
			if (usernameExists)
			{
				Users existingUser = usersFacade.findByAttribute(UsersFacade.ATTR_USERNAME, username);
				if (existingUser.getIsRejected())
				{
					existingUser.setSurname(surname);
					existingUser.setPassword(password);
					existingUser.setEmail(email);
					existingUser.setRole(rolesFacade.find(userType));
					existingUser.setIsWaiting(true);
					existingUser.setIsRejected(false);
					usersFacade.edit(existingUser);
					result = "LOGIN";
					updated = true;
				}
				else
					errorCodes.add(7);
			}
			
			if (emailExists && !updated)
				errorCodes.add(8);
			
			if (errorCodes.isEmpty() && !usernameExists)
			{
				Users user = new Users();
				user.setUsername(username);
				user.setSurname(surname);
				user.setPassword(password);
				user.setEmail(email);
				user.setRole(rolesFacade.find(userType));
				user.setIsWaiting(true);
				user.setIsRejected(false);
				usersFacade.create(user);
				result = "LOGIN";
			}
		}
		
		return result; 
	}
}
