package lixco.com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
import javax.persistence.criteria.Expression;
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
import lixco.com.entity.FreightContract;
import lixco.com.interfaces.IFreightContractService;
import lixco.com.reqInfo.FreightContractReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class FreightContractService implements IFreightContractService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;

	@Override
	public int search(String json, PagingInfo page, List<FreightContract> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{freight_contract_info:{contract_no:'',from_date:'',to_date:'',customer_id:0,car_id:0,payment_method_id:0,payment:-1}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hContractNo=JsonParserUtil.getValueString(j.get("freight_contract_info"),"contract_no", null);
			HolderParser hFromDate=JsonParserUtil.getValueString(j.get("freight_contract_info"),"from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j.get("freight_contract_info"),"to_date", null);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j.get("freight_contract_info"),"customer_id", null);
			HolderParser hCarId=JsonParserUtil.getValueNumber(j.get("freight_contract_info"),"car_id", null);
			HolderParser hPaymentMethodId=JsonParserUtil.getValueNumber(j.get("freight_contract_info"),"payment_method_id", null);
			HolderParser hPayment=JsonParserUtil.getValueNumber(j.get("freight_contract_info"),"payment", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<FreightContract> cq=cb.createQuery(FreightContract.class);
			Root<FreightContract> root_= cq.from(FreightContract.class);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<String> pContractNo=cb.parameter(String.class);
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<Long> pCarId=cb.parameter(Long.class);
			ParameterExpression<Long> pPaymentMethodId=cb.parameter(Long.class);
			ParameterExpression<Integer> pPayment=cb.parameter(Integer.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pContractNo,""));
			dis.getExpressions().add(cb.equal(root_.get("contract_no"), pContractNo));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pFromDate));
			dis1.getExpressions().add(cb.equal(pFromDate,""));
			dis1.getExpressions().add(cb.greaterThanOrEqualTo(root_.get("contract_date"), pFromDate));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.isNull(pToDate));
			dis2.getExpressions().add(cb.equal(pToDate,""));
			dis2.getExpressions().add(cb.lessThanOrEqualTo(root_.get("contract_date"), pToDate));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pCustomerId,0));
			dis3.getExpressions().add(cb.equal(root_.get("customer").get("id"), pCustomerId));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.equal(pCarId,0));
			dis4.getExpressions().add(cb.equal(root_.get("car").get("id"), pCarId));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pPaymentMethodId,0));
			dis5.getExpressions().add(cb.equal(root_.get("payment_method").get("id"), pPaymentMethodId));
			predicates.add(dis5);
			Predicate dis6=cb.disjunction();
			dis6.getExpressions().add(cb.equal(pPayment,-1));
			dis6.getExpressions().add(cb.equal(root_.get("payment"),pPayment));
			predicates.add(dis6);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root_.get("contract_code")));
			TypedQuery<FreightContract> query=em.createQuery(cq);
			query.setParameter(pContractNo,Objects.toString(hContractNo.getValue(), ""));
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy"));
			query.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query.setParameter(pCarId,Long.parseLong(Objects.toString(hCarId.getValue(),"0")));
			query.setParameter(pPaymentMethodId,Long.parseLong(Objects.toString(hPaymentMethodId.getValue(),"0")));
			query.setParameter(pPayment,Integer.parseInt(Objects.toString(hPayment.getValue(), "-1")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(FreightContract.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pContractNo,Objects.toString(hContractNo.getValue(), ""));
			query2.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy"));
			query2.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy"));
			query2.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query2.setParameter(pCarId,Long.parseLong(Objects.toString(hCarId.getValue(),"0")));
			query2.setParameter(pPaymentMethodId,Long.parseLong(Objects.toString(hPaymentMethodId.getValue(),"0")));
			query2.setParameter(pPayment,Integer.parseInt(Objects.toString(hPayment.getValue(), "-1")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
			
		}catch(Exception e){
			logger.error("FreightContractService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(FreightContractReqInfo t) {
		int res=-1;
		try{
			FreightContract p=t.getFreight_contract();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("FreightContractService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(FreightContractReqInfo t) {
		int res=-1;
		try{
			FreightContract p=t.getFreight_contract();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   FreightContractReqInfo f=new FreightContractReqInfo();
				   selectById(p.getId(), f);
				   t.setFreight_contract(f.getFreight_contract());
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("FreightContractService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, FreightContractReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<FreightContract> cq=cb.createQuery(FreightContract.class);
			Root<FreightContract> root_= cq.from(FreightContract.class);
			root_.fetch("customer",JoinType.INNER);
			root_.fetch("car",JoinType.LEFT);
			root_.fetch("payment_method",JoinType.LEFT);
			cq.select(root_).where(cb.equal(root_.get("id"), id));
			TypedQuery<FreightContract> query=em.createQuery(cq);
			t.setFreight_contract(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("FreightContractService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			FreightContract t=em.find(FreightContract.class, id);
			if(t !=null){
				em.remove(t.getList_freight_contract_detail());
				em.remove(t);
				res=0;
			}
		}catch(Exception e){
			logger.error("FreightContractService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public String initOrderCode() {
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<FreightContract> root= cq.from(FreightContract.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)root.get("contract_code"),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/100000000;
			if(p<1){
				return String.format("%08d", max+1);
			}
			return (max+1)+"";
		}catch(Exception e){
			logger.error("FreightContractService.initOrderCode:"+e.getMessage(),e);
		}
		return null;
	}

	@Override
	public int selectByOrderCode(String contractCode, FreightContractReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<FreightContract> cq=cb.createQuery(FreightContract.class);
			Root<FreightContract> root_= cq.from(FreightContract.class);
			root_.fetch("customer",JoinType.INNER);
			root_.fetch("car",JoinType.LEFT);
			root_.fetch("payment_method",JoinType.LEFT);
			cq.select(root_).where(cb.equal(root_.get("contract_code"), contractCode));
			TypedQuery<FreightContract> query=em.createQuery(cq);
			t.setFreight_contract(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("FreightContractService.selectByOrderCode:"+e.getMessage(),e);
		}
		return res;
	}

}
