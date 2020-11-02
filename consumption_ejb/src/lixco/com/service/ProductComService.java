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

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.entity.ProductCom;
import lixco.com.interfaces.IProductComService;
import lixco.com.reqInfo.ProductComReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class ProductComService implements IProductComService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<ProductCom> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductCom> cq=cb.createQuery(ProductCom.class);
			Root<ProductCom> root= cq.from(ProductCom.class);
			cq.select(root);
			TypedQuery<ProductCom> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ProductComService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json, PagingInfo page, List<ProductCom> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ pcom_info:{pcom_code:'',pcom_name:'',product_brand_id:0}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hPComCode=JsonParserUtil.getValueString(j.get("pcom_info"), "pcom_code", null);
			HolderParser hPComName=JsonParserUtil.getValueString(j.get("pcom_info"), "pcom_name", null);
			HolderParser hproductBrandId=JsonParserUtil.getValueNumber(j.get("pcom_info"),"product_brand_id",null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductCom> cq=cb.createQuery(ProductCom.class);
			Root<ProductCom> root_= cq.from(ProductCom.class);
			root_.fetch("product_brand",JoinType.INNER);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<String> pPComCode=cb.parameter(String.class);
			ParameterExpression<String> pPComName=cb.parameter(String.class);
			ParameterExpression<String> pPComNameLike=cb.parameter(String.class);
			ParameterExpression<Long> pProductBrandId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pPComCode));
			dis.getExpressions().add(cb.equal(pPComCode,""));
			dis.getExpressions().add(cb.equal(root_.get("pcom_code"), pPComCode));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pPComName));
			dis1.getExpressions().add(cb.equal(pPComName,""));
			dis1.getExpressions().add(cb.like(root_.get("pcom_name"), pPComNameLike));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pProductBrandId,0));
			dis2.getExpressions().add(cb.equal(root_.get("product_brand").get("id"),pProductBrandId));
			predicates.add(dis2);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<ProductCom> query=em.createQuery(cq);
			query.setParameter(pPComCode, Objects.toString(hPComCode.getValue(),null));
			query.setParameter(pPComName, Objects.toString(hPComName.getValue(), null));
			query.setParameter(pPComNameLike, "%"+Objects.toString(hPComName.getValue(), null)+"%");
			query.setParameter(pProductBrandId, Long.parseLong(Objects.toString(hproductBrandId.getValue(), "0")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(ProductCom.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pPComCode, Objects.toString(hPComCode.getValue(),null));
			query2.setParameter(pPComName, Objects.toString(hPComName.getValue(), null));
			query2.setParameter(pPComNameLike, "%"+Objects.toString(hPComName.getValue(), null)+"%");
			query2.setParameter(pProductBrandId, Long.parseLong(Objects.toString(hproductBrandId.getValue(), "0")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
			
		}catch(Exception e){
			logger.error("ProductComService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(ProductComReqInfo t) {
		int res=-1;
		try{
			ProductCom p=t.getProduct_com();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("ProductComService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(ProductComReqInfo t) {
		int res=-1;
		try{
			ProductCom p=t.getProduct_com();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setProduct_com(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("ProductComService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, ProductComReqInfo t) {
		int res=-1;
		try{
			ProductCom p=em.find(ProductCom.class,id);
			if(p!=null){
				t.setProduct_com(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("ProductComService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from ProductCom where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("ProductComService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int findLike(String text, int pageSize, List<ProductCom> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductCom> cq=cb.createQuery(ProductCom.class);
			Root<ProductCom> root= cq.from(ProductCom.class);
			ParameterExpression<String> paramLike=cb.parameter(String.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.like(root.get("pcom_code"),paramLike));
			dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("pcom_name")),cb.literal("đ"),cb.literal("d")), paramLike));
			cq.select(root).where(dis);
			TypedQuery<ProductCom> query=em.createQuery(cq);
			query.setParameter(paramLike, "%"+text+"%");
			if(pageSize!=-1){
				query.setFirstResult(0);
				query.setMaxResults(pageSize);
			}
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ProductComService.findLike:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkProductComCode(String code, long pComId) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<ProductCom> root= cq.from(ProductCom.class);
			ParameterExpression<String> pPComCode=cb.parameter(String.class);
			ParameterExpression<Long> pPComId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pPComId, 0));
			con1.getExpressions().add(cb.equal(root.get("pcom_code"),pPComCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pPComId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pPComId));
			con2.getExpressions().add(cb.equal(root.get("pcom_code"),pPComCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pPComId, pComId);
			query.setParameter(pPComCode, code);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("ProductComService.checkProductComCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, ProductComReqInfo t) {
		int res=-1;
		try {
			if(code !=null && !"".equals(code)) {
				CriteriaBuilder cb=em.getCriteriaBuilder();
				CriteriaQuery<ProductCom> cq=cb.createQuery(ProductCom.class);
				Root<ProductCom> root= cq.from(ProductCom.class);
				cq.select(root).where(cb.equal(root.get("pcom_code"), code));
				TypedQuery<ProductCom> query=em.createQuery(cq);
				t.setProduct_com(query.getSingleResult());
				res=0;
			}
		}catch(Exception e) {
			logger.error("ProductComService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

}
