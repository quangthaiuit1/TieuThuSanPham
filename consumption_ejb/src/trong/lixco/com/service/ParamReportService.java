package trong.lixco.com.service;


import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import trong.lixco.com.entity.ParamReport;




@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ParamReportService  extends AbstractService<ParamReport>{
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	@Override
	protected SessionContext getUt() {
		return ct;
	}
	
	@Override
	protected Class<ParamReport> getEntityClass() {
		return ParamReport.class;
	}
}
