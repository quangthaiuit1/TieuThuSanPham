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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.entity.OrderDetail;
import lixco.com.entity.PromotionOrderDetail;
import lixco.com.interfaces.IPromotionOrderDetailService;
import lixco.com.reqInfo.PromotionOrderDetailReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class PromotionOrderDetailService implements IPromotionOrderDetailService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectByOrder(long orderId, List<PromotionOrderDetail> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionOrderDetail> cq=cb.createQuery(PromotionOrderDetail.class);
			Root<PromotionOrderDetail> root= cq.from(PromotionOrderDetail.class);
			root.fetch("product",JoinType.INNER).fetch("promotion_product_group",JoinType.LEFT);
			Join<PromotionOrderDetail,OrderDetail> orderDetail_= (Join)root.fetch("order_detail",JoinType.INNER);
			orderDetail_.fetch("product",JoinType.INNER);
			cq.select(root).where(cb.equal(orderDetail_.get("order_lix").get("id"), orderId));
			TypedQuery<PromotionOrderDetail> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("PromotionOrderDetailService.selectByOrder:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int insert(PromotionOrderDetailReqInfo t) {
		int res=-1;
		try{
			PromotionOrderDetail p=t.getPromotion_order_detail();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("PromotionOrderDetailService.insert:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int update(PromotionOrderDetailReqInfo t) {
		int res=-1;
		try{
			PromotionOrderDetail p=t.getPromotion_order_detail();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   selectById(p.getId(), t);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("PromotionOrderDetailService.update:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectById(long id, PromotionOrderDetailReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionOrderDetail> cq=cb.createQuery(PromotionOrderDetail.class);
			Root<PromotionOrderDetail> root= cq.from(PromotionOrderDetail.class);
			Join<PromotionOrderDetail,OrderDetail> orderDetail_=(Join) root.fetch("order_detail",JoinType.INNER);
			orderDetail_.fetch("product",JoinType.INNER);
			root.fetch("product",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("id"), id));
			TypedQuery<PromotionOrderDetail> query=em.createQuery(cq);
			t.setPromotion_order_detail(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("PromotionOrderDetailService.selectById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from PromotionOrderDetail where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("PromotionOrderDetailService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int deleteAll(long orderId) {
		int res=-1;
		try{
			//JPQL
			Query query=em.createQuery("delete from PromotionOrderDetail as p inner join p.order_detail as dt where dt.order_lix.id =:id ");
			query.setParameter("id",orderId);
			res =query.executeUpdate();
		}catch(Exception e){
			logger.error("PromotionOrderDetailService.deleteAll:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectBy(String json, List<PromotionOrderDetail> list) {
		int res=-1;
		try{
			/*{product_id:0,order_lix_id:0,order_product_id:0}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json,JsonObject.class);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j,"product_id", null);
			HolderParser hOrderLixId=JsonParserUtil.getValueNumber(j,"order_lix_id", null);
			HolderParser hOrderProductId=JsonParserUtil.getValueNumber(j,"order_product_id", null);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionOrderDetail> cq=cb.createQuery(PromotionOrderDetail.class);
			Root<PromotionOrderDetail> root= cq.from(PromotionOrderDetail.class);
			Join<PromotionOrderDetail,OrderDetail> orderDetail_=(Join) root.fetch("order_detail",JoinType.INNER);
			orderDetail_.fetch("product",JoinType.INNER);
			root.fetch("product",JoinType.INNER);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pOrderLixId=cb.parameter(Long.class);
			ParameterExpression<Long> pOrderProductId=cb.parameter(Long.class);
			List<Predicate>  predicates=new ArrayList<Predicate>();
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pProductId, 0));
			dis.getExpressions().add(cb.equal(root.get("product").get("id"), pProductId));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pOrderLixId, 0));
			dis1.getExpressions().add(cb.equal(orderDetail_.get("order_lix").get("id"),pOrderLixId));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pOrderProductId, 0));
			dis2.getExpressions().add(cb.equal(orderDetail_.get("product").get("id"), pOrderProductId));
			predicates.add(dis2);
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<PromotionOrderDetail> query=em.createQuery(cq);
			query.setParameter(pProductId,Long.parseLong(Objects.toString(hProductId.getValue(),"0")));
			query.setParameter(pOrderLixId,Long.parseLong(Objects.toString(hOrderLixId.getValue(),"0")));
			query.setParameter(pOrderProductId,Long.parseLong(Objects.toString(hOrderProductId.getValue(),"0")));
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("PromotionOrderDetailService.selectBy:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int deleteBy(long orderDetailPosId) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from PromotionOrderDetail as p where p.order_detail.id=:id ");
			query.setParameter("id",orderDetailPosId);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("PromotionOrderDetailService.deleteBy:"+e.getMessage(),e);
		}
		return res;
	}
}
