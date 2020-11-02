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
import lixco.com.entity.Car;
import lixco.com.entity.Carrier;
import lixco.com.entity.Customer;
import lixco.com.entity.CustomerTypes;
import lixco.com.entity.ExportBatchPos;
import lixco.com.entity.FormUpGoods;
import lixco.com.entity.FreightContract;
import lixco.com.entity.HarborCategory;
import lixco.com.entity.IECategories;
import lixco.com.entity.InvoiceDetailPos;
import lixco.com.entity.InvoicePos;
import lixco.com.entity.PaymentMethod;
import lixco.com.entity.Pos;
import lixco.com.entity.PricingProgram;
import lixco.com.entity.PricingProgramDetail;
import lixco.com.entity.Product;
import lixco.com.entity.ProductPos;
import lixco.com.entity.Stevedore;
import lixco.com.entity.Stocker;
import lixco.com.entity.Warehouse;
import lixco.com.interfaces.IBatchPosService;
import lixco.com.interfaces.ICarService;
import lixco.com.interfaces.ICarrierService;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.ICustomerTypesService;
import lixco.com.interfaces.IFormUpGoodsService;
import lixco.com.interfaces.IHarborCategoryService;
import lixco.com.interfaces.IIECategoriesService;
import lixco.com.interfaces.IInvoiceDetailPosService;
import lixco.com.interfaces.IInvoicePosService;
import lixco.com.interfaces.IPaymentMethodService;
import lixco.com.interfaces.IPricingProgramDetailService;
import lixco.com.interfaces.IProcessLogicInvoicePosService;
import lixco.com.interfaces.IProductPosService;
import lixco.com.interfaces.IProductService;
import lixco.com.interfaces.IPromotionalPricingService;
import lixco.com.interfaces.IStevedoreService;
import lixco.com.interfaces.IStockerService;
import lixco.com.interfaces.IWarehouseService;
import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.PricingProgramDetailReqInfo;
import lixco.com.reqInfo.ProductPosReqInfo;
import lixco.com.reqInfo.PromotionalPricingReqInfo;
import lixco.com.reqInfo.WrapDataInvoiceDetailPos;
import lixco.com.reqInfo.WrapExportDataPosReqInfo;
import lixco.com.reqInfo.WrapInvoiceDetailPosReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class InvoicePosBean extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IProcessLogicInvoicePosService processLogicInvoicePosService;
	@Inject
	private IProductService productService;
	@Inject
	private ICustomerService customerService;
	@Inject
	private ICustomerTypesService customerTypesService;
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
	private IInvoicePosService invoicePosService;
	@Inject
	private IInvoiceDetailPosService invoiceDetailPosService;
	@Inject
	private IBatchPosService batchPosService;
	@Inject
	private IProductPosService productPosSerive;
	@Inject
	private IPromotionalPricingService promotionalPricingService;
	@Inject
	private IPricingProgramDetailService pricingProgramDetailService;
	private InvoicePos invoicePosCrud;
	private InvoicePos invoicePosSelect;
	private List<InvoicePos> listInvoicePos;
	private WrapInvoiceDetailPosReqInfo wrapInvoiceDetailPosCrud;
	private WrapInvoiceDetailPosReqInfo wrapInvoiceDetailPosSelect;
	private List<WrapInvoiceDetailPosReqInfo> listWrapInvoiceDetailPosReqInfo;
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
	/*Xuất lo hàng*/
	private List<WrapExportDataPosReqInfo> listWrapExportDataPosCrud;
	private WrapInvoiceDetailPosReqInfo wrapInvoiceDetailPosPick;
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
			logger.error("InvoicePosBean.initItem:"+e.getMessage(),e);
		}
		
	}
	public void search(){
		try{
			/*{ invoice_info:{from_date:'',to_date:'',customer_id:0,invoice_code:'',voucher_code:'',ie_categories_id:0,po_no:'',order_code:'',order_voucher:'',payment:-1,status:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listInvoicePos=new ArrayList<>();
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
			invoicePosService.search(JsonParserUtil.getGson().toJson(json), page, listInvoicePos);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		}catch(Exception e){
			logger.error("InvoicePosBean.search:"+e.getMessage(),e);
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
			logger.error("InvoicePosBean.initRowPerPage:" + e.getMessage(), e);
		}
	}
	public void paginatorChange(int currentPage) {
		try {
			/*{ invoice_info:{from_date:'',to_date:'',customer_id:0,invoice_code:'',voucher_code:'',ie_categories_id:0,po_no:'',order_code:'',order_voucher:'',payment:-1,status:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listInvoicePos = new ArrayList<>();
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
			invoicePosService.search(JsonParserUtil.getGson().toJson(json), page, listInvoicePos);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("InvoicePosBean.paginatorChange:" + e.getMessage(), e);
		}
	}
	public void showDialogBatchPos(WrapInvoiceDetailPosReqInfo detail){
		PrimeFaces current=PrimeFaces.current();
		try{
			wrapInvoiceDetailPosPick=detail;
			InvoiceDetailPos invoiceDetailPos=detail.getInvoice_detail_pos();
			listWrapExportDataPosCrud=new ArrayList<>();
			List<ProductPos> listPP=new ArrayList<>();
			List<ExportBatchPos> listEBP=new ArrayList<>();
			//lấy danh sách lô hàng đã xuất và chưa xuất(còn tồn) cho chi tiết này 
			productPosSerive.exportPosInvoiceDetail(invoiceDetailPos.getProduct().getId(), invoiceDetailPos.getId(), listEBP, listPP);
			for(ExportBatchPos ex:listEBP){
				ex.setSelect(true);
			}
			for(ProductPos p:listPP){
				ExportBatchPos exportBatchPosItem=new ExportBatchPos();
				exportBatchPosItem.setProduct_pos(p);
				listEBP.add(exportBatchPosItem);
			}
			Collections.sort(listEBP, new Comparator<ExportBatchPos>() {

				@Override
				public int compare(final ExportBatchPos record1, final ExportBatchPos record2) {
					int c;
					ProductPos productPos1=record1.getProduct_pos();
					Pos pos1=productPos1.getPos();
					ProductPos productPos2=record2.getProduct_pos();
					Pos pos2=productPos2.getPos();
				    c = productPos1.getBatch_pos().getManufacture_date().compareTo(productPos2.getBatch_pos().getManufacture_date());
				    if (c != 0){
				    	return c;
				    }
				    c = pos1.getWarehouse().getName().compareTo(pos2.getWarehouse().getName());
				    if (c != 0){
				    	return c;
				    }
				    c = Integer.compare(pos1.getRow_stack(),pos2.getRow_stack());
				    if(c!=0){
				    	return c;
				    }
				    c = Integer.compare(pos1.getFloorb(),pos2.getFloorb());
				    if(c!=0){
				    	return c;
				    }
				    c = pos1.getPos_code().compareTo(pos2.getPos_code());
				    return c;
				 }

			});
			//prepare list wrap
			for(ExportBatchPos ex:listEBP){
				WrapExportDataPosReqInfo w=new WrapExportDataPosReqInfo();
				w.setExport_batch_pos(ex);
				w.setQuantity_export_box(ex.getQuantity_export_box());
				w.setSelect(ex.isSelect());
				listWrapExportDataPosCrud.add(w);
			
			}
			current.executeScript("PF('dlgexport').show();");
		}catch(Exception e){
			logger.error("InvoicePosBean.showDialogBatchPos:" + e.getMessage(), e);
		}
	}
	public void selectExportBatchPos(WrapExportDataPosReqInfo t){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			ProductPos p=t.getExport_batch_pos().getProduct_pos();
			InvoiceDetailPos invoiceDetailPosPick=wrapInvoiceDetailPosPick.getInvoice_detail_pos();
			if(t.isSelect()){ 
				//load lại productPos hiện tại
				ProductPosReqInfo rp=new ProductPosReqInfo();
				productPosSerive.selectById(p.getId(), rp);
				p=rp.getProduct_pos();
				t.getExport_batch_pos().setProduct_pos(p);
				//số lượng thùng tồn đặt tại vị trí đó.
				double soBoxTon=BigDecimal.valueOf(p.getQuantity_import())
						.subtract(BigDecimal.valueOf(p.getQuantity_export())).doubleValue();
				//số lượng yêu cầu và số lượng thực xuất chi tiết phiếu xuất
				double quantity=invoiceDetailPosPick.getQuantity();
				double quantityReal=invoiceDetailPosPick.getReal_quantity();
				//số lượng yêu cầu còn lại chưa xuất
				double quantityEx=BigDecimal.valueOf(quantity).subtract(BigDecimal.valueOf(quantityReal)).doubleValue();
				if(quantityEx==0)
				{
					//đã đủ số lượng.
					t.setSelect(false);
					notify.warning("Đã xuất đủ số lượng!");
					return;
				}
				double soluongCal=BigDecimal.valueOf(quantityEx).subtract(BigDecimal.valueOf(soBoxTon)).doubleValue();
				if(soluongCal>=0){
					t.setQuantity_export_box(soBoxTon);
					//tính toán số pallet xuất bằng số pallet hiện có.
					quantityReal=BigDecimal.valueOf(quantityReal).add(BigDecimal.valueOf(soBoxTon)).doubleValue();
				}else{
					t.setQuantity_export_box(quantityEx);
					//tính toán số pallet sử dụng
//					Product product=invoiceDetailPosPick.getProduct();//
//					double sothungpallet=product.getBox_quantity();
					quantityReal=BigDecimal.valueOf(quantityReal).add(BigDecimal.valueOf(quantityEx)).doubleValue();
				}
				invoiceDetailPosPick.setReal_quantity(quantityReal);
			}else{
				double quantityExBox=t.getQuantity_export_box();
				double quantityReal=BigDecimal.valueOf(invoiceDetailPosPick.getReal_quantity()).subtract(BigDecimal.valueOf(quantityExBox)).doubleValue();
				invoiceDetailPosPick.setReal_quantity(quantityReal);
				t.setQuantity_export_box(0);
			}
		}catch (Exception e) {
			logger.error("InvoicePosBean.selectProductPosExport:" + e.getMessage(), e);
		}
		PrimeFaces.current().ajax().update("menuformid:tabview1:tablect:"+listWrapInvoiceDetailPosReqInfo.indexOf(wrapInvoiceDetailPosPick)+":tableInner");
	}
	public void changeQuantityExportBatch(WrapExportDataPosReqInfo item){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			//cập nhật lại số lượng thực xuất chi tiết phiếu nhập
			if(item.isSelect()){
				//tính toán lại số lượng pallet xuất
				ProductPosReqInfo rp=new ProductPosReqInfo();
				productPosSerive.selectById(item.getExport_batch_pos().getProduct_pos().getId(),rp);
				ProductPos p=rp.getProduct_pos();
				item.getExport_batch_pos().setProduct_pos(p);
				InvoiceDetailPos invoiceDetailPosPick=wrapInvoiceDetailPosPick.getInvoice_detail_pos();
				//số lượng chỉnh sửa
				double quantityBoxEdit=item.getQuantity_export_box();
				//số lượng tồn tại vị trí
				double quantityBoxTonPos=BigDecimal.valueOf(p.getQuantity_import()).subtract(BigDecimal.valueOf(p.getQuantity_export())).doubleValue();
				double sum=0;
				for(WrapExportDataPosReqInfo ex:listWrapExportDataPosCrud){
					if(ex.isSelect()){
					   sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(ex.getQuantity_export_box())).doubleValue();
					}
				}
				if(sum >invoiceDetailPosPick.getQuantity()){
					//trả lại trạng thái trước.
					item.setQuantity_export_box(item.getExport_batch_pos().getQuantity_export_box());
					notify.warning("Số lượng pallet xuất lớn hơn số lượng pallet tồn.");
					sum=0;
					for(WrapExportDataPosReqInfo ex:listWrapExportDataPosCrud){
						if(ex.isSelect()){
						   sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(ex.getQuantity_export_box())).doubleValue();
						}
					}
					invoiceDetailPosPick.setReal_quantity(sum);
					return;
				}
				if(quantityBoxEdit>quantityBoxTonPos){
					//trả lại trạng thái trước.
					item.setQuantity_export_box(item.getExport_batch_pos().getQuantity_export_box());
					notify.warning("Số lượng thùng xuất lớn hơn số lượng thùng tồn.");
					return;
				}
				sum=0;
				for(WrapExportDataPosReqInfo ex:listWrapExportDataPosCrud){
					if(ex.isSelect()){
					   sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(ex.getQuantity_export_box())).doubleValue();
					}
				}
				invoiceDetailPosPick.setReal_quantity(sum);
			}
		}catch (Exception e) {
			logger.error("InvoicePosBean.changeQuantityExportBatch:" + e.getMessage(), e);
		}
	}
	public void createdNew(){
		try{
			invoicePosCrud=new InvoicePos();
			//tạo mã đơn hàng
			invoicePosCrud.setInvoice_date(ToolTimeCustomer.getFirstDateOfDay(new  Date()));
			invoicePosCrud.setCreated_by(account.getMember().getName());
			listWrapInvoiceDetailPosReqInfo=new ArrayList<>();
		}catch(Exception e){
			logger.error("InvoicePosBean.createNew:" + e.getMessage(), e);
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
			logger.error("InvoicePosBean.showDialogPickHDVC:" + e.getMessage(), e);
		}
	}
	public void showAddInvoiceDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
            if(invoicePosCrud!=null && invoicePosCrud.getId()!=0){
            	//init wrap chi tiết phiếu nhập
            	wrapInvoiceDetailPosCrud=new WrapInvoiceDetailPosReqInfo();
            	wrapInvoiceDetailPosCrud.setList_export_batch_pos(new ArrayList<>());
            	InvoiceDetailPos detail= new InvoiceDetailPos();
            	detail.setInvoice_pos(invoicePosCrud);
            	wrapInvoiceDetailPosCrud.setInvoice_detail_pos(detail);
            	current.executeScript("PF('idlg1').show();");
            }
		}catch (Exception e) {
			logger.error("InvoicePosBean.showAddInvoiceDetail:" + e.getMessage(), e);
		}
	}
	public void showEditInvoiceDetail(){
		PrimeFaces current = PrimeFaces.current();
		try {
			if (wrapInvoiceDetailPosSelect != null) {
					wrapInvoiceDetailPosCrud = wrapInvoiceDetailPosSelect.clone();
					wrapInvoiceDetailPosCrud.getInvoice_detail_pos().setInvoice_pos(invoicePosCrud);
					current.executeScript("PF('idlg1').show();");
			}
		} catch (Exception e) {
			logger.error("InvoicePosBean.showEditInvoiceDetail:" + e.getMessage(), e);
		}
	}
	public void showEditInvoice(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(invoicePosSelect !=null){
				invoicePosCrud=invoicePosSelect.clone();
				listWrapInvoiceDetailPosReqInfo=new ArrayList<>();
				//show thông tin chi tiết hóa đơn
				List<InvoiceDetailPos> listTemp=new ArrayList<>();
				invoiceDetailPosService.selectByOrder(invoicePosCrud.getId(), listTemp);
				for(InvoiceDetailPos dt:listTemp){
					List<ExportBatchPos> list=new ArrayList<>();
					invoicePosService.getListExportBatchPos(dt.getId(), list);
					WrapInvoiceDetailPosReqInfo w=new WrapInvoiceDetailPosReqInfo(dt, list);
					listWrapInvoiceDetailPosReqInfo.add(w);
				}
				//tính tổng tiền hóa đơn
				sumInvoice();
			}
		}catch(Exception e){
			logger.error("InvoicePosBean.showEditInvoice:" + e.getMessage(), e);
		}
		current.executeScript("utility.expandTable('.ui-row-toggler');");
	}
	private void initRowPerPageHDVC() {
		try {
			listRowPerPageHDVC = new ArrayList<Integer>();
			listRowPerPageHDVC.add(90);
			listRowPerPageHDVC.add(180);
			listRowPerPageHDVC.add(240);
			pageSizeHDVC = listRowPerPageHDVC.get(0);
		} catch (Exception e) {
			logger.error("InvoicePosBean.initRowPerPage:" + e.getMessage(), e);
		}
	}
	
	public List<Product> completeProduct(String text){
		try{
			List<Product> list=new ArrayList<>();
			productService.complete2(formatHandler.converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("InvoicePosBean.completeProduct:"+e.getMessage(),e);
		}
		return null;
	}
	public List<Car> completeCar(String text){
		try{
			if(text !=null && !"".equals(text)){
				List<Car> list=new ArrayList<>();
				carService.complete(formatHandler.converViToEn(text), list);
				return list;
			}
		}catch(Exception e){
			logger.error("InvoicePosBean.completeCar:" + e.getMessage(), e);
		}
		return null;
	}
	public List<Customer> completeCustomer(String text){
		try{
			List<Customer> list=new ArrayList<>();
			customerService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("InvoicePosBean.completeCustomer:"+e.getMessage(),e);
		}
		return null;
	}
	public List<Carrier> completeCarrier(String text){
		try{
			List<Carrier> list=new ArrayList<>();
			carrierService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch (Exception e) {
			logger.error("InvoicePosBean.completeCarrier:"+e.getMessage(),e);
		}
		return null;
	}
	public double sumInvoice(){
		try{
			if(listWrapInvoiceDetailPosReqInfo !=null && listWrapInvoiceDetailPosReqInfo.size()>0){
				double totalevent = listWrapInvoiceDetailPosReqInfo.stream().mapToDouble(f -> f.getInvoice_detail_pos().getQuantity()*f.getInvoice_detail_pos().getUnit_price()).sum();
				return totalevent;
			}
		}catch(Exception e){
			logger.error("InvoicePosBean.sumInvoice:" + e.getMessage(), e);
		}
		return 0;
	}
	public void deleteInvoiceDetail(){
		try{
			StringBuilder messages=new StringBuilder();
			processLogicInvoicePosService.deleteInvoiceDetailPosMaster(0, messages);
		}catch(Exception e){
			logger.error("InvoicePosBean.deleteInvoiceDetail:" + e.getMessage(), e);
		}
	}
	public void changeProductInvoiceDetail(){
		try{
			if(wrapInvoiceDetailPosCrud !=null && invoicePosCrud !=null && invoicePosCrud.getId()!=0){
				InvoiceDetailPos detailPos=wrapInvoiceDetailPosCrud.getInvoice_detail_pos();
				Product product=detailPos.getProduct();
				//chương trình đơn giá
				PricingProgram pricingProgram= invoicePosCrud.getPricing_program();
				//lấy đơn giá sản phẩm.
				if(invoicePosCrud.isIpromotion()){
					//nạp đơn giá khuyến mãi.
					// tim nạp đơn giá sản phẩm khuyến mãi
					PromotionalPricingReqInfo ppr = new PromotionalPricingReqInfo();
					JsonObject json = new JsonObject();
					json.addProperty("order_date", ToolTimeCustomer.convertDateToString(
							invoicePosCrud.getOrder_lix_pos().getOrder_date(), "dd/MM/yyyy"));
					json.addProperty("product_id", product.getId());
					promotionalPricingService.findSettingPromotionalPricing(JsonParserUtil.getGson().toJson(json), ppr);
//					pod.setQuantity(t.getQuantity() * ppd.getPromotion_quantity() / ppd.getBox_quatity());
					if (ppr.getPromotional_pricing() != null) {
						detailPos.setUnit_price(ppr.getPromotional_pricing().getUnit_price());
					}
				}else if(pricingProgram !=null){
					//nạp đơn giá trong chương trình đơn giá.
					  //cập nhật đơn giá sản phẩm
					  PricingProgramDetailReqInfo dt= new PricingProgramDetailReqInfo();
					  pricingProgramDetailService.findSettingPricingChild(pricingProgram.getId(), product.getId(), dt);
					  if(dt.getPricing_program_detail()==null){
						  pricingProgramDetailService.findSettingPricing(pricingProgram.getId(),product.getId() , dt);
					  }
					  PricingProgramDetail ppd=dt.getPricing_program_detail();
					  detailPos.setUnit_price(ppd==null ? 0: ppd.getUnit_price());
				}
			}
		}catch (Exception e) {
			logger.error("InvoicePosBean.changeProductInvoiceDetail:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdateInvoiceDetail(){
		try{
			if(invoicePosCrud !=null && invoicePosCrud.getId() !=0 && wrapInvoiceDetailPosCrud !=null){
				//kiểm tra đầy đủ thông tin
				InvoiceDetailPos detail=wrapInvoiceDetailPosCrud.getInvoice_detail_pos();
				if(detail !=null && detail.getProduct() !=null && detail.getQuantity() !=0){
					if(detail.getId()==0){
						if(allowSave(new Date())){
							
						}else{
							
						}
					}else{
						if(allowUpdate(new Date())){
							
						}else{
							
						}
					}
				}else{
					
				}
			}
		}catch (Exception e) {
			logger.error("InvoicePosBean.saveOrUpdateInvoiceDetail:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdateExportBatchPos(){
		PrimeFaces current=PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(listWrapExportDataPosCrud !=null && listWrapExportDataPosCrud.size()>0){
				InvoiceDetailPos invoiceDetailPosPick= wrapInvoiceDetailPosPick.getInvoice_detail_pos();
				//prepare wrap  để lưu/ cập nhật dữ liệu.
				WrapDataInvoiceDetailPos  data=new WrapDataInvoiceDetailPos(invoiceDetailPosPick, new ArrayList<>());
				data.setEmployee_name(account.getMember().getName());
				for(WrapExportDataPosReqInfo p:listWrapExportDataPosCrud){
					if(p.isSelect()){
						data.getList_wrap_export_data_pos().add(p);
					}
				}
				//lưu lô hàng 
				Message message=new Message();
				int code=processLogicInvoicePosService.saveListWrapExportDataPos(data, message);
				if(code>=0){
				    //load lại dữ liệu cho lô hàng đó.
					List<ExportBatchPos> list=new ArrayList<>();
					invoicePosService.getListExportBatchPos(invoiceDetailPosPick.getId(), list);
					wrapInvoiceDetailPosPick.setList_export_batch_pos(list);
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
		PrimeFaces.current().ajax().update("menuformid:tabview1:tablect:"+listWrapInvoiceDetailPosReqInfo.indexOf(wrapInvoiceDetailPosPick)+":tableInner");
		PrimeFaces.current().ajax().update("menuformid:tabview1:tablect:"+listWrapInvoiceDetailPosReqInfo.indexOf(wrapInvoiceDetailPosPick)+":realExport");
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
			logger.error("InvoicePosBean.saveOrUpdateInvoice:" + e.getMessage(), e);
		}
	}
	public void exportPDF(){
		try{
			
		}catch (Exception e) {
			logger.error("InvoicePosBean.exportPDF:" + e.getMessage(), e);
		}
	}
	public void destroyInvoice(){
		try{
			
		}catch (Exception e) {
			logger.error("InvoicePosBean.destroyInvoice:" + e.getMessage(), e);
		}
	}
	@Override
	protected Logger getLogger() {
		return logger;
	}
	public InvoicePos getInvoicePosCrud() {
		return invoicePosCrud;
	}
	public void setInvoicePosCrud(InvoicePos invoicePosCrud) {
		this.invoicePosCrud = invoicePosCrud;
	}
	public InvoicePos getInvoicePosSelect() {
		return invoicePosSelect;
	}
	public void setInvoicePosSelect(InvoicePos invoicePosSelect) {
		this.invoicePosSelect = invoicePosSelect;
	}
	public List<InvoicePos> getListInvoicePos() {
		return listInvoicePos;
	}
	public void setListInvoicePos(List<InvoicePos> listInvoicePos) {
		this.listInvoicePos = listInvoicePos;
	}
	public WrapInvoiceDetailPosReqInfo getWrapInvoiceDetailPosCrud() {
		return wrapInvoiceDetailPosCrud;
	}
	public void setWrapInvoiceDetailPosCrud(WrapInvoiceDetailPosReqInfo wrapInvoiceDetailPosCrud) {
		this.wrapInvoiceDetailPosCrud = wrapInvoiceDetailPosCrud;
	}
	public WrapInvoiceDetailPosReqInfo getWrapInvoiceDetailPosSelect() {
		return wrapInvoiceDetailPosSelect;
	}
	public void setWrapInvoiceDetailPosSelect(WrapInvoiceDetailPosReqInfo wrapInvoiceDetailPosSelect) {
		this.wrapInvoiceDetailPosSelect = wrapInvoiceDetailPosSelect;
	}
	public List<WrapInvoiceDetailPosReqInfo> getListWrapInvoiceDetailPosReqInfo() {
		return listWrapInvoiceDetailPosReqInfo;
	}
	public void setListWrapInvoiceDetailPosReqInfo(List<WrapInvoiceDetailPosReqInfo> listWrapInvoiceDetailPosReqInfo) {
		this.listWrapInvoiceDetailPosReqInfo = listWrapInvoiceDetailPosReqInfo;
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
	public List<WrapExportDataPosReqInfo> getListWrapExportDataPosCrud() {
		return listWrapExportDataPosCrud;
	}
	public void setListWrapExportDataPosCrud(List<WrapExportDataPosReqInfo> listWrapExportDataPosCrud) {
		this.listWrapExportDataPosCrud = listWrapExportDataPosCrud;
	}
	public WrapInvoiceDetailPosReqInfo getWrapInvoiceDetailPosPick() {
		return wrapInvoiceDetailPosPick;
	}
	public void setWrapInvoiceDetailPosPick(WrapInvoiceDetailPosReqInfo wrapInvoiceDetailPosPick) {
		this.wrapInvoiceDetailPosPick = wrapInvoiceDetailPosPick;
	}
}
