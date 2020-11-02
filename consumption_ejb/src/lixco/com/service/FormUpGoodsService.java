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

import lixco.com.entity.FormUpGoods;
import lixco.com.interfaces.IFormUpGoodsService;
import lixco.com.reqInfo.FormUpGoodsReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class FormUpGoodsService implements IFormUpGoodsService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource
	private SessionContext ct;
	@Override
	public int selectAll(List<FormUpGoods> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<FormUpGoods> cq=cb.createQuery(FormUpGoods.class);
			Root<FormUpGoods> root= cq.from(FormUpGoods.class);
			cq.select(root);
			TypedQuery<FormUpGoods> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("FormUpGoodsService.selectAll:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int insert(FormUpGoodsReqInfo t) {
		int res=-1;
		try{
			FormUpGoods p=t.getForm_up_goods();
			if(p !=null){
				em.persist(p);
				if(p.getId()>0){
					res=0;
				}
			}
		}catch(Exception e){
			logger.error("FormUpGoodsService.insert:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int update(FormUpGoodsReqInfo t) {
		int res=-1;
		try{
			FormUpGoods p=t.getForm_up_goods();
			if(p !=null){
				p=em.merge(p);
				if(p !=null){
				   selectById(p.getId(), t);
				   res=0;
				}
			}
		}catch(Exception e){
			logger.error("FormUpGoodsService.update:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int selectById(long id, FormUpGoodsReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<FormUpGoods> cq=cb.createQuery(FormUpGoods.class);
			Root<FormUpGoods> root= cq.from(FormUpGoods.class);
			cq.select(root).where(cb.equal(root.get("id"), id));
			TypedQuery<FormUpGoods> query=em.createQuery(cq);
			t.setForm_up_goods(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("FormUpGoodsService.selectById:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int deleteById(long id) {
		int res=-1;
		try{
			//JQPL
			Query query=em.createQuery("delete from FormUpGoods where id=:id ");
			query.setParameter("id",id);
			res=query.executeUpdate();
		}catch(Exception e){
			logger.error("FormUpGoodsService.deleteById:"+e.getMessage(),e);
		}
		return res;
	}

}
