package lixco.com.service;

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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.PagingInfo;
import lixco.com.entity.Customer;
import lixco.com.entity.CustomerTypes;
import lixco.com.interfaces.ICustomerService;
import lixco.com.reqInfo.CustomerReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class CustomerService implements ICustomerService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<Customer> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Customer> cq=cb.createQuery(Customer.class);
			Root<Customer> root= cq.from(Customer.class);
			cq.select(root);
			TypedQuery<Customer> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("CustomerService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public int search(String json, PagingInfo page, List<Customer> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{customer_info:{customer_code:'',customer_name:'',tax_code:'',phone_number:'',customer_types_id:0}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hCustomerCode=JsonParserUtil.getValueString(j.get("customer_info"),"customer_code", null);
			HolderParser hCustomerName=JsonParserUtil.getValueString(j.get("customer_info"),"customer_name", null);
			HolderParser hTaxCode=JsonParserUtil.getValueString(j.get("customer_info"),"tax_code", null);
			HolderParser hPhoneNumber=JsonParserUtil.getValueString(j.get("customer_info"), "phone_number", null);
			HolderParser hCustomerTypesId=JsonParserUtil.getValueNumber(j.get("customer_info"),"customer_types_id", null);
			HolderParser hPageIndex=JsonParserUtil.getValueString(j.get("page"),"page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueString(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Customer> cq=cb.createQuery(Customer.class);
			Root<Customer> root_= cq.from(Customer.class);
			root_.fetch("customer_types",JoinType.INNER);
			root_.fetch("city",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<String> pCustomerCode=cb.parameter(String.class);
			ParameterExpression<String> pCustomerName=cb.parameter(String.class);
			ParameterExpression<String> pCustomerNameLike=cb.parameter(String.class);
			ParameterExpression<String> pTaxCode=cb.parameter(String.class);
			ParameterExpression<String> pPhoneNumber=cb.parameter(String.class);
			ParameterExpression<Long> pCustomerTypesId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con =cb.conjunction();
			dis.getExpressions().add(cb.isNull(pCustomerCode));
			dis.getExpressions().add(cb.equal(pCustomerCode,""));
			con.getExpressions().add(cb.notEqual(pCustomerCode, ""));
			con.getExpressions().add(cb.equal(root_.get("customer_code"), pCustomerCode));
			dis.getExpressions().add(con);
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			Predicate con1=cb.conjunction();
			dis1.getExpressions().add(cb.isNull(pCustomerName));
			dis1.getExpressions().add(cb.equal(pCustomerName,""));
			con1.getExpressions().add(cb.notEqual(pCustomerName, ""));
			con1.getExpressions().add(cb.like(root_.get("customer_name"), pCustomerNameLike));
			dis1.getExpressions().add(con1);
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			Predicate con2=cb.conjunction();
			dis2.getExpressions().add(cb.isNull(pTaxCode));
			dis2.getExpressions().add(cb.equal(pTaxCode,""));
			con2.getExpressions().add(cb.notEqual(pTaxCode, ""));
			con2.getExpressions().add(cb.equal(root_.get("tax_code"), pTaxCode));
			dis2.getExpressions().add(con2);
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.isNull(pPhoneNumber));
			dis3.getExpressions().add(cb.equal(pPhoneNumber,""));
			dis3.getExpressions().add(cb.equal(root_.get("cell_phone"), pPhoneNumber));
			dis3.getExpressions().add(cb.equal(root_.get("home_phone"), pPhoneNumber));
			predicates.add(dis3);
			Predicate dis4=cb.disjunction();
			Predicate con4=cb.conjunction();
			dis4.getExpressions().add(cb.equal(pCustomerTypesId,0));
			con4.getExpressions().add(cb.notEqual(pCustomerTypesId, 0));
			con4.getExpressions().add(cb.equal(root_.get("customer_types").get("id"), pCustomerTypesId));
			dis4.getExpressions().add(con4);
			predicates.add(dis4);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Customer> query=em.createQuery(cq);
			query.setParameter(pCustomerCode, Objects.toString(hCustomerCode.getValue(), null));
			query.setParameter(pCustomerName, Objects.toString(hCustomerName.getValue(), null));
			query.setParameter(pCustomerNameLike, "%"+Objects.toString(hCustomerName.getValue(), null)+"%");
			query.setParameter(pTaxCode, Objects.toString(hTaxCode.getValue(), null));
			query.setParameter(pPhoneNumber, Objects.toString(hPhoneNumber.getValue(), null));
			query.setParameter(pCustomerTypesId, Long.parseLong(Objects.toString(hCustomerTypesId.getValue(),"0")));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(Customer.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pCustomerCode, Objects.toString(hCustomerCode.getValue(), null));
			query2.setParameter(pCustomerName, Objects.toString(hCustomerName.getValue(), null));
			query2.setParameter(pCustomerNameLike, "%"+Objects.toString(hCustomerName.getValue(), null)+"%");
			query2.setParameter(pTaxCode, Objects.toString(hTaxCode.getValue(), null));
			query2.setParameter(pPhoneNumber, Objects.toString(hPhoneNumber.getValue(), null));
			query2.setParameter(pCustomerTypesId, Long.parseLong(Objects.toString(hCustomerTypesId.getValue(),"0")));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch(Exception e){
			logger.error("CustomerService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(CustomerReqInfo t) {
		int res=-1;
		try{
			Customer p=t.getCustomer();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("CustomerService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(CustomerReqInfo t) {
		int res=-1;
		try{
			Customer p=t.getCustomer();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setCustomer(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("CustomerService.update:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectById(long id, CustomerReqInfo info) {
		int res=-1;
		try{
			Customer p=em.find(Customer.class,id);
			if(p!=null){
				info.setCustomer(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("CustomerService.selectById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from Customer where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("CustomerService.delete:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int checkCustomerCode(String customerCode,long customerId) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<Customer> root= cq.from(Customer.class);
			ParameterExpression<String> pCustomerCode=cb.parameter(String.class);
			ParameterExpression<Long> pCustomerId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pCustomerId, 0));
			con1.getExpressions().add(cb.equal(root.get("customer_code"),pCustomerCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pCustomerId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pCustomerId));
			con2.getExpressions().add(cb.equal(root.get("customer_code"),pCustomerCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pCustomerId, customerId);
			query.setParameter(pCustomerCode, customerCode);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("CustomerService.checkCustomerCode:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int autoComplete(String text, int size, List<Customer> list) {
		return 0;
	}
	@Override
	public int selectAllByCustomerTypes(long customerTypesId, List<Customer> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Customer> cq=cb.createQuery(Customer.class);
			Root<Customer> root= cq.from(Customer.class);
			cq.select(root).where(cb.equal(root.get("customer_types").get("id"),customerTypesId));
			TypedQuery<Customer> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("CustomerService.selectAllByCustomerTypes:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int complete(String text, List<Customer> list) {
		int res=-1;
		try{
			if(text !=null && !"".equals(text)){
				CriteriaBuilder cb=em.getCriteriaBuilder();
				CriteriaQuery<Customer> cq=cb.createQuery(Customer.class);
				Root<Customer> root= cq.from(Customer.class);
				Predicate dis=cb.disjunction();
				dis.getExpressions().add(cb.equal(root.get("customer_code"),text));
				dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("customer_name")),cb.literal("đ"),cb.literal("d")), "%"+text+"%"));
				cq.select(cb.construct(Customer.class,root.get("id"),root.get("customer_code"),root.get("customer_name"))).where(dis);
				TypedQuery<Customer> query=em.createQuery(cq);
				list.addAll(query.getResultList());
			}
			res=0;
		}catch(Exception e){
			logger.error("CustomerService.complete<Text,list>:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int complete(String text, CustomerTypes customerTypes, List<Customer> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Customer> cq=cb.createQuery(Customer.class);
			Root<Customer> root= cq.from(Customer.class);
			Predicate con=cb.conjunction();
			if(customerTypes !=null){
			   con.getExpressions().add(cb.equal(root.get("customer_types").get("id"),customerTypes.getId()));
			}
			if(text !=null && !"".equals(text)){
				Predicate dis=cb.disjunction();
				dis.getExpressions().add(cb.equal(root.get("customer_code"),text));
				dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("customer_name")),cb.literal("đ"),cb.literal("d")), "%"+text+"%"));
				con.getExpressions().add(dis);
			}
			cq.select(cb.construct(Customer.class,root.get("id"),root.get("customer_code"),root.get("customer_name"))).where(con);
			TypedQuery<Customer> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
			
			
		}catch(Exception e){
			logger.error("CustomerService.complete<text,customerTypes,list>:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectByCode(String code, CustomerReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Customer> cq=cb.createQuery(Customer.class);
			Root<Customer> root= cq.from(Customer.class);
			cq.select(root).where(cb.equal(root.get("customer_code"), code));
			TypedQuery<Customer> query=em.createQuery(cq);
			t.setCustomer(query.getSingleResult());
		}catch(Exception e){
//			logger.error("CustomerService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

}
