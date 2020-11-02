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

import lixco.com.entity.InvoiceDetail;
import lixco.com.interfaces.IInvoiceDetailService;
import lixco.com.reqInfo.InvoiceDetailReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class InvoiceDetailService implements IInvoiceDetailService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectByOrder(long invoiceId, List<InvoiceDetail> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<InvoiceDetail> cq=cb.createQuery(InvoiceDetail.class);
			Root<InvoiceDetail> root= cq.from(InvoiceDetail.class);
			root.fetch("product",JoinType.INNER);
			root.fetch("invoice",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("invoice").get("id"), invoiceId));
			TypedQuery<InvoiceDetail> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("InvoiceDetailService.selectByOrder:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectById(long id, InvoiceDetailReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<InvoiceDetail> cq=cb.createQuery(InvoiceDetail.class);
			Root<InvoiceDetail> root= cq.from(InvoiceDetail.class);
			root.fetch("product",JoinType.INNER);
			root.fetch("invoice",JoinType.INNER);
			root.fetch("invoice_detail_own",JoinType.LEFT);
			cq.select(root).where(cb.equal(root.get("id"), id));
			TypedQuery<InvoiceDetail> query=em.createQuery(cq);
			t.setInvoice_detail(query.getSingleResult());
			res=0;
		}catch (Exception e) {
			logger.error("InvoiceDetailService.selectById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectByInvoiceDetailMain(long idMain, List<InvoiceDetail> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<InvoiceDetail> cq=cb.createQuery(InvoiceDetail.class);
			Root<InvoiceDetail> root= cq.from(InvoiceDetail.class);
			root.fetch("product",JoinType.INNER);
			root.fetch("invoice",JoinType.INNER);
			root.fetch("invoice_detail_own",JoinType.LEFT);
			cq.select(root).where(cb.equal(root.get("invoice_detail_own").get("id"), idMain));
			TypedQuery<InvoiceDetail> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("InvoiceDetailService.selectByInvoiceDetailMain:"+e.getMessage(),e);
		}
		return res;
	}

}
