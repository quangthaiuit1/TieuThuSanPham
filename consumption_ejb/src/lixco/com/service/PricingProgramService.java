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
import javax.persistence.criteria.Expression;
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
import lixco.com.entity.PricingProgram;
import lixco.com.entity.PricingProgramDetail;
import lixco.com.interfaces.IPricingProgramService;
import lixco.com.reqInfo.PricingProgramReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class PricingProgramService implements IPricingProgramService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<PricingProgram> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PricingProgram> cq=cb.createQuery(PricingProgram.class);
			Root<PricingProgram> root= cq.from(PricingProgram.class);
			cq.select(root);
			TypedQuery<PricingProgram> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("PricingProgramService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json, PagingInfo page, List<PricingProgram> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ pricing_program_info:{from_date:'',to_date:'',program_code:'',parent_pricing_program_id:0,product_id:0,disable:0}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hFromDate=JsonParserUtil.getValueString(j.get("pricing_program_info"),"from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j.get("pricing_program_info"),"to_date", null);
			HolderParser hProgramCode=JsonParserUtil.getValueString(j.get("pricing_program_info"),"program_code", null);
			HolderParser hParentProgram=JsonParserUtil.getValueNumber(j.get("pricing_program_info"), "parent_pricing_program_id", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j.get("pricing_program_info"),"product_id", null);
			HolderParser hDisable=JsonParserUtil.getValueNumber(j.get("pricing_program_info"),"disable", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PricingProgram> cq=cb.createQuery(PricingProgram.class);
			Root<PricingProgram> root_= cq.from(PricingProgram.class);
			root_.fetch("parent_pricing_program",JoinType.LEFT);
			Join<PricingProgram, PricingProgramDetail> pricingProgramDetail_=root_.join("list_pricing_program_detail",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<String> pProgramCode=cb.parameter(String.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pParentId=cb.parameter(Long.class);
			ParameterExpression<Integer> pDisable=cb.parameter(Integer.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pFromDate));
			dis.getExpressions().add(cb.equal(pFromDate,""));
			dis.getExpressions().add(cb.greaterThanOrEqualTo(root_.get("effective_date"), pFromDate));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pToDate));
			dis1.getExpressions().add(cb.equal(pToDate,""));
			dis1.getExpressions().add(cb.lessThanOrEqualTo(root_.get("expiry_date"), pToDate));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.isNull(pProgramCode));
			dis2.getExpressions().add(cb.equal(pProgramCode,""));
			dis2.getExpressions().add(cb.equal(root_.get("program_code"), pProgramCode));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pProductId,0));
			dis3.getExpressions().add(cb.equal(pricingProgramDetail_.get("product").get("id"), pProductId));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.equal(pParentId,0));
			dis4.getExpressions().add(cb.equal(root_.get("parent_pricing_program").get("id"),pParentId));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pDisable,-1));
			dis5.getExpressions().add(cb.equal(root_.get("disable"),pDisable));
			predicates.add(dis5);
			cq.select(root_).distinct(true).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root_.get("program_code")));
			TypedQuery<PricingProgram> query=em.createQuery(cq);
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query.setParameter(pProgramCode,Objects.toString(hProgramCode.getValue(), null));
			query.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue(), null)));
			query.setParameter(pParentId, Long.parseLong(Objects.toString(hParentProgram.getValue(), null)));
			query.setParameter(pDisable, Integer.parseInt(Objects.toString(hDisable.getValue(), null)));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(PricingProgram.class);
			pricingProgramDetail_=root_.join("list_pricing_program_detail",JoinType.LEFT);
			cq1.select(cb.countDistinct(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query2.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query2.setParameter(pProgramCode,Objects.toString(hProgramCode.getValue(), null));
			query2.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue(), null)));
			query2.setParameter(pParentId, Long.parseLong(Objects.toString(hParentProgram.getValue(), null)));
			query2.setParameter(pDisable, Integer.parseInt(Objects.toString(hDisable.getValue(), null)));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
			
		}catch(Exception e){
			logger.error("PricingProgramService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(PricingProgramReqInfo t) {
		int res=-1;
		try{
			PricingProgram p=t.getPricing_program();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("PricingProgramService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(PricingProgramReqInfo t) {
		int res=-1;
		try{
			PricingProgram p=t.getPricing_program();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setPricing_program(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("PricingProgramService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, PricingProgramReqInfo t) {
		int res=-1;
		try{
			PricingProgram p=em.find(PricingProgram.class,id);
			if(p!=null){
				t.setPricing_program(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("PricingProgramService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from PricingProgramDetail where pricing_program.id = :id");
			query.setParameter("id", id);
			query.executeUpdate();
			Query query1=em.createQuery("delete from PricingProgram where id=:id ");
			query1.setParameter("id",id);
			res=query1.executeUpdate();
		}catch(Exception e){
			logger.error("PricingProgramService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int getDateMaxSubPricingProgram(long parent_program_id,StringBuilder result) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq=cb.createQuery(Object[].class);
			Root<PricingProgram> root_= cq.from(PricingProgram.class);
			cq.multiselect(cb.count(root_.get("id")),cb.greatest(root_.get("effective_date")),cb.greatest(root_.get("expiry_date"))).where(cb.equal(root_.get("parent_pricing_program"),parent_program_id));
			TypedQuery<Object[]> query=em.createQuery(cq);
			List<Object[]> list=query.getResultList();
			for(Object[] obj:list){
				int count=Integer.parseInt(Objects.toString(obj[0],"0"));
				Date maxEffectiveDate=(Date)obj[1];
				Date maxExpiryDate=(Date)obj[2];
				JsonObject json=new JsonObject();
				json.addProperty("count", count);
				json.addProperty("effective_date", ToolTimeCustomer.convertDateToString(maxEffectiveDate,"dd/MM/yyyy HH:mm:ss"));
				json.addProperty("expiry_date", ToolTimeCustomer.convertDateToString(maxExpiryDate,"dd/MM/yyyy HH:mm:ss"));
				result.append(JsonParserUtil.getGson().toJson(json));
			}
			res=0;
		}catch(Exception e){
			logger.error("PricingProgramService.checkDatePricingProgram:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public String initPricingProgramCode() {
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<PricingProgram> root= cq.from(PricingProgram.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)cb.substring(root.get("program_code"),3),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/100000;
			if(p<1){
				return "DG"+String.format("%06d", max+1);
			}
			return "DG"+max+1;
		}catch(Exception e){
			logger.error("PricingProgramService.initPricingProgramCode:"+e.getMessage(),e);
		}
		return null;
	}

	@Override
	public int findLike(String text, int size, List<PricingProgram> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PricingProgram> cq=cb.createQuery(PricingProgram.class);
			Root<PricingProgram> root= cq.from(PricingProgram.class);
			ParameterExpression<String> paramLike=cb.parameter(String.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("program_code")),cb.literal("đ"),cb.literal("d")), paramLike));
			dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("voucher_code")),cb.literal("đ"),cb.literal("d")), paramLike));
			cq.select(root).where(dis);
			TypedQuery<PricingProgram> query=em.createQuery(cq);
			query.setParameter(paramLike, "%"+text+"%");
			if(size!=-1){
				query.setFirstResult(0);
				query.setMaxResults(size);
			}
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("PricingProgramService.findLike:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int complete(String text, List<PricingProgram> list) {
		int res=-1;
		try{
			if(text !=null && !"".equals(text)){
				CriteriaBuilder cb=em.getCriteriaBuilder();
				CriteriaQuery<PricingProgram> cq=cb.createQuery(PricingProgram.class);
				Root<PricingProgram> root= cq.from(PricingProgram.class);
				Predicate con=cb.conjunction();
				con.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("program_code")),cb.literal("đ"),cb.literal("d")), "%"+text+"%"));
				con.getExpressions().add(cb.isFalse(root.get("disable")));
				cq.select(cb.construct(PricingProgram.class,root.get("id"),root.get("program_code"),root.get("effective_date"),root.get("expiry_date"))).where(con);
				TypedQuery<PricingProgram> query=em.createQuery(cq);
				list.addAll(query.getResultList());
				res=0;
		    }
			res=0;
		}catch(Exception e){
			logger.error("PricingProgramService.complete:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, PricingProgramReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PricingProgram> cq=cb.createQuery(PricingProgram.class);
			Root<PricingProgram> root= cq.from(PricingProgram.class);
			cq.select(root).where(cb.equal(root.get("program_code"), code));
			TypedQuery<PricingProgram> query=em.createQuery(cq);
			t.setPricing_program(query.getSingleResult());
			res=0;
		}catch(Exception e){
//			logger.error("PricingProgramService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}
}
