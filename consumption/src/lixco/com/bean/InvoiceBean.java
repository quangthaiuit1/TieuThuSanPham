package lixco.com.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;

import com.google.gson.JsonObject;

import lixco.com.common.FormatHandler;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Batch;
import lixco.com.entity.Car;
import lixco.com.entity.Carrier;
import lixco.com.entity.Customer;
import lixco.com.entity.CustomerTypes;
import lixco.com.entity.ExportBatch;
import lixco.com.entity.ExportBatchPos;
import lixco.com.entity.FormUpGoods;
import lixco.com.entity.FreightContract;
import lixco.com.entity.HarborCategory;
import lixco.com.entity.IECategories;
import lixco.com.entity.Invoice;
import lixco.com.entity.InvoiceDetail;
import lixco.com.entity.InvoiceDetailPos;
import lixco.com.entity.PaymentMethod;
import lixco.com.entity.ProductPos;
import lixco.com.entity.Stevedore;
import lixco.com.entity.Stocker;
import lixco.com.entity.Warehouse;
import lixco.com.interfaces.IBatchService;
import lixco.com.interfaces.ICarService;
import lixco.com.interfaces.ICarrierService;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.IFormUpGoodsService;
import lixco.com.interfaces.IHarborCategoryService;
import lixco.com.interfaces.IIECategoriesService;
import lixco.com.interfaces.IInvoiceDetailService;
import lixco.com.interfaces.IInvoiceService;
import lixco.com.interfaces.IPaymentMethodService;
import lixco.com.interfaces.IProcessLogicInvoiceService;
import lixco.com.interfaces.IProductService;
import lixco.com.interfaces.IStevedoreService;
import lixco.com.interfaces.IStockerService;
import lixco.com.interfaces.IWarehouseService;
import lixco.com.reqInfo.BatchReqInfo;
import lixco.com.reqInfo.InvoiceDetailReqInfo;
import lixco.com.reqInfo.InvoiceReqInfo;
import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.ProductPosReqInfo;
import lixco.com.reqInfo.WrapDataInvoiceDetail;
import lixco.com.reqInfo.WrapDataInvoiceDetailPos;
import lixco.com.reqInfo.WrapDelExportBatchReqInfo;
import lixco.com.reqInfo.WrapDelInvoiceDetailReqInfo;
import lixco.com.reqInfo.WrapExportDataPosReqInfo;
import lixco.com.reqInfo.WrapExportDataReqInfo;
import lixco.com.reqInfo.WrapInvoiceDelInfo;
import lixco.com.reqInfo.WrapInvoiceDetailReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class InvoiceBean extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IProcessLogicInvoiceService processLogicInvoiceService;
	@Inject
	private IProductService productService;
	@Inject
	private ICustomerService customerService;
	@Inject
	private IPaymentMethodService paymentMethodService;
	@Inject
	private IIECategoriesService iECategoriesService;
	@Inject
	private IWarehouseService warehouseService;
	@Inject
	private ICarService carService;
	@Inject
	private ICarrierService carrierService;
	@Inject
	private IHarborCategoryService harborCategoryService;
	@Inject
	private IStevedoreService stevedoreService;
	@Inject
	private IFormUpGoodsService formUpGoodsService;
	@Inject
	private IStockerService stockerService;
	@Inject
	private IInvoiceService invoiceService;
	@Inject
	private IInvoiceDetailService invoiceDetailService;
	@Inject
	private IBatchService batchService;
	private Invoice invoiceCrud;
	private Invoice invoiceSelect;
	private List<Invoice> listInvoice;
	private WrapInvoiceDetailReqInfo wrapInvoiceDetailCrud;
	private WrapInvoiceDetailReqInfo wrapInvoiceDetailSelect;
	private List<WrapInvoiceDetailReqInfo> listWrapInvoiceDetail;
	private List<WrapInvoiceDetailReqInfo> listWrapInvoiceDetailFilter;
	private List<PaymentMethod> listPaymentMethod;
	private List<IECategories> listIECategories;
	private List<Warehouse> listWarehouse;
	private List<Stocker> listStocker;
	private List<Stevedore> listStevedores;
	private List<HarborCategory> listHarborCategory;
	private List<FormUpGoods> listFormUpGoods;
	private FormatHandler formatHandler;
	private Date fromDateFCFilter;
	private Date toDateFCFilter;
	private CustomerTypes customerTypesFCFilter;
	private Customer customerFcFilter;
	private String contractNoFCFilter;
	private List<FreightContract> listFreightContractSelect;
	private int pageSizeHDVC;
	private NavigationInfo navigationInfoHDVC;
	private List<Integer> listRowPerPageHDVC;
	private String keySearchCustomer;
	private List<Customer> listCustomerSelect;
	/*search invoice*/
	private Date fromDateSearch;
	private Date toDateSearch;
	private Customer customerSearch;
	private String invoiceCodeSearch;
	private String voucherCodeSearch;
	private IECategories ieCategoriesSearch;
	private String poNoSearch;
	private String orderVoucherSearch;
	private String orderCodeSearch;
	private int paymentSearch;
	private int statusSearch;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	private Account account;
	/*Export lô hàng*/
	private WrapInvoiceDetailReqInfo wrapInvoiceDetailPick;
	private List<WrapExportDataReqInfo> listWrapExportData;
	
	@Override
	protected void initItem() {
		try{
			formatHandler=FormatHandler.getInstance();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			listIECategories=new ArrayList<IECategories>();
			iECategoriesService.selectAll(listIECategories);
			listPaymentMethod=new ArrayList<>();
			paymentMethodService.selectAll(listPaymentMethod);
			listWarehouse=new ArrayList<>();
			warehouseService.selectAll(listWarehouse);
			listHarborCategory=new ArrayList<>();
			harborCategoryService.search(0, listHarborCategory);
			listStevedores=new ArrayList<>();
			stevedoreService.selectAll(listStevedores);
			listFormUpGoods=new ArrayList<>();
			formUpGoodsService.selectAll(listFormUpGoods);
			listStocker=new ArrayList<>();
			stockerService.search(null,0, listStocker);
			statusSearch=-1;
			paymentSearch=-1;
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			search();
			createdNew();
		}catch(Exception e){
			logger.error("InvoiceBean.initItem:"+e.getMessage(),e);
		}
		
	}
	public void search(){
		try{
			/*{ invoice_info:{from_date:'',to_date:'',customer_id:0,invoice_code:'',voucher_code:'',ie_categories_id:0,po_no:'',order_code:'',order_voucher:'',payment:-1,status:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listInvoice=new ArrayList<>();
			PagingInfo page=new PagingInfo();
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 :customerSearch.getId());
			jsonInfo.addProperty("invoice_code", invoiceCodeSearch);
			jsonInfo.addProperty("voucher_code", voucherCodeSearch);
			jsonInfo.addProperty("ie_categories_id", ieCategoriesSearch==null ? 0 :ieCategoriesSearch.getId());
			jsonInfo.addProperty("po_no", poNoSearch);
			jsonInfo.addProperty("order_code",orderCodeSearch);
			jsonInfo.addProperty("order_voucher",orderVoucherSearch);
			jsonInfo.addProperty("payment", paymentSearch);
			jsonInfo.addProperty("status", statusSearch);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("invoice_info", jsonInfo);
			json.add("page", jsonPage);
			invoiceService.search(JsonParserUtil.getGson().toJson(json), page, listInvoice);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		}catch(Exception e){
			logger.error("InvoiceBean.search:"+e.getMessage(),e);
		}
	}
	private void initRowPerPage() {
		try {
			listRowPerPage = new ArrayList<Integer>();
			listRowPerPage.add(90);
			listRowPerPage.add(180);
			listRowPerPage.add(240);
			pageSize = listRowPerPage.get(0);
		} catch (Exception e) {
			logger.error("InvoiceBean.initRowPerPage:" + e.getMessage(), e);
		}
	}
	public void paginatorChange(int currentPage) {
		try {
			/*{ invoice_info:{from_date:'',to_date:'',customer_id:0,invoice_code:'',voucher_code:'',ie_categories_id:0,po_no:'',order_code:'',order_voucher:'',payment:-1,status:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listInvoice = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 :customerSearch.getId());
			jsonInfo.addProperty("invoice_code", invoiceCodeSearch);
			jsonInfo.addProperty("voucher_code", voucherCodeSearch);
			jsonInfo.addProperty("ie_categories_id", ieCategoriesSearch==null ? 0 :ieCategoriesSearch.getId());
			jsonInfo.addProperty("po_no", poNoSearch);
			jsonInfo.addProperty("order_code",orderCodeSearch);
			jsonInfo.addProperty("order_voucher",orderVoucherSearch);
			jsonInfo.addProperty("payment", paymentSearch);
			jsonInfo.addProperty("status", statusSearch);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("invoice_info", jsonInfo);
			json.add("page", jsonPage);
			invoiceService.search(JsonParserUtil.getGson().toJson(json), page, listInvoice);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("InvoiceBean.paginatorChange:" + e.getMessage(), e);
		}
	}
	public void createdNew(){
		try{
			invoiceCrud=new Invoice();
			//tạo mã đơn hàng
			invoiceCrud.setInvoice_date(ToolTimeCustomer.getFirstDateOfDay(new  Date()));
			invoiceCrud.setCreated_by(account.getMember().getName());
			listWrapInvoiceDetail=new ArrayList<>();
		}catch(Exception e){
			logger.error("InvoiceBean.createNew:" + e.getMessage(), e);
		}
	}
	public void showDialogPickHDVC(){
		PrimeFaces current=PrimeFaces.current();
		try{
			fromDateFCFilter=ToolTimeCustomer.getDateMinCustomer(ToolTimeCustomer.getMonthCurrent(), ToolTimeCustomer.getYearCurrent());
			toDateFCFilter=ToolTimeCustomer.getDateMaxCustomer(ToolTimeCustomer.getMonthCurrent(), ToolTimeCustomer.getYearCurrent());
			contractNoFCFilter=null;
			keySearchCustomer=null;
			listCustomerSelect=null;
			customerFcFilter=null;
			customerTypesFCFilter=null;
			navigationInfoHDVC = new NavigationInfo();
			navigationInfoHDVC.setCurrentPage(1);
			initRowPerPageHDVC();
			navigationInfoHDVC.setLimit(pageSizeHDVC);
			navigationInfoHDVC.setMaxIndices(5);
			current.executeScript("PF('dlg2').show();");
		}catch(Exception e){
			logger.error("InvoiceBean.showDialogPickHDVC:" + e.getMessage(), e);
		}
	}
	public void showEditInvoice(){
		try{
			if(invoiceSelect !=null ){
				invoiceCrud=invoiceSelect.clone();
				//show thông tin chi tiết hóa đơn
				listWrapInvoiceDetail=new ArrayList<>();
				List<InvoiceDetail> listInvoiceDetail=new ArrayList<>();
				invoiceDetailService.selectByOrder(invoiceCrud.getId(), listInvoiceDetail);
				for(InvoiceDetail dt:listInvoiceDetail){
					List<ExportBatch> list=new ArrayList<>();
					invoiceService.getListExportBatch(dt.getId(), list);
					WrapInvoiceDetailReqInfo w=new WrapInvoiceDetailReqInfo(dt, list);
					listWrapInvoiceDetail.add(w);
				}
				//tính tổng tiền hóa đơn
				sumInvoice();
			}
		}catch(Exception e){
			logger.error("InvoiceBean.showEditInvoice:" + e.getMessage(), e);
		}
	}
	public void showEditInvoiceDetail(){
		PrimeFaces current = PrimeFaces.current();
		try {
			if (wrapInvoiceDetailSelect != null) {
					wrapInvoiceDetailCrud = wrapInvoiceDetailSelect.clone();
					wrapInvoiceDetailCrud.getInvoice_detail().setInvoice(invoiceCrud);
					current.executeScript("PF('dlgc1').show();");
			}
		} catch (Exception e) {
			logger.error("InvoiceBean.showEditInvoiceDetail:" + e.getMessage(), e);
		}
	}
	private void initRowPerPageHDVC() {
		try {
			listRowPerPageHDVC = new ArrayList<Integer>();
			listRowPerPageHDVC.add(90);
			listRowPerPageHDVC.add(180);
			listRowPerPageHDVC.add(240);
			pageSizeHDVC = listRowPerPageHDVC.get(0);
		} catch (Exception e) {
			logger.error("InvoiceBean.initRowPerPage:" + e.getMessage(), e);
		}
	}
	public List<Car> completeCar(String text){
		try{
			if(text !=null && !"".equals(text)){
				List<Car> list=new ArrayList<>();
				carService.complete(formatHandler.converViToEn(text), list);
				return list;
			}
		}catch(Exception e){
			logger.error("InvoiceBean.completeCar:" + e.getMessage(), e);
		}
		return null;
	}
	public List<Customer> completeCustomer(String text){
		try{
			List<Customer> list=new ArrayList<>();
			customerService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("InvoiceBean.completeCustomer:"+e.getMessage(),e);
		}
		return null;
	}
	public List<Carrier> completeCarrier(String text){
		try{
			List<Carrier> list=new ArrayList<>();
			carrierService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch (Exception e) {
			logger.error("InvoiceBean.completeCarrier:"+e.getMessage(),e);
		}
		return null;
	}
	public double sumInvoice(){
		try{
			if(listWrapInvoiceDetail !=null && listWrapInvoiceDetail.size()>0){
				double totalevent = listWrapInvoiceDetail.stream().mapToDouble(f -> f.getInvoice_detail().getQuantity()*f.getInvoice_detail().getUnit_price()).sum();
				return totalevent;
			}
		}catch(Exception e){
			logger.error("InvoiceBean.sumInvoice:" + e.getMessage(), e);
		}
		return 0;
	}
	public void deleteInvoiceDetail(){
		PrimeFaces current=PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(wrapInvoiceDetailSelect!=null){
				if(allowDelete(new Date())){
					InvoiceDetailReqInfo tempReqInfo=new InvoiceDetailReqInfo();
					invoiceDetailService.selectById(wrapInvoiceDetailSelect.getInvoice_detail().getId(), tempReqInfo);
					InvoiceDetail detail=tempReqInfo.getInvoice_detail();
					if(detail !=null){
						StringBuilder messages=new StringBuilder();
						InvoiceDetail detailOwn=detail.getInvoice_detail_own();
						//nếu đây là chi tiết hóa đơn khuyến mãi
						if(detailOwn !=null){
							//lấy tất cả chi tiết khuyến mãi liên quan bao gồm cả chi tiết ta muốn xóa
							List<InvoiceDetail> listDetailKML=new ArrayList<>();
							invoiceDetailService.selectByInvoiceDetailMain(detailOwn.getId(), listDetailKML);
							List<Long> listID=new ArrayList<>();
							for(InvoiceDetail dt:listDetailKML){
								listID.add(dt.getId());
							}
							//mã thông báo
							messages.append("Nếu xóa chi tiết khuyến mãi này sẽ xóa dữ liệu sau: \\n ");
							messages.append("Chi tiết khuyến mãi liên quan: "+listID+" \\n");
							messages.append("Chi tiết hóa đơn chính: "+detailOwn.getId());
							messages.append("Bạn có muốn xóa không ?");
						}else{
							//ngược lại đây là hóa đơn chính
							//lấy tất cả chi tiết khuyến mãi liên quan
							List<InvoiceDetail> listDetailKM=new ArrayList<>();
							invoiceDetailService.selectByInvoiceDetailMain(wrapInvoiceDetailSelect.getInvoice_detail().getId(), listDetailKM);
							List<Long> listID=new ArrayList<>();
							for(InvoiceDetail dt:listDetailKM){
								listID.add(dt.getId());
							}
							//mã thông báo
							messages.append("Nếu xóa chi tiết hóa đơn chính này sẽ xóa dữ liệu sau: \\n ");
							messages.append("Chi tiết hóa đơn chính: "+invoiceSelect.getId());
							messages.append("Chi tiết khuyến mãi liên quan: "+listID+" \\n");
							messages.append("Bạn có muốn xóa không ?");
						}
						current.executeScript(
								"swalfunction('Cảnh báo','"+messages.toString()+"','warning','acceptDelInvoiceDetail()');");
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
				}
			}else{
				notify.warning("Chưa chọn dòng chi tiết để xóa!");
			}
		}catch(Exception e){
			logger.error("InvoiceBean.deleteInvoiceDetail:" + e.getMessage(), e);
		}
	}
	public void acceptDelInvoiceDetail(){
		PrimeFaces current=PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			WrapDelInvoiceDetailReqInfo t1=new WrapDelInvoiceDetailReqInfo(account.getMember().getId(), account.getMember().getName(),wrapInvoiceDetailSelect.getInvoice_detail().getId());
			Message message=new Message();
			int code=processLogicInvoiceService.deleteInvoiceDetailMaster(t1, message);
			if(code==0){
				listWrapInvoiceDetail.remove(wrapInvoiceDetailSelect);
				notify.success("Xóa thành công!");
			}else{
				// đưa ra mã lỗi
				String m=message.getUser_message()+" \\n"+message.getInternal_message();
				current.executeScript(
						"swaldesignclose('Thất bại', '"+m+"','warning');");
			}
		}catch (Exception e) {
			logger.error("InvoiceBean.acceptDelInvoiceDetail:" + e.getMessage(), e);
		}
	}
	public void deleteInvoice(){
		PrimeFaces current=PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(invoiceSelect !=null){
				if(allowDelete(new Date())){
					InvoiceReqInfo t=new  InvoiceReqInfo();
					//Nếu đây là hóa đơn khuyến mãi
					if(invoiceSelect.isIpromotion()){
						//lấy hóa đơn chính 
						invoiceService.selectById(invoiceSelect.getId(), t);
						Invoice invoiceTemp=t.getInvoice();
						if(invoiceTemp !=null){
							Invoice invoiceMainTemp=invoiceTemp.getInvoice_own();
							if(invoiceMainTemp!=null){
								current.executeScript(
										"swalfunction('Cảnh báo','Hóa đơn khuyến mãi này liên kết với hóa đơn chính "+invoiceMainTemp.getInvoice_code()+
										" bạn có muốn xóa luôn hóa đơn chính không','warning','acceptDelInvoice()');");
							}
						}
					}else{
						//Nếu đây là hóa đơn chính
						//lấy hóa đơn khuyến mãi
						invoiceService.selectByInvoiceOwn(invoiceSelect.getId(), t);
						Invoice invoiceKMTemp=t.getInvoice();
						if(invoiceKMTemp !=null){
							current.executeScript(
									"swalfunction('Cảnh báo','Hóa đơn chính này liên kết với hóa đơn khuyến mãi "+invoiceKMTemp.getInvoice_code()+
									" bạn có muốn xóa luôn hóa đơn khuyến mãi không','warning','acceptDelInvoice()');");
						}
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
				}
			}else{
				notify.warning("Chưa chọn dòng để xóa!");
			}
		}catch (Exception e) {
			logger.error("InvoiceBean.deleteInvoice:" + e.getMessage(), e);
		}
	}
	public void acceptDelInvoice(){
		PrimeFaces current=PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			WrapInvoiceDelInfo t1=new WrapInvoiceDelInfo(invoiceSelect.getId(), account.getMember().getId(),  account.getMember().getName());
			Message message=new Message();
			int code=processLogicInvoiceService.deleteInvoiceMaster(t1, message);
			if(code==0){
				listInvoice.remove(invoiceSelect);
				notify.success("Xóa thành công!");
			}else{
				// đưa ra mã lỗi
				String m=message.getUser_message()+" \\n"+message.getInternal_message();
				current.executeScript(
						"swaldesignclose('Thất bại', '"+m+"','warning');");
			}
		}catch (Exception e) {
			logger.error("InvoiceBean.acceptDelInvoice:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdateInvoiceTemp(){
		try{
			
		}catch (Exception e) {
			logger.error("InvoiceBean.saveOrUpdateInvoice:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdateInvoice(){
		try{
			
		}catch (Exception e) {
			logger.error("InvoiceBean.saveOrUpdateInvoice:" + e.getMessage(), e);
		}
	}
	public void exportPDF(){
		try{
			
		}catch (Exception e) {
			logger.error("InvoiceBean.exportPDF:" + e.getMessage(), e);
		}
	}
	public void destroyInvoice(){
		try{
			
		}catch (Exception e) {
			logger.error("InvoiceBean.destroyInvoice:" + e.getMessage(), e);
		}
	}
	public void deleteExportBatch(ExportBatch item,List<ExportBatch> list){
		PrimeFaces current=PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			WrapDelExportBatchReqInfo t=new WrapDelExportBatchReqInfo(item.getId(), account.getMember().getId(), account.getMember().getName());
			Message message=new Message();
			int code=processLogicInvoiceService.deleteExportBatch(t, message);
			if(code==0){
				list.remove(item);
				notify.success("xóa lô hàng xuất thành công!");
			}else{
				//đưa ra mã lỗi
				String m=message.getUser_message()+" \\n"+message.getInternal_message();
				current.executeScript(
						"swaldesignclose('Thất bại', '"+m+"','warning');");
			}
		}catch (Exception e) {
			logger.error("InvoiceBean.deleteExportBatch:" + e.getMessage(), e);
		}
	}
	public void showDialogExport(WrapInvoiceDetailReqInfo item){
		PrimeFaces current=PrimeFaces.current();
		try{
			wrapInvoiceDetailPick =item;
			InvoiceDetail invoiceDetail=item.getInvoice_detail();
			listWrapExportData=new ArrayList<>();
			//lấy danh sách lô hàng đã xuất và chưa xuất(còn tồn) cho chi tiết này
			List<ExportBatch> listExportBatch=new ArrayList<>();
			List<Batch> listBatch=new ArrayList<>();
			batchService.exportBatchByInvoiceDetail(invoiceDetail.getProduct().getId(), invoiceDetail.getId(), listExportBatch, listBatch);
			for(ExportBatch eb:listExportBatch){
				eb.setSelect(true);
			}
			for(Batch b:listBatch){
				ExportBatch e=new ExportBatch();
				e.setBatch(b);
				listExportBatch.add(e);
			}
			Collections.sort(listExportBatch, new Comparator<ExportBatch>() {

				@Override
				public int compare(final ExportBatch record1, final ExportBatch record2) {
					int c;
					Batch batch1=record1.getBatch();
					Batch batch2=record2.getBatch();
				    c =batch1.getManufacture_date().compareTo(batch2.getManufacture_date());
				    return c;
				 }

			});
			//prepare list wrap
			for(ExportBatch ex:listExportBatch){
				WrapExportDataReqInfo w=new WrapExportDataReqInfo();
				w.setExport_batch(ex);
				w.setQuantity_export(ex.getQuantity());
				w.setSelect(ex.isSelect());
				listWrapExportData.add(w);
			}
			current.executeScript("PF('dlgexport').show();");
		}catch (Exception e) {
			logger.error("InvoiceBean.showDialogExport:" + e.getMessage(), e);
		}
	}
	public void selectExportBatch(WrapExportDataReqInfo t){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			Batch batch=t.getExport_batch().getBatch();
			InvoiceDetail invoiceDetailPick=wrapInvoiceDetailPick.getInvoice_detail();
			if(t.isSelect()){ 
				//load lại Batch hiện tại
				BatchReqInfo brq=new BatchReqInfo();
				batchService.selectById(batch.getId(), brq);
				batch=brq.getBatch();
				t.getExport_batch().setBatch(batch);
				//số lượng tồn đặt tại vị trí đó.
				double soTon=BigDecimal.valueOf(batch.getQuantity_import())
						.subtract(BigDecimal.valueOf(batch.getQuantity_export())).doubleValue();
				//số lượng yêu cầu và số lượng thực xuất chi tiết hóa đơn
				double quantity=invoiceDetailPick.getQuantity();
				double quantityReal=invoiceDetailPick.getReal_quantity();
				//số lượng yêu cầu còn lại chưa xuất
				double quantityEx=BigDecimal.valueOf(quantity).subtract(BigDecimal.valueOf(quantityReal)).doubleValue();
				if(quantityEx==0)
				{
					//đã đủ số lượng.
					t.setSelect(false);
					notify.warning("Đã xuất đủ số lượng!");
					return;
				}
				double soluongCal=BigDecimal.valueOf(quantityEx).subtract(BigDecimal.valueOf(soTon)).doubleValue();
				if(soluongCal>=0){
					t.setQuantity_export(soTon);
					//tăng số lượng thực xuất
					quantityReal=BigDecimal.valueOf(quantityReal).add(BigDecimal.valueOf(soTon)).doubleValue();
				}else{
					t.setQuantity_export(quantityEx);
					//tăng số lượng thực xuất
					quantityReal=BigDecimal.valueOf(quantityReal).add(BigDecimal.valueOf(quantityEx)).doubleValue();
				}
				invoiceDetailPick.setReal_quantity(quantityReal);
			}else{
				double quantityExBox=t.getQuantity_export();
				double quantityReal=BigDecimal.valueOf(invoiceDetailPick.getReal_quantity()).subtract(BigDecimal.valueOf(quantityExBox)).doubleValue();
				invoiceDetailPick.setReal_quantity(quantityReal);
				t.setQuantity_export(0);
			}
		}catch (Exception e) {
			logger.error("InvoicePosBean.selectProductPosExport:" + e.getMessage(), e);
		}
		PrimeFaces.current().ajax().update("menuformid:tabview1:tablect:"+listWrapInvoiceDetail.indexOf(wrapInvoiceDetailPick)+":tableInner");
	}
	public void changeQuantityExportBatch(WrapExportDataReqInfo item){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			//cập nhật lại số lượng thực xuất chi tiết phiếu nhập
			if(item.isSelect()){
				//tính toán lại số lượng pallet xuất
				BatchReqInfo brq=new BatchReqInfo();
				batchService.selectById(item.getExport_batch().getBatch().getId(),brq);
				Batch batch=brq.getBatch();
				item.getExport_batch().setBatch(batch);
				InvoiceDetail invoiceDetailPick=wrapInvoiceDetailPick.getInvoice_detail();
				//số lượng chỉnh sửa
				double quantityEdit=item.getQuantity_export();
				//số lượng tồn của lô hàng
				double quantityTon=BigDecimal.valueOf(batch.getQuantity_import()).subtract(BigDecimal.valueOf(batch.getQuantity_export())).doubleValue();
				double sum=0;
				for(WrapExportDataReqInfo ex:listWrapExportData){
					if(ex.isSelect()){
					   sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(ex.getQuantity_export())).doubleValue();
					}
				}
				if(quantityEdit>quantityTon){
					//trả lại trạng thái trước.
					item.setQuantity_export(item.getExport_batch().getQuantity());
					notify.warning("Số lượng xuất lớn hơn số lượng tồn.");
					return;
				}
				if(sum >invoiceDetailPick.getQuantity()){
					//trả lại trạng thái trước.
					item.setQuantity_export(item.getExport_batch().getQuantity());
					notify.warning("Số lượng xuất lớn hơn số lượng yêu cầu.");
					sum=0;
					for(WrapExportDataReqInfo ex:listWrapExportData){
						if(ex.isSelect()){
						   sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(ex.getQuantity_export())).doubleValue();
						}
					}
					invoiceDetailPick.setReal_quantity(sum);
					return;
				}
				sum=0;
				for(WrapExportDataReqInfo ex:listWrapExportData){
					if(ex.isSelect()){
					   sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(ex.getQuantity_export())).doubleValue();
					}
				}
				invoiceDetailPick.setReal_quantity(sum);
			}
		}catch (Exception e) {
			logger.error("InvoiceBean.changeQuantityExportBatch:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdateExportBatch(){
		try{
			PrimeFaces current=PrimeFaces.current();
			Notify notify = new Notify(FacesContext.getCurrentInstance());
			try{
				if(listWrapExportData !=null && listWrapExportData.size()>0){
					InvoiceDetail invoiceDetailPick= wrapInvoiceDetailPick.getInvoice_detail();
					//prepare wrap  để lưu/ cập nhật dữ liệu.
					WrapDataInvoiceDetail  data=new WrapDataInvoiceDetail(invoiceDetailPick, new ArrayList<>(),account.getMember().getName());
					for(WrapExportDataReqInfo p:listWrapExportData){
						if(p.isSelect()){
							data.getList_wrap_export_data().add(p);
						}
					}
					//lưu lô hàng 
					Message message=new Message();
					int code=processLogicInvoiceService.saveListWrapExportData(data, message);
					if(code>=0){
					    //load lại dữ liệu cho lô hàng đó.
						List<ExportBatchPos> list=new ArrayList<>();
//						invoicePosService.getListExportBatchPos(invoiceDetailPosPick.getId(), list);
//						wrapInvoiceDetailPosPick.setList_export_batch_pos(list);
						//update current row.
						notify.success("Lưu lô hàng thành công!");
					}else{
						String m=message.getUser_message()+" \\n"+message.getInternal_message();
						current.executeScript(
								"swaldesignclose('Thất bại', '"+m+"','warning');");
					}
				}
			}catch (Exception e) {
				logger.error("InvoicePosBean.saveOrUpdateExportBatchPos:" + e.getMessage(), e);
			}
//			PrimeFaces.current().ajax().update("menuformid:tabview1:tablect:"+listWrapInvoiceDetailPosReqInfo.indexOf(wrapInvoiceDetailPosPick)+":tableInner");
//			PrimeFaces.current().ajax().update("menuformid:tabview1:tablect:"+listWrapInvoiceDetailPosReqInfo.indexOf(wrapInvoiceDetailPosPick)+":realExport");
		}catch (Exception e) {
			logger.error("InvoiceBean.changeQuantityExportBatch:" + e.getMessage(), e);
		}
	}
	@Override
	protected Logger getLogger() {
		return logger;
	}

	public Invoice getInvoiceCrud() {
		return invoiceCrud;
	}

	public void setInvoiceCrud(Invoice invoiceCrud) {
		this.invoiceCrud = invoiceCrud;
	}

	public Invoice getInvoiceSelect() {
		return invoiceSelect;
	}

	public void setInvoiceSelect(Invoice invoiceSelect) {
		this.invoiceSelect = invoiceSelect;
	}

	public List<Invoice> getListInvoice() {
		return listInvoice;
	}

	public void setListInvoice(List<Invoice> listInvoice) {
		this.listInvoice = listInvoice;
	}
	public WrapInvoiceDetailReqInfo getWrapInvoiceDetailCrud() {
		return wrapInvoiceDetailCrud;
	}
	public void setWrapInvoiceDetailCrud(WrapInvoiceDetailReqInfo wrapInvoiceDetailCrud) {
		this.wrapInvoiceDetailCrud = wrapInvoiceDetailCrud;
	}
	public WrapInvoiceDetailReqInfo getWrapInvoiceDetailSelect() {
		return wrapInvoiceDetailSelect;
	}
	public void setWrapInvoiceDetailSelect(WrapInvoiceDetailReqInfo wrapInvoiceDetailSelect) {
		this.wrapInvoiceDetailSelect = wrapInvoiceDetailSelect;
	}
	public List<WrapInvoiceDetailReqInfo> getListWrapInvoiceDetail() {
		return listWrapInvoiceDetail;
	}
	public void setListWrapInvoiceDetail(List<WrapInvoiceDetailReqInfo> listWrapInvoiceDetail) {
		this.listWrapInvoiceDetail = listWrapInvoiceDetail;
	}
	public List<PaymentMethod> getListPaymentMethod() {
		return listPaymentMethod;
	}

	public void setListPaymentMethod(List<PaymentMethod> listPaymentMethod) {
		this.listPaymentMethod = listPaymentMethod;
	}

	public List<IECategories> getListIECategories() {
		return listIECategories;
	}

	public void setListIECategories(List<IECategories> listIECategories) {
		this.listIECategories = listIECategories;
	}
	public List<Warehouse> getListWarehouse() {
		return listWarehouse;
	}
	public void setListWarehouse(List<Warehouse> listWarehouse) {
		this.listWarehouse = listWarehouse;
	}
	public FormatHandler getFormatHandler() {
		return formatHandler;
	}
	public void setFormatHandler(FormatHandler formatHandler) {
		this.formatHandler = formatHandler;
	}
	public Date getFromDateFCFilter() {
		return fromDateFCFilter;
	}
	public void setFromDateFCFilter(Date fromDateFCFilter) {
		this.fromDateFCFilter = fromDateFCFilter;
	}
	public Date getToDateFCFilter() {
		return toDateFCFilter;
	}
	public void setToDateFCFilter(Date toDateFCFilter) {
		this.toDateFCFilter = toDateFCFilter;
	}
	public CustomerTypes getCustomerTypesFCFilter() {
		return customerTypesFCFilter;
	}
	public void setCustomerTypesFCFilter(CustomerTypes customerTypesFCFilter) {
		this.customerTypesFCFilter = customerTypesFCFilter;
	}
	public Customer getCustomerFcFilter() {
		return customerFcFilter;
	}
	public void setCustomerFcFilter(Customer customerFcFilter) {
		this.customerFcFilter = customerFcFilter;
	}
	public String getContractNoFCFilter() {
		return contractNoFCFilter;
	}
	public void setContractNoFCFilter(String contractNoFCFilter) {
		this.contractNoFCFilter = contractNoFCFilter;
	}
	public List<FreightContract> getListFreightContractSelect() {
		return listFreightContractSelect;
	}
	public void setListFreightContractSelect(List<FreightContract> listFreightContractSelect) {
		this.listFreightContractSelect = listFreightContractSelect;
	}
	public int getPageSizeHDVC() {
		return pageSizeHDVC;
	}
	public void setPageSizeHDVC(int pageSizeHDVC) {
		this.pageSizeHDVC = pageSizeHDVC;
	}
	public NavigationInfo getNavigationInfoHDVC() {
		return navigationInfoHDVC;
	}
	public void setNavigationInfoHDVC(NavigationInfo navigationInfoHDVC) {
		this.navigationInfoHDVC = navigationInfoHDVC;
	}
	public List<Integer> getListRowPerPageHDVC() {
		return listRowPerPageHDVC;
	}
	public void setListRowPerPageHDVC(List<Integer> listRowPerPageHDVC) {
		this.listRowPerPageHDVC = listRowPerPageHDVC;
	}
	public String getKeySearchCustomer() {
		return keySearchCustomer;
	}
	public void setKeySearchCustomer(String keySearchCustomer) {
		this.keySearchCustomer = keySearchCustomer;
	}
	public List<Customer> getListCustomerSelect() {
		return listCustomerSelect;
	}
	public void setListCustomerSelect(List<Customer> listCustomerSelect) {
		this.listCustomerSelect = listCustomerSelect;
	}
	public List<Stocker> getListStocker() {
		return listStocker;
	}
	public void setListStocker(List<Stocker> listStocker) {
		this.listStocker = listStocker;
	}
	public List<Stevedore> getListStevedores() {
		return listStevedores;
	}
	public void setListStevedores(List<Stevedore> listStevedores) {
		this.listStevedores = listStevedores;
	}
	public List<HarborCategory> getListHarborCategory() {
		return listHarborCategory;
	}
	public void setListHarborCategory(List<HarborCategory> listHarborCategory) {
		this.listHarborCategory = listHarborCategory;
	}
	public List<FormUpGoods> getListFormUpGoods() {
		return listFormUpGoods;
	}
	public void setListFormUpGoods(List<FormUpGoods> listFormUpGoods) {
		this.listFormUpGoods = listFormUpGoods;
	}
	public Date getFromDateSearch() {
		return fromDateSearch;
	}
	public void setFromDateSearch(Date fromDateSearch) {
		this.fromDateSearch = fromDateSearch;
	}
	public Date getToDateSearch() {
		return toDateSearch;
	}
	public void setToDateSearch(Date toDateSearch) {
		this.toDateSearch = toDateSearch;
	}
	public Customer getCustomerSearch() {
		return customerSearch;
	}
	public void setCustomerSearch(Customer customerSearch) {
		this.customerSearch = customerSearch;
	}
	public String getInvoiceCodeSearch() {
		return invoiceCodeSearch;
	}
	public void setInvoiceCodeSearch(String invoiceCodeSearch) {
		this.invoiceCodeSearch = invoiceCodeSearch;
	}
	public String getVoucherCodeSearch() {
		return voucherCodeSearch;
	}
	public void setVoucherCodeSearch(String voucherCodeSearch) {
		this.voucherCodeSearch = voucherCodeSearch;
	}
	public IECategories getIeCategoriesSearch() {
		return ieCategoriesSearch;
	}
	public void setIeCategoriesSearch(IECategories ieCategoriesSearch) {
		this.ieCategoriesSearch = ieCategoriesSearch;
	}
	public String getPoNoSearch() {
		return poNoSearch;
	}
	public void setPoNoSearch(String poNoSearch) {
		this.poNoSearch = poNoSearch;
	}
	public String getOrderVoucherSearch() {
		return orderVoucherSearch;
	}
	public void setOrderVoucherSearch(String orderVoucherSearch) {
		this.orderVoucherSearch = orderVoucherSearch;
	}
	public String getOrderCodeSearch() {
		return orderCodeSearch;
	}
	public void setOrderCodeSearch(String orderCodeSearch) {
		this.orderCodeSearch = orderCodeSearch;
	}
	public int getPaymentSearch() {
		return paymentSearch;
	}
	public void setPaymentSearch(int paymentSearch) {
		this.paymentSearch = paymentSearch;
	}
	public int getStatusSearch() {
		return statusSearch;
	}
	public void setStatusSearch(int statusSearch) {
		this.statusSearch = statusSearch;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public NavigationInfo getNavigationInfo() {
		return navigationInfo;
	}
	public void setNavigationInfo(NavigationInfo navigationInfo) {
		this.navigationInfo = navigationInfo;
	}
	public List<Integer> getListRowPerPage() {
		return listRowPerPage;
	}
	public void setListRowPerPage(List<Integer> listRowPerPage) {
		this.listRowPerPage = listRowPerPage;
	}
	public List<WrapInvoiceDetailReqInfo> getListWrapInvoiceDetailFilter() {
		return listWrapInvoiceDetailFilter;
	}
	public void setListWrapInvoiceDetailFilter(List<WrapInvoiceDetailReqInfo> listWrapInvoiceDetailFilter) {
		this.listWrapInvoiceDetailFilter = listWrapInvoiceDetailFilter;
	}
	public WrapInvoiceDetailReqInfo getWrapInvoiceDetailPick() {
		return wrapInvoiceDetailPick;
	}
	public void setWrapInvoiceDetailPick(WrapInvoiceDetailReqInfo wrapInvoiceDetailPick) {
		this.wrapInvoiceDetailPick = wrapInvoiceDetailPick;
	}
	public List<WrapExportDataReqInfo> getListWrapExportData() {
		return listWrapExportData;
	}
	public void setListWrapExportData(List<WrapExportDataReqInfo> listWrapExportData) {
		this.listWrapExportData = listWrapExportData;
	}
}
