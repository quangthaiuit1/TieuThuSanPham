package lixco.com.service;

import java.util.ArrayList;
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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Hibernate;
import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.entity.Product;
import lixco.com.entity.PromotionProductGroup;
import lixco.com.interfaces.IProductService;
import lixco.com.reqInfo.ProductReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class ProductService implements IProductService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<Product> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Product> cq=cb.createQuery(Product.class);
			Root<Product> root= cq.from(Product.class);
			cq.select(root);
			TypedQuery<Product> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ProductService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json, PagingInfo page, List<Product> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ product_info:{product_code:'',product_name:'',lever_code:'',product_com_id:0}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hProductCode=JsonParserUtil.getValueString(j.get("product_info"), "product_code", null);
			HolderParser hProductName=JsonParserUtil.getValueString(j.get("product_info"), "product_name", null);
			HolderParser hLeverCode=JsonParserUtil.getValueString(j.get("product_info"),"lever_code",null);
			HolderParser hProductComId=JsonParserUtil.getValueNumber(j.get("product_info"),"product_com_id",null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Product> cq=cb.createQuery(Product.class);
			Root<Product> root_= cq.from(Product.class);
			root_.fetch("product_com",JoinType.LEFT);
			root_.fetch("product_type",JoinType.LEFT);
			root_.fetch("product_group",JoinType.LEFT);
			root_.fetch("promotion_product_group",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<String> pProductCode=cb.parameter(String.class);
			ParameterExpression<String> pProductName=cb.parameter(String.class);
			ParameterExpression<String> pProductNameLike=cb.parameter(String.class);
			ParameterExpression<String> pLeverCode=cb.parameter(String.class);
			ParameterExpression<Long> pProductComId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pProductCode));
			dis.getExpressions().add(cb.equal(pProductCode,""));
			dis.getExpressions().add(cb.equal(root_.get("product_code"), pProductCode));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pProductName));
			dis1.getExpressions().add(cb.equal(pProductName,""));
			dis1.getExpressions().add(cb.like(root_.get("product_name"),pProductNameLike));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.isNull(pLeverCode));
			dis2.getExpressions().add(cb.equal(pLeverCode,""));
			dis2.getExpressions().add(cb.equal(root_.get("lever_code"),pLeverCode));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pProductComId, 0));
			dis3.getExpressions().add(cb.equal(root_.get("product_com").get("id"), pProductComId));
			predicates.add(dis3);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Product> query=em.createQuery(cq);
			query.setParameter(pProductCode, Objects.toString(hProductCode.getValue(),null));
			query.setParameter(pProductName, Objects.toString(hProductName.getValue(), null));
			query.setParameter(pProductNameLike, "%"+Objects.toString(hProductName.getValue(), null)+"%");
			query.setParameter(pLeverCode, Objects.toString(hLeverCode.getValue(),null));
			query.setParameter(pProductComId, Long.parseLong(Objects.toString(hProductComId.getValue(),"0")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(Product.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pProductCode, Objects.toString(hProductCode.getValue(),null));
			query2.setParameter(pProductName, Objects.toString(hProductName.getValue(), null));
			query2.setParameter(pProductNameLike, "%"+Objects.toString(hProductName.getValue(), null)+"%");
			query2.setParameter(pLeverCode, Objects.toString(hLeverCode.getValue(),null));
			query2.setParameter(pProductComId, Long.parseLong(Objects.toString(hProductComId.getValue(),"0")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch(Exception e){
			logger.error("ProductService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(ProductReqInfo t) {
		int res=-1;
		try{
			Product p=t.getProduct();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("ProductService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(ProductReqInfo t) {
		int res=-1;
		try{
			Product p=t.getProduct();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
					selectById(p.getId(), t);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("ProductService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, ProductReqInfo t) {
		int res=-1;
		try{
			Product p=em.find(Product.class,id);
			if(p!=null){
				Hibernate.initialize(p.getProduct_com());
				Hibernate.initialize(p.getProduct_group());
				Hibernate.initialize(p.getProduct_type());
				Hibernate.initialize(p.getPromotion_product());
				Hibernate.initialize(p.getPromotion_product_group());
				t.setProduct(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("ProductService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from Product where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("ProductService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int findLike(String text, int size, List<Product> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Product> cq=cb.createQuery(Product.class);
			Root<Product> root= cq.from(Product.class);
			ParameterExpression<String> paramLike=cb.parameter(String.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.like(root.get("product_code"),paramLike));
			dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("product_name")),cb.literal("đ"),cb.literal("d")), paramLike));
			cq.select(root).where(dis);
			TypedQuery<Product> query=em.createQuery(cq);
			query.setParameter(paramLike, "%"+text+"%");
			if(size!=-1){
				query.setFirstResult(0);
				query.setMaxResults(size);
			}
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ProductService.findLike:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkProductCode(String code,long productId) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<Product> root= cq.from(Product.class);
			ParameterExpression<String> pProductCode=cb.parameter(String.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pProductId, 0));
			con1.getExpressions().add(cb.equal(root.get("product_code"),pProductCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pProductId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pProductId));
			con2.getExpressions().add(cb.equal(root.get("product_code"),pProductCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pProductId, productId);
			query.setParameter(pProductCode, code);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("ProductService.checkProductCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int complete(String text,List<Product> list) {
		int res=-1;
		try{
			if(text !=null && !"".equals(text)){
				CriteriaBuilder cb=em.getCriteriaBuilder();
				CriteriaQuery<Product> cq=cb.createQuery(Product.class);
				Root<Product> root= cq.from(Product.class);
				Predicate dis=cb.disjunction();
				dis.getExpressions().add(cb.equal(root.get("product_code"), text));
				dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("product_name")),cb.literal("đ"),cb.literal("d")), "%"+text+"%"));
				cq.select(cb.construct(Product.class, root.get("id"),root.get("product_code"),root.get("product_name"))).where(dis);
				TypedQuery<Product> query=em.createQuery(cq);
				list.addAll(query.getResultList());
				
			}
			res=0;
		}catch(Exception e){
			logger.error("ProductService.complete:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, ProductReqInfo reqInfo) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Product> cq=cb.createQuery(Product.class);
			Root<Product> root= cq.from(Product.class);
			cq.select(root).where(cb.equal(root.get("product_code"), code));
			TypedQuery<Product> query=em.createQuery(cq);
			reqInfo.setProduct(query.getSingleResult());
			res=0;
		}catch (Exception e) {
			logger.error("ProductService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int complete2(String text, List<Product> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Product> cq=cb.createQuery(Product.class);
			Root<Product> root= cq.from(Product.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(root.get("product_code"), text));
			dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("product_name")),cb.literal("đ"),cb.literal("d")), "%"+text+"%"));
			cq.select(cb.construct(Product.class, root.get("id"),root.get("product_code"),root.get("product_name"),root.get("box_quantity"))).where(dis);
			TypedQuery<Product> query=em.createQuery(cq);
			list.addAll(query.getResultList());
		}catch (Exception e) {
			logger.error("ProductService.complete2:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int complete3(String text, List<Product> list) {
		int res=-1;
		try{
//			(long id, String product_code, String product_name, double specification, double box_quantity,
//					double factor, double tare
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Product> cq=cb.createQuery(Product.class);
			Root<Product> root= cq.from(Product.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(root.get("product_code"), text));
			dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("product_name")),cb.literal("đ"),cb.literal("d")), "%"+text+"%"));
			cq.select(cb.construct(Product.class, root.get("id"),root.get("product_code"),root.get("product_name"),root.get("specification"),
					root.get("box_quantity"),root.get("factor"),root.get("tare"))).where(dis);
			TypedQuery<Product> query=em.createQuery(cq);
			list.addAll(query.getResultList());
		}catch (Exception e) {
			logger.error("ProductService.complete3:"+e.getMessage(),e);
		}
		return res;
	}

}
