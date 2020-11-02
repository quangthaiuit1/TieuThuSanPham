package lixco.com.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;


import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.BatchPos;
import lixco.com.entity.GoodsReceiptNotePos;
import lixco.com.entity.GoodsReceiptNotePosDetail;
import lixco.com.entity.Pos;
import lixco.com.entity.Product;
import lixco.com.entity.ProductPos;
import lixco.com.interfaces.IProcessLogicGoodsReceiptNotePosService;
import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.WrapGoodsReceiptNotePosDetailReqInfo;
import lixco.com.reqInfo.WrapGoodsReceiptNotePosReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.BEAN)
public class ProcessLogicGoodsReceiptNotePosService implements IProcessLogicGoodsReceiptNotePosService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource(lookup = "java:/consumption")
	DataSource datasource;
	@Resource
	UserTransaction ut;
	@Override
	public int deleteGoodsReceiptNoteMaster(long id,Message messages) throws IllegalStateException, SystemException, SQLException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			//load trans
			GoodsReceiptNotePos transReceipt=em.find(GoodsReceiptNotePos.class,id);
			if(transReceipt==null){
				res=-1;
				messages.setUser_message("Phiếu nhập không tồn tại!");
				messages.setInternal_message("error request data!");
				ut.commit();
				return res;
			}
			CriteriaQuery<GoodsReceiptNotePosDetail> cqDetail=cb.createQuery(GoodsReceiptNotePosDetail.class);
			Root<GoodsReceiptNotePosDetail>  rootDetail=cqDetail.from(GoodsReceiptNotePosDetail.class);
			rootDetail.fetch("batch_pos",JoinType.INNER);
			cqDetail.select(rootDetail).where(cb.equal(rootDetail.get("goods_receipt_note_pos").get("id"), id));
			TypedQuery<GoodsReceiptNotePosDetail> queryDetail=em.createQuery(cqDetail);
			List<GoodsReceiptNotePosDetail>  listDetail=queryDetail.getResultList();
			for(GoodsReceiptNotePosDetail dt: listDetail){
				/*Lấy danh sách vị trí sản phẩm trong kho*/
				CriteriaQuery<ProductPos> cqProductPos=cb.createQuery(ProductPos.class);
				Root<ProductPos> rootProductPos=cqProductPos.from(ProductPos.class);
				cqProductPos.select(rootProductPos).where(cb.equal(rootProductPos.get("goods_receipt_note_pos_detail").get("id"),dt.getId()));
				TypedQuery<ProductPos> queryProductPos =em.createQuery(cqProductPos);
				List<ProductPos> listProductPos=queryProductPos.getResultList();
				for(ProductPos t:listProductPos){
					// xóa productpos
					if(t.getQuantity_export()!=0){
						res=-1;
						messages.setUser_message("Lô hàng có vị trí "+t.getPos().getBarcode()+" đã xuất không được xóa phiếu nhập!");
						messages.setInternal_message("error ProductPos Id["+t.getId()+"]!");
						ut.rollback();
						return res;
					}
				}
				//nếu chưa xuất lô hàng vị trí thì xóa bình thường sử dụng JPQL
				Query queryDelProductPos=em.createQuery("delete from ProductPos p where p.goods_receipt_note_pos_detail.id = :idd ");
				queryDelProductPos.setParameter("idd", dt.getId());
				if(queryDelProductPos.executeUpdate()<0){
					res=-1;
					messages.setUser_message("Xóa lô hàng vị trí thất bại");
					messages.setInternal_message("error delete BatchPos");
					ut.rollback();
					return res;
				}
				//gỡ số lượng lô hàng của chi tiết phiếu nhập ra.
				BatchPos transBatch=dt.getBatch_pos();
				double quantityImport=BigDecimal.valueOf(transBatch.getQuantity_import()).subtract(BigDecimal.valueOf(dt.getQuantity())).doubleValue();
				transBatch.setQuantity_import(quantityImport);
				if(em.merge(transBatch)==null){
					res=-1;
					messages.setUser_message("Xóa lô hàng vị trí thất bại");
					messages.setInternal_message("error BatchPos Id["+transBatch.getId()+"]");
					ut.rollback();
					return res;
				}
				//xóa chi tiết phiếu nhập sử dụng JPQL
				Query queryDelDetail=em.createQuery("delete from GoodsReceiptNotePosDetail where id=:idd ");
				queryDelDetail.setParameter("idd", dt.getId());
				if(queryDelDetail.executeUpdate()<0){
					res=-1;
					messages.setUser_message("Xóa chi tiết phiếu nhập thất bại!");
					messages.setInternal_message("error GoodsReceiptNotePosDetail Id["+dt.getId()+"]");
					ut.rollback();
					return res;
				}
				
				
			}
			//xóa phiếu nhập sử dụng JPQL
			Query queryDelReceipt=em.createQuery("delete from GoodsReceiptNotePos where id= :id ");
			queryDelReceipt.setParameter("id", id);
			if(queryDelReceipt.executeUpdate()<0){
				res=-1;
				messages.setUser_message("Xóa phiếu nhập thất bại!");
				messages.setInternal_message("error GoodsReceiptNotePos Id["+id+"]");
				ut.rollback();
				return res;
			}
			res=0;
			ut.commit();
		}catch(Exception e){
			res=-1;
			messages.setUser_message("Không xóa được phiếu nhập!");
			messages.setInternal_message("ProcessLogicGoodsReceiptNotePosService.deleteGoodsReceiptNoteMaster:"+e.getMessage());
			ut.rollback();
			logger.error("ProcessLogicGoodsReceiptNotePosService.deleteGoodsReceiptNoteMaster:"+e.getMessage(),e);
		}finally {
			if (con != null)
				con.close();
		}
		return res;
	}
	@Override
	public int deleteGoodsReciptNoteDetailMaster(long id, Message messages) throws IllegalStateException, SystemException, SQLException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			//load trans trong database
			CriteriaQuery<GoodsReceiptNotePosDetail> cqDetail=cb.createQuery(GoodsReceiptNotePosDetail.class);
			Root<GoodsReceiptNotePosDetail>  rootDetail=cqDetail.from(GoodsReceiptNotePosDetail.class);
			rootDetail.fetch("batch_pos",JoinType.INNER);
			cqDetail.select(rootDetail).where(cb.equal(rootDetail.get("id"), id));
			TypedQuery<GoodsReceiptNotePosDetail> queryDetail=em.createQuery(cqDetail);
			GoodsReceiptNotePosDetail transDetail=queryDetail.getSingleResult();
			if(transDetail==null){
				res=-1;
				messages.setUser_message("Chi tiết phiếu nhập không tồn tại!");
				messages.setInternal_message("error request data!");
				ut.commit();
				return res;
			}
			/*Lấy danh sách vị trí đặt lô hàng trong kho*/
			CriteriaQuery<ProductPos> cqProductPos=cb.createQuery(ProductPos.class);
			Root<ProductPos> rootProductPos=cqProductPos.from(ProductPos.class);
			rootProductPos.fetch("batch_pos",JoinType.INNER);
			cqProductPos.select(rootProductPos).where(cb.equal(rootProductPos.get("goods_receipt_note_pos_detail").get("id"),id));
			TypedQuery<ProductPos> queryProductPos =em.createQuery(cqProductPos);
			List<ProductPos> listProductPos=queryProductPos.getResultList();
			int stt=0;
			for(ProductPos t:listProductPos){
				stt++;
				// xóa productpos
				if(t.getQuantity_export()!=0){
					res=-1;
					messages.setUser_message("Vị trí đặt lô hàng đã xuất không được xóa chi tiết phiếu nhập!");
					messages.setInternal_message("error ProductPos "+stt+"-"+t.getBatch_pos().getBatch_code()+"-"+t.getPos().getBarcode());
					ut.rollback();
					return res;
				}
			}
			//nếu chưa xuất lô hàng vị trí thì xóa bình thường sử dụng JPQL
			Query queryDelProductPos=em.createQuery("delete from ProductPos where goods_receipt_note_pos_detail.id = :idd ");
			queryDelProductPos.setParameter("idd",id);
			if(queryDelProductPos.executeUpdate()<0){
				res=-1;
				messages.setUser_message("Xóa vị trí đặt lô hàng thất bại !");
				messages.setInternal_message("error delete");
				ut.rollback();
				return res;
			}
			//gỡ số lượng lô hàng của chi tiết phiếu nhập ra.
			BatchPos transBatch=transDetail.getBatch_pos();
			double quantityImport=BigDecimal.valueOf(transBatch.getQuantity_import()).subtract(BigDecimal.valueOf(transDetail.getQuantity())).doubleValue();
			transBatch.setQuantity_import(quantityImport);
			if(em.merge(transBatch)==null){
				res=-1;
				messages.setUser_message("Xóa lô hàng thất bại!");
				messages.setInternal_message("error BatchPos "+transBatch.getBatch_code());
				ut.rollback();
				return res;
			}
			//xóa chi tiết phiếu nhập sử dụng JPQL
			Query queryDelDetail=em.createQuery("delete from GoodsReceiptNotePosDetail where id=:idd ");
			queryDelDetail.setParameter("idd",id);
			if(queryDelDetail.executeUpdate()<0){
				res=-1;
				messages.setUser_message("Xóa chi tiết phiếu nhập thất bại!");
				messages.setInternal_message("error GoodsReceiptNotePosDetail Id["+id+"]");
				ut.rollback();
				return res;
			}
			res=0;
			ut.commit();
		}catch(Exception e){
			res=-1;
			messages.setUser_message("Không xóa được chi tiết phiếu nhập!");
			messages.setInternal_message("ProcessLogicGoodsReceiptNotePosService.deleteGoodsReceiptNoteMaster");
			ut.rollback();
			logger.error("ProcessLogicGoodsReceiptNotePosService.deleteGoodsReciptNoteDetailMaster:"+e.getMessage(),e);
		}finally {
			if (con != null)
				con.close();
		}
		return res;
	}
	@Override
	public int saveOrUpdateGoodsReceiptNotePosService(WrapGoodsReceiptNotePosReqInfo t, Message messages,int status) throws IllegalStateException, SystemException, SQLException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			GoodsReceiptNotePos p = t.getGoods_receipt_note_pos();
			List<WrapGoodsReceiptNotePosDetailReqInfo> listDetail = t.getList_goods_receipt_note_pos_detail();
			if (p != null && listDetail.size() > 0) {
				if (p.getId() == 0) {
					//tạo phiếu nhập
					//init mã phiếu nhập
					p.setReceipt_code(initReceiptNoteCode());
					//init mã chứng từ
					initCodeVoucher(p);
					//trạng thái phiếu nhập
					p.setStatus(status);
					em.persist(p);
					if (p.getId() == 0) {
						res = -1;
						messages.setUser_message("Lưu phiếu nhập thất bại");
						messages.setInternal_message("error persist GoodsReceiptNotePos");
						ut.rollback();
						return res;
					}
					int stt=1;
					for (WrapGoodsReceiptNotePosDetailReqInfo w : listDetail) {
						GoodsReceiptNotePosDetail dt = w.getGoods_receipt_note_pos_detail();
						List<ProductPos> listProductPos = w.getList_product_pos();
						if(dt != null){
							String batchCode = dt.getBatch_code();
							Product product = dt.getProduct();
							if (dt.getId() == 0 && batchCode != null && !"".equals(batchCode)&& product != null && product.getId() != 0) {
								dt.setGoods_receipt_note_pos(p);
								// lưu chi tiết phiếu nhập
								em.persist(dt);
								if (dt.getId() == 0) {
									res = -1;
									messages.setUser_message("Lưu chi tiết phiếu nhập thất bại!");
									messages.setInternal_message("error row detail:" + product.getProduct_code());
									ut.rollback();
									return res;
								}
								// kiểm tra xem đã tồn tại lô hàng đó chưa.
								CriteriaQuery<BatchPos> cqbatch = cb.createQuery(BatchPos.class);
								Root<BatchPos> rootBatch = cqbatch.from(BatchPos.class);
								cqbatch.select(rootBatch).where(cb.and(cb.equal(rootBatch.get("batch_code"), batchCode),cb.equal(rootBatch.get("product").get("id"), product.getId())));
								TypedQuery<BatchPos> queryBatch = em.createQuery(cqbatch);
								List<BatchPos> listBatchPos = queryBatch.getResultList();
								BatchPos batchPos = null;
								if (listBatchPos.size() == 0) {
									batchPos = new BatchPos();
									batchPos.setBatch_code(batchCode);
									batchPos.setCreated_date(new Date());
									batchPos.setCreated_by(t.getUsername());
									// ngày sản xuất
									batchPos.setManufacture_date(p.getImport_date());
									batchPos.setProduct(dt.getProduct());
									// số lượng thùng nhập vào
									batchPos.setQuantity_import(dt.getQuantity());
									em.persist(batchPos);
									if (batchPos.getId() == 0) {
										res = -1;
										messages.setUser_message("Tạo lô hàng sản phẩm: " + product.getProduct_code()+ " có mã " + batchCode + " thất bại!");
										messages.setInternal_message("error row detail "+stt+" "+ product.getProduct_code());
										ut.rollback();
										return res;
									}
								} else {
									// cập nhật số lượng lại batchpos
									batchPos = listBatchPos.get(0);
									double quantityBatch = BigDecimal.valueOf(batchPos.getQuantity_import()).add(BigDecimal.valueOf(dt.getQuantity())).doubleValue();
									batchPos.setQuantity_import(quantityBatch);
									batchPos.setLast_modifed_date(new Date());
									batchPos.setLast_modifed_by(t.getUsername());
									batchPos = em.merge(batchPos);
									if (batchPos == null) {
										res = -1;
										messages.setUser_message("Cập nhật số lượng lô hàng thất bại!");
										messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode);
										ut.rollback();
										return res;
									}
								}
								// gán lô hàng cho phiếu nhập
								dt.setBatch_pos(batchPos);
								dt = em.merge(dt);
								if (dt == null) {
									res = -1;
									messages.setUser_message("Cập nhật chi tiết phiếu nhập thất bại!");
									messages.setInternal_message("error row detail "+stt+" "+product.getProduct_code()+"-"+batchCode);
									ut.rollback();
									return res;
								}
								if (listProductPos != null) {
									double quantitySumPP1=0;
									for(ProductPos pp:listProductPos){
										quantitySumPP1=BigDecimal.valueOf(quantitySumPP1).add(BigDecimal.valueOf(pp.getQuantity_import())).doubleValue();
									}
									if(quantitySumPP1 >dt.getQuantity()){
										res = -1;
										messages.setUser_message("Tổng số lượng lô hàng đặt tại vị trí lớn hơn số lượng chi tiết phiếu nhập!");
										messages.setInternal_message("error detail row "+stt+"-"+ product.getProduct_code()+"-"+batchCode+"");
										ut.rollback();
										return res;
									}
									for (ProductPos ps : listProductPos) {
										if (ps != null && ps.getId() == 0) {
											// kiểm tra vị trí kho còn trống để thêm vào không
											Pos posTrans = em.find(Pos.class, ps.getPos().getId());
											if (posTrans == null) {
												res = -1;
												messages.setUser_message("Vị trí kho không tồn tại!");
												messages.setInternal_message("error row detail "+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+ps.getPos().getBarcode());
												ut.rollback();
												return res;
											}
											//số lượng thùng đã đặt tại vị trí đó
											CriteriaQuery<Object> cqProductPos=cb.createQuery(Object.class);
											Root<ProductPos> root= cqProductPos.from(ProductPos.class);
											Join<ProductPos, BatchPos> batchPos_=root.join("batch_pos",JoinType.INNER);
											Join<BatchPos, Product> product_=batchPos_.join("product",JoinType.INNER);
											cqProductPos.multiselect(cb.sum(cb.quot(cb.diff(root.get("quantity_import"),root.get("quantity_export")),product_.get("box_quantity")))).
													where(cb.equal(root.get("pos").get("id"), posTrans.getId()));
											TypedQuery<Object> queryProductPos=em.createQuery(cqProductPos);
											Object result=  queryProductPos.getSingleResult();
											// số lượng pallet đang chiếm chổ tại vị trí đó (Pos)
											int quantityPalletPutInPos=(int) Math.ceil(Double.parseDouble(Objects.toString(result, "0")));
											// kiểm tra số lượng chổ trống vị trí kho còn không và đủ đáp ứng số lượng nhập vào hay không nếu không đáp ứng dc đưa ra warning để người dùng biết mà cập nhật lại.
											if (quantityPalletPutInPos > posTrans.getQuantity_pallet()) {
												// trường hợp không đáp ứng với res
												String mess=Objects.toString(messages.getUser_message(),"");
												messages.setUser_message(mess+posTrans.getBarcode()+" ["+quantityPalletPutInPos+"/"+posTrans.getQuantity_pallet()+"] //n");
												messages.setInternal_message("");
												messages.setCode(1001);
											}
											//Cập nhật vị trí đặt lô hàng (ProductPos)
											ps.setBatch_pos(batchPos);
											ps.setCreated_date(new Date());
											ps.setCreated_by(t.getUsername());
											ps.setGoods_receipt_note_pos_detail(dt);
											ps.setStatus(status);
											em.persist(ps);
											if (ps.getId() == 0) {
												res = -1;
												messages.setUser_message("Lưu vị trí đặt lô hàng trong kho thất bại!");
												messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+ps.getPos().getBarcode());
												ut.rollback();
												return res;
											}
										}
									}
								}
							}else{
								res=-1;
								messages.setUser_message("Thông tin không đầy đủ!");
								messages.setInternal_message("error data request!");
								ut.rollback();
								return res;
							}
					   }
					}
				}else{
					//update phiếu nhập
					GoodsReceiptNotePos transReceipt=em.find(GoodsReceiptNotePos.class,p.getId());
					if(transReceipt==null){
						res=-1;
						messages.setUser_message("Phiếu nhập không tồn tại!");
						messages.setInternal_message("error request data!");
						ut.rollback();
						return res;
					}
					if(transReceipt.getStatus()==1 && status==0){
						res=-1;
						messages.setUser_message("Phiếu nhập đã hoàn thành không thể cập nhật trạng thái lưu tạm!");
						messages.setInternal_message("error request data!");
						ut.rollback();
						return res;
					}
					// cập nhật phiếu nhập
					p.setStatus(status);
					p=em.merge(p);
					if(p ==null){
						res=-1;
						messages.setUser_message("Cập nhật phiếu nhập thất bại!");
						messages.setInternal_message("");
						ut.rollback();
						return res;
					}
					int stt=1;
					for (WrapGoodsReceiptNotePosDetailReqInfo w : listDetail) {
						GoodsReceiptNotePosDetail dt = w.getGoods_receipt_note_pos_detail();
						List<ProductPos> listProductPos = w.getList_product_pos();
						if(dt!=null){
							String batchCode = dt.getBatch_code();
							Product product = dt.getProduct();
							dt.setGoods_receipt_note_pos(p);
							//khi thêm chi tiết phiếu nhập vào một phiếu đã lưu rồi.
							if (batchCode != null && !"".equals(batchCode)&& product != null && product.getId() != 0) {
								// tổng số lượng nhập tại ví trí đặt lô hàng
								double quantitySumPP1=0;
								if(listProductPos !=null){
									//tính tổng số lượng nhập đặt tại vị trí lô hàng
									for(ProductPos pp:listProductPos){
										quantitySumPP1=BigDecimal.valueOf(quantitySumPP1).add(BigDecimal.valueOf(pp.getQuantity_import())).doubleValue();
									}
									//kiểm tra dữ liệu gửi lên có số lượng đặt lô hàng tại các vị trí lớn hơn số lượng của chi tiết phiếu nhập 
									if(quantitySumPP1 >dt.getQuantity()){
										res = -1;
										messages.setUser_message("Tổng số lượng lô hàng đặt tại các vị trí lớn hơn số lượng chi tiết phiếu nhập!");
										messages.setInternal_message("error detail row "+stt+"-"+product.getProduct_code()+"-"+batchCode);
										ut.rollback();
										return res;
									}
								}
								//nếu khi người dùng thêm mới chi tiết phiếu nhập
								if(dt.getId()==0){
									// lưu chi tiết phiếu nhập
									em.persist(dt);
									if (dt.getId() == 0) {
										res = -1;
										messages.setUser_message("Lưu chi tiết phiếu nhập thất bại!");
										messages.setInternal_message("error detail row "+stt+"-"+ product.getProduct_code());
										ut.rollback();
										return res;
									}
									// kiểm tra xem đã tồn tại lô hàng đó chưa.
									CriteriaQuery<BatchPos> cqbatch = cb.createQuery(BatchPos.class);
									Root<BatchPos> rootBatch = cqbatch.from(BatchPos.class);
									cqbatch.select(rootBatch).where(cb.and(cb.equal(rootBatch.get("batch_code"), batchCode),cb.equal(rootBatch.get("product").get("id"), product.getId())));
									TypedQuery<BatchPos> queryBatch = em.createQuery(cqbatch);
									List<BatchPos> listBatchPos = queryBatch.getResultList();
									BatchPos batchPos = null;
									//nếu chưa tồn tại lô hàng
									if (listBatchPos.size() == 0) {
										batchPos = new BatchPos();
										batchPos.setBatch_code(batchCode);
										batchPos.setCreated_date(new Date());
										batchPos.setCreated_by(t.getUsername());
										// ngày sản xuất
										batchPos.setManufacture_date(p.getImport_date());
										batchPos.setProduct(dt.getProduct());
										// số lượng thùng nhập vào
										batchPos.setQuantity_import(dt.getQuantity());
										em.persist(batchPos);
										if (batchPos.getId() == 0) {
											res = -1;
											messages.setUser_message("Tạo lô hàng sản phẩm: " + product.getProduct_code()+ " có mã " + batchCode + " thất bại!");
											messages.setInternal_message("error row detail "+stt+" "+ product.getProduct_code());
											ut.rollback();
											return res;
										}
									} else {
										// cập nhật số lượng lại batchpos
										batchPos = listBatchPos.get(0);
										double quantityBatch = BigDecimal.valueOf(batchPos.getQuantity_import()).add(BigDecimal.valueOf(dt.getQuantity())).doubleValue();
										batchPos.setQuantity_import(quantityBatch);
										batchPos.setLast_modifed_date(new Date());
										batchPos.setLast_modifed_by(t.getUsername());
										batchPos = em.merge(batchPos);
										if (batchPos == null) {
											res = -1;
											messages.setUser_message("Cập nhật số lượng lô hàng thất bại!");
											messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode);
											ut.rollback();
											return res;
										}
									}
									// gán lô hàng cho phiếu nhập
									dt.setBatch_pos(batchPos);
									dt = em.merge(dt);
									if (dt == null) {
										res = -1;
										messages.setUser_message("Cập nhật chi tiết phiếu nhập thất bại!");
										messages.setInternal_message("error row detail "+stt+" "+product.getProduct_code()+"-"+batchCode);
										ut.rollback();
										return res;
									}
									if (listProductPos != null) {
										double quantitySumPP=0;
										for(ProductPos pp:listProductPos){
											quantitySumPP=BigDecimal.valueOf(quantitySumPP).add(BigDecimal.valueOf(pp.getQuantity_import())).doubleValue();
										}
										if(quantitySumPP >dt.getQuantity()){
											res = -1;
											messages.setUser_message("Tổng số lượng lô hàng đặt tại vị trí lớn hơn số lượng chi tiết phiếu nhập!");
											messages.setInternal_message("error detail row "+stt+"-"+ product.getProduct_code()+"-"+batchCode+"");
											ut.rollback();
											return res;
										}
										for (ProductPos ps : listProductPos) {
											if (ps != null && ps.getId() == 0) {
												// kiểm tra vị trí kho còn trống để thêm vào không
												Pos posTrans = em.find(Pos.class, ps.getPos().getId());
												if (posTrans == null) {
													res = -1;
													messages.setUser_message("Vị trí kho không tồn tại!");
													messages.setInternal_message("error row detail "+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+ps.getPos().getBarcode());
													ut.rollback();
													return res;
												}
												//số lượng thùng đã đặt tại vị trí đó
												CriteriaQuery<Object> cqProductPos=cb.createQuery(Object.class);
												Root<ProductPos> root= cqProductPos.from(ProductPos.class);
												Join<ProductPos, BatchPos> batchPos_=root.join("batch_pos",JoinType.INNER);
												Join<BatchPos, Product> product_=batchPos_.join("product",JoinType.INNER);
												cqProductPos.multiselect(cb.sum(cb.quot(cb.diff(root.get("quantity_import"),root.get("quantity_export")),product_.get("box_quantity")))).
														where(cb.equal(root.get("pos").get("id"), posTrans.getId()));
												TypedQuery<Object> queryProductPos=em.createQuery(cqProductPos);
												Object result=  queryProductPos.getSingleResult();
												// số lượng pallet đang chiếm chổ tại vị trí đó (Pos)
												int quantityPalletPutInPos=(int) Math.ceil(Double.parseDouble(Objects.toString(result, "0")));
												// kiểm tra số lượng chổ trống vị trí kho còn không và đủ đáp ứng số lượng nhập vào hay không nếu không đáp ứng dc đưa ra warning để người dùng biết mà cập nhật lại.
												if (quantityPalletPutInPos > posTrans.getQuantity_pallet()) {
													// trường hợp không đáp ứng với res
													String mess=Objects.toString(messages.getUser_message(),"");
													messages.setUser_message(mess+posTrans.getBarcode()+" ["+quantityPalletPutInPos+"/"+posTrans.getQuantity_pallet()+"] //n");
													messages.setInternal_message("");
													messages.setCode(1001);
												}
												//Cập nhật vị trí đặt lô hàng (ProductPos)
												ps.setBatch_pos(batchPos);
												ps.setCreated_date(new Date());
												ps.setCreated_by(t.getUsername());
												ps.setGoods_receipt_note_pos_detail(dt);
												ps.setStatus(status);
												em.persist(ps);
												if (ps.getId() == 0) {
													res = -1;
													messages.setUser_message("Lưu vị trí đặt lô hàng trong kho thất bại!");
													messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+ps.getPos().getBarcode());
													ut.rollback();
													return res;
												}
											}
										}
									}
								}else{
									//trường hợp khi cập nhật lại chi tiết phiếu nhập
									//load trans chi tiết phiếu nhập trong database
									CriteriaQuery<GoodsReceiptNotePosDetail> cqReceiptDetail=cb.createQuery(GoodsReceiptNotePosDetail.class);
									Root<GoodsReceiptNotePosDetail> rootReceiptDetail=cqReceiptDetail.from(GoodsReceiptNotePosDetail.class);
									rootReceiptDetail.fetch("product",JoinType.INNER);
									rootReceiptDetail.fetch("batch_pos",JoinType.LEFT);
									cqReceiptDetail.select(rootReceiptDetail).where(cb.equal(rootReceiptDetail.get("id"), dt.getId()));
									TypedQuery<GoodsReceiptNotePosDetail> query=em.createQuery(cqReceiptDetail);
									GoodsReceiptNotePosDetail transDetailOld=query.getSingleResult();
									//kiểm tra vị trí đặt lô hàng đã xuất chưa use JPQL(old)
									Query queryCheckEx=em.createQuery("select sum(p.quantity_export) from ProductPos as p where p.goods_receipt_note_pos_detail.id= :idd ");
									queryCheckEx.setParameter("idd",transDetailOld.getId());
									//tổng số lượng xuất của lô hàng của chi tiết phiếu nhập đó
									double sumEx=Double.parseDouble(Objects.toString(queryCheckEx.getSingleResult(),"0"));
									//nếu lô hàng đã xuất nếu thay đổi sản phẩm và lô hàng thì rollback
									//ngược lại lô hàng đặt tại các vị trí chưa xuất hoặc đã xuất nhưng không thay đổi sản phẩm hoặc mã lô hàng
									if(sumEx>0){
										//nếu thay đổi sản phẩm
										if(!transDetailOld.getProduct().equals(dt.getProduct())){
											res=-1;
											messages.setUser_message("Chi tiết phiếu nhập có lô hàng đã xuất không được thay đổi sản phẩm!");
											messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode);
											ut.rollback();
											return res;
										}
										//nếu thay đổi lô hàng
										if(!batchCode.equals(transDetailOld.getBatch_pos().getBatch_code())){
											res=-1;
											messages.setUser_message("Chi tiết phiếu nhập có lô hàng đã xuất không được thay đổi mã lô hàng!");
											messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode);
											ut.rollback();
											return res;
										}
										if(sumEx > dt.getQuantity()){
											//nếu số tổng số lượng xuất tại các vị trí đặt lô hàng(ProductPos) lớn hơn số lượng chi tiết phiếu nhập thì rollback
											res=-1;
											messages.setUser_message("Tống số lượng xuất tại vị trí đặt lô hàng lớn hơn số lượng chi tiết phiếu nhập!");
											messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode);
											ut.rollback();
											return res;
										}
									}
									//những trường hợp lô hàng đặt tại các vị trí chưa xuất hoặc đã xuất nhưng không thay đổi sản phẩm và mã lô hàng thì chạy những dòng bên dưới
									//gở số lượng lô hàng(số lượng nhập) cũ và gỡ số lượng lô hàng(số lượng xuất) cũ.
									//BatchPos trans old
									BatchPos batchPosTransOld=transDetailOld.getBatch_pos();
									double quantityImportOld=BigDecimal.valueOf(batchPosTransOld.getQuantity_import()).subtract(BigDecimal.valueOf(transDetailOld.getQuantity())).doubleValue();
									batchPosTransOld.setQuantity_import(quantityImportOld);
									//gỡ số lượng xuất của lô hàng của chi tiết phiếu nhập đó ra
									double quantityExportOld=BigDecimal.valueOf(batchPosTransOld.getQuantity_export()).subtract(BigDecimal.valueOf(sumEx)).doubleValue();
									batchPosTransOld.setQuantity_export(quantityExportOld);
									if(em.merge(batchPosTransOld)==null){
										messages.setUser_message("Gỡ số lượng lô hàng thất bại!");
										messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode);
										ut.rollback();
										return res;
									}
									//tạo lô hàng mới đối với trường hợp chưa xuất hoặc cập nhật lại lô hàng nếu đã tồn tại lô hàng đó.
									//được phép tạo lô hàng mới khi người dùng thay đổi mã lô hàng và lô hàng đó chưa có xuất thì mới tạo đã kiểm tra ở trên
									// kiểm tra xem đã tồn tại lô hàng đó chưa.
									CriteriaQuery<BatchPos> cqbatch = cb.createQuery(BatchPos.class);
									Root<BatchPos> rootBatch = cqbatch.from(BatchPos.class);
									cqbatch.select(rootBatch).where(cb.and(cb.equal(rootBatch.get("batch_code"), batchCode),cb.equal(rootBatch.get("product").get("id"), product.getId())));
									TypedQuery<BatchPos> queryBatch = em.createQuery(cqbatch);
									List<BatchPos>  listBatchPosNew= queryBatch.getResultList();
									BatchPos batchPosNew = null;
									//trường hợp nếu thay đổi mã lô hàng mới và lô hàng trong trans chưa xuất.
									if (listBatchPosNew.size() == 0) {
										//chưa có lô hàng đó tạo lô hàng
										batchPosNew = new BatchPos();
										batchPosNew.setBatch_code(batchCode);
										batchPosNew.setCreated_date(new Date());
										batchPosNew.setCreated_by(t.getUsername());
										// ngày sản xuất
										batchPosNew.setManufacture_date(p.getImport_date());
										batchPosNew.setProduct(dt.getProduct());
										// số lượng nhập
										batchPosNew.setQuantity_import(dt.getQuantity());
										//cập nhật lại số lượng xuất không cần thêm dòng này vì số lượng bằng 0 số lượng trong trans old chưa xuất
//										batchPosNew.setQuantity_export(sumEx);
										em.persist(batchPosNew);
										if (batchPosNew.getId() == 0) {
											res = -1;
											messages.setUser_message("Tạo lô hàng thất bại!");
											messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode);
											ut.rollback();
											return res;
										}
									} else {
										//cập nhật số lượng lại batchpos
										batchPosNew = listBatchPosNew.get(0);
										double quantityImportNew = BigDecimal.valueOf(batchPosNew.getQuantity_import()).add(BigDecimal.valueOf(dt.getQuantity())).doubleValue();
										batchPosNew.setQuantity_import(quantityImportNew);
										//cập nhật lại số lượng xuất nếu số lượng xuất bằng 0 lô hàng chưa xuất nếu lớn hơn 0 vậy là lô hàng đã xuất khi cập nhật không thay đổi mã lô hàng và sản phẩm.
										//số lượng xuất này đã được kiểm tra số lượng phải nhỏ hơn số lượng nhập
										double quantityExportNew = BigDecimal.valueOf(batchPosNew.getQuantity_export()).add(BigDecimal.valueOf(sumEx)).doubleValue();
										batchPosNew.setQuantity_export(quantityExportNew);
										batchPosNew.setLast_modifed_date(new Date());
										batchPosNew.setLast_modifed_by(t.getUsername());
										batchPosNew = em.merge(batchPosNew);
										if (batchPosNew == null) {
											res = -1;
											messages.setUser_message("Cập nhật số lượng lô hàng thất bại!");
											messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode);
											ut.rollback();
											return res;
										}
									}
									//cập nhật lại chi tiết phiếu nhập 
									dt.setBatch_pos(batchPosNew);
									dt=em.merge(dt);
									if(dt==null){
										res = -1;
										messages.setUser_message("Cập nhật chi tiết phiếu nhập thất bại!");
										messages.setInternal_message("error detail row "+stt+"-"+product.getProduct_code()+"-"+batchCode);
										ut.rollback();
										return res;
									}
									if(listProductPos!=null){
										for(ProductPos ps:listProductPos){
											if(ps.getId()==0){
												// khi thêm mới vị trí thì bình thường
												//load trans pos
												Pos posTransNew=em.find(Pos.class, ps.getPos().getId());
												if(posTransNew ==null){
													res = -1;
													messages.setUser_message("Vị trí kho " + ps.getPos().getBarcode() + " không tồn tại!");
													messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+ps.getPos().getBarcode());
													ut.rollback();
													return res;
												}
												
												//kiểm tra không gian lưu trữ tại vị trí đặt lô hàng có đáp ứng được không nếu không thì đưa ra cảnh báo cho người dùng.
												CriteriaQuery<Object> cqProductPos=cb.createQuery(Object.class);
												Root<ProductPos> root= cqProductPos.from(ProductPos.class);
												Join<ProductPos, BatchPos> batchPos_=root.join("batch_pos",JoinType.INNER);
												Join<BatchPos, Product> product_=batchPos_.join("product",JoinType.INNER);
												cqProductPos.multiselect(cb.sum(cb.quot(cb.diff(root.get("quantity_import"),root.get("quantity_export")),product_.get("box_quantity")))).
														where(cb.equal(root.get("pos").get("id"), posTransNew.getId()));
												TypedQuery<Object> queryProductPos=em.createQuery(cqProductPos);
												Object result=  queryProductPos.getSingleResult();
												// số lượng pallet đang chiếm chổ tại vị trí đó (Pos)
												int quantityPalletPutInPos=(int) Math.ceil(Double.parseDouble(Objects.toString(result, "0")));
												// kiểm tra số lượng chổ trống vị trí kho còn không và đủ đáp ứng số lượng nhập vào hay không nếu không đáp ứng dc đưa ra warning để người dùng biết mà cập nhật lại.
												if (quantityPalletPutInPos > posTransNew.getQuantity_pallet()) {
													// trường hợp không đáp ứng với res
													String mess=Objects.toString(messages.getUser_message(),"");
													messages.setUser_message(mess+posTransNew.getBarcode()+" ["+quantityPalletPutInPos+"/"+posTransNew.getQuantity_pallet()+"] //n");
													if(messages.getInternal_message() ==null || "".equals(messages.getInternal_message())){
													   messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+ps.getPos().getBarcode());
													   messages.setCode(1001);
													}
												}
												//không cần check số lượng vị đã kiểm tra ở trên
												//tạo vị trí đặt lô hàng productpos
												ps.setBatch_pos(batchPosNew);
												ps.setCreated_date(new Date());
												ps.setCreated_by(t.getUsername());
												ps.setGoods_receipt_note_pos_detail(dt);
												ps.setStatus(status);
												em.persist(ps);
												if (ps.getId() == 0) {
													res = -1;
													messages.setUser_message("Lưu vị trí đặt lô hàng trong kho thất bại!");
													messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+ps.getPos().getBarcode());
													ut.rollback();
													return res;
												}
											}else{
												//load trans ProductPos by id
												CriteriaQuery<ProductPos> cqProductPos=cb.createQuery(ProductPos.class);
												Root<ProductPos> rootProductPos=cqProductPos.from(ProductPos.class);
												rootProductPos.fetch("pos",JoinType.INNER);
												rootProductPos.fetch("batch_pos",JoinType.INNER);
												cqProductPos.select(rootProductPos).where(cb.equal(rootProductPos.get("id"), ps.getId()));
												TypedQuery<ProductPos> queryProductPos=em.createQuery(cqProductPos);
												ProductPos productPosTransOld=queryProductPos.getSingleResult();
												double quantityExportProductPosTransOld=productPosTransOld.getQuantity_export();
												//kiểm tra nếu vị trí đặt lô hàng đã xuất chưa nếu đã xuất rồi thì số lượng xuất phải nhỏ hơn số lượng nhập tại vị trí đó.
												if(quantityExportProductPosTransOld != 0 && ps.getQuantity_import() < quantityExportProductPosTransOld){
													res=-1;
													messages.setUser_message("Vị trí đặt lô hàng có số lượng xuất lớn hơn số lượng nhập vào!");
													messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+ps.getPos().getBarcode());
													ut.rollback();
													return res;
												}
												//kiểm tra có thay đổi vị trí đặt lô hàng và vị trí đặt lô hàng đã xuất rồi
												if(!productPosTransOld.getPos().equals(ps.getPos()) && quantityExportProductPosTransOld!=0){
													//lô hàng đã xuất không được thay đổi vị trí
													res=-1;
													messages.setUser_message("Vị trí đặt lô hàng đã xuất không được thay đổi vị trí!");
													messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+ps.getPos().getBarcode());
													ut.rollback();
													return res;
												}
												//Pos trans old
												Pos posTransOld=productPosTransOld.getPos();
												//load trans pos mới vị trí mình muốn cập nhật hoặc không thay đổi( TH thay đổi vị trí nhưng chưa xuất)
												Pos posTransNew = em.find(Pos.class, ps.getPos().getId());
												if (posTransNew == null) {
													res = -1;
													messages.setUser_message("Vị trí kho " + ps.getPos().getBarcode() + " không tồn tại!");
													messages.setInternal_message("error row detail "+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+ps.getPos().getBarcode());
													ut.rollback();
													return res;
												}
												//kiểm tra không gian lưu trữ tại vị trí đặt lô hàng có đáp ứng được không nếu không thì đưa ra cảnh báo cho người dùng.
												CriteriaQuery<Object> cqProductPosCk=cb.createQuery(Object.class);
												Root<ProductPos> rootProductPosCk= cqProductPosCk.from(ProductPos.class);
												Join<ProductPos, BatchPos> batchPosCk_=rootProductPosCk.join("batch_pos",JoinType.INNER);
												Join<BatchPos, Product> productCk_=batchPosCk_.join("product",JoinType.INNER);
												cqProductPosCk.multiselect(cb.sum(cb.quot(cb.diff(rootProductPosCk.get("quantity_import"),rootProductPosCk.get("quantity_export")),productCk_.get("box_quantity")))).
														where(cb.equal(rootProductPosCk.get("pos").get("id"), posTransNew.getId()));
												TypedQuery<Object> queryProductPosCk=em.createQuery(cqProductPosCk);
												Object resultCk=  queryProductPosCk.getSingleResult();
												// số lượng pallet đang chiếm chổ tại vị trí đó (Pos)
												int quantityPalletPutInPos=(int) Math.ceil(Double.parseDouble(Objects.toString(resultCk, "0")));
												// kiểm tra số lượng chổ trống vị trí kho còn không và đủ đáp ứng số lượng nhập vào hay không nếu không đáp ứng dc đưa ra warning để người dùng biết mà cập nhật lại.
												if (quantityPalletPutInPos > posTransNew.getQuantity_pallet()) {
													// trường hợp không đáp ứng với res
													String mess=Objects.toString(messages.getUser_message(),"");
													messages.setUser_message(mess+posTransNew.getBarcode()+" ["+quantityPalletPutInPos+"/"+posTransNew.getQuantity_pallet()+"] //n");
													if(messages.getInternal_message() ==null || "".equals(messages.getInternal_message())){
													   messages.setInternal_message("error detail row "+stt+"-"+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+ps.getPos().getBarcode());
													   messages.setCode(1001);
													}
												}
								
											
												//cập nhật lại vị trí đặt lô hàng
												ps.setBatch_pos(batchPosNew);
												ps.setQuantity_export(quantityExportProductPosTransOld);
												ps.setGoods_receipt_note_pos_detail(dt);
												ps.setLast_modifed_by(t.getUsername());
												ps.setLast_modifed_date(new Date());
												ps.setStatus(status);
												ps=em.merge(ps);
												if(ps==null){
													res = -1;
													messages.setUser_message("Cập nhật vị trí đặt lô hàng thất bại!");
													messages.setInternal_message("error row detail "+dt.getProduct().getProduct_code()+"-"+batchCode+"-"+posTransNew.getBarcode());
													ut.rollback();
													return res;
												}
											}
											
										}
									}
								}
							}else{
						    	res=-1;
								messages.setUser_message("Thông tin không đầy đủ!");
								messages.setInternal_message("error data request!");
								ut.rollback();
								return res;
					        } 
					   }
					}
				}
			}else{
				res = -1;
				messages.setUser_message("Thông tin không đầy đủ hoặc không hợp lệ!");
				messages.setInternal_message("Error data request!");
				ut.rollback();
				return res;
			}
			res=0;
			ut.commit();
		}catch(Exception e){
			res=-1;
			messages.setUser_message("Lưu thất bại!");
			messages.setInternal_message("ProcessLogicGoodsReceiptNotePosService.saveOrUpdateGoodsReceiptNotePosService:"+e.getMessage());
			ut.rollback();
			logger.error("ProcessLogicGoodsReceiptNotePosService.saveOrUpdateGoodsReceiptNotePosService:"+e.getMessage(),e);
		}finally {
			if (con != null)
				con.close();
		}
		return res;
	}
	private String initReceiptNoteCode(){
		try {
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<GoodsReceiptNotePos> root= cq.from(GoodsReceiptNotePos.class);
//			cq.multiselect(cb.coalesce(cb.max(root.get("id")),0));
			cq.multiselect(cb.coalesce(cb.max((Expression<Integer>)cb.quot((Expression)root.get("receipt_code"),1)),0));
			TypedQuery<Integer> query=em.createQuery(cq);
			int max=query.getSingleResult();
			double p=(double)max/10000000;
			if(p<1){
				return String.format("%07d", max+1);
			}
			return (max+1)+"";
		} catch (Exception e) {
			logger.error("ProcessLogicGoodsReceiptNotePosService.initReceiptNoteCode:" + e.getMessage(), e);
		}
		return null;
	}
	private int initCodeVoucher(GoodsReceiptNotePos t){
		int res=-1;
		try{
			Date date=t.getImport_date();
			int year=ToolTimeCustomer.getYearM(date);
			int month=ToolTimeCustomer.getMonthM(date);
			int day=ToolTimeCustomer.getDayM(date);
					
			String voucher=day+""+month+""+year+"/";
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<String> cq=cb.createQuery(String.class);
			Root<GoodsReceiptNotePos> root= cq.from(GoodsReceiptNotePos.class);
			cq.select(root.get("voucher_code"))
			.where(cb.equal(root.get("import_date"), ToolTimeCustomer.getFirstDateOfDay(date)))
			.orderBy(cb.desc(root.get("import_date")));
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
			logger.error("ProcessLogicGoodsReceiptNotePosService.initCodeVoucher:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int deleteProductPos(long id, Message messages) throws IllegalStateException, SystemException, SQLException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<ProductPos> cqProductPos=cb.createQuery(ProductPos.class);
			Root<ProductPos> rootProductPos=cqProductPos.from(ProductPos.class);
			rootProductPos.fetch("pos",JoinType.INNER);
			rootProductPos.fetch("batch_pos",JoinType.INNER);
			cqProductPos.select(rootProductPos).where(cb.equal(rootProductPos.get("id"), id));
			TypedQuery<ProductPos> queryProductPos=em.createQuery(cqProductPos);
			ProductPos productPosTrans=queryProductPos.getSingleResult();
			if(productPosTrans==null){
				res=-1;
				messages.setUser_message("Vị trí đặt lô hàng không tồn tại!");
				messages.setInternal_message("");
				return res;
			}
			if(productPosTrans.getQuantity_export()==0){
				Query query=em.createQuery("delete from ProductPos where id=:id");
			    query.setParameter("id",id);
				if(query.executeUpdate()<0){
					res=-1;
					messages.setUser_message("Xóa vị trí đặt lô hàng thất bại!");
					messages.setInternal_message("");
					ut.rollback();
					return res;
				}
			}else{
				res=-1;
				messages.setUser_message("Vị trí đặt lô hàng đã xuất không được xóa!");
				messages.setInternal_message("");
				ut.rollback();
				return res;
			}
			res=0;
			ut.commit();
		}catch(Exception e){
			res=-1;
			messages.setUser_message("Xóa thất bại!");
			messages.setInternal_message("ProcessLogicGoodsReceiptNotePosService.saveOrUpdateGoodsReceiptNotePosService:"+e.getMessage());
			ut.rollback();
			logger.error("ProcessLogicGoodsReceiptNotePosService.saveOrUpdateGoodsReceiptNotePosService:"+e.getMessage(),e);
		}finally {
			if (con != null)
				con.close();
		}
		return res;
	}
//	public static void main(String[] args) {
//		double num=0.033+0.21+0.03+0.1+0.015+0.22+0.11+0.2222;
//		double abs=Math.abs(num);
//		double log=Math.log10(abs);
//		double integerDigits = Math.floor(Math.log10(Math.abs(num))+1);
//	    double mult = Math.pow(10,(15-integerDigits)); // also consider integer digits
//	    double result = Math.round(num * mult) / mult;
//		System.out.println(result);
//		
//	}
}
