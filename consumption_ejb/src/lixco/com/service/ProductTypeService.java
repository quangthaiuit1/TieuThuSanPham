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

import lixco.com.entity.City;
import lixco.com.entity.ProductType;
import lixco.com.interfaces.IProductTypeService;
import lixco.com.reqInfo.ProductTypeReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class ProductTypeService implements IProductTypeService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<ProductType> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductType> cq=cb.createQuery(ProductType.class);
			Root<ProductType> root= cq.from(ProductType.class);
			cq.select(root);
			TypedQuery<ProductType> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ProductTypeService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(ProductTypeReqInfo t) {
		int res=-1;
		try{
			ProductType p=t.getProduct_type();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("ProductTypeService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(ProductTypeReqInfo t) {
		int res=-1;
		try{
			ProductType p=t.getProduct_type();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setProduct_type(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("ProductTypeService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, ProductTypeReqInfo t) {
		int res=-1;
		try{
			ProductType p=em.find(ProductType.class,id);
			if(p!=null){
				t.setProduct_type(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("ProductTypeService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from ProductType where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("ProductTypeService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, ProductTypeReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductType> cq=cb.createQuery(ProductType.class);
			Root<ProductType> root= cq.from(ProductType.class);
			cq.select(root).where(cb.equal(root.get("code"), code));
			TypedQuery<ProductType> query=em.createQuery(cq);
			t.setProduct_type(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("ProductTypeService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkCode(String code, long id) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<City> root= cq.from(City.class);
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
			logger.error("ProductTypeService.checkCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String code, String name, List<ProductType> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductType> cq=cb.createQuery(ProductType.class);
			Root<ProductType> root= cq.from(ProductType.class);
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
			TypedQuery<ProductType> query=em.createQuery(cq);
			query.setParameter(pcode, code);
			query.setParameter(pname, name);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ProductTypeService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectBy(String json, List<ProductType> list) {
		try{
		}catch (Exception e) {
			logger.error("ProductTypeService.selectBy:"+e.getMessage(),e);
		}
		return 0;
	}

}
