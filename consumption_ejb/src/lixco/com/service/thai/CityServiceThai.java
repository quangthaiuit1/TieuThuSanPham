package lixco.com.service.thai;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import lixco.com.entity.City;
import trong.lixco.com.service.AbstractService;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CityServiceThai extends AbstractService<City> {
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;

	@Override
	protected Class<City> getEntityClass() {
		return City.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}
}
