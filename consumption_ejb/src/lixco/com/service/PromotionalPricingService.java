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
import lixco.com.entity.PromotionalPricing;
import lixco.com.interfaces.IPromotionalPricingService;
import lixco.com.reqInfo.PromotionalPricingReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class PromotionalPricingService implements IPromotionalPricingService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int search(String json, PagingInfo page, List<PromotionalPricing> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{promotional_pricing_info:{product_id:0,from_date:'',to_date:''}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j.get("promotional_pricing_info"), "product_id", null);
			HolderParser hFromDate=JsonParserUtil.getValueString(j.get("promotional_pricing_info"), "from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j.get("promotional_pricing_info"),"to_date",null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionalPricing> cq=cb.createQuery(PromotionalPricing.class);
			Root<PromotionalPricing> root_= cq.from(PromotionalPricing.class);
			root_.fetch("product",JoinType.INNER);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pFromDate));
			dis.getExpressions().add(cb.equal(pFromDate,""));
			dis.getExpressions().add(cb.greaterThanOrEqualTo(root_.get("effective_date"), pFromDate));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pToDate));
			dis1.getExpressions().add(cb.equal(pToDate,""));
			dis1.getExpressions().add(cb.lessThanOrEqualTo(root_.get("expiry_date"),pToDate));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pProductId, 0));
			dis2.getExpressions().add(cb.equal(root_.get("product").get("id"), pProductId));
			predicates.add(dis2);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<PromotionalPricing> query=em.createQuery(cq);
			query.setParameter(pFromDate, ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(),null),"dd/MM/yyyy"));
			query.setParameter(pToDate, ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(),null),"dd/MM/yyyy"));
			query.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue(),"0")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(PromotionalPricing.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pFromDate, ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(),null),"dd/MM/yyyy"));
			query2.setParameter(pToDate, ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(),null),"dd/MM/yyyy"));
			query2.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue(),"0")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch(Exception e){
			logger.error("PromotionalPricingService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(PromotionalPricingReqInfo t) {
		int res=-1;
		try{
			PromotionalPricing p=t.getPromotional_pricing();
			if(p !=null){
				if(checkExsits(p)==0){
					em.persist(p);
					if(p.getId()>0){
						res=0;
					}
				}else{
					//đã tồn tại sản phẩm
					res=-2;
				}
			}
		}catch(Exception e){
			logger.error("PromotionalPricingService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(PromotionalPricingReqInfo t) {
		int res=-1;
		try{
			PromotionalPricing p=t.getPromotional_pricing();
			if(p !=null){
				if(checkExsits(p)==0){
					p=em.merge(p);
					if(p !=null){
					   selectById(p.getId(), t);
					   res=0;
					}
				}else{
					res=-2;
				}
			}
		}catch(Exception e){
			logger.error("PromotionalPricingService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, PromotionalPricingReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionalPricing> cq=cb.createQuery(PromotionalPricing.class);
			Root<PromotionalPricing> root_= cq.from(PromotionalPricing.class);
			root_.fetch("product",JoinType.INNER);
			cq.select(root_).where(cb.equal(root_.get("id"), id));
			TypedQuery<PromotionalPricing> query=em.createQuery(cq);
			t.setPromotional_pricing(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("PromotionalPricingService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from PromotionalPricing where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("PromotionalPricingService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkExsits(PromotionalPricing p) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<PromotionalPricing> root= cq.from(PromotionalPricing.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pId=cb.parameter(Long.class);
			ParameterExpression<Date> pEff=cb.parameter(Date.class);
			ParameterExpression<Date> pExp=cb.parameter(Date.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pId, 0));
			dis.getExpressions().add(cb.notEqual(root.get("id"), pId));
			Predicate con =cb.conjunction();
			con.getExpressions().add(dis);
			con.getExpressions().add(cb.equal(root.get("product").get("id"),pProductId));
			con.getExpressions().add(cb.equal(root.get("effective_date"), pEff));
			con.getExpressions().add(cb.equal(root.get("expiry_date"), pExp));
			cq.select(cb.count(root.get("id"))).where(con);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pId, p.getId());
			query.setParameter(pProductId, p.getProduct().getId());
			query.setParameter(pEff, p.getEffective_date());
			query.setParameter(pExp, p.getExpiry_date());
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("PromotionalPricingService.checkExsits:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteAll() {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from PromotionalPricing ");
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("PromotionalPricingService.deleteAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int findSettingPromotionalPricing(String json, PromotionalPricingReqInfo t) {
		int res=-1;
		try{/*order_date:'',product_id:0}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hOrderDate=JsonParserUtil.getValueString(j, "order_date", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j, "product_id", null);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionalPricing> cq=cb.createQuery(PromotionalPricing.class);
			Root<PromotionalPricing> root_= cq.from(PromotionalPricing.class);
			ParameterExpression<Date> pOrderDate=cb.parameter(Date.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			Predicate con=cb.conjunction();
			con.getExpressions().add(cb.lessThanOrEqualTo(root_.get("effective_date"), pOrderDate));
			con.getExpressions().add(cb.greaterThanOrEqualTo(root_.get("expiry_date"), pOrderDate));
			con.getExpressions().add(cb.equal(root_.get("product").get("id"), pProductId));
			cq.select(root_).where(con).orderBy(cb.desc(root_.get("effective_date")));
			TypedQuery<PromotionalPricing> query=em.createQuery(cq);
			query.setParameter(pOrderDate, ToolTimeCustomer.convertStringToDate(Objects.toString(hOrderDate.getValue(),null),"dd/MM/yyyy"));
			query.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue(),"0")));
			List<PromotionalPricing> list=query.getResultList();
			if(list.size()>0){
				t.setPromotional_pricing(list.get(0));
			}
			res=0;
		}catch(Exception e){
			logger.error("PromotionalPricingService.findSettingPromotionalPricing:"+e.getMessage(),e);
		}
		return res;
	}

}
