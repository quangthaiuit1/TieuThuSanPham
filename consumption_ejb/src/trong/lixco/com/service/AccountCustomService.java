package trong.lixco.com.service;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jboss.logging.Logger;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class AccountCustomService extends AbstractService<Object>{
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	@PersistenceContext( unitName="account_jpa2")
	private EntityManager account;
	@Resource
	private SessionContext ct;
	
	@Override
	protected Class<Object> getEntityClass() {
		return Object.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		// TODO Auto-generated method stub
		return account;
	}

	@Override
	protected SessionContext getUt() {
		// TODO Auto-generated method stub
		return ct;
	}
	public List<Object[]> getAllAcount(){
		List<Object[]> list=new ArrayList<Object[]>();
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT userName,password FROM account");
		Query query = account.createNativeQuery(sql.toString());
		list.clear();
		list.addAll(query.getResultList());
		return list;
	}
	public List<Object[]> getOneAcount(String user, String pass){
		List<Object[]> list=new ArrayList<Object[]>();
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT userName,password,member_id FROM account WHERE account.userName ='"+user+"' AND account.password = '"+pass+"'");
		Query query = account.createNativeQuery(sql.toString());
		list.clear();
		list.addAll(query.getResultList());
		
		List<Object[]> listMem = new ArrayList<Object[]>();
		for (int i = 0; i < list.size(); i++) {
			Object[] obMem = list.get(i);
			StringBuilder sqlMem = new StringBuilder();
			sqlMem.append("SELECT id,name,code FROM member WHERE member.id ="+obMem[2]);
			Query queryMem = account.createNativeQuery(sqlMem.toString());
			listMem.clear();
			listMem.addAll(queryMem.getResultList());
		}
		
		return listMem;
	}
	//search employee for code
	public List<Object[]> findEmployeeByCode(String codeEmployee){
		List<Object[]> list = new ArrayList<>();
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT department_id, name FROM member WHERE member.code="+codeEmployee);
		Query query = account.createNativeQuery(sql.toString());
		list.clear();
		list.addAll(query.getResultList());
		return list;
	}
	//search department for department_id
	public List<Object[]> findDepartmentById(Long department_id){
		List<Object[]> list = new ArrayList<>();
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT code, name FROM department WHERE department.id="+department_id);
		Query query = account.createNativeQuery(sql.toString());
		list.clear();
		list.addAll(query.getResultList());
		return list;
	}
	//login search account
	public List<Object[]> getAcount(String user, String pass){
		List<Object[]> list=new ArrayList<Object[]>();
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT userName,password,id,member_id FROM account WHERE account.userName ='"+user+"' AND account.password = '"+pass+"'");
		Query query = account.createNativeQuery(sql.toString());
		list.clear();
		list.addAll(query.getResultList());
		return list;
	}
	//search member
	public List<Object[]> getMember(Long member_id){
		List<Object[]> list=new ArrayList<Object[]>();
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT name,code FROM member WHERE member.id ="+member_id);
		Query query = account.createNativeQuery(sql.toString());
		list.clear();
		list.addAll(query.getResultList());
		return list;
	}
}
