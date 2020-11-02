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
import lixco.com.entity.GoodsReceiptNote;
import lixco.com.interfaces.IGoodsReceiptNoteService;
import lixco.com.reqInfo.GoodsReceiptNoteReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class GoodsReceiptNoteService implements IGoodsReceiptNoteService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int search(String json, PagingInfo page, List<GoodsReceiptNote> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{
			/*{ goods_receipt_note:{from_date:'',to_date:'',customer_id:0,ie_categories_id:0,warehouse_id:0,status:-1}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j.get("goods_receipt_note"),"customer_id", null);
			HolderParser hFromDate=JsonParserUtil.getValueString(j.get("goods_receipt_note"),"from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j.get("goods_receipt_note"),"to_date" , null);
			HolderParser hIECategoriesId=JsonParserUtil.getValueNumber(j.get("goods_receipt_note"),"ie_categories_id", null);
			HolderParser hWarehouseId=JsonParserUtil.getValueNumber(j.get("goods_receipt_note"),"warehouse_id", null);
			HolderParser hStatus=JsonParserUtil.getValueNumber(j.get("goods_receipt_note"),"status", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<GoodsReceiptNote> cq=cb.createQuery(GoodsReceiptNote.class);
			Root<GoodsReceiptNote> root_= cq.from(GoodsReceiptNote.class);
			root_.fetch("customer",JoinType.INNER);
			root_.fetch("ie_categories",JoinType.INNER);
			root_.fetch("warehouse",JoinType.LEFT);
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<Long> pIECategorieId=cb.parameter(Long.class);
			ParameterExpression<Long> pWarehouseId=cb.parameter(Long.class);
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
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.equal(pWarehouseId, 0));
			dis4.getExpressions().add(cb.equal(root_.get("warehouse").get("id"), pWarehouseId));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pStatus, -1));
			dis5.getExpressions().add(cb.equal(root_.get("status"), pStatus));
			predicates.add(dis5);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root_.get("import_date")));
			TypedQuery<GoodsReceiptNote> query=em.createQuery(cq);
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy"));
			query.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query.setParameter(pIECategorieId,Long.parseLong(Objects.toString(hIECategoriesId.getValue(), "0")));
			query.setParameter(pWarehouseId,Long.parseLong(Objects.toString(hWarehouseId.getValue(), "0")));
			query.setParameter(pStatus, Integer.parseInt(Objects.toString(hStatus.getValue(),"-1")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(GoodsReceiptNote.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy"));
			query2.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy"));
			query2.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query2.setParameter(pIECategorieId,Long.parseLong(Objects.toString(hIECategoriesId.getValue(), "0")));
			query2.setParameter(pWarehouseId,Long.parseLong(Objects.toString(hWarehouseId.getValue(), "0")));
			query2.setParameter(pStatus, Integer.parseInt(Objects.toString(hStatus.getValue(),"-1")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch(Exception e){
			logger.error("GoodsReceiptNoteService.search:"+e.getMessage(),e);
		}
		return res;
	}

	
	@Override
	public int insert(GoodsReceiptNoteReqInfo t) {
		int res=-1;
		try{
			GoodsReceiptNote p=t.getGoods_receipt_note();
			if(p !=null){
				//auto general voucher_code
				if(initCode(p)==0){
					em.persist(p);
					selectById(p.getId(), t);
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("GoodsReceiptNoteService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(GoodsReceiptNoteReqInfo t) {
		int res=-1;
		try{
			GoodsReceiptNote p=t.getGoods_receipt_note();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
					selectById(p.getId(), t);
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("GoodsReceiptNoteService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, GoodsReceiptNoteReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<GoodsReceiptNote> cq=cb.createQuery(GoodsReceiptNote.class);
			Root<GoodsReceiptNote> root= cq.from(GoodsReceiptNote.class);
			root.fetch("customer",JoinType.INNER);
			root.fetch("ie_categories",JoinType.INNER);
			root.fetch("warehouse",JoinType.LEFT);
			cq.select(root).where(cb.equal(root.get("id"), id));
			TypedQuery<GoodsReceiptNote> query=em.createQuery(cq);
			t.setGoods_receipt_note(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("GoodsReceiptNoteService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JPQl
			GoodsReceiptNote t=em.find(GoodsReceiptNote.class, id);
			if(t!=null){
				em.remove(t.getList_goods_receipt_note_detail());
				em.remove(t);
				res=0;
			}
		}catch (Exception e) {
			logger.error("GoodsReceiptNoteService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}
	private int initCode(GoodsReceiptNote t){
		int res=-1;
		
		try{
			Date date=t.getImport_date();
			int year=ToolTimeCustomer.getYearM(date);
			int month=ToolTimeCustomer.getMonthM(date);
			int day=ToolTimeCustomer.getDayM(date);
					
			String voucher=day+""+month+""+year+"/";
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<String> cq=cb.createQuery(String.class);
			Root<GoodsReceiptNote> root= cq.from(GoodsReceiptNote.class);
			cq.select(root.get("voucher_code"))
			.where(cb.equal(root.get("import_date"), ToolTimeCustomer.getFirstDateOfDay(date)))
			.orderBy(cb.desc(root.get("import_date")));
			TypedQuery<String> query=em.createQuery(cq);
			List<String> list=query.getResultList();
			
			if(list.size() >0){
				String temp=list.get(0);
				if(temp !=null){
					int last=temp.lastIndexOf("/");
					String sub=temp.substring(last+1);
					voucher= voucher+String.format("%02d",Integer.parseInt(sub)+1);
					t.setVoucher_code(voucher);
				}
			}else{
				voucher= voucher+String.format("%02d",1);
				t.setVoucher_code(voucher);
			}
			res=0;
		}catch(Exception e){
			logger.error("GoodsReceiptNoteService.initCode:"+e.getMessage(),e);
		}
		return res;
	}

}
