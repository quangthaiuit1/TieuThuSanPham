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
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import lixco.com.entity.Area;
import lixco.com.interfaces.IAreaService;
import lixco.com.reqInfo.AreaReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class AreaService implements IAreaService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<Area> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Area> cq=cb.createQuery(Area.class);
			Root<Area> root= cq.from(Area.class);
			cq.select(root);
			TypedQuery<Area> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("AreaService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(AreaReqInfo t) {
		int res=-1;
		try{
			Area p=t.getArea();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch (Exception e) {
		}
		return res;
	}

	@Override
	public int update(AreaReqInfo t) {
		int res=-1;
		try{
			Area p=t.getArea();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setArea(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("AreaService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, AreaReqInfo t) {
		int res=-1;
		try{
			Area p=em.find(Area.class,id);
			if(p!=null){
				t.setArea(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("AreaService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from Area where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("AreaService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkAreaCode(String code, long areaId) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<Area> root= cq.from(Area.class);
			ParameterExpression<String> pCode=cb.parameter(String.class);
			ParameterExpression<Long> pAreaId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pAreaId, 0));
			con1.getExpressions().add(cb.equal(root.get("area_code"),pCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pAreaId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pAreaId));
			con2.getExpressions().add(cb.equal(root.get("area_code"),pCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pAreaId, areaId);
			query.setParameter(pCode, code);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("AreaService.checkCityCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, AreaReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Area> cq=cb.createQuery(Area.class);
			Root<Area> root= cq.from(Area.class);
			cq.select(root).where(cb.equal(root.get("area_code"),code));
			TypedQuery<Area> query=em.createQuery(cq);
			t.setArea(query.getSingleResult());
			res=0;
		}catch (Exception e) {
			logger.error("AreaService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

}
