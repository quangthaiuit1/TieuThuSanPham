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
import lixco.com.entity.PromotionProgram;
import lixco.com.entity.PromotionProgramDetail;
import lixco.com.interfaces.IPromotionProgramService;
import lixco.com.reqInfo.PromotionProgramReqInfo;

@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class PromotionProgramService implements IPromotionProgramService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<PromotionProgram> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionProgram> cq=cb.createQuery(PromotionProgram.class);
			Root<PromotionProgram> root= cq.from(PromotionProgram.class);
			cq.select(root);
			TypedQuery<PromotionProgram> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("PromotionProgramService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int search(String json, PagingInfo page, List<PromotionProgram> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ promotion_program_info:{from_date:'',to_date:'',program_code:'',product_id:0,promotion_product_id:0,disable:0}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hFromDate=JsonParserUtil.getValueString(j.get("promotion_program_info"),"from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j.get("promotion_program_info"),"to_date", null);
			HolderParser hProgramCode=JsonParserUtil.getValueString(j.get("promotion_program_info"),"program_code", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j.get("promotion_program_info"),"product_id", null);
			HolderParser hPromotionProductId=JsonParserUtil.getValueNumber(j.get("promotion_program_info"),"promotion_product_id", null);
			HolderParser hDisable=JsonParserUtil.getValueNumber(j.get("promotion_program_info"),"disable", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionProgram> cq=cb.createQuery(PromotionProgram.class);
			Root<PromotionProgram> root_= cq.from(PromotionProgram.class);
			Join<PromotionProgram, PromotionProgramDetail> programDetail_=root_.join("list_promotion_program_detail",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<String> pProgramCode=cb.parameter(String.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pPromotionProductId=cb.parameter(Long.class);
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
			dis3.getExpressions().add(cb.equal(programDetail_.get("product").get("id"), pProductId));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.equal(pPromotionProductId,0));
			dis4.getExpressions().add(cb.equal(programDetail_.get("promotion_product").get("id"),pPromotionProductId));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pDisable, -1));
			dis5.getExpressions().add(cb.equal(root_.get("disable"), pDisable));
			predicates.add(dis5);
			cq.select(root_).distinct(true).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root_.get("program_code")));
			TypedQuery<PromotionProgram> query=em.createQuery(cq);
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query.setParameter(pProgramCode,Objects.toString(hProgramCode.getValue(), null));
			query.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue(), null)));
			query.setParameter(pPromotionProductId, Long.parseLong(Objects.toString(hPromotionProductId.getValue(), null)));
			query.setParameter(pDisable, Integer.parseInt(Objects.toString(hDisable.getValue(), null)));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(PromotionProgram.class);
			programDetail_=root_.join("list_promotion_program_detail",JoinType.LEFT);
			cq1.select(cb.countDistinct(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query2.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query2.setParameter(pProgramCode,Objects.toString(hProgramCode.getValue(), null));
			query2.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue(), null)));
			query2.setParameter(pPromotionProductId, Long.parseLong(Objects.toString(hPromotionProductId.getValue(), null)));
			query2.setParameter(pDisable, Integer.parseInt(Objects.toString(hDisable.getValue(), null)));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
			
		}catch(Exception e){
			logger.error("PromotionProgramService.search:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int insert(PromotionProgramReqInfo t) {
		int res=-1;
		try{
			PromotionProgram p=t.getPromotion_program();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("PromotionProgramService.insert:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int update(PromotionProgramReqInfo t) {
		int res=-1;
		try{
			PromotionProgram p=t.getPromotion_program();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setPromotion_program(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("PromotionProgramService.update:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectById(long id, PromotionProgramReqInfo t) {
		int res=-1;
		try{
			PromotionProgram p=em.find(PromotionProgram.class,id);
			if(p!=null){
				t.setPromotion_program(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("PromotionProgramService.selectById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from PromotionProgram where id = :id");
			query.setParameter("id", id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("PromotionProgramService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public String initPromotionProgramCode() {
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<PromotionProgram> root= cq.from(PromotionProgram.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)cb.substring(root.get("program_code"),3),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/100000;
			if(p<1){
				return "CT"+String.format("%06d", max+1);
			}
			return "CT"+max+1;
		}catch(Exception e){
			logger.error("PromotionProgramService.initPromotionProgramCode:"+e.getMessage(),e);
		}
		return null;
	}
	@Override
	public int findLike(String text, int size, List<PromotionProgram> list) {
		int res=-1;
		try{
			
		}catch(Exception e){
			logger.error("PromotionProgramService.findLike:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int complete(String text, List<PromotionProgram> list) {
		int res=-1;
		try{
			if(text !=null && !"".equals(text)){
				CriteriaBuilder cb=em.getCriteriaBuilder();
				CriteriaQuery<PromotionProgram> cq=cb.createQuery(PromotionProgram.class);
				Root<PromotionProgram> root= cq.from(PromotionProgram.class);
				Predicate con=cb.conjunction();
				con.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("program_code")),cb.literal("đ"),cb.literal("d")), "%"+text+"%"));
				con.getExpressions().add(cb.isFalse(root.get("disable")));
				cq.select(cb.construct(PromotionProgram.class,root.get("id"),root.get("program_code"),root.get("effective_date"),root.get("expiry_date"))).where(con);
				TypedQuery<PromotionProgram> query=em.createQuery(cq);
				list.addAll(query.getResultList());
				res=0;
		    }
		}catch(Exception e){
			logger.error("PromotionProgramService.complete:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectByCode(String programCode, PromotionProgramReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionProgram> cq=cb.createQuery(PromotionProgram.class);
			Root<PromotionProgram> root= cq.from(PromotionProgram.class);
			cq.select(root).where(cb.equal(root.get("program_code"), programCode));
			TypedQuery<PromotionProgram> query=em.createQuery(cq);
			t.setPromotion_program(query.getSingleResult());
			res=0;
		}catch(Exception e){
//			logger.error("PromotionProgramService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}
}
