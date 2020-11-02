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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import lixco.com.entity.Pos;
import lixco.com.entity.Warehouse;
import lixco.com.interfaces.IPosService;
import lixco.com.reqInfo.PosReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class PosService implements IPosService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int insert(PosReqInfo t) {
		int res=-1;
		try{
			Pos p=t.getPos();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("PosService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(PosReqInfo t) {
		int res=-1;
		try{
			Pos p=t.getPos();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setPos(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("PosService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from Pos where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("PosService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByFloor(long floorId, List<Pos> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Pos> cq=cb.createQuery(Pos.class);
			Root<Pos> root= cq.from(Pos.class);
			cq.select(root).where(cb.equal(root.get("floor").get("id"),floorId));
			TypedQuery<Pos> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("PosService.selectByFloor:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, PosReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Pos> cq=cb.createQuery(Pos.class);
			Root<Pos> root= cq.from(Pos.class);
			cq.select(root).where(cb.equal(root.get("id"),id));
			TypedQuery<Pos> query=em.createQuery(cq);
			t.setPos(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("PosService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByWarehouse(String warehouseCode, String pos_code, PosReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Pos> cq=cb.createQuery(Pos.class);
			Root<Pos> root= cq.from(Pos.class);
			Join<Pos, Warehouse> warehouse_= (Join)root.fetch("warehouse",JoinType.INNER);
			cq.select(root).where(cb.and(cb.equal(warehouse_.get("code"),warehouseCode),cb.equal(root.get("pos_code"), pos_code)));
			TypedQuery<Pos> query=em.createQuery(cq);
			t.setPos(query.getSingleResult());
			res=0;
		}catch (Exception e) {
			logger.error("PosService.selectByWarehouse:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectRowStack(long warehouseId, List<Integer> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<Pos> root= cq.from(Pos.class);
			cq.select(root.get("row_stack")).distinct(true).where(cb.equal(root.get("warehouse").get("id"),warehouseId)).orderBy(cb.asc(root.get("row_stack")));
			TypedQuery<Integer> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("PosService.selectRowStack:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByRowStack(int rowStack, long warehouseId, List<Pos> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Pos> cq=cb.createQuery(Pos.class);
			Root<Pos> root= cq.from(Pos.class);
			cq.select(root).where(cb.and(cb.equal(root.get("warehouse").get("id"),warehouseId),cb.equal(root.get("row_stack"), rowStack)))
			.orderBy(cb.asc(root.get("row_stack")),cb.asc(root.get("floorb")));
			TypedQuery<Pos> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("PosService.selectByRowStack:"+e.getMessage(),e);
		}
		return res;
	}

}
