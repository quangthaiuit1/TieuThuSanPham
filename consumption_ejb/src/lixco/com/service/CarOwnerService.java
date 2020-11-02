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
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import lixco.com.entity.CarOwner;
import lixco.com.interfaces.ICarOwnerService;
import lixco.com.reqInfo.CarOwnerReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class CarOwnerService implements ICarOwnerService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<CarOwner> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<CarOwner> cq=cb.createQuery(CarOwner.class);
			Root<CarOwner> root= cq.from(CarOwner.class);
			cq.select(root);
			TypedQuery<CarOwner> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("CarOwnerService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(CarOwnerReqInfo t) {
		int res=-1;
		try{
			CarOwner p = t.getCar_owner();
			if (p != null) {
				if (checkCode(p.getCode(), p.getId()) == 0) {
					em.persist(p);
					if (p.getId() > 0) {
						res = 0;
					}
				}else{
					res=-2;//duplicate code
				}
			}
		}catch(Exception e){
			logger.error("CarOwnerService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(CarOwnerReqInfo t) {
		int res=-1;
		try{
			CarOwner p=t.getCar_owner();
			if(p !=null){
				if (checkCode(p.getCode(), p.getId())==0) {
					p = em.merge(p);
					if (p != null) {
						t.setCar_owner(p);
						res = 0;
					}
				} else {
					res=-2;//duplicate code
				}
			}
		}catch(Exception e){
			logger.error("CarOwnerService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, CarOwnerReqInfo t) {
		int res=-1;
		try{
			CarOwner p=em.find(CarOwner.class,id);
			if(p!=null){
				t.setCar_owner(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("CarOwnerService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from CarOwner where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("CarOwnerService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public String initCode() {
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<CarOwner> root= cq.from(CarOwner.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)cb.substring(root.get("code"),3),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/100;
			if(p<1){
				return "CO"+String.format("%02d", max+1);
			}
			return "CO"+max+1;
		}catch(Exception e){
//			logger.error("CarOwnerService.initCode:"+e.getMessage(),e);
		}
		return null;
	}

	@Override
	public int checkCode(String code,long id) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<CarOwner> root= cq.from(CarOwner.class);
			ParameterExpression<String> pCode=cb.parameter(String.class);
			ParameterExpression<Long> pId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pId, 0));
			con1.getExpressions().add(cb.equal(root.get("code"),pCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pId));
			con2.getExpressions().add(cb.equal(root.get("code"),pCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pId, id);
			query.setParameter(pCode, code);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("CarOwnerService.checkCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCodeOld(String code, CarOwnerReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<CarOwner> cq=cb.createQuery(CarOwner.class);
			Root<CarOwner> root= cq.from(CarOwner.class);
			cq.select(root).where(cb.equal(root.get("old_code"), code));
			TypedQuery<CarOwner> query=em.createQuery(cq);
			t.setCar_owner(query.getSingleResult());
			res=0;
		}catch(Exception e){
//			logger.error("CarOwnerService.selectByCodeOld:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, CarOwnerReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<CarOwner> cq=cb.createQuery(CarOwner.class);
			Root<CarOwner> root= cq.from(CarOwner.class);
			cq.select(root).where(cb.equal(root.get("code"), code));
			TypedQuery<CarOwner> query=em.createQuery(cq);
			t.setCar_owner(query.getSingleResult());
			res=0;
		}catch(Exception e){
//			logger.error("CarOwnerService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

}
