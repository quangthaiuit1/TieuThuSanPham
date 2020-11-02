package lixco.com.service;

import java.util.ArrayList;
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

import lixco.com.entity.ProductGroup;
import lixco.com.interfaces.IProductGroupService;
import lixco.com.reqInfo.ProductGroupReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class ProductGroupService implements IProductGroupService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<ProductGroup> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductGroup> cq=cb.createQuery(ProductGroup.class);
			Root<ProductGroup> root= cq.from(ProductGroup.class);
			cq.select(root);
			TypedQuery<ProductGroup> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ProductGroupService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(ProductGroupReqInfo t) {
		int res=-1;
		try{
			ProductGroup p=t.getProduct_Group();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("ProductGroupService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(ProductGroupReqInfo t) {
		int res=-1;
		try{
			ProductGroup p=t.getProduct_Group();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setProduct_Group(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("ProductGroupService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, ProductGroupReqInfo t) {
		int res=-1;
		try{
			ProductGroup p=em.find(ProductGroup.class,id);
			if(p!=null){
				t.setProduct_Group(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("ProductGroupService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from ProductGroup where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("ProductGroupService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, ProductGroupReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductGroup> cq=cb.createQuery(ProductGroup.class);
			Root<ProductGroup> root= cq.from(ProductGroup.class);
			cq.select(root).where(cb.equal(root.get("code"), code));
			TypedQuery<ProductGroup> query=em.createQuery(cq);
			t.setProduct_Group(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("ProductGroupService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkCode(String code, long id) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<ProductGroup> root= cq.from(ProductGroup.class);
			ParameterExpression<String> pcode=cb.parameter(String.class);
			ParameterExpression<Long> pId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pId, 0));
			con1.getExpressions().add(cb.equal(root.get("code"),pcode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pId));
			con2.getExpressions().add(cb.equal(root.get("code"),pcode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pId, id);
			query.setParameter(pcode, code);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("ProductGroupService.checkCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String code, String name, List<ProductGroup> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductGroup> cq=cb.createQuery(ProductGroup.class);
			Root<ProductGroup> root= cq.from(ProductGroup.class);
			ParameterExpression<String> pcode=cb.parameter(String.class);
			ParameterExpression<String> pname=cb.parameter(String.class);
			List<Predicate> predicates=new ArrayList<>();
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pcode));
			dis.getExpressions().add(cb.equal(root.get("code"),""));
			dis.getExpressions().add(cb.equal(root.get("code"),pcode));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pname));
			dis1.getExpressions().add(cb.equal(root.get("name"),""));
			dis1.getExpressions().add(cb.like(root.get("name"),"%"+name+"%"));
			predicates.add(dis1);
			cq.select(root).where(predicates.toArray(new Predicate[0]));
			TypedQuery<ProductGroup> query=em.createQuery(cq);
			query.setParameter(pcode, code);
			query.setParameter(pname, name);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ProductGroupService.search:"+e.getMessage(),e);
		}
		return res;
	}
}
