package lixco.com.service;

import java.util.List;

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

import lixco.com.entity.OrderDetail;
import lixco.com.entity.OrderLix;
import lixco.com.entity.Product;
import lixco.com.interfaces.IOrderDetailService;
import lixco.com.reqInfo.OrderDetailReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class OrderDetailService implements IOrderDetailService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectByOrder(long orderId, List<OrderDetail> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<OrderDetail> cq=cb.createQuery(OrderDetail.class);
			Root<OrderDetail> root= cq.from(OrderDetail.class);
			root.fetch("product",JoinType.INNER);
			root.fetch("order_lix",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("order_lix").get("id"), orderId));
			TypedQuery<OrderDetail> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("OrderDetailService.selectByOrder:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int insert(OrderDetailReqInfo t) {
		int res=-1;
		try{
			OrderDetail p=t.getOrder_detail();
			if(p !=null){
				Product product=p.getProduct();
				OrderLix orderLix=p.getOrder_lix();
				int check=checkExsits(product !=null ? product.getId() :0,p.getId(), orderLix !=null ? orderLix.getId() :0);
				res=check;
				if(check==0){
					em.persist(p);
					if(p.getId()>0){
						res=0;
					}
				}else{
					res=-2;
				}
			}
		}catch(Exception e){
			logger.error("OrderDetailService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(OrderDetailReqInfo t) {
		int res=-1;
		try{
			OrderDetail p=t.getOrder_detail();
			if(p !=null){
				Product product=p.getProduct();
				OrderLix orderLix=p.getOrder_lix();
				int check=checkExsits(product !=null ? product.getId() :0,p.getId(), orderLix !=null ? orderLix.getId() :0);
				res=check;
				if(check==0){
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
			logger.error("OrderDetailService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, OrderDetailReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<OrderDetail> cq=cb.createQuery(OrderDetail.class);
			Root<OrderDetail> root= cq.from(OrderDetail.class);
			root.fetch("product",JoinType.INNER);
			root.fetch("order_lix",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("id"), id));
			TypedQuery<OrderDetail> query=em.createQuery(cq);
			t.setOrder_detail(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("OrderDetailService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from OrderDetail where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("OrderDetailService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkExsits(long productId, long id, long orderId) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<OrderDetail> root= cq.from(OrderDetail.class);
			ParameterExpression<Long> pId=cb.parameter(Long.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pOrderId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pId, 0));
			con1.getExpressions().add(cb.equal(root.get("order_lix").get("id"),pOrderId));
			con1.getExpressions().add(cb.equal(root.get("product").get("id"),pProductId));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pId));
			con2.getExpressions().add(cb.equal(root.get("order_lix").get("id"),pOrderId));
			con2.getExpressions().add(cb.equal(root.get("product").get("id"),pProductId));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pId, id);
			query.setParameter(pProductId, productId);
			query.setParameter(pOrderId, orderId);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("OrderDetailService.checkExist:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteAll(long orderId) {
		int res=-1;
		try{
			//JPQL
			Query query=em.createQuery("delete from OrderDetail where order_lix.id =:id ");
			query.setParameter("id",orderId);
			res =query.executeUpdate();
		}catch(Exception e){
			logger.error("OrderDetailService.deleteAll:"+e.getMessage(),e);
		}
		return res;
	}

}
