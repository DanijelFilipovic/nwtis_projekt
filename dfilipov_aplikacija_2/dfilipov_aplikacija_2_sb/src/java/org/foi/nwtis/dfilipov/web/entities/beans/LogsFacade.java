package org.foi.nwtis.dfilipov.web.entities.beans;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.foi.nwtis.dfilipov.web.entities.Logs;
import org.foi.nwtis.dfilipov.web.entities.Users;

@Stateless
public class LogsFacade extends AbstractFacade<Logs>
{
	@PersistenceContext(unitName = "dfilipov_aplikacija_2_dbPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager()
	{
		return em;
	}

	public LogsFacade()
	{
		super(Logs.class);
	}
	
	public List<Logs> findWithFilter(int firstRow, int maxRows, String request, Users user, Date dateFrom, Date dateTo)
	{
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Logs> criteriaQuery = criteriaBuilder.createQuery(Logs.class);
		Root<Logs> logs = criteriaQuery.from(entityClass);
		
		criteriaQuery.select(logs);
		
		Predicate equalsRequest = request != null && !request.isEmpty()
			? criteriaBuilder.equal(logs.<String>get("request"), request)
			: criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
		
		Predicate equalsUser = user != null
			? criteriaBuilder.equal(logs.<Users>get("user"), user)
			: criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
		
		Predicate geDateFrom = dateFrom != null
			? criteriaBuilder.greaterThanOrEqualTo(logs.<Date>get("datetime"), dateFrom)
			: criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
		
		Predicate leDateTo = dateTo != null
			? criteriaBuilder.lessThanOrEqualTo(logs.<Date>get("datetime"), dateTo)
			: criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
		
		criteriaQuery.where(criteriaBuilder.and(equalsRequest, equalsUser, geDateFrom, leDateTo));
		
		return getEntityManager()
			.createQuery(criteriaQuery)
			.setFirstResult(firstRow)
			.setMaxResults(maxRows)
			.getResultList();
	}
}
