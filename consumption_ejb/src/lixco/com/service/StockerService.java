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

import lixco.com.entity.Carrier;
import lixco.com.entity.Stocker;
import lixco.com.interfaces.IStockerService;
import lixco.com.reqInfo.StockerReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class StockerService implements IStockerService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<Stocker> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Stocker> cq=cb.createQuery(Stocker.class);
			Root<Stocker> root= cq.from(Stocker.class);
			cq.select(root);
			TypedQuery<Stocker> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("StockerService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(StockerReqInfo t) {
		int res=-1;
		try{
			Stocker p=t.getStocker();
			if(p !=null){
				p.setStocker_code(initCode());
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("StockerService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(StockerReqInfo t) {
		int res=-1;
		try{
			Stocker p=t.getStocker();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   selectById(p.getId(), t);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("StockerService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, StockerReqInfo t) {
		int res=-1;
		try{
			Stocker stocker=em.find(Stocker.class, id);
			t.setStocker(stocker);
			res=0;
		}catch(Exception e){
			logger.error("StockerService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from Stocker where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("StockerService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String text,int disable, List<Stocker> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Stocker> cq=cb.createQuery(Stocker.class);
			Root<Stocker> root= cq.from(Stocker.class);
			ParameterExpression<String> pText=cb.parameter(String.class);
			ParameterExpression<Integer> pDis=cb.parameter(Integer.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pText));
			dis.getExpressions().add(cb.equal(root.get("stocker_code"), pText));
			dis.getExpressions().add(cb.like(root.get("stocker_name"), "%"+text+"%"));
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pDis, -1));
			dis1.getExpressions().add(cb.equal(root.get("disable"), pDis));
			
			cq.select(root).where(cb.and(dis,dis1));
			TypedQuery<Stocker> query=em.createQuery(cq);
			query.setParameter(pText, text);
			query.setParameter(pDis,disable);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("StockerService.search:"+e.getMessage(),e);
		}
		return res;
	}
	private String initCode() {
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<Stocker> root= cq.from(Stocker.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)root.get("stocker_code"),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/100;
			if(p<1){
				return String.format("%02d", max+1);
			}
			return ""+max+1;
		}catch(Exception e){
//			logger.error("StockerService.initCode:"+e.getMessage(),e);
		}
		return null;
	}

	@Override
	public int selectByCode(String code, StockerReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Stocker> cq=cb.createQuery(Stocker.class);
			Root<Stocker> root= cq.from(Stocker.class);
			cq.select(root).where(cb.equal(root.get("stocker_code"), code));
			TypedQuery<Stocker> query=em.createQuery(cq);
			t.setStocker(query.getSingleResult());
			res=0;
		}catch(Exception e){
//			logger.error("StockerService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

}
