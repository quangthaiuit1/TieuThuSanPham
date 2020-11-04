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
import lixco.com.entity.City;
import lixco.com.interfaces.ICityService;
import lixco.com.reqInfo.CityReqInfo;

@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class CityService implements ICityService {
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;

	@Override
	public int selectAll(List<City> list) {
		int res = -1;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<City> cq = cb.createQuery(City.class);
			Root<City> root = cq.from(City.class);
			cq.select(root);
			TypedQuery<City> query = em.createQuery(cq);
			list.addAll(query.getResultList());
			res = 0;
		} catch (Exception e) {
			logger.error("CityService.selectAll:" + e.getMessage(), e);
		}
		return res;
	}

	@Override
	public int insert(CityReqInfo t) {
		int res = -1;
		try {
			City p = t.getCity();
			if (p != null) {
				em.persist(p);
				if (p.getId() > 0) {
					res = 0;
				}
			}
		} catch (Exception e) {
			logger.error("CityService.insert:" + e.getMessage(), e);
		}
		return res;
	}

	@Override
	public int update(CityReqInfo t) {
		int res = -1;
		try {
			City p = t.getCity();
			if (p != null) {
				p = em.merge(p);
				if (p != null) {
					t.setCity(p);
					res = 0;
				}
			}
		} catch (Exception e) {
			logger.error("CityService.update:" + e.getMessage(), e);
		}
		return res;
	}

	@Override
	public int selectById(long id, CityReqInfo t) {
		int res = -1;
		try {
			City p = em.find(City.class, id);
			if (p != null) {
				t.setCity(p);
				res = 0;
			}
		} catch (Exception e) {
			logger.error("CityService.selectById:" + e.getMessage(), e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res = -1;
		try {
			// JQPL
			Query query = em.createQuery("delete from City where id=:id ");
			query.setParameter("id", id);
			res = query.executeUpdate();
		} catch (Exception e) {
			logger.error("CityService.deleteById:" + e.getMessage(), e);
		}
		return res;
	}

	@Override
	public int search(String json, PagingInfo page, List<City> list) {
		int res = -1;
		long totalRow = 0;
		long totalPage = 0;
		try {/*
				 * { city_info:{city_code:'',city_name:'',country_id:0,area_id:0},
				 * page:{page_index:0, page_size:0}}
				 */
			JsonObject j = JsonParserUtil.getGson().fromJson(json, JsonObject.class);
			HolderParser hCityCode = JsonParserUtil.getValueString(j.get("city_info"), "city_code", null);
			HolderParser hCityName = JsonParserUtil.getValueString(j.get("city_info"), "city_name", null);
			HolderParser hCountryId = JsonParserUtil.getValueNumber(j.get("city_info"), "country_id", null);
			HolderParser hAreaId = JsonParserUtil.getValueNumber(j.get("city_info"), "area_id", null);
			HolderParser hPageIndex = JsonParserUtil.getValueNumber(j.get("page"), "page_index", null);
			HolderParser hPageSize = JsonParserUtil.getValueNumber(j.get("page"), "page_size", null);
			int pageIndex = Integer.parseInt(Objects.toString(hPageIndex.getValue(), "0"));
			int pageSize = Integer.parseInt(Objects.toString(hPageSize.getValue(), "0"));
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<City> cq = cb.createQuery(City.class);
			Root<City> root_ = cq.from(City.class);
			root_.fetch("country", JoinType.LEFT);
			root_.fetch("area", JoinType.LEFT);
			List<Predicate> predicates = new ArrayList<Predicate>();
			ParameterExpression<String> pCityCode = cb.parameter(String.class);
			ParameterExpression<String> pCityName = cb.parameter(String.class);
			ParameterExpression<String> pCityNameLike = cb.parameter(String.class);
			ParameterExpression<Long> pCountryId = cb.parameter(Long.class);
			ParameterExpression<Long> pAreaId = cb.parameter(Long.class);
			Predicate dis = cb.disjunction();
			dis.getExpressions().add(cb.isNull(pCityCode));
			dis.getExpressions().add(cb.equal(pCityCode, ""));
			dis.getExpressions().add(cb.equal(root_.get("city_code"), pCityCode));
			predicates.add(dis);
			Predicate dis1 = cb.disjunction();
			dis1.getExpressions().add(cb.isNull(pCityName));
			dis1.getExpressions().add(cb.equal(pCityName, ""));
			dis1.getExpressions().add(cb.like(root_.get("city_name"), pCityNameLike));
			predicates.add(dis1);
			Predicate dis2 = cb.disjunction();
			dis2.getExpressions().add(cb.equal(pCountryId, 0));
			dis2.getExpressions().add(cb.equal(root_.get("country").get("id"), pCountryId));
			predicates.add(dis2);
			Predicate dis3 = cb.disjunction();
			dis3.getExpressions().add(cb.equal(pAreaId, 0));
			dis3.getExpressions().add(cb.equal(root_.get("area").get("id"), pAreaId));
			predicates.add(dis3);
			cq.select(root_).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<City> query = em.createQuery(cq);
			query.setParameter(pCityCode, Objects.toString(hCityCode.getValue(), null));
			query.setParameter(pCityName, Objects.toString(hCityName.getValue(), null));
			query.setParameter(pCityNameLike, "%" + Objects.toString(hCityName.getValue(), null) + "%");
			query.setParameter(pCountryId, Long.parseLong(Objects.toString(hCountryId.getValue(), "0")));
			query.setParameter(pAreaId, Long.parseLong(Objects.toString(hAreaId.getValue(), "0")));
			query.setFirstResult((pageIndex - 1) * pageSize);
			query.setMaxResults(pageSize);
			list.addAll(query.getResultList());
			// paging
			CriteriaQuery<Long> cq1 = cb.createQuery(Long.class);
			root_ = cq1.from(City.class);
			cq1.select(cb.count(root_.get("id"))).where(cb.and(predicates.toArray(new Predicate[0])));
			TypedQuery<Long> query2 = em.createQuery(cq1);
			query2.setParameter(pCityCode, Objects.toString(hCityCode.getValue(), null));
			query2.setParameter(pCityName, Objects.toString(hCityName.getValue(), null));
			query2.setParameter(pCityNameLike, "%" + Objects.toString(hCityName.getValue(), null) + "%");
			query2.setParameter(pCountryId, Long.parseLong(Objects.toString(hCountryId.getValue(), "0")));
			query2.setParameter(pAreaId, Long.parseLong(Objects.toString(hAreaId.getValue(), "0")));
			totalRow = query2.getSingleResult();
			if (pageSize != 0) {
				totalPage = (long) Math.ceil((double) totalRow / pageSize);
			}
			page.setTotalRow(totalRow);
			page.setTotalPage(totalPage);
			res = 0;
		} catch (Exception e) {
			logger.error("CityService.search:" + e.getMessage(), e);
		}
		return res;
	}

	@Override
	public int checkCityCode(String code, long cityId) {
		int res = -1;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<City> root = cq.from(City.class);
			ParameterExpression<String> pCityCode = cb.parameter(String.class);
			ParameterExpression<Long> pCityId = cb.parameter(Long.class);
			Predicate dis = cb.disjunction();
			Predicate con1 = cb.conjunction();
			Predicate con2 = cb.conjunction();
			con1.getExpressions().add(cb.equal(pCityId, 0));
			con1.getExpressions().add(cb.equal(root.get("city_code"), pCityCode));
			dis.getExpressions().add(con1);
			con2.getExpressions().add(cb.notEqual(pCityId, 0));
			con2.getExpressions().add(cb.notEqual(root.get("id"), pCityId));
			con2.getExpressions().add(cb.equal(root.get("city_code"), pCityCode));
			dis.getExpressions().add(con2);
			cq.select(cb.count(root.get("id"))).where(dis);
			TypedQuery<Long> query = em.createQuery(cq);
			query.setParameter(pCityId, cityId);
			query.setParameter(pCityCode, code);
			res = query.getSingleResult().intValue();
		} catch (Exception e) {
			logger.error("CityService.checkCityCode:" + e.getMessage(), e);
		}
		return res;
	}

	@Override
	public int findLike(String text, int size, List<City> list) {
		int res = -1;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<City> cq = cb.createQuery(City.class);
			Root<City> root = cq.from(City.class);
			ParameterExpression<String> paramLike = cb.parameter(String.class);
			Predicate dis = cb.disjunction();
			dis.getExpressions().add(cb.like(root.get("city_code"), paramLike));
			dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("city_name")),
					cb.literal("đ"), cb.literal("d")), paramLike));
			cq.select(root).where(dis);
			TypedQuery<City> query = em.createQuery(cq);
			query.setParameter(paramLike, "%" + text + "%");
			if (size != -1) {
				query.setFirstResult(0);
				query.setMaxResults(size);
			}
			list.addAll(query.getResultList());
			res = 0;
		} catch (Exception e) {
			logger.error("CityService.findLike:" + e.getMessage(), e);
		}
		return res;
	}

	@Override
	public int selectByCode(String code, CityReqInfo t) {
		int res = -1;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<City> cq = cb.createQuery(City.class);
			Root<City> root = cq.from(City.class);
			cq.select(root).where(cb.equal(root.get("city_code"), code));
			TypedQuery<City> query = em.createQuery(cq);
			t.setCity(query.getSingleResult());
			res = 0;
		} catch (Exception e) {
			// logger.error("CityService.selectByCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int complete(String text, List<City> list) {
		int res = -1;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<City> cq = cb.createQuery(City.class);
			Root<City> root = cq.from(City.class);
			Predicate dis = cb.disjunction();
			dis.getExpressions().add(cb.equal(root.get("city_code"), text));
			dis.getExpressions().add(cb.like(cb.function("replace", String.class, cb.lower(root.get("city_name")),
					cb.literal("đ"), cb.literal("d")), "%" + text + "%"));
			cq.select(cb.construct(City.class, root.get("id"), root.get("city_code"), root.get("city_name")))
					.where(dis);
			list.addAll(em.createQuery(cq).getResultList());
			res = 0;
		} catch (Exception e) {
			logger.error("CityService.complete:" + e.getMessage(), e);
		}
		return res;
	}

}
