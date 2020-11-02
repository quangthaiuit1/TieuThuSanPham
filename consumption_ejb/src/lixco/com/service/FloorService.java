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

import lixco.com.entity.Floor;
import lixco.com.interfaces.IFloorService;
import lixco.com.reqInfo.FloorReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class FloorService implements IFloorService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int insert(FloorReqInfo t) {
		int res=-1;
		try{
			Floor p=t.getFloor();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("FloorService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(FloorReqInfo t) {
		int res=-1;
		try{
			Floor p=t.getFloor();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setFloor(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("FloorService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from Floor where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("FloorService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, FloorReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Floor> cq=cb.createQuery(Floor.class);
			Root<Floor> root= cq.from(Floor.class);
			cq.select(root).where(cb.equal(root.get("id"),id));
			TypedQuery<Floor> query=em.createQuery(cq);
			t.setFloor(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("FloorService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByRow(long rowId, List<Floor> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Floor> cq=cb.createQuery(Floor.class);
			Root<Floor> root= cq.from(Floor.class);
			cq.select(root).where(cb.equal(root.get("row_stack").get("id"),rowId));
			TypedQuery<Floor> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("FloorService.selectByRow:"+e.getMessage(),e);
		}
		return res;
	}

}
