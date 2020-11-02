package lixco.com.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;

import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Batch;
import lixco.com.entity.BatchPos;
import lixco.com.entity.DeliveryPricing;
import lixco.com.entity.ExportBatch;
import lixco.com.entity.ExportBatchPos;
import lixco.com.entity.IEInvoice;
import lixco.com.entity.IEInvoiceDetail;
import lixco.com.entity.Invoice;
import lixco.com.entity.InvoiceDetail;
import lixco.com.entity.InvoiceDetailPos;
import lixco.com.entity.OrderDetail;
import lixco.com.entity.OrderLix;
import lixco.com.entity.ProductPos;
import lixco.com.entity.PromotionOrderDetail;
import lixco.com.interfaces.IProcessLogicInvoiceService;
import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.WrapDataInvoiceDetail;
import lixco.com.reqInfo.WrapDelExportBatchReqInfo;
import lixco.com.reqInfo.WrapDelInvoiceDetailReqInfo;
import lixco.com.reqInfo.WrapExportDataPosReqInfo;
import lixco.com.reqInfo.WrapExportDataReqInfo;
import lixco.com.reqInfo.WrapIEInvocieReqInfo;
import lixco.com.reqInfo.WrapInvoiceDelInfo;
import lixco.com.reqInfo.WrapInvoiceReqInfo;
import lixco.com.reqInfo.WrapOrderDetailLixReqInfo;
import lixco.com.reqInfo.WrapOrderLixReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.BEAN)
public class ProcessLogicInvoiceService implements IProcessLogicInvoiceService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource(lookup = "java:/consumption")
	DataSource datasource;
	@Resource
	private UserTransaction ut;
	@Override
	public int deleteInvoiceMaster(WrapInvoiceDelInfo t, Message message) throws IllegalStateException, SystemException, SQLException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			//load trans Invoice
			CriteriaQuery<Invoice> cqInvoice=cb.createQuery(Invoice.class);
			Root<Invoice> rootInvoice=cqInvoice.from(Invoice.class);
			//lấy hóa đơn sở hữu nếu đây là hóa đơn khuyến mãi
			rootInvoice.fetch("invoice_own",JoinType.LEFT);
			cqInvoice.select(rootInvoice).where(cb.equal(rootInvoice.get("id"), t.getInvoice_id()));
			TypedQuery<Invoice> queryInvoice=em.createQuery(cqInvoice);
			List<Invoice> listInvoiceTrans=queryInvoice.getResultList();
			if(listInvoiceTrans.size()==0){
				res=-1;
				ut.rollback();
				message.setUser_message("Hóa đơn không tồn tại!");
				message.setInternal_message("");
				return res;
			}
			Invoice invoiceTrans=listInvoiceTrans.get(0);
			Invoice invoiceOwnTrans=invoiceTrans.getInvoice_own();
			//lấy danh sách chi tiết hóa đơn
			CriteriaQuery<InvoiceDetail> cq = cb.createQuery(InvoiceDetail.class);
			Root<InvoiceDetail> rootDetail=cq.from(InvoiceDetail.class);
			cq.select(rootDetail).where(cb.equal(rootDetail.get("invoice").get("id"), invoiceTrans.getId()));
			TypedQuery<InvoiceDetail> queryDetail =em.createQuery(cq);
			List<InvoiceDetail> listDetail=queryDetail.getResultList();
			for(InvoiceDetail dt:listDetail){
				//Trả lại số lượng cho lô hàng
				CriteriaQuery<ExportBatch> cqExportBatch = cb.createQuery(ExportBatch.class);
				Root<ExportBatch> rootExportBatch = cqExportBatch.from(ExportBatch.class);
				rootExportBatch.fetch("batch",JoinType.INNER);
				cqExportBatch.select(rootExportBatch).where(cb.equal(rootExportBatch.get("invoice_detail").get("id"), dt.getId()));
				TypedQuery<ExportBatch> queryExportBatch=em.createQuery(cqExportBatch);
				List<ExportBatch> listEBTrans=queryExportBatch.getResultList();
				for(ExportBatch ex:listEBTrans){
					Batch batchTrans=ex.getBatch();
					//cập nhật lại số lượng nhâp chô lô hàng
					double quantityImportBatch=BigDecimal.valueOf(batchTrans.getQuantity_import()).add(BigDecimal.valueOf(ex.getQuantity())).doubleValue();
					batchTrans.setQuantity_import(quantityImportBatch);
					//giảm số lượng xuất của lô hàng
					double quantityExportBatch=BigDecimal.valueOf(batchTrans.getQuantity_export()).subtract(BigDecimal.valueOf(ex.getQuantity())).doubleValue();
					batchTrans.setQuantity_export(quantityExportBatch);
					batchTrans.setLast_modifed_by(t.getMember_name());
					batchTrans.setLast_modifed_date(new Date());
					//cập nhật lô hàng
					if(em.merge(batchTrans)==null){
						res=-1;
						ut.rollback();
						message.setUser_message("Trả lại số lượng lô hàng thất bại!");
						message.setInternal_message("error merge batch "+batchTrans.getBatch_code()+" invoice_detail_id "+dt.getId());
						return res;
					}
				}
				// xóa tất cả lô hàng xuất theo invoice_detail_id(ExportBatch)
				Query queryDelExportBatch = em.createQuery("delete from ExportBatch where invoice_detail.id=:did ");
				queryDelExportBatch.setParameter("did", dt.getId());
				int check = queryDelExportBatch.executeUpdate();
				if(check <0){
					res=-1;
					ut.rollback();
					message.setUser_message("Xóa lô hàng xuất thất bại!");
					message.setInternal_message("error delete ExportBatch by invocie_detail_id "+dt.getId());
					return res;
				}
			}
			//nếu là hóa đơn chính
			if(invoiceOwnTrans==null){
				//tìm hóa đơn khuyến mãi và xóa
				CriteriaQuery<Invoice> cqInvoiceKM=cb.createQuery(Invoice.class);
				Root<Invoice> rootInvoiceKM=cqInvoiceKM.from(Invoice.class);
				cqInvoiceKM.select(rootInvoiceKM).where(cb.equal(rootInvoiceKM.get("invoice_own").get("id"), invoiceTrans.getId()));
				TypedQuery<Invoice> queryInvoiceKM=em.createQuery(cqInvoiceKM);
				List<Invoice> list=queryInvoiceKM.getResultList();
				for(Invoice d:list){
					//lấy danh sách chi tiết của hóa đơn khuyến mãi
					CriteriaQuery<InvoiceDetail> cqDetailKM = cb.createQuery(InvoiceDetail.class);
					Root<InvoiceDetail> rootDetailKM=cqDetailKM.from(InvoiceDetail.class);
					cq.select(rootDetailKM).where(cb.equal(rootDetailKM.get("invoice").get("id"), d.getId()));
					TypedQuery<InvoiceDetail> queryDetailKM =em.createQuery(cqDetailKM);
					List<InvoiceDetail> listDetailKM=queryDetailKM.getResultList();
					for(InvoiceDetail dtkm:listDetailKM){
						//Trả lại số lượng cho lô hàng
						CriteriaQuery<ExportBatch> cqExportBatchKM = cb.createQuery(ExportBatch.class);
						Root<ExportBatch> rootExportBatchKM = cqExportBatchKM.from(ExportBatch.class);
						rootExportBatchKM.fetch("batch",JoinType.INNER);
						cqExportBatchKM.select(rootExportBatchKM).where(cb.equal(rootExportBatchKM.get("invoice_detail").get("id"), dtkm.getId()));
						TypedQuery<ExportBatch> queryExportBatchKM=em.createQuery(cqExportBatchKM);
						List<ExportBatch> listEBTransKM=queryExportBatchKM.getResultList();
						for(ExportBatch exkm:listEBTransKM){
							Batch batchTransKM=exkm.getBatch();
							//cập nhật lại số lượng nhâp chô lô hàng
							double quantityImportBatchKM=BigDecimal.valueOf(batchTransKM.getQuantity_import()).add(BigDecimal.valueOf(exkm.getQuantity())).doubleValue();
							batchTransKM.setQuantity_import(quantityImportBatchKM);
							//giảm số lượng xuất của lô hàng
							double quantityExportBatchKM=BigDecimal.valueOf(batchTransKM.getQuantity_export()).subtract(BigDecimal.valueOf(exkm.getQuantity())).doubleValue();
							batchTransKM.setQuantity_export(quantityExportBatchKM);
							batchTransKM.setLast_modifed_by(t.getMember_name());
							batchTransKM.setLast_modifed_date(new Date());
							//cập nhật lô hàng
							if(em.merge(batchTransKM)==null){
								res=-1;
								ut.rollback();
								message.setUser_message("Trả lại số lượng lô hàng thất bại!");
								message.setInternal_message("error merge batch "+batchTransKM.getBatch_code()+" invoice_detail_id "+dtkm.getId());
								return res;
							}
						}
						// xóa tất cả lô hàng xuất theo invoice_detail_id(ExportBatch)
						Query queryDelExportBatch = em.createQuery("delete from ExportBatch where invoice_detail.id=:did ");
						queryDelExportBatch.setParameter("did", dtkm.getId());
						int check = queryDelExportBatch.executeUpdate();
						if(check <0){
							res=-1;
							ut.rollback();
							message.setUser_message("Xóa lô hàng xuất thất bại!");
							message.setInternal_message("error delete ExportBatch by invocie_detail_id "+dtkm.getId());
							return res;
						}
					}
					//xóa tất cả chi tiết hóa đơn khuyến mãi.
					Query queryDelDetailKM=em.createQuery("delete from InvoiceDetail where invoice.id=:id ");
					queryDelDetailKM.setParameter("id", d.getId());
					if(queryDelDetailKM.executeUpdate()<0){
						res=-1;
						ut.rollback();
						message.setUser_message("Xóa chi tiết hóa đơn khuyến mãi thất bại!");
						message.setInternal_message("error delete InvoiceDetail by invocie "+d.getId());
						return res;
					}
					//xóa hóa đơn khuyến mãi
					Query queryDelInvoiceKM=em.createQuery("delete from Invoice where id = :id ");
					queryDelInvoiceKM.setParameter("id", d.getId());
					if(queryDelDetailKM.executeUpdate()<0){
						res=-1;
						ut.rollback();
						message.setUser_message("Xóa hóa đơn khuyến mãi thất bại!");
						message.setInternal_message("error delete Invoice "+d.getId());
						return res;
					}
					//sau khi xóa hóa đơn khuyến mãi xong ta xóa hóa đơn chính
					//xóa chi tiết hóa đơn chính
					Query queryDelDetailMain=em.createQuery("delete from InvoiceDetail where invoice.id=:id ");
					queryDelDetailMain.setParameter("id", invoiceTrans.getId());
					if(queryDelDetailMain.executeUpdate()<0){
						res=-1;
						ut.rollback();
						message.setUser_message("Xóa chi tiết hóa đơn khuyến mãi thất bại!");
						message.setInternal_message("error delete InvoiceDetail by invocie "+invoiceTrans.getId());
						return res;
					}
					//xóa hóa đơn chính.
					Query queryDelInvoiceMain=em.createQuery("delete from Invoice where id = :id ");
					queryDelInvoiceMain.setParameter("id", invoiceTrans.getId());
					if(queryDelInvoiceMain.executeUpdate()<0){
						res=-1;
						ut.rollback();
						message.setUser_message("Xóa hóa đơn khuyến mãi thất bại!");
						message.setInternal_message("error delete Invoice "+invoiceTrans.getId());
						return res;
					}
				}
			}else{//nếu đây là hóa dơn khuyến mãi thì InvoiceOwnTrans khác null (InvoiceOwnTrans là hóa đơn chính)
				//lấy danh sách chi tiết của hóa đơn chính.
				CriteriaQuery<InvoiceDetail> cqDetailMain=cb.createQuery(InvoiceDetail.class);
				Root<InvoiceDetail> rootDetailMain=cqDetailMain.from(InvoiceDetail.class);
				cqDetailMain.select(rootDetailMain).where(cb.equal(rootDetailMain.get("invoice").get("id"), invoiceOwnTrans.getId()));
				TypedQuery<InvoiceDetail> queryDetailMain=em.createQuery(cqDetailMain);
				List<InvoiceDetail> listDetailMain=queryDetailMain.getResultList();
				for(InvoiceDetail dtm:listDetailMain){
					//Trả lại số lượng cho lô hàng
					CriteriaQuery<ExportBatch> cqExportBatchMain = cb.createQuery(ExportBatch.class);
					Root<ExportBatch> rootExportBatchMain = cqExportBatchMain.from(ExportBatch.class);
					rootExportBatchMain.fetch("batch",JoinType.INNER);
					cqExportBatchMain.select(rootExportBatchMain).where(cb.equal(rootExportBatchMain.get("invoice_detail").get("id"), dtm.getId()));
					TypedQuery<ExportBatch> queryExportBatchMain=em.createQuery(cqExportBatchMain);
					List<ExportBatch> listEBTransMain=queryExportBatchMain.getResultList();
					for(ExportBatch exm:listEBTransMain){
						Batch batchTransMain=exm.getBatch();
						//cập nhật lại số lượng nhâp chô lô hàng
						double quantityImportBatchMain=BigDecimal.valueOf(batchTransMain.getQuantity_import()).add(BigDecimal.valueOf(exm.getQuantity())).doubleValue();
						batchTransMain.setQuantity_import(quantityImportBatchMain);
						//giảm số lượng xuất của lô hàng
						double quantityExportBatchMain=BigDecimal.valueOf(batchTransMain.getQuantity_export()).subtract(BigDecimal.valueOf(exm.getQuantity())).doubleValue();
						batchTransMain.setQuantity_export(quantityExportBatchMain);
						batchTransMain.setLast_modifed_by(t.getMember_name());
						batchTransMain.setLast_modifed_date(new Date());
						//cập nhật lô hàng
						if(em.merge(batchTransMain)==null){
							res=-1;
							ut.rollback();
							message.setUser_message("Trả lại số lượng lô hàng thất bại!");
							message.setInternal_message("error merge batch "+batchTransMain.getBatch_code()+" invoice_detail_id "+dtm.getId());
							return res;
						}
					}
					// xóa tất cả lô hàng xuất theo invoice_detail_id(ExportBatch)
					Query queryDelExportBatch = em.createQuery("delete from ExportBatch where invoice_detail.id=:did ");
					queryDelExportBatch.setParameter("did", dtm.getId());
					int check = queryDelExportBatch.executeUpdate();
					if(check <0){
						res=-1;
						ut.rollback();
						message.setUser_message("Xóa lô hàng xuất thất bại!");
						message.setInternal_message("error delete ExportBatch by invocie_detail_id "+dtm.getId());
						return res;
					}
				}
				//xóa tất cả chi tiết hóa đơn khuyến mãi
				Query queryDelDetailKM=em.createQuery("delete from InvoiceDetail where invoice.id=:id ");
				queryDelDetailKM.setParameter("id", invoiceTrans.getId());
				if(queryDelDetailKM.executeUpdate()<0){
					res=-1;
					ut.rollback();
					message.setUser_message("Xóa chi tiết hóa đơn khuyến mãi thất bại!");
					message.setInternal_message("error delete InvoiceDetail by invocie "+invoiceTrans.getId());
					return res;
				}
				//xóa hóa đơn khuyến mãi
				Query queryDelInvoiceKM=em.createQuery("delete from Invoice where id = :id ");
				queryDelInvoiceKM.setParameter("id", invoiceTrans.getId());
				if(queryDelDetailKM.executeUpdate()<0){
					res=-1;
					ut.rollback();
					message.setUser_message("Xóa hóa đơn khuyến mãi thất bại!");
					message.setInternal_message("error delete Invoice "+invoiceTrans.getId());
					return res;
				}
				//sau khi xóa hóa đơn khuyến mãi xong xóa luôn hóa đơn chính.
				//xóa chi tiết hóa đơn chính
				Query queryDelDetailMain=em.createQuery("delete from InvoiceDetail where invoice.id=:id ");
				queryDelDetailMain.setParameter("id", invoiceOwnTrans.getId());
				if(queryDelDetailMain.executeUpdate()<0){
					res=-1;
					ut.rollback();
					message.setUser_message("Xóa chi tiết hóa đơn khuyến mãi thất bại!");
					message.setInternal_message("error delete InvoiceDetail by invocie "+invoiceOwnTrans.getId());
					return res;
				}
				//xóa hóa đơn chính.
				Query queryDelInvoiceMain=em.createQuery("delete from Invoice where id = :id ");
				queryDelInvoiceMain.setParameter("id", invoiceOwnTrans.getId());
				if(queryDelInvoiceMain.executeUpdate()<0){
					res=-1;
					ut.rollback();
					message.setUser_message("Xóa hóa đơn khuyến mãi thất bại!");
					message.setInternal_message("error delete Invoice "+invoiceOwnTrans.getId());
					return res;
				}
			}
			res=0;
			ut.commit();
			
		}catch(Exception e){
			res=-1;
			ut.rollback();
			message.setUser_message("Xóa hóa đơn thất bại!");
			message.setInternal_message("ProcessLogicInvoiceService.deleteInvoice:"+e.getMessage());
			logger.error("ProcessLogicInvoiceService.deleteInvoice:"+e.getMessage(),e);
		}finally {
			if(con!=null){
				con.close();
			}
		}
		return res;
	}
	@Override
	public int deleteInvoiceDetailMaster(WrapDelInvoiceDetailReqInfo t, Message message)throws IllegalStateException, SystemException, SQLException {
		int res = -1;
		Connection con = null;
		try {
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<InvoiceDetail> cqDetail=cb.createQuery(InvoiceDetail.class);
			Root<InvoiceDetail> root=cqDetail.from(InvoiceDetail.class);
			root.fetch("invoice_detail_own",JoinType.LEFT);
			cqDetail.select(root).where(cb.equal(root.get("id"), t.getId()));
			TypedQuery<InvoiceDetail> query=em.createQuery(cqDetail);
			List<InvoiceDetail> list=query.getResultList();
			if(list.size()==0){
				res=-1;
				ut.rollback();
				message.setUser_message("Chi tiết hóa đơn không tồn tại");
				message.setInternal_message("");
			}
			InvoiceDetail invoiceDetailTrans=list.get(0);
			InvoiceDetail invoiceDetailOwnTrans=invoiceDetailTrans.getInvoice_detail_own(); 
			//nếu là chi tiết của hóa đơn chính
			if(invoiceDetailOwnTrans==null){
				//lấy danh sách chi tiết của hóa đơn khuyến mãi
				CriteriaQuery<InvoiceDetail> cqDetailKM=cb.createQuery(InvoiceDetail.class);
				Root<InvoiceDetail> rootKM=cqDetailKM.from(InvoiceDetail.class);
				cqDetailKM.select(rootKM).where(cb.equal(rootKM.get("invoice_detail_own"),invoiceDetailTrans.getId()));
				TypedQuery<InvoiceDetail> queryKM=em.createQuery(cqDetailKM);
				List<InvoiceDetail> listDetailKM=queryKM.getResultList();
				if(listDetailKM.size()>0){
					for(InvoiceDetail dtkm:listDetailKM){
						//Trả lại số lượng cho lô hàng
						CriteriaQuery<ExportBatch> cqExportBatchKM = cb.createQuery(ExportBatch.class);
						Root<ExportBatch> rootExportBatchKM = cqExportBatchKM.from(ExportBatch.class);
						rootExportBatchKM.fetch("batch",JoinType.INNER);
						cqExportBatchKM.select(rootExportBatchKM).where(cb.equal(rootExportBatchKM.get("invoice_detail").get("id"), dtkm.getId()));
						TypedQuery<ExportBatch> queryExportBatchKM=em.createQuery(cqExportBatchKM);
						List<ExportBatch> listEBTransKM=queryExportBatchKM.getResultList();
						if(listEBTransKM.size()>0){
							for(ExportBatch exkm:listEBTransKM){
								Batch batchTransKM=exkm.getBatch();
								//cập nhật lại số lượng nhâp chô lô hàng
								double quantityImportBatchKM=BigDecimal.valueOf(batchTransKM.getQuantity_import()).add(BigDecimal.valueOf(exkm.getQuantity())).doubleValue();
								batchTransKM.setQuantity_import(quantityImportBatchKM);
								//giảm số lượng xuất của lô hàng
								double quantityExportBatchKM=BigDecimal.valueOf(batchTransKM.getQuantity_export()).subtract(BigDecimal.valueOf(exkm.getQuantity())).doubleValue();
								batchTransKM.setQuantity_export(quantityExportBatchKM);
								batchTransKM.setLast_modifed_by(t.getMember_name());
								batchTransKM.setLast_modifed_date(new Date());
								//cập nhật lô hàng
								if(em.merge(batchTransKM)==null){
									res=-1;
									ut.rollback();
									message.setUser_message("Trả lại số lượng lô hàng cho chi tiết hóa đơn khuyến mãi liên kết thất bại!");
									message.setInternal_message("error merge batch "+batchTransKM.getBatch_code()+" invoice_detail_id "+dtkm.getId());
									return res;
								}
							}
							// xóa tất cả lô hàng xuất theo invoice_detail_id(ExportBatch)
							Query queryDelExportBatch = em.createQuery("delete from ExportBatch where invoice_detail.id=:did ");
							queryDelExportBatch.setParameter("did", dtkm.getId());
							int check = queryDelExportBatch.executeUpdate();
							if(check <=0){
								res=-1;
								ut.rollback();
								message.setUser_message("Xóa lô hàng xuất cho tiết hóa đơn khuyến mãi liên kết chi tiết hóa đơn chính thất bại!");
								message.setInternal_message("error delete ExportBatch by invocie_detail_id "+dtkm.getId());
								return res;
							}
						}
					}
					//xóa tất cả chi tiết khuyến mãi liên kết đến chi tiết hóa đơn chính ta muốn xóa
					Query queryDelDetailKML=em.createQuery("delete from InvoiceDetail where invoice_detail_own.id=:id ");
					queryDelDetailKML.setParameter("id", invoiceDetailTrans.getId());
					if(queryDelDetailKML.executeUpdate()<=0){
						res=-1;
						ut.rollback();
						message.setUser_message("Xóa chi tiết hóa đơn khuyến mãi liên kết với chi tiết hóa đơn chính thất bại!");
						message.setInternal_message("error delete InvoiceDetail by id "+invoiceDetailTrans.getId());
						return res;
					}
				}
				//xóa chi tiết hóa đơn ta muốn xóa (Chi tiết hóa đơn chính)
				Query queryDelDetailMainL=em.createQuery("delete from InvoiceDetail where id=:id ");
				queryDelDetailMainL.setParameter("id", invoiceDetailTrans.getId());
				if(queryDelDetailMainL.executeUpdate()<=0){
					res=-1;
					ut.rollback();
					message.setUser_message("Xóa chi tiết hóa đơn thất bại!");
					message.setInternal_message("error delete InvoiceDetail by id "+invoiceDetailTrans.getId());
					return res;
				}
				
			}else{
				//nếu là chi tiết hóa đơn khuyến mãi
				//lấy danh sách chi tiết khuyến mãi liên quan đến chi tiết khuyến mãi cần xóa thông qua Id hóa đơn chính.
				CriteriaQuery<InvoiceDetail> cqDetailKML=cb.createQuery(InvoiceDetail.class);
				Root<InvoiceDetail> rootKML=cqDetailKML.from(InvoiceDetail.class);
				cqDetailKML.select(rootKML).where(cb.equal(rootKML.get("invoice_detail_own").get("id"),invoiceDetailOwnTrans.getId()));
				TypedQuery<InvoiceDetail> queryKML=em.createQuery(cqDetailKML);
				//danh sách chi tiết hóa đơn khuyến mãi liên kết bao gồm cả chi tiết hóa đơn ta muốn xóa
				List<InvoiceDetail> listDetailKML=queryKML.getResultList();
				if(listDetailKML.size()>0){
					for(InvoiceDetail dtkml:listDetailKML){
						//Trả lại số lượng cho lô hàng
						CriteriaQuery<ExportBatch> cqExportBatchKML = cb.createQuery(ExportBatch.class);
						Root<ExportBatch> rootExportBatchKML = cqExportBatchKML.from(ExportBatch.class);
						rootExportBatchKML.fetch("batch",JoinType.INNER);
						cqExportBatchKML.select(rootExportBatchKML).where(cb.equal(rootExportBatchKML.get("invoice_detail").get("id"), dtkml.getId()));
						TypedQuery<ExportBatch> queryExportBatchKML=em.createQuery(cqExportBatchKML);
						List<ExportBatch> listEBTransKML=queryExportBatchKML.getResultList();
						if(listEBTransKML.size()>0){
							for(ExportBatch exkml:listEBTransKML){
								Batch batchTransKML=exkml.getBatch();
								//cập nhật lại số lượng nhâp chô lô hàng
								double quantityImportBatchKML=BigDecimal.valueOf(batchTransKML.getQuantity_import()).add(BigDecimal.valueOf(exkml.getQuantity())).doubleValue();
								batchTransKML.setQuantity_import(quantityImportBatchKML);
								//giảm số lượng xuất của lô hàng
								double quantityExportBatchKML=BigDecimal.valueOf(batchTransKML.getQuantity_export()).subtract(BigDecimal.valueOf(exkml.getQuantity())).doubleValue();
								batchTransKML.setQuantity_export(quantityExportBatchKML);
								batchTransKML.setLast_modifed_by(t.getMember_name());
								batchTransKML.setLast_modifed_date(new Date());
								//cập nhật lô hàng
								if(em.merge(batchTransKML)==null){
									res=-1;
									ut.rollback();
									message.setUser_message("Trả lại số lượng lô hàng cho chi tiết hóa đơn khuyến mãi thất bại thất bại!");
									message.setInternal_message("error merge batch "+batchTransKML.getBatch_code()+" invoice_detail_id "+dtkml.getId());
									return res;
								}
							}
							// xóa tất cả lô hàng xuất theo invoice_detail_id(ExportBatch)
							Query queryDelExportBatch = em.createQuery("delete from ExportBatch where invoice_detail.id=:did ");
							queryDelExportBatch.setParameter("did", dtkml.getId());
							int check = queryDelExportBatch.executeUpdate();
							if(check <=0){
								res=-1;
								ut.rollback();
								message.setUser_message("Xóa lô hàng xuất cho tiết hóa đơn khuyến mãi thất bại!");
								message.setInternal_message("error delete ExportBatch by invocie_detail_id "+dtkml.getId());
								return res;
							}
						}
					}
					//thực hiện xóa chi tiết khuyến mãi liên quan bom gồm cả chi tiết khuyến mãi ta muốn xóa
					Query queryDelDetailKML=em.createQuery("delete from InvoiceDetail where invoice_detail_own.id=:id ");
					queryDelDetailKML.setParameter("id", invoiceDetailOwnTrans.getId());
					if(queryDelDetailKML.executeUpdate()<=0){
						res=-1;
						ut.rollback();
						message.setUser_message("Xóa chi tiết hóa đơn khuyến mãi liên quan thất bại!");
						message.setInternal_message("error delete InvoiceDetail by invocie "+invoiceDetailOwnTrans.getId());
						return res;
					}
				}
				//thực hiện xóa chi tiết hóa đơn chính liên kết với chi tiết hóa đơn khuyến mãi ta muốn xóa
				Query queryDelDetailMainL=em.createQuery("delete from InvoiceDetail where id=:id ");
				queryDelDetailMainL.setParameter("id", invoiceDetailOwnTrans.getId());
				if(queryDelDetailMainL.executeUpdate()<=0){
					res=-1;
					ut.rollback();
					message.setUser_message("Xóa chi tiết hóa đơn chính liên kết với hóa đơn khuyến mãi thất bại!");
					message.setInternal_message("error delete InvoiceDetail by invocie "+invoiceDetailOwnTrans.getId());
					return res;
				}
				
			}
			res=0;
			ut.commit();
		} catch (Exception e) {
			ut.rollback();
			message.setUser_message("Xóa chi tiết hóa đơn thất bại!");
			message.setInternal_message("ProcessLogicInvoiceService.deleteInvoiceDetailMaster:" + e.getMessage());
			logger.error("ProcessLogicInvoiceService.deleteInvoiceDetailMaster:" + e.getMessage(), e);
		} finally {
			if (con != null)
				con.close();
		}
		return res;
	}
	@Override
	public int saveOrUpdateMaster(WrapInvoiceReqInfo t, StringBuilder messages)throws IllegalStateException, SystemException, SQLException {
		int res=-1;
		Connection con=null;
		try{
//			ut.begin();
//			con = datasource.getConnection();
//			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
//			CriteriaBuilder cb=em.getCriteriaBuilder();
//			Invoice pInvoice=t.getInvoice();
//			List<InvoiceDetail> pListDetail=t.getList_invoice_detail();
//			if(pInvoice !=null && pListDetail.size()>0){
//				if(pInvoice.getStatus()==1){
//					if(pInvoice.getId()!=0){
//						//load trans
//						Invoice invoiceTrans=em.find(Invoice.class,pInvoice.getId());
//						if(invoiceTrans.getStatus()==0){
//							//tiến hành lưu hóa đơn và lưu chi tiết hóa đơn và xuất lô hàng
//							pInvoice=em.merge(pInvoice);
//							if(p!=null){
//								for(GoodsReceiptNoteDetail dt: listDetail){
//									dt.setGoods_receipt_note(p);
//									if(dt.getId()==0){
//										em.persist(dt);
//										if(dt.getId()==0){
//											ut.rollback();
//											res=-1;
//											messages.append("Lưu chi tiết phiếu nhập thất bại!");
//											return res;
//										}
//									}else{
//										dt=em.merge(dt);
//										if(dt==null){
//											ut.rollback();
//											res=-1;
//											messages.append("Cập nhật chi tiết phiếu nhập thất bại!");
//											return res;
//										}
//									}
//									//tạo lô hàng.
//									Batch batch = new Batch();
//									batch.setCreated_by(dt.getCreated_by());
//									batch.setCreated_date(new Date());
//									batch.setBatch_code(initbatchCode());
//									batch.setManufacture_date(p.getImport_date());
//									batch.setProduct(dt.getProduct());
//									batch.setQuantity(dt.getQuantity());
//									batch.setUnit_price(dt.getUnit_price());
//									batch.setGoods_receipt_note_detail(dt);
//									em.persist(batch);
//									if (batch.getId() == 0) {
//										res = -1;
//										ut.rollback();
//										messages.append("Khởi tạo lô hàng thất bại!");
//										return res;
//									}
//									dt.setBatch(batch);
//									if(em.merge(dt)==null){
//										res = -1;
//										ut.rollback();
//										messages.append("Cập nhật chi tiết phiếu nhập thất bại!");
//										return res;
//									}
//								}
//							}else{
//								ut.rollback();
//								res=-1;
//								messages.append("Lưu phiếu nhập thất bại!");
//								return res;
//							}
//								
//						}else{
//							//Chỉ được cập nhật một số thông tin quan trọng của phiếu nhập.
//							String sql1 = "update GoodsReceiptNote c set c.note = :n,c.movement_commands=:mc,c.tk_1=:tk1,c.tk_2=:tk2,c.vcnb_invoice_code=:vcnb where c.id = :id ";
//							Query query1 = em.createQuery(sql1);
//							query1.setParameter("n", p.getNote());
//							query1.setParameter("mc", p.getMovement_commands());
//							query1.setParameter("tk1", p.getTk_1());
//							query1.setParameter("tk2", p.getTk_2());
//							query1.setParameter("vcnb", p.getVcnb_invoice_code());
//							query1.setParameter("id", p.getId());
//							int code = query1.executeUpdate();
//							if(code!=0){
//								//cập nhật chi tiết phiếu nhập và cập nhật lô hàng nếu hợp lệ
//								for(GoodsReceiptNoteDetail dt:listDetail){
//									dt.setGoods_receipt_note(goodsTrans);
//									if(dt.getId()!=0){
//										//load trans
//									   GoodsReceiptNoteDetail transDetail=em.find(GoodsReceiptNoteDetail.class,dt.getId());
//									    // lấy lô hàng hiện tại
//										Batch batchTrans = transDetail.getBatch();
//										boolean check = false;
//									   if(transDetail !=null){
//											// kiểm tra có lô hàng này đã xuất chưa.
//											CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//											Root<ExportBatch> root = cq.from(ExportBatch.class);
//											Subquery<Long> sub = cq.subquery(Long.class);
//											Root<GoodsReceiptNoteDetail> root2 = sub.from(GoodsReceiptNoteDetail.class);
//											sub.select(root2.get("batch").get("id"))
//													.where(cb.equal(root2.get("goods_receipt_note"), goodsTrans.getId()));
//											cq.select(cb.count(root.get("id"))).where(root.get("batch").get("id").in(sub));
//											TypedQuery<Long> query = em.createQuery(cq);
//											int chk = query.getSingleResult().intValue();
//											// nếu đã xuất lô hàng
//											if (chk > 0) {
//												// kiểm tra nếu cập nhật sản phẩm khác với sản phẩm ban đầu
//												if (!dt.getProduct().equals(transDetail.getProduct())) {
//													// Lô hàng đã xuất không được thay đổi sản phẩm
//													res = -1;
//													messages.append("Lô hàng đã xuất không được chỉnh sửa sản phẩm!");
//													ut.rollback();
//													return res;
//												} else {
//													// Lấy số lượng chi tiết phiếu nhập - sản lượng lô hàng(trans)
//													double sl = BigDecimal.valueOf(dt.getQuantity())
//															.subtract(BigDecimal.valueOf(transDetail.getQuantity())).doubleValue();
//													// kiểm tra số lượng chi tiết phiếu nhập chỉnh sửa nếu lớn hơn hoặc bằng sản lượng ban đầu(trans) thì cập nhật bình thường
//													if (sl >= 0) {
//														check = true;
//														// cập nhật số lượng lô hàng bằng chi tiết phiếu nhập
//														batchTrans.setQuantity(dt.getQuantity());
//													} else {
//														// Lấy số lượng xuất thực tế của lô hàng
//														CriteriaQuery<Double> cq3 = cb.createQuery(Double.class);
//														Root<ExportBatch> root3 = cq3.from(ExportBatch.class);
//														cq3.select(cb.sum(root3.get("quantity")))
//																.where(cb.equal(root3.get("batch").get("id"), batchTrans.getId()));
//														TypedQuery<Double> query3 = em.createQuery(cq3);
//														double quantityExport = query3.getSingleResult();
//														// kiểm tra số lượng đã xuất cho lô hàng trên nếu nhỏ hơn hoặc bằng số lượng chi tiết phiếu nhập thì cập nhật lại số lượng lô hàng
//														if (quantityExport <= dt.getQuantity()) {
//															check = true;
//															double quantityNew = BigDecimal.valueOf(dt.getQuantity())
//																	.subtract(BigDecimal.valueOf(quantityExport)).doubleValue();
//															batchTrans.setQuantity(quantityNew);
//														} else {
//															// số lượng xuất thực tế lớn hơn số lượng nhập
//															res = -1;
//															messages.append("Lô hàng đã xuất có số lượng lớn hơn số lượng chỉnh sửa!");
//															ut.rollback();
//															return res;
//														}
//													}
//												}
//											} else {
//												// trường chưa xuất lô hàng cập nhật lại số lượng và sản phẩm lô hàng giống chi tiết phiếu nhập
//												check = true;
//												batchTrans.setProduct(dt.getProduct());
//												batchTrans.setQuantity(dt.getQuantity());
//												batchTrans.setUnit_price(dt.getUnit_price());
//											}
//									   }
//									   if(check){
//										   if (em.merge(dt) != null) {
//												batchTrans.setLast_modifed_by(dt.getLast_modifed_by());
//												batchTrans.setLast_modifed_date(new Date());
//												if (em.merge(batchTrans) == null) {
//													ut.rollback();
//													res = -1;
//													messages.append("Cập lô hàng thất bại!");
//													return res;
//												}
//											} else {
//												ut.rollback();
//												res = -1;
//												messages.append("Cập nhật thất bại!");
//												return res;
//											}
//									   }
//									}else{
//										//trường hợp thêm mới chi tiết vào phiếu nhập đã hoàn thành
//										em.persist(dt);
//										if(dt.getId()==0){
//											ut.rollback();
//											res=-1;
//											messages.append("Lưu chi tiết phiếu nhập thất bại!");
//											return  res;
//										}
//										//tạo mới lô hàng
//										Batch batch=new Batch();
//										batch.setProduct(dt.getProduct());
//										batch.setQuantity(dt.getQuantity());
//										batch.setUnit_price(dt.getUnit_price());
//										batch.setBatch_code(initbatchCode());
//										batch.setCreated_by(dt.getCreated_by());
//										batch.setCreated_date(new Date());
//										batch.setGoods_receipt_note_detail(dt);
//										batch.setManufacture_date(goodsTrans.getImport_date());
//										em.persist(batch);
//										if(batch.getId()==0){
//											res=-1;
//											ut.rollback();
//											messages.append("Tạo lô hàng thất bại!");
//											return res;
//										}
//										dt.setBatch(batch);
//										if(em.merge(dt)==null){
//											res=-1;
//											ut.rollback();
//											messages.append("Cập nhật chi tiết phiếu nhập thất bại!");
//											return res;
//										}
//										
//									}
//								}
//							}else{
//								res=-1;
//								messages.append("Cập nhật phiếu nhập thất bại!");
//								ut.rollback();
//								return res;
//							}
//						}
//						//trả về kết quả
//						ut.commit();
//						res=0;
//						CriteriaQuery<GoodsReceiptNote> cq=cb.createQuery(GoodsReceiptNote.class);
//						Root<GoodsReceiptNote> root=cq.from(GoodsReceiptNote.class);
//						root.fetch("customer",JoinType.INNER);
//						root.fetch("ie_categories",JoinType.INNER);
//						root.fetch("warehouse",JoinType.LEFT);
//						cq.select(root).where(cb.equal(root.get("id"), goodsTrans.getId()));
//						TypedQuery<GoodsReceiptNote> query=em.createQuery(cq);
//						t.setGoods_receipt_Note(query.getSingleResult());
//						
//						CriteriaQuery<GoodsReceiptNoteDetail> cq2=cb.createQuery(GoodsReceiptNoteDetail.class);
//						Root<GoodsReceiptNoteDetail> root2= cq2.from(GoodsReceiptNoteDetail.class);
//						root2.fetch("product",JoinType.INNER);
//						root2.fetch("goods_receipt_note",JoinType.INNER);
//						root2.fetch("batch",JoinType.LEFT);
//						cq2.select(root2).where(cb.equal(root2.get("goods_receipt_note"),goodsTrans.getId()));
//						TypedQuery<GoodsReceiptNoteDetail> query2=em.createQuery(cq2);
//						t.setList_goods_receipt_note_detail(query2.getResultList());
//					}else{
//						p.setReceipt_code(initReceiptNoteCode());
//						initCodeVoucher(p);
//						em.persist(p);
//						if(p.getId() !=0){
//							res=-1;
//							messages.append("Lưu phiếu nhập thất bại!");
//							ut.rollback();
//							return res;
//						}
//						//tạo lô hàng
//						for(GoodsReceiptNoteDetail dt:listDetail){
//							if(dt.getId()==0){
//								em.persist(dt);
//								if(dt.getId()==0){
//									res=-1;
//									messages.append("Lưu chi tiết phiếu nhập thất bại!");
//									ut.rollback();
//									return res;
//								}
//								//tạo mới lô hàng
//								Batch batch=new Batch();
//								batch.setProduct(dt.getProduct());
//								batch.setQuantity(dt.getQuantity());
//								batch.setUnit_price(dt.getUnit_price());
//								batch.setBatch_code(initbatchCode());
//								batch.setCreated_by(dt.getCreated_by());
//								batch.setCreated_date(new Date());
//								batch.setGoods_receipt_note_detail(dt);
//								batch.setManufacture_date(p.getImport_date());
//								em.persist(batch);
//								if(batch.getId()==0){
//									res=-1;
//									ut.rollback();
//									messages.append("Tạo lô hàng thất bại!");
//									return res;
//								}
//								dt.setBatch(batch);
//								if(em.merge(dt)==null){
//									res=-1;
//									ut.rollback();
//									messages.append("Cập nhật chi tiết phiếu nhập thấi bại!");
//									return res;
//								}
//								
//							}else{
//								res=-1;
//								messages.append("Thông tin không hợp lệ!");
//								ut.rollback();
//								return res;
//							}
//						}
//						ut.commit();
//						res=0;
//						//trả về kết quả
//						CriteriaQuery<GoodsReceiptNote> cq=cb.createQuery(GoodsReceiptNote.class);
//						Root<GoodsReceiptNote> root=cq.from(GoodsReceiptNote.class);
//						root.fetch("customer",JoinType.INNER);
//						root.fetch("ie_categories",JoinType.INNER);
//						root.fetch("warehouse",JoinType.LEFT);
//						cq.select(root).where(cb.equal(root.get("id"), p.getId()));
//						TypedQuery<GoodsReceiptNote> query=em.createQuery(cq);
//						t.setGoods_receipt_Note(query.getSingleResult());
//						
//						CriteriaQuery<GoodsReceiptNoteDetail> cq2=cb.createQuery(GoodsReceiptNoteDetail.class);
//						Root<GoodsReceiptNoteDetail> root2= cq2.from(GoodsReceiptNoteDetail.class);
//						root2.fetch("product",JoinType.INNER);
//						root2.fetch("goods_receipt_note",JoinType.INNER);
//						root2.fetch("batch",JoinType.LEFT);
//						cq2.select(root2).where(cb.equal(root2.get("goods_receipt_note"),p.getId()));
//						TypedQuery<GoodsReceiptNoteDetail> query2=em.createQuery(cq2);
//						t.setList_goods_receipt_note_detail(query2.getResultList());
//						res=0;
//					
//					}
//				}else{
//					res=-1;
//					messages.append("not support!");
//					ut.commit();
//					return res;
//				}
//			}else{
//				res=-1;
//				messages.append("Thông tin không đầy đủ!");
//				return res;
//			}
		}catch(Exception e){
			res=-1;
			ut.rollback();
			messages.append("ProcessLogicGoodsReceiptNoteService.saveOrUpsateMaster:"+e.getMessage());
			logger.error("ProcessLogicGoodsReceiptNoteService.saveOrUpsateMaster:"+e.getMessage(),e);
		}finally {
			if(con!=null)
				con.close();
		}
		return res;
	}
	private String initInvoiceCode(){
		try {
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<Invoice> root= cq.from(Invoice.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)root.get("invoice_code"),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/10000000;
			if(p<1){
				return String.format("%08d", max+1);
			}
			return (max+1)+"";
		} catch (Exception e) {
			logger.error("ProcessLogicInvoiceService.initReceiptNoteCode:" + e.getMessage(), e);
		}
		return null;
	}
	private int initCodeVoucher(Invoice t){
		int res=-1;
		
		try{
			Date date=t.getInvoice_date();
			int year=ToolTimeCustomer.getYearM(date);
			int month=ToolTimeCustomer.getMonthM(date);
			int day=ToolTimeCustomer.getDayM(date);
					
			String voucher=day+""+month+""+year+"/";
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<String> cq=cb.createQuery(String.class);
			Root<Invoice> root= cq.from(Invoice.class);
			cq.select(root.get("invoice_code"))
			.where(cb.equal(root.get("invoice_date"), ToolTimeCustomer.getFirstDateOfDay(date)))
			.orderBy(cb.desc(root.get("invoice_date")));
			TypedQuery<String> query=em.createQuery(cq);
			List<String> list=query.getResultList();
			
			if(list.size() >0){
				String temp=list.get(0);
				if(temp !=null){
					int last=temp.lastIndexOf("/");
					String sub=temp.substring(last+1);
					voucher= voucher+String.format("%02d",Integer.parseInt(sub)+1);
					t.setVoucher_code(voucher);
				}
			}else{
				voucher= voucher+String.format("%02d",1);
				t.setVoucher_code(voucher);
			}
			res=0;
		}catch(Exception e){
			logger.error("ProcessLogicInvoiceService.initCode:"+e.getMessage(),e);
		}
		return res;
	}

	@Override
	public int createInvoiceByOrderLix(WrapOrderLixReqInfo t,StringBuilder messages) throws IllegalStateException, SystemException, SQLException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			OrderLix pOrderLix=t.getOrder_lix();
			List<WrapOrderDetailLixReqInfo>  pListWrapOrderDetail=t.getList_wrap_order_detail();
			List<PromotionOrderDetail> pListPromotionOrderDetail=t.getList_promotion_order_detail();
			if(pOrderLix !=null && pListWrapOrderDetail !=null && pListWrapOrderDetail.size()>0){
				//lấy trans đơn hàng
				OrderLix transOrder=em.find(OrderLix.class, pOrderLix.getId());
				if(transOrder !=null){
					List<InvoiceDetail> listInvoiceDetail=new ArrayList<>();
					//cập nhật đơn hàng trạng thái và thực xuất
					//cập nhật chi tiết thực xuất đơn hàng.
					Map<OrderDetail, InvoiceDetail> map=new LinkedHashMap<>();
					for(WrapOrderDetailLixReqInfo w : pListWrapOrderDetail){
						OrderDetail d=w.getOrder_detail();
						//lấy trans orderdetail
						OrderDetail transOrderDetail=em.find(OrderDetail.class, d.getId());
						if(transOrderDetail==null){
							res=-1;
							ut.rollback();
							messages.append("Chi tiết hóa đơn không tồn tại!");
							return res;
						}
						//cập nhật thực xuất
						double slthucxuat=BigDecimal.valueOf(transOrderDetail.getRealbox_quantity()).add(BigDecimal.valueOf(w.getQuantity())).doubleValue();
						transOrderDetail.setRealbox_quantity(slthucxuat);
						//update trans
						if(em.merge(transOrderDetail)==null){
							res=-1;
							ut.rollback();
							messages.append("Cập nhật chi tiết đơn hàng thất bại!");
							return res;
						}
						//chuẩn bị danh sách chi tiết hóa đơn 
						InvoiceDetail detail=new InvoiceDetail();
						detail.setEx_order(true);
						detail.setProduct(d.getProduct());
						//số số lượng theo đơn vị sản phẩm
						detail.setQuantity(w.getQuantity()*d.getProduct().getSpecification());
						detail.setUnit_price(transOrderDetail.getUnit_price());
						detail.setNote(transOrderDetail.getNote());
						listInvoiceDetail.add(detail);
						if(!map.containsKey(d)){
							map.put(d, detail);						
						}
					}
					//kiểm tra số lượng để set trạng thái
					Query query=em.createQuery("select count(id) from OrderDetail where order_lix.id= :idm and box_quantity <> realbox_quantity ");
					query.setParameter("idm", transOrder.getId());
					int ck=Integer.parseInt(Objects.toString(query.getSingleResult()));
					if(ck>0){
						//cập nhật trạng thái đơn hàng là đang giao hàng
						transOrder.setStatus(1);
						if(em.merge(transOrder)==null){
							res=-1;
							ut.rollback();
							messages.append("Cập nhật trạng thái đơn hàng thất bại!");
							return res;
						}
						
					}else if(ck==0){
						//cập nhật trạng thái đã hoàn thành
						transOrder.setStatus(2);
						if(em.merge(transOrder)==null){
							res=-1;
							ut.rollback();
							messages.append("Cập nhật trạng thái đơn hàng thất bại!");
							return res;
						}
					}else{
						res=-1;
						ut.rollback();
						messages.append("Dữ liệu sai!");
						return res;
						
					}
					//tạo đơn hàng thứ nhất
					Invoice invoice=new Invoice();
					invoice.setOrder_lix(transOrder);
					invoice.setOrder_code(transOrder.getOrder_code());
					invoice.setOrder_voucher(transOrder.getVoucher_code());
					invoice.setCreated_by_id(t.getMember_id());
					invoice.setCreated_by(t.getMember_name());
					invoice.setCreated_date(new Date());
					invoice.setCustomer(pOrderLix.getCustomer());
					invoice.setInvoice_date(new Date());
					invoice.setPayment_method(pOrderLix.getPayment_method());
					invoice.setCar(pOrderLix.getCar());
					invoice.setDelivery_pricing(pOrderLix.getDelivery_pricing());//đơn giá vận chuyển chứa nơi đến
					invoice.setFreight_contract(pOrderLix.getFreight_contract());
					invoice.setIe_categories(pOrderLix.getIe_categories());
					invoice.setWarehouse(pOrderLix.getWarehouse());
					invoice.setTax_value(pOrderLix.getTax_value());
					invoice.setVoucher_code(pOrderLix.getVoucher_code());
					invoice.setPricing_program(pOrderLix.getPricing_program());
					invoice.setPromotion_program(pOrderLix.getPromotion_program());
					invoice.setPo_no(pOrderLix.getPo_no());
					invoice.setInvoice_code(initInvoiceCode());
					initCodeVoucher(invoice);
					em.persist(invoice);
					if(invoice.getId()==0){
						res=-1;
						ut.rollback();
						messages.append("Tạo hóa đơn thất bại!");
						return res;
					}
					//tạo chi tiết  cho đơn hàng thứ nhất
					for(InvoiceDetail v:listInvoiceDetail){
						v.setInvoice(invoice);
						v.setCreated_by(t.getMember_name());
						v.setCreated_by_id(t.getMember_id());
						v.setCreated_date(new Date());
						em.persist(v);
						//xuất lô hàng 
						if(v.getId()==0){
							res=-1;
							messages.append("Tạo chi tiết hóa đơn thất bại!");
							ut.rollback();
							return res;
						}
					}
					if(pListPromotionOrderDetail != null && pListPromotionOrderDetail.size()>0){
						//tạo đơn hàng khuyến mãi(thứ 2)
						Invoice invoice2=new Invoice();
						invoice2.setIpromotion(true);
						invoice2.setOrder_lix(transOrder);
						invoice2.setOrder_code(transOrder.getOrder_code());
						invoice2.setOrder_voucher(transOrder.getVoucher_code());
						invoice2.setCreated_by_id(t.getMember_id());
						invoice2.setCreated_by(t.getMember_name());
						invoice2.setCreated_date(new Date());
						invoice2.setCustomer(pOrderLix.getCustomer());
						invoice2.setInvoice_date(new Date());
						invoice2.setPayment_method(pOrderLix.getPayment_method());
						invoice2.setCar(pOrderLix.getCar());
						invoice2.setDelivery_pricing(pOrderLix.getDelivery_pricing());//đơn giá vận chuyển chứa nơi đến
						invoice2.setFreight_contract(pOrderLix.getFreight_contract());
						invoice2.setIe_categories(pOrderLix.getIe_categories());
						invoice2.setWarehouse(pOrderLix.getWarehouse());
						invoice2.setTax_value(pOrderLix.getTax_value());
						invoice2.setVoucher_code(pOrderLix.getVoucher_code());
						invoice2.setPricing_program(pOrderLix.getPricing_program());
						invoice2.setPromotion_program(pOrderLix.getPromotion_program());
						invoice2.setPo_no(pOrderLix.getPo_no());
						invoice2.setInvoice_code(initInvoiceCode());
						//hóa đơn sở hữu khuyến mãi này
						invoice2.setInvoice_own(invoice);
						initCodeVoucher(invoice2);
						em.persist(invoice2);
						if(invoice2.getId()==0){
							res=-1;
							ut.rollback();
							messages.append("Tạo hóa đơn khuyến mãi thất bại!");
							return res;
						}
						//lưu chi tiết hóa đơn khuyến mãi
						for(PromotionOrderDetail p: pListPromotionOrderDetail){
							InvoiceDetail invoiceMain=null;
							if(map.containsKey(p.getOrder_detail())){
								invoiceMain=map.get(p.getOrder_detail());
							}
							InvoiceDetail detail=new InvoiceDetail();
							detail.setInvoice_detail_own(invoiceMain);
							detail.setEx_order(true);
							detail.setProductdh_code(p.getOrder_detail().getProduct().getProduct_code());
							detail.setInvoice(invoice2);
							detail.setCreated_by(t.getMember_name());
							detail.setCreated_date(new Date());
							detail.setCreated_by_id(t.getMember_id());
							detail.setProduct(p.getProduct());
							detail.setQuantity(p.getQuantity());
							detail.setUnit_price(p.getUnit_price());
							detail.setNote(p.getNote());
//							detail.setOrder_detail(p.getOrder_detail());// không cần set vì đây là chi tiết của hóa đơn trích từ hóa đơn khuyến mãi
							em.persist(detail);
							if(detail.getId()==0){
								res=-1;
								messages.append("Tạo chi tiết hóa đơn thất bại!");
								ut.rollback();
								return res;
							}
						}
					}
				}else{
					res=-1;
					ut.commit();
					messages.append("Đơn hàng không tồn tại!");
				}
			}
			res=0;
			ut.commit();
		}catch (Exception e) {
			res=-1;
			ut.rollback();
			messages.append("ProcessLogicInvoicePosService.createInvoicePosByOrder:"+e.getMessage());
			logger.error("ProcessLogicInvoicePosService.createInvoicePosByOrder:"+e.getMessage(),e);
		}finally {
			if(con!=null){
				con.close();
			}
		}
		return res;
	}
	@Override
	public int createInvoiceByIEInvoice(WrapIEInvocieReqInfo t, Message message) throws SQLException, IllegalStateException, SecurityException, SystemException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			long iEInvoiceId =t.getIe_invoice_id();
			//kiểm tra hóa đơn đã chuyển qua hóa đơn chính chưa. nếu đã chuyển hóa đơn chính thì không cho nó insert vào.
			CriteriaQuery<IEInvoice> cq=cb.createQuery(IEInvoice.class);
			Root<IEInvoice> root=cq.from(IEInvoice.class);
//			root.fetch("invoice",JoinType.LEFT);
//			root.fetch("payment_method",JoinType.LEFT);
//			root.fetch("customer",JoinType.INNER);
//			root.fetch("car",JoinType.LEFT);
//			root.fetch("ie_categories",JoinType.INNER);
//			root.fetch("contract",JoinType.LEFT);
//			root.fetch("stevedore",JoinType.LEFT);
//			root.fetch("form_up_goods",JoinType.LEFT);
//			root.fetch("warehouse",JoinType.LEFT);
//			root.fetch("harbor_category",JoinType.LEFT);
//			root.fetch("stocker",JoinType.LEFT);
			cq.select(root).where(cb.equal(root.get("id"),iEInvoiceId));
			TypedQuery<IEInvoice> query=em.createQuery(cq);
			IEInvoice iEInvoiceTrans=query.getSingleResult();
			if(iEInvoiceTrans==null){
				res=-1;
				ut.rollback();
				message.setUser_message("phiếu xuất hàng xuất khẩu không tồn tại!");
				return res;
			}
			if(iEInvoiceTrans.getInvoice()!=null){
				res=-1;
				ut.rollback();
				message.setUser_message("Phiếu xuất hàng xuất khẩu đã chuyển qua hóa đơn không được chuyển!");
				return res;
			}
			//lấy danh sách chi tiết phiếu nhâp
			CriteriaQuery<IEInvoiceDetail> cqDetail=cb.createQuery(IEInvoiceDetail.class);
			Root<IEInvoiceDetail> rootIEInvocieDetail=cqDetail.from(IEInvoiceDetail.class);
			cqDetail.select(rootIEInvocieDetail).where(cb.equal(rootIEInvocieDetail.get("ie_invoice").get("id"),iEInvoiceId));
			TypedQuery<IEInvoiceDetail> queryDetail=em.createQuery(cqDetail);
			List<IEInvoiceDetail> listIEInvoiceDetailTrans=queryDetail.getResultList();
			if(listIEInvoiceDetailTrans.size()==0){
				res=-1;
				ut.rollback();
				message.setUser_message("Danh sách chi tiết xuất hàng xuất khẩu không có!");
				return res;
			}
			Invoice invoice=new Invoice();
			invoice.setInvoice_date(iEInvoiceTrans.getInvoice_date());
			invoice.setOrder_lix(null);
			invoice.setOrder_code(null);
			invoice.setOrder_voucher(null);
			invoice.setCreated_by_id(t.getCreated_by_id());
			invoice.setCreated_by(t.getCreated_by());
			invoice.setCreated_date(new Date());
			invoice.setCustomer(iEInvoiceTrans.getCustomer());
			invoice.setInvoice_date(new Date());
			invoice.setPayment_method(iEInvoiceTrans.getPayment_method());
			invoice.setCar(iEInvoiceTrans.getCar());
			//lấy nơi vận chuyển
			CriteriaQuery<DeliveryPricing> cqDeliveryPricing=cb.createQuery(DeliveryPricing.class);
			Root<DeliveryPricing> rootDeliveryPricing=cqDeliveryPricing.from(DeliveryPricing.class);
			cqDeliveryPricing.select(rootDeliveryPricing).where(cb.and(cb.equal(rootDeliveryPricing.get("customer").get("id"),iEInvoiceTrans.getCustomer().getId()),cb.isFalse(rootDeliveryPricing.get("disable"))));
			TypedQuery<DeliveryPricing> queryDeliveryPricing=em.createQuery(cqDeliveryPricing);
			List<DeliveryPricing> listDPTrans=queryDeliveryPricing.getResultList();
			if(listDPTrans.size() >0){
				invoice.setDelivery_pricing(listDPTrans.get(0));//đơn giá vận chuyển chứa nơi đến
			}
			invoice.setFreight_contract(null);
			invoice.setIe_categories(iEInvoiceTrans.getIe_categories());
			invoice.setWarehouse(iEInvoiceTrans.getWarehouse());
			invoice.setTax_value(iEInvoiceTrans.getTax_value());
			invoice.setVoucher_code(iEInvoiceTrans.getVoucher_code());
			invoice.setPricing_program(null);
			invoice.setPromotion_program(null);
			invoice.setPo_no(null);
			invoice.setInvoice_code(initInvoiceCode());
			invoice.setStevedore(iEInvoiceTrans.getStevedore());
			invoice.setStocker(iEInvoiceTrans.getStocker());
			invoice.setForm_up_goods(iEInvoiceTrans.getForm_up_goods());
			invoice.setHarbor_category(iEInvoiceTrans.getHarbor_category());
			invoice.setIe_reason(iEInvoiceTrans.getIe_reason());
			invoice.setContract(iEInvoiceTrans.getContract());
			invoice.setDelivery_date(iEInvoiceTrans.getInvoice_date());
			invoice.setDepartment_name(iEInvoiceTrans.getDepartment_name());
			invoice.setNote(iEInvoiceTrans.getNote());
			invoice.setPayment(iEInvoiceTrans.isPaid());
			invoice.setTimeout(iEInvoiceTrans.isTime_out());
			initCodeVoucher(invoice);
			em.persist(invoice);
			if(invoice.getId()==0){
				res=-1;
				ut.rollback();
				message.setUser_message("Không tạo được hóa đơn!");
				return res;
			}
			for(IEInvoiceDetail dt:listIEInvoiceDetailTrans){
				InvoiceDetail detail=new InvoiceDetail();
				detail.setCreated_date(new Date());
				detail.setCreated_by(t.getCreated_by());
				detail.setForeign_unit_price(dt.getForeign_unit_price());
				detail.setTotal_foreign_amount(dt.getTotal_foreign_amount());
				detail.setProduct(dt.getProduct());
				detail.setQuantity(dt.getQuantity());
				detail.setUnit_price(dt.getUnit_price());
				detail.setTotal_amount(dt.getTotal_amount());
				detail.setInvoice(invoice);
//				detail.setIE_invoice_detail không cần thiết
				em.persist(detail);
				if(detail.getId()==0){
					res=-1;
					ut.rollback();
					message.setUser_message("Tạo chi tiết hóa đơn thất bại!");
					return res;
				}
			}
			//cập nhật lại phiếu xuất xuất khẩu là đã copy qua hóa đơn chính
			iEInvoiceTrans.setInvoice(invoice);
			if(em.merge(iEInvoiceTrans)==null){
				res=-1;
				ut.rollback();
				message.setUser_message("Cập nhật phiếu xuất đã sao chép sang hóa đơn chính thất bại!");
				return res;
			}
			res=0;
			ut.commit();
		}catch (Exception e) {
			res=-1;
			ut.rollback();
			message.setUser_message("Lưu thất bại!");
			message.setInternal_message("ProcessLogicIEInvoiceService.createInvoiceByIEInvoice:"+e.getMessage());
			logger.error("ProcessLogicIEInvoiceService.createInvoiceByIEInvoice:"+e.getMessage(),e);
		}finally{
			if (con != null)
				con.close();
		}
		return res;

	}
	@Override
	public int deleteExportBatch(WrapDelExportBatchReqInfo t, Message message) throws SQLException, IllegalStateException, SecurityException, SystemException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ExportBatch> cqExportBatch=cb.createQuery(ExportBatch.class);
			Root<ExportBatch> rootExportBatch=cqExportBatch.from(ExportBatch.class);
			rootExportBatch.fetch("batch",JoinType.INNER);
			cqExportBatch.select(rootExportBatch).where(cb.equal(rootExportBatch.get("id"), t.getId()));
			TypedQuery<ExportBatch> queryEB=em.createQuery(cqExportBatch);
			List<ExportBatch> list=queryEB.getResultList();
			if(list.size()==0){
				res=-1;
				ut.rollback();
				message.setUser_message("Lô hàng xuất không tồn tại!");
				message.setInternal_message("error export_batch id:"+t.getId());
				return res;
			}
			ExportBatch exportBatchTrans=list.get(0);
			Batch batchTrans=exportBatchTrans.getBatch();
			//trả số lượng cho lô hàng
			//tăng số lượng nhập cho lô hàng
			double quantityImport=BigDecimal.valueOf(batchTrans.getQuantity_import()).add(BigDecimal.valueOf(exportBatchTrans.getQuantity())).doubleValue();
			//giảm số lượng xuất của lô hàng đó đi
			double quantityExport=BigDecimal.valueOf(batchTrans.getQuantity_export()).subtract(BigDecimal.valueOf(exportBatchTrans.getQuantity())).doubleValue();
			batchTrans.setLast_modifed_by(t.getMember_name());
			batchTrans.setLast_modifed_date(new Date());
			batchTrans.setQuantity_import(quantityImport);
			batchTrans.setQuantity_export(quantityExport);
			if(em.merge(batchTrans)==null){
				res=-1;
				ut.rollback();
				message.setUser_message("Cập nhật lại số lượng lô hàng thất bại!");
				message.setInternal_message("error batch_code:"+batchTrans.getBatch_code()+" id:"+batchTrans.getId());
				return res;
			}
			//xóa lô hàng xuất (ExportBatch) sử dụng JQPL
			Query queryDelEB=em.createQuery("delete from ExportBatch where id= :id ");
			queryDelEB.setParameter("id", exportBatchTrans.getId());
			if(queryDelEB.executeUpdate()<=0){
				res=-1;
				ut.rollback();
				message.setUser_message("Xóa lô hàng xuất thất bại!");
				message.setInternal_message("error delele ExportBatch id:"+exportBatchTrans.getId());
				return res;
			}
			//
			res=0;
			ut.commit();
		}catch (Exception e) {
			res=-1;
			ut.rollback();
			message.setUser_message("Xóa lô hàng xuất thất bại!");
			message.setInternal_message("ProcessLogicIEInvoiceService.deleteExportBatch:"+e.getMessage());
			logger.error("ProcessLogicIEInvoiceService.deleteExportBatch:"+e.getMessage(),e);
		}finally{
			if (con != null)
				con.close();
		}
		return res;
	}
	@Override
	public int saveListWrapExportData(WrapDataInvoiceDetail t, Message message) throws SQLException, IllegalStateException, SecurityException, SystemException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			InvoiceDetail detail=t.getInvoice_detail();
			List<WrapExportDataReqInfo> listData=t.getList_wrap_export_data();
			if(detail !=null && detail.getId() !=0 && listData !=null && listData.size()>0){
				//load trans chi tiết hóa đơn
				InvoiceDetail invoiceDetailTrans=em.find(InvoiceDetail.class, detail.getId());
				if(invoiceDetailTrans==null){
					res=-1;
					ut.rollback();
					message.setUser_message("Chi tiết hóa đơn không tồn tại!");
					message.setInternal_message("error invoice detail not found!");
					return res;
				}
				double sumExport=0;
				//kiểm tra tổng số lượng xuất có lơn hơn số lượng yêu cầu không.
				for(WrapExportDataReqInfo q:listData){
					sumExport=BigDecimal.valueOf(sumExport).add(BigDecimal.valueOf(q.getQuantity_export())).doubleValue();
				}
				//nếu tổng số lượng xuất lớn hơn số lượng yêu cầu thì rollback
				if(sumExport >invoiceDetailTrans.getQuantity()){
					res=-1;
					ut.rollback();
					message.setUser_message("Tổng số lượng thực xuất lớn hơn số lượng yêu cầu!");
					message.setInternal_message("");
					return res;
				}
				//xóa tất cả lô hàng xuất trước đó và trả lại số lượng lô hàng.
				//load danh sách lô hàng xuất trong trans.
				CriteriaQuery<ExportBatch> cqEB=cb.createQuery(ExportBatch.class);
				Root<ExportBatch> rootEB= cqEB.from(ExportBatch.class);
				rootEB.fetch("batch",JoinType.INNER);
				cqEB.select(rootEB).where(cb.equal(rootEB.get("invoice_detail").get("id"),detail.getId()));
				TypedQuery<ExportBatch> queryEB=em.createQuery(cqEB);
				List<ExportBatch> listExportBatchTrans=queryEB.getResultList();
				if(listExportBatchTrans.size()>0){
					for(ExportBatch ex:listExportBatchTrans){
						double quantityEB=ex.getQuantity();
						Batch bTrans=ex.getBatch();
						//trả lại số lượng cho lô hàng bằng cách giảm số lượng xuất của lô hàng lại
						double quantityExportBP=BigDecimal.valueOf(bTrans.getQuantity_export()).subtract(BigDecimal.valueOf(quantityEB)).doubleValue();
						bTrans.setQuantity_export(quantityExportBP);
						if(em.merge(bTrans)==null){
							res=-1;
							ut.rollback();
							message.setUser_message("Thực hiện thất bại!");
							message.setInternal_message("error batch!!");
							return res;
						}
					}
					Query queryDelExportBatch= em.createQuery("delete from ExportBatch as p where p.invoice_detail.id=:idd");
					queryDelExportBatch.setParameter("idd",detail.getId());
					if(queryDelExportBatch.executeUpdate()<=0){
						res=-1;
						ut.rollback();
						message.setUser_message("Xóa lô hàng xuất thất bại!");
						message.setInternal_message("error del ExportBatch");
						return res;
					}
				}
				//tiến hành kiễm tra dữ liệu hợp lệ và tạo lại dữ liệu
				int i=0;
				for(WrapExportDataReqInfo ex:listData){
					i++;
					ExportBatch exportBatchOld=ex.getExport_batch();
					double quantityExport=ex.getQuantity_export();
					//set id bằng 0 cho trường hợp đã xuất trước đó(persist)
					exportBatchOld.setId(0);
					//số lượng lô hàng
				}
			}else{
				res=-1;
				ut.rollback();
				message.setUser_message("Thông tin không đúng!");
				message.setInternal_message("error request data!");
				return res;
			}
			res=0;
		}catch (Exception e) {
			res=-1;
			ut.rollback();
			message.setUser_message("Lưu thất bại!");
			message.setInternal_message("ProcessLogicInvoiceService.saveListWrapExportData:"+e.getMessage());
			logger.error("ProcessLogicInvoiceService.saveListWrapExportData:"+e.getMessage(),e);
		}finally{
			if (con != null)
				con.close();
		}
		return res;
	}
}
