package org.foi.nwtis.dfilipov.web.entities.beans;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.foi.nwtis.dfilipov.web.entities.Roles;
import org.foi.nwtis.dfilipov.web.entities.Users;

@Stateless
public class UsersFacade extends AbstractFacade<Users>
{
	public static final String ATTR_ID			= "id";
	public static final String ATTR_USERNAME	= "username";
	public static final String ATTR_SURNAME		= "surname";
	public static final String ATTR_PASSWORD	= "password";
	public static final String ATTR_EMAIL		= "email";
	public static final String ATTR_ISWAITING	= "is_waiting";
	public static final String ATTR_ISREJECTED	= "is_rejected";
	
	@PersistenceContext(unitName = "dfilipov_aplikacija_2_dbPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager()
	{
		return em;
	}

	public UsersFacade()
	{
		super(Users.class);
	}
	
	public Users findByAttribute(String attribute, Object value)
	{
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Users> criteriaQuery = criteriaBuilder.createQuery(Users.class);
		Root<Users> users = criteriaQuery.from(entityClass);
		
		criteriaQuery.select(users);
		criteriaQuery.where(criteriaBuilder.equal(users.get(attribute), value));
		
		try
		{
			return getEntityManager().createQuery(criteriaQuery).getSingleResult();
		}
		catch (NoResultException ex)
		{
			return null;
		}
	}
	
	public Users findByUsername(String username)
	{
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Users> criteriaQuery = criteriaBuilder.createQuery(Users.class);
		Root<Users> users = criteriaQuery.from(entityClass);
		
		criteriaQuery.select(users);
		Predicate equalUsername = criteriaBuilder.equal(users.get("username"), username);
		criteriaQuery.where(equalUsername);
		
		try
		{
			return getEntityManager().createQuery(criteriaQuery).getSingleResult();
		}
		catch (NoResultException ex)
		{
			return null;
		}
	}
	
	public Users findByUsernameAndPassword(String username, String password)
	{
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Users> criteriaQuery = criteriaBuilder.createQuery(Users.class);
		Root<Users> users = criteriaQuery.from(entityClass);
		
		criteriaQuery.select(users);
		Predicate equalUsername = criteriaBuilder.equal(users.get("username"), username);
		Predicate equalPassword = criteriaBuilder.equal(users.get("password"), password);
		criteriaQuery.where(criteriaBuilder.and(equalUsername, equalPassword));
		
		try
		{
			return getEntityManager().createQuery(criteriaQuery).getSingleResult();
		}
		catch (NoResultException ex)
		{
			return null;
		}
	}
	
	public Users findByUsernamePasswordAndRole(String username, String password, Roles role)
	{
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Users> criteriaQuery = criteriaBuilder.createQuery(Users.class);
		Root<Users> users = criteriaQuery.from(entityClass);
		
		criteriaQuery.select(users);
		Predicate equalUsername = criteriaBuilder.equal(users.<String>get("username"), username);
		Predicate equalPassword = criteriaBuilder.equal(users.<String>get("password"), password);
		Predicate equalRole = criteriaBuilder.equal(users.<Roles>get("role"), role);
		criteriaQuery.where(criteriaBuilder.and(equalUsername, equalPassword, equalRole));
		
		try
		{
			return getEntityManager().createQuery(criteriaQuery).getSingleResult();
		}
		catch (NoResultException ex)
		{
			return null;
		}
	}
	
	public List<Users> findWaitingUsers()
	{
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Users> criteriaQuery = criteriaBuilder.createQuery(Users.class);
		Root<Users> users = criteriaQuery.from(entityClass);
		
		Predicate isWaiting = criteriaBuilder.equal(users.<Boolean>get("isWaiting"), true);
		Predicate isNotRejected = criteriaBuilder.equal(users.<Boolean>get("isRejected"), false);
		
		criteriaQuery
			.select(users)
			.where(criteriaBuilder.and(isWaiting, isNotRejected));
		
		return getEntityManager().createQuery(criteriaQuery).getResultList();
	}
}
