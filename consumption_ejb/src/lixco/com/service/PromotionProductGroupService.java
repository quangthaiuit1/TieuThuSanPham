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
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.entity.PromotionProductGroup;
import lixco.com.interfaces.IPromotionProductGroupService;
import lixco.com.reqInfo.PromotionProductGroupReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class PromotionProductGroupService implements IPromotionProductGroupService {
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<PromotionProductGroup> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionProductGroup> cq=cb.createQuery(PromotionProductGroup.class);
			Root<PromotionProductGroup> root= cq.from(PromotionProductGroup.class);
			cq.select(root);
			TypedQuery<PromotionProductGroup> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("PromotionProductGroupService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(PromotionProductGroupReqInfo t) {
		int res=-1;
		try{
			PromotionProductGroup p=t.getPromotion_product_group();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("PromotionProductGroupService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(PromotionProductGroupReqInfo t) {
		int res=-1;
		try{
			PromotionProductGroup p=t.getPromotion_product_group();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setPromotion_product_group(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("PromotionProductGroupService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, PromotionProductGroupReqInfo t) {
		int res=-1;
		try{
			PromotionProductGroup p=em.find(PromotionProductGroup.class,id);
			if(p!=null){
				t.setPromotion_product_group(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("PromotionProductGroupService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from PromotionProductGroup where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("PromotionProductGroupService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json, List<PromotionProductGroup> list) {
		int res=-1;
		try{/*{code:'',name:''}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hCode=JsonParserUtil.getValueString(j, "code", null);
			HolderParser hName=JsonParserUtil.getValueString(j, "name", null);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionProductGroup> cq=cb.createQuery(PromotionProductGroup.class);
			Root<PromotionProductGroup> root_= cq.from(PromotionProductGroup.class);
			ParameterExpression<String> pCode=cb.parameter(String.class);
			ParameterExpression<String> pName=cb.parameter(String.class);
			ParameterExpression<String> pNameLike=cb.parameter(String.class);
			List<Predicate> predicates=new ArrayList<Predicate>();
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pCode, ""));
			dis.getExpressions().add(cb.equal(root_.get("code"), pCode));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pName, ""));
			dis1.getExpressions().add(cb.like(root_.get("name"),pNameLike));
			predicates.add(dis1);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<PromotionProductGroup> query=em.createQuery(cq);
			query.setParameter(pCode,Objects.toString(hCode.getValue(),""));
			query.setParameter(pName,Objects.toString(hName.getValue(),""));
			query.setParameter(pNameLike,"%"+Objects.toString(hName.getValue(),"")+"%");
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("PromotionProductGroupService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteAll() {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from PromotionProductGroup ");
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("PromotionProductGroupService.deleteAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, PromotionProductGroupReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionProductGroup> cq=cb.createQuery(PromotionProductGroup.class);
			Root<PromotionProductGroup> root_= cq.from(PromotionProductGroup.class);
			cq.select(root_).where(cb.equal(root_.get("code"),code));
			TypedQuery<PromotionProductGroup> query=em.createQuery(cq);
			t.setPromotion_product_group(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("PromotionProductGroupService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int complete(String text, int size, List<PromotionProductGroup> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<PromotionProductGroup> cq=cb.createQuery(PromotionProductGroup.class);
			Root<PromotionProductGroup> root_= cq.from(PromotionProductGroup.class);
			ParameterExpression<String> param=cb.parameter(String.class);
			ParameterExpression<String> paramLike=cb.parameter(String.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(root_.get("code"),param));
			dis.getExpressions().add(cb.like(root_.get("name"), paramLike));
			cq.select(cb.construct(PromotionProductGroup.class, root_.get("id"),root_.get("code"),root_.get("name"))).where(dis);
			TypedQuery<PromotionProductGroup> query=em.createQuery(cq);
			query.setParameter(param,text);
			query.setParameter(paramLike,"%"+text+"%");
			if(size!=-1){
				query.setMaxResults(size);
			}
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("PromotionProductGroupService.complete:"+e.getMessage(),e);
		}
		return res;
	}

}
