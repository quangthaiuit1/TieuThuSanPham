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
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.validator.HibernateValidator;
import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.entity.Product;
import lixco.com.entity.PromotionProgramDetail;
import lixco.com.interfaces.IPromotionProgramDetailService;
import lixco.com.reqInfo.PromotionProgramDetailReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class PromotionProgramDetailService implements IPromotionProgramDetailService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAllByPromotionProgram(long program_id, List<PromotionProgramDetail> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionProgramDetail> cq=cb.createQuery(PromotionProgramDetail.class);
			Root<PromotionProgramDetail> root= cq.from(PromotionProgramDetail.class);
			cq.select(root).where(cb.equal(root.get("promotion_program").get("id"), program_id));
			TypedQuery<PromotionProgramDetail> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("PromotionProgramDetailService.selectAllByPromotionProgram:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int search(String json, PagingInfo page, List<PromotionProgramDetail> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ promotion_program_detail_info:{program_id:0,product_id:0,promotion_product_id:0}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hProgramId=JsonParserUtil.getValueNumber(j.get("promotion_program_detail_info"),"program_id", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j.get("promotion_program_detail_info"),"product_id",null);
			HolderParser hPromotionProductId=JsonParserUtil.getValueNumber(j.get("promotion_program_detail_info"),"promotion_product_id",null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionProgramDetail> cq=cb.createQuery(PromotionProgramDetail.class);
			Root<PromotionProgramDetail> root_= cq.from(PromotionProgramDetail.class);
			Join<PromotionProgramDetail, Product> product_=(Join)root_.fetch("product",JoinType.INNER);
			Join<PromotionProgramDetail, Product> productPromotion_=(Join)root_.fetch("promotion_product",JoinType.INNER);
			root_.fetch("promotion_program",JoinType.INNER);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Long> pProgramId=cb.parameter(Long.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pPromotionProductId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pProgramId, 0));
			dis.getExpressions().add(cb.equal(root_.get("promotion_program").get("id"), pProgramId));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pProductId, 0));
			dis1.getExpressions().add(cb.equal(root_.get("product").get("id"), pProductId));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pPromotionProductId, 0));
			dis2.getExpressions().add(cb.equal(root_.get("promotion_product").get("id"), pPromotionProductId));
			predicates.add(dis2);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.asc(product_.get("product_code")),cb.asc(root_.get("promotion_form")),cb.asc(productPromotion_.get("product_code")));
			TypedQuery<PromotionProgramDetail> query=em.createQuery(cq);
			query.setParameter(pProgramId, Long.parseLong(Objects.toString(hProgramId.getValue(),"0")));
			query.setParameter(pProductId,Long.parseLong(Objects.toString(hProductId.getValue(),"0")));
			query.setParameter(pPromotionProductId,Long.parseLong(Objects.toString(hPromotionProductId.getValue(),"0")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(PromotionProgramDetail.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pProgramId, Long.parseLong(Objects.toString(hProgramId.getValue(),"0")));
			query2.setParameter(pProductId,Long.parseLong(Objects.toString(hProductId.getValue(),"0")));
			query2.setParameter(pPromotionProductId,Long.parseLong(Objects.toString(hPromotionProductId.getValue(),"0")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch(Exception e){
			logger.error("PromotionProgramDetailService.search:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int insert(PromotionProgramDetailReqInfo t) {
		int res=-1;
		try{
			PromotionProgramDetail p=t.getPromotion_program_detail();
			if(p !=null){
				Product product=p.getProduct();
				Product promotionProduct=p.getPromotion_product();
				int check=checkExist(product !=null ? product.getId() :0, promotionProduct !=null ? promotionProduct.getId() :0,p.getId(), p.getPromotion_program().getId(),p.getPromotion_form());
				res=check;
				if(check==0){
					em.persist(p);
					if(p.getId()>0){
						res=0;
					}
				}
			}
		}catch(Exception e){
			logger.error("PromotionProgramDetailService.insert:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int update(PromotionProgramDetailReqInfo t) {
		int res=-1;
		try{
			PromotionProgramDetail p=t.getPromotion_program_detail();
			if(p !=null){
				Product product=p.getProduct();
				Product promotionProduct=p.getPromotion_product();
//				(long productId,long promotionProductId,long id,long programId,int form)
				int check=checkExist(product !=null ? product.getId() :0, promotionProduct !=null ? promotionProduct.getId() :0,p.getId(), p.getPromotion_program().getId(),p.getPromotion_form());
				res=check;
				if(check==0){
					p=em.merge(p);
					if(p !=null){
					   t.setPromotion_program_detail(p);
					   res=0;
					}
				}
			}
		}catch(Exception e){
			logger.error("PromotionProgramDetailService.update:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectById(long id, PromotionProgramDetailReqInfo t) {
		int res=-1;
		try{
			PromotionProgramDetail p=em.find(PromotionProgramDetail.class,id);
			if(p != null){
				t.setPromotion_program_detail(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("PromotionProgramDetailService.selectById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from PromotionProgramDetail where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("PromotionProgramDetailService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}
	public int checkExist(long productId,long promotionProductId,long id,long programId,int form){
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<PromotionProgramDetail> root= cq.from(PromotionProgramDetail.class);
			ParameterExpression<Long> pId=cb.parameter(Long.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pPromotionProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pProgramId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pId, 0));
			con1.getExpressions().add(cb.equal(root.get("promotion_program").get("id"),pProgramId));
			con1.getExpressions().add(cb.equal(root.get("product").get("id"),pProductId));
			con1.getExpressions().add(cb.equal(root.get("promotion_product").get("id"),pPromotionProductId));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pId));
			con2.getExpressions().add(cb.equal(root.get("promotion_program").get("id"),pProgramId));
			con2.getExpressions().add(cb.equal(root.get("product").get("id"),pProductId));
			con2.getExpressions().add(cb.equal(root.get("promotion_product").get("id"),pPromotionProductId));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(cb.and(dis,cb.equal(root.get("promotion_form"), form)));
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pId, id);
			query.setParameter(pProductId, productId);
			query.setParameter(pPromotionProductId, promotionProductId);
			query.setParameter(pProgramId, programId);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("PromotionProgramDetailService.checkExist:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int deleteAll(long programId) {
		int res=-1;
		try{
			//JPQL
			Query query=em.createQuery("delete from PromotionProgramDetail where promotion_program.id =:id ");
			query.setParameter("id",programId);
			res =query.executeUpdate();
		}catch(Exception e){
			logger.error("PromotionProgramDetailService.deleteAll:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectBy(String json, List<PromotionProgramDetail> list) {
		int res=-1;
		/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
		try{
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j,"product_id", null);
			HolderParser hPromotionProductId=JsonParserUtil.getValueNumber(j,"promotion_product_id", null);
			HolderParser hPromotionProgramId=JsonParserUtil.getValueNumber(j,"promotion_program_id", null);
			HolderParser hPromotionForm=JsonParserUtil.getValueNumber(j,"promotion_form", null);
			List<Predicate> predicates=new ArrayList<>();
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionProgramDetail> cq=cb.createQuery(PromotionProgramDetail.class);
			Root<PromotionProgramDetail> root_= cq.from(PromotionProgramDetail.class);
			root_.fetch("product",JoinType.INNER);
			Fetch<PromotionProgramDetail, Product> productp_=root_.fetch("promotion_product",JoinType.INNER);
			productp_.fetch("promotion_product_group",JoinType.LEFT);
			root_.fetch("promotion_program",JoinType.INNER);
			ParameterExpression<Long> pProgramId=cb.parameter(Long.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pPromotionProductId=cb.parameter(Long.class);
			ParameterExpression<Integer> pPromotionForm=cb.parameter(Integer.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pProductId, 0));
			dis.getExpressions().add(cb.equal(root_.get("product").get("id"),pProductId));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pPromotionProductId, 0));
			dis1.getExpressions().add(cb.equal(root_.get("promotion_product").get("id"),pPromotionProductId));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pProgramId, 0));
			dis2.getExpressions().add(cb.equal(root_.get("promotion_program").get("id"),pProgramId));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pPromotionForm, 0));
			dis3.getExpressions().add(cb.equal(root_.get("promotion_form"),pPromotionForm));
			predicates.add(dis3);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<PromotionProgramDetail> query=em.createQuery(cq);
			query.setParameter(pProgramId, Long.parseLong(Objects.toString(hPromotionProgramId.getValue(),"0")));
			query.setParameter(pProductId,Long.parseLong(Objects.toString(hProductId.getValue(),"0")));
			query.setParameter(pPromotionProductId,Long.parseLong(Objects.toString(hPromotionProductId.getValue(),"0")));
			query.setParameter(pPromotionForm,Integer.parseInt(Objects.toString(hPromotionForm.getValue(),"0")));
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("PromotionProgramDetailService.selectBy:"+e.getMessage(),e);
		}
		return res;
	}
	
}
