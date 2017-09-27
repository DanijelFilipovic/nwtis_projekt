package org.foi.nwtis.dfilipov.web.beans;

import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.foi.nwtis.dfilipov.web.entities.Users;
import org.foi.nwtis.dfilipov.web.singleton.ActiveUsersBean;

@Named(value = "logoutBean")
@RequestScoped
public class LogoutBean
{
	@EJB
	private ActiveUsersBean activeUsersBean;
	
	public LogoutBean() {}
	
	public String logout()
	{
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		Object session = context.getSession(false);
		if (session != null) 
		{
			Users user = (Users) context.getSessionMap().get("USER");
			activeUsersBean.removeUser(user);
			context.invalidateSession();
		}
		return "INDEX";
	}
}
