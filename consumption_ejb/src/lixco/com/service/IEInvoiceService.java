package lixco.com.service;

import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import org.jboss.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Car;
import lixco.com.entity.City;
import lixco.com.entity.Contract;
import lixco.com.entity.Country;
import lixco.com.entity.Currency;
import lixco.com.entity.Customer;
import lixco.com.entity.HarborCategory;
import lixco.com.entity.IEInvoice;
import lixco.com.entity.IEInvoiceDetail;
import lixco.com.entity.InvoiceDetail;
import lixco.com.entity.InvoicePos;
import lixco.com.entity.PaymentMethod;
import lixco.com.entity.Product;
import lixco.com.entity.ProductType;
import lixco.com.entity.Stevedore;
import lixco.com.entity.Warehouse;
import lixco.com.interfaces.IIEInvoiceService;
import lixco.com.reportInfo.IECommercialInvoiceReportInfo;
import lixco.com.reportInfo.IEExportReport;
import lixco.com.reportInfo.IEInvoiceCustomerBillNoDetailReport;
import lixco.com.reportInfo.IEInvoiceHarborBillNoDetailReport;
import lixco.com.reportInfo.IEInvoiceListByContNoReport;
import lixco.com.reportInfo.IEInvoiceListByTaxValueZeroReport;
import lixco.com.reportInfo.IEInvoiceOrderListByProductCodeReport;
import lixco.com.reportInfo.IEInvoiceOrderListByVoucherReport;
import lixco.com.reportInfo.IEInvoiceReport;
import lixco.com.reportInfo.IEOrderFormReport;
import lixco.com.reportInfo.IEPackingListReport;
import lixco.com.reportInfo.IEProformaInvocieReportInfo;
import lixco.com.reportInfo.IEVanningReport;
import lixco.com.reqInfo.IEInvoiceDetailReqInfo;
import lixco.com.reqInfo.IEInvoiceReqInfo;
import lixco.com.reqInfo.WrapIEInvocieReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class IEInvoiceService implements IIEInvoiceService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int insert(IEInvoiceReqInfo t) {
		int res=-1;
		try{
			IEInvoice p=t.getIe_invoice();
			if(p !=null){
				if(p.getInvoice_code()==null || "".equals(p.getInvoice_code())){
				   p.setInvoice_code(initInvoiceCode());
				}
				if(p.getVoucher_code()==null || "".equals(p.getVoucher_code())){
				   p.setVoucher_code(initVoucherCode());
				}
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("IEInvoiceService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(IEInvoiceReqInfo t) {
		int res=-1;
		try{
			IEInvoice p=t.getIe_invoice();
			if(p !=null){
				if(em.merge(p) !=null){
					selectById(p.getId(),t);
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("IEInvoiceService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json, PagingInfo page, List<IEInvoice> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{
			/*{ie_invoice:{from_date:'',to_date:'',customer_id:0,invoice_code:'',voucher_code:'',product_id:0,ie_categories_id:0,contract_voucher_code:''},page:{page_size:0,page_index:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hFromDate=JsonParserUtil.getValueString(j.get("ie_invoice"), "from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j.get("ie_invoice"),"to_date", null);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j.get("ie_invoice"), "customer_id", null);
			HolderParser hInvoiceCode=JsonParserUtil.getValueString(j.get("ie_invoice"),"invoice_code", null);
			HolderParser hVoucherCode=JsonParserUtil.getValueString(j.get("ie_invoice"),"voucher_code", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j.get("ie_invoice"), "product_id", null);
			HolderParser hIECategoriesId=JsonParserUtil.getValueNumber(j.get("ie_invoice"),"ie_categories_id", null);
			HolderParser hContractVoucherCode=JsonParserUtil.getValueString(j.get("ie_invoice"), "contract_voucher_code", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<String> pInvoiceCode=cb.parameter(String.class);
			ParameterExpression<String> pVoucherCode=cb.parameter(String.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pIECategoriesId=cb.parameter(Long.class);
			ParameterExpression<String> pContractVoucherCode=cb.parameter(String.class);
			CriteriaQuery<IEInvoice> cq=cb.createQuery(IEInvoice.class);
			Root<IEInvoice> root=cq.from(IEInvoice.class);
			root.fetch("customer",JoinType.INNER);
			root.fetch("car",JoinType.LEFT);
			root.fetch("stocker",JoinType.LEFT);
			root.fetch("payment_method",JoinType.LEFT);
			root.fetch("ie_categories",JoinType.LEFT);
			Join<IEInvoice, Contract> contract_= (Join)root.fetch("contract",JoinType.LEFT);
			root.fetch("stevedore",JoinType.LEFT);
			root.fetch("form_up_goods",JoinType.LEFT);
			root.fetch("warehouse",JoinType.LEFT);
			root.fetch("harbor_category",JoinType.LEFT);
			root.fetch("currency",JoinType.LEFT);
			root.fetch("post_of_tran",JoinType.LEFT);
			root.fetch("invoice",JoinType.LEFT);
			Subquery<Long> subquery=cq.subquery(Long.class);
			Root<IEInvoiceDetail> rootSub=subquery.from(IEInvoiceDetail.class);
			subquery.select(rootSub.get("ie_invoice").get("id")).where(cb.equal(rootSub.get("product"), pProductId));
			List<Predicate> predicates=new ArrayList<Predicate>();
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pFromDate));
			dis1.getExpressions().add(cb.greaterThanOrEqualTo(root.get("invoice_date"), pFromDate));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.isNull(pToDate));
			dis2.getExpressions().add(cb.lessThanOrEqualTo(root.get("invoice_date"), pToDate));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pCustomerId, 0));
			dis3.getExpressions().add(cb.equal(root.get("customer").get("id"), pCustomerId));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.equal(pInvoiceCode, ""));
			dis4.getExpressions().add(cb.equal(root.get("invoice_code"), pInvoiceCode));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pVoucherCode, ""));
			dis5.getExpressions().add(cb.equal(root.get("voucher_code"), pVoucherCode));
			predicates.add(dis5);
			Predicate dis6=cb.disjunction();
			dis6.getExpressions().add(cb.equal(pProductId, 0));
			dis6.getExpressions().add(cb.equal(root.get("id"), subquery));
			predicates.add(dis6);
			Predicate dis7=cb.disjunction();
			dis7.getExpressions().add(cb.equal(pIECategoriesId, 0));
			dis7.getExpressions().add(cb.equal(root.get("ie_categories").get("id"), pIECategoriesId));
			predicates.add(dis7);
			Predicate dis8=cb.disjunction();
			dis8.getExpressions().add(cb.equal(pContractVoucherCode, ""));
			dis8.getExpressions().add(cb.equal(contract_.get("voucher_code"), pContractVoucherCode));
			predicates.add(dis8);
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<IEInvoice> query=em.createQuery(cq);
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue()),"dd/MM/yyyy"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue()),"dd/MM/yyyy"));
			query.setParameter(pCustomerId, Long.parseLong(Objects.toString(hCustomerId.getValue())));
			query.setParameter(pInvoiceCode, Objects.toString(hInvoiceCode.getValue(),""));
			query.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(),""));
			query.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue())));
			query.setParameter(pIECategoriesId, Long.parseLong(Objects.toString(hIECategoriesId.getValue())));
			query.setParameter(pContractVoucherCode, Objects.toString(hContractVoucherCode.getValue(),""));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root=cq1.from(IEInvoice.class);
			contract_= root.join("contract",JoinType.LEFT);
			//thu hoi
			dis8.getExpressions().clear();
			//re innit
			dis8.getExpressions().add(cb.equal(pContractVoucherCode, ""));
			dis8.getExpressions().add(cb.equal(contract_.get("voucher_code"), pContractVoucherCode));
			predicates.add(dis8);
			cq1.select(cb.count(root.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(),null),"dd/MM/yyyy"));
			query2.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(),null),"dd/MM/yyyy"));
			query2.setParameter(pCustomerId, Long.parseLong(Objects.toString(hCustomerId.getValue())));
			query2.setParameter(pInvoiceCode, Objects.toString(hInvoiceCode.getValue(),""));
			query2.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(),""));
			query2.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue())));
			query2.setParameter(pIECategoriesId, Long.parseLong(Objects.toString(hIECategoriesId.getValue())));
			query2.setParameter(pContractVoucherCode, Objects.toString(hContractVoucherCode.getValue(),""));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			// thực hiện xóa chi tiết phiếu xuất xuất khẩu
			Query queryDelIEInvoiceDetail=em.createQuery("delete from IEInvoiceDetail as dt where dt.ie_invoice=:id");
			queryDelIEInvoiceDetail.setParameter("id", id);
			if(queryDelIEInvoiceDetail.executeUpdate()>=0){
				//thực hiện delete phiếu xuất xuất khẩu
				Query queryDelIEInvoice=em.createQuery("delete from IEInvoice as d where d.id=:id");
				queryDelIEInvoice.setParameter("id", id);
				if(queryDelIEInvoice.executeUpdate()>=0){
					res=0;
				}else{
					ct.getUserTransaction().rollback();
				}
			}else{
				ct.getUserTransaction().rollback();
			}
		}catch(Exception e){
			logger.error("IEInvoiceService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id,IEInvoiceReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<IEInvoice> cq=cb.createQuery(IEInvoice.class);
			Root<IEInvoice> root=cq.from(IEInvoice.class);
			root.fetch("customer",JoinType.INNER);
			root.fetch("car",JoinType.LEFT);
			root.fetch("payment_method",JoinType.LEFT);
			root.fetch("ie_categories",JoinType.INNER);
			root.fetch("contract",JoinType.LEFT);
			root.fetch("stevedore",JoinType.LEFT);
			root.fetch("form_up_goods",JoinType.LEFT);
			root.fetch("warehouse",JoinType.LEFT);
			root.fetch("harbor_category",JoinType.LEFT);
			root.fetch("currency",JoinType.LEFT);
			root.fetch("post_of_tran",JoinType.LEFT);
			cq.select(root).where(cb.equal(root.get("id"), id));
			TypedQuery<IEInvoice> query=em.createQuery(cq);
			t.setIe_invoice(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("IEInvoiceService.selectById:"+e.getMessage(),e);
		}
		return res;
	}
	private String initInvoiceCode(){
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<IEInvoice> root= cq.from(IEInvoice.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)root.get("invoice_code"),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/100000000;
			if(p<1){
				return String.format("%08d", max+1);
			}
			return (max+1)+"";
		}catch (Exception e) {
			logger.error("IEInvoiceService.initInvoiceCode:"+e.getMessage(),e);
		}
		return null;
	}
	private String initVoucherCode(){
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<IEInvoice> root= cq.from(IEInvoice.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)root.get("voucher_code"),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/10000000;
			if(p<1){
				return String.format("%07d", max+1);
			}
			return (max+1)+"";
		}catch (Exception e) {
			logger.error("IEInvoiceService.initVoucherCode:"+e.getMessage(),e);
		}
		return null;
		
	}

	@Override
	public int insertDetail(IEInvoiceDetailReqInfo t) {
		int res=-1;
		try{
			IEInvoiceDetail p=t.getIe_invoice_detail();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("IEInvoiceService.insertDetail:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int updateDetail(IEInvoiceDetailReqInfo t) {
		int res=-1;
		try{
			IEInvoiceDetail p=t.getIe_invoice_detail();
			if(p !=null){
				if(em.merge(p)!=null){
					selectDetailById(p.getId(), t);
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("IEInvoiceService.updateDetail:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectIEInvoideDetailByInvoice(long iEInvoiceId, List<IEInvoiceDetail> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<IEInvoiceDetail> cq=cb.createQuery(IEInvoiceDetail.class);
			Root<IEInvoiceDetail> root=cq.from(IEInvoiceDetail.class);
			root.fetch("product",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("ie_invoice").get("id"), iEInvoiceId));
			TypedQuery<IEInvoiceDetail> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEInvoideDetailByInvoice:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteDetailById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from IEInvoiceDetail where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch (Exception e) {
			logger.error("IEInvoiceService.deleteDetailById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectDetailById(long id, IEInvoiceDetailReqInfo t) {
		int res=-1;
		try {
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<IEInvoiceDetail> cq=cb.createQuery(IEInvoiceDetail.class);
			Root<IEInvoiceDetail> root=cq.from(IEInvoiceDetail.class);
			root.fetch("product",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("id"), id));
			TypedQuery<IEInvoiceDetail> query=em.createQuery(cq);
			t.setIe_invoice_detail(query.getSingleResult());
			res=0;
		} catch (Exception e) {
			logger.error("IEInvoiceService.selectDetailById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectCommercialInvoiceReport(long iEInvoiceId, List<IECommercialInvoiceReportInfo> list) {
		int res=-1;
		try {
			StringBuilder sql=new StringBuilder();
			sql.append("select p.id as product_id,p.en_name as product_en_name,p.specification,p.unit,pt.id as product_type_id,pt.en_name as product_type_en_name,t.quantity_export, ");
			sql.append("t.foreign_unit_price,t.total_export_foreign_amount from product as p ");
			sql.append("inner join producttype as pt on p.product_type_id=pt.id ");
			sql.append("inner join ( select d.product_id,sum(d.quantity_export) as quantity_export,sum(d.foreign_unit_price)/count(d.id) as foreign_unit_price, ");
			sql.append("sum(d.total_export_foreign_amount) as total_export_foreign_amount ");
			sql.append("from ieinvoicedetail as d  where d.ie_invoice_id=:pid group by d.product_id) as t on t.product_id=p.id order by pt.en_name ");
			Query query=em.createNativeQuery(sql.toString());
			query.setParameter("pid",iEInvoiceId);
			List<Object[]> listObj=query.getResultList();
			for(Object[] p:listObj){
				IECommercialInvoiceReportInfo item=new IECommercialInvoiceReportInfo(Long.parseLong(Objects.toString(p[0])),Objects.toString(p[1]),Double.parseDouble(Objects.toString(p[2])),
						Objects.toString(p[3]),Long.parseLong(Objects.toString(p[4])),Objects.toString(p[5]),Double.parseDouble(Objects.toString(p[6])),Double.parseDouble(Objects.toString(p[7])),Double.parseDouble(Objects.toString(p[8])));
				list.add(item);
			}
			res=0;
		} catch (Exception e) {
			logger.error("IEInvoiceService.selectCommercialInvoiceReport:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectCommercialInvoiceReport(String json, List<IECommercialInvoiceReportInfo> list) {
		int res=-1;
		try{/*list_ie_invoice_detail_id:[]}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hListIdDetail=JsonParserUtil.getValueList(j, "list_ie_invoice_detail_id", null,new TypeToken<List<Long>>(){}.getType());
			List<Long> listId=(List<Long>) hListIdDetail.getValue();
			StringBuilder sql=new StringBuilder();
			sql.append("select p.id as product_id,p.en_name as product_en_name,p.specification,p.unit,pt.id as product_type_id,pt.en_name as product_type_en_name,t.quantity_export, ");
			sql.append("t.foreign_unit_price,t.total_export_foreign_amount from product as p ");
			sql.append("inner join producttype as pt on p.product_type_id=pt.id ");
			sql.append("inner join ( select d.product_id,sum(d.quantity_export) as quantity_export,sum(d.foreign_unit_price)/count(d.id) as foreign_unit_price, ");
			sql.append("sum(d.total_export_foreign_amount) as total_export_foreign_amount ");
			sql.append("from ieinvoicedetail as d  where d.id in :pid group by d.product_id) as t on t.product_id=p.id order by pt.en_name ");
			Query query=em.createNativeQuery(sql.toString());
			query.setParameter("pid",listId);
			List<Object[]> listObj=query.getResultList();
			for(Object[] p:listObj){
				IECommercialInvoiceReportInfo item=new IECommercialInvoiceReportInfo(Long.parseLong(Objects.toString(p[0])),Objects.toString(p[1]),Double.parseDouble(Objects.toString(p[2])),
						Objects.toString(p[3]),Long.parseLong(Objects.toString(p[4])),Objects.toString(p[5]),Double.parseDouble(Objects.toString(p[6])),Double.parseDouble(Objects.toString(p[7])),Double.parseDouble(Objects.toString(p[8])));
				list.add(item);
			}
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectCommercialInvoiceReport_:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectOrderFormReport(long iEInvoiceId, List<IEOrderFormReport> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<IEOrderFormReport> cq=cb.createQuery(IEOrderFormReport.class);
			Root<IEInvoiceDetail> root=cq.from(IEInvoiceDetail.class);
			Join<IEInvoiceDetail, Product> product=root.join("product",JoinType.INNER);
			cq.select(cb.construct( IEOrderFormReport.class,product.get("product_code"),product.get("product_name"),product.get("specification"),
					root.get("quantity_export"),root.get("quantity"),root.get("unit_price"),root.get("total_amount"),root.get("batch_code")))
			.where(cb.equal(root.get("ie_invoice").get("id"), iEInvoiceId));
			TypedQuery<IEOrderFormReport> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectOrderFormReport:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEProformaInvoiceReport(long iEInvoiceId, List<IEProformaInvocieReportInfo> list) {
		int res=-1;
		try{
			StringBuilder sql=new StringBuilder();
			sql.append("select p.id as product_id,p.en_name as product_en_name,p.specification,p.unit,pt.id as product_type_id,pt.en_name as product_type_en_name,t.quantity_export, ");
			sql.append("t.foreign_unit_price,t.total_export_foreign_amount from product as p ");
			sql.append("inner join producttype as pt on p.product_type_id=pt.id ");
			sql.append("inner join ( select d.product_id,sum(d.quantity_export) as quantity_export,sum(d.foreign_unit_price)/count(d.id) as foreign_unit_price, ");
			sql.append("sum(d.total_export_foreign_amount) as total_export_foreign_amount ");
			sql.append("from ieinvoicedetail as d  where d.ie_invoice_id=:pid group by d.product_id) as t on t.product_id=p.id order by pt.en_name ");
			Query query=em.createNativeQuery(sql.toString());
			query.setParameter("pid",iEInvoiceId);
			List<Object[]> listObj=query.getResultList();
			for(Object[] p:listObj){
				IEProformaInvocieReportInfo item=new IEProformaInvocieReportInfo(Long.parseLong(Objects.toString(p[0])),Objects.toString(p[1]),Double.parseDouble(Objects.toString(p[2])),
						Objects.toString(p[3]),Long.parseLong(Objects.toString(p[4])),Objects.toString(p[5]),Double.parseDouble(Objects.toString(p[6])),Double.parseDouble(Objects.toString(p[7])),Double.parseDouble(Objects.toString(p[8])));
				list.add(item);
			}
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEProformaInvoiceReport:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEProformaInvoiceReport(String json, List<IEProformaInvocieReportInfo> list) {
		int res=-1;
		try{
			/*list_ie_invoice_detail_id:[]}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hListIdDetail=JsonParserUtil.getValueList(j, "list_ie_invoice_detail_id", null,new TypeToken<List<Long>>(){}.getType());
			List<Long> listId=(List<Long>) hListIdDetail.getValue();
			StringBuilder sql=new StringBuilder();
			sql.append("select p.id as product_id,p.en_name as product_en_name,p.specification,p.unit,pt.id as product_type_id,pt.en_name as product_type_en_name,t.quantity_export, ");
			sql.append("t.foreign_unit_price,t.total_export_foreign_amount from product as p ");
			sql.append("inner join producttype as pt on p.product_type_id=pt.id ");
			sql.append("inner join ( select d.product_id,sum(d.quantity_export) as quantity_export,sum(d.foreign_unit_price)/count(d.id) as foreign_unit_price, ");
			sql.append("sum(d.total_export_foreign_amount) as total_export_foreign_amount ");
			sql.append("from ieinvoicedetail as d  where d.id in :pid group by d.product_id) as t on t.product_id=p.id order by pt.en_name ");
			Query query=em.createNativeQuery(sql.toString());
			query.setParameter("pid",listId);
			List<Object[]> listObj=query.getResultList();
			for(Object[] p:listObj){
				IEProformaInvocieReportInfo item=new IEProformaInvocieReportInfo(Long.parseLong(Objects.toString(p[0])),Objects.toString(p[1]),Double.parseDouble(Objects.toString(p[2])),
						Objects.toString(p[3]),Long.parseLong(Objects.toString(p[4])),Objects.toString(p[5]),Double.parseDouble(Objects.toString(p[6])),Double.parseDouble(Objects.toString(p[7])),Double.parseDouble(Objects.toString(p[8])));
				list.add(item);
			}
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEExportReport:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEPackingListReport(long iEInvoiceId, List<IEPackingListReport> list) {
		int res=-1;
		try{
//			(long product_id, String product_en_name, long product_type_id,String product_type_en_name, String unit, double specification, double tare, double factor,double quantity_export)
			StringBuilder sql=new StringBuilder();
			sql.append("select p.id as product_id,p.en_name as product_en_name,pt.id as product_type_id,pt.en_name as product_type_en_name,p.unit,p.specification,p.tare,p.factor,t.quantity_export, ");
			sql.append("t.foreign_unit_price,t.total_export_foreign_amount from product as p ");
			sql.append("inner join producttype as pt on p.product_type_id=pt.id ");
			sql.append("inner join ( select d.product_id,sum(d.quantity_export) as quantity_export,sum(d.foreign_unit_price)/count(d.id) as foreign_unit_price, ");
			sql.append("sum(d.total_export_foreign_amount) as total_export_foreign_amount ");
			sql.append("from ieinvoicedetail as d  where d.ie_invoice_id=:pid group by d.product_id) as t on t.product_id=p.id order by pt.en_name ");
			Query query=em.createNativeQuery(sql.toString());
			query.setParameter("pid",iEInvoiceId);
			List<Object[]> listObj=query.getResultList();
			for(Object[] p:listObj){
				IEPackingListReport item=new IEPackingListReport(Long.parseLong(Objects.toString(p[0])),Objects.toString(p[1],null),Long.parseLong(Objects.toString(p[2])),
						Objects.toString(p[3],null),Objects.toString(p[4],null),Double.parseDouble(Objects.toString(p[5])),Double.parseDouble(Objects.toString(p[6])),Double.parseDouble(Objects.toString(p[7])),Double.parseDouble(Objects.toString(p[8])));
				list.add(item);
			}
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEPackingListReport:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEPackingListReport(String json, List<IEPackingListReport> list) {
		int res=-1;
		try{
			/*list_ie_invoice_detail_id:[]}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hListIdDetail=JsonParserUtil.getValueList(j, "list_ie_invoice_detail_id", null,new TypeToken<List<Long>>(){}.getType());
			List<Long> listId=(List<Long>) hListIdDetail.getValue();
//			(long product_id, String product_en_name, long product_type_id,String product_type_en_name, String unit, double specification, double tare, double factor,double quantity_export)
			StringBuilder sql=new StringBuilder();
			sql.append("select p.id as product_id,p.en_name as product_en_name,pt.id as product_type_id,pt.en_name as product_type_en_name,p.unit,p.specification,p.tare,p.factor,t.quantity_export, ");
			sql.append("t.foreign_unit_price,t.total_export_foreign_amount from product as p ");
			sql.append("inner join producttype as pt on p.product_type_id=pt.id ");
			sql.append("inner join ( select d.product_id,sum(d.quantity_export) as quantity_export,sum(d.foreign_unit_price)/count(d.id) as foreign_unit_price, ");
			sql.append("sum(d.total_export_foreign_amount) as total_export_foreign_amount ");
			sql.append("from ieinvoicedetail as d  where d.id in :pid group by d.product_id) as t on t.product_id=p.id order by pt.en_name ");
			Query query=em.createNativeQuery(sql.toString());
			query.setParameter("pid",listId);
			List<Object[]> listObj=query.getResultList();
			for(Object[] p:listObj){
				IEPackingListReport item=new IEPackingListReport(Long.parseLong(Objects.toString(p[0])),Objects.toString(p[1],null),Long.parseLong(Objects.toString(p[2])),
						Objects.toString(p[3],null),Objects.toString(p[4],null),Double.parseDouble(Objects.toString(p[5])),Double.parseDouble(Objects.toString(p[6])),Double.parseDouble(Objects.toString(p[7])),Double.parseDouble(Objects.toString(p[8])));
				list.add(item);
			}
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEExportReport:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEVanningReport(long iEInvoiceId, List<IEVanningReport> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<IEVanningReport> cq=cb.createQuery(IEVanningReport.class);
			Root<IEInvoiceDetail> root=cq.from(IEInvoiceDetail.class);
			Join<IEInvoiceDetail, Product> product=root.join("product",JoinType.INNER);
			Join<Product, ProductType>	productType=product.join("product_type",JoinType.LEFT);
//			(long product_type_id, String product_type_en_name, String product_en_name)
			cq.select(cb.construct(IEVanningReport.class, productType.get("id"),productType.get("en_name"),product.get("en_name"),root.get("container_no"))).distinct(true)
			.where(cb.equal(root.get("ie_invoice").get("id"), iEInvoiceId)).orderBy(cb.asc(productType.get("en_name")),cb.asc(product.get("en_name")));
			TypedQuery<IEVanningReport> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEVanningReport:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEVanningReport(String json, List<IEVanningReport> list) {
		int res=-1;
		try{
			/*list_ie_invoice_detail_id:[]}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hListIdDetail=JsonParserUtil.getValueList(j, "list_ie_invoice_detail_id", null,new TypeToken<List<Long>>(){}.getType());
			List<Long> listId=(List<Long>) hListIdDetail.getValue();
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<IEVanningReport> cq=cb.createQuery(IEVanningReport.class);
			Root<IEInvoiceDetail> root=cq.from(IEInvoiceDetail.class);
			Join<IEInvoiceDetail, Product> product=root.join("product",JoinType.INNER);
			Join<Product, ProductType>	productType=product.join("product_type",JoinType.LEFT);
//			(long product_type_id, String product_type_en_name, String product_en_name)
			cq.select(cb.construct(IEVanningReport.class, productType.get("id"),productType.get("en_name"),product.get("en_name"),root.get("container_no"))).distinct(true)
			.where(root.get("id").in(listId)).orderBy(cb.asc(productType.get("en_name")),cb.asc(product.get("en_name")));
			TypedQuery<IEVanningReport> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEVanningReport:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEInvoiceReport(long iEInvoiceId,List<IEInvoiceReport> list) {
		int res=-1;
		try{
			StringBuilder sql=new StringBuilder();
			sql.append("select t.product_id,p.product_name,p.unit,t.quantity_export,t.foreign_unit_price,t.total_export_foreign_amount from product as p ");
			sql.append("inner join ( select d.product_id,sum(d.quantity_export) as quantity_export,sum(d.foreign_unit_price)/count(d.id) as foreign_unit_price, ");
			sql.append("sum(d.total_export_foreign_amount) as total_export_foreign_amount ");
			sql.append(" from ieinvoicedetail as d  where d.ie_invoice_id=:pid group by d.product_id) as t on t.product_id=p.id ");
			sql.append("order by p.product_name ");
			Query query=em.createNativeQuery(sql.toString());
			query.setParameter("pid",iEInvoiceId);
			List<Object[]> listObj=query.getResultList();
			for(Object[] p:listObj){
				IEInvoiceReport item=new IEInvoiceReport(Long.parseLong(Objects.toString(p[0])),Objects.toString(p[1],null),Objects.toString(p[2],null),
						Double.parseDouble(Objects.toString(p[3])),Double.parseDouble(Objects.toString(p[4])),Double.parseDouble(Objects.toString(p[5])));
				list.add(item);
			}
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEInvoiceReport:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEExportReport(long iEInvoiceId, List<IEExportReport> list) {
		int res=-1;
		try{
			StringBuilder sql=new StringBuilder();
			sql.append("select t.product_id,p.product_name,p.unit,p.specification,t.quantity_export,t.unit_price,t.total_amount from product as p ");
			sql.append("inner join ( select d.product_id,sum(d.quantity_export) as quantity_export,sum(d.unit_price)/count(d.id) as unit_price, ");
			sql.append("sum(d.total_amount) as total_amount ");
			sql.append(" from ieinvoicedetail as d  where d.ie_invoice_id=:pid group by d.product_id) as t on t.product_id=p.id ");
			sql.append("order by p.product_name ");
			Query query=em.createNativeQuery(sql.toString());
			query.setParameter("pid",iEInvoiceId);
			List<Object[]> listObj=query.getResultList();
			for(Object[] p:listObj){
//				(long produtc_id,String product_name, String unit, double specification, double quantity_export,double unit_price, double total_amount)
				IEExportReport item=new IEExportReport(Long.parseLong(Objects.toString(p[0])),Objects.toString(p[1],null),Objects.toString(p[2],null),
						Double.parseDouble(Objects.toString(p[3])),Double.parseDouble(Objects.toString(p[4])),Double.parseDouble(Objects.toString(p[5])),Double.parseDouble(Objects.toString(p[6])));
				list.add(item);
			}
			res=0;
			
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEExportReport:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEInvoiceOrderListByVoucherReport(String json, List<IEInvoiceOrderListByVoucherReport> list) {
		int res=-1;
		try{
//			{from_date:'',to_date:'',customer_id:0,invoice_code:'',voucher_code:'',product_id:0,ie_categories_id:0,contract_voucher_code:''}
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hFromDate=JsonParserUtil.getValueString(j, "from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j,"to_date", null);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j, "customer_id", null);
			HolderParser hInvoiceCode=JsonParserUtil.getValueString(j,"invoice_code", null);
			HolderParser hVoucherCode=JsonParserUtil.getValueString(j,"voucher_code", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j, "product_id", null);
			HolderParser hIECategoriesId=JsonParserUtil.getValueNumber(j,"ie_categories_id", null);
			HolderParser hContractVoucherCode=JsonParserUtil.getValueString(j, "contract_voucher_code", null);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<String> pInvoiceCode=cb.parameter(String.class);
			ParameterExpression<String> pVoucherCode=cb.parameter(String.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pIECategoriesId=cb.parameter(Long.class);
			ParameterExpression<String> pContractVoucherCode=cb.parameter(String.class);
			CriteriaQuery<IEInvoiceOrderListByVoucherReport> cq=cb.createQuery(IEInvoiceOrderListByVoucherReport.class);
			Root<IEInvoiceDetail> root=cq.from(IEInvoiceDetail.class);
			Join<IEInvoiceDetail, Product> product_=root.join("product",JoinType.INNER);
			Join<InvoiceDetail, IEInvoice> iEInvoice_=root.join("ie_invoice" ,JoinType.INNER);
			Join<IEInvoice,Customer> customer_=iEInvoice_.join("customer",JoinType.INNER);
			Join<IEInvoice, Car> car_= iEInvoice_.join("car",JoinType.LEFT);
			Join<IEInvoice,PaymentMethod> paymentMethod_ =iEInvoice_.join("payment_method",JoinType.LEFT);
			Join<IEInvoice, Stevedore> stevedore_=iEInvoice_.join("stevedore",JoinType.LEFT);
			Join<IEInvoice, Contract> contract_=iEInvoice_.join("contract",JoinType.LEFT);
			Join<IEInvoice, Warehouse> warehouse_=iEInvoice_.join("warehouse",JoinType.LEFT);
			Join<IEInvoice, Currency> currency_=iEInvoice_.join("currency",JoinType.LEFT);
			Join<IEInvoice, HarborCategory> harborCategory_= iEInvoice_.join("post_of_tran",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pFromDate));
			dis1.getExpressions().add(cb.greaterThanOrEqualTo(iEInvoice_.get("invoice_date"), pFromDate));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.isNull(pToDate));
			dis2.getExpressions().add(cb.lessThanOrEqualTo(iEInvoice_.get("invoice_date"), pToDate));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pCustomerId, 0));
			dis3.getExpressions().add(cb.equal(iEInvoice_.get("customer").get("id"), pCustomerId));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.equal(pInvoiceCode, ""));
			dis4.getExpressions().add(cb.equal(iEInvoice_.get("invoice_code"), pInvoiceCode));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pVoucherCode, ""));
			dis5.getExpressions().add(cb.equal(iEInvoice_.get("voucher_code"), pVoucherCode));
			predicates.add(dis5);
			Predicate dis6=cb.disjunction();
			dis6.getExpressions().add(cb.equal(pProductId, 0));
			dis6.getExpressions().add(cb.equal(product_.get("id"), pProductId));
			predicates.add(dis6);
			Predicate dis7=cb.disjunction();
			dis7.getExpressions().add(cb.equal(pIECategoriesId, 0));
			dis7.getExpressions().add(cb.equal(iEInvoice_.get("ie_categories").get("id"), pIECategoriesId));
			predicates.add(dis7);
			Predicate dis8=cb.disjunction();
			dis8.getExpressions().add(cb.equal(pContractVoucherCode, ""));
			dis8.getExpressions().add(cb.equal(contract_.get("voucher_code"), pContractVoucherCode));
			predicates.add(dis8);
//			(String voucher_code, Date invoice_date, String contract_voucher_code,
//					String customer_code, String customer_name, String license_plate, String payment_name,
//					String warehouse_name, double tax_value, Date etd_date, double exchange_rate, String currency_type,
//					String note, String bill_no, String declaration_code, String post_of_tran_code, String product_code,
//					String product_name, double quantity_export, double foreign_unit_price, double total_foreign_amount,
//					String driver_name)
			cq.select(cb.construct(IEInvoiceOrderListByVoucherReport.class, iEInvoice_.get("voucher_code"),iEInvoice_.get("invoice_date"),contract_.get("voucher_code"), customer_.get("customer_code"),
					customer_.get("customer_name"),car_.get("license_plate"),paymentMethod_.get("method_name"),stevedore_.get("content"), warehouse_.get("name"),iEInvoice_.get("tax_value"),iEInvoice_.get("etd_date"),
					iEInvoice_.get("exchange_rate"),currency_.get("currency_type"),iEInvoice_.get("note"),iEInvoice_.get("bill_no"),iEInvoice_.get("declaration_code"),harborCategory_.get("port_no"),
					product_.get("product_code"),product_.get("product_name"),root.get("quantity_export"),root.get("foreign_unit_price"),root.get("total_foreign_amount"),iEInvoice_.get("driver_name")))
			.where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.asc(root.get("voucher_code")));
			TypedQuery<IEInvoiceOrderListByVoucherReport> query=em.createQuery(cq);
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(),null),"dd/MM/yyyy"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(),null),"dd/MM/yyyy"));
			query.setParameter(pCustomerId, Long.parseLong(Objects.toString(hCustomerId.getValue())));
			query.setParameter(pInvoiceCode, Objects.toString(hInvoiceCode.getValue(),""));
			query.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(),""));
			query.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue())));
			query.setParameter(pIECategoriesId, Long.parseLong(Objects.toString(hIECategoriesId.getValue())));
			query.setParameter(pContractVoucherCode, Objects.toString(hContractVoucherCode.getValue(),""));
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEInvoiceOrderListByVoucherReport:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEInvoiceOrderListByProductCodeReport(String json,List<IEInvoiceOrderListByProductCodeReport> list) {
		int res=-1;
		try{
//			{from_date:'',to_date:'',customer_id:0,invoice_code:'',voucher_code:'',product_id:0,ie_categories_id:0,contract_voucher_code:''}
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hFromDate=JsonParserUtil.getValueString(j, "from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j,"to_date", null);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j, "customer_id", null);
			HolderParser hInvoiceCode=JsonParserUtil.getValueString(j,"invoice_code", null);
			HolderParser hVoucherCode=JsonParserUtil.getValueString(j,"voucher_code", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j, "product_id", null);
			HolderParser hIECategoriesId=JsonParserUtil.getValueNumber(j,"ie_categories_id", null);
			HolderParser hContractVoucherCode=JsonParserUtil.getValueString(j, "contract_voucher_code", null);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<String> pInvoiceCode=cb.parameter(String.class);
			ParameterExpression<String> pVoucherCode=cb.parameter(String.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pIECategoriesId=cb.parameter(Long.class);
			ParameterExpression<String> pContractVoucherCode=cb.parameter(String.class);
			CriteriaQuery<IEInvoiceOrderListByProductCodeReport> cq=cb.createQuery(IEInvoiceOrderListByProductCodeReport.class);
			Root<IEInvoiceDetail> root=cq.from(IEInvoiceDetail.class);
			Join<IEInvoiceDetail, Product> product_=root.join("product",JoinType.INNER);
			Join<InvoiceDetail, IEInvoice> iEInvoice_=root.join("ie_invoice" ,JoinType.INNER);
			Join<IEInvoice,Customer> customer_=iEInvoice_.join("customer",JoinType.INNER);
			Join<IEInvoice, Car> car_= iEInvoice_.join("car",JoinType.LEFT);
			Join<IEInvoice,PaymentMethod> paymentMethod_ =iEInvoice_.join("payment_method",JoinType.LEFT);
			Join<IEInvoice, Stevedore> stevedore_=iEInvoice_.join("stevedore",JoinType.LEFT);
			Join<IEInvoice, Contract> contract_=iEInvoice_.join("contract",JoinType.LEFT);
			Join<IEInvoice, Warehouse> warehouse_=iEInvoice_.join("warehouse",JoinType.LEFT);
			Join<IEInvoice, Currency> currency_=iEInvoice_.join("currency",JoinType.LEFT);
			Join<IEInvoice, HarborCategory> harborCategory_= iEInvoice_.join("post_of_tran",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pFromDate));
			dis1.getExpressions().add(cb.greaterThanOrEqualTo(iEInvoice_.get("invoice_date"), pFromDate));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.isNull(pToDate));
			dis2.getExpressions().add(cb.lessThanOrEqualTo(iEInvoice_.get("invoice_date"), pToDate));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pCustomerId, 0));
			dis3.getExpressions().add(cb.equal(iEInvoice_.get("customer").get("id"), pCustomerId));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.equal(pInvoiceCode, ""));
			dis4.getExpressions().add(cb.equal(iEInvoice_.get("invoice_code"), pInvoiceCode));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pVoucherCode, ""));
			dis5.getExpressions().add(cb.equal(iEInvoice_.get("voucher_code"), pVoucherCode));
			predicates.add(dis5);
			Predicate dis6=cb.disjunction();
			dis6.getExpressions().add(cb.equal(pProductId, 0));
			dis6.getExpressions().add(cb.equal(product_.get("id"), pProductId));
			predicates.add(dis6);
			Predicate dis7=cb.disjunction();
			dis7.getExpressions().add(cb.equal(pIECategoriesId, 0));
			dis7.getExpressions().add(cb.equal(iEInvoice_.get("ie_categories").get("id"), pIECategoriesId));
			predicates.add(dis7);
			Predicate dis8=cb.disjunction();
			dis8.getExpressions().add(cb.equal(pContractVoucherCode, ""));
			dis8.getExpressions().add(cb.equal(contract_.get("voucher_code"), pContractVoucherCode));
			predicates.add(dis8);
//			(String voucher_code, Date invoice_date, String contract_voucher_code,
//					String customer_code, String customer_name, String license_plate, String payment_name,
//					String warehouse_name, double tax_value, Date etd_date, double exchange_rate, String currency_type,
//					String note, String bill_no, String declaration_code, String post_of_tran_code, String product_code,
//					String product_name, double quantity_export, double foreign_unit_price, double total_foreign_amount,
//					String driver_name)
			cq.select(cb.construct(IEInvoiceOrderListByProductCodeReport.class, iEInvoice_.get("voucher_code"),iEInvoice_.get("invoice_date"),contract_.get("voucher_code"), customer_.get("customer_code"),
					customer_.get("customer_name"),car_.get("license_plate"),paymentMethod_.get("method_name"),stevedore_.get("content"), warehouse_.get("name"),iEInvoice_.get("tax_value"),iEInvoice_.get("etd_date"),
					iEInvoice_.get("exchange_rate"),currency_.get("currency_type"),iEInvoice_.get("note"),iEInvoice_.get("bill_no"),iEInvoice_.get("declaration_code"),harborCategory_.get("port_no"),
					product_.get("product_code"),product_.get("product_name"),root.get("quantity_export"),root.get("foreign_unit_price"),root.get("total_foreign_amount"),iEInvoice_.get("driver_name")))
			.where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.asc(product_.get("product_code")));
			TypedQuery<IEInvoiceOrderListByProductCodeReport> query=em.createQuery(cq);
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(),null),"dd/MM/yyyy"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(),null),"dd/MM/yyyy"));
			query.setParameter(pCustomerId, Long.parseLong(Objects.toString(hCustomerId.getValue())));
			query.setParameter(pInvoiceCode, Objects.toString(hInvoiceCode.getValue(),""));
			query.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(),""));
			query.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue())));
			query.setParameter(pIECategoriesId, Long.parseLong(Objects.toString(hIECategoriesId.getValue())));
			query.setParameter(pContractVoucherCode, Objects.toString(hContractVoucherCode.getValue(),""));
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEInvoiceOrderListByProductCodeReport:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectIEInvoicListByContNoReport(String json, List<IEInvoiceListByContNoReport> list) {
		int res=-1;
		try{
//			{from_date:'',to_date:'',customer_id:0,invoice_code:'',voucher_code:'',product_id:0,ie_categories_id:0,contract_voucher_code:''}
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hFromDate=JsonParserUtil.getValueString(j, "from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j,"to_date", null);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j, "customer_id", null);
			HolderParser hInvoiceCode=JsonParserUtil.getValueString(j,"invoice_code", null);
			HolderParser hVoucherCode=JsonParserUtil.getValueString(j,"voucher_code", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j, "product_id", null);
			HolderParser hIECategoriesId=JsonParserUtil.getValueNumber(j,"ie_categories_id", null);
			HolderParser hContractVoucherCode=JsonParserUtil.getValueString(j, "contract_voucher_code", null);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<String> pInvoiceCode=cb.parameter(String.class);
			ParameterExpression<String> pVoucherCode=cb.parameter(String.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pIECategoriesId=cb.parameter(Long.class);
			ParameterExpression<String> pContractVoucherCode=cb.parameter(String.class);
			CriteriaQuery<IEInvoiceListByContNoReport> cq=cb.createQuery(IEInvoiceListByContNoReport.class);
			Root<IEInvoiceDetail> root=cq.from(IEInvoiceDetail.class);
			Join<IEInvoiceDetail, Product> product_=root.join("product",JoinType.INNER);
			Join<InvoiceDetail, IEInvoice> iEInvoice_=root.join("ie_invoice" ,JoinType.INNER);
			Join<IEInvoice,Customer> customer_=iEInvoice_.join("customer",JoinType.INNER);
			Join<IEInvoice, Contract> contract_=iEInvoice_.join("contract",JoinType.LEFT);
			Join<Customer, City> city_=customer_.join("city",JoinType.LEFT);
			iEInvoice_.join("post_of_tran",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pFromDate));
			dis1.getExpressions().add(cb.greaterThanOrEqualTo(iEInvoice_.get("invoice_date"), pFromDate));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.isNull(pToDate));
			dis2.getExpressions().add(cb.lessThanOrEqualTo(iEInvoice_.get("invoice_date"), pToDate));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pCustomerId, 0));
			dis3.getExpressions().add(cb.equal(iEInvoice_.get("customer").get("id"), pCustomerId));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.equal(pInvoiceCode, ""));
			dis4.getExpressions().add(cb.equal(iEInvoice_.get("invoice_code"), pInvoiceCode));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pVoucherCode, ""));
			dis5.getExpressions().add(cb.equal(iEInvoice_.get("voucher_code"), pVoucherCode));
			predicates.add(dis5);
			Predicate dis6=cb.disjunction();
			dis6.getExpressions().add(cb.equal(pProductId, 0));
			dis6.getExpressions().add(cb.equal(product_.get("id"), pProductId));
			predicates.add(dis6);
			Predicate dis7=cb.disjunction();
			dis7.getExpressions().add(cb.equal(pIECategoriesId, 0));
			dis7.getExpressions().add(cb.equal(iEInvoice_.get("ie_categories").get("id"), pIECategoriesId));
			predicates.add(dis7);
			Predicate dis8=cb.disjunction();
			dis8.getExpressions().add(cb.equal(pContractVoucherCode, ""));
			dis8.getExpressions().add(cb.equal(contract_.get("voucher_code"), pContractVoucherCode));
			predicates.add(dis8);
//			(String city_name, String customer_name, String ft_container, String container_no,
//					String arrival_place, String container_number, double quantity_export)
			cq.select(cb.construct(IEInvoiceListByContNoReport.class, city_.get("city_name"),customer_.get("customer_name"),root.get("ft_container"),root.get("container_no"),
					root.get("arrival_place"),root.get("container_number"),root.get("quantity_export"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<IEInvoiceListByContNoReport> query=em.createQuery(cq);
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(),null),"dd/MM/yyyy"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(),null),"dd/MM/yyyy"));
			query.setParameter(pCustomerId, Long.parseLong(Objects.toString(hCustomerId.getValue())));
			query.setParameter(pInvoiceCode, Objects.toString(hInvoiceCode.getValue(),""));
			query.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(),""));
			query.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue())));
			query.setParameter(pIECategoriesId, Long.parseLong(Objects.toString(hIECategoriesId.getValue())));
			query.setParameter(pContractVoucherCode, Objects.toString(hContractVoucherCode.getValue(),""));
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEInvoicListByContNoReport:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEInvoiceListByTaxValueZeroReport(String json, List<IEInvoiceListByTaxValueZeroReport> list) {
		int res=-1;
		try{
//			{from_date:'',to_date:'',customer_id:0,invoice_code:'',voucher_code:'',product_id:0,ie_categories_id:0,contract_voucher_code:''}
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hFromDate=JsonParserUtil.getValueString(j, "from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j,"to_date", null);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j, "customer_id", null);
			HolderParser hInvoiceCode=JsonParserUtil.getValueString(j,"invoice_code", null);
			HolderParser hVoucherCode=JsonParserUtil.getValueString(j,"voucher_code", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j, "product_id", null);
			HolderParser hIECategoriesId=JsonParserUtil.getValueNumber(j,"ie_categories_id", null);
			HolderParser hContractVoucherCode=JsonParserUtil.getValueString(j, "contract_voucher_code", null);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<String> pInvoiceCode=cb.parameter(String.class);
			ParameterExpression<String> pVoucherCode=cb.parameter(String.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			ParameterExpression<Long> pIECategoriesId=cb.parameter(Long.class);
			ParameterExpression<String> pContractVoucherCode=cb.parameter(String.class);
			CriteriaQuery<IEInvoiceListByTaxValueZeroReport> cq=cb.createQuery(IEInvoiceListByTaxValueZeroReport.class);
			Root<IEInvoiceDetail> root=cq.from(IEInvoiceDetail.class);
			Join<IEInvoiceDetail, Product> product_=root.join("product",JoinType.INNER);
			Join<InvoiceDetail, IEInvoice> iEInvoice_=root.join("ie_invoice" ,JoinType.INNER);
			Join<IEInvoice,Customer> customer_=iEInvoice_.join("customer",JoinType.INNER);
			Join<IEInvoice, Contract> contract_=iEInvoice_.join("contract",JoinType.LEFT);
			Join<Customer, City> city_=customer_.join("city",JoinType.LEFT);
			Join<City, Country> country_=city_.join("country",JoinType.INNER);
			iEInvoice_.join("post_of_tran",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pFromDate));
			dis1.getExpressions().add(cb.greaterThanOrEqualTo(iEInvoice_.get("invoice_date"), pFromDate));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.isNull(pToDate));
			dis2.getExpressions().add(cb.lessThanOrEqualTo(iEInvoice_.get("invoice_date"), pToDate));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pCustomerId, 0));
			dis3.getExpressions().add(cb.equal(iEInvoice_.get("customer").get("id"), pCustomerId));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.equal(pInvoiceCode, ""));
			dis4.getExpressions().add(cb.equal(iEInvoice_.get("invoice_code"), pInvoiceCode));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pVoucherCode, ""));
			dis5.getExpressions().add(cb.equal(iEInvoice_.get("voucher_code"), pVoucherCode));
			predicates.add(dis5);
			Predicate dis6=cb.disjunction();
			dis6.getExpressions().add(cb.equal(pProductId, 0));
			dis6.getExpressions().add(cb.equal(product_.get("id"), pProductId));
			predicates.add(dis6);
			Predicate dis7=cb.disjunction();
			dis7.getExpressions().add(cb.equal(pIECategoriesId, 0));
			dis7.getExpressions().add(cb.equal(iEInvoice_.get("ie_categories").get("id"), pIECategoriesId));
			predicates.add(dis7);
			Predicate dis8=cb.disjunction();
			dis8.getExpressions().add(cb.equal(pContractVoucherCode, ""));
			dis8.getExpressions().add(cb.equal(contract_.get("voucher_code"), pContractVoucherCode));
			predicates.add(dis8);
//			(String contract_voucher_code, Date contract_date, String customer_name,
//			String properties, String country_name, double contract_foreign_total_amount, double contract_total_amount,
//			Date payment_period, String declaration_code, Date declaration_date, String goods, double declaration_quantity,
//			double declaration_foreign_total_amount, double declaration_total_amount, String method_of_transportation,
//			String voucher_code, Date invoice_date, double quantity_export, String material_name,
//			double foreign_total_amount, double total_amount, String payment, String payment_voucher,
//			Date payment_voucher_date, double payment_voucher_foreign_total_amount, double payment_voucher_total_amount,
//			String payment_report)
			cq.select(cb.construct(IEInvoiceListByTaxValueZeroReport.class, contract_.get("voucher_code"),contract_.get("contract_date"),customer_.get("customer_name"),
					country_.get("country_name"),root.get("total_foreign_amount"),root.get("total_amount"),
					iEInvoice_.get("declaration_code"),root.get("quantity_export"),
					root.get("total_foreign_amount"),root.get("total_amount"),
					iEInvoice_.get("voucher_code"),iEInvoice_.get("invoice_date"),root.get("quantity_export"),
					root.get("total_foreign_amount"),root.get("total_amount"),iEInvoice_.get("payment"),
					root.get("total_foreign_amount"),root.get("total_amount"))
					).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<IEInvoiceListByTaxValueZeroReport> query=em.createQuery(cq);
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(),null),"dd/MM/yyyy"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(),null),"dd/MM/yyyy"));
			query.setParameter(pCustomerId, Long.parseLong(Objects.toString(hCustomerId.getValue())));
			query.setParameter(pInvoiceCode, Objects.toString(hInvoiceCode.getValue(),""));
			query.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(),""));
			query.setParameter(pProductId, Long.parseLong(Objects.toString(hProductId.getValue())));
			query.setParameter(pIECategoriesId, Long.parseLong(Objects.toString(hIECategoriesId.getValue())));
			query.setParameter(pContractVoucherCode, Objects.toString(hContractVoucherCode.getValue(),""));
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEInvoicListByContNoReport:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int selectIEInvoiceHarborBillNoDetailReport(String json, List<IEInvoiceHarborBillNoDetailReport> list) {
		int res=-1;
		try{
			/*list_ie_invoice_detail_id:[]}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hListIdDetail=JsonParserUtil.getValueList(j, "list_ie_invoice_detail_id", null,new TypeToken<List<Long>>(){}.getType());
			List<Long> listId=(List<Long>) hListIdDetail.getValue();
//			(String container_no, String ft_container, long product_id,String product_en_name, long product_type_id, String product_type_en_name, String unit,
//					double specification, double tare, double factor, double quantity_export)
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<IEInvoiceHarborBillNoDetailReport> cq=cb.createQuery(IEInvoiceHarborBillNoDetailReport.class);
			Root<IEInvoiceDetail> root=cq.from(IEInvoiceDetail.class);
			ParameterExpression<List> param=cb.parameter(List.class);
			Join<IEInvoiceDetail, Product> product=root.join("product",JoinType.INNER);
			Join<Product, ProductType>	productType=product.join("product_type",JoinType.LEFT);
			cq.select(cb.construct(IEInvoiceHarborBillNoDetailReport.class,root.get("container_no"),root.get("ft_container"),product.get("id"),product.get("en_name"),
					productType.get("id"),productType.get("en_name"),product.get("unit"),product.get("specification"),product.get("tare"),product.get("factor"),root.get("quantity_export")))
			.where(root.get("id").in(param)).orderBy(cb.asc(root.get("container_no")),cb.asc(root.get("ft_container")),cb.asc(productType.get("en_name")),cb.asc(product.get("en_name")));
			TypedQuery<IEInvoiceHarborBillNoDetailReport> query=em.createQuery(cq);
			query.setParameter(param, listId);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEInvoiceHarborBillNoDetailReport:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectIEInvoiceCustomerBillNoDetailReport(String json, List<IEInvoiceCustomerBillNoDetailReport> list) {
		int res=-1;
		try{
			/*list_ie_invoice_detail_id:[]}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hListIdDetail=JsonParserUtil.getValueList(j, "list_ie_invoice_detail_id", null,new TypeToken<List<Long>>(){}.getType());
			List<Long> listId=(List<Long>) hListIdDetail.getValue();
//			(String container_no,long product_type_id, String product_en_name, String product_type_en_name, String unit, double specification, double tare,
//					double factor, double quantity_export)
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<IEInvoiceCustomerBillNoDetailReport> cq=cb.createQuery(IEInvoiceCustomerBillNoDetailReport.class);
			Root<IEInvoiceDetail> root=cq.from(IEInvoiceDetail.class);
			ParameterExpression<List> param=cb.parameter(List.class);
			Join<IEInvoiceDetail, Product> product=root.join("product",JoinType.INNER);
			Join<Product, ProductType>	productType=product.join("product_type",JoinType.LEFT);
			cq.select(cb.construct(IEInvoiceCustomerBillNoDetailReport.class,root.get("container_no"),productType.get("id"),product.get("en_name"),
					productType.get("en_name"),product.get("unit"),product.get("specification"),product.get("tare"),product.get("factor"),root.get("quantity_export")))
			.where(root.get("id").in(param)).orderBy(cb.asc(productType.get("en_name")),cb.asc(product.get("en_name")),cb.asc(root.get("container_no")));
			TypedQuery<IEInvoiceCustomerBillNoDetailReport> query=em.createQuery(cq);
			query.setParameter(param, listId);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("IEInvoiceService.selectIEInvoiceCustomerBillNoDetailReport:"+e.getMessage(),e);
		}
		return res;
	}
}
