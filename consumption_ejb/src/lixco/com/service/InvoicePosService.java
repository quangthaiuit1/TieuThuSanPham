package lixco.com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
import lixco.com.entity.ExportBatchPos;
import lixco.com.entity.InvoiceDetailPos;
import lixco.com.entity.InvoicePos;
import lixco.com.entity.Pos;
import lixco.com.entity.Product;
import lixco.com.entity.ProductPos;
import lixco.com.interfaces.IInvoicePosService;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class InvoicePosService implements IInvoicePosService {
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int search(String json, PagingInfo page, List<InvoicePos> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ invoice_info:{from_date:'',to_date:'',customer_id:0,invoice_code:'',voucher_code:'',ie_categories_id:0,po_no:'',order_code:'',order_voucher:'',payment:-1,status:-1}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hFromDate=JsonParserUtil.getValueString(j.get("invoice_info"),"from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j.get("invoice_info"),"to_date", null);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j.get("invoice_info"),"customer_id", null);
			HolderParser hInvoiceCode=JsonParserUtil.getValueString(j.get("invoice_info"),"invoice_code", null);
			HolderParser hVoucherCode=JsonParserUtil.getValueString(j.get("invoice_info"),"voucher_code", null);
			HolderParser hIECategoriesId=JsonParserUtil.getValueNumber(j.get("invoice_info"),"ie_categories_id", null);
			HolderParser hPoNo=JsonParserUtil.getValueString(j.get("invoice_info"),"po_no", null);
			HolderParser hOrderCode=JsonParserUtil.getValueString(j.get("invoice_info"),"order_code", null);
			HolderParser hOrderVoucher=JsonParserUtil.getValueString(j.get("invoice_info"),"order_voucher", null);
			HolderParser hPayment=JsonParserUtil.getValueString(j.get("invoice_info"),"payment", null);
			HolderParser hStatus=JsonParserUtil.getValueString(j.get("invoice_info"),"status", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<InvoicePos> cq=cb.createQuery(InvoicePos.class);
			Root<InvoicePos> root_= cq.from(InvoicePos.class);
			root_.fetch("customer",JoinType.INNER);//khách hàng
			root_.fetch("promotion_program",JoinType.LEFT);// chương trình KM
			root_.fetch("pricing_program",JoinType.LEFT);// chương trình đơn giá
			root_.fetch("payment_method",JoinType.LEFT);//phương thức thanh toán
			root_.fetch("car",JoinType.LEFT);//xe
			root_.fetch("freight_contract",JoinType.LEFT);// hợp đồng vận chuyển.
			root_.fetch("contract",JoinType.LEFT);//hợp đồng
			root_.fetch("warehouse",JoinType.LEFT);// kho
			root_.fetch("ie_categories",JoinType.LEFT);//danh mục nhập xuất
			root_.fetch("delivery_pricing",JoinType.LEFT);// đơn giá vận chuyển (nơi đến)
			root_.fetch("harbor_category",JoinType.LEFT);//cảng vận chuyển
			root_.fetch("stocker",JoinType.LEFT);// thủ kho
			root_.fetch("stevedore",JoinType.LEFT);//bốc xếp
			root_.fetch("form_up_goods",JoinType.LEFT);//hình thức lên hàng
			root_.fetch("carrier",JoinType.LEFT);// người vận chuyển
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<String> pInvoiceCode=cb.parameter(String.class);
			ParameterExpression<String> pVoucherCode=cb.parameter(String.class);
			ParameterExpression<Long> pIECategoriesId=cb.parameter(Long.class);
			ParameterExpression<String> pPoNo=cb.parameter(String.class);
			ParameterExpression<String> pOrderCode=cb.parameter(String.class);
			ParameterExpression<String> pOrderVoucher=cb.parameter(String.class);
			ParameterExpression<Integer> pPayment=cb.parameter(Integer.class);
			ParameterExpression<Integer> pStatus=cb.parameter(Integer.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pFromDate));
			dis.getExpressions().add(cb.equal(pFromDate,""));
			dis.getExpressions().add(cb.greaterThanOrEqualTo(root_.get("invoice_date"), pFromDate));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pToDate));
			dis1.getExpressions().add(cb.equal(pToDate,""));
			dis1.getExpressions().add(cb.lessThanOrEqualTo(root_.get("invoice_date"), pToDate));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pCustomerId,0));
			dis2.getExpressions().add(cb.equal(root_.get("customer").get("id"), pCustomerId));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pInvoiceCode,""));
			dis3.getExpressions().add(cb.equal(root_.get("invoice_code"),pInvoiceCode));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.equal(pVoucherCode,""));
			dis4.getExpressions().add(cb.equal(root_.get("voucher_code"),pVoucherCode));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pIECategoriesId,0));
			dis5.getExpressions().add(cb.equal(root_.get("ie_categories").get("id"),pIECategoriesId));
			predicates.add(dis5);
			Predicate dis6=cb.disjunction();
			dis6.getExpressions().add(cb.equal(pPoNo,""));
			dis6.getExpressions().add(cb.equal(root_.get("po_no"),pPoNo));
			predicates.add(dis6);
			Predicate dis7=cb.disjunction();
			dis7.getExpressions().add(cb.equal(pOrderCode,""));
			dis7.getExpressions().add(cb.equal(root_.get("order_code"),pOrderCode));
			predicates.add(dis7);
			Predicate dis8=cb.disjunction();
			dis8.getExpressions().add(cb.equal(pOrderVoucher,""));
			dis8.getExpressions().add(cb.equal(root_.get("order_voucher"),pOrderVoucher));
			predicates.add(dis8);
			Predicate dis9=cb.disjunction();
			dis9.getExpressions().add(cb.equal(pPayment,-1));
			dis9.getExpressions().add(cb.equal(root_.get("payment"),pPayment));
			predicates.add(dis9);
			Predicate dis10=cb.disjunction();
			dis10.getExpressions().add(cb.equal(pStatus,-1));
			dis10.getExpressions().add(cb.equal(root_.get("status"),pStatus));
			predicates.add(dis10);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root_.get("invoice_code")));
			TypedQuery<InvoicePos> query=em.createQuery(cq);
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy"));
			query.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query.setParameter(pInvoiceCode, Objects.toString(hInvoiceCode.getValue(), ""));
			query.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(), ""));
			query.setParameter(pIECategoriesId,Long.parseLong(Objects.toString(hIECategoriesId.getValue(), "0")));
			query.setParameter(pPoNo, Objects.toString(hPoNo.getValue(), ""));
			query.setParameter(pOrderCode, Objects.toString(hOrderCode.getValue(), ""));
			query.setParameter(pOrderVoucher, Objects.toString(hOrderVoucher.getValue(), ""));
			query.setParameter(pPayment,Integer.parseInt(Objects.toString(hPayment.getValue(),"-1")));
			query.setParameter(pStatus,Integer.parseInt(Objects.toString(hStatus.getValue(),"-1")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(InvoicePos.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy"));
			query2.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy"));
			query2.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query2.setParameter(pInvoiceCode, Objects.toString(hInvoiceCode.getValue(), ""));
			query2.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(), ""));
			query2.setParameter(pIECategoriesId,Long.parseLong(Objects.toString(hIECategoriesId.getValue(), "0")));
			query2.setParameter(pPoNo, Objects.toString(hPoNo.getValue(), ""));
			query2.setParameter(pOrderCode, Objects.toString(hOrderCode.getValue(), ""));
			query2.setParameter(pOrderVoucher, Objects.toString(hOrderVoucher.getValue(), ""));
			query2.setParameter(pPayment,Integer.parseInt(Objects.toString(hPayment.getValue(),"-1")));
			query2.setParameter(pStatus,Integer.parseInt(Objects.toString(hStatus.getValue(),"-1")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);	
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
			
		}catch(Exception e){
			logger.error("InvoicePosService.search:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int getListExportBatchPos(long invoiceDetailId, List<ExportBatchPos> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ExportBatchPos> cq=cb.createQuery(ExportBatchPos.class);
			Root<ExportBatchPos> root_= cq.from(ExportBatchPos.class);
			Fetch<ExportBatchPos,ProductPos> productPos_=root_.fetch("product_pos",JoinType.INNER);
			Fetch<ProductPos,Pos> pos_ = productPos_.fetch("pos",JoinType.INNER);
			pos_.fetch("warehouse",JoinType.INNER);
			productPos_.fetch("batch_pos",JoinType.INNER);
			cq.select(root_).where(cb.equal(root_.get("invoice_detail_pos").get("id"), invoiceDetailId));
			TypedQuery<ExportBatchPos> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("InvoicePosService.getListExportBatchPos:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int processingContract(List<Long> listProductId,Date effectiveDate,Date expiryDate,List<Object[]> list) {
		int res=-1;
		try{
			if(listProductId!=null && listProductId.size()>0 && effectiveDate !=null){
				CriteriaBuilder cb=em.getCriteriaBuilder();
				CriteriaQuery<Object[]> cq=cb.createQuery(Object[].class);
				Root<InvoiceDetailPos> root=cq.from(InvoiceDetailPos.class);
				Join<InvoiceDetailPos, InvoicePos> invoicePos_=root.join("invoice_pos",JoinType.INNER);
				Join<InvoiceDetailPos, Product> product_=root.join("product",JoinType.INNER);
				ParameterExpression<Date>  pEffectiveDate=cb.parameter(Date.class);
				ParameterExpression<Date>  pExpiryDate=cb.parameter(Date.class);
				List<Predicate> predicates=new ArrayList<>();
				predicates.add(cb.greaterThanOrEqualTo(invoicePos_.get("invoice_date"),pEffectiveDate));
				Predicate dis1=cb.disjunction();
				dis1.getExpressions().add(cb.isNull(pExpiryDate));
				dis1.getExpressions().add(cb.lessThanOrEqualTo(invoicePos_.get("invoice_date"), pExpiryDate));
				predicates.add(dis1);
				predicates.add(root.get("product").get("id").in(listProductId));
				cq.multiselect(root.get("product").get("id"),cb.sum(cb.prod(root.get("quantity"),product_.get("factor"))))
				.where(cb.and(predicates.toArray(new Predicate[0]))).groupBy(root.get("product").get("id"));
				TypedQuery<Object[]> query=em.createQuery(cq);
				query.setParameter(pEffectiveDate, effectiveDate);
				query.setParameter(pExpiryDate, expiryDate);
				list.addAll(query.getResultList());
				res=0;
			}
		}catch (Exception e) {
			logger.error("InvoicePosService.processingContract:"+e.getMessage(),e);
		}
		return res;
	}

}
