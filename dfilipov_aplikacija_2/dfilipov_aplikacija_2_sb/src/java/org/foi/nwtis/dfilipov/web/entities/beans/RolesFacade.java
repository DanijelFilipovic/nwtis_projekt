package org.foi.nwtis.dfilipov.web.entities.beans;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.foi.nwtis.dfilipov.web.entities.Roles;

@Stateless
public class RolesFacade extends AbstractFacade<Roles>
{
	@PersistenceContext(unitName = "dfilipov_aplikacija_2_dbPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager()
	{
		return em;
	}

	public RolesFacade()
	{
		super(Roles.class);
	}
	
	public Roles findByName(String rolename)
	{
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Roles> criteriaQuery = criteriaBuilder.createQuery(Roles.class);
		Root<Roles> roles = criteriaQuery.from(entityClass);
		
		criteriaQuery
			.select(roles)
			.where(criteriaBuilder.equal(roles.<String>get("name"), rolename));
		
		try
		{
			return getEntityManager().createQuery(criteriaQuery).getSingleResult();
		}
		catch (NoResultException ex)
		{
			return null;
		}
	}
}
