package lixco.com.service;

import java.util.ArrayList;
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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.entity.DeliveryPricing;
import lixco.com.interfaces.IDeliveryPricingService;
import lixco.com.reqInfo.DeliveryPricingReqInfo;

@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class DeliveryPricingService implements IDeliveryPricingService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<DeliveryPricing> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<DeliveryPricing> cq=cb.createQuery(DeliveryPricing.class);
			Root<DeliveryPricing> root= cq.from(DeliveryPricing.class);
			cq.select(root);
			TypedQuery<DeliveryPricing> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("DeliveryPricingService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(DeliveryPricingReqInfo t) {
		int res=-1;
		try{
			DeliveryPricing p=t.getDelivery_pricing();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("DeliveryPricingService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(DeliveryPricingReqInfo t) {
		int res=-1;
		try{
			DeliveryPricing p=t.getDelivery_pricing();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   selectById(p.getId(), t);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("DeliveryPricingService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, DeliveryPricingReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<DeliveryPricing> cq=cb.createQuery(DeliveryPricing.class);
			Root<DeliveryPricing> root= cq.from(DeliveryPricing.class);
			root.fetch("customer",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("id"), id));
			TypedQuery<DeliveryPricing> query=em.createQuery(cq);
			t.setDelivery_pricing(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("DeliveryPricingService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from DeliveryPricing where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("DeliveryPricingService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json, PagingInfo page, List<DeliveryPricing> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ delivery_pricing_info:{customer_id:0,place_code:'',disable:-1}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j.get("delivery_pricing_info"), "customer_id", null);
			HolderParser hPlaceCode=JsonParserUtil.getValueString(j.get("delivery_pricing_info"),"place_code", null);
			HolderParser hDisable=JsonParserUtil.getValueNumber(j.get("delivery_pricing_info"),"disable", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"), "page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<DeliveryPricing> cq=cb.createQuery(DeliveryPricing.class);
			Root<DeliveryPricing> root_= cq.from(DeliveryPricing.class);
			root_.fetch("customer",JoinType.INNER);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<String> pPlaceCode=cb.parameter(String.class);
			ParameterExpression<Integer> pDisable=cb.parameter(Integer.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pCustomerId, 0));
			dis.getExpressions().add(cb.equal(root_.get("customer").get("id"), pCustomerId));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pPlaceCode, ""));
			dis1.getExpressions().add(cb.equal(root_.get("place_code"), pPlaceCode));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pDisable, -1));
			dis2.getExpressions().add(cb.equal(root_.get("disable"), pDisable));
			predicates.add(dis2);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<DeliveryPricing> query=em.createQuery(cq);
			query.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query.setParameter(pPlaceCode, Objects.toString(hPlaceCode.getValue(), ""));
			query.setParameter(pDisable,Integer.parseInt(Objects.toString(hDisable.getValue(),"-1")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(DeliveryPricing.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query2.setParameter(pPlaceCode, Objects.toString(hPlaceCode.getValue(), ""));
			query2.setParameter(pDisable,Integer.parseInt(Objects.toString(hDisable.getValue(),"-1")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch(Exception e){
			logger.error("DeliveryPricingService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public String initPlaceCode() {
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<DeliveryPricing> root= cq.from(DeliveryPricing.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)root.get("place_code"),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/1000;
			if(p<1){
				return String.format("%03d", max+1);
			}
			return (max+1)+"";
		}catch(Exception e){
			logger.error("DeliveryPricingService.initOrderCode:"+e.getMessage(),e);
		}
		return null;
	}

	@Override
	public int selectByPlaceCode(String placeCode, DeliveryPricingReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<DeliveryPricing> cq=cb.createQuery(DeliveryPricing.class);
			Root<DeliveryPricing> root_= cq.from(DeliveryPricing.class);
			root_.fetch("customer",JoinType.INNER);
			cq.select(root_).where(cb.equal(root_.get("place_code"), placeCode));
			TypedQuery<DeliveryPricing> query=em.createQuery(cq);
			t.setDelivery_pricing(query.getSingleResult());
			res=0;
		}catch(Exception e){
//			logger.error("DeliveryPricingService.selectByPlaceCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkCode(String code,long id) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<DeliveryPricing> root= cq.from(DeliveryPricing.class);
			ParameterExpression<String> pCode=cb.parameter(String.class);
			ParameterExpression<Long> pId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pId, 0));
			con1.getExpressions().add(cb.equal(root.get("place_code"),pCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pId));
			con2.getExpressions().add(cb.equal(root.get("place_code"),pCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pId, id);
			query.setParameter(pCode, code);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("DeliveryPricingService.checkCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int seletcBy(String json, List<DeliveryPricing> list) {
		int res=-1;
		try{
			/*{place_code:'',customer_id:0}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hPlaceCode=JsonParserUtil.getValueString(j, "place_code", null);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j,"customer_id", null);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<DeliveryPricing> cq=cb.createQuery(DeliveryPricing.class);
			Root<DeliveryPricing> root= cq.from(DeliveryPricing.class);
			ParameterExpression<String> pPlaceCode=cb.parameter(String.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			List<Predicate> predicates=new ArrayList<>();
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pPlaceCode, ""));
			dis.getExpressions().add(cb.equal(root.get("place_code"), pPlaceCode));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pCustomerId,0));
			dis1.getExpressions().add(cb.equal(root.get("customer").get("id"),pCustomerId));
			predicates.add(dis1);
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<DeliveryPricing> query=em.createQuery(cq);
			query.setParameter(pPlaceCode, Objects.toString(hPlaceCode.getValue(),""));
			query.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(),"0")));
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("DeliveryPricingService.seletcBy:"+e.getMessage(),e);
		}
		return res;
	}

}
