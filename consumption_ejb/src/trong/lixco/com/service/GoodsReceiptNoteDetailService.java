package trong.lixco.com.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
import javax.resource.spi.TransactionSupport.TransactionSupportLevel;

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.entity.Batch;
import lixco.com.entity.GoodsReceiptNoteDetail;
import lixco.com.interfaces.IGoodsReceiptNoteDetailService;
import lixco.com.reqInfo.GoodsReceiptNoteDetailReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class GoodsReceiptNoteDetailService implements IGoodsReceiptNoteDetailService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;

	@Override
	public int search(String json, PagingInfo page, List<GoodsReceiptNoteDetail> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{
			/*{goods_receipt_note_detail:{goods_receipt_note_id:0,product_id:0,batch_code:''},page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hGoodsReceiptNoteId=JsonParserUtil.getValueNumber(j.get("goods_receipt_note_detail"),"goods_receipt_note_id", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j.get("goods_receipt_note_detail"),"product_id", null);
			HolderParser hBatchCode=JsonParserUtil.getValueString(j.get("goods_receipt_note_detail"),"batch_code", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<GoodsReceiptNoteDetail> cq=cb.createQuery(GoodsReceiptNoteDetail.class);
			Root<GoodsReceiptNoteDetail> root_= cq.from(GoodsReceiptNoteDetail.class);
			root_.fetch("product",JoinType.INNER);
			root_.fetch("goods_receipt_note",JoinType.INNER);
			Join<GoodsReceiptNoteDetail,Batch> batch_=(Join)root_.fetch("batch",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Long> pGoodsReceiptNoteId=cb.parameter(Long.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<String> pBatchCode=cb.parameter(String.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pGoodsReceiptNoteId, 0));
			dis.getExpressions().add(cb.equal(root_.get("goods_receipt_note").get("id"), pGoodsReceiptNoteId));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pProductId, 0));
			dis1.getExpressions().add(cb.equal(root_.get("product").get("id"),pProductId));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pBatchCode, ""));
			dis2.getExpressions().add(cb.equal(batch_.get("batch_code"), pBatchCode));
			predicates.add(dis2);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<GoodsReceiptNoteDetail> query=em.createQuery(cq);
			query.setParameter(pGoodsReceiptNoteId, Long.parseLong(Objects.toString(hGoodsReceiptNoteId.getValue(), "0")));
			query.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue(), "0")));
			query.setParameter(pBatchCode, Objects.toString(hBatchCode.getValue(), ""));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(GoodsReceiptNoteDetail.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pGoodsReceiptNoteId, Long.parseLong(Objects.toString(hGoodsReceiptNoteId.getValue(), "0")));
			query2.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue(), "0")));
			query2.setParameter(pBatchCode, Objects.toString(hBatchCode.getValue(), ""));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch(Exception e){
			logger.error("GoodsReceiptNoteDetailService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(GoodsReceiptNoteDetailReqInfo t) {
		int res=-1;
		try{
			GoodsReceiptNoteDetail p=t.getGoods_receipt_note_detail();
			if(p !=null){
				//check sản phẩm đã tồn tại chưa.
				if(checkExsist(p.getProduct().getId(), p.getGoods_receipt_note().getId())==0){
					em.persist(p);
					selectById(p.getId(), t);
					res=0;
				}else{
					res=-2;//duplicate
				}
				
			}
		}catch(Exception e){
			logger.error("GoodsReceiptNoteDetailService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public int update(GoodsReceiptNoteDetailReqInfo t) {
		int res=-1;
		try{
			GoodsReceiptNoteDetail p=t.getGoods_receipt_note_detail();
			if(p !=null){
				 p=em.merge(p);
				 if(p !=null){
					 selectById(p.getId(), t);
					 res=0;
				 }
			}
		}catch(Exception e){
			logger.error("GoodsReceiptNoteDetailService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, GoodsReceiptNoteDetailReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<GoodsReceiptNoteDetail> cq=cb.createQuery(GoodsReceiptNoteDetail.class);
			Root<GoodsReceiptNoteDetail> root_= cq.from(GoodsReceiptNoteDetail.class);
			root_.fetch("product",JoinType.INNER);
			root_.fetch("goods_receipt_note",JoinType.INNER);
			root_.fetch("batch",JoinType.LEFT);
			cq.select(root_).where(cb.equal(root_.get("id"), id));
			TypedQuery<GoodsReceiptNoteDetail> query=em.createQuery(cq);
			t.setGoods_receipt_note_detail(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("GoodsReceiptNoteDetailService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JPQL
			Query query=em.createQuery("delete from GoodsReceiptNoteDetail where id=:id");
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("GoodsReceiptNoteDetailService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, List<GoodsReceiptNoteDetail> list) {
		return 0;
	}

	private int checkExsist(long productId,long receiptNoteId) {
		int res=-1;
		try{
			//JPQl
			Query query=em.createQuery("select count(id) from GoodsReceiptNoteDetail where product.id=:idp and goods_receipt_note.id= :idg ");
			return  Integer.parseInt(Objects.toString(query.getSingleResult(),"0"));
			
		}catch(Exception e){
			logger.error("GoodsReceiptNoteDetailService.checkExsist:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByReceiptNote(long receiptId, List<GoodsReceiptNoteDetail> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<GoodsReceiptNoteDetail> cq=cb.createQuery(GoodsReceiptNoteDetail.class);
			Root<GoodsReceiptNoteDetail> root_= cq.from(GoodsReceiptNoteDetail.class);
			root_.fetch("product",JoinType.INNER);
			root_.fetch("goods_receipt_note",JoinType.INNER);
			root_.fetch("batch",JoinType.LEFT);
			cq.select(root_).where(cb.equal(root_.get("goods_receipt_note").get("id"), receiptId));
			TypedQuery<GoodsReceiptNoteDetail> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("GoodsReceiptNoteDetailService.selectByReceiptNote:"+e.getMessage(),e);
		}
		return res;
	}

}
