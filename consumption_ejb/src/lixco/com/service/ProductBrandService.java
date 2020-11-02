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
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.entity.ProductBrand;
import lixco.com.interfaces.IProductBrandService;
import lixco.com.reqInfo.ProductBrandReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class ProductBrandService implements IProductBrandService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<ProductBrand> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductBrand> cq=cb.createQuery(ProductBrand.class);
			Root<ProductBrand> root= cq.from(ProductBrand.class);
			cq.select(root);
			TypedQuery<ProductBrand> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ProductBrandService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json, PagingInfo page, List<ProductBrand> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ pbrand_info:{pbrand_code:'',pbrand_name:''}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hPBrandCode=JsonParserUtil.getValueString(j.get("pbrand_info"), "pbrand_code", null);
			HolderParser hPBrandName=JsonParserUtil.getValueString(j.get("pbrand_info"), "pbrand_name", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductBrand> cq=cb.createQuery(ProductBrand.class);
			Root<ProductBrand> root_= cq.from(ProductBrand.class);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<String> pPBrandCode=cb.parameter(String.class);
			ParameterExpression<String> pPBrandName=cb.parameter(String.class);
			ParameterExpression<String> pPBrandNameLike=cb.parameter(String.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pPBrandCode));
			dis.getExpressions().add(cb.equal(pPBrandCode,""));
			dis.getExpressions().add(cb.equal(root_.get("pbrand_code"), pPBrandCode));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pPBrandName));
			dis1.getExpressions().add(cb.equal(pPBrandName,""));
			dis1.getExpressions().add(cb.like(root_.get("pbrand_name"),pPBrandNameLike));
			predicates.add(dis1);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<ProductBrand> query=em.createQuery(cq);
			query.setParameter(pPBrandCode, Objects.toString(hPBrandCode.getValue(),null));
			query.setParameter(pPBrandName, Objects.toString(hPBrandName.getValue(), null));
			query.setParameter(pPBrandNameLike, "%"+Objects.toString(hPBrandName.getValue(), null)+"%");
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(ProductBrand.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pPBrandCode, Objects.toString(hPBrandCode.getValue(),null));
			query2.setParameter(pPBrandName, Objects.toString(hPBrandName.getValue(), null));
			query2.setParameter(pPBrandNameLike, "%"+Objects.toString(hPBrandName.getValue(), null)+"%");
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
			
		}catch(Exception e){
			logger.error("ProductBrandService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(ProductBrandReqInfo t) {
		int res=-1;
		try{
			ProductBrand p=t.getProduct_brand();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("ProductBrandService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(ProductBrandReqInfo t) {
		int res=-1;
		try{
			ProductBrand p=t.getProduct_brand();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setProduct_brand(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("ProductBrandService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, ProductBrandReqInfo t) {
		int res=-1;
		try{
			ProductBrand p=em.find(ProductBrand.class,id);
			if(p!=null){
				t.setProduct_brand(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("ProductBrandService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from ProductBrand where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("ProductBrandService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int findLike(String text, int size, List<ProductBrand> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductBrand> cq=cb.createQuery(ProductBrand.class);
			Root<ProductBrand> root= cq.from(ProductBrand.class);
			ParameterExpression<String> paramLike=cb.parameter(String.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.like(root.get("pbrand_code"),paramLike));
			dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("pbrand_name")),cb.literal("đ"),cb.literal("d")), paramLike));
			cq.select(root).where(dis);
			TypedQuery<ProductBrand> query=em.createQuery(cq);
			query.setParameter(paramLike, "%"+text+"%");
			if(size!=-1){
				query.setFirstResult(0);
				query.setMaxResults(size);
			}
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ProductBrandService.findLike:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkProductBrandCode(String code, long productBrandId) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<ProductBrand> root= cq.from(ProductBrand.class);
			ParameterExpression<String> pCode=cb.parameter(String.class);
			ParameterExpression<Long> pProductBrandId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pProductBrandId, 0));
			con1.getExpressions().add(cb.equal(root.get("pbrand_code"),pCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pProductBrandId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pProductBrandId));
			con2.getExpressions().add(cb.equal(root.get("pbrand_name"),pCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pProductBrandId, productBrandId);
			query.setParameter(pCode, code);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("ProductBrandService.checkProductBrandCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, ProductBrandReqInfo t) {
		int res=-1;
		try {
			if(code !=null) {
				CriteriaBuilder cb=em.getCriteriaBuilder();
				CriteriaQuery<ProductBrand> cq=cb.createQuery(ProductBrand.class);
				Root<ProductBrand> root= cq.from(ProductBrand.class);
				cq.select(root).where(cb.equal(root.get("pbrand_code"), code));
				TypedQuery<ProductBrand> query=em.createQuery(cq);
				t.setProduct_brand(query.getSingleResult());
				res=0;
			}
		}catch(Exception e) {
			logger.error("ProductBrandService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

}
