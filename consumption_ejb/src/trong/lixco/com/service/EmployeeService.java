package trong.lixco.com.service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import trong.lixco.com.entity.Employee;


//@WebService
//@Stateless
////@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
//@LocalBean
//@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@WebService(name = "httpEmployeeServiceSoap", targetNamespace = "http://httpEmployeeServiceSoap/")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public class EmployeeService extends AbstractService<Employee>{
	
	protected final Logger logger = Logger.getLogger(getClass());
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;
	@Inject
	private AccountCustomService accountCustomService;
	
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
	 public EmployeeService() {
	        // TODO Auto-generated constructor stub
	    }
	@WebMethod(operationName = "getEmployeeByCode", action = "getEmployeeByCode")
	public List<Employee> getEmployeeByCode(@WebParam(name = "codeEm")String codeEm, @WebParam(name = "ngayLam")String ngayLam) {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
		Date dateNgayLam = null;
		try {
			dateNgayLam = sf.parse(ngayLam);
		} catch (Exception e) {
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
		List<Predicate> predicates = new LinkedList<Predicate>();
		Root<Employee> root = cq.from(Employee.class);
		predicates.add(cb.equal(root.get("codeEmployee"), codeEm));
		predicates.add(cb.equal(root.get("ngayHienTai"), dateNgayLam));

		cq.select(root).where(cb.or(
				cb.equal(root.get("codeEmployee"), codeEm),
				cb.equal(root.get("ngayHienTai"), dateNgayLam)),
				cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<Employee> query = em.createQuery(cq);
		List<Employee> results = query.getResultList();
//		if (results.size() != 0)
//			return results.get(0);
		return results;
	}
	@WebMethod(operationName = "findByNgayHienTaiAndCodeEm", action = "findByNgayHienTaiAndCodeEm")
	public Employee findByNgayHienTaiAndCodeEm(@WebParam(name = "codeEm")String codeEm,@WebParam(name = "ngayHienTai") Date ngayHienTai) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
		Root<Employee> root = cq.from(Employee.class);
//		cq.select(root).where(cb.equal(root.get("accept_head_of_department_repair"), acceptRepair));
		cq.select(root).where(cb.equal(root.get("codeEmployee"), codeEm));
		cq.select(root).where(cb.equal(root.get("ngayHienTai"), ngayHienTai));
		TypedQuery<Employee> query = em.createQuery(cq);
		List<Employee> results = query.getResultList();
		if (results.size() != 0)
			return results.get(0);
		return null;
	}
	@WebMethod(operationName = "getOneAcount", action = "getOneAcount")
	public List<Object[]> getOneAcount(@WebParam(name = "user")String user, @WebParam(name = "pass")String pass){
		List<Object[]> list=new ArrayList<Object[]>();
		list = accountCustomService.getOneAcount(user, pass);
		return list;
	}
	@WebMethod(operationName = "findEmployeeByCodeNew", action = "findEmployeeByCodeNew")
	public List<Object[]> findEmployeeByCodeNew(@WebParam(name = "codeEm")String codeEm){
		List<Object[]> list=new ArrayList<Object[]>();
		list = accountCustomService.findEmployeeByCode(codeEm);
		return list;
	}
	@WebMethod(operationName = "findDepartmentById", action = "findDepartmentById")
	public List<Object[]> findDepartmentById(@WebParam(name = "department_id")Long department_id){
		List<Object[]> list=new ArrayList<Object[]>();
		list = accountCustomService.findDepartmentById(department_id);
		return list;
	}

}
