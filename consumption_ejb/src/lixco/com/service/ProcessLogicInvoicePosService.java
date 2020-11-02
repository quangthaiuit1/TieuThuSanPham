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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;

import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.BatchPos;
import lixco.com.entity.ExportBatchPos;
import lixco.com.entity.InvoiceDetailPos;
import lixco.com.entity.InvoicePos;
import lixco.com.entity.OrderDetailPos;
import lixco.com.entity.OrderLixPos;
import lixco.com.entity.Product;
import lixco.com.entity.ProductPos;
import lixco.com.entity.PromotionOrderDetailPos;
import lixco.com.interfaces.IProcessLogicInvoicePosService;
import lixco.com.reqInfo.InvoiceDetailPosReqInfo;
import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.WrapDataInvoiceDetailPos;
import lixco.com.reqInfo.WrapExportDataPosReqInfo;
import lixco.com.reqInfo.WrapInvoicePosReqInfo;
import lixco.com.reqInfo.WrapOrderDetailPosLixReqInfo;
import lixco.com.reqInfo.WrapOrderLixPosReqInfo;
@Stateless
@TransactionManagement(value = TransactionManagementType.BEAN)
public class ProcessLogicInvoicePosService implements IProcessLogicInvoicePosService{
	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	@Resource(lookup = "java:/consumption")
	DataSource datasource;
	@Resource
	private UserTransaction ut;
	@Override
	public int deleteInvoicePosMaster(long id, StringBuilder messages) throws IllegalStateException, SystemException, SQLException {
		return 0;
	}
	@Override
	public int deleteInvoiceDetailPosMaster(long id, StringBuilder messages) throws IllegalStateException, SystemException, SQLException {
		return 0;
	}
	@Override
	public int insertInvoicePosDetail(InvoiceDetailPosReqInfo t, StringBuilder messages) throws IllegalStateException, SystemException, SQLException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			InvoiceDetailPos detailPos=t.getInvoice_detail_pos();
			InvoicePos invoicePos=detailPos.getInvoice_pos();
			Product product=detailPos.getProduct();
			double quantityExport=detailPos.getQuantity();//số lượng sản phẩm.
			if(detailPos !=null && invoicePos !=null && invoicePos.getId()!=0 && product !=null && product.getId() !=0){
				double quantityBoxExport=BigDecimal.valueOf(quantityExport).divide(BigDecimal.valueOf(product.getSpecification())).doubleValue();
				//load trans hóa đơn
				CriteriaQuery<OrderLixPos> cqOrderLixPos=cb.createQuery(OrderLixPos.class);
				Root<InvoicePos> rootInvoicePos=cqOrderLixPos.from(InvoicePos.class);
				rootInvoicePos.fetch("order_lix_pos",JoinType.INNER);
				cqOrderLixPos.select(rootInvoicePos.get("order_lix_pos")).where(cb.equal(rootInvoicePos.get("id"), invoicePos.getId()));
				TypedQuery<OrderLixPos> queryOrderLixPos=em.createQuery(cqOrderLixPos);
				List<OrderLixPos> listOrderLixPos=queryOrderLixPos.getResultList();
				if(listOrderLixPos.size()>0){
					OrderLixPos orderLixPosTrans=listOrderLixPos.get(0);
					// kiểm tra sản phẩm đó đã tồn tại trong đơn hàng không nếu tồn tại trong đơn hàng thì tăng số lượng thực hiện của đơn hàng đó.
					CriteriaQuery<OrderDetailPos> cqOrderDetailPos=cb.createQuery(OrderDetailPos.class);
					Root<OrderDetailPos> rootOrderDetailPos=cqOrderDetailPos.from(OrderDetailPos.class);
					Predicate conp=cb.conjunction();
					conp.getExpressions().add(cb.equal(rootOrderDetailPos.get("order_lix_pos").get("id"),orderLixPosTrans.getId()));
					conp.getExpressions().add(cb.equal(rootOrderDetailPos.get("product").get("id"),product.getId()));
					cqOrderDetailPos.select(rootOrderDetailPos).where(conp);
					TypedQuery<OrderDetailPos> query=em.createQuery(cqOrderDetailPos);
					List<OrderDetailPos> listOrderDetailPosTrans =query.getResultList();

					for(OrderDetailPos d:listOrderDetailPosTrans){
						//số lượng thùng còn lại chưa xuất.
						double quantityBoxRe=BigDecimal.valueOf(d.getBox_quantity()).subtract(BigDecimal.valueOf(d.getRealbox_quantity())).doubleValue();
						if(quantityBoxRe!=0){
							//khấu trừ số thùng.
							if(quantityBoxExport>=quantityBoxRe){
								quantityBoxExport=BigDecimal.valueOf(quantityBoxExport).subtract(BigDecimal.valueOf(quantityBoxRe)).doubleValue();
								//cập nhật lại số thùng thực xuất chi tiết đơn hàng.
								double realBoxQuantityOrderDetail=BigDecimal.valueOf(d.getRealbox_quantity()).add(BigDecimal.valueOf(quantityBoxRe)).doubleValue();
								d.setRealbox_quantity(realBoxQuantityOrderDetail);
								if(em.merge(d)==null){
									res=-1;
									ut.rollback();
									messages.append("Cập nhật số lượng thực xuất của đơn hàng thất bại!");
									return res;
								}
							}else {
//								if(quantityBoxExport)
							}
							
						}
					}
					
				}else{
					// thông tin không đầy đủ
					res=-1;
					messages.append("Thông tin không đầy đủ!");
				}
				// tạo chi tiết hóa đơn
				
			}
		}catch (Exception e) {
			logger.error("ProcessLogicInvoicePosService.insertInvoicePosDetail:"+e.getMessage(),e);
		}finally {
			if(con!=null){
				con.close();
			}
		}
		return res;
	}
	@Override
	public int updateInvoicePosDetail(InvoiceDetailPosReqInfo t, StringBuilder messages) throws IllegalStateException, SystemException, SQLException {
		return 0;
	}
	@Override
	public int saveOrUpdateMasterTemp(WrapInvoicePosReqInfo t, StringBuilder messages) throws IllegalStateException, SystemException, SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int saveOrUpdateMaster(WrapInvoicePosReqInfo t, StringBuilder messages) throws IllegalStateException, SystemException, SQLException {
		return 0;
	}
	@Override
	public int selectInvoiceDetailPosById(long id, InvoiceDetailPosReqInfo t) throws IllegalStateException, SystemException, SQLException {
		return 0;
	}
	@Override
	public int selectInvoicePosById(long id, WrapInvoicePosReqInfo t)throws IllegalStateException, SystemException, SQLException {
		return 0;
	}
	@Override
	public int createInvoicePosByOrder(WrapOrderLixPosReqInfo t, StringBuilder messages) throws IllegalStateException, SystemException, SQLException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			OrderLixPos pOrderLixPos=t.getOrder_lix_pos();
			List<WrapOrderDetailPosLixReqInfo>  pListWrapOrderDetailPos=t.getList_wrap_order_detail_pos();
			List<PromotionOrderDetailPos> pListPromotionOrderDetailPos=t.getList_promotion_order_detail_pos();
			if(pOrderLixPos !=null && pListWrapOrderDetailPos !=null && pListWrapOrderDetailPos.size()>0){
				//lấy trans đơn hàng
				OrderLixPos transOrderPos=em.find(OrderLixPos.class, pOrderLixPos.getId());
				if(transOrderPos !=null){
					List<InvoiceDetailPos> listInvoiceDetailPos=new ArrayList<>();
					//cập nhật đơn hàng trạng thái và thực xuất
					//cập nhật chi tiết thực xuất đơn hàng.
					Map<OrderDetailPos, InvoiceDetailPos> map=new LinkedHashMap<>();
					for(WrapOrderDetailPosLixReqInfo w : pListWrapOrderDetailPos){
						OrderDetailPos d=w.getOrder_detail_pos();
						//lấy trans orderdetail
						OrderDetailPos transOrderDetail=em.find(OrderDetailPos.class, d.getId());
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
						InvoiceDetailPos detail=new InvoiceDetailPos();
						detail.setEx_order(true);
						detail.setProduct(d.getProduct());
						detail.setQuantity(w.getQuantity()*d.getProduct().getSpecification());
						detail.setUnit_price(transOrderDetail.getUnit_price());
						detail.setNote(transOrderDetail.getNote());
						listInvoiceDetailPos.add(detail);
						if(!map.containsKey(d)){
							map.put(d, detail);						
						}
					}
					//kiểm tra số lượng để set trạng thái
					Query query=em.createQuery("select count(id) from OrderDetailPos where order_lix_pos.id= :idm and box_quantity <> realbox_quantity ");
					query.setParameter("idm", transOrderPos.getId());
					int ck=Integer.parseInt(Objects.toString(query.getSingleResult()));
					if(ck>0){
						//cập nhật trạng thái đơn hàng là đang giao hàng
						transOrderPos.setStatus(1);
						if(em.merge(transOrderPos)==null){
							res=-1;
							ut.rollback();
							messages.append("Cập nhật trạng thái đơn hàng thất bại!");
							return res;
						}
						
					}else if(ck==0){
						//cập nhật trạng thái đã hoàn thành
						transOrderPos.setStatus(2);
						if(em.merge(transOrderPos)==null){
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
					InvoicePos invoicePos=new InvoicePos();
					invoicePos.setOrder_lix_pos(transOrderPos);
					invoicePos.setOrder_code(transOrderPos.getOrder_code());
					invoicePos.setOrder_voucher(transOrderPos.getVoucher_code());
					invoicePos.setCreated_by_id(t.getCreated_by_id());
					invoicePos.setCreated_by(t.getCreated_by());
					invoicePos.setCreated_date(new Date());
					invoicePos.setCustomer(pOrderLixPos.getCustomer());
					invoicePos.setInvoice_date(new Date());
					invoicePos.setPayment_method(pOrderLixPos.getPayment_method());
					invoicePos.setCar(pOrderLixPos.getCar());
					invoicePos.setDelivery_pricing(pOrderLixPos.getDelivery_pricing());//đơn giá vận chuyển chứa nơi đến
					invoicePos.setFreight_contract(pOrderLixPos.getFreight_contract());
					invoicePos.setIe_categories(pOrderLixPos.getIe_categories());
					invoicePos.setWarehouse(pOrderLixPos.getWarehouse());
					invoicePos.setTax_value(pOrderLixPos.getTax_value());
					invoicePos.setVoucher_code(pOrderLixPos.getVoucher_code());
					invoicePos.setPricing_program(pOrderLixPos.getPricing_program());
					invoicePos.setPromotion_program(pOrderLixPos.getPromotion_program());
					invoicePos.setPo_no(pOrderLixPos.getPo_no());
					invoicePos.setInvoice_code(initInvoiceCode());
					initCodeVoucher(invoicePos);
					em.persist(invoicePos);
					if(invoicePos.getId()==0){
						res=-1;
						ut.rollback();
						messages.append("Tạo hóa đơn thất bại!");
						return res;
					}
					//tạo chi tiết  cho đơn hàng thứ nhất
					for(InvoiceDetailPos v:listInvoiceDetailPos){
						v.setInvoice_pos(invoicePos);
						v.setCreated_by(t.getCreated_by());
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
					if(pListPromotionOrderDetailPos != null && pListPromotionOrderDetailPos.size()>0){
						//tạo đơn hàng khuyến mãi(thứ 2)
						InvoicePos invoicePos2=new InvoicePos();
						invoicePos2.setIpromotion(true);
						invoicePos2.setOrder_lix_pos(transOrderPos);
						invoicePos2.setOrder_code(transOrderPos.getOrder_code());
						invoicePos2.setOrder_voucher(transOrderPos.getVoucher_code());
						invoicePos2.setCreated_by_id(t.getCreated_by_id());
						invoicePos2.setCreated_by(t.getCreated_by());
						invoicePos2.setCreated_date(new Date());
						invoicePos2.setCustomer(pOrderLixPos.getCustomer());
						invoicePos2.setInvoice_date(new Date());
						invoicePos2.setPayment_method(pOrderLixPos.getPayment_method());
						invoicePos2.setCar(pOrderLixPos.getCar());
						invoicePos2.setDelivery_pricing(pOrderLixPos.getDelivery_pricing());//đơn giá vận chuyển chứa nơi đến
						invoicePos2.setFreight_contract(pOrderLixPos.getFreight_contract());
						invoicePos2.setIe_categories(pOrderLixPos.getIe_categories());
						invoicePos2.setWarehouse(pOrderLixPos.getWarehouse());
						invoicePos2.setTax_value(pOrderLixPos.getTax_value());
						invoicePos2.setVoucher_code(pOrderLixPos.getVoucher_code());
						invoicePos2.setPricing_program(pOrderLixPos.getPricing_program());
						invoicePos2.setPromotion_program(pOrderLixPos.getPromotion_program());
						invoicePos2.setPo_no(pOrderLixPos.getPo_no());
						invoicePos2.setInvoice_code(initInvoiceCode());
						initCodeVoucher(invoicePos2);
						em.persist(invoicePos2);
						if(invoicePos2.getId()==0){
							res=-1;
							ut.rollback();
							messages.append("Tạo hóa đơn khuyến mãi thất bại!");
							return res;
						}
						//lưu chi tiết hóa đơn khuyến mãi
						for(PromotionOrderDetailPos p: pListPromotionOrderDetailPos){
							InvoiceDetailPos invoiceMain=null;
							if(map.containsKey(p.getOrder_detail_pos())){
								invoiceMain=map.get(p.getOrder_detail_pos());
							}
							InvoiceDetailPos detailPos=new InvoiceDetailPos();
							detailPos.setInvoice_detail_pos_own(invoiceMain);
							detailPos.setEx_order(true);
							detailPos.setProductdh_code(p.getOrder_detail_pos().getProduct().getProduct_code());
							detailPos.setInvoice_pos(invoicePos2);
							detailPos.setCreated_by(t.getCreated_by());
							detailPos.setCreated_date(new Date());
							detailPos.setProduct(p.getProduct());
							detailPos.setQuantity(p.getQuantity());
							detailPos.setUnit_price(p.getUnit_price());
							detailPos.setNote(p.getNote());
							em.persist(detailPos);
							if(detailPos.getId()==0){
								res=-1;
								messages.append("Tạo chi tiết hóa đơn thất bại!");
								ut.rollback();
								return res;
							}
						}
					}
					res=0;
				}else{
					res=-1;
					ut.commit();
					messages.append("Đơn hàng không tồn tại!");
				}
			}
			ut.commit();
		}catch (Exception e) {
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
	private String initInvoiceCode(){
		try {
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq=cb.createQuery(Integer.class);
			Root<InvoicePos> root= cq.from(InvoicePos.class);
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
			logger.error("ProcessLogicInvoicePosService.initReceiptNoteCode:" + e.getMessage(), e);
		}
		return null;
	}
	private int initCodeVoucher(InvoicePos t){
		int res=-1;
		
		try{
			Date date=t.getInvoice_date();
			int year=ToolTimeCustomer.getYearM(date);
			int month=ToolTimeCustomer.getMonthM(date);
			int day=ToolTimeCustomer.getDayM(date);
					
			String voucher=day+""+month+""+year+"/";
			CriteriaBuilder cb=em.getCriteriaBuilder();
			CriteriaQuery<String> cq=cb.createQuery(String.class);
			Root<InvoicePos> root= cq.from(InvoicePos.class);
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
			logger.error("ProcessLogicInvoicePosService.initCode:"+e.getMessage(),e);
		}
		return res;
	}
	@Override
	public int saveListWrapExportDataPos(WrapDataInvoiceDetailPos t, Message message) throws IllegalStateException, SystemException, SQLException {
		int res=-1;
		Connection con=null;
		try{
			ut.begin();
			con = datasource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			CriteriaBuilder cb=em.getCriteriaBuilder();
			InvoiceDetailPos detail=t.getInvoice_detail_pos();
			List<WrapExportDataPosReqInfo> listData=t.getList_wrap_export_data_pos();
			if(detail !=null && detail.getId() !=0 && listData !=null && listData.size()>0){
				//load trans chi tiết hóa đơn
				InvoiceDetailPos invoiceDetailPosTrans=em.find(InvoiceDetailPos.class, detail.getId());
				if(invoiceDetailPosTrans==null){
					res=-1;
					ut.rollback();
					message.setUser_message("Chi tiết hóa đơn không tồn tại!");
					message.setInternal_message("error invoice detail not found!");
					return res;
				}
				double sumExportBox=0;
				//kiểm tra tổng số lượng xuất có lơn hơn số lượng yêu cầu không.
				for(WrapExportDataPosReqInfo q:listData){
					sumExportBox=BigDecimal.valueOf(sumExportBox).add(BigDecimal.valueOf(q.getQuantity_export_box())).doubleValue();
				}
				if(sumExportBox >invoiceDetailPosTrans.getQuantity()){
					res=-1;
					ut.rollback();
					message.setUser_message("Tổng số lượng thực xuất lớn hơn số lượng yêu cầu!");
					return res;
				}
				//xóa tất cả lô hàng xuất trước đó và trả lại số lượng lô hàng pos.
				//load danh sách lô hàng xuất trong trans.
				CriteriaQuery<ExportBatchPos> cqEBP=cb.createQuery(ExportBatchPos.class);
				Root<ExportBatchPos> rootEBP= cqEBP.from(ExportBatchPos.class);
				Fetch<ExportBatchPos,ProductPos> productPos_=rootEBP.fetch("product_pos",JoinType.INNER);
				productPos_.fetch("batch_pos",JoinType.INNER);
				cqEBP.select(rootEBP).where(cb.equal(rootEBP.get("invoice_detail_pos").get("id"),detail.getId()));
				TypedQuery<ExportBatchPos> queryEBP=em.createQuery(cqEBP);
				List<ExportBatchPos> listExportBatchPosTrans=queryEBP.getResultList();
				if(listExportBatchPosTrans.size()>0){
					for(ExportBatchPos ex:listExportBatchPosTrans){
						double quantityEBP=ex.getQuantity_export_box();
						ProductPos ppTrans=ex.getProduct_pos();
						BatchPos bpTrans=ppTrans.getBatch_pos();
						//trả lại số lượng cho vị trí đặt lô hàng.
						double quantityExportPP=BigDecimal.valueOf(ppTrans.getQuantity_export()).subtract(BigDecimal.valueOf(quantityEBP)).doubleValue();
						ppTrans.setQuantity_export(quantityExportPP);
						if(em.merge(ppTrans)==null){
							res=-1;
							ut.rollback();
							message.setUser_message("Lưu thất bại!");
							message.setInternal_message("");
							return res;
						}
						//trả lại số lượng cho lô hàng
						double quantityExportBP=BigDecimal.valueOf(bpTrans.getQuantity_export()).subtract(BigDecimal.valueOf(quantityEBP)).doubleValue();
						bpTrans.setQuantity_export(quantityExportBP);
						if(em.merge(bpTrans)==null){
							res=-1;
							ut.rollback();
							message.setUser_message("Lưu thất bại!");
							message.setInternal_message("");
							return res;
						}
					}
					Query queryDelExportBatchPos= em.createQuery("delete from ExportBatchPos as p where p.invoice_detail_pos.id=:idd");
					queryDelExportBatchPos.setParameter("idd",detail.getId());
					if(queryDelExportBatchPos.executeUpdate()<=0){
						res=-1;
						ut.rollback();
						message.setUser_message("Lưu thất bại!");
						message.setInternal_message("");
						return res;
					}
				}
				
				// tiến hành kiễm tra dữ liệu hợp lệ và tạo lại dữ liệu
				int i=0;
				for(WrapExportDataPosReqInfo ex:listData){
					i++;
					ExportBatchPos exportBatchPosOld=ex.getExport_batch_pos();
					double quantityExportBox=ex.getQuantity_export_box();
					ProductPos productPosOld=exportBatchPosOld.getProduct_pos();
					//set id bằng 0 cho trường hợp đã xuất trước đó 
					exportBatchPosOld.setId(0);
					/*thêm lại dữ liệu lô hàng xuất*/
					//kiểm tra vị trí đặt lô hàng có tồn tại không.
					CriteriaQuery<ProductPos> cqProductPos=cb.createQuery(ProductPos.class);
					Root<ProductPos> rootProductPos= cqProductPos.from(ProductPos.class);
					rootProductPos.fetch("batch_pos",JoinType.INNER);
					cqProductPos.select(rootProductPos).where(cb.equal(rootProductPos.get("id"),productPosOld.getId()));
					TypedQuery<ProductPos> queryProductPos=em.createQuery(cqProductPos);
					ProductPos productPosTrans=queryProductPos.getSingleResult();
					if(productPosTrans==null){
						res=-1;
						ut.rollback();
						message.setUser_message("Vị trí đặt lô hàng không tồn tại!");
						message.setInternal_message("error row "+i);
						return res;
					}
					BatchPos batchPosTrans=productPosTrans.getBatch_pos();
					//số lượng thùng tồn hiện tại của vị trí đặt lô hàng
					double quantityExportBoxTrans=BigDecimal.valueOf(productPosTrans.getQuantity_import()).subtract(BigDecimal.valueOf(productPosTrans.getQuantity_export())).doubleValue();
					//kiểm tra số lượng có đáp ứng được không
					if(quantityExportBox>quantityExportBoxTrans){
						res=-1;
						ut.rollback();
						message.setUser_message("Số lượng thùng xuất tại vị trí đặt lô hàng lớn hơn số lượng thùng tồn hiện có!");
						message.setInternal_message("error row "+i);
						return res;
					}
//						//kiểm tra tổng số lượng thùng xuất tới dòng hiện tại đang chạy có số lượng xuất lớn hơn số lượng yêu cầu
//						sumExportBox=BigDecimal.valueOf(sumExportBox).add(BigDecimal.valueOf(quantityExportBox)).doubleValue();
//						if(sumExportBox> invoiceDetailPosTrans.getQuantity()){
//							res=-1;
//							ut.rollback();
//							message.setUser_message("Số lượng thực xuất lớn hơn số lượng yêu cầu!");
//							message.setInternal_message("error row "+i);
//							return res;
//						}
					exportBatchPosOld.setInvoice_detail_pos(invoiceDetailPosTrans);
					exportBatchPosOld.setQuantity_export_box(quantityExportBox);
					//lưu lô hàng xuất 
					em.persist(exportBatchPosOld);
					if(exportBatchPosOld.getId()==0){
						res=-1;
						ut.rollback();
						message.setUser_message("Số lượng thực xuất lớn hơn số lượng yêu cầu!");
						message.setInternal_message("error row "+i);
						return res;
					}
					//giảm số lượng tồn tại vị trí đặt lô hàng
					double quantityExportProductPos=BigDecimal.valueOf(productPosTrans.getQuantity_export()).add(BigDecimal.valueOf(quantityExportBox)).doubleValue();
					productPosTrans.setQuantity_export(quantityExportProductPos);
					if(em.merge(productPosTrans)==null){
						res=-1;
						ut.rollback();
						message.setUser_message("Cập nhật lại số lượng tồn tại vị trí đặt lô hàng thất bại sau khi xuất!");
						message.setInternal_message("error row "+i);
						return res;
					}
					//giảm số lượng tồn trong lô hàng
					double quantityExportBatchPos=BigDecimal.valueOf(batchPosTrans.getQuantity_export()).add(BigDecimal.valueOf(quantityExportBox)).doubleValue();
					batchPosTrans.setQuantity_export(quantityExportBatchPos);
					if(em.merge(batchPosTrans)==null){
						res=-1;
						ut.rollback();
						message.setUser_message("Cập nhật lại số lượng tồn lô hàng thất bại sau khi xuất!");
						message.setInternal_message("error row "+i);
						return res;
					}
						
				}
				//cập lại chi tiết hóa đơn thực xuất.
				invoiceDetailPosTrans.setReal_quantity(sumExportBox);
				if(em.merge(invoiceDetailPosTrans)==null){
					res=-1;
					ut.rollback();
					message.setUser_message("Cập nhật thực xuất lô hàng thất bại!");
					message.setInternal_message("");
					return res;
				}
			}else{
				res=-1;
				ut.rollback();
				message.setUser_message("Thông tin không đúng!");
				message.setInternal_message("error request data!");
				return res;
			}
			ut.commit();
			res=0;
		}catch (Exception e) {
			res=-1;
			ut.rollback();
			message.setUser_message("Lưu thất bại!");
			message.setInternal_message("ProcessLogicInvoicePosService.saveListWrapExportDataPos:"+e.getMessage());
			logger.error("ProcessLogicInvoicePosService.saveListWrapExportDataPos:"+e.getMessage(),e);
		}finally{
			if (con != null)
				con.close();
		}
		return res;
	}
//	public static void main(String[] args) {
//		BigDecimal bg1=new BigDecimal(0.01);
//		BigDecimal bg2=new BigDecimal("0.01");
//		double bg3=BigDecimal.valueOf(0.01).doubleValue();
//		System.out.println(bg1);
//		System.out.println(bg2);
//		System.out.println(bg3);
//		System.out.println(0.033+0.21+0.03+0.1+0.015+0.22+0.11+0.2222);
//		
//	}

}
