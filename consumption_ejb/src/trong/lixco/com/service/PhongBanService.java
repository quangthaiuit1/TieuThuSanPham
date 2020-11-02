package trong.lixco.com.service;


import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import trong.lixco.com.entity.PhongBan;


@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PhongBanService extends AbstractService<PhongBan>{
	protected final Logger logger = Logger.getLogger(getClass());
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;
	@Override
	protected Class<PhongBan> getEntityClass() {
		// TODO Auto-generated method stub
		return PhongBan.class;
	}
	@Override
	protected EntityManager getEntityManager() {
		// TODO Auto-generated method stub
		return em;
	}
	@Override
	protected SessionContext getUt() {
		// TODO Auto-generated method stub
		return ct;
	}
	public PhongBan getPhongBanByCodeOld(String codeEm) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PhongBan> cq = cb.createQuery(PhongBan.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<PhongBan> root = cq.from(PhongBan.class);
		predicates.add(cb.equal(root.get("codePBOld"), codeEm));

		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<PhongBan> query = em.createQuery(cq);
		List<PhongBan> results = query.getResultList();
		if (results.size() != 0)
			return results.get(0);
		return null;
	}
}
