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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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
import lixco.com.entity.OrderLixPos;
import lixco.com.interfaces.IOrderLixPosService;
import lixco.com.reqInfo.OrderLixPosReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class OrderLixPosService implements IOrderLixPosService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int search(String json, PagingInfo page, List<OrderLixPos> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ order_info:{from_date:'',to_date:'',customer_id:0,order_code:'',voucher_code:'',ie_categories_id:0,po_no:'',delivered:-1,status:-1}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hFromDate=JsonParserUtil.getValueString(j.get("order_info"),"from_date", null);
			HolderParser hToDate=JsonParserUtil.getValueString(j.get("order_info"),"to_date", null);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j.get("order_info"),"customer_id", null);
			HolderParser hOrderCode=JsonParserUtil.getValueString(j.get("order_info"),"order_code", null);
			HolderParser hVoucherCode=JsonParserUtil.getValueString(j.get("order_info"),"voucher_code", null);
			HolderParser hIECategoriesId=JsonParserUtil.getValueNumber(j.get("order_info"),"ie_categories_id", null);
			HolderParser hPoNo=JsonParserUtil.getValueString(j.get("order_info"),"po_no", null);
			HolderParser hDelivered=JsonParserUtil.getValueNumber(j.get("order_info"),"delivered", null);
			HolderParser hStatus=JsonParserUtil.getValueNumber(j.get("order_info"),"status", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<OrderLixPos> cq=cb.createQuery(OrderLixPos.class);
			Root<OrderLixPos> root_= cq.from(OrderLixPos.class);
			root_.fetch("customer",JoinType.INNER);
			root_.fetch("promotion_program",JoinType.LEFT);
			root_.fetch("pricing_program",JoinType.LEFT);
			root_.fetch("payment_method",JoinType.LEFT);
			root_.fetch("car",JoinType.LEFT);
			root_.fetch("warehouse",JoinType.LEFT);
			root_.fetch("ie_categories",JoinType.LEFT);
			root_.fetch("delivery_pricing",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<String> pOrderCode=cb.parameter(String.class);
			ParameterExpression<String> pVoucherCode=cb.parameter(String.class);
			ParameterExpression<Long> pIECategoriesId=cb.parameter(Long.class);
			ParameterExpression<String> pPoNo=cb.parameter(String.class);
			ParameterExpression<Integer> pDelivered=cb.parameter(Integer.class);
			ParameterExpression<Integer> pStatus=cb.parameter(Integer.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pFromDate));
			dis.getExpressions().add(cb.equal(pFromDate,""));
			dis.getExpressions().add(cb.greaterThanOrEqualTo(root_.get("order_date"), pFromDate));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pToDate));
			dis1.getExpressions().add(cb.equal(pToDate,""));
			dis1.getExpressions().add(cb.lessThanOrEqualTo(root_.get("order_date"), pToDate));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pCustomerId,0));
			dis2.getExpressions().add(cb.equal(root_.get("customer").get("id"), pCustomerId));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.equal(pOrderCode,""));
			dis3.getExpressions().add(cb.equal(root_.get("order_code"),pOrderCode));
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
			dis6.getExpressions().add(cb.equal(pDelivered,-1));
			dis6.getExpressions().add(cb.equal(root_.get("delivered"),pDelivered));
			predicates.add(dis6);
			Predicate dis7=cb.disjunction();
			dis7.getExpressions().add(cb.equal(pPoNo,""));
			dis7.getExpressions().add(cb.equal(root_.get("po_no"),pPoNo));
			predicates.add(dis7);
			Predicate dis8=cb.disjunction();
			dis8.getExpressions().add(cb.equal(pStatus,-1));
			dis8.getExpressions().add(cb.equal(root_.get("status"),pStatus));
			predicates.add(dis8);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.desc(root_.get("order_code")));
			TypedQuery<OrderLixPos> query=em.createQuery(cq);
			query.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query.setParameter(pOrderCode, Objects.toString(hOrderCode.getValue(), ""));
			query.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(), ""));
			query.setParameter(pIECategoriesId,Long.parseLong(Objects.toString(hIECategoriesId.getValue(), "0")));
			query.setParameter(pDelivered,Integer.parseInt(Objects.toString(hDelivered.getValue(), "0")));
			query.setParameter(pPoNo, Objects.toString(hPoNo.getValue(), ""));
			query.setParameter(pStatus,Integer.parseInt(Objects.toString(hStatus.getValue(), "0")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(OrderLixPos.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pFromDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query2.setParameter(pToDate,ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue(), null),"dd/MM/yyyy HH:mm:ss"));
			query2.setParameter(pCustomerId,Long.parseLong(Objects.toString(hCustomerId.getValue(), "0")));
			query2.setParameter(pOrderCode, Objects.toString(hOrderCode.getValue(), ""));
			query2.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(), ""));
			query2.setParameter(pIECategoriesId,Long.parseLong(Objects.toString(hIECategoriesId.getValue(), "0")));
			query2.setParameter(pDelivered,Integer.parseInt(Objects.toString(hDelivered.getValue(), "0")));
			query2.setParameter(pPoNo, Objects.toString(hPoNo.getValue(), ""));
			query2.setParameter(pStatus,Integer.parseInt(Objects.toString(hStatus.getValue(), "0")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
			
		}catch(Exception e){
			logger.error("OrderLixPosService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(OrderLixPosReqInfo t) {
		int res=-1;
		try{
			OrderLixPos p=t.getOrder_lix_pos();
			if(p !=null){
				p.setOrder_code(initOrderCode());
				p.setVoucher_code(initVoucherCode(p.getOrder_date(), 0));
				int check=checkByCode(p.getOrder_code(), p.getId());
				if(check>0){
					res=-2;
				}else if(check!=-1){
					em.persist(p);
					if(p.getId()>0){
						res=0;
					}
				}
			}
		}catch(Exception e){
			logger.error("OrderLixPosService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(OrderLixPosReqInfo t) {
		int res = -1;
		try {
			OrderLixPos p = t.getOrder_lix_pos();
			if (p != null) {
				int check = checkByCode(p.getOrder_code(), p.getId());
				if (check > 0) {
					res = -2;
				} else if (check != -1) {
					p = em.merge(p);
					if (p != null) {
						OrderLixPosReqInfo f = new OrderLixPosReqInfo();
						selectById(p.getId(), f);
						t.setOrder_lix_pos(f.getOrder_lix_pos());
						res = 0;
					}
				}
			}
		} catch (Exception e) {
			logger.error("OrderLixPosService.update:" + e.getMessage(), e);
		}
		return res;
	}

	@Override
	public int selectById(long id, OrderLixPosReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<OrderLixPos> cq=cb.createQuery(OrderLixPos.class);
			Root<OrderLixPos> root_= cq.from(OrderLixPos.class);
			root_.fetch("customer",JoinType.INNER);
			root_.fetch("promotion_program",JoinType.LEFT);
			root_.fetch("pricing_program",JoinType.LEFT);
			root_.fetch("payment_method",JoinType.LEFT);
			root_.fetch("car",JoinType.LEFT);
			root_.fetch("warehouse",JoinType.LEFT);
			root_.fetch("ie_categories",JoinType.LEFT);
			root_.fetch("delivery_pricing",JoinType.LEFT);
			cq.select(root_).where(cb.equal(root_.get("id"), id));
			TypedQuery<OrderLixPos> query=em.createQuery(cq);
			t.setOrder_lix_pos(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("OrderLixPosService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//delete tất cả chi tiết sản phẩm khuyến mãi
			Query  queryDelPromotionOrderDetailPos=em.createQuery("delete p from PromotionOrderDetailPos as p inner join  p.order_detail_pos as dt where dt.order_lix_pos.id =:id ");
			queryDelPromotionOrderDetailPos.setParameter("id", id);
			if(queryDelPromotionOrderDetailPos.executeUpdate() >=0){
				// thực hiện xóa chi tiết đơn hàng
				Query queryDelOrderDetailPos=em.createQuery("delete from OrderDetailPos as dt where dt.order_lix_pos=:id");
				queryDelOrderDetailPos.setParameter("id", id);
				if(queryDelOrderDetailPos.executeUpdate()>=0){
					//thực hiện delete đơn hàng
					Query queryDelOrderLixPos=em.createQuery("delete from OrderLixPos as d where d.id=:id");
					queryDelOrderLixPos.setParameter("id", id);
					if(queryDelOrderLixPos.executeUpdate()>=0){
						res=0;
					}else{
						ct.getUserTransaction().rollback();
					}
				}else{
					ct.getUserTransaction().rollback();
				}
				
			}else{
				ct.getUserTransaction().rollback();
			}
		}catch(Exception e){
			logger.error("OrderLixPosService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public String initOrderCode() {
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<OrderLixPos> root= cq.from(OrderLixPos.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)root.get("order_code"),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/100000000;
			if(p<1){
				return String.format("%08d", max+1);
			}
			return (max+1)+"";
		}catch(Exception e){
			logger.error("OrderLixPosService.initOrderCode:"+e.getMessage(),e);
		}
		return null;
	}

	@Override
	public int selectByOrderCode(String orderCode, OrderLixPosReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<OrderLixPos> cq=cb.createQuery(OrderLixPos.class);
			Root<OrderLixPos> root_= cq.from(OrderLixPos.class);
			root_.fetch("customer",JoinType.INNER);
			root_.fetch("promotion_program",JoinType.LEFT);
			root_.fetch("pricing_program",JoinType.LEFT);
			root_.fetch("payment_method",JoinType.LEFT);
			root_.fetch("car",JoinType.LEFT);
			root_.fetch("warehouse",JoinType.LEFT);
			root_.fetch("ie_categories",JoinType.LEFT);
			root_.fetch("delivery_pricing",JoinType.LEFT);
			cq.select(root_).where(cb.equal(root_.get("order_code"), orderCode));
			TypedQuery<OrderLixPos> query=em.createQuery(cq);
			t.setOrder_lix_pos(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("OrderLixPosService.selectByOrderCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkByCode(String code, long id) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<OrderLixPos> root= cq.from(OrderLixPos.class);
			ParameterExpression<String> pCode=cb.parameter(String.class);
			ParameterExpression<Long> pId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pId, 0));
			con1.getExpressions().add(cb.equal(root.get("order_code"),pCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pId));
			con2.getExpressions().add(cb.equal(root.get("order_code"),pCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pId, id);
			query.setParameter(pCode, code);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("OrderLixPosService.checkProductCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public String initVoucherCode(Date orderDate,long id) {
		try{
			if(orderDate !=null){
				int month=ToolTimeCustomer.getMonthM(orderDate);
				int year=ToolTimeCustomer.getYearM(orderDate);
				Date minDate=ToolTimeCustomer.getDateMinCustomer(month,year);
				Date maxDate=ToolTimeCustomer.getDateMaxCustomer(month,year);
				CriteriaBuilder cb=em.getCriteriaBuilder();
				CriteriaQuery<String> cq=cb.createQuery(String.class);
				Root<OrderLixPos> root= cq.from(OrderLixPos.class);
				ParameterExpression<Long> pId=cb.parameter(Long.class);
				Predicate dis=cb.disjunction();
				Predicate con1=cb.conjunction();
				Predicate con2=cb.conjunction();
				con1.getExpressions().add(cb.equal(pId, 0));
				con1.getExpressions().add(cb.greaterThanOrEqualTo(root.get("order_date"),minDate));
				con1.getExpressions().add(cb.lessThanOrEqualTo(root.get("order_date"),maxDate));
				dis.getExpressions().add(con1);
				con2.getExpressions().add(cb.notEqual(pId, 0));
				con2.getExpressions().add(cb.notEqual(root.get("id"), pId));
				con2.getExpressions().add(cb.greaterThanOrEqualTo(root.get("order_date"),minDate));
				con2.getExpressions().add(cb.lessThanOrEqualTo(root.get("order_date"),maxDate));
				dis.getExpressions().add(con2);
				cq.select(cb.greatest(root.get("voucher_code"))).where(dis);
				TypedQuery<String> query=em.createQuery(cq);
				query.setParameter(pId, id);
				List<String> list=query.getResultList();
				String voucher=year+"-"+String.format("%02d",month)+"/";
				if(list.size()>0){
					String temp=list.get(0);
					if(temp !=null){
						int last=temp.lastIndexOf("/");
						String sub=temp.substring(last+1);
						return voucher+String.format("%05d",Integer.parseInt(sub)+1);
					}
				}
				return voucher+"00001";
			}
		}catch(Exception e){
			logger.error("OrderLixPosService.initVoucherCode:"+e.getMessage(),e);
		}
		return null;
	}
	

}
