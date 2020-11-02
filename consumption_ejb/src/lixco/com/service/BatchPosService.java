package lixco.com.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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

import lixco.com.entity.BatchPos;
import lixco.com.interfaces.IBatchPosService;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class BatchPosService implements IBatchPosService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Override
	public double getQuantityRemaining(long productId) {
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq=cb.createQuery(Object[].class);
			Root<BatchPos> root= cq.from(BatchPos.class);
			cq.multiselect(cb.coalesce(cb.sum(root.get("quantity_import")),0.0),cb.coalesce(cb.sum(root.get("quantity_export")),0.0)).where(cb.equal(root.get("product").get("id"),productId));
			TypedQuery<Object[]> query=em.createQuery(cq);
			List<Object[]> list=query.getResultList();
			double remain=0;
			for(Object[] p: list){
				Object p1=p[0];
				Object p2=p[1];
				double quantityImport=Double.parseDouble(Objects.toString(p1,"0"));
				double quantityExport=Double.parseDouble(Objects.toString(p2, "0"));
				remain=BigDecimal.valueOf(quantityImport).subtract(BigDecimal.valueOf(quantityExport)).doubleValue();
			}
			return remain;
		}catch(Exception e){
			logger.error("BatchPosService.getQuantityRemaining:"+e.getMessage(),e);
		}
		return 0;
	}

}
