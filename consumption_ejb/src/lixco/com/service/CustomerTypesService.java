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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.entity.CustomerTypes;
import lixco.com.interfaces.ICustomerTypesService;
import lixco.com.reqInfo.CustomerTypesReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class CustomerTypesService implements ICustomerTypesService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<CustomerTypes> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<CustomerTypes> cq=cb.createQuery(CustomerTypes.class);
			Root<CustomerTypes> root= cq.from(CustomerTypes.class);
			cq.select(root);
			TypedQuery<CustomerTypes> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("CustomerTypesService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(CustomerTypesReqInfo t) {
		int res=-1;
		try{
			CustomerTypes p=t.getCustomer_types();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("CustomerTypesService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(CustomerTypesReqInfo t) {
		int res=-1;
		try{
			CustomerTypes p=t.getCustomer_types();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setCustomer_types(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("CustomerTypesService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, CustomerTypesReqInfo t) {
		int res=-1;
		try{
			CustomerTypes p=em.find(CustomerTypes.class,id);
			if(p!=null){
				t.setCustomer_types(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("CustomerTypesService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from CustomerTypes where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("CustomerTypesService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json, List<CustomerTypes> list) {
		int res=-1;
		try{/*{ customer_types_info:{code:'',name:''}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hGroupCode=JsonParserUtil.getValueString(j.get("customer_types_info"),"code", null);
			HolderParser hGroupName=JsonParserUtil.getValueString(j.get("customer_types_info"),"name", null);
			HolderParser hChannelID=JsonParserUtil.getValueNumber(j.get("customer_types_info"), "channel_id", null);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<CustomerTypes> cq=cb.createQuery(CustomerTypes.class);
			Root<CustomerTypes> root_= cq.from(CustomerTypes.class);
			root_.fetch("customer_channel",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<String> pCode=cb.parameter(String.class);
			ParameterExpression<String> pName=cb.parameter(String.class);
			ParameterExpression<String> pNameLike=cb.parameter(String.class);
			ParameterExpression<Long> pChannelId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pCode));
			dis.getExpressions().add(cb.equal(pCode, ""));
			dis.getExpressions().add(cb.equal(root_.get("code"), pCode));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pName));
			dis1.getExpressions().add(cb.equal(pName, ""));
			dis1.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root_.get("name")),cb.literal("đ"),cb.literal("d")), pNameLike));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.equal(pChannelId, 0));
			dis2.getExpressions().add(cb.equal(root_.get("customer_channel").get("id"), pChannelId));
			predicates.add(dis2);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(cb.asc(root_.get("id")));
			TypedQuery<CustomerTypes> query=em.createQuery(cq);
			query.setParameter(pCode, Objects.toString(hGroupCode.getValue(), null));
			query.setParameter(pName, Objects.toString(hGroupName.getValue(), null));
			query.setParameter(pChannelId,Long.parseLong(Objects.toString(hChannelID.getValue(), "0")));
			query.setParameter(pNameLike, "%"+Objects.toString(hGroupName.getValue()+"%", null));
			list.addAll(query.getResultList());
			res=0;
			
		}catch(Exception e){
			logger.error("CustomerTypesService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkCustomerTypeCode(String code, long customerTypesId) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<CustomerTypes> root= cq.from(CustomerTypes.class);
			ParameterExpression<String> pCode=cb.parameter(String.class);
			ParameterExpression<Long> pCustomerTypesId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pCustomerTypesId, 0));
			con1.getExpressions().add(cb.equal(root.get("code"),pCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pCustomerTypesId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pCustomerTypesId));
			con2.getExpressions().add(cb.equal(root.get("group_code"),pCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pCustomerTypesId, customerTypesId);
			query.setParameter(pCode, code);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("CustomerTypesService.checkCustomerTypeCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int findLike(String text, List<CustomerTypes> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<CustomerTypes> cq=cb.createQuery(CustomerTypes.class);
			Root<CustomerTypes> root= cq.from(CustomerTypes.class);
			ParameterExpression<String> paramLike=cb.parameter(String.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.like(root.get("code"),paramLike));
			dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("name")),cb.literal("đ"),cb.literal("d")), paramLike));
			cq.select(root).where(dis);
			TypedQuery<CustomerTypes> query=em.createQuery(cq);
			query.setParameter(paramLike, "%"+text+"%");
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("CustomerTypesService.findLike:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, CustomerTypesReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<CustomerTypes> cq=cb.createQuery(CustomerTypes.class);
			Root<CustomerTypes> root= cq.from(CustomerTypes.class);
			cq.select(root).where(cb.equal(root.get("code"),code));
			TypedQuery<CustomerTypes> query=em.createQuery(cq);
			t.setCustomer_types(query.getSingleResult());
			res=0;
		}catch (Exception e) {
			logger.error("CustomerTypesService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

}
