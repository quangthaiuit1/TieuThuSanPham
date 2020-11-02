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
import javax.persistence.criteria.Subquery;

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Contract;
import lixco.com.entity.ContractDetail;
import lixco.com.entity.ContractReqInfo;
import lixco.com.interfaces.IContractService;
import lixco.com.reqInfo.ContractDetailReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class ContractService implements IContractService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int insert(ContractReqInfo t) {
		int res=-1;
		try{
			Contract p=t.getContract();
			if(p !=null){
				if(checkVoucherCode(p.getVoucher_code(), p.getId())==0){
					p.setContract_code(initContractCode());
					em.persist(p);
					if(p.getId()>0){
						res=0;
					}
				}else{
					res=-2;
				}
			}
		}catch (Exception e) {
			logger.error("ContractService.insert:"+e.getMessage(),e);
		}
		return res;
	}
	private int checkVoucherCode(String code,long id) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<Contract> root= cq.from(Contract.class);
			ParameterExpression<String> pCode=cb.parameter(String.class);
			ParameterExpression<Long> pId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pId, 0));
			con1.getExpressions().add(cb.equal(root.get("voucher_code"),pCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pId));
			con2.getExpressions().add(cb.equal(root.get("voucher_code"),pCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pId, id);
			query.setParameter(pCode, code);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("ContractService.checkVoucherCode:"+e.getMessage(),e);
		}
		return res;
	}
	private String initContractCode() {
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<Contract> root= cq.from(Contract.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)root.get("contract_code"),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/100000000;
			if(p<1){
				return String.format("%08d", max+1);
			}
			return (max+1)+"";
		}catch(Exception e){
			logger.error("ContractService.initContractCode:"+e.getMessage(),e);
		}
		return null;
	}
	@Override
	public int update(ContractReqInfo t) {
		int res=-1;
		try{
			Contract p=t.getContract();
			if(p !=null){
				if(checkVoucherCode(p.getVoucher_code(), p.getId())==0){
					em.merge(p);
					if(em.merge(p)!=null){
						res=0;
					}
				}else{
					res=-2;
				}
			}
		}catch (Exception e) {
			logger.error("ContractService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json,PagingInfo page, List<Contract> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{contract:{customer_id:0,voucher_code:'',from_date:'',to_date:'',liquidated:-1},page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hCustomerId=JsonParserUtil.getValueNumber(j.get("contract"),"customer_id",null);
			HolderParser hVoucherCode=JsonParserUtil.getValueString(j.get("contract"),"voucher_code", null);
			HolderParser hFromDate=JsonParserUtil.getValueString(j.get("contract"),"from_date",null);
			HolderParser hToDate=JsonParserUtil.getValueString(j.get("contract"),"to_date", null);
			HolderParser hLiquidated=JsonParserUtil.getValueNumber(j.get("contract"),"liquidated",null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			ParameterExpression<String> pVoucherCode=cb.parameter(String.class);
			ParameterExpression<Date> pFromDate=cb.parameter(Date.class);
			ParameterExpression<Date> pToDate=cb.parameter(Date.class);
			ParameterExpression<Integer> pLiquidated=cb.parameter(Integer.class);
			CriteriaQuery<Contract> cq=cb.createQuery(Contract.class);
			Root<Contract> root=cq.from(Contract.class);
			root.fetch("customer",JoinType.INNER);
			root.fetch("currency",JoinType.INNER);
			List<Predicate> predicates=new ArrayList<>();
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pCustomerId, 0));
			dis1.getExpressions().add(cb.equal(root.get("customer").get("id"), pCustomerId));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pVoucherCode, ""));
			dis2.getExpressions().add(cb.equal(root.get("voucher_code"), pVoucherCode));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.isNull(pFromDate));
			dis3.getExpressions().add(cb.greaterThanOrEqualTo(root.get("effective_date"),pFromDate));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			dis4.getExpressions().add(cb.isNull(pToDate));
			dis4.getExpressions().add(cb.lessThanOrEqualTo(root.get("expiry_date"),pToDate));
			predicates.add(dis4);
			Predicate dis5=cb.disjunction();
			dis5.getExpressions().add(cb.equal(pLiquidated,-1));
			dis5.getExpressions().add(cb.equal(root.get("liquidated"),pLiquidated));
			predicates.add(dis5);
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Contract> query=em.createQuery(cq);
			query.setParameter(pCustomerId, Long.parseLong(Objects.toString(hCustomerId.getValue(),"0")));
			query.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(),""));
			query.setParameter(pFromDate, ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue())));
			query.setParameter(pToDate, ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue())));
			query.setParameter(pLiquidated, Integer.parseInt(Objects.toString(hLiquidated.getValue(),"-1")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root=cq1.from(Contract.class);
			cq1.select(cb.count(root.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pCustomerId, Long.parseLong(Objects.toString(hCustomerId.getValue(),"0")));
			query2.setParameter(pVoucherCode, Objects.toString(hVoucherCode.getValue(),""));
			query2.setParameter(pFromDate, ToolTimeCustomer.convertStringToDate(Objects.toString(hFromDate.getValue())));
			query2.setParameter(pToDate, ToolTimeCustomer.convertStringToDate(Objects.toString(hToDate.getValue())));
			query2.setParameter(pLiquidated, Integer.parseInt(Objects.toString(hLiquidated.getValue(),"-1")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);	
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch (Exception e) {
			logger.error("ContractService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, ContractReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Contract> cq=cb.createQuery(Contract.class);
			Root<Contract> root=cq.from(Contract.class);
			root.fetch("customer",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("id"),id));
			TypedQuery<Contract> query=em.createQuery(cq);
			t.setContract(query.getSingleResult());
			res=0;
		}catch (Exception e) {
			logger.error("ContractService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insertDetail(ContractDetailReqInfo t) {
		int res=-1;
		try{
			ContractDetail p=t.getContract_detail();
			if(p !=null){
				em.persist(p);
				if(p.getId() !=0){
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("ContractService.insertDetail:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int updateDetail(ContractDetailReqInfo t) {
		int res=-1;
		try{
			ContractDetail p=t.getContract_detail();
			if(p !=null){
				em.merge(p);
				if(em.merge(p)!=null){
					selectByIdDetail(p.getId(), t);
					res=0;
				}
			}
		}catch (Exception e) {
			logger.error("ContractService.updateDetail:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectContractDetailByContractId(long contractId,List<ContractDetail> list){
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ContractDetail> cq=cb.createQuery(ContractDetail.class);
			Root<ContractDetail> root=cq.from(ContractDetail.class);
			root.fetch("product",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("contract").get("id"), contractId));
			TypedQuery<ContractDetail> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("ContractService.searchDetail:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByIdDetail(long id, ContractDetailReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ContractDetail> cq=cb.createQuery(ContractDetail.class);
			Root<ContractDetail> root=cq.from(ContractDetail.class);
			root.fetch("product",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("id"), id));
			t.setContract_detail(t.getContract_detail());
			res=0;
		}catch (Exception e) {
			logger.error("ContractService.selectByIdDetail:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			Query queryDelDetail=em.createQuery("delete from ContractDetail as p where p.contract.id=:id");
			queryDelDetail.setParameter("id", id);
			if(queryDelDetail.executeUpdate()>=0){
				Query queryDelContract=em.createQuery("delete from Contract where id=:id ");
				queryDelContract.setParameter("id", id);
				res=queryDelContract.executeUpdate();
			}else{
				ct.getUserTransaction().rollback();
			}
		}catch (Exception e) {
			logger.error("ContractService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteDetailById(long id) {
		int res=-1;
		try{
			Query queryDelDetail=em.createQuery("delete from ContractDetail as p where p.id=:id");
			queryDelDetail.setParameter("id", id);
			res=queryDelDetail.executeUpdate();
		}catch(Exception e){
			logger.error("ContractService.deleteDetailById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public String initVoucherCode() {
		String last=null;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<String> cq=cb.createQuery(String.class);
			Root<Contract> root=cq.from(Contract.class);
			Subquery<Long> sub1=cq.subquery(Long.class);
			Root<Contract> rootSub=sub1.from(Contract.class);
			sub1.select(cb.max(rootSub.get("id")));
			cq.select(root.get("voucher_code")).where(cb.equal(root.get("id"), sub1));
			TypedQuery<String> query=em.createQuery(cq);
			List<String> list=query.getResultList();
			if(list.size() >0){
				last= list.get(0);
				if(last.contains("/")){
					String str=last.split("/")[0];
					return String.format("%03d", Integer.parseInt(str)+1) +"/"+ToolTimeCustomer.getYearCurrent();
				}
			}
			
		}catch(Exception e){
			logger.error("ContractService.initVoucherCode:"+e.getMessage(),e);
			return last;
		}
		return "001/"+ToolTimeCustomer.getYearCurrent();
	}
	@Override
	public int complete(String text, List<Contract> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Contract> cq=cb.createQuery(Contract.class);
			Root<Contract> root=cq.from(Contract.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(root.get("contract_code"),text));
			dis.getExpressions().add(cb.equal(root.get("voucher_code"),text));
			cq.select(cb.construct(Contract.class,root.get("id"),root.get("contract_code"),root.get("voucher_code"),
					root.get("customer"),root.get("currency"))).where(dis);
			TypedQuery<Contract> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("ContractService.complete:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectContractDetail(String json, List<ContractDetail> list) {
		int res=-1;
		try{
			/*{contract_id:0,product_id:0}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hContractId=JsonParserUtil.getValueNumber(j, "contract_id", null);
			HolderParser hProductId=JsonParserUtil.getValueNumber(j, "product_id", null);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			ParameterExpression<Long> pContractId=cb.parameter(Long.class);
			ParameterExpression<Long> pProductId=cb.parameter(Long.class);
			List<Predicate> predicates = new ArrayList<>();
			CriteriaQuery<ContractDetail> cq=cb.createQuery(ContractDetail.class);
			Root<ContractDetail> root=cq.from(ContractDetail.class);
			root.fetch("product",JoinType.INNER);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pContractId, 0));
			dis1.getExpressions().add(cb.equal(root.get("contract").get("id"), pContractId));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pProductId, 0));
			dis2.getExpressions().add(cb.equal(root.get("product"), pProductId));
			predicates.add(dis2);
			cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<ContractDetail> query=em.createQuery(cq);
			query.setParameter(pContractId,Long.parseLong(Objects.toString(hContractId.getValue())));
			query.setParameter(pProductId,Long.parseLong(Objects.toString(hProductId.getValue())));
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ContractService.selectContractDetail:"+e.getMessage(),e);
		}
		return res;
	}

}
