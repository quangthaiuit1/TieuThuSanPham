package lixco.com.service;

import java.util.List;

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
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import lixco.com.entity.CustomerChannel;
import lixco.com.interfaces.ICustomerChannelService;
import lixco.com.reqInfo.CustomerChannelReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class CustomerChannelService implements ICustomerChannelService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<CustomerChannel> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<CustomerChannel> cq=cb.createQuery(CustomerChannel.class);
			Root<CustomerChannel> root= cq.from(CustomerChannel.class);
			cq.select(root);
			TypedQuery<CustomerChannel> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("CustomerChannelService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(CustomerChannelReqInfo t) {
		int res=-1;
		try{
			CustomerChannel p=t.getCustomer_channel();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("CustomerChannelService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(CustomerChannelReqInfo t) {
		int res=-1;
		try{
			CustomerChannel p=t.getCustomer_channel();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   t.setCustomer_channel(p);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("CustomerChannelService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, CustomerChannelReqInfo t) {
		int res=-1;
		try{
			CustomerChannel p=em.find(CustomerChannel.class,id);
			if(p!=null){
				t.setCustomer_channel(p);
				res=0;
			}
		}catch(Exception e){
			logger.error("CustomerChannelService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from CustomerChannel where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("CustomerChannelService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

}
