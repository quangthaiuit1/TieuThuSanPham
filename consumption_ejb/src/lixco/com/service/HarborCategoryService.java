package lixco.com.service;

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

import lixco.com.entity.HarborCategory;
import lixco.com.interfaces.IHarborCategoryService;
import lixco.com.reqInfo.HarborCategoryReqInfo;

@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class HarborCategoryService implements IHarborCategoryService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<HarborCategory> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<HarborCategory> cq=cb.createQuery(HarborCategory.class);
			Root<HarborCategory> root= cq.from(HarborCategory.class);
			cq.select(root);
			TypedQuery<HarborCategory> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("HarborCategoryService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(HarborCategoryReqInfo t) {
		int res=-1;
		try{
			HarborCategory p=t.getHarbor_category();
			if(p !=null){
				em.persist(p);
				em.flush();
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("HarborCategoryService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(HarborCategoryReqInfo t) {
		int res=-1;
		try{
			HarborCategory p=t.getHarbor_category();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   selectById(p.getId(), t);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("HarborCategoryService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, HarborCategoryReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<HarborCategory> cq=cb.createQuery(HarborCategory.class);
			Root<HarborCategory> root= cq.from(HarborCategory.class);
			root.fetch("country",JoinType.LEFT);
			cq.select(root).where(cb.equal(root.get("id"), id));
			TypedQuery<HarborCategory> query=em.createQuery(cq);
			t.setHarbor_category(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("HarborCategoryService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from HarborCategory where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("HarborCategoryService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(int harborType, List<HarborCategory> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<HarborCategory> cq=cb.createQuery(HarborCategory.class);
			Root<HarborCategory> root= cq.from(HarborCategory.class);
			ParameterExpression<Integer> pHarborType=cb.parameter(Integer.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pHarborType, -1));
			dis.getExpressions().add(cb.equal(root.get("harbor_type"), pHarborType));
			cq.select(root).where(dis);
			TypedQuery<HarborCategory> query=em.createQuery(cq);
			query.setParameter(pHarborType, Integer.parseInt(Objects.toString(harborType,"0")));
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("HarborCategoryService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int initCode(HarborCategory t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<HarborCategory> root= cq.from(HarborCategory.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)root.get("harbor_code"),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/10000;
			if(p<1){
				t.setHarbor_code(String.format("%04d", max+1));
			}else{
				t.setHarbor_code(max+1+"");
			}
		}catch(Exception e){
//			logger.error("HarborCategoryService.initCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, HarborCategoryReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<HarborCategory> cq=cb.createQuery(HarborCategory.class);
			Root<HarborCategory> root= cq.from(HarborCategory.class);
			cq.select(root).where(cb.equal(root.get("harbor_code_old"), code));
			TypedQuery<HarborCategory> query=em.createQuery(cq);
			t.setHarbor_category(query.getSingleResult());
			res=0;
		}catch(Exception e){
//			logger.error("HarborCategoryService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

}
