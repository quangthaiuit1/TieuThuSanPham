package trong.lixco.com.service;


import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.logging.Logger;

import trong.lixco.com.entity.ImagePath;


@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ImagePathService extends AbstractService<ImagePath> {
	protected final Logger logger = Logger.getLogger(getClass());
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;

	@Override
	protected Class<ImagePath> getEntityClass() {
		return ImagePath.class;
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
