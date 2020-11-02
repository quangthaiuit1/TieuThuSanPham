package lixco.com.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import lixco.com.entity.Currency;
import lixco.com.interfaces.ICurrencyService;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class CurrencyService implements ICurrencyService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Override
	public int selectAll(List<Currency> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Currency> cq=cb.createQuery(Currency.class);
			Root<Currency> root=cq.from(Currency.class);
			cq.select(root);
			TypedQuery<Currency> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("CurrencyService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

}
