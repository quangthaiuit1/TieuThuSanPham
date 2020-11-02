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

import lixco.com.entity.RowStack;
import lixco.com.interfaces.IRowStackService;
import lixco.com.reqInfo.RowStackReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class RowStackService implements IRowStackService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int insert(RowStackReqInfo t) {
		int res=-1;
		try{
			RowStack p=t.getRow_stack();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("RowStackService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(RowStackReqInfo t) {
		int res=-1;
		try{
			RowStack p=t.getRow_stack();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setRow_stack(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("RowStackService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByWarehouse(long warehouseId, List<RowStack> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<RowStack> cq=cb.createQuery(RowStack.class);
			Root<RowStack> root= cq.from(RowStack.class);
			cq.select(root).where(cb.equal(root.get("warehouse").get("id"),warehouseId));
			TypedQuery<RowStack> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("RowStackService.selectByWarehouse:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, RowStackReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<RowStack> cq=cb.createQuery(RowStack.class);
			Root<RowStack> root= cq.from(RowStack.class);
			cq.select(root).where(cb.equal(root.get("id"),id));
			TypedQuery<RowStack> query=em.createQuery(cq);
			t.setRow_stack(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("RowStackService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from RowStack where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("RowStackService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

}
