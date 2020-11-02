package lixco.com.service;

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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import lixco.com.entity.InvoiceDetailPos;
import lixco.com.interfaces.IInvoiceDetailPosService;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class InvoiceDetailPosService implements IInvoiceDetailPosService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectByOrder(long invoiceId, List<InvoiceDetailPos> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<InvoiceDetailPos> cq=cb.createQuery(InvoiceDetailPos.class);
			Root<InvoiceDetailPos> root= cq.from(InvoiceDetailPos.class);
			root.fetch("product",JoinType.INNER);
			root.fetch("invoice_pos",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("invoice_pos").get("id"), invoiceId));
			TypedQuery<InvoiceDetailPos> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("InvoiceDetailPosService.selectByOrder:"+e.getMessage(),e);
		}
		return res;
	}

}
