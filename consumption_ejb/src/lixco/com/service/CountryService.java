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
import lixco.com.common.PagingInfo;
import lixco.com.entity.Country;
import lixco.com.interfaces.ICountryService;
import lixco.com.reqInfo.CountryReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class CountryService implements ICountryService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<Country> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Country> cq=cb.createQuery(Country.class);
			Root<Country> root= cq.from(Country.class);
			cq.select(root);
			TypedQuery<Country> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("CountryService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json,PagingInfo page,List<Country> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ country_info:{country_code:'',country_name:''}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hCountryCode=JsonParserUtil.getValueString(j.get("country_info"), "country_code", null);
			HolderParser hCountryName=JsonParserUtil.getValueString(j.get("country_info"),"country_name", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"), "page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Country> cq=cb.createQuery(Country.class);
			Root<Country> root_= cq.from(Country.class);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<String> pCountryCode=cb.parameter(String.class);
			ParameterExpression<String> pCountryName=cb.parameter(String.class);
			ParameterExpression<String> pCountryNameLike=cb.parameter(String.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.isNull(pCountryCode));
			dis.getExpressions().add(cb.equal(pCountryCode, ""));
			dis.getExpressions().add(cb.equal(root_.get("country_code"), pCountryCode));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pCountryName));
			dis1.getExpressions().add(cb.equal(pCountryName, ""));
			dis1.getExpressions().add(cb.like(root_.get("country_name"),pCountryNameLike));
			predicates.add(dis1);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Country> query=em.createQuery(cq);
			query.setParameter(pCountryCode, Objects.toString(hCountryCode.getValue(), null));
			query.setParameter(pCountryName, Objects.toString(hCountryName.getValue(), null));
			query.setParameter(pCountryNameLike, "%"+Objects.toString(hCountryName.getValue(), null)+"%");
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(Country.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pCountryCode, Objects.toString(hCountryCode.getValue(), null));
			query2.setParameter(pCountryName, Objects.toString(hCountryName.getValue(), null));
			query2.setParameter(pCountryNameLike, "%"+Objects.toString(hCountryName.getValue(), null)+"%");
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch(Exception e){
			logger.error("CountryService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(CountryReqInfo t) {
		int res=-1;
		try{
			Country p=t.getCountry();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("CountryService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(CountryReqInfo t) {
		int res=-1;
		try{
			Country p=t.getCountry();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setCountry(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("CountryService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, CountryReqInfo t) {
		int res=-1;
		try{
			Country p=em.find(Country.class,id);
			if(p!=null){
				t.setCountry(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("CountryService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from Country where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("CountryService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int checkCountryCode(String code, long country_id) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Long> cq=cb.createQuery(Long.class);
			Root<Country> root= cq.from(Country.class);
			ParameterExpression<String> pCountryCode=cb.parameter(String.class);
			ParameterExpression<Long> pCountryId=cb.parameter(Long.class);
			Predicate dis=cb.disjunction();
			Predicate con1=cb.conjunction();
			Predicate con2=cb.conjunction();
			con1.getExpressions().add(cb.equal(pCountryId, 0));
			con1.getExpressions().add(cb.equal(root.get("country_code"),pCountryCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pCountryId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pCountryId));
			con2.getExpressions().add(cb.equal(root.get("country_code"),pCountryCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query=em.createQuery(cq);
			query.setParameter(pCountryId, country_id);
			query.setParameter(pCountryCode, code);
			res=query.getSingleResult().intValue();
		}catch(Exception e){
			logger.error("CountryService.checkCountryCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, CountryReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Country> cq=cb.createQuery(Country.class);
			Root<Country> root= cq.from(Country.class);
			cq.select(root).where(cb.equal(root.get("country_code"), code));
			TypedQuery<Country> query=em.createQuery(cq);
			t.setCountry(query.getSingleResult());
			res=0;
		}catch(Exception e){
//			logger.error("CountryService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int complete(String text, List<Country> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Country> cq=cb.createQuery(Country.class);
			Root<Country> root= cq.from(Country.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(root.get("country_code"), text));
			dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("country_name")),cb.literal("đ"),cb.literal("d")), "%"+text+"%"));
			cq.select(cb.construct(Country.class, root.get("id"),root.get("country_code"),root.get("country_name"))).where(dis);
			TypedQuery<Country> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("CountryService.autoComplete:"+e.getMessage(),e);
		}
		return res;
	}

}
