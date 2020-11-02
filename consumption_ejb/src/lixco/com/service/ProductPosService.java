package lixco.com.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import lixco.com.entity.BatchPos;
import lixco.com.entity.ExportBatchPos;
import lixco.com.entity.Pos;
import lixco.com.entity.Product;
import lixco.com.entity.ProductPos;
import lixco.com.interfaces.IProductPosService;
import lixco.com.reqInfo.ProductPosReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class ProductPosService implements IProductPosService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Override
	public int exportPos(long productId, List<ProductPos> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductPos> cq=cb.createQuery(ProductPos.class);
			Root<ProductPos> root= cq.from(ProductPos.class);
			Join<ProductPos, BatchPos> batchPos_=(Join)root.fetch("batch_pos",JoinType.INNER);
			Join<ProductPos, Pos> pos_=(Join)root.fetch("pos",JoinType.INNER);
			pos_.fetch("warehouse",JoinType.INNER);
			Predicate con= cb.conjunction();
			con.getExpressions().add(cb.equal(batchPos_.get("product").get("id"), productId));
			con.getExpressions().add(cb.greaterThan(root.get("quantity_import"),root.get("quantity_export")));
			con.getExpressions().add(cb.equal(root.get("status"),1));
			cq.select(root).where(con).orderBy(cb.asc(batchPos_.get("manufacture_date")),
					cb.asc(pos_.get("warehouse")),cb.asc(pos_.get("row_stack")),cb.asc(pos_.get("floorb")),cb.asc(pos_.get("pos_code")));
			TypedQuery<ProductPos> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch (Exception e) {
			logger.error("ProductPosService.exportPos:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int selectById(long id, ProductPosReqInfo t) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductPos> cq=cb.createQuery(ProductPos.class);
			Root<ProductPos> root= cq.from(ProductPos.class);
			Join<ProductPos, BatchPos> batchPos_=(Join)root.fetch("batch_pos",JoinType.INNER);
			Join<ProductPos, Pos> pos_=(Join)root.fetch("pos",JoinType.INNER);
			pos_.fetch("warehouse",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("id"),id));
			TypedQuery<ProductPos> query=em.createQuery(cq);
			t.setProduct_pos(query.getSingleResult());
			res=0;
		}catch(Exception e){
			logger.error("ProductPosService.selectById:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int getListProducPutPos(long posId, List<Object[]> list) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq=cb.createQuery(Object[].class);
			Root<ProductPos> root= cq.from(ProductPos.class);
			Join<ProductPos, BatchPos> batchPos_=root.join("batch_pos",JoinType.INNER);
			cq.multiselect(batchPos_.get("product").get("id"),cb.sum(cb.diff(root.get("quantity_import"),root.get("quantity_export")))).where(cb.equal(root.get("pos").get("id"), posId));
			TypedQuery<Object[]> query=em.createQuery(cq);
			list.addAll(query.getResultList());
			res=0;
		}catch(Exception e){
			logger.error("ProductPosService.getListProducPutPos:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int getTotalPalletPutPos(long posId) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Object> cq=cb.createQuery(Object.class);
			Root<ProductPos> root= cq.from(ProductPos.class);
			Join<ProductPos, BatchPos> batchPos_=root.join("batch_pos",JoinType.INNER);
			Join<BatchPos, Product> product_=batchPos_.join("product",JoinType.INNER);
			cq.multiselect(cb.sum(cb.quot(cb.diff(root.get("quantity_import"),root.get("quantity_export")),product_.get("box_quantity")))).
					where(cb.equal(root.get("pos").get("id"), posId));
			TypedQuery<Object> query=em.createQuery(cq);
			Object result=  query.getSingleResult();
			res=(int) Math.ceil(Double.parseDouble(Objects.toString(result, "0")));
		}catch (Exception e) {
			logger.error("ProductPosService.getTotalPalletPutPos:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int getTotalPalletPutPosOutPP(long posId, long productPosId) {
		int res=-1;
		try{
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Object> cq=cb.createQuery(Object.class);
			Root<ProductPos> root= cq.from(ProductPos.class);
			Join<ProductPos, BatchPos> batchPos_=root.join("batch_pos",JoinType.INNER);
			Join<BatchPos, Product> product_=batchPos_.join("product",JoinType.INNER);
			cq.multiselect(cb.sum(cb.quot(cb.diff(root.get("quantity_import"),root.get("quantity_export")),product_.get("box_quantity")))).
					where(cb.and(cb.equal(root.get("pos").get("id"), posId),cb.notEqual(root.get("id"), productPosId)));
			TypedQuery<Object> query=em.createQuery(cq);
			Object result=  query.getSingleResult();
			res=(int) Math.ceil(Double.parseDouble(Objects.toString(result, "0")));
		}catch (Exception e) {
			logger.error("ProductPosService.getTotalPalletPutPosOutPP:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int exportPosInvoiceDetail(long productId, long invoiceDetailId,List<ExportBatchPos> listExportBatchPos, List<ProductPos> list) {
		int res=-1;
		try{
			//lấy lô hàng xuất của chi tiết hóa đơn
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ExportBatchPos> cq=cb.createQuery(ExportBatchPos.class);
			Root<ExportBatchPos> root= cq.from(ExportBatchPos.class);
			Fetch<ExportBatchPos, ProductPos> productPos_=root.fetch("product_pos",JoinType.INNER);
			Join<ProductPos, Pos> pos_=(Join)productPos_.fetch("pos",JoinType.INNER);
			pos_.fetch("warehouse",JoinType.INNER);
			Join<ProductPos,BatchPos> batchPos_=(Join) productPos_.fetch("batch_pos",JoinType.INNER);
			cq.select(root).where(cb.equal(root.get("invoice_detail_pos").get("id"),invoiceDetailId)).orderBy(cb.asc(batchPos_.get("manufacture_date")),
					cb.asc(pos_.get("warehouse")),cb.asc(pos_.get("row_stack")),cb.asc(pos_.get("floorb")),cb.asc(pos_.get("pos_code")));
			TypedQuery<ExportBatchPos> query=em.createQuery(cq);
			listExportBatchPos.addAll(query.getResultList());
			//danh sách vị trí đặt lô hàng còn tồn trong kho.
			CriteriaQuery<ProductPos> cq2=cb.createQuery(ProductPos.class);
			Root<ProductPos> root2=cq2.from(ProductPos.class);
			Join<ProductPos, BatchPos> batchPos2_=(Join)root2.fetch("batch_pos",JoinType.INNER);
			Join<ProductPos, Pos> pos2_=(Join)root2.fetch("pos",JoinType.INNER);
			pos2_.fetch("warehouse",JoinType.INNER);
			Predicate con= cb.conjunction();
			con.getExpressions().add(cb.equal(batchPos2_.get("product").get("id"), productId));
			con.getExpressions().add(cb.greaterThan(root2.get("quantity_import"),root2.get("quantity_export")));
			con.getExpressions().add(cb.equal(root2.get("status"),1));
			if(listExportBatchPos.size()>0){
				List<Long> listTemp=new ArrayList<>();
				for(ExportBatchPos ex:listExportBatchPos){
					listTemp.add(ex.getProduct_pos().getId());
				}
				
				con.getExpressions().add(root2.get("id").in(listTemp).not());
			}
			cq2.select(root2).where(con).orderBy(cb.asc(batchPos2_.get("manufacture_date")),
					cb.asc(pos2_.get("warehouse")),cb.asc(pos2_.get("row_stack")),cb.asc(pos2_.get("floorb")),cb.asc(pos2_.get("pos_code")));
			TypedQuery<ProductPos> query2=em.createQuery(cq2);
			list.addAll(query2.getResultList());
			res=0;
			
		}catch (Exception e) {
			logger.error("ProductPosService.exportPosInvoiceDetail:"+e.getMessage(),e);
		}
		return res;
	}


}
