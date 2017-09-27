package org.foi.nwtis.dfilipov.servlets;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import org.foi.nwtis.dfilipov.registerer.Registerer;
import org.foi.nwtis.dfilipov.web.entities.beans.RolesFacade;
import org.foi.nwtis.dfilipov.web.entities.beans.UsersFacade;

@WebServlet(loadOnStartup = 1)
public class AppServlet extends HttpServlet
{
	private Registerer registerer;
	
	@EJB
	private UsersFacade usersFacade;
	
	@EJB
	private RolesFacade rolesFacade;
	
	@Override
	public void init() throws ServletException
	{
		registerer = new Registerer(this);
		registerer.start();
	}

	@Override
	public void destroy()
	{
		if (registerer != null && registerer.isAlive())
			registerer.interrupt();
	}

	public UsersFacade getUsersFacade()
	{
		return usersFacade;
	}

	public RolesFacade getRolesFacade()
	{
		return rolesFacade;
	}
}
