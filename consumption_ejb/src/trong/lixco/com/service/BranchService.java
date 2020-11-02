package trong.lixco.com.service;


import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.logging.Logger;

import trong.lixco.com.entity.Branch;


@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class BranchService extends AbstractService<Branch>{

	protected final Logger logger = Logger.getLogger(getClass());
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;
	
	@Override
	protected Class<Branch> getEntityClass() {
		// TODO Auto-generated method stub
		return Branch.class;
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

}
