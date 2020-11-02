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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.entity.PricingProgram;
import lixco.com.entity.PricingProgramDetail;
import lixco.com.entity.Product;
import lixco.com.interfaces.IPricingProgramDetailService;
import lixco.com.reqInfo.PricingProgramDetailReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class PricingProgramDetailService implements IPricingProgramDetailService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAllByPricingProgram(long program_id,List<PricingProgramDetail> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PricingProgramDetail> cq=cb.createQuery(PricingProgramDetail.class);
			Root<PricingProgramDetail> root= cq.from(PricingProgramDetail.class);
			cq.select(root).where(cb.equal(root.get("pricing_program").get("id"), program_id));
			TypedQuery<PricingProgramDetail> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("PricingProgramDetailService.selectAllByPricingProgram:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json, PagingInfo page, List<PricingProgramDetail> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ pricing_program_detail_info:{program_id:0,program_code:'',product_id:0}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hProgramId=JsonParserUtil.getValueNumber(j.get("pricing_program_detail_info"),"program_id", null);
			HolderParser hProductCode=JsonParserUtil.getValueString(j.get("pricing_program_detail_info"),"program_code",null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j.get("pricing_program_detail_info"),"product_id",null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PricingProgramDetail> cq=cb.createQuery(PricingProgramDetail.class);
			Root<PricingProgramDetail> root_= cq.from(PricingProgramDetail.class);
			Join<PricingProgramDetail, PricingProgram> pricingProgram_=root_.join("pricing_program",JoinType.LEFT);
			root_.fetch("product",JoinType.INNER).fetch("promotion_product",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Long> pProgramId=cb.parameter(Long.class);
			ParameterExpression<String> pProgramCode=cb.parameter(String.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pProgramId, 0));
			dis.getExpressions().add(cb.equal(root_.get("pricing_program").get("id"), pProgramId));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pProgramCode));
			dis1.getExpressions().add(cb.equal(pProgramCode, ""));
			dis1.getExpressions().add(cb.equal(pricingProgram_.get("program_code"),pProgramCode));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pProductId, 0));
			dis2.getExpressions().add(cb.equal(root_.get("product").get("id"), pProductId));
			predicates.add(dis2);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root_.get("created_date")));
			TypedQuery<PricingProgramDetail> query=em.createQuery(cq);
			query.setParameter(pProgramId, Long.parseLong(Objects.toString(hProgramId.getValue(),"0")));
			query.setParameter(pProgramCode, Objects.toString(hProductCode.getValue(), null));
			query.setParameter(pProductId,Long.parseLong(Objects.toString(hProductId.getValue(),"0")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(PricingProgramDetail.class);
			pricingProgram_=root_.join("pricing_program",JoinType.LEFT);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pProgramId, Long.parseLong(Objects.toString(hProgramId.getValue(),"0")));
			query2.setParameter(pProgramCode, Objects.toString(hProductCode.getValue(), null));
			query2.setParameter(pProductId,Long.parseLong(Objects.toString(hProductId.getValue(),"0")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch(Exception e){
			logger.error("PricingProgramDetailService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(PricingProgramDetailReqInfo t) {
		int res=-1;
		try{
			PricingProgramDetail p=t.getPricing_program_detail();
			if(p !=null){
				Product product=p.getProduct();
				PricingProgram program=p.getPricing_program();
				int check=checkExsits(product !=null ? product.getId() :0,p.getId(), program !=null ? program.getId() :0);
				res=check;
				if(check==0){
					em.persist(p);
					if(p.getId()>0){
						res=0;
					}
				}
			}
		}catch(Exception e){
			logger.error("PricingProgramDetailService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(PricingProgramDetailReqInfo t) {
		int res=-1;
		try{
			PricingProgramDetail p=t.getPricing_program_detail();
			if(p !=null){
				Product product=p.getProduct();
				PricingProgram program=p.getPricing_program();
				int check=checkExsits(product !=null ? product.getId() :0,p.getId(), program !=null ? program.getId() :0);
				res=check;
				if(check==0){
					p=em.merge(p);
					if(p !=null){
					   t.setPricing_program_detail(p);;
					   res=0;
					}
				}
			}
		}catch(Exception e){
			logger.error("PricingProgramDetailService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, PricingProgramDetailReqInfo t) {
		int res=-1;
		try{
			PricingProgramDetail p=em.find(PricingProgramDetail.class,id);
			if(p!=null){
				t.setPricing_program_detail(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("PricingProgramDetailService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from PricingProgramDetail where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("PricingProgramDetailService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkExsits(long productId, long id, long programId) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<PricingProgramDetail> root= cq.from(PricingProgramDetail.class);
			ParameterExpression<Long> pId=cb.parameter(Long.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pProgramId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pId, 0));
			con1.getExpressions().add(cb.equal(root.get("pricing_program").get("id"),pProgramId));
			con1.getExpressions().add(cb.equal(root.get("product").get("id"),pProductId));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pId));
			con2.getExpressions().add(cb.equal(root.get("pricing_program").get("id"),pProgramId));
			con2.getExpressions().add(cb.equal(root.get("product").get("id"),pProductId));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pId, id);
			query.setParameter(pProductId, productId);
			query.setParameter(pProgramId, programId);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("PricingProgramDetailService.checkExist:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteAll(long programId) {
		int res=-1;
		try{
			//JPQL
			Query query=em.createQuery("delete from PricingProgramDetail where pricing_program.id =:id ");
			query.setParameter("id",programId);
			res =query.executeUpdate();
		}catch(Exception e){
			logger.error("PricingProgramDetailService.deleteAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int updateByPredicate(long productId, long programId,double unitPrice, double revenue,double quantity) {
		int res=-1;
		try{
			StringBuilder sql=new StringBuilder();
			sql.append("update PricingProgramDetail set quantity=:q,revenue_per_ton=:r,unit_price=:u where product_id=:pid and pricing_program.id =:ppi ");
			Query query=em.createQuery(sql.toString());
			query.setParameter("q",quantity);
			query.setParameter("r", revenue);
			query.setParameter("u", unitPrice);
			query.setParameter("pid", productId);
			query.setParameter("ppi", programId);
			return query.executeUpdate();
		}catch(Exception e){
			logger.error("PricingProgramDetailService.updateByPredicate:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int findSettingPricing(long programId, long productId, PricingProgramDetailReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PricingProgramDetail> cq=cb.createQuery(PricingProgramDetail.class);
			Root<PricingProgramDetail> root= cq.from(PricingProgramDetail.class);
			Predicate con=cb.conjunction();
			con.getExpressions().add(cb.equal(root.get("pricing_program").get("id"), programId));
			con.getExpressions().add(cb.equal(root.get("product").get("id"), productId));
			cq.select(root).where(con);
			TypedQuery<PricingProgramDetail> query=em.createQuery(cq);
			t.setPricing_program_detail(query.getSingleResult());
			res=0;
		}catch(Exception e){
//			logger.error("PricingProgramDetailService.findSettingPricing:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int findSettingPricingChild(long parentProgramId, long productId, PricingProgramDetailReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PricingProgramDetail> cq=cb.createQuery(PricingProgramDetail.class);
			Root<PricingProgramDetail> root= cq.from(PricingProgramDetail.class);
			Join<PricingProgramDetail,PricingProgram> pricingPro_=root.join("pricing_program",JoinType.INNER);
			Predicate con=cb.conjunction();
			con.getExpressions().add(cb.equal(pricingPro_.get("parent_pricing_program").get("id"), parentProgramId));
			con.getExpressions().add(cb.equal(root.get("product").get("id"), productId));
			cq.select(root).where(con).orderBy(cb.desc(pricingPro_.get("effective_date")));
			TypedQuery<PricingProgramDetail> query=em.createQuery(cq);
			List<PricingProgramDetail> list=query.getResultList();
			if(list.size()>0){
				t.setPricing_program_detail(list.get(0));
			}
			res=0;
		}catch(Exception e){
//			logger.error("PricingProgramDetailService.findSettingPricing:"+e.getMessage(),e);
		}
		return res;
	}

}
