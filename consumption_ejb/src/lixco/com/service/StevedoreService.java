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
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import lixco.com.entity.Stevedore;
import lixco.com.interfaces.IStevedoreService;
import lixco.com.reqInfo.StevedoreReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class StevedoreService implements IStevedoreService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<Stevedore> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Stevedore> cq=cb.createQuery(Stevedore.class);
			Root<Stevedore> root= cq.from(Stevedore.class);
			cq.select(root);
			TypedQuery<Stevedore> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("StevedoreService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(StevedoreReqInfo t) {
		int res=-1;
		try{
			Stevedore p=t.getStevedore();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setStevedore(p);
				   res=0;
				}
				
			}
		}catch(Exception e){
			logger.error("StevedoreService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(StevedoreReqInfo t) {
		int res=-1;
		try{
			Stevedore p=t.getStevedore();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setStevedore(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("StevedoreService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, StevedoreReqInfo t) {
		int res=-1;
		try{
			Stevedore p=em.find(Stevedore.class,id);
			if(p!=null){
				t.setStevedore(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("StevedoreService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from Stevedore where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("StevedoreService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

}
