package trong.lixco.com.service;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.jws.WebParam;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.LinkedList;

import org.jboss.logging.Logger;

import trong.lixco.com.entity.Employee;


@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class EmployeeUseForBeanService extends AbstractService<Employee>{
	protected final Logger logger = Logger.getLogger(getClass());
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;
	
	@Override
	protected Class<Employee> getEntityClass() {
		// TODO Auto-generated method stub
		return Employee.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		// TODO Auto-generated method stub
		return em;
	}

	@Override
	protected SessionContext getUt() {
		// TODO Auto-generated method stub
		return ct;
	}
	public List<Employee> getListEmployee(Long created_by_id, Long request_by_id, String department_name,
			String chi_dinh_noi_sua, Date fromdate, Date todate, String content_request) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<Employee> root = cq.from(Employee.class);
//			if (created_by_id != null) {
//				predicates.add(cb.equal(root.get("created_by_id"), created_by_id));
//			}
//			if (request_by_id != null)
//				predicates.add(cb.equal(root.get("id"), request_by_id));
			if (chi_dinh_noi_sua != null){
				predicates.add(cb.equal(root.get("codeEmployee"), chi_dinh_noi_sua));
			}
//			if (department_name != null && !"".equals(department_name))
//				predicates.add(cb.like(root.get("codeTo"), "%" + department_name + "%"));
			if (department_name != null && !"".equals(department_name))
				predicates.add(cb.like(root.get("codeTo"), department_name));
//			if (chi_dinh_noi_sua != null) {
//				predicates.add(cb.equal(root.get("chi_dinh_noi_sua"), chi_dinh_noi_sua));
//			}

			if (fromdate != null) {
				Predicate predicateStart = cb.greaterThanOrEqualTo(root.get("ngayHienTai"), fromdate);
				predicates.add(predicateStart);
			}
			if (todate != null) {
				Predicate predicateEnd = cb.lessThanOrEqualTo(root.get("ngayHienTai"), todate);
				predicates.add(predicateEnd);
			}
//			if (content_request != null && !"".equals(content_request)) {
//				predicates.add(cb.like(root.get("content_request"), "%" + content_request + "%"));
//			}
			cq.select(root)
					.where(cb.or(
							cb.equal(root.get("codeEmployee"), chi_dinh_noi_sua)),
							cb.and(predicates.toArray(new Predicate[0])))
					.orderBy(cb.desc(root.get("id")));
			TypedQuery<Employee> query = em.createQuery(cq);
			List<Employee> results = query.getResultList();
			return results;
		} catch (Exception e) {
			logger.error("repairService.getListRepair:" + e.getMessage(), e);
		}
		return null;

	}
	
	public List<Employee> getListEmployee(Long created_by_id, Long request_by_id, String department_name, Date fromdate, Date todate, String content_request) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<Employee> root = cq.from(Employee.class);
//			if (created_by_id != null) {
//				predicates.add(cb.equal(root.get("created_by_id"), created_by_id));
//			}
//			if (request_by_id != null)
//				predicates.add(cb.equal(root.get("id"), request_by_id));
//			if (chi_dinh_noi_sua != null){
//				predicates.add(cb.equal(root.get("codeEmployee"), chi_dinh_noi_sua));
//			}
//			if (department_name != null && !"".equals(department_name))
//				predicates.add(cb.like(root.get("toSx"), "%" + department_name + "%"));
			if (department_name != null && !"".equals(department_name))
				predicates.add(cb.like(root.get("codeTo"), department_name ));
			
//			if (chi_dinh_noi_sua != null) {
//				predicates.add(cb.equal(root.get("chi_dinh_noi_sua"), chi_dinh_noi_sua));
//			}

			if (fromdate != null) {
				Predicate predicateStart = cb.greaterThanOrEqualTo(root.get("ngayHienTai"), fromdate);
				predicates.add(predicateStart);
			}
			if (todate != null) {
				Predicate predicateEnd = cb.lessThanOrEqualTo(root.get("ngayHienTai"), todate);
				predicates.add(predicateEnd);
			}
//			if (content_request != null && !"".equals(content_request)) {
//				predicates.add(cb.like(root.get("content_request"), "%" + content_request + "%"));
//			}
			cq.select(root)
					.where(
							cb.and(predicates.toArray(new Predicate[0])))
					.orderBy(cb.desc(root.get("id")));
			TypedQuery<Employee> query = em.createQuery(cq);
			List<Employee> results = query.getResultList();
			return results;
		} catch (Exception e) {
			logger.error("repairService.getListRepair:" + e.getMessage(), e);
		}
		return null;

	}
	
	public List<Employee> getEmployeeByCode(String codeEm) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<Employee> root = cq.from(Employee.class);
		predicates.add(cb.equal(root.get("codeEmployeeOld"), codeEm));

		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<Employee> query = em.createQuery(cq);
		List<Employee> results = query.getResultList();
//		if (results.size() != 0)
//			return results.get(0);
		return results;
	}
	
	public Employee getEmployeeByCodeOld(String codeEm) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<Employee> root = cq.from(Employee.class);
		predicates.add(cb.equal(root.get("codeEmployeeOld"), codeEm));

		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<Employee> query = em.createQuery(cq);
		List<Employee> results = query.getResultList();
		if (results.size() != 0)
			return results.get(0);
		return null;
	}
	
	//employee xem lương
	public List<Employee> getEmployeeByCodeNew(String codeEm) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<Employee> root = cq.from(Employee.class);
		predicates.add(cb.equal(root.get("codeEmployee"), codeEm));

		cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<Employee> query = em.createQuery(cq);
		List<Employee> results = query.getResultList();
//		if (results.size() != 0)
//			return results.get(0);
		return results;
	}
	
	public List<Employee> getListEmployee(Date fromdate, Date todate) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<Employee> root = cq.from(Employee.class);

			if (fromdate != null) {
				Predicate predicateStart = cb.greaterThanOrEqualTo(root.get("createdDate"), fromdate);
				predicates.add(predicateStart);
			}
			if (todate != null) {
				Predicate predicateEnd = cb.lessThanOrEqualTo(root.get("createdDate"), todate);
				predicates.add(predicateEnd);
			}
//			if (content_request != null && !"".equals(content_request)) {
//				predicates.add(cb.like(root.get("content_request"), "%" + content_request + "%"));
//			}
			cq.select(root)
					.where(
							cb.and(predicates.toArray(new Predicate[0])))
					.orderBy(cb.desc(root.get("id")));
			TypedQuery<Employee> query = em.createQuery(cq);
			List<Employee> results = query.getResultList();
			return results;
		} catch (Exception e) {
			logger.error("repairService.getListRepair:" + e.getMessage(), e);
		}
		return null;

	}
	
	public List<Employee> getListEmployeeSearch(Date fromdate, Date todate) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<Employee> root = cq.from(Employee.class);

			if (fromdate != null) {
				Predicate predicateStart = cb.greaterThanOrEqualTo(root.get("ngayHienTai"), fromdate);
				predicates.add(predicateStart);
			}
			if (todate != null) {
				Predicate predicateEnd = cb.lessThanOrEqualTo(root.get("ngayHienTai"), todate);
				predicates.add(predicateEnd);
			}
//			if (content_request != null && !"".equals(content_request)) {
//				predicates.add(cb.like(root.get("content_request"), "%" + content_request + "%"));
//			}
			cq.select(root)
					.where(
							cb.and(predicates.toArray(new Predicate[0])))
					.orderBy(cb.desc(root.get("id")));
			TypedQuery<Employee> query = em.createQuery(cq);
			List<Employee> results = query.getResultList();
			return results;
		} catch (Exception e) {
			logger.error("repairService.getListRepair:" + e.getMessage(), e);
		}
		return null;

	}
	
	public List<Employee> getListEmployeeForNgayLam(Date fromdate, Date todate) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<Employee> root = cq.from(Employee.class);

			if (fromdate != null) {
				Predicate predicateStart = cb.greaterThanOrEqualTo(root.get("ngayHienTai"), fromdate);
				predicates.add(predicateStart);
			}
			if (todate != null) {
				Predicate predicateEnd = cb.lessThanOrEqualTo(root.get("ngayHienTai"), todate);
				predicates.add(predicateEnd);
			}
//			if (content_request != null && !"".equals(content_request)) {
//				predicates.add(cb.like(root.get("content_request"), "%" + content_request + "%"));
//			}
			cq.select(root)
					.where(
							cb.and(predicates.toArray(new Predicate[0])))
					.orderBy(cb.desc(root.get("id")));
			TypedQuery<Employee> query = em.createQuery(cq);
			List<Employee> results = query.getResultList();
			return results;
		} catch (Exception e) {
			logger.error("repairService.getListRepair:" + e.getMessage(), e);
		}
		return null;

	}
	
	public List<Employee> getListEmployeeForNgayLamAndCode(String codeOld, Date fromdate, Date todate) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
			List<Predicate> predicates = new LinkedList<Predicate>();
			Root<Employee> root = cq.from(Employee.class);
			
			if (codeOld != null) {
				predicates.add(cb.equal(root.get("codeEmployeeOld"), codeOld));
			}
			
			if (fromdate != null) {
				Predicate predicateStart = cb.greaterThanOrEqualTo(root.get("ngayHienTai"), fromdate);
				predicates.add(predicateStart);
			}
			if (todate != null) {
				Predicate predicateEnd = cb.lessThanOrEqualTo(root.get("ngayHienTai"), todate);
				predicates.add(predicateEnd);
			}
//			if (content_request != null && !"".equals(content_request)) {
//				predicates.add(cb.like(root.get("content_request"), "%" + content_request + "%"));
//			}
			cq.select(root)
					.where(
							cb.and(predicates.toArray(new Predicate[0])))
					.orderBy(cb.asc(root.get("id")));
			TypedQuery<Employee> query = em.createQuery(cq);
			List<Employee> results = query.getResultList();
			return results;
		} catch (Exception e) {
			logger.error("repairService.getListRepair:" + e.getMessage(), e);
		}
		return null;

	}
	
}
