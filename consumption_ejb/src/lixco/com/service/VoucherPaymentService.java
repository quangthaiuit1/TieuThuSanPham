package lixco.com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Contract;
import lixco.com.entity.VoucherPayment;
import lixco.com.interfaces.IVoucherPaymentService;
import lixco.com.reqInfo.VoucherPaymentReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class VoucherPaymentService implements IVoucherPaymentService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Override
	public int search(String json, PagingInfo page, List<VoucherPayment> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ voucher_payment:{voucher_code:'',payment_date:'',contact:{voucher_code:''},payment_customer_id:0,receiver_customer_id:0}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json,JsonObject.class);
			HolderParser hVoucherPayment=JsonParserUtil.getValueObject(j, "voucher_payment", null, JsonObject.class);
			JsonObject jsonVoucherPayment=(JsonObject)hVoucherPayment.getValue();
			HolderParser hVoucherCode=JsonParserUtil.getValueString(jsonVoucherPayment, "voucher_code", null);
			HolderParser hPaymentDate=JsonParserUtil.getValueString(jsonVoucherPayment, "payment_date", null);
			HolderParser hPaymentCustomerId=JsonParserUtil.getValueNumber(jsonVoucherPayment, "payment_customer_id", null);
			HolderParser hReceiverCustomerId=JsonParserUtil.getValueNumber(jsonVoucherPayment, "receiver_customer_id", null);
			HolderParser hContract=JsonParserUtil.getValueObject(jsonVoucherPayment,"contact", null,JsonObject.class);
			HolderParser hContractVoucherCode=JsonParserUtil.getValueString((JsonObject)hContract.getValue(),"voucher_code", null);
			HolderParser hPage=JsonParserUtil.getValueObject(j, "page", null, JsonObject.class);
			JsonObject jsonPage=(JsonObject) hPage.getValue();
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(jsonPage,"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(jsonPage,"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<VoucherPayment> cq=cb.createQuery(VoucherPayment.class);
			Root<VoucherPayment> root=cq.from(VoucherPayment.class);
			root.fetch("payment_customer",JoinType.INNER);
			root.fetch("receiver_customer",JoinType.INNER);
			root.fetch("currency",JoinType.INNER);
			Join<VoucherPayment, Contract> contact_=(Join)root.fetch("contract",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<String> pVoucherCode=cb.parameter(String.class);
			ParameterExpression<Date> pPaymentDate=cb.parameter(Date.class);
			ParameterExpression<Long> pPaymentCustomerId=cb.parameter(Long.class);
			ParameterExpression<Long> pReceiverCustomerId=cb.parameter(Long.class);
			ParameterExpression<String> pContractVoucherCode=cb.parameter(String.class);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pVoucherCode, ""));
			dis1.getExpressions().add(cb.equal(root.get("voucher_code"),pVoucherCode));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.isNull(pPaymentDate));
			dis2.getExpressions().add(cb.equal(root.get("payment_date"),pPaymentDate));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pContractVoucherCode, ""));
			dis3.getExpressions().add(cb.equal(contact_.get("voucher_code"),pContractVoucherCode));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.equal(pPaymentCustomerId, 0));
			dis4.getExpressions().add(cb.equal(root.get("payment_customer").get("id"), pPaymentCustomerId));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pReceiverCustomerId, 0));
			dis5.getExpressions().add(cb.equal(root.get("receiver_customer").get("id"), pReceiverCustomerId));
			predicates.add(dis5);
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<VoucherPayment> query=em.createQuery(cq);
			query.setParameter(pVoucherCode,Objects.toString(hVoucherCode.getValue(),""));
			query.setParameter(pPaymentDate, ToolTimeCustomer.convertStringToDate(Objects.toString(hPaymentDate.getValue(), null),"dd/MM/yyyy"));
			query.setParameter(pContractVoucherCode, Objects.toString(hContractVoucherCode.getValue(),""));
			query.setParameter(pPaymentCustomerId,Long.parseLong(Objects.toString(hPaymentCustomerId.getValue(), "0")));
			query.setParameter(pReceiverCustomerId,Long.parseLong(Objects.toString(hReceiverCustomerId.getValue(), "0")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root=cq1.from(VoucherPayment.class);
			contact_=root.join("contract",JoinType.LEFT);
			dis3.getExpressions().clear();
			dis3.getExpressions().add(cb.equal(pContractVoucherCode, ""));
			dis3.getExpressions().add(cb.equal(contact_.get("voucher_code"),pContractVoucherCode));
			cq1.select(cb.count(root.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pVoucherCode,Objects.toString(hVoucherCode.getValue(),""));
			query2.setParameter(pPaymentDate, ToolTimeCustomer.convertStringToDate(Objects.toString(hPaymentDate.getValue(), null),"dd/MM/yyyy"));
			query2.setParameter(pContractVoucherCode, Objects.toString(hContractVoucherCode.getValue(),""));
			query2.setParameter(pPaymentCustomerId,Long.parseLong(Objects.toString(hPaymentCustomerId.getValue(), "0")));
			query2.setParameter(pReceiverCustomerId,Long.parseLong(Objects.toString(hReceiverCustomerId.getValue(), "0")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch (Exception e) {
			logger.error("VoucherPaymentService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(VoucherPaymentReqInfo t) {
		int res=-1;
		try{
			VoucherPayment p=t.getVoucher_payment();
			if(p !=null){
				em.persist(p);
				if(p.getId() !=0){
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("VoucherPaymentService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(VoucherPaymentReqInfo t) {
		int res=-1;
		try{
			VoucherPayment p=t.getVoucher_payment();
			if(p !=null){
				em.merge(p);
				if(em.merge(p)!=null){
					//selectById nạp lên
					selectById(p.getId(), t);
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("VoucherPaymentService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JPQl
			Query query=em.createQuery("delete from VoucherPayment where id=:id ");
			query.setParameter("id", id);
			res=query.executeUpdate();
		}catch (Exception e) {
			logger.error("VoucherPaymentService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, VoucherPaymentReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<VoucherPayment> cq=cb.createQuery(VoucherPayment.class);
			Root<VoucherPayment> root=cq.from(VoucherPayment.class);
			root.fetch("payment_customer",JoinType.INNER);
			root.fetch("payment_customer",JoinType.INNER);
			root.fetch("currency",JoinType.INNER);
			root.fetch("contract",JoinType.LEFT);
			cq.select(root).where(cb.equal(root.get("id"), id));
			TypedQuery<VoucherPayment> query=em.createQuery(cq);
			t.setVoucher_payment(query.getSingleResult());
			res=0;
		}catch (Exception e) {
			logger.error("VoucherPaymentService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int getVoucherPaymentBy(String json, List<VoucherPayment> list) {
		int res=-1;
		try{/*{contract_id:0}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json,JsonObject.class);
			HolderParser hContractId=JsonParserUtil.getValueNumber(j, "contract_id", null);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<VoucherPayment> cq=cb.createQuery(VoucherPayment.class);
			Root<VoucherPayment> root=cq.from(VoucherPayment.class);
			ParameterExpression<Long> pContractId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal( pContractId,0));
			dis.getExpressions().add(cb.equal(root.get("contract").get("id"), pContractId));
			cq.select(root).where(dis).orderBy(cb.asc(root.get("payment_date")));
			TypedQuery<VoucherPayment> query=em.createQuery(cq);
			query.setParameter(pContractId,Long.parseLong(Objects.toString(hContractId.getValue())));
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("VoucherPaymentService.getVoucherPaymentBy:"+e.getMessage(),e);
		}
		return res;
	}

}
