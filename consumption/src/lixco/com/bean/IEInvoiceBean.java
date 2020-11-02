package lixco.com.bean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;

import com.google.gson.JsonObject;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;

import lixco.com.common.FormatHandler;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Car;
import lixco.com.entity.Contract;
import lixco.com.entity.ContractDetail;
import lixco.com.entity.Currency;
import lixco.com.entity.Customer;
import lixco.com.entity.FormUpGoods;
import lixco.com.entity.HarborCategory;
import lixco.com.entity.IECategories;
import lixco.com.entity.IEInvoice;
import lixco.com.entity.IEInvoiceDetail;
import lixco.com.entity.PaymentMethod;
import lixco.com.entity.Product;
import lixco.com.entity.Stocker;
import lixco.com.entity.Warehouse;
import lixco.com.interfaces.IBatchService;
import lixco.com.interfaces.ICarService;
import lixco.com.interfaces.IContractService;
import lixco.com.interfaces.ICurrencyService;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.IFormUpGoodsService;
import lixco.com.interfaces.IHarborCategoryService;
import lixco.com.interfaces.IIECategoriesService;
import lixco.com.interfaces.IIEInvoiceService;
import lixco.com.interfaces.IInvoiceService;
import lixco.com.interfaces.IPaymentMethodService;
import lixco.com.interfaces.IProcessLogicInvoiceService;
import lixco.com.interfaces.IProductService;
import lixco.com.interfaces.IStockerService;
import lixco.com.interfaces.IWarehouseService;
import lixco.com.reportInfo.IECommercialInvoiceReportInfo;
import lixco.com.reportInfo.IEExportReport;
import lixco.com.reportInfo.IEInvoiceCustomerBillNoDetailReport;
import lixco.com.reportInfo.IEInvoiceHarborBillNoDetailReport;
import lixco.com.reportInfo.IEInvoiceListByContNoReport;
import lixco.com.reportInfo.IEInvoiceListByTaxValueZeroReport;
import lixco.com.reportInfo.IEInvoiceOrderListByProductCodeReport;
import lixco.com.reportInfo.IEInvoiceOrderListByVoucherReport;
import lixco.com.reportInfo.IEInvoiceReport;
import lixco.com.reportInfo.IEOrderFormReport;
import lixco.com.reportInfo.IEPackingListReport;
import lixco.com.reportInfo.IEProformaInvocieReportInfo;
import lixco.com.reportInfo.IEVanningReport;
import lixco.com.reqInfo.BatchReqInfo;
import lixco.com.reqInfo.HarborCategoryReqInfo;
import lixco.com.reqInfo.IEInvoiceDetailReqInfo;
import lixco.com.reqInfo.IEInvoiceReqInfo;
import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.ReportTypeIEInVoice;
import lixco.com.reqInfo.WrapIEInvocieReqInfo;
import net.sf.jasperreports.data.DataSourceCollection;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.ConvertNumberToText;
import trong.lixco.com.util.EnglishNumberToWords;
import trong.lixco.com.util.Notify;
import trong.lixco.com.util.NumberWordConverter;
import trong.lixco.com.util.ToolReport;
@Named(value = "iEInvoiceBean")
@ViewScoped
public class IEInvoiceBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	@Inject
	private IIEInvoiceService iEInvoiceService;
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
	private ICurrencyService currencyService;
	@Inject
	private IHarborCategoryService harborCategoryService;
	@Inject
	private IStockerService stockerService;
	@Inject
	private ICarService carService;
	@Inject
	private IContractService contractService;
	@Inject
	private IFormUpGoodsService formUpGoodsService;
	@Inject
	private IProcessLogicInvoiceService processLogicInvoiceService;
	@Inject
	private IInvoiceService invoiceService;
	@Inject
	private IBatchService batchService;
	@Inject
	private Logger logger;
	private List<IECategories> listIECategories;
	private List<PaymentMethod> listPaymentMethod;
	private List<Currency> listCurrency;
	private List<Warehouse> listWarehouse;
	private List<HarborCategory> listHarborCategory;
	private List<Stocker> listStocker;
	private List<FormUpGoods> listFormUpGoods;
	private IEInvoice iEInvoiceCrud;
	private IEInvoice iEInvoiceSelect;
	private List<IEInvoice> listIEInvoice;
	private IEInvoiceDetail iEInvoiceDetailCrud;
	private IEInvoiceDetail iEInvoiceDetailSelect;
	private List<IEInvoiceDetail> listIEInvoiceDetail;
	private double quantityContract;
	private double quantityContractProcessed;
	private double quantityBatchInvCurr;// số lượng lô hàng tồn hiện tại
	private double quantityBatchInvCal;// số lượng lô hàng tồn ước tính.

	private double sumUSD;
	private double sumUSDXK;
	private double sumVND;
	/* search ieinvoice */
	private Date fromDateSearch;
	private Date toDateSearch;
	private Customer customerSearch;
	private String invoiceCodeSearch;
	private String voucherCodeSearch;
	private Product productSearch;
	private IECategories iECategoriesSearch;
	private String contractVoucherCode;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	private Account account;
	private FormatHandler formatHandler;
	/* report */
	private List<ReportTypeIEInVoice> listSelectReport;
	private ReportTypeIEInVoice reportTypeIEInVoiceSelect;
	/*excel*/
	private List<ReportTypeIEInVoice> listSelectExcel;
	private ReportTypeIEInVoice reportTypeExcelSelect;
	/*Out */
	private List<IEInvoiceDetail> listIEInvoiceDetailOut;
	private List<IEInvoiceDetail> listIEInvoiceDetailSelectOut;
	private List<IEInvoice> listIEInvoiceOut;
	/*danh sách report tab danh sách*/
	private List<ReportTypeIEInVoice> listReportPdfOut;
	private ReportTypeIEInVoice reportPdfOutSelect;
	/*danh sách bill no hải quan*/
	private List<ReportTypeIEInVoice> listReportHarborOut;
	private ReportTypeIEInVoice reportHarborOutSelect;
	

	@Override
	protected void initItem() {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			formatHandler = FormatHandler.getInstance();
			listFormUpGoods = new ArrayList<>();
			formUpGoodsService.selectAll(listFormUpGoods);
			listCurrency = new ArrayList<>();
			currencyService.selectAll(listCurrency);
			listIECategories = new ArrayList<IECategories>();
			iECategoriesService.selectAll(listIECategories);
			listPaymentMethod = new ArrayList<>();
			paymentMethodService.selectAll(listPaymentMethod);
			listWarehouse = new ArrayList<>();
			warehouseService.selectAll(listWarehouse);
			listHarborCategory = new ArrayList<>();
			final int IE_HARBORCATEGORY = 1;
			harborCategoryService.search(IE_HARBORCATEGORY, listHarborCategory);
			listStocker = new ArrayList<>();
			stockerService.search(null, 0, listStocker);
			// init contract crud
			iEInvoiceCrud = new IEInvoice();
			iEInvoiceCrud.setInvoice_date(new Date());
			int year = ToolTimeCustomer.getYearCurrent();
			int month = ToolTimeCustomer.getMonthCurrent();
			fromDateSearch = ToolTimeCustomer.getDateMinCustomer(month, year);
			toDateSearch = ToolTimeCustomer.getDateMaxCustomer(month, year);
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			search();
			createNew();
			/* init report */
			listSelectReport = new ArrayList<>();
			listSelectReport.add(new ReportTypeIEInVoice(1, "In hóa đơn"));
			listSelectReport.add(new ReportTypeIEInVoice(2, "Commercial Invoice"));
			listSelectReport.add(new ReportTypeIEInVoice(3, "In phiếu xuất kho"));
			listSelectReport.add(new ReportTypeIEInVoice(4, "Proforma Invoice"));
			listSelectReport.add(new ReportTypeIEInVoice(5, "In đơn hàng"));
			listSelectReport.add(new ReportTypeIEInVoice(6, "Packing List"));
			listSelectReport.add(new ReportTypeIEInVoice(7, "Kiểm tra lệnh hóa đơn"));
			listSelectReport.add(new ReportTypeIEInVoice(8, "Vanning Report"));
			/* init report pdf tab danh sách */
			listReportPdfOut = new ArrayList<>();
			listReportPdfOut.add(new ReportTypeIEInVoice(1, "Commercial Invoice"));
			listReportPdfOut.add(new ReportTypeIEInVoice(2, "Proforma Invoice"));
			listReportPdfOut.add(new ReportTypeIEInVoice(3, "Packing List"));
			listReportPdfOut.add(new ReportTypeIEInVoice(4, "Vanning Report"));
			/*report hải quan và c/o */
			listReportHarborOut=new ArrayList<>();
			listReportHarborOut.add(new ReportTypeIEInVoice(1, "Chi tiết bill no(hải quan)"));
			listReportHarborOut.add(new ReportTypeIEInVoice(2, "Chi tiết bill no(khách hàng)"));
			listReportHarborOut.add(new ReportTypeIEInVoice(3, "C/O from AJ"));
			listReportHarborOut.add(new ReportTypeIEInVoice(4, "Đơn đề nghị cấp C/O mẫu AJ"));
			/*innit excel report*/
			listSelectExcel=new ArrayList<>();
			listSelectExcel.add(new ReportTypeIEInVoice(1, "Bảng kê thứ tự theo số ct"));
			listSelectExcel.add(new ReportTypeIEInVoice(2, "Bảng kê thứ tự theo masp"));
			listSelectExcel.add(new ReportTypeIEInVoice(3, "Bảng kê Cont no"));
			listSelectExcel.add(new ReportTypeIEInVoice(4, "Bảng kê thuế suất 0%"));
			listIEInvoiceOut=new ArrayList<>();
			listIEInvoiceDetailSelectOut=new ArrayList<>();
			
			
		} catch (Exception e) {
			logger.error("IEInvoiceBean.initItem:" + e.getMessage(), e);
		}
	}
	public void processInvoice(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(iEInvoiceCrud !=null && iEInvoiceCrud.getId() !=0){
				WrapIEInvocieReqInfo t=new WrapIEInvocieReqInfo(iEInvoiceCrud.getId(), account.getMember().getName(), account.getMember().getId());
				Message message=new Message();
			    int code=processLogicInvoiceService.createInvoiceByIEInvoice(t, message);
			    if(code==0){
			    	current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Lưu đơn hàng xuất xuất khẩu thành công!','success',2000);");
			    }else{
			    	current.executeScript(
							"swaldesigntimer('Cảnh báo!', '"+message.getUser_message()+"','warning',2000);");
			    }
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Đơn hàng xuất xuất khẩu không tồn tại!','warning',2000);");
			}
		}catch (Exception e) {
			logger.error("IEInvoiceBean.processInvoice:" + e.getMessage(), e);
		}
	}
	public void search() {
		try {
			/*
			 * {ie_invoice:{from_date:'',to_date:'',customer_id:0,invoice_code:'
			 * ',voucher_code:'',product_id:0,ie_categories_id:0,
			 * contract_voucher_code:''},page:{page_index:0,page_size:0}}
			 */
			listIEInvoiceOut=new ArrayList<>();
			listIEInvoiceDetailSelectOut=new ArrayList<>();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listIEInvoice = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("from_date",
					fromDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date",
					toDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch == null ? 0 : customerSearch.getId());
			jsonInfo.addProperty("invoice_code", invoiceCodeSearch);
			jsonInfo.addProperty("product_id", productSearch == null ? 0 : productSearch.getId());
			jsonInfo.addProperty("ie_categories_id", iECategoriesSearch == null ? 0 : iECategoriesSearch.getId());
			jsonInfo.addProperty("contract_voucher_code", contractVoucherCode);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("ie_invoice", jsonInfo);
			json.add("page", jsonPage);
			iEInvoiceService.search(JsonParserUtil.getGson().toJson(json), page, listIEInvoice);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		} catch (Exception e) {
			logger.error("IEInvoiceBean.search:" + e.getMessage(), e);
		}
	}
	public void proccessExportIEInvoiceByHarbor(boolean word){
		try{
			if(reportHarborOutSelect !=null){
				long id=reportHarborOutSelect.getId();
				if(id==1){
					exportReportDetailBillNoHarbor(word);
				}else if(id==2){
					exportReportDetailBillNoCustomer(word);
				}else if(id==3){
					exportReportCOFromD();
				}
			}
		}catch (Exception e) {
			logger.error("IEInvoiceBean.proccessExportIEInvoiceByHarbor:" + e.getMessage(), e);
		}
	}
	private void exportReportCOFromD(){
		PrimeFaces current = PrimeFaces.current();
		try {
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/cofromd.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			String ba = Base64.getEncoder().encodeToString(data);
			current.executeScript("utility.printPDF('" + ba + "')");
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportReportCOFromD:" + e.getMessage(), e);
		}
	}
	private void exportReportDetailBillNoHarbor(boolean word){
		PrimeFaces current=PrimeFaces.current();
		try{
			//kiểm tra danh sách đơn hàng xuất xuất khẩu chọn để gọp in có cùng mã khách hàng hợp đồng và bill no hay không.
			IEInvoice iEInvoiceFirst=null;
			if(listIEInvoiceOut.size()>0 && listIEInvoiceDetailSelectOut.size()>0){
				iEInvoiceFirst=listIEInvoiceOut.get(0);
				String customerCode=iEInvoiceFirst.getCustomer().getCustomer_code();
				Contract contract=iEInvoiceFirst.getContract();
				String contractVoucherCode=contract ==null ? "" :contract.getVoucher_code();
				String billNo=iEInvoiceFirst.getBill_no();
				for(int i=1;i<listIEInvoiceOut.size();i++){
					if(!customerCode.equals(listIEInvoiceOut.get(i).getCustomer().getCustomer_code()) || 
							!contractVoucherCode.equals(listIEInvoiceOut.get(i).getContract().getVoucher_code()) || !billNo.equals((listIEInvoiceOut.get(i).getBill_no()))){
						current.executeScript(
								"swaldesigntimer('Cảnh báo!', 'Danh sách đơn hàng chọn không cùng mã khách hàng, số hợp đồng hoặc bill no !','warning',2000);");
						return;
					}
				}
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
				return;
			}
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/chitietbillnohaiquan.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			List<IEInvoiceHarborBillNoDetailReport> list = new ArrayList<>();
			//lấy danh sách id chi tiết đơn hàng xuất xuất khẩu.
			List<Long> listID=new ArrayList<>();
			for (IEInvoiceDetail p : listIEInvoiceDetailSelectOut) {
				if(p.isCheck()){
					listID.add(p.getId());
				}
			}
			JsonObject json=new JsonObject();
			json.add("list_ie_invoice_detail_id", JsonParserUtil.getGson().toJsonTree(listID));
			iEInvoiceService.selectIEInvoiceHarborBillNoDetailReport(JsonParserUtil.getGson().toJson(json), list);
			if(listID.size()==0){
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
				return;
			}
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, dataSource);
			if(word){
				JRDocxExporter exporter = new JRDocxExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				FacesContext facesContext = FacesContext.getCurrentInstance();
				HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
						.getExternalContext().getResponse();
				httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + "chitietbillnohaiquan"+iEInvoiceCrud.getVoucher_code() + ".docx");
				ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, servletOutputStream);
				exporter.exportReport();
				facesContext.responseComplete();
			}else{
				// slipt 40 ký tự
				byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
				String ba = Base64.getEncoder().encodeToString(data);
				current.executeScript("utility.printPDF('" + ba + "')");
			}
		}catch (Exception e) {
			logger.error("IEInvoiceBean.proccessExportIEInvoiceByHarbor:" + e.getMessage(), e);
		}
	}
	private void exportReportDetailBillNoCustomer(boolean word){
		PrimeFaces current=PrimeFaces.current();
		try{
			//kiểm tra danh sách đơn hàng xuất xuất khẩu chọn để gọp in có cùng mã khách hàng hợp đồng và bill no hay không.
			IEInvoice iEInvoiceFirst=null;
			String customerName=null;
			String address=null;
			String shippingMark=null;
			String harborEnName=null;
			String countryEnName=null;
			String freight=null;
			if(listIEInvoiceOut.size()>0 && listIEInvoiceDetailSelectOut.size()>0){
					iEInvoiceFirst=listIEInvoiceOut.get(0);
					Customer customer=iEInvoiceFirst.getCustomer();
					String customerCode=customer.getCustomer_code();
					customerName=customer.getCustomer_name();
					address=customer.getAddress();
					freight=iEInvoiceFirst.getFreight();
					HarborCategory harborCategory=iEInvoiceFirst.getHarbor_category();
					if (harborCategory != null) {
						HarborCategoryReqInfo hr = new HarborCategoryReqInfo();
						harborCategoryService.selectById(harborCategory.getId(), hr);
						harborCategory = hr.getHarbor_category();
						harborEnName=iEInvoiceFirst.getHarbor_category().getHarbor_name();
						countryEnName=harborCategory.getCountry() == null ? "" : harborCategory.getCountry().getEn_name();
					Contract contract=iEInvoiceFirst.getContract();
					String contractVoucherCode=contract ==null ? "" :contract.getVoucher_code();
					String billNo=iEInvoiceFirst.getBill_no();
					for(int i=1;i<listIEInvoiceOut.size();i++){
						if(!customerCode.equals(listIEInvoiceOut.get(i).getCustomer().getCustomer_code()) || 
								!contractVoucherCode.equals(listIEInvoiceOut.get(i).getContract().getVoucher_code()) || !billNo.equals((listIEInvoiceOut.get(i).getBill_no()))){
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Danh sách đơn hàng chọn không cùng mã khách hàng, số hợp đồng hoặc bill no !','warning',2000);");
							return;
						}
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
					return;
				}
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/ieinvocie/chitietbillnohaiquankhachhang.jasper");
				Map<String, Object> importParam = new HashMap<String, Object>();
				List<IEInvoiceCustomerBillNoDetailReport> list = new ArrayList<>();
				//lấy danh sách id chi tiết đơn hàng xuất xuất khẩu.
				List<Long> listID=new ArrayList<>();
				for (IEInvoiceDetail p : listIEInvoiceDetailSelectOut) {
					if(p.isCheck()){
						listID.add(p.getId());
					}
				}
				JsonObject json=new JsonObject();
				json.add("list_ie_invoice_detail_id", JsonParserUtil.getGson().toJsonTree(listID));
				iEInvoiceService.selectIEInvoiceCustomerBillNoDetailReport(JsonParserUtil.getGson().toJson(json), list);
				if(listID.size()==0){
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
					return;
				}
				importParam.put("order_no", listIEInvoiceDetailSelectOut.get(0).getOrder_no());
				importParam.put("customer_name", customerName);
				importParam.put("address", address);
				importParam.put("shipping_mark", shippingMark);
				importParam.put("harbor_en_name", harborEnName);
				importParam.put("country_en_name", countryEnName);
				importParam.put("freight", freight);
				JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
				JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, dataSource);
				if(word){
					JRDocxExporter exporter = new JRDocxExporter();
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					FacesContext facesContext = FacesContext.getCurrentInstance();
					HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
							.getExternalContext().getResponse();
					httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + "chitietbillnohaiquan"+iEInvoiceCrud.getVoucher_code() + ".docx");
					ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
					exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, servletOutputStream);
					exporter.exportReport();
					facesContext.responseComplete();
				}else{
					// slipt 40 ký tự
					byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
					String ba = Base64.getEncoder().encodeToString(data);
					current.executeScript("utility.printPDF('" + ba + "')");
				}
			}
		}catch (Exception e) {
			logger.error("IEInvoiceBean.exportReportDetailBillNoCustomer:" + e.getMessage(), e);
		}
	}
	public void processExportIEInvoiceByOption(){
		try{
			if(reportTypeExcelSelect !=null){
				long id=reportTypeExcelSelect.getId();
				if(id==1){
					exportIEInvoiceOrderListByVoucherReport();
				}else if(id==2){
					exportIEInvoiceOrderListByProductCodeReport();
				}else if(id==3){
					exportIEInvoiceListByContNoReport();
				}else if(id==4){
					exportIEInvoiceListByTaxValueZeroReport();
				}
			}
		}catch (Exception e) {
			logger.error("IEInvoiceBean.processExportIEInvoiceByOption:" + e.getMessage(), e);
		}
	}
	private void exportIEInvoiceListByTaxValueZeroReport(){
		try{
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("from_date",
					fromDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date",
					toDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch == null ? 0 : customerSearch.getId());
			jsonInfo.addProperty("invoice_code", invoiceCodeSearch);
			jsonInfo.addProperty("product_id", productSearch == null ? 0 : productSearch.getId());
			jsonInfo.addProperty("ie_categories_id", iECategoriesSearch == null ? 0 : iECategoriesSearch.getId());
			jsonInfo.addProperty("contract_voucher_code", contractVoucherCode);
			List<IEInvoiceListByTaxValueZeroReport> list=new ArrayList<>();
			iEInvoiceService.selectIEInvoiceListByTaxValueZeroReport(JsonParserUtil.getGson().toJson(jsonInfo), list);
			//xuat file excel.
			if (list.size()>0) {
				List<Object[]> results = new ArrayList<Object[]>();
				Object[] title = {"stt","sohdong","ngayhdong","tenkh","tinhchat","nuocnk","usdhdong","vndhdong","thoihantt",
						"sotokhai","ngaytokhai","hanghoa","soluongtokhai","usdtokhai","vndtokhai","phuongtienvc","sohoadon","ngayhoadon",
						"soluonghoadon","vattu","usdhoadon","vndhoadon","hinhthuctt","socttt","ngaycttt","usdcttt","vndcttt","bienbantl"};
				results.add(title);
				int stt=1;
				for (IEInvoiceListByTaxValueZeroReport it : list) {
					Object[] row = {stt,it.getContract_voucher_code(),ToolTimeCustomer.convertDatetoString(it.getContract_date()),it.getCustomer_name(),"MUA BÁN",it.getCountry_name(),
							it.getContract_foreign_total_amount(),it.getContract_total_amount(),it.getPayment_period(),it.getDeclaration_code(),it.getDeclaration_date(),
							it.getGoods(),it.getDeclaration_quantity(),it.getDeclaration_foreign_total_amount(),it.getDeclaration_total_amount(),it.getMethod_of_transportation(),
							it.getVoucher_code(),ToolTimeCustomer.convertDatetoString(it.getInvoice_date()),it.getDeclaration_quantity(),it.getMaterial_name(),it.getForeign_total_amount(),it.getTotal_amount(),
							it.getPayment(),it.getPayment_voucher(),ToolTimeCustomer.convertDatetoString(it.getPayment_voucher_date()),it.getPayment_voucher_foreign_total_amount(),
							it.getPayment_voucher_total_amount(),it.getPayment_report()};
					results.add(row);
				}
				ToolReport.printReportExcelRaw(results,"Bang_ke_hso_xk_thue_suat");
			}
		}catch (Exception e) {
			logger.error("IEInvoiceBean.exportIEInvoiceListByTaxValueZeroReport:" + e.getMessage(), e);
		}
	}
	private void exportIEInvoiceListByContNoReport(){
		try{
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("from_date",
					fromDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date",
					toDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch == null ? 0 : customerSearch.getId());
			jsonInfo.addProperty("invoice_code", invoiceCodeSearch);
			jsonInfo.addProperty("product_id", productSearch == null ? 0 : productSearch.getId());
			jsonInfo.addProperty("ie_categories_id", iECategoriesSearch == null ? 0 : iECategoriesSearch.getId());
			jsonInfo.addProperty("contract_voucher_code", contractVoucherCode);
			List<IEInvoiceListByContNoReport> list=new ArrayList<>();
			iEInvoiceService.selectIEInvoicListByContNoReport(JsonParserUtil.getGson().toJson(jsonInfo), list);
			//xuat file excel.
			if (list.size()>0) {
				List<Object[]> results = new ArrayList<Object[]>();
				Object[] title = {"thành phố","tên khách hàng","cont_20_40","cont_no","nơi đến","số cont","số lượng"};
				results.add(title);
				for (IEInvoiceListByContNoReport it : list) {
					Object[] row = {it.getCity_name(),it.getCustomer_name(),it.getFt_container(),it.getContainer_no(),it.getArrival_place(),it.getContainer_number(),it.getQuantity_export()};
					results.add(row);
				}
				ToolReport.printReportExcelRaw(results,"Bang_ke_cont_no");
			}
		}catch (Exception e) {
			logger.error("IEInvoiceBean.exportIEInvoiceListByContNoReport:" + e.getMessage(), e);
		}
	}
	private void exportIEInvoiceOrderListByProductCodeReport(){
		try{
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("from_date",
					fromDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date",
					toDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch == null ? 0 : customerSearch.getId());
			jsonInfo.addProperty("invoice_code", invoiceCodeSearch);
			jsonInfo.addProperty("product_id", productSearch == null ? 0 : productSearch.getId());
			jsonInfo.addProperty("ie_categories_id", iECategoriesSearch == null ? 0 : iECategoriesSearch.getId());
			jsonInfo.addProperty("contract_voucher_code", contractVoucherCode);
			List<IEInvoiceOrderListByProductCodeReport> list=new ArrayList<>();
			iEInvoiceService.selectIEInvoiceOrderListByProductCodeReport(JsonParserUtil.getGson().toJson(jsonInfo), list);
			//xuat file excel.
			if (list.size()>0) {
				List<Object[]> results = new ArrayList<Object[]>();
				Object[] title = {"Số ct","ngày","số hợp đồng","makh","tên khách hàng","số xe","thanh toán","htbocxep","kho","hệ số thuế","edt","tỉ giá","đơn vi tiền",
						"ghi chú","bill_no","tờ khai","port no","masp","tên sản phẩm","số lượng","đơn giá ngoại tệ","tổng tiền ngoại tệ","tài xế"};
				results.add(title);
				for (IEInvoiceOrderListByProductCodeReport it : list) {
					Object[] row = {it.getVoucher_code(),ToolTimeCustomer.convertDatetoString(it.getInvoice_date()),it.getContract_voucher_code(),it.getCustomer_code(),it.getCustomer_name(),
							it.getLicense_plate(),it.getPayment_name(),it.getStevedore_content(),it.getWarehouse_name(),it.getTax_value(),ToolTimeCustomer.convertDatetoString(it.getEtd_date()),it.getExchange_rate(),
							it.getCurrency_type(),it.getNote(),it.getBill_no(),it.getDeclaration_code(),it.getPost_of_tran_code(),it.getProduct_code(),it.getProduct_name(),it.getQuantity_export(),
							it.getForeign_unit_price(),it.getTotal_foreign_amount(),it.getDriver_name()};
					results.add(row);
				}
				ToolReport.printReportExcelRaw(results,"Bang_ke_chi_tiet_don_hang_theo_san_pham");
			}
		}catch (Exception e) {
			logger.error("IEInvoiceBean.exportIEInvoiceOrderListByProductCodeReport:" + e.getMessage(), e);
		}
	}
	private void exportIEInvoiceOrderListByVoucherReport(){
		try{
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("from_date",
					fromDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date",
					toDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch == null ? 0 : customerSearch.getId());
			jsonInfo.addProperty("invoice_code", invoiceCodeSearch);
			jsonInfo.addProperty("product_id", productSearch == null ? 0 : productSearch.getId());
			jsonInfo.addProperty("ie_categories_id", iECategoriesSearch == null ? 0 : iECategoriesSearch.getId());
			jsonInfo.addProperty("contract_voucher_code", contractVoucherCode);
			List<IEInvoiceOrderListByVoucherReport> list=new ArrayList<>();
			iEInvoiceService.selectIEInvoiceOrderListByVoucherReport(JsonParserUtil.getGson().toJson(jsonInfo), list);
			//xuat file excel.
			if (list.size()>0) {
				List<Object[]> results = new ArrayList<Object[]>();
				Object[] title = {"Số ct","ngày","số hợp đồng","makh","tên khách hàng","số xe","thanh toán","htbocxep","kho","hệ số thuế","edt","tỉ giá","đơn vi tiền",
						"ghi chú","bill_no","tờ khai","port no","masp","tên sản phẩm","số lượng","đơn giá ngoại tệ","tổng tiền ngoại tệ","tài xế"};
				results.add(title);
				for (IEInvoiceOrderListByVoucherReport it : list) {
					Object[] row = {it.getVoucher_code(),ToolTimeCustomer.convertDatetoString(it.getInvoice_date()),it.getContract_voucher_code(),it.getCustomer_code(),it.getCustomer_name(),
							it.getLicense_plate(),it.getPayment_name(),it.getStevedore_content(),it.getWarehouse_name(),it.getTax_value(),ToolTimeCustomer.convertDatetoString(it.getEtd_date()),it.getExchange_rate(),
							it.getCurrency_type(),it.getNote(),it.getBill_no(),it.getDeclaration_code(),it.getPost_of_tran_code(),it.getProduct_code(),it.getProduct_name(),it.getQuantity_export(),
							it.getForeign_unit_price(),it.getTotal_foreign_amount(),it.getDriver_name()};
					results.add(row);
				}
				ToolReport.printReportExcelRaw(results,"Bang_ke_chi_tiet_don_hang_theo_chung_tu");
			}
		}catch (Exception e) {
			logger.error("IEInvoiceBean.exportIEInvoiceOrderListByVoucherReport:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			/*
			 * {ie_invoice:{from_date:'',to_date:'',customer_id:0,invoice_code:'
			 * ',voucher_code:'',product_id:0,ie_categories_id:0,
			 * contract_voucher_code:''},page:{page_index:0,page_size:0}}
			 */
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listIEInvoice = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("from_date",
					fromDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date",
					toDateSearch == null ? "" : ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch == null ? 0 : customerSearch.getId());
			jsonInfo.addProperty("invoice_code", invoiceCodeSearch);
			jsonInfo.addProperty("product_id", productSearch == null ? 0 : productSearch.getId());
			jsonInfo.addProperty("ie_categories_id", iECategoriesSearch == null ? 0 : iECategoriesSearch.getId());
			jsonInfo.addProperty("contract_voucher_code", contractVoucherCode);
			// thông tin trang
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("contract", jsonInfo);
			json.add("page", jsonPage);
			iEInvoiceService.search(JsonParserUtil.getGson().toJson(json), page, listIEInvoice);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("IEInvoiceBean.paginatorChange:" + e.getMessage(), e);
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
			logger.error("IEInvoiceBean.initRowPerPage:" + e.getMessage(), e);
		}
	}

	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (iEInvoiceCrud != null) {
				// kiểm tra dữ liệu có đầy đủ không
				Customer customer = iEInvoiceCrud.getCustomer();
				Date invoiceDate = iEInvoiceCrud.getInvoice_date();
				IECategories ieCategories = iEInvoiceCrud.getIe_categories();
				if (customer != null && invoiceDate != null && ieCategories != null) {
					IEInvoiceReqInfo t = new IEInvoiceReqInfo(iEInvoiceCrud);
					if (iEInvoiceCrud.getId() == 0) {
						if (allowSave(new Date())) {
							iEInvoiceCrud.setCreated_date(new Date());
							iEInvoiceCrud.setCreated_by(account.getMember().getName());
							int code = iEInvoiceService.insert(t);
							switch (code) {
							case 0:
								// nếu thành công thêm hợp đồng vào danh sách
								listIEInvoice.add(0, t.getIe_invoice().clone());
								if (listIEInvoiceDetail != null && listIEInvoiceDetail.size() > 0) {
									// trường hợp copy phiếu
									IEInvoiceDetailReqInfo td = new IEInvoiceDetailReqInfo();
									for (IEInvoiceDetail d : listIEInvoiceDetail) {
										if (d.getId() == 0) {
											td.setIe_invoice_detail(d);
											d.setIe_invoice(t.getIe_invoice());
											d.setCreated_by(account.getMember().getName());
											d.setCreated_date(new Date());
											d.setLast_modifed_by(null);
											d.setLast_modifed_date(null);
											iEInvoiceService.insertDetail(td);
										}
									}
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Sao chép thành công!','success',2000);");
								} else {
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Tạo đơn hàng xuất xuất khẩu thành công!','success',2000);");
								}
								break;

							default:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Không lưu được, kiểm tra lại mã hóa đơn hoặc số chứng từ hóa đã tồn tại!','warning',2000);");
								break;
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
						}
					} else {
						if (allowUpdate(new Date())) {
							iEInvoiceCrud.setLast_modifed_by(account.getMember().getName());
							iEInvoiceCrud.setLast_modifed_date(new Date());
							int code = iEInvoiceService.update(t);
							switch (code) {
							case 0:
								// nếu cập nhật thành công thì cập nhật lại danh
								// sách.
								listIEInvoice.set(listIEInvoice.indexOf(iEInvoiceCrud), t.getIe_invoice());
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								break;

							default:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Cập nhật thất bại!','warning',2000);");
								break;
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
						}
					}
				} else {
					current.executeScript("swaldesigntimer('Cảnh báo!','Thông tin không đầy đủ!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void saveOrUpdateDetail() {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		PrimeFaces current = PrimeFaces.current();
		try {
			if (iEInvoiceDetailCrud != null) {
				// kiểm tra số liệu
				Product product = iEInvoiceDetailCrud.getProduct();
				double quantity = iEInvoiceDetailCrud.getQuantity_export();
				double foreignUnitPrice = iEInvoiceDetailCrud.getForeign_unit_price();
				if (product != null && quantity != 0 && foreignUnitPrice != 0) {
					IEInvoiceDetail ieInvoiceDetail = iEInvoiceDetailCrud.clone();
					IEInvoiceDetailReqInfo t = new IEInvoiceDetailReqInfo(ieInvoiceDetail);
					;
					if (iEInvoiceDetailCrud.getId() == 0) {
						if (allowSave(new Date())) {
							ieInvoiceDetail.setCreated_by(account.getMember().getName());
							ieInvoiceDetail.setCreated_date(new Date());
							if (iEInvoiceService.insertDetail(t) == 0) {
								// thêm vào danh sách.
								listIEInvoiceDetail.add(0, t.getIe_invoice_detail());
								notify.success("Lưu thành công!");
								// refesh lại dialog
								iEInvoiceDetailCrud = new IEInvoiceDetail();
								iEInvoiceDetailCrud.setIe_invoice(iEInvoiceCrud);
							} else {
								notify.warning("Lưu thất bại!");
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
						}
					} else {
						if (allowUpdate(new Date())) {
							ieInvoiceDetail.setLast_modifed_by(account.getMember().getName());
							ieInvoiceDetail.setLast_modifed_date(new Date());
							if (iEInvoiceService.updateDetail(t) == 0) {
								// cập nhật lại danh sách
								listIEInvoiceDetail.set(listIEInvoiceDetail.indexOf(t.getIe_invoice_detail()),
										t.getIe_invoice_detail());
								notify.success("Cập nhật thành công!");
							} else {
								notify.warning("Cập nhật thất bại!");
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
						}
					}
				} else {
					notify.warning("Thông tin không đầy đủ!");
				}
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.saveOrUpdateDetail:" + e.getMessage(), e);
		}
		sumUSD();
	}

	public void showIEInvoice() {
		try {
			if (iEInvoiceSelect != null) {
				iEInvoiceCrud = iEInvoiceSelect.clone();
				// load chi tiết hợp đồng.
				listIEInvoiceDetail = new ArrayList<>();
				iEInvoiceService.selectIEInvoideDetailByInvoice(iEInvoiceCrud.getId(), listIEInvoiceDetail);
				sumUSD();
				listIEInvoiceDetailOut=new ArrayList<>();
				if(iEInvoiceSelect.isCheck()){
					listIEInvoiceDetailOut=new ArrayList<>();
					//load danh sách đã check vào
					for(IEInvoiceDetail ie:listIEInvoiceDetailSelectOut){
						if(ie.getIe_invoice().equals(iEInvoiceSelect)){
							listIEInvoiceDetailOut.add(ie);
						}
					}
					
				}
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.showIEInvoice:" + e.getMessage(), e);
		}
	}

	public List<Customer> completeCustomer(String text) {
		try {
			List<Customer> list = new ArrayList<Customer>();
			customerService.complete(formatHandler.converViToEn(text), list);
			return list;
		} catch (Exception e) {
			logger.error("IEInvoiceBean.completeCustomer:" + e.getMessage(), e);
		}
		return null;
	}

	public List<Product> completeProduct(String text) {
		try {
			List<Product> list = new ArrayList<>();
			productService.complete3(formatHandler.converViToEn(text), list);
			return list;
		} catch (Exception e) {
			logger.error("IEInvoiceBean.completeProduct:" + e.getMessage(), e);
		}
		return null;
	}

	public List<Car> completeCar(String text) {
		try {
			if (text != null && !"".equals(text)) {
				List<Car> list = new ArrayList<>();
				carService.complete(formatHandler.converViToEn(text), list);
				return list;
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.completeCar:" + e.getMessage(), e);
		}
		return null;
	}

	public List<Contract> completeContract(String text) {
		try {
			List<Contract> list = new ArrayList<>();
			contractService.complete(formatHandler.converViToEn(text), list);
			return list;
		} catch (Exception e) {
			logger.error("IEInvoiceBean.completeContract:" + e.getMessage(), e);
		}
		return null;
	}

	public void showDialogPrint() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (iEInvoiceCrud != null && iEInvoiceCrud.getId() != 0 && listIEInvoiceDetail != null
					&& listIEInvoiceDetail.size() > 0) {
				current.executeScript("PF('idlg2').show();");
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.showDialogPrint:" + e.getMessage(), e);
		}
	}

	public void showDialogEditIEInvoiceDetail() {
		PrimeFaces current = PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (iEInvoiceCrud != null && iEInvoiceCrud.getId() != 0) {
				if (iEInvoiceDetailSelect != null && iEInvoiceCrud.getId() != 0) {
					iEInvoiceDetailCrud = iEInvoiceDetailSelect.clone();
					/* {contract_id:0,product_id:0} */
					JsonObject json = new JsonObject();
					json.addProperty("contract_id", iEInvoiceCrud.getContract().getId());
					json.addProperty("product_id", iEInvoiceDetailCrud.getProduct().getId());
					List<ContractDetail> listContractDetail = new ArrayList<>();
					contractService.selectContractDetail(JsonParserUtil.getGson().toJson(json), listContractDetail);
					List<Long> listProductId = new ArrayList<>();
					for (ContractDetail d : listContractDetail) {
						listProductId.add(d.getProduct().getId());
						quantityContract = d.getQuantity();// số lượng hợp đồng
					}
					/* {list_product_id:[],contract_id:0} */
					JsonObject json2 = new JsonObject();
					json2.add("list_product_id", JsonParserUtil.getGson().toJsonTree(listProductId));
					json2.addProperty("contract_id", iEInvoiceCrud.getContract().getId());
					List<Object[]> listTemp = new ArrayList<>();
					invoiceService.processingContract(JsonParserUtil.getGson().toJson(json2), listTemp);
					for (Object[] t : listTemp) {
						quantityContractProcessed = Double.parseDouble(Objects.toString(t[1], "0"));
					}
					// thực hiện xem tồn dự kiến và tồn thực tế
					quantityBatchInvCurr = batchService.getQuantityRemaining(iEInvoiceDetailCrud.getProduct().getId());
					// thực hiện tính tồn ước tính
					current.executeScript("PF('idlg1').show();");
				} else {
					notify.warning("Chưa chọn chi tiết để chỉnh sửa!");
				}
			} else {
				notify.warning("Đơn hàng không tồn tại!");
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.showDialogEditIEInvoiceDetail:" + e.getMessage(), e);
		}
	}

	public void copyIEInvoice() {
		try {
			if (iEInvoiceCrud != null && iEInvoiceCrud.getId() != 0 && listIEInvoiceDetail != null
					&& listIEInvoiceDetail.size() > 0) {
				iEInvoiceCrud.setId(0);
				iEInvoiceCrud.setInvoice_code(null);
				iEInvoiceCrud.setVoucher_code(null);
				iEInvoiceCrud.setInvoice(null);
				for (IEInvoiceDetail item : listIEInvoiceDetail) {
					item.setId(0);
					item.setIe_invoice(null);
					item.setLast_modifed_by(null);
					item.setLast_modifed_date(null);
				}
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.copyContract:" + e.getMessage(), e);
		}
	}

	public void showDialogAddIEInvoiceDetail() {
		PrimeFaces current = PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (iEInvoiceCrud != null && iEInvoiceCrud.getId() != 0) {
				iEInvoiceDetailCrud = new IEInvoiceDetail();
				quantityContract = 0;
				quantityContractProcessed = 0;
				quantityBatchInvCurr = 0;// số lượng lô hàng tồn hiện tại
				quantityBatchInvCal = 0;//
				iEInvoiceDetailCrud.setIe_invoice(iEInvoiceCrud);
				current.executeScript("PF('idlg1').show();");
			} else {
				notify.warning("Đơn hàng không tồn tại!");
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.showDialogAddIEInvoiceDetail:" + e.getMessage(), e);
		}
	}

	public void deleteIEInvoice() {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (iEInvoiceSelect != null && iEInvoiceSelect.getId() != 0) {
				if (allowDelete(new Date())) {
					if (iEInvoiceService.deleteById(iEInvoiceSelect.getId()) >= 0) {
						listIEInvoice.remove(iEInvoiceSelect);
						notify.success("Xóa thành công!");
					} else {
						notify.warning("Xóa thất bại!");
					}
				} else {
					notify.warning("Tài khoản chưa được phân quyền!");
				}
			} else {
				notify.warning("Chưa chọn dòng để xóa!");
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.deleteIEInvoice:" + e.getMessage(), e);
		}
	}

	public void deleteIEInvoiceDetail() {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (iEInvoiceDetailSelect != null && iEInvoiceDetailSelect.getId() != 0) {
				if (allowDelete(new Date())) {
					if (iEInvoiceService.deleteDetailById(iEInvoiceDetailSelect.getId()) >= 0) {
						listIEInvoiceDetail.remove(iEInvoiceDetailSelect);
						notify.success("Xóa thành công!");
						sumUSD();
					} else {
						notify.warning("Xóa thất bại!");
					}
				} else {
					notify.warning("Tài khoản chưa được phân quyền!");
				}
			} else {
				notify.warning("Chưa chọn dòng chi tiết để xóa!");
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.deleteIEInvoiceDetail:" + e.getMessage(), e);
		}
	}

	public void createNew() {
		try {
			iEInvoiceCrud = new IEInvoice();
			iEInvoiceCrud.setInvoice_date(new Date());
			iEInvoiceCrud.setCreated_by(account.getMember().getName());
			iEInvoiceCrud.setCreated_by_id(account.getMember().getId());
			listIEInvoiceDetail = new ArrayList<>();
		} catch (Exception e) {
			logger.error("IEInvoiceBean.createNew:" + e.getMessage(), e);
		}
	}

	public void sumUSD() {
		try {
			sumUSD = 0;
			sumUSDXK = 0;
			sumVND = 0;
			if (listIEInvoiceDetail != null && listIEInvoiceDetail.size() > 0) {
				for (IEInvoiceDetail p : listIEInvoiceDetail) {
					BigDecimal bg = BigDecimal.valueOf(p.getQuantity_export())
							.multiply(BigDecimal.valueOf(p.getForeign_unit_price()));
					BigDecimal bg1 = BigDecimal.valueOf(p.getQuantity())
							.multiply(BigDecimal.valueOf(p.getUnit_price()));
					sumUSD = BigDecimal.valueOf(sumUSD).add(bg).doubleValue();
					sumVND = BigDecimal.valueOf(sumVND).add(bg1).doubleValue();
				}
			}
			sumUSD = (double) Math.round(sumUSD * 100) / 100;
			sumUSDXK = (double) Math.round(sumUSD * 100) / 100;
			sumVND = (double) Math.round(sumVND * 1000) / 1000;
		} catch (Exception e) {
			logger.error("IEInvoiceBean.createNew:" + e.getMessage(), e);
		}
	}

	

	public void changeAmountOrProduct(){
		try{
			if (iEInvoiceDetailCrud != null && iEInvoiceDetailCrud.getProduct() != null) {
				// thực hiện xem số lượng thực hiện của hợp đồng
				if (iEInvoiceCrud.getContract() != null) {
					/* {contract_id:0,product_id:0} */
					JsonObject json = new JsonObject();
					json.addProperty("contract_id", iEInvoiceCrud.getContract().getId());
					json.addProperty("product_id", iEInvoiceDetailCrud.getProduct().getId());
					List<ContractDetail> listContractDetail = new ArrayList<>();
					contractService.selectContractDetail(JsonParserUtil.getGson().toJson(json), listContractDetail);
					List<Long> listProductId = new ArrayList<>();
					for (ContractDetail d : listContractDetail) {
						listProductId.add(d.getProduct().getId());
						quantityContract = d.getQuantity();// số lượng hợp đồng
						//lấy đơn giá ngoại tệ
						iEInvoiceDetailCrud.setForeign_unit_price(d.getUnit_price());
					}
					/* {list_product_id:[],contract_id:0} */
					JsonObject json2 = new JsonObject();
					json2.add("list_product_id", JsonParserUtil.getGson().toJsonTree(listProductId));
					json2.addProperty("contract_id", iEInvoiceCrud.getContract().getId());
					List<Object[]> listTemp = new ArrayList<>();
					invoiceService.processingContract(JsonParserUtil.getGson().toJson(json2), listTemp);
					for (Object[] t : listTemp) {
						quantityContractProcessed = Double.parseDouble(Objects.toString(t[1], "0"));
					}
				}
				// thực hiện xem tồn dự kiến và tồn thực tế
				quantityBatchInvCurr = batchService.getQuantityRemaining(iEInvoiceDetailCrud.getProduct().getId());
				// thực hiện tính tồn ước tính
			}
			//tính số lượng, đơn giá, số tiền
			//tính số lượng làm tròn 2 chử số
			double quantity=(double)Math.round(iEInvoiceDetailCrud.getQuantity_export()*100)/100;
			iEInvoiceDetailCrud.setQuantity(quantity);
			//tính toán số tiền ngoại tệ
			double totalForeignAmount=BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(iEInvoiceDetailCrud.getForeign_unit_price())).doubleValue();
			totalForeignAmount=(double)Math.round(totalForeignAmount*1000)/1000;
			iEInvoiceDetailCrud.setTotal_foreign_amount(totalForeignAmount);
			//tính số lượng số tiền ngoại tệ xuất khẩu
			double totalExportForeignAmount=BigDecimal.valueOf(iEInvoiceDetailCrud.getQuantity_export()).multiply(BigDecimal.valueOf(iEInvoiceDetailCrud.getForeign_unit_price())).doubleValue();
			totalExportForeignAmount=(double)Math.round(totalExportForeignAmount*100)/100;
			iEInvoiceDetailCrud.setTotal_export_foreign_amount(totalExportForeignAmount);
			//tính toán đơn giá vnđ
			double unitPrice=BigDecimal.valueOf(iEInvoiceDetailCrud.getForeign_unit_price()).multiply(BigDecimal.valueOf(iEInvoiceCrud.getExchange_rate())).doubleValue();
			unitPrice=(double)Math.round(unitPrice*10000)/1000;
			iEInvoiceDetailCrud.setUnit_price(unitPrice);
			//tính toán số tiền vnd
			double totalAmount=BigDecimal.valueOf(iEInvoiceDetailCrud.getQuantity()).multiply(BigDecimal.valueOf(iEInvoiceDetailCrud.getForeign_unit_price()))
					.multiply(BigDecimal.valueOf(iEInvoiceCrud.getExchange_rate())).doubleValue();
			totalAmount=(double)Math.round(totalAmount*1000)/1000;
			iEInvoiceDetailCrud.setTotal_amount(totalAmount);
		}catch (Exception e) {
			logger.error("IEInvoiceBean.changeAmount:" + e.getMessage(), e);
		}
	}
	public void processPrintPDFReport() {
		try {
			if (reportTypeIEInVoiceSelect != null) {
				long id = reportTypeIEInVoiceSelect.getId();
				if (id == 1) {
					exportPDFIEInvocie();
				} else if (id == 2) {
					exportCommercialInvoice();
				} else if(id==3){
					exportIEExportReport();
				}else if (id == 4) {
					exportProformaInvoice();
				}else if(id==5){
					exportOrderFormReport();
				}else if (id == 6) {
					exportPackingList();
				} else if (id == 8) {
					exportVanningReport();
				}
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.processPrintReport:" + e.getMessage(), e);
		}
	}
	public void processExportPdfOutReport(){
		try{
			 if(reportPdfOutSelect !=null){
				 long id=reportPdfOutSelect.getId();
				 if(id==1){
					 exportCommercialInvoiceOut();
				 }else if(id==2){
					 exportProformaInvoiceOut();
				 }else if(id==3){
					 exportPackingListOut();
				 }if(id==4){
					 exportVanningReportOut();
				 }
			 }
		}catch (Exception e) {
			logger.error("IEInvoiceBean.processExportPdfOutReport:" + e.getMessage(), e);
		}
	}

	private void exportPDFIEInvocie() {
		PrimeFaces current = PrimeFaces.current();
		try {
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/inhoadon.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png"));
			List<IEInvoiceReport> list=new ArrayList<>();
			iEInvoiceService.selectIEInvoiceReport(iEInvoiceCrud.getId(), list);
			importParam.put("list_data", list);
			Date invoiceDate = iEInvoiceCrud.getInvoice_date();
			if (invoiceDate != null) {
				int day = ToolTimeCustomer.getDayM(invoiceDate);
				int month = ToolTimeCustomer.getMonthM(invoiceDate);
				int year = ToolTimeCustomer.getYearM(invoiceDate);
				String titleDay = "Ngày(day) " + String.format("%02d", day) + " tháng(month) "
						+ String.format("%02d", month) + " năm(year) " + year;
				importParam.put("title_day", titleDay);
			}

			importParam.put("created_by", iEInvoiceCrud.getCreated_by());
			Customer customer = iEInvoiceCrud.getCustomer();
			if (customer != null) {
				importParam.put("customer_name", customer.getCustomer_name());
				importParam.put("address", customer.getAddress());
				importParam.put("phone_number", customer.getHome_phone());
			} else {
				importParam.put("customer_name", "");
				importParam.put("address", "");
				importParam.put("phone_number", "");
			}
			importParam.put("voucher_code", iEInvoiceCrud.getVoucher_code());
			Contract contract = iEInvoiceCrud.getContract();
			if (contract != null) {
				importParam.put("contract_voucher_code", contract.getVoucher_code());
				importParam.put("contract_date", contract.getContract_date());
			} else {
				importParam.put("contract_voucher_code", "");
				importParam.put("contract_date", null);
			}
			PaymentMethod method = iEInvoiceCrud.getPayment_method();
			importParam.put("payment_method_name", method == null ? "" : method.getMethod_name());
			importParam.put("place_delivery", iEInvoiceCrud.getPlace_delivery());
			importParam.put("place_discharge", iEInvoiceCrud.getPlace_discharge());
			importParam.put("bl_no", "");
			importParam.put("truck_no", "");
			String totalQuantityUSD = "USD " + formatHandler.getNumberFormat(sumUSDXK, 100);
			importParam.put("total_amount_usd", totalQuantityUSD);
			String wordsTotalAmountUSD = ConvertNumberToText.docSo(sumUSDXK, "USD");
			importParam.put("works_total_amount_usd", wordsTotalAmountUSD);
			String exchangeRate = formatHandler.getNumberFormat(iEInvoiceCrud.getExchange_rate(), 1000) + "VND/USD";
			importParam.put("exchange_rate", exchangeRate);
			String totalQuantityVND = formatHandler.getNumberFormat(sumVND, 1000) + " đồng";
			importParam.put("total_amount_vnd", totalQuantityVND);
			String wordsTotalQuantityVND = ConvertNumberToText.docSo(sumVND, "ĐỒNG");
			importParam.put("works_total_amount_vnd", wordsTotalQuantityVND);
			// slipt 40 ký tự
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			String ba = Base64.getEncoder().encodeToString(data);
			current.executeScript("utility.printPDF('" + ba + "')");
		} catch (Exception e) {
			logger.error("OrderLixBean.exportPDF:" + e.getMessage(), e);
		}

	}
	private void exportCommercialInvoiceOut() {
		PrimeFaces current = PrimeFaces.current();
		try {
			//kiểm tra danh sách đơn hàng xuất xuất khẩu chọn để gọp in có cùng mã khách hàng hợp đồng và bill no hay không.
			IEInvoice iEInvoiceFirst=null;
			if(listIEInvoiceOut.size()>0 && listIEInvoiceDetailSelectOut.size()>0){
				iEInvoiceFirst=listIEInvoiceOut.get(0);
				String customerCode=iEInvoiceFirst.getCustomer().getCustomer_code();
				Contract contract=iEInvoiceFirst.getContract();
				String contractVoucherCode=contract ==null ? "" :contract.getVoucher_code();
				String billNo=iEInvoiceFirst.getBill_no();
				for(int i=1;i<listIEInvoiceOut.size();i++){
					if(!customerCode.equals(listIEInvoiceOut.get(i).getCustomer().getCustomer_code()) || 
							!contractVoucherCode.equals(listIEInvoiceOut.get(i).getContract().getVoucher_code()) || !billNo.equals((listIEInvoiceOut.get(i).getBill_no()))){
						current.executeScript(
								"swaldesigntimer('Cảnh báo!', 'Danh sách đơn hàng chọn không cùng mã khách hàng, số hợp đồng hoặc bill no !','warning',2000);");
						return;
					}
				}
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
				return;
			}
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/commercialinvoice.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png"));
			List<IECommercialInvoiceReportInfo> list = new ArrayList<>();
			//lấy danh sách id chi tiết đơn hàng xuất xuất khẩu.
			List<Long> listID=new ArrayList<>();
			double sumUSD = 0;
			double sumUSDXK = 0;
			for (IEInvoiceDetail p : listIEInvoiceDetailSelectOut) {
				if(p.isCheck()){
					listID.add(p.getId());
					BigDecimal bg = BigDecimal.valueOf(p.getQuantity_export())
							.multiply(BigDecimal.valueOf(p.getForeign_unit_price()));
					sumUSD = BigDecimal.valueOf(sumUSD).add(bg).doubleValue();
				}
			}
			if(listID.size()==0){
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
				return;
			}
			sumUSD = (double) Math.round(sumUSD * 100) / 100;
			sumUSDXK = sumUSD;
			JsonObject json=new JsonObject();
			json.add("list_ie_invoice_detail_id", JsonParserUtil.getGson().toJsonTree(listID));
			iEInvoiceService.selectCommercialInvoiceReport(JsonParserUtil.getGson().toJson(json), list);

			
			importParam.put("list_data", list);
			importParam.put("invoice_date",
					ToolTimeCustomer.convertDateToString(iEInvoiceFirst.getInvoice_date(), "dd/MM/yyyy"));
			importParam.put("shipped_per", iEInvoiceFirst.getShipped_per());
			importParam.put("shipping_mark", iEInvoiceFirst.getShipping_mark());
			importParam.put("payment", iEInvoiceFirst.getPayment());
			importParam.put("term_of_delivery", iEInvoiceFirst.getTerm_of_delivery());
			HarborCategory harborCategory = iEInvoiceFirst.getHarbor_category();
			if (harborCategory != null) {
				HarborCategoryReqInfo hr = new HarborCategoryReqInfo();
				harborCategoryService.selectById(harborCategory.getId(), hr);
				harborCategory = hr.getHarbor_category();
				importParam.put("harbor_en_name", iEInvoiceFirst.getHarbor_category().getHarbor_name());
				importParam.put("country_en_name",
						harborCategory.getCountry() == null ? "" : harborCategory.getCountry().getEn_name());
			}
			
			Customer customer = iEInvoiceFirst.getCustomer();
			if (customer != null) {
				importParam.put("customer_name", customer.getCustomer_name());
				importParam.put("address", customer.getAddress());
			}
			importParam.put("voucher_code", iEInvoiceFirst.getVoucher_code());
			Contract contract = iEInvoiceFirst.getContract();
			if (contract != null) {
				importParam.put("contract_voucher_code", contract.getVoucher_code());
			}
			String totalQuantityUSD = "USD " + formatHandler.getNumberFormat(sumUSDXK, 100);
			importParam.put("total_amount_usd", totalQuantityUSD);
			String wordsTotalAmountUSD = NumberWordConverter.getMoneyIntoWords(sumUSDXK);
			importParam.put("works_total_amount_usd", wordsTotalAmountUSD);
			// tổng thùng
			double totalBox = 0;
			double tons_pcs = 0;
			double tons_mts = 0;
			for (IECommercialInvoiceReportInfo ie : list) {
				totalBox += BigDecimal.valueOf(ie.getQuantity_export())
						.divide(BigDecimal.valueOf(ie.getSpecification())).doubleValue();
				if ("KG".equals(ie.getUnit())) {
					tons_mts = BigDecimal.valueOf(tons_mts).add(BigDecimal.valueOf(ie.getQuantity_export() / 1000))
							.doubleValue();
				} else {
					tons_pcs = BigDecimal.valueOf(tons_pcs).add(BigDecimal.valueOf(ie.getQuantity_export()))
							.doubleValue();
				}
			}
			importParam.put("total_cartons", totalBox);
			
			importParam.put("ton_mts", tons_mts);
			importParam.put("ton_pcs", tons_pcs);
			
			// slipt 40 ký tự
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			String ba = Base64.getEncoder().encodeToString(data);
			current.executeScript("utility.printPDF('" + ba + "')");
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportCommercialInvoiceOut:" + e.getMessage(), e);
		}
	}
	private void exportCommercialInvoice() {
		PrimeFaces current = PrimeFaces.current();
		try {
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/commercialinvoice.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png"));
			List<IECommercialInvoiceReportInfo> list = new ArrayList<>();
			iEInvoiceService.selectCommercialInvoiceReport(iEInvoiceCrud.getId(), list);

			importParam.put("list_data", list);
			importParam.put("invoice_date",
					ToolTimeCustomer.convertDateToString(iEInvoiceCrud.getInvoice_date(), "dd/MM/yyyy"));
			importParam.put("shipped_per", iEInvoiceCrud.getShipped_per());
			importParam.put("shipping_mark", iEInvoiceCrud.getShipping_mark());
			importParam.put("payment", iEInvoiceCrud.getPayment());
			importParam.put("term_of_delivery", iEInvoiceCrud.getTerm_of_delivery());
			HarborCategory harborCategory = iEInvoiceCrud.getHarbor_category();
			if (harborCategory != null) {
				HarborCategoryReqInfo hr = new HarborCategoryReqInfo();
				harborCategoryService.selectById(harborCategory.getId(), hr);
				harborCategory = hr.getHarbor_category();
				importParam.put("harbor_en_name", iEInvoiceCrud.getHarbor_category().getHarbor_name());
				importParam.put("country_en_name",
						harborCategory.getCountry() == null ? "" : harborCategory.getCountry().getEn_name());
			}

			Customer customer = iEInvoiceCrud.getCustomer();
			if (customer != null) {
				importParam.put("customer_name", customer.getCustomer_name());
				importParam.put("address", customer.getAddress());
			}
			importParam.put("voucher_code", iEInvoiceCrud.getVoucher_code());
			Contract contract = iEInvoiceCrud.getContract();
			if (contract != null) {
				importParam.put("contract_voucher_code", contract.getVoucher_code());
			}
			String totalQuantityUSD = "USD " + formatHandler.getNumberFormat(sumUSDXK, 100);
			importParam.put("total_amount_usd", totalQuantityUSD);
			String wordsTotalAmountUSD = NumberWordConverter.getMoneyIntoWords(sumUSDXK);
			importParam.put("works_total_amount_usd", wordsTotalAmountUSD);
			// tổng thùng
			double totalBox = 0;
			double tons_pcs = 0;
			double tons_mts = 0;
			for (IECommercialInvoiceReportInfo ie : list) {
				totalBox += BigDecimal.valueOf(ie.getQuantity_export())
						.divide(BigDecimal.valueOf(ie.getSpecification())).doubleValue();
				if ("KG".equals(ie.getUnit())) {
					tons_mts = BigDecimal.valueOf(tons_mts).add(BigDecimal.valueOf(ie.getQuantity_export() / 1000))
							.doubleValue();
				} else {
					tons_pcs = BigDecimal.valueOf(tons_pcs).add(BigDecimal.valueOf(ie.getQuantity_export()))
							.doubleValue();
				}
			}
			importParam.put("total_cartons", totalBox);

			importParam.put("ton_mts", tons_mts);
			importParam.put("ton_pcs", tons_pcs);

			// slipt 40 ký tự
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			String ba = Base64.getEncoder().encodeToString(data);
			current.executeScript("utility.printPDF('" + ba + "')");
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportCommercialInvoice:" + e.getMessage(), e);
		}
	}

	private void exportProformaInvoiceOut() {
		PrimeFaces current = PrimeFaces.current();
		try {
			
			//kiểm tra danh sách đơn hàng xuất xuất khẩu chọn để gọp in có cùng mã khách hàng hợp đồng và bill no hay không.
			IEInvoice iEInvoiceFirst=null;
			if(listIEInvoiceOut.size()>0 && listIEInvoiceDetailSelectOut.size()>0){
				iEInvoiceFirst=listIEInvoiceOut.get(0);
				String customerCode=iEInvoiceFirst.getCustomer().getCustomer_code();
				Contract contract=iEInvoiceFirst.getContract();
				String contractVoucherCode=contract ==null ? "" :contract.getVoucher_code();
				String billNo=iEInvoiceFirst.getBill_no();
				for(int i=1;i<listIEInvoiceOut.size();i++){
					if(!customerCode.equals(listIEInvoiceOut.get(i).getCustomer().getCustomer_code()) || 
							!contractVoucherCode.equals(listIEInvoiceOut.get(i).getContract().getVoucher_code()) || !billNo.equals((listIEInvoiceOut.get(i).getBill_no()))){
						current.executeScript(
								"swaldesigntimer('Cảnh báo!', 'Danh sách đơn hàng chọn không cùng mã khách hàng, số hợp đồng hoặc bill no !','warning',2000);");
						return;
					}
				}
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
				return;
			}
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/proformainvoice.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png"));
			List<IEProformaInvocieReportInfo> list = new ArrayList<>();
			//lấy danh sách id chi tiết đơn hàng xuất xuất khẩu.
			List<Long> listID=new ArrayList<>();
			double sumUSD = 0;
			double sumUSDXK = 0;
			for (IEInvoiceDetail p : listIEInvoiceDetailSelectOut) {
				if(p.isCheck()){
					listID.add(p.getId());
					BigDecimal bg = BigDecimal.valueOf(p.getQuantity_export())
							.multiply(BigDecimal.valueOf(p.getForeign_unit_price()));
					sumUSD = BigDecimal.valueOf(sumUSD).add(bg).doubleValue();
				}
			}
			if(listID.size()==0){
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
				return;
			}
			sumUSD = (double) Math.round(sumUSD * 100) / 100;
			sumUSDXK = sumUSD;
			JsonObject json=new JsonObject();
			json.add("list_ie_invoice_detail_id", JsonParserUtil.getGson().toJsonTree(listID));
			iEInvoiceService.selectIEProformaInvoiceReport(JsonParserUtil.getGson().toJson(json), list);
			
			
			
			
			importParam.put("list_data", list);
			importParam.put("invoice_date",
					ToolTimeCustomer.convertDateToString(iEInvoiceFirst.getInvoice_date(), "dd/MM/yyyy"));
			importParam.put("payment", iEInvoiceFirst.getPayment());
			importParam.put("term_of_delivery", iEInvoiceFirst.getTerm_of_delivery());
			importParam.put("account_no", iEInvoiceFirst.getAccount_b());
			HarborCategory harborCategory = iEInvoiceFirst.getHarbor_category();
			if (harborCategory != null) {
				importParam.put("harbor_en_name", iEInvoiceFirst.getHarbor_category().getHarbor_name());
			}
			
			Customer customer = iEInvoiceFirst.getCustomer();
			if (customer != null) {
				importParam.put("customer_name", customer.getCustomer_name());
				importParam.put("address", customer.getAddress());
			}
			String totalQuantityUSD = "USD " + formatHandler.getNumberFormat(sumUSDXK, 100);
			importParam.put("total_amount_usd", totalQuantityUSD);
			String wordsTotalAmountUSD = NumberWordConverter.getMoneyIntoWords(sumUSDXK);
			importParam.put("works_total_amount_usd", wordsTotalAmountUSD);
			// tổng thùng
			double totalBox = 0;
			double tons_pcs = 0;
			double tons_mts = 0;
			for (IEProformaInvocieReportInfo ie : list) {
				totalBox += BigDecimal.valueOf(ie.getQuantity_export())
						.divide(BigDecimal.valueOf(ie.getSpecification())).doubleValue();
				if ("KG".equals(ie.getUnit())) {
					tons_mts = BigDecimal.valueOf(tons_mts).add(BigDecimal.valueOf(ie.getQuantity_export() / 1000))
							.doubleValue();
				} else {
					tons_pcs = BigDecimal.valueOf(tons_pcs).add(BigDecimal.valueOf(ie.getQuantity_export()))
							.doubleValue();
				}
			}
			importParam.put("total_cartons", totalBox);
			
			importParam.put("ton_mts", tons_mts);
			importParam.put("ton_pcs", tons_pcs);
			
			// slipt 40 ký tự
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			String ba = Base64.getEncoder().encodeToString(data);
			current.executeScript("utility.printPDF('" + ba + "')");
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportProformaInvoiceOut:" + e.getMessage(), e);
		}
	}
	private void exportProformaInvoice() {
		PrimeFaces current = PrimeFaces.current();
		try {
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/proformainvoice.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png"));
			List<IEProformaInvocieReportInfo> list = new ArrayList<>();
			iEInvoiceService.selectIEProformaInvoiceReport(iEInvoiceCrud.getId(), list);

			importParam.put("list_data", list);
			importParam.put("invoice_date",
					ToolTimeCustomer.convertDateToString(iEInvoiceCrud.getInvoice_date(), "dd/MM/yyyy"));
			importParam.put("payment", iEInvoiceCrud.getPayment());
			importParam.put("term_of_delivery", iEInvoiceCrud.getTerm_of_delivery());
			importParam.put("account_no", iEInvoiceCrud.getAccount_b());
			HarborCategory harborCategory = iEInvoiceCrud.getHarbor_category();
			if (harborCategory != null) {
				importParam.put("harbor_en_name", iEInvoiceCrud.getHarbor_category().getHarbor_name());
			}

			Customer customer = iEInvoiceCrud.getCustomer();
			if (customer != null) {
				importParam.put("customer_name", customer.getCustomer_name());
				importParam.put("address", customer.getAddress());
			}
			String totalQuantityUSD = "USD " + formatHandler.getNumberFormat(sumUSDXK, 100);
			importParam.put("total_amount_usd", totalQuantityUSD);
			String wordsTotalAmountUSD = NumberWordConverter.getMoneyIntoWords(sumUSDXK);
			importParam.put("works_total_amount_usd", wordsTotalAmountUSD);
			// tổng thùng
			double totalBox = 0;
			double tons_pcs = 0;
			double tons_mts = 0;
			for (IEProformaInvocieReportInfo ie : list) {
				totalBox += BigDecimal.valueOf(ie.getQuantity_export())
						.divide(BigDecimal.valueOf(ie.getSpecification())).doubleValue();
				if ("KG".equals(ie.getUnit())) {
					tons_mts = BigDecimal.valueOf(tons_mts).add(BigDecimal.valueOf(ie.getQuantity_export() / 1000))
							.doubleValue();
				} else {
					tons_pcs = BigDecimal.valueOf(tons_pcs).add(BigDecimal.valueOf(ie.getQuantity_export()))
							.doubleValue();
				}
			}
			importParam.put("total_cartons", totalBox);

			importParam.put("ton_mts", tons_mts);
			importParam.put("ton_pcs", tons_pcs);

			// slipt 40 ký tự
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			String ba = Base64.getEncoder().encodeToString(data);
			current.executeScript("utility.printPDF('" + ba + "')");
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportProformaInvoice:" + e.getMessage(), e);
		}
	}

	private void exportVanningReport() {
		PrimeFaces current = PrimeFaces.current();
		try {
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/vanningreport.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png"));
			List<IEVanningReport> list = new ArrayList<>();
			iEInvoiceService.selectIEVanningReport(iEInvoiceCrud.getId(), list);

			importParam.put("list_data", list);
			importParam.put("invoice_date",
					ToolTimeCustomer.convertDateToString(iEInvoiceCrud.getInvoice_date(), "dd/MM/yyyy"));
			importParam.put("shipped_per", iEInvoiceCrud.getShipped_per());
			importParam.put("shipping_mark", iEInvoiceCrud.getShipping_mark());
			importParam.put("payment", iEInvoiceCrud.getPayment());
			importParam.put("created_by", iEInvoiceCrud.getCreated_by());
			importParam.put("term_of_delivery", iEInvoiceCrud.getTerm_of_delivery());
			importParam.put("load_date",
					ToolTimeCustomer.convertDateToString(iEInvoiceCrud.getUp_goods_date(), "dd/MM/yyyy"));
			HarborCategory harborCategory = iEInvoiceCrud.getHarbor_category();
			if (harborCategory != null) {
				HarborCategoryReqInfo hr = new HarborCategoryReqInfo();
				harborCategoryService.selectById(harborCategory.getId(), hr);
				harborCategory = hr.getHarbor_category();
				importParam.put("harbor_en_name", iEInvoiceCrud.getHarbor_category().getHarbor_name());
				importParam.put("country_en_name",
						harborCategory.getCountry() == null ? "" : harborCategory.getCountry().getEn_name());
			}

			Customer customer = iEInvoiceCrud.getCustomer();
			if (customer != null) {
				importParam.put("customer_name", customer.getCustomer_name());
				importParam.put("address", customer.getAddress());
			}
			importParam.put("voucher_code", iEInvoiceCrud.getVoucher_code());
			Contract contract = iEInvoiceCrud.getContract();
			if (contract != null) {
				importParam.put("contract_voucher_code", contract.getVoucher_code());
			}

			// slipt 40 ký tự
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			String ba = Base64.getEncoder().encodeToString(data);
			current.executeScript("utility.printPDF('" + ba + "')");
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportVanningReport:" + e.getMessage(), e);
		}
	}
	private void exportVanningReportOut() {
		PrimeFaces current = PrimeFaces.current();
		try {
			//kiểm tra danh sách đơn hàng xuất xuất khẩu chọn để gọp in có cùng mã khách hàng hợp đồng và bill no hay không.
			IEInvoice iEInvoiceFirst=null;
			if(listIEInvoiceOut.size()>0 && listIEInvoiceDetailSelectOut.size()>0){
				iEInvoiceFirst=listIEInvoiceOut.get(0);
				String customerCode=iEInvoiceFirst.getCustomer().getCustomer_code();
				Contract contract=iEInvoiceFirst.getContract();
				String contractVoucherCode=contract ==null ? "" :contract.getVoucher_code();
				String billNo=iEInvoiceFirst.getBill_no();
				for(int i=1;i<listIEInvoiceOut.size();i++){
					if(!customerCode.equals(listIEInvoiceOut.get(i).getCustomer().getCustomer_code()) || 
							!contractVoucherCode.equals(listIEInvoiceOut.get(i).getContract().getVoucher_code()) || !billNo.equals((listIEInvoiceOut.get(i).getBill_no()))){
						current.executeScript(
								"swaldesigntimer('Cảnh báo!', 'Danh sách đơn hàng chọn không cùng mã khách hàng, số hợp đồng hoặc bill no !','warning',2000);");
						return;
					}
				}
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
				return;
			}
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/vanningreport.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png"));
			List<IEVanningReport> list = new ArrayList<>();
			//lấy danh sách id chi tiết đơn hàng xuất xuất khẩu.
			List<Long> listID=new ArrayList<>();
			for (IEInvoiceDetail p : listIEInvoiceDetailSelectOut) {
				if(p.isCheck()){
					listID.add(p.getId());
				}
			}
			if(listID.size()==0){
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
				return;
			}
			JsonObject json=new JsonObject();
			json.add("list_ie_invoice_detail_id", JsonParserUtil.getGson().toJsonTree(listID));
			iEInvoiceService.selectIEVanningReport(JsonParserUtil.getGson().toJson(json), list);
			
			
			
			importParam.put("list_data", list);
			importParam.put("invoice_date",
					ToolTimeCustomer.convertDateToString(iEInvoiceFirst.getInvoice_date(), "dd/MM/yyyy"));
			importParam.put("shipped_per", iEInvoiceFirst.getShipped_per());
			importParam.put("shipping_mark", iEInvoiceFirst.getShipping_mark());
			importParam.put("payment", iEInvoiceFirst.getPayment());
			importParam.put("created_by", iEInvoiceFirst.getCreated_by());
			importParam.put("term_of_delivery", iEInvoiceFirst.getTerm_of_delivery());
			importParam.put("load_date",
					ToolTimeCustomer.convertDateToString(iEInvoiceFirst.getUp_goods_date(), "dd/MM/yyyy"));
			HarborCategory harborCategory = iEInvoiceFirst.getHarbor_category();
			if (harborCategory != null) {
				HarborCategoryReqInfo hr = new HarborCategoryReqInfo();
				harborCategoryService.selectById(harborCategory.getId(), hr);
				harborCategory = hr.getHarbor_category();
				importParam.put("harbor_en_name", iEInvoiceFirst.getHarbor_category().getHarbor_name());
				importParam.put("country_en_name",
						harborCategory.getCountry() == null ? "" : harborCategory.getCountry().getEn_name());
			}
			
			Customer customer = iEInvoiceFirst.getCustomer();
			if (customer != null) {
				importParam.put("customer_name", customer.getCustomer_name());
				importParam.put("address", customer.getAddress());
			}
			importParam.put("voucher_code", iEInvoiceCrud.getVoucher_code());
			Contract contract = iEInvoiceCrud.getContract();
			if (contract != null) {
				importParam.put("contract_voucher_code", contract.getVoucher_code());
			}
			
			// slipt 40 ký tự
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			String ba = Base64.getEncoder().encodeToString(data);
			current.executeScript("utility.printPDF('" + ba + "')");
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportVanningReportOut:" + e.getMessage(), e);
		}
	}

	private void exportPackingList() {
		PrimeFaces current = PrimeFaces.current();
		try {
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/packinglist.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png"));
			List<IEPackingListReport> list = new ArrayList<>();
			iEInvoiceService.selectIEPackingListReport(iEInvoiceCrud.getId(), list);

			importParam.put("list_data", list);
			importParam.put("voucher_code", iEInvoiceCrud.getVoucher_code());
			importParam.put("shipping_mark", iEInvoiceCrud.getShipping_mark());
			Contract contract = iEInvoiceCrud.getContract();
			if (contract != null) {
				importParam.put("contract_voucher_code", contract.getVoucher_code());
			}
			importParam.put("invoice_date",
					ToolTimeCustomer.convertDateToString(iEInvoiceCrud.getInvoice_date(), "dd/MM/yyyy"));
			importParam.put("term_of_delivery", iEInvoiceCrud.getTerm_of_delivery());
			HarborCategory harborCategory = iEInvoiceCrud.getHarbor_category();
			if (harborCategory != null) {
				HarborCategoryReqInfo hr = new HarborCategoryReqInfo();
				harborCategoryService.selectById(harborCategory.getId(), hr);
				harborCategory = hr.getHarbor_category();
				importParam.put("harbor_en_name", iEInvoiceCrud.getHarbor_category().getHarbor_name());
				importParam.put("country_en_name",
						harborCategory.getCountry() == null ? "" : harborCategory.getCountry().getEn_name());
			}

			Customer customer = iEInvoiceCrud.getCustomer();
			if (customer != null) {
				importParam.put("customer_name", customer.getCustomer_name());
				importParam.put("address", customer.getAddress());
			}
			// tổng thùng
			double totalCartons = 0;
			double totalNetWeight = 0;
			double totalGrossWeight = 0;
			for (IEPackingListReport ie : list) {
				// số thùng
				BigDecimal box = BigDecimal.valueOf(ie.getQuantity_export())
						.divide(BigDecimal.valueOf(ie.getSpecification()));
				totalCartons = BigDecimal.valueOf(totalCartons).add(box).doubleValue();
				// FormatHandler.getInstance().getNumberFormatEn(($F{quantity_export}*$F{product.factor}/1000),1000000)+"
				// MTS"
				double netWeight = BigDecimal.valueOf(ie.getQuantity_export())
						.multiply(BigDecimal.valueOf(ie.getFactor())).doubleValue();
				double mlNetWeight = netWeight / 1000;
				totalNetWeight = BigDecimal.valueOf(totalNetWeight).add(BigDecimal.valueOf(mlNetWeight))
						.doubleValue();
				// FormatHandler.getInstance().getNumberFormatEn((($F{quantity_export}*$F{product.factor})+($F{quantity_export}/$F{product.specification}*$F{product.tare})),1000000)+"
				// KGS"
				double boxPackedWeight = BigDecimal.valueOf(ie.getQuantity_export())
						.divide(BigDecimal.valueOf(ie.getSpecification()))
						.multiply(BigDecimal.valueOf(ie.getTare())).doubleValue();
				totalGrossWeight = BigDecimal.valueOf(totalGrossWeight).add(BigDecimal.valueOf(boxPackedWeight))
						.doubleValue();
			}
			importParam.put("total_cartons", totalCartons);
			importParam.put("total_net_weight", totalNetWeight);
			importParam.put("total_gross_weight", totalGrossWeight);
			// slipt 40 ký tự
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			String ba = Base64.getEncoder().encodeToString(data);
			current.executeScript("utility.printPDF('" + ba + "')");
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportPackingList:" + e.getMessage(), e);
		}
	}
	public void exportPackingListWord() {
//		PrimeFaces current = PrimeFaces.current();
		try {
			if(iEInvoiceCrud!=null && iEInvoiceCrud.getId() !=0 && listIEInvoiceDetail.size()>0){
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/ieinvocie/packinglist.jasper");
				Map<String, Object> importParam = new HashMap<String, Object>();
				importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/gfx/lixco_logo.png"));
				List<IEPackingListReport> list = new ArrayList<>();
				iEInvoiceService.selectIEPackingListReport(iEInvoiceCrud.getId(), list);
				
				importParam.put("list_data", list);
				importParam.put("voucher_code", iEInvoiceCrud.getVoucher_code());
				importParam.put("shipping_mark", iEInvoiceCrud.getShipping_mark());
				Contract contract = iEInvoiceCrud.getContract();
				if (contract != null) {
					importParam.put("contract_voucher_code", contract.getVoucher_code());
				}
				importParam.put("invoice_date",
						ToolTimeCustomer.convertDateToString(iEInvoiceCrud.getInvoice_date(), "dd/MM/yyyy"));
				importParam.put("term_of_delivery", iEInvoiceCrud.getTerm_of_delivery());
				HarborCategory harborCategory = iEInvoiceCrud.getHarbor_category();
				if (harborCategory != null) {
					HarborCategoryReqInfo hr = new HarborCategoryReqInfo();
					harborCategoryService.selectById(harborCategory.getId(), hr);
					harborCategory = hr.getHarbor_category();
					importParam.put("harbor_en_name", iEInvoiceCrud.getHarbor_category().getHarbor_name());
					importParam.put("country_en_name",
							harborCategory.getCountry() == null ? "" : harborCategory.getCountry().getEn_name());
				}
				
				Customer customer = iEInvoiceCrud.getCustomer();
				if (customer != null) {
					importParam.put("customer_name", customer.getCustomer_name());
					importParam.put("address", customer.getAddress());
				}
				// tổng thùng
				double totalCartons = 0;
				double totalNetWeight = 0;
				double totalGrossWeight = 0;
				for (IEPackingListReport ie : list) {
					// số thùng
					BigDecimal box = BigDecimal.valueOf(ie.getQuantity_export())
							.divide(BigDecimal.valueOf(ie.getSpecification()));
					totalCartons = BigDecimal.valueOf(totalCartons).add(box).doubleValue();
					// FormatHandler.getInstance().getNumberFormatEn(($F{quantity_export}*$F{product.factor}/1000),1000000)+"
					// MTS"
					double netWeight = BigDecimal.valueOf(ie.getQuantity_export())
							.multiply(BigDecimal.valueOf(ie.getFactor())).doubleValue();
					double mlNetWeight = netWeight / 1000;
					totalNetWeight = BigDecimal.valueOf(totalNetWeight).add(BigDecimal.valueOf(mlNetWeight))
							.doubleValue();
					// FormatHandler.getInstance().getNumberFormatEn((($F{quantity_export}*$F{product.factor})+($F{quantity_export}/$F{product.specification}*$F{product.tare})),1000000)+"
					// KGS"
					double boxPackedWeight = BigDecimal.valueOf(ie.getQuantity_export())
							.divide(BigDecimal.valueOf(ie.getSpecification()))
							.multiply(BigDecimal.valueOf(ie.getTare())).doubleValue();
					totalGrossWeight = BigDecimal.valueOf(totalGrossWeight).add(BigDecimal.valueOf(boxPackedWeight))
							.doubleValue();
				}
				importParam.put("total_cartons", totalCartons);
				importParam.put("total_net_weight", totalNetWeight);
				importParam.put("total_gross_weight", totalGrossWeight);
				String printFileName = JasperFillManager.fillReportToFile(reportPath, importParam, new JREmptyDataSource());
//				JasperPrint jasperPrint = JasperFillManager.fillReportToFile)(reportPath, importParam, new JREmptyDataSource());
				JRDocxExporter exporter = new JRDocxExporter();
				exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME, printFileName);
				FacesContext facesContext = FacesContext.getCurrentInstance();
				HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
						.getExternalContext().getResponse();
				httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + "packing_list"+iEInvoiceCrud.getVoucher_code() + ".docx");
				ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, servletOutputStream);
				exporter.exportReport();
				facesContext.responseComplete();
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportPackingList:" + e.getMessage(), e);
		}
	}
	private void exportPackingListOut() {
		PrimeFaces current = PrimeFaces.current();
		try {
			//kiểm tra danh sách đơn hàng xuất xuất khẩu chọn để gọp in có cùng mã khách hàng hợp đồng và bill no hay không.
			IEInvoice iEInvoiceFirst=null;
			if(listIEInvoiceOut.size()>0 && listIEInvoiceDetailSelectOut.size()>0){
				iEInvoiceFirst=listIEInvoiceOut.get(0);
				String customerCode=iEInvoiceFirst.getCustomer().getCustomer_code();
				Contract contract=iEInvoiceFirst.getContract();
				String contractVoucherCode=contract ==null ? "" :contract.getVoucher_code();
				String billNo=iEInvoiceFirst.getBill_no();
				for(int i=1;i<listIEInvoiceOut.size();i++){
					if(!customerCode.equals(listIEInvoiceOut.get(i).getCustomer().getCustomer_code()) || 
							!contractVoucherCode.equals(listIEInvoiceOut.get(i).getContract().getVoucher_code()) || !billNo.equals((listIEInvoiceOut.get(i).getBill_no()))){
						current.executeScript(
								"swaldesigntimer('Cảnh báo!', 'Danh sách đơn hàng chọn không cùng mã khách hàng, số hợp đồng hoặc bill no !','warning',2000);");
						return;
					}
				}
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
				return;
			}
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/packinglist.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png"));
			List<IEPackingListReport> list = new ArrayList<>();
			//lấy danh sách id chi tiết đơn hàng xuất xuất khẩu.
			List<Long> listID=new ArrayList<>();
			for (IEInvoiceDetail p : listIEInvoiceDetailSelectOut) {
				if(p.isCheck()){
					listID.add(p.getId());
				}
			}
			if(listID.size()==0){
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xuất file!','warning',2000);");
				return;
			}
			JsonObject json=new JsonObject();
			json.add("list_ie_invoice_detail_id", JsonParserUtil.getGson().toJsonTree(listID));
			iEInvoiceService.selectIEPackingListReport(JsonParserUtil.getGson().toJson(json), list);
			
			importParam.put("list_data", list);
			importParam.put("voucher_code", iEInvoiceFirst.getVoucher_code());
			importParam.put("shipping_mark", iEInvoiceFirst.getShipping_mark());
			Contract contract = iEInvoiceFirst.getContract();
			if (contract != null) {
				importParam.put("contract_voucher_code", contract.getVoucher_code());
			}
			importParam.put("invoice_date",
					ToolTimeCustomer.convertDateToString(iEInvoiceFirst.getInvoice_date(), "dd/MM/yyyy"));
			importParam.put("term_of_delivery", iEInvoiceFirst.getTerm_of_delivery());
			HarborCategory harborCategory = iEInvoiceFirst.getHarbor_category();
			if (harborCategory != null) {
				HarborCategoryReqInfo hr = new HarborCategoryReqInfo();
				harborCategoryService.selectById(harborCategory.getId(), hr);
				harborCategory = hr.getHarbor_category();
				importParam.put("harbor_en_name", iEInvoiceFirst.getHarbor_category().getHarbor_name());
				importParam.put("country_en_name",
						harborCategory.getCountry() == null ? "" : harborCategory.getCountry().getEn_name());
			}
			
			Customer customer = iEInvoiceFirst.getCustomer();
			if (customer != null) {
				importParam.put("customer_name", customer.getCustomer_name());
				importParam.put("address", customer.getAddress());
			}
			// tổng thùng
			double totalCartons = 0;
			double totalNetWeight = 0;
			double totalGrossWeight = 0;
			for (IEPackingListReport ie : list) {
				// số thùng
				BigDecimal box = BigDecimal.valueOf(ie.getQuantity_export())
						.divide(BigDecimal.valueOf(ie.getSpecification()));
				totalCartons = BigDecimal.valueOf(totalCartons).add(box).doubleValue();
				// FormatHandler.getInstance().getNumberFormatEn(($F{quantity_export}*$F{product.factor}/1000),1000000)+"
				// MTS"
				double netWeight = BigDecimal.valueOf(ie.getQuantity_export())
						.multiply(BigDecimal.valueOf(ie.getFactor())).doubleValue();
				double mlNetWeight = netWeight / 1000;
				totalNetWeight = BigDecimal.valueOf(totalNetWeight).add(BigDecimal.valueOf(mlNetWeight))
						.doubleValue();
				// FormatHandler.getInstance().getNumberFormatEn((($F{quantity_export}*$F{product.factor})+($F{quantity_export}/$F{product.specification}*$F{product.tare})),1000000)+"
				// KGS"
				double boxPackedWeight = BigDecimal.valueOf(ie.getQuantity_export())
						.divide(BigDecimal.valueOf(ie.getSpecification()))
						.multiply(BigDecimal.valueOf(ie.getTare())).doubleValue();
				totalGrossWeight = BigDecimal.valueOf(totalGrossWeight).add(BigDecimal.valueOf(boxPackedWeight))
						.doubleValue();
			}
			importParam.put("total_cartons", totalCartons);
			importParam.put("total_net_weight", totalNetWeight);
			importParam.put("total_gross_weight", totalGrossWeight);
			// slipt 40 ký tự
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			String ba = Base64.getEncoder().encodeToString(data);
			current.executeScript("utility.printPDF('" + ba + "')");
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportPackingListOut:" + e.getMessage(), e);
		}
	}

	private void exportOrderFormReport(){
		PrimeFaces current=PrimeFaces.current();
		try{
			
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/reports/ieinvocie/indonhang.jasper");
			String reportPath2 = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/reports/ieinvocie/indonhangS.jasper");
					 
				Map<String, Object> importParam = new HashMap<String, Object>();
				importParam.put("voucher_code",iEInvoiceCrud.getVoucher_code());
				importParam.put("invoice_date",ToolTimeCustomer.convertDateToString(iEInvoiceCrud.getInvoice_date(),"dd/MM/yyyy"));
				importParam.put("license_plate", iEInvoiceCrud.getCar() == null ? "" : iEInvoiceCrud.getCar().getLicense_plate());
				String customerName=iEInvoiceCrud.getCustomer().getCustomer_name();
				//slipt 40 ký tự
				String line1="";
				String line2="";
				if(customerName.length() >40){
					String customer40=customerName.substring(0,40);
					char last= customerName.charAt(40);
					if(last!=' '){
						//tìm last space trong đoạn 40
						int space=customer40.lastIndexOf(' ');
						line1=customer40.substring(0,space);
						line2=customerName.substring(space);
					}else{
						line1=customer40;
						line2=customerName.substring(40);
					}
				}else{
					line1=customerName;
				}
				importParam.put("customer_name1", line1);
				importParam.put("customer_name2", line2);
				importParam.put("customer_code",iEInvoiceCrud.getCustomer().getCustomer_code());
				importParam.put("note", iEInvoiceCrud.getNote());
				List<IEOrderFormReport> listData=new ArrayList<>();
				iEInvoiceService.selectOrderFormReport(iEInvoiceCrud.getId(), listData);
				for(IEOrderFormReport p:listData){
					BatchReqInfo br=new BatchReqInfo();
					batchService.selectByCode(p.getBatch_code(),br);
					if(br.getBatch()!=null){
					   p.setManufacture_date(br.getBatch().getManufacture_date());
					}
				}
				importParam.put("list_data",listData);
 				JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
				byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
				List<byte[]> listByte=new ArrayList<>();
				listByte.add(data);
				JasperPrint jasperPrint2 = JasperFillManager.fillReport(reportPath2, importParam, new JREmptyDataSource());
				byte[] data2 = JasperExportManager.exportReportToPdf(jasperPrint2);
				listByte.add(data2);
				byte[] lastData=mergePDF(listByte);
				String ba = Base64.getEncoder().encodeToString(lastData);
				current.executeScript("utility.printPDF('" + ba + "')");
			}catch(Exception e){
		    logger.error("IEInvoiceBean.exportOrderFormReport:" + e.getMessage(), e);
	     }

	}
	private void exportIEExportReport(){
		PrimeFaces current = PrimeFaces.current();
		try {
			String reportPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/phieuxuatkho.jasper");
			String reportPath2 = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/reports/ieinvocie/biennhanhanghoa.jasper");
			Map<String, Object> importParam = new HashMap<String, Object>();
			importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/gfx/lixco_logo.png"));
			List<IEExportReport> list = new ArrayList<>();
			iEInvoiceService.selectIEExportReport(iEInvoiceCrud.getId(), list);

			importParam.put("list_data", list);
			Date invoiceDate=iEInvoiceCrud.getInvoice_date();
			importParam.put("voucher_code", iEInvoiceCrud.getVoucher_code());
			int day=ToolTimeCustomer.getDayM(invoiceDate);
			int month=ToolTimeCustomer.getMonthM(invoiceDate);
			int year=ToolTimeCustomer.getYearM(invoiceDate);
			importParam.put("day", day);importParam.put("month", month);importParam.put("year", year);
			importParam.put("department_name",iEInvoiceCrud.getDepartment_name());
			importParam.put("warehouse_name", "LIX");
			Car car=iEInvoiceCrud.getCar();
			importParam.put("license_plate", car==null ?"" :car.getLicense_plate());
			

			Customer customer = iEInvoiceCrud.getCustomer();
			if (customer != null) {
				importParam.put("customer_name", customer.getCustomer_name());
			}
			// tổng thùng
			double totalQuantityExport = 0;
			double totalAmount = 0;
			double totalBox=0;
			for (IEExportReport ie : list) {
				// số lượng và tổng tiền
				totalQuantityExport=BigDecimal.valueOf(totalQuantityExport).add(BigDecimal.valueOf(ie.getQuantity_export())).doubleValue();
				totalAmount=BigDecimal.valueOf(totalAmount).add(BigDecimal.valueOf(ie.getTotal_amount())).doubleValue();
				double box=BigDecimal.valueOf(ie.getQuantity_export()).divide(BigDecimal.valueOf(ie.getSpecification())).doubleValue();
				totalBox=BigDecimal.valueOf(totalBox).add(BigDecimal.valueOf(box)).doubleValue();
			}
			//làm tròn
			totalAmount=Math.round(totalAmount*1000)/1000;
			totalQuantityExport=Math.round(totalQuantityExport*1000)/100;
			
			importParam.put("total_quantity", totalQuantityExport);
			importParam.put("total_amount", totalAmount);
			importParam.put("words_total_amount", ConvertNumberToText.docSo(totalAmount, "ĐỒNG"));
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			List<byte[]> listByte=new ArrayList<>();
			listByte.add(data);
			importParam.put("total_box", totalBox);
			JasperPrint jasperPrint2 = JasperFillManager.fillReport(reportPath2, importParam, new JREmptyDataSource());
			byte[] data2 = JasperExportManager.exportReportToPdf(jasperPrint2);
			listByte.add(data2);
			byte[] lastData=mergePDF(listByte);
			String ba = Base64.getEncoder().encodeToString(lastData);
			current.executeScript("utility.printPDF('" + ba + "')");
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportIEExportReport:" + e.getMessage(), e);
		}
	}
	public void exportExcel(){
		try{
			if (reportTypeIEInVoiceSelect != null) {
				long id = reportTypeIEInVoiceSelect.getId();
				if(id==1){
					exportExcelRawIEInvoice();
				}
			}
		}catch (Exception e) {
			logger.error("IEInvoiceBean.exportExcel:" + e.getMessage(), e);
		}
	}
	public void showDialogExportFileOutner(){
		PrimeFaces current = PrimeFaces.current();
		try {
				current.executeScript("PF('idlg3').show();");
		} catch (Exception e) {
			logger.error("IEInvoiceBean.showDialogExportFileOutner:" + e.getMessage(), e);
		}
	}
	private void exportExcelBangKeSoThuTuTheoCT(){
		try{
			
		}catch (Exception e) {
			logger.error("IEInvoiceBean.exportExcelBangKeSoThuTuTheoCT:" + e.getMessage(), e);
		}
	}

	private void exportExcelRawIEInvoice() {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			List<IEInvoiceReport> list=new ArrayList<>();
			iEInvoiceService.selectIEInvoiceReport(iEInvoiceCrud.getId(), list);
			if (list.size()>0) {
				List<Object[]> results = new ArrayList<Object[]>();
				Object[] title = { "SỐ hóa đơn", "ngày","địa chỉ","điện thoại","số hợp đồng","htttoan","tên sản phẩm", "dvt", "số lượng ", "đơn giá(nt)",
						"thành tiền(nt)", "số tiền","tỉ  giá","số tiền chữ","số tiền usd" };
				results.add(title);
				Customer customer=iEInvoiceCrud.getCustomer();
				String httoan=iEInvoiceCrud.getPayment_method().getMethod_name();
				String contractVoucherCode=iEInvoiceCrud.getContract().getVoucher_code();
				for (IEInvoiceReport it : list) {
					double ex=BigDecimal.valueOf(iEInvoiceCrud.getExchange_rate()).multiply(BigDecimal.valueOf(it.getForeign_unit_price())).doubleValue();
					double totalAmount=BigDecimal.valueOf(ex).multiply(BigDecimal.valueOf(it.getQuantity_export())).doubleValue();
					totalAmount=(double)Math.round(totalAmount*1000)/1000;
					Object[] row = {iEInvoiceCrud.getVoucher_code(),ToolTimeCustomer.convertDateToString(iEInvoiceCrud.getInvoice_date(), "dd/MM/yyyy"), customer.getAddress()==null ?  "" :customer.getAddress() ,
							customer.getHome_phone()==null ? "" :customer.getHome_phone(),
							contractVoucherCode==null ? "" : contractVoucherCode,httoan==null ? "" :httoan,it.getProduct_name(),it.getUnit(),it.getQuantity_export(),it.getForeign_unit_price(),it.getTotal_export_foreign_amount(),
							totalAmount,iEInvoiceCrud.getExchange_rate(),"",""};
					results.add(row);
				}
				Object[] rowLast={"","","","","","","Tổng cộng","","","","",sumVND,"",ConvertNumberToText.docSo(sumVND, "ĐỒNG"),NumberWordConverter.getMoneyIntoWords(sumUSDXK)};
				results.add(rowLast);
				String titleEx="Hoa_don_xuat_khau_"+iEInvoiceCrud.getVoucher_code();
				ToolReport.printReportExcelRaw(results,titleEx);
			} else {
				notify.message("Không có dữ liệu");
			}
		} catch (Exception e) {
			logger.error("IEInvoiceBean.exportExcelRawIEInvoice:" + e.getMessage(), e);
		}
	}

	private byte[] mergePDF(List<byte[]> pdfFilesAsByteArray) throws DocumentException, IOException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Document document = null;
		PdfCopy writer = null;
		for (byte[] pdfByteArray : pdfFilesAsByteArray) {

			try {
				PdfReader reader = new PdfReader(pdfByteArray);
				int numberOfPages = reader.getNumberOfPages();

				if (document == null) {
					document = new Document(reader.getPageSizeWithRotation(1));
					writer = new PdfCopy(document, outStream); // new
					document.open();
				}
				PdfImportedPage page;
				for (int i = 0; i < numberOfPages;) {
					++i;
					page = writer.getImportedPage(reader, i);
					writer.addPage(page);
				}
			} catch (Exception e) {
			}

		}
		document.close();
		outStream.close();
		return outStream.toByteArray();

	}
	public void updateIEInvoiceDetail(){
		try{
			
		}catch (Exception e) {
			logger.error("IEInvoiceBean.updateIEInvoiceDetail",e.getMessage(),e);
		}
	}
	public void selectIEInvoice(boolean checkbox,IEInvoice item){
		try{
			if(checkbox){
				listIEInvoiceOut.add(item);
				//load list chi tiết đơn hàng xuất xuất khẩu
				listIEInvoiceDetailOut=new ArrayList<>();
				iEInvoiceService.selectIEInvoideDetailByInvoice(item.getId(), listIEInvoiceDetailOut);
				for(IEInvoiceDetail d:listIEInvoiceDetailOut){
					d.setIe_invoice(item);
					
				}
				listIEInvoiceDetailSelectOut.addAll(listIEInvoiceDetailOut);
			}else{
				//remove detail chứa hóa đơn đó
				Iterator<IEInvoiceDetail> itr = listIEInvoiceDetailSelectOut.iterator(); 
		        while (itr.hasNext()) 
		        { 
		        	IEInvoiceDetail x = (IEInvoiceDetail)itr.next(); 
		            if (x.getIe_invoice().equals(item)) 
		                itr.remove(); 
		        } 
		        listIEInvoiceOut.remove(item);
		        listIEInvoiceDetailOut=new ArrayList<>();
			}
		}catch (Exception e) {
			logger.error("IEInvoiceBean.selectIEInvoice",e.getMessage(),e);
		}
	}
//	public void selectIEInvoiceDetail(boolean checkbox,IEInvoiceDetail item){
//		try{
//			if(checkbox){
//				listIEInvoiceDetailSelectOut.add(item);
//				if(item.)
//			}else{
//				listIEInvoiceDetailSelectOut.remove(item);
//			}
//		}catch (Exception e) {
//			logger.error("IEInvoiceBean.selectIEInvoiceDetail",e.getMessage(),e);
//		}
//	}
	@Override
	protected Logger getLogger() {
		return logger;
	}

	public List<IECategories> getListIECategories() {
		return listIECategories;
	}

	public void setListIECategories(List<IECategories> listIECategories) {
		this.listIECategories = listIECategories;
	}

	public List<PaymentMethod> getListPaymentMethod() {
		return listPaymentMethod;
	}

	public void setListPaymentMethod(List<PaymentMethod> listPaymentMethod) {
		this.listPaymentMethod = listPaymentMethod;
	}

	public List<Warehouse> getListWarehouse() {
		return listWarehouse;
	}

	public void setListWarehouse(List<Warehouse> listWarehouse) {
		this.listWarehouse = listWarehouse;
	}

	public IEInvoice getiEInvoiceCrud() {
		return iEInvoiceCrud;
	}

	public void setiEInvoiceCrud(IEInvoice iEInvoiceCrud) {
		this.iEInvoiceCrud = iEInvoiceCrud;
	}

	public IEInvoice getiEInvoiceSelect() {
		return iEInvoiceSelect;
	}

	public void setiEInvoiceSelect(IEInvoice iEInvoiceSelect) {
		this.iEInvoiceSelect = iEInvoiceSelect;
	}

	public List<IEInvoice> getListIEInvoice() {
		return listIEInvoice;
	}

	public void setListIEInvoice(List<IEInvoice> listIEInvoice) {
		this.listIEInvoice = listIEInvoice;
	}

	public IEInvoiceDetail getiEInvoiceDetailCrud() {
		return iEInvoiceDetailCrud;
	}

	public void setiEInvoiceDetailCrud(IEInvoiceDetail iEInvoiceDetailCrud) {
		this.iEInvoiceDetailCrud = iEInvoiceDetailCrud;
	}

	public IEInvoiceDetail getiEInvoiceDetailSelect() {
		return iEInvoiceDetailSelect;
	}

	public void setiEInvoiceDetailSelect(IEInvoiceDetail iEInvoiceDetailSelect) {
		this.iEInvoiceDetailSelect = iEInvoiceDetailSelect;
	}

	public List<IEInvoiceDetail> getListIEInvoiceDetail() {
		return listIEInvoiceDetail;
	}

	public void setListIEInvoiceDetail(List<IEInvoiceDetail> listIEInvoiceDetail) {
		this.listIEInvoiceDetail = listIEInvoiceDetail;
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

	public Product getProductSearch() {
		return productSearch;
	}

	public void setProductSearch(Product productSearch) {
		this.productSearch = productSearch;
	}

	public IECategories getiECategoriesSearch() {
		return iECategoriesSearch;
	}

	public void setiECategoriesSearch(IECategories iECategoriesSearch) {
		this.iECategoriesSearch = iECategoriesSearch;
	}

	public String getContractVoucherCode() {
		return contractVoucherCode;
	}

	public void setContractVoucherCode(String contractVoucherCode) {
		this.contractVoucherCode = contractVoucherCode;
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

	public FormatHandler getFormatHandler() {
		return formatHandler;
	}

	public void setFormatHandler(FormatHandler formatHandler) {
		this.formatHandler = formatHandler;
	}

	public IProductService getProductService() {
		return productService;
	}

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	public List<Currency> getListCurrency() {
		return listCurrency;
	}

	public void setListCurrency(List<Currency> listCurrency) {
		this.listCurrency = listCurrency;
	}

	public List<HarborCategory> getListHarborCategory() {
		return listHarborCategory;
	}

	public void setListHarborCategory(List<HarborCategory> listHarborCategory) {
		this.listHarborCategory = listHarborCategory;
	}

	public List<Stocker> getListStocker() {
		return listStocker;
	}

	public void setListStocker(List<Stocker> listStocker) {
		this.listStocker = listStocker;
	}

	public List<FormUpGoods> getListFormUpGoods() {
		return listFormUpGoods;
	}

	public void setListFormUpGoods(List<FormUpGoods> listFormUpGoods) {
		this.listFormUpGoods = listFormUpGoods;
	}

	public double getSumUSD() {
		return sumUSD;
	}

	public void setSumUSD(double sumUSD) {
		this.sumUSD = sumUSD;
	}

	public double getSumUSDXK() {
		return sumUSDXK;
	}

	public void setSumUSDXK(double sumUSDXK) {
		this.sumUSDXK = sumUSDXK;
	}

	public double getSumVND() {
		return sumVND;
	}

	public void setSumVND(double sumVND) {
		this.sumVND = sumVND;
	}

	public double getQuantityContract() {
		return quantityContract;
	}

	public void setQuantityContract(double quantityContract) {
		this.quantityContract = quantityContract;
	}

	public double getQuantityContractProcessed() {
		return quantityContractProcessed;
	}

	public void setQuantityContractProcessed(double quantityContractProcessed) {
		this.quantityContractProcessed = quantityContractProcessed;
	}

	public double getQuantityBatchInvCurr() {
		return quantityBatchInvCurr;
	}

	public void setQuantityBatchInvCurr(double quantityBatchInvCurr) {
		this.quantityBatchInvCurr = quantityBatchInvCurr;
	}

	public double getQuantityBatchInvCal() {
		return quantityBatchInvCal;
	}

	public void setQuantityBatchInvCal(double quantityBatchInvCal) {
		this.quantityBatchInvCal = quantityBatchInvCal;
	}

	public List<ReportTypeIEInVoice> getListSelectReport() {
		return listSelectReport;
	}

	public void setListSelectReport(List<ReportTypeIEInVoice> listSelectReport) {
		this.listSelectReport = listSelectReport;
	}

	public ReportTypeIEInVoice getReportTypeIEInVoiceSelect() {
		return reportTypeIEInVoiceSelect;
	}

	public void setReportTypeIEInVoiceSelect(ReportTypeIEInVoice reportTypeIEInVoiceSelect) {
		this.reportTypeIEInVoiceSelect = reportTypeIEInVoiceSelect;
	}

	public List<ReportTypeIEInVoice> getListSelectExcel() {
		return listSelectExcel;
	}

	public void setListSelectExcel(List<ReportTypeIEInVoice> listSelectExcel) {
		this.listSelectExcel = listSelectExcel;
	}

	public ReportTypeIEInVoice getReportTypeExcelSelect() {
		return reportTypeExcelSelect;
	}

	public void setReportTypeExcelSelect(ReportTypeIEInVoice reportTypeExcelSelect) {
		this.reportTypeExcelSelect = reportTypeExcelSelect;
	}

	public List<IEInvoiceDetail> getListIEInvoiceDetailOut() {
		return listIEInvoiceDetailOut;
	}

	public void setListIEInvoiceDetailOut(List<IEInvoiceDetail> listIEInvoiceDetailOut) {
		this.listIEInvoiceDetailOut = listIEInvoiceDetailOut;
	}

	public List<IEInvoiceDetail> getListIEInvoiceDetailSelectOut() {
		return listIEInvoiceDetailSelectOut;
	}

	public void setListIEInvoiceDetailSelectOut(List<IEInvoiceDetail> listIEInvoiceDetailSelectOut) {
		this.listIEInvoiceDetailSelectOut = listIEInvoiceDetailSelectOut;
	}

	public List<IEInvoice> getListIEInvoiceOut() {
		return listIEInvoiceOut;
	}

	public void setListIEInvoiceOut(List<IEInvoice> listIEInvoiceOut) {
		this.listIEInvoiceOut = listIEInvoiceOut;
	}
	public List<ReportTypeIEInVoice> getListReportPdfOut() {
		return listReportPdfOut;
	}
	public void setListReportPdfOut(List<ReportTypeIEInVoice> listReportPdfOut) {
		this.listReportPdfOut = listReportPdfOut;
	}
	public ReportTypeIEInVoice getReportPdfOutSelect() {
		return reportPdfOutSelect;
	}
	public void setReportPdfOutSelect(ReportTypeIEInVoice reportPdfOutSelect) {
		this.reportPdfOutSelect = reportPdfOutSelect;
	}
	public List<ReportTypeIEInVoice> getListReportHarborOut() {
		return listReportHarborOut;
	}
	public void setListReportHarborOut(List<ReportTypeIEInVoice> listReportHarborOut) {
		this.listReportHarborOut = listReportHarborOut;
	}
	public ReportTypeIEInVoice getReportHarborOutSelect() {
		return reportHarborOutSelect;
	}
	public void setReportHarborOutSelect(ReportTypeIEInVoice reportHarborOutSelect) {
		this.reportHarborOutSelect = reportHarborOutSelect;
	}
	
}
