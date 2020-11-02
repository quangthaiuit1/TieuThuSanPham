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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Customer;
import lixco.com.entity.CustomerPromotionProgram;
import lixco.com.entity.PromotionProgram;
import lixco.com.interfaces.ICustomerPromotionProgramService;
import lixco.com.reqInfo.CustomerPromotionProgramReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class CustomerPromotionProgramService implements ICustomerPromotionProgramService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int search(String json, PagingInfo page, List<CustomerPromotionProgram> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ customer_promotion_program_info:{customer_types_id:0,customer_id:0,program_code:'',from_date:'',to_date:'',disable:-1}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j.get("customer_promotion_program_info"),"customer_id", null);
			HolderParser hCustomerTypeId=JsonParserUtil.getValueNumber(j.get("customer_promotion_program_info"),"customer_types_id", null);
			HolderParser hProgramCode=JsonParserUtil.getValueString(j.get("customer_promotion_program_info"),"program_code", null);
			HolderParser hFromDate=JsonParserUtil.getValueString(j.get("customer_promotion_program_info"),"from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j.get("customer_promotion_program_info"),"to_date", null);
			HolderParser hDisable=JsonParserUtil.getValueNumber(j.get("customer_promotion_program_info"),"disable", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<CustomerPromotionProgram> cq=cb.createQuery(CustomerPromotionProgram.class);
			Root<CustomerPromotionProgram> root_= cq.from(CustomerPromotionProgram.class);
			Join<CustomerPromotionProgram, PromotionProgram> promotionProgram_=(Join)root_.fetch("promotion_program", JoinType.INNER);
			Join<CustomerPromotionProgram, Customer> customer_=(Join)root_.fetch("customer", JoinType.INNER);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Long> pCustomerTypesId=cb.parameter(Long.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<String> pProgramCode=cb.parameter(String.class);
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Integer> pDisable=cb.parameter(Integer.class);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pCustomerTypesId,0));
			dis1.getExpressions().add(cb.equal(customer_.get("customer_types").get("id"), pCustomerTypesId));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pCustomerId,0));
			dis2.getExpressions().add(cb.equal(customer_.get("id"), pCustomerId));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.isNull(pProgramCode));
			dis3.getExpressions().add(cb.equal(pProgramCode,""));
			dis3.getExpressions().add(cb.equal(promotionProgram_.get("program_code"), pProgramCode));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.isNull(pFromDate));
			dis4.getExpressions().add(cb.equal(pFromDate,""));
			dis4.getExpressions().add(cb.greaterThanOrEqualTo(root_.get("effective_date"), pFromDate));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.isNull(pToDate));
			dis5.getExpressions().add(cb.equal(pToDate,""));
			dis5.getExpressions().add(cb.isNull(root_.get("expiry_date")));
			dis5.getExpressions().add(cb.lessThanOrEqualTo(root_.get("expiry_date"), pToDate));
			predicates.add(dis5);
			Predicate dis6=cb.disjunction();
			dis6.getExpressions().add(cb.equal(pDisable, -1));
			dis6.getExpressions().add(cb.equal(root_.get("disable"), pDisable));
			predicates.add(dis6);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root_.get("created_date")));
			TypedQuery<CustomerPromotionProgram> query=em.createQuery(cq);
			query.setParameter(pCustomerId, Long.parseLong(Objects.toString(hCustomerId.getValue(), null)));
			query.setParameter(pCustomerTypesId, Long.parseLong(Objects.toString(hCustomerTypeId.getValue(), null)));
			query.setParameter(pProgramCode,Objects.toString(hProgramCode.getValue(), null));
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy"));
			query.setParameter(pDisable, Integer.parseInt(Objects.toString(hDisable.getValue(), null)));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(CustomerPromotionProgram.class);
			promotionProgram_=root_.join("promotion_program", JoinType.INNER);
			customer_= root_.join("customer", JoinType.INNER);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pCustomerId, Long.parseLong(Objects.toString(hCustomerId.getValue(), null)));
			query2.setParameter(pCustomerTypesId, Long.parseLong(Objects.toString(hCustomerTypeId.getValue(), null)));
			query2.setParameter(pProgramCode,Objects.toString(hProgramCode.getValue(), null));
			query2.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy"));
			query2.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy"));
			query2.setParameter(pDisable, Integer.parseInt(Objects.toString(hDisable.getValue(), null)));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch(Exception e){
			logger.error("CustomerPromotionProgramService.search:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int insert(CustomerPromotionProgramReqInfo t) {
		int res=-1;
		try{
			CustomerPromotionProgram p=t.getCustomer_promotion_program();
			if(p !=null){
				//kiểm tra chương trình đơn giá có áp dụng cho khách hàng này chưa
				//JPQL
				Query query=em.createQuery("select count(p) from CustomerPromotionProgram as p where p.promotion_program=:pc and p.customer=:c ");
				query.setParameter("pc",p.getPromotion_program());
				query.setParameter("c",p.getCustomer());
				int chk=Integer.parseInt(Objects.toString(query.getSingleResult()));
				if(chk==0){
					em.persist(p);
					if(p.getId()>0){
						res=0;
					}
				}else{
					res=-2;//duplicatie
				}
				
			}
		}catch(Exception e){
			logger.error("CustomerPromotionProgramService.insert:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int update(CustomerPromotionProgramReqInfo t) {
		int res=-1;
		try{
			CustomerPromotionProgram p=t.getCustomer_promotion_program();
			if(p !=null){
				//kiểm tra chương trình đơn giá có áp dụng cho khách hàng này chưa
				//JPQL
				Query query=em.createQuery("select count(p) from CustomerPromotionProgram as p where p.promotion_program=:pc and p.customer=:c and p.id <> :idc ");
				query.setParameter("pc",p.getPromotion_program());
				query.setParameter("c",p.getCustomer());
				query.setParameter("idc", p.getId());
				int chk=Integer.parseInt(Objects.toString(query.getSingleResult()));
				if (chk == 0) {
					p = em.merge(p);
					if (p != null) {
						res = 0;
					}
				} else {
					res = -2;//duplicate
				}
			}
		}catch(Exception e){
			logger.error("CustomerPromotionProgramService.update:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectById(long id, CustomerPromotionProgramReqInfo t) {
		int res=-1;
		try{
			CustomerPromotionProgram p=em.find(CustomerPromotionProgram.class,id);
			if(p!=null){
				t.setCustomer_promotion_program(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("CustomerPromotionProgramService.selectById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from CustomerPromotionProgram where id = :id");
			query.setParameter("id", id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("CustomerPromotionProgramService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectAll(long programId, List<CustomerPromotionProgram> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<CustomerPromotionProgram> cq=cb.createQuery(CustomerPromotionProgram.class);
			Root<CustomerPromotionProgram> root= cq.from(CustomerPromotionProgram.class);
			cq.select(root).where(cb.equal(root.get("promotion_program").get("id"),programId));
			root.fetch("customer",JoinType.INNER);
			root.fetch("promotion_program",JoinType.INNER);
			TypedQuery<CustomerPromotionProgram> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("CustomerPromotionProgramService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectBy(long programId, long customerId, CustomerPromotionProgramReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<CustomerPromotionProgram> cq=cb.createQuery(CustomerPromotionProgram.class);
			Root<CustomerPromotionProgram> root= cq.from(CustomerPromotionProgram.class);
			root.fetch("promotion_program",JoinType.INNER);
			root.fetch("customer",JoinType.INNER);
			cq.select(root).where(cb.and(cb.equal(root.get("promotion_program").get("id"),programId),cb.equal(root.get("customer").get("id"), customerId)));
			res=0;
		}catch(Exception e){
			logger.error("CustomerPromotionProgramService.selectBy:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectForCustomer(String json, CustomerPromotionProgramReqInfo t) {
		int res=-1;
		try{   /*{order_date:'',customer_id:0}*/
				JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
				HolderParser hOrderDate=JsonParserUtil.getValueString(j,"order_date", null);
				HolderParser hCustomerId=JsonParserUtil.getValueNumber(j,"customer_id", null);
				if(hOrderDate.getErr()==0 && hCustomerId.getErr()==0){
					CriteriaBuilder cb=em.getCriteriaBuilder();
					CriteriaQuery<CustomerPromotionProgram> cq=cb.createQuery(CustomerPromotionProgram.class);
					Root<CustomerPromotionProgram> root= cq.from(CustomerPromotionProgram.class);
					root.fetch("promotion_program",JoinType.INNER);
					root.fetch("customer",JoinType.INNER);
					List<Predicate> predicates=new ArrayList<>();
					ParameterExpression<Date> pOrderDate=cb.parameter(Date.class);
					ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
					predicates.add(cb.equal(root.get("customer").get("id"),pCustomerId));
					predicates.add(cb.lessThanOrEqualTo(root.get("effective_date"), pOrderDate));
					Predicate dis=cb.disjunction();
					dis.getExpressions().add(cb.isNull(root.get("expiry_date")));
					dis.getExpressions().add(cb.greaterThanOrEqualTo(root.get("expiry_date"), pOrderDate));
					predicates.add(dis);
					Subquery<Date> subquery=cq.subquery(Date.class);
					Root<CustomerPromotionProgram> rootSub=subquery.from(CustomerPromotionProgram.class);
					List<Predicate> predicates2=new ArrayList<>();
					predicates2.add(cb.equal(rootSub.get("customer").get("id"),pCustomerId));
					predicates2.add(cb.lessThanOrEqualTo(rootSub.get("effective_date"), pOrderDate));
					Predicate dis1=cb.disjunction();
					dis1.getExpressions().add(cb.isNull(rootSub.get("expiry_date")));
					dis1.getExpressions().add(cb.greaterThanOrEqualTo(rootSub.get("expiry_date"), pOrderDate));
					predicates2.add(dis1);
					subquery.select(cb.greatest(rootSub.get("effective_date"))).where(cb.and(predicates2.toArray(new Predicate[0])));
					predicates.add(cb.equal(root.get("effective_date"), subquery));
					predicates.add(cb.isFalse(root.get("disable")));
					cq.select(root).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root.get("created_date")));
					TypedQuery<CustomerPromotionProgram> query=em.createQuery(cq);
					query.setParameter(pOrderDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hOrderDate.getValue(),null),"dd/MM/yyyy"));
					query.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(),"0")));
					List<CustomerPromotionProgram> list=query.getResultList();
					if(list.size() >0){
					  t.setCustomer_promotion_program(list.get(0));
				    }
				res=0;
			}else{
				res=-1;
			}
		}catch(Exception e){
			logger.error("CustomerPromotionProgramService.selectForCustomer:"+e.getMessage(),e);
		}
		return res;
	}
	

}
