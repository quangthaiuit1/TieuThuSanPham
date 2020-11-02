package lixco.com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
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
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Batch;
import lixco.com.entity.GoodsReceiptNoteDetail;
import lixco.com.entity.GoodsReceiptNotePos;
import lixco.com.entity.GoodsReceiptNotePosDetail;
import lixco.com.entity.Pos;
import lixco.com.entity.ProductPos;
import lixco.com.interfaces.IGoodsReceiptNotePosService;
import lixco.com.reqInfo.GoodsReceiptNotePosReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class GoodsReceiptNotePosService implements IGoodsReceiptNotePosService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int search(String json, PagingInfo page, List<GoodsReceiptNotePos> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{
		   /*{ goods_receipt_note_pos:{from_date:'',to_date:'',customer_id:0,ie_categories_id:0,status:-1}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j.get("goods_receipt_note_pos"),"customer_id", null);
			HolderParser hFromDate=JsonParserUtil.getValueString(j.get("goods_receipt_note_pos"),"from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j.get("goods_receipt_note_pos"),"to_date" , null);
			HolderParser hIECategoriesId=JsonParserUtil.getValueNumber(j.get("goods_receipt_note_pos"),"ie_categories_id", null);
			HolderParser hStatus=JsonParserUtil.getValueNumber(j.get("goods_receipt_note_pos"),"status", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<GoodsReceiptNotePos> cq=cb.createQuery(GoodsReceiptNotePos.class);
			Root<GoodsReceiptNotePos> root_= cq.from(GoodsReceiptNotePos.class);
			root_.fetch("customer",JoinType.INNER);
			root_.fetch("ie_categories",JoinType.INNER);
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<Long> pIECategorieId=cb.parameter(Long.class);
			ParameterExpression<Integer> pStatus=cb.parameter(Integer.class);
			List<Predicate> predicates=new ArrayList<Predicate>();
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pFromDate));
			dis.getExpressions().add(cb.greaterThanOrEqualTo(root_.get("import_date"),pFromDate));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pToDate));
			dis1.getExpressions().add(cb.lessThanOrEqualTo(root_.get("import_date"), pToDate));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pCustomerId, 0));
			dis2.getExpressions().add(cb.equal(root_.get("customer").get("id"),pCustomerId));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pIECategorieId, 0));
			dis3.getExpressions().add(cb.equal(root_.get("ie_categories").get("id"), pIECategorieId));
			predicates.add(dis3);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pStatus, -1));
			dis5.getExpressions().add(cb.equal(root_.get("status"), pStatus));
			predicates.add(dis5);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root_.get("import_date")));
			TypedQuery<GoodsReceiptNotePos> query=em.createQuery(cq);
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy"));
			query.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query.setParameter(pIECategorieId,Long.parseLong(Objects.toString(hIECategoriesId.getValue(), "0")));
			query.setParameter(pStatus, Integer.parseInt(Objects.toString(hStatus.getValue(),"-1")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(GoodsReceiptNotePos.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy"));
			query2.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy"));
			query2.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query2.setParameter(pIECategorieId,Long.parseLong(Objects.toString(hIECategoriesId.getValue(), "0")));
			query2.setParameter(pStatus, Integer.parseInt(Objects.toString(hStatus.getValue(),"-1")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch (Exception e) {
			logger.error("GoodsReceiptNotePosService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int searchDetail(String json, PagingInfo page, List<GoodsReceiptNotePosDetail> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{
			/*{goods_receipt_note_pos_detail:{goods_receipt_note_id:0,product_id:0,batch_code:''},page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hGoodsReceiptNoteId=JsonParserUtil.getValueNumber(j.get("goods_receipt_note_pos_detail"),"goods_receipt_note_id", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j.get("goods_receipt_note_pos_detail"),"product_id", null);
			HolderParser hBatchCode=JsonParserUtil.getValueString(j.get("goods_receipt_note_pos_detail"),"batch_code", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<GoodsReceiptNotePosDetail> cq=cb.createQuery(GoodsReceiptNotePosDetail.class);
			Root<GoodsReceiptNotePosDetail> root_= cq.from(GoodsReceiptNotePosDetail.class);
			root_.fetch("product",JoinType.INNER);
			root_.fetch("goods_receipt_note_pos",JoinType.INNER);
			Join<GoodsReceiptNoteDetail,Batch> batch_=(Join)root_.fetch("batch",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Long> pGoodsReceiptNoteId=cb.parameter(Long.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<String> pBatchCode=cb.parameter(String.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pGoodsReceiptNoteId, 0));
			dis.getExpressions().add(cb.equal(root_.get("goods_receipt_note_pos").get("id"), pGoodsReceiptNoteId));
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
			TypedQuery<GoodsReceiptNotePosDetail> query=em.createQuery(cq);
			query.setParameter(pGoodsReceiptNoteId, Long.parseLong(Objects.toString(hGoodsReceiptNoteId.getValue(), "0")));
			query.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue(), "0")));
			query.setParameter(pBatchCode, Objects.toString(hBatchCode.getValue(), ""));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(GoodsReceiptNotePosDetail.class);
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
		}catch (Exception e) {
			logger.error("GoodsReceiptNotePosService.searchDetail:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectByReceiptNotePos(long receiptId, List<GoodsReceiptNotePosDetail> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<GoodsReceiptNotePosDetail> cq=cb.createQuery(GoodsReceiptNotePosDetail.class);
			Root<GoodsReceiptNotePosDetail> root_= cq.from(GoodsReceiptNotePosDetail.class);
			root_.fetch("product",JoinType.INNER);
			root_.fetch("goods_receipt_note_pos",JoinType.INNER);
			root_.fetch("batch_pos",JoinType.LEFT);
			cq.select(root_).where(cb.equal(root_.get("goods_receipt_note_pos").get("id"), receiptId)).orderBy(cb.desc(root_.get("created_date")));
			TypedQuery<GoodsReceiptNotePosDetail> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("GoodsReceiptNotePosService.selectByReceiptPosNote:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectListProductPos(long receiptDetailId, List<ProductPos> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductPos> cq=cb.createQuery(ProductPos.class);
			Root<ProductPos> root_= cq.from(ProductPos.class);
			Fetch<ProductPos,Pos> pos_=root_.fetch("pos",JoinType.INNER);
			pos_.fetch("warehouse",JoinType.INNER);
			cq.select(root_).where(cb.equal(root_.get("goods_receipt_note_pos_detail").get("id"), receiptDetailId));
			TypedQuery<ProductPos> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("GoodsReceiptNotePosService.selectListProductPos:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, GoodsReceiptNotePosReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<GoodsReceiptNotePos> cq=cb.createQuery(GoodsReceiptNotePos.class);
			Root<GoodsReceiptNotePos> root_= cq.from(GoodsReceiptNotePos.class);
			root_.fetch("customer",JoinType.INNER);
			root_.fetch("ie_categories",JoinType.INNER);
			cq.select(root_).where(cb.equal(root_.get("id"), id)).orderBy(cb.desc(root_.get("created_date")));
			TypedQuery<GoodsReceiptNotePos> query=em.createQuery(cq);
			t.setGoods_receipt_note_pos(query.getSingleResult());
			res=0;
		}catch (Exception e) {
			logger.error("GoodsReceiptNotePosService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

}
