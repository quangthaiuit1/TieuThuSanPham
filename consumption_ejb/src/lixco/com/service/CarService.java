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
import lixco.com.common.PagingInfo;
import lixco.com.entity.Car;
import lixco.com.interfaces.ICarService;
import lixco.com.reqInfo.CarReqInfo;

@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class CarService implements ICarService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<Car> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Car> cq=cb.createQuery(Car.class);
			Root<Car> root= cq.from(Car.class);
			cq.select(root);
			TypedQuery<Car> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("CarService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(CarReqInfo t) {
		int res=-1;
		try{
			Car p=t.getCar();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("CarService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(CarReqInfo t) {
		int res=-1;
		try{
			Car p=t.getCar();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   selectById(p.getId(), t);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("CarService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, CarReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Car> cq=cb.createQuery(Car.class);
			Root<Car> root= cq.from(Car.class);
			root.fetch("car_owner",JoinType.LEFT);
			root.fetch("car_type",JoinType.LEFT);
			cq.select(root).where(cb.equal(root.get("id"), id));
			TypedQuery<Car> query=em.createQuery(cq);
			t.setCar(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("CarService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from Car where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("CarService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int search(String json, PagingInfo page, List<Car> list) {
		int res=-1;
		long totalRow=0;
		long totalPage=0;
		try{/*{ car_info:{car_owner_id:0,car_type_id:0,license_plate:'',phone_number:''}, page:{page_index:0, page_size:0}}*/
			JsonObject j=JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hCarOwnerId=JsonParserUtil.getValueNumber(j.get("car_info"), "car_owner_id", null);
			HolderParser hCarTypeId=JsonParserUtil.getValueNumber(j.get("car_info"), "car_type_id", null);
			HolderParser hLicensePlate=JsonParserUtil.getValueString(j.get("car_info"),"license_plate", null);
			HolderParser hPhoneNumber=JsonParserUtil.getValueString(j.get("car_info"),"phone_number", null);
			HolderParser hPageIndex=JsonParserUtil.getValueNumber(j.get("page"), "page_index", null);
			HolderParser hPageSize=JsonParserUtil.getValueNumber(j.get("page"),"page_size", null);
			int pageIndex=Integer.parseInt(Objects.toString(hPageIndex.getValue(),"0"));
			int pageSize=Integer.parseInt(Objects.toString(hPageSize.getValue(),"0"));
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Car> cq=cb.createQuery(Car.class);
			Root<Car> root_= cq.from(Car.class);
			root_.fetch("car_owner",JoinType.LEFT);
			root_.fetch("car_type",JoinType.LEFT);
			List<Predicate> predicates=new ArrayList<Predicate>();
			ParameterExpression<Long> pCarOwnerId=cb.parameter(Long.class);
			ParameterExpression<Long> pCarTypeId=cb.parameter(Long.class);
			ParameterExpression<String> pLicensePlate=cb.parameter(String.class);
			ParameterExpression<String> pLicensePlateLike=cb.parameter(String.class);
			ParameterExpression<String> pPhoneNumber=cb.parameter(String.class);
			ParameterExpression<String> pPhoneNumberLike=cb.parameter(String.class);
			Predicate dis=cb.disjunction();
			dis.getExpressions().add(cb.equal(pCarOwnerId, 0));
			dis.getExpressions().add(cb.equal(root_.get("car_owner").get("id"), pCarOwnerId));
			predicates.add(dis);
			Predicate dis1=cb.disjunction();
			dis1.getExpressions().add(cb.equal(pCarOwnerId, 0));
			dis1.getExpressions().add(cb.equal(root_.get("car_type").get("id"), pCarTypeId));
			predicates.add(dis1);
			Predicate dis2=cb.disjunction();
			dis2.getExpressions().add(cb.isNull(pLicensePlate));
			dis2.getExpressions().add(cb.equal(pLicensePlate, ""));
			dis2.getExpressions().add(cb.like(root_.get("license_plate"), pLicensePlateLike));
			predicates.add(dis2);
			Predicate dis3=cb.disjunction();
			dis3.getExpressions().add(cb.isNull(pPhoneNumber));
			dis3.getExpressions().add(cb.equal(pPhoneNumber, ""));
			dis3.getExpressions().add(cb.like(root_.get("phone_number"), pPhoneNumberLike));
			predicates.add(dis3);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Car> query=em.createQuery(cq);
			query.setParameter(pCarOwnerId,Long.parseLong(Objects.toString(hCarOwnerId.getValue(), "0")));
			query.setParameter(pCarTypeId, Long.parseLong(Objects.toString(hCarTypeId.getValue(), "0")));
			query.setParameter(pLicensePlate, Objects.toString(hLicensePlate.getValue(), null));
			query.setParameter(pLicensePlateLike,"%"+Objects.toString(hLicensePlate.getValue()+"%", null));
			query.setParameter(pPhoneNumber, Objects.toString(hPhoneNumber.getValue(), null));
			query.setParameter(pPhoneNumberLike, "%"+Objects.toString(hPhoneNumber.getValue()+"%", null));
			query.setFirstResult((pageIndex-1)*pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			//paging
			CriteriaQuery<Long> cq1=cb.createQuery(Long.class);
			root_=cq1.from(Car.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2=em.createQuery(cq1);
			query2.setParameter(pCarOwnerId,Long.parseLong(Objects.toString(hCarOwnerId.getValue(), "0")));
			query2.setParameter(pCarTypeId, Long.parseLong(Objects.toString(hCarTypeId.getValue(), "0")));
			query2.setParameter(pLicensePlate, Objects.toString(hLicensePlate.getValue(), null));
			query2.setParameter(pLicensePlateLike,"%"+Objects.toString(hLicensePlate.getValue()+"%", null));
			query2.setParameter(pPhoneNumber, Objects.toString(hPhoneNumber.getValue(), null));
			query2.setParameter(pPhoneNumberLike, "%"+Objects.toString(hPhoneNumber.getValue()+"%", null));
			totalRow=query2.getSingleResult();
			if(pageSize !=0){
				totalPage=(long) Math.ceil((double)totalRow/pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res=0;
		}catch(Exception e){
			logger.error("CarService.search:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectByLicensePlate(String licensePlate, CarReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Car> cq=cb.createQuery(Car.class);
			Root<Car> root= cq.from(Car.class);
			cq.select(root).where(cb.equal(root.get("license_plate"), licensePlate));
			TypedQuery<Car> query=em.createQuery(cq);
			t.setCar(query.getSingleResult());
			res=0;
		}catch(Exception e){
//			logger.error("CarService.selectByLicensePlate:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int complete(String text, List<Car> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Car> cq=cb.createQuery(Car.class);
			Root<Car> root= cq.from(Car.class);
			cq.select(cb.construct(Car.class, root.get("id"),root.get("license_plate"),root.get("driver"))).where(cb.like(root.get("license_plate"),"%"+text+"%"));
			TypedQuery<Car> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("CarService.complete:"+e.getMessage(),e);
		}
		return res;
	}

}
