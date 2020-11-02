package lixco.com.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

import com.google.gson.JsonObject;

import lixco.com.common.FormatHandler;
import lixco.com.common.HolderParser;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Customer;
import lixco.com.entity.CustomerPricingProgram;
import lixco.com.entity.CustomerTypes;
import lixco.com.entity.PricingProgram;
import lixco.com.entity.PricingProgramDetail;
import lixco.com.entity.Product;
import lixco.com.interfaces.ICustomerPricingProgramService;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.ICustomerTypesService;
import lixco.com.interfaces.IPricingProgramDetailService;
import lixco.com.interfaces.IPricingProgramService;
import lixco.com.interfaces.IProductService;
import lixco.com.reqInfo.CustomerPricingProgramReqInfo;
import lixco.com.reqInfo.CustomerReqInfo;
import lixco.com.reqInfo.PricingProgramDetailReqInfo;
import lixco.com.reqInfo.PricingProgramReqInfo;
import lixco.com.reqInfo.ProductReqInfo;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.entity.ParamReportDetail;
import trong.lixco.com.service.ParamReportDetailService;
import trong.lixco.com.util.Notify;
import trong.lixco.com.util.ToolReport;

@Named
@ViewScoped
public class PricingProgramBean extends AbstractBean{
	private static final long serialVersionUID = 5675661379860367168L;
	@Inject
	private Logger logger;
	@Inject
	private IPricingProgramService pricingProgramService;
	@Inject
	private IPricingProgramDetailService pricingProgramDetailService;
	@Inject
	private IProductService productService;
	@Inject
	private ICustomerPricingProgramService customerPricingProgramService;
	@Inject
	private ICustomerTypesService customerTypesService;
	@Inject
	private ICustomerService customerService;
	@Inject
	private ParamReportDetailService paramReportDetailService;
	private PricingProgram pricingProgramCrud;
	private PricingProgram pricingProgramSelect;
	private List<PricingProgram> listPricingProgram;
	private PricingProgramDetail pricingProgramDetailCrud;
	private PricingProgramDetail pricingProgramDetailSelect;
	private List<PricingProgramDetail> listPricingProgramDetail;
	private Date fromDate;
	private Date toDate;
	private String programCode;
	private String mainProductCode;
	private String voucherCode;
	private Product product;
	private PricingProgram parentPricingProgram;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	/*search detail*/
	private Product product2;
	private int pageSize2;
	private NavigationInfo navigationInfo2;
	private List<Integer> listRowPerPage2;
	private Account account;
	private FormatHandler formatHandler;
	/*Cài đặt chương trình đơn giá*/
	private List<CustomerTypes> listCustomerTypes;
	private CustomerPricingProgram customerPricingProgramCrud;
	private CustomerPricingProgram customerPricingProgramSelect;
	private List<CustomerPricingProgram> listCustomerPricingProgram;
	private int pageSize3;
	private NavigationInfo navigationInfo3;
	private List<Integer> listRowPerPage3;
	private CustomerTypes customerTypesSetting;
	private DualListModel<Customer> modelCustomerSetting;
	private PricingProgram pricingProgramSetting;
	private String textSearch;
	/*search 3*/
	private CustomerTypes customerTypesSearch3;
	private String programCodeSearch3;
	private Customer customerSearch3;
	private Date fromDateSearch3;
	private Date toDateSearch3;
	/*In báo giá*/
	private String baogiaso;
	private String baogiasogoc;
	private String tenkhachhang;
	private Date ngaybaogia;
	/*panel danh sách khách hàng đã cài đặt chương trình đơn giá*/
	private List<CustomerPricingProgram> listCustomerPricingProgramSet;
	private CustomerPricingProgram customerPricingProgramSetSelect;
	private CustomerPricingProgram customerPricingProgramSetCrud;
	/*nạp chương trình đơn giá bằng excel*/
	private boolean rewrite;// ghi lại dữ liệu hay không
	@Override
	protected void initItem() {
		try{//paging 1
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			//paging 2
			navigationInfo2 = new NavigationInfo();
			navigationInfo2.setCurrentPage(1);
			initRowPerPage2();
			navigationInfo2.setLimit(pageSize2);
			navigationInfo2.setMaxIndices(5);
			formatHandler=FormatHandler.getInstance();
			Date currentDate=new Date();
			fromDate=ToolTimeCustomer.getDateMinCustomer(ToolTimeCustomer.getMonthM(currentDate), ToolTimeCustomer.getYearM(currentDate));
			fromDateSearch3=fromDate;
			search();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			createdNew();
			/*innit setting*/
			listCustomerTypes=new ArrayList<CustomerTypes>();
			customerTypesService.selectAll(listCustomerTypes);
			modelCustomerSetting=new DualListModel<>(new ArrayList<Customer>(), new ArrayList<Customer>());
			/*load danh sach cài đặt chương trình đơn giá */
			//paging 2
			navigationInfo3 = new NavigationInfo();
			navigationInfo3.setCurrentPage(1);
			initRowPerPage3();
			navigationInfo3.setLimit(pageSize3);
			navigationInfo3.setMaxIndices(5);
			searchCustomerPricingProgram();
		}catch(Exception e){
			logger.error("PricingProgramBean.initItem:"+e.getMessage(),e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void search(){
		try{
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listPricingProgram=new ArrayList<PricingProgram>();
			PagingInfo page=new PagingInfo();
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDate, "dd/MM/yyyy HH:mm:ss"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDate, "dd/MM/yyyy HH:mm:ss"));
			jsonInfo.addProperty("program_code", programCode);
			jsonInfo.addProperty("parent_pricing_program_id",parentPricingProgram !=null ? parentPricingProgram.getId() : 0);
			jsonInfo.addProperty("product_id", product !=null ? product.getId() : 0);
			jsonInfo.addProperty("disable",0);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("pricing_program_info", jsonInfo);
			json.add("page", jsonPage);
			pricingProgramService.search(JsonParserUtil.getGson().toJson(json), page, listPricingProgram);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		}catch(Exception e){
			logger.error("PricingProgramBean.search:"+e.getMessage(),e);
		}
	}
	public void searchCustomerPricingProgram(){
		try{
			/*{ customer_pricing_program_info:{customer_types_id:0,customer_id:0,program_code:'',from_date:'',to_date:'',disable:-1}, page:{page_index:0, page_size:0}}*/
			initRowPerPage3();
			navigationInfo3.setLimit(pageSize3);
			navigationInfo3.setMaxIndices(5);
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("customer_types_id", customerTypesSearch3 ==null ? 0: customerTypesSearch3.getId());
			jsonInfo.addProperty("customer_id", customerSearch3==null ? 0: customerSearch3.getId());
			jsonInfo.addProperty("program_code", programCodeSearch3);
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch3, "dd/MM/yyyy HH:mm:ss"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch3, "dd/MM/yyyy HH:mm:ss"));
			jsonInfo.addProperty("disable", -1);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",1);
			jsonPage.addProperty("page_size", pageSize3);
			JsonObject json=new JsonObject();
			json.add("customer_pricing_program_info", jsonInfo);
			json.add("page", jsonPage);
			listCustomerPricingProgram=new ArrayList<>();
			PagingInfo page=new PagingInfo();
			customerPricingProgramService.search(JsonParserUtil.getGson().toJson(json), page,listCustomerPricingProgram);
			navigationInfo3.setTotalRecords((int) page.getTotalRow());
			navigationInfo3.setCurrentPage(1);
		}catch(Exception e){
			logger.error("PricingProgramBean.searchCustomerPricingProgram:"+e.getMessage(),e);
		}
	}
	public void search2(){
		try{
			
			initRowPerPage2();
			navigationInfo2.setLimit(pageSize2);
			navigationInfo2.setMaxIndices(5);
			listPricingProgramDetail=new ArrayList<PricingProgramDetail>();
			PagingInfo page=new PagingInfo();
			if(pricingProgramCrud !=null && pricingProgramCrud.getId()!=0){
			// thông tin phân trang
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("program_id",pricingProgramCrud !=null ? pricingProgramCrud.getId() : 0);
			jsonInfo.addProperty("product_id", product2 !=null ? product2.getId():0);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",1);
			jsonPage.addProperty("page_size", pageSize2);
			JsonObject json=new JsonObject();
			json.add("pricing_program_detail_info", jsonInfo);
			json.add("page", jsonPage);
			pricingProgramDetailService.search(JsonParserUtil.getGson().toJson(json), page, listPricingProgramDetail);
			}
			navigationInfo2.setTotalRecords((int) page.getTotalRow());
			navigationInfo2.setCurrentPage(1);
		}catch(Exception e){
			logger.error("PricingProgramBean.search2:"+e.getMessage(),e);
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
			logger.error("PricingProgramBean.initRowPerPage:" + e.getMessage(), e);
		}
	}
	private void initRowPerPage2() {
		try {
			listRowPerPage2 = new ArrayList<Integer>();
			listRowPerPage2.add(90);
			listRowPerPage2.add(180);
			listRowPerPage2.add(240);
			pageSize2 = listRowPerPage2.get(0);
		} catch (Exception e) {
			logger.error("PricingProgramBean.initRowPerPage2:" + e.getMessage(), e);
		}
	}
	private void initRowPerPage3() {
		try {
			listRowPerPage3 = new ArrayList<Integer>();
			listRowPerPage3.add(90);
			listRowPerPage3.add(120);
			listRowPerPage3.add(150);
			pageSize3 = listRowPerPage3.get(0);
		} catch (Exception e) {
			logger.error("PricingProgramBean.initRowPerPage3:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listPricingProgram = new ArrayList<PricingProgram>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDate, "dd/MM/yyyy HH:mm:ss"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDate, "dd/MM/yyyy HH:mm:ss"));
			jsonInfo.addProperty("program_code", programCode);
			jsonInfo.addProperty("parent_pricing_program_id",parentPricingProgram !=null ? parentPricingProgram.getId() : 0);
			jsonInfo.addProperty("voucher_code", voucherCode);
			jsonInfo.addProperty("product_id", product !=null ? product.getId() : 0);
			jsonInfo.addProperty("disable", 0);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("pricing_program_info", jsonInfo);
			json.add("page", jsonPage);
			pricingProgramService.search(JsonParserUtil.getGson().toJson(json), page, listPricingProgram);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("PricingProgramBean.paginatorChange:" + e.getMessage(), e);
		}
	}
	public void paginatorChange2(int currentPage2) {
		try {
			navigationInfo2.setLimit(pageSize2);
			navigationInfo2.setMaxIndices(5);
			listPricingProgramDetail = new ArrayList<PricingProgramDetail>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("program_id",pricingProgramCrud !=null ? pricingProgramCrud.getId() : 0);
			jsonInfo.addProperty("product_id", product2 !=null ? product2.getId():0);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",currentPage2);
			jsonPage.addProperty("page_size", pageSize2);
			JsonObject json=new JsonObject();
			json.add("pricing_program_detail_info", jsonInfo);
			json.add("page", jsonPage);
			pricingProgramDetailService.search(JsonParserUtil.getGson().toJson(json), page, listPricingProgramDetail);
			navigationInfo2.setTotalRecords((int) page.getTotalRow());
			navigationInfo2.setCurrentPage(currentPage2);
		} catch (Exception e) {
			logger.error("PricingProgramBean.paginatorChange2:" + e.getMessage(), e);
		}
	}
	public void paginatorChange3(int currentPage3) {
		try {
			////
			navigationInfo3.setLimit(pageSize3);
			navigationInfo3.setMaxIndices(5);
			listCustomerPricingProgram=new ArrayList<>();
			PagingInfo page=new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("customer_types_id", customerTypesSearch3 ==null ? 0: customerTypesSearch3.getId());
			jsonInfo.addProperty("customer_id", customerSearch3==null ? 0: customerSearch3.getId());
			jsonInfo.addProperty("program_code", programCodeSearch3);
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch3, "dd/MM/yyyy HH:mm:ss"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch3, "dd/MM/yyyy HH:mm:ss"));
			jsonInfo.addProperty("disable", -1);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",currentPage3);
			jsonPage.addProperty("page_size", pageSize3);
			JsonObject json=new JsonObject();
			json.add("customer_pricing_program_info", jsonInfo);
			json.add("page", jsonPage);
			customerPricingProgramService.search(JsonParserUtil.getGson().toJson(json), page, listCustomerPricingProgram);
			navigationInfo3.setTotalRecords((int) page.getTotalRow());
			navigationInfo3.setCurrentPage(currentPage3);
		} catch (Exception e) {
			logger.error("PricingProgramBean.paginatorChange3:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdate(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(pricingProgramCrud !=null){
				String programCode=pricingProgramCrud.getProgram_code();
				Date effectiveDate=pricingProgramCrud.getEffective_date();
				Date expiryDate=pricingProgramCrud.getExpiry_date();
				PricingProgram mainpricingProgram=pricingProgramCrud.getParent_pricing_program();
				if(programCode !=null && programCode!="" && effectiveDate !=null && (expiryDate==null || effectiveDate.getTime() <= expiryDate.getTime())){
					PricingProgramReqInfo t=new PricingProgramReqInfo(pricingProgramCrud);
					if(mainpricingProgram != null){
						//kiểm tra ngày có phù hợp không so với chương trình cha
						long timeMainEffectiveDate = mainpricingProgram.getEffective_date().getTime();
						Date mainExpiryDate=mainpricingProgram.getExpiry_date();
						if(effectiveDate.getTime()>=timeMainEffectiveDate && 
							(mainExpiryDate==null ||expiryDate==null || expiryDate.getTime() <=mainExpiryDate.getTime())){
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo', 'Ngày hiệu lực và ngày kết thúc phải nằm trong phạm vi hiệu lực của chương trình chính!','warning',2000);");
							return;
						}
						
					}else if(pricingProgramCrud.getId()!=0){
						//kiểm tra ngày của tất cả chương trình con có hợp lệ không
						StringBuilder result=new StringBuilder();
						pricingProgramService.getDateMaxSubPricingProgram(pricingProgramCrud.getId(), result);
						String str=result.toString();
						if(str.length()>0){
							JsonObject json=JsonParserUtil.getGson().fromJson(str,JsonObject.class);
							HolderParser hCount=JsonParserUtil.getValueNumber(json,"count", null);
							/* Ngày chương trình con*/
							HolderParser hSEffectiveDate=JsonParserUtil.getValueString(json,"effective_date", null);
							HolderParser hSExpiryDate=JsonParserUtil.getValueString(json,"expiry_date", null);
							int count=Integer.parseInt(Objects.toString(hCount.getValue(),"0"));
							Date sEffectiveDate=ToolTimeCustomer.convertStringToDate(Objects.toString(hSEffectiveDate.getValue(),null),"dd/MM/yyyy HH:mm:ss");
							Date sExpiryDate=ToolTimeCustomer.convertStringToDate(Objects.toString(hSExpiryDate.getValue(),null),"dd/MM/yyyy HH:mm:ss");
							if(count==0 || (sEffectiveDate !=null && sEffectiveDate.getTime()>=effectiveDate.getTime() && (expiryDate ==null || 
									sExpiryDate==null || sExpiryDate.getTime()<= expiryDate.getTime()))){
								//not thing
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Chương trình này có ngày hiệu lực và ngày kết thúc không hợp lệ so với chương trình đơn giá phụ liên kết!','warning',3000);");
								return;
							}
						}
					}
					if(pricingProgramCrud.getId()==0){
						if(allowSave(new Date())){
							pricingProgramCrud.setCreated_date(new Date());
							pricingProgramCrud.setCreated_by(account.getMember().getName());
							if(pricingProgramService.insert(t)!=-1){
								//kiểm tra thử có phải lưu dữ liệu copy hay không
								if(listPricingProgramDetail !=null && listPricingProgramDetail.size()>0 && listPricingProgramDetail.get(0).getId()==0){
									//đúng
									for(PricingProgramDetail pd:listPricingProgramDetail){
										pd.setPricing_program(t.getPricing_program());
										pricingProgramDetailService.update(new PricingProgramDetailReqInfo(pd));
									}
									current.executeScript(
											"swaldesigntimer('Thành công!', 'sao chép thành công!','success',2000);");
									search();
								}else{
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listPricingProgram.add(0, pricingProgramCrud);
								}
								
							}else{
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Lưu chương trình thất bại!','error',2000);");
							}
							
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						if(allowUpdate(new Date())){
							pricingProgramCrud.setLast_modifed_date(new Date());
							pricingProgramCrud.setLast_modifed_by(account.getMember().getName());
							if(pricingProgramService.update(t) !=-1){
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								listPricingProgram.set(listPricingProgram.indexOf(pricingProgramCrud),pricingProgramCrud);
							}else{
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Cập nhật thất bại!','error',2000);");
							}
							
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ hoặc ngày hiệu lực và ngày kết thúc không hợp lệ!','warning',2500);");
				}
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.saveOrUpdate:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdateCustomerPricingProgram(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(customerPricingProgramCrud !=null){
				PricingProgram ct=customerPricingProgramCrud.getPricing_program();
				Customer cs=customerPricingProgramCrud.getCustomer();
				Date eff=customerPricingProgramCrud.getEffective_date();
				if(ct !=null && cs !=null && eff !=null){
					//wrap containner
					CustomerPricingProgramReqInfo t=new CustomerPricingProgramReqInfo(customerPricingProgramCrud);
					int chk=0;
					if(customerPricingProgramCrud.getId()==0){
						if(allowSave(new Date())){
							customerPricingProgramCrud.setCreated_by(account.getMember().getName());
							customerPricingProgramCrud.setCreated_date(new Date());
							chk=customerPricingProgramService.insert(t);
							switch (chk) {
							case 0:
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Thành công!','success',2000);");
								listCustomerPricingProgram.add(0,customerPricingProgramCrud);
								customerPricingProgramCrud=new CustomerPricingProgram();
								break;
							case -2:
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Trùng dữ liệu, chương trình đơn giá này đã được áp dụng cho khách hàng trên!','warning',2500);");
								break;

							default:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Thêm thất bại!','error',2000);");
								break;
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						if(allowUpdate(new Date())){
							customerPricingProgramCrud.setLast_modifed_by(account.getMember().getName());
							customerPricingProgramCrud.setLast_modifed_date(new Date());
							chk=customerPricingProgramService.update(t);
							switch (chk) {
							case 0:
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								listCustomerPricingProgram.set(listCustomerPricingProgram.indexOf(customerPricingProgramCrud),customerPricingProgramCrud.clone());
								break;
							case -2:
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Trùng dữ liệu, chương trình đơn giá này đã được áp dụng cho khách hàng trên!','warning',2500);");
								break;

							default:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Cập nhật thất bại!','error',2000);");
								break;
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ, điền đủ đầy thông tin chứa(*)','warning',2500);");
				}
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.saveOrUpdateCustomerPricingProgram:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdateCustomerPricingProgramSet(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(customerPricingProgramSetCrud !=null && pricingProgramCrud !=null){
				customerPricingProgramSetCrud.setPricing_program(pricingProgramCrud);
				Customer cs=customerPricingProgramSetCrud.getCustomer();
				Date eff=customerPricingProgramSetCrud.getEffective_date();
				if( cs !=null && eff !=null){
					//wrap containner
					CustomerPricingProgramReqInfo t=new CustomerPricingProgramReqInfo(customerPricingProgramSetCrud);
					int chk=0;
					if(customerPricingProgramSetCrud.getId()==0){
						if(allowSave(new Date())){
							customerPricingProgramSetCrud.setCreated_by(account.getMember().getName());
							customerPricingProgramSetCrud.setCreated_date(new Date());
							chk=customerPricingProgramService.insert(t);
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						if(allowUpdate(new Date())){
							customerPricingProgramSetCrud.setLast_modifed_by(account.getMember().getName());
							customerPricingProgramSetCrud.setLast_modifed_date(new Date());
							chk=customerPricingProgramService.update(t);
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}
					switch (chk) {
					case 0:
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Thành công!','success',2000);");
						break;
					case -2:
						current.executeScript(
								"swaldesigntimer('Cảnh báo', 'Trùng dữ liệu, chương trình đơn giá này đã được áp dụng cho khách hàng trên!','warning',2500);");
						break;
						
					default:
						current.executeScript(
								"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
						break;
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ, điền đủ đầy thông tin chứa(*)!','warning',2500);");
				}
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.saveOrUpdateCustomerPricingProgramSet:"+e.getMessage(),e);
		}
	}
	public void deleteCustomerPricingProgramSet(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(customerPricingProgramSetSelect !=null){
				//delete select;
				if(allowDelete(new Date())){
					if(customerPricingProgramService.deleteById(customerPricingProgramSetSelect.getId())>0){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Thành công!','success',2000);");
						listCustomerPricingProgramSet.remove(customerPricingProgramSetSelect);
					}else{
						current.executeScript(
								"swaldesigntimer('Thất bại!', 'Không xóa được!','warning',2000);");
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
				}
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.deleteCustomerPricingProgramSet:"+e.getMessage(),e);
		}
	}
	public void deleteCustomerPricingProgram(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(customerPricingProgramSelect !=null){
				//delete select;
				if(allowDelete(new Date())){
					if(customerPricingProgramService.deleteById(customerPricingProgramSelect.getId())>0){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Thành công!','success',2000);");
						listCustomerPricingProgram.remove(customerPricingProgramSelect);
					}else{
						current.executeScript(
								"swaldesigntimer('Thất bại!', 'Không xóa được!','warning',2000);");
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
				}
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.deleteCustomerPricingProgram:"+e.getMessage(),e);
		}
	}
	public void showEdit(){
		try{
			pricingProgramCrud=pricingProgramSelect.clone();
			pricingProgramDetailCrud=new PricingProgramDetail();
			//init detail
			listPricingProgramDetail=new ArrayList<PricingProgramDetail>();
			product2=null;
			search2();
			//init danh sách khách hàng đã được cài đặt.
			listCustomerPricingProgramSet=new ArrayList<>();
			customerPricingProgramService.selectAll(pricingProgramSelect.getId(), listCustomerPricingProgramSet);
		}catch(Exception e){
			logger.error("PricingProgramBean.showDialogEdit:"+e.getMessage(),e);
		}
	}
	//khi select row
	public void showEditCustomerPricingProgram(){
		try{
			customerPricingProgramCrud=customerPricingProgramSelect.clone();
		}catch(Exception e){
			logger.error("PricingProgramBean.showEditCustomerPricingProgram:"+e.getMessage(),e);
		}
	}
	// show dialog khi dùng double click
	public void showDbClickCustomerPricingProgram(){
		PrimeFaces current=PrimeFaces.current();
		try{
			customerPricingProgramCrud=customerPricingProgramSelect.clone();
			current.executeScript("PF('dlg3').show();");
		}catch(Exception e){
			logger.error("PricingProgramBean.showDbClickCustomerPricingProgram:"+e.getMessage(),e);
		}
	}
	public void showDialogAddSettings(){
		PrimeFaces current=PrimeFaces.current();
		try{
			customerPricingProgramCrud=new CustomerPricingProgram();
			current.executeScript("PF('dlg3').show();");
		}catch(Exception e){
			logger.error("PricingProgramBean.showDialogAddSettings:"+e.getMessage(),e);
		}
	}
	public void changeNgay(){
		try{
			PricingProgram pg=customerPricingProgramCrud.getPricing_program();
			if(pg !=null){
				customerPricingProgramCrud.setEffective_date(pg.getEffective_date());
				customerPricingProgramCrud.setExpiry_date(pg.getExpiry_date());
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.changeNgay:"+e.getMessage(),e);
		}
	}
	public void createdNew(){
		try{
			pricingProgramCrud=new PricingProgram();
			String code=pricingProgramService.initPricingProgramCode();
			pricingProgramCrud.setProgram_code(code);
			pricingProgramCrud.setEffective_date(new Date());
			pricingProgramDetailCrud=new PricingProgramDetail();
			listPricingProgramDetail=new ArrayList<PricingProgramDetail>();
		}catch(Exception e){
			logger.error("PricingProgramBean.createdNew:"+e.getMessage(),e);
		}
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(pricingProgramSelect !=null){
				if(allowDelete(new Date())){
					if(pricingProgramService.deleteById(pricingProgramSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listPricingProgram.remove(pricingProgramSelect);
						createdNew();
					}else{
						current.executeScript(
								"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
				}
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xóa!','warning',2000);");
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.delete:"+e.getMessage(),e);
		}
	}
	public void showDialogDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(pricingProgramCrud!=null && pricingProgramCrud.getId()!=0){
				pricingProgramDetailCrud=new PricingProgramDetail();
				pricingProgramDetailCrud.setPricing_program(pricingProgramCrud);
				current.executeScript("PF('dlg1').show();");
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Chương trình đơn giá không tồn tại, vui lòng chọn một chương trình đơn giá!','warning',2000);");
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.showDialogDetail:"+e.getMessage(),e);
		}
	}
	public void showDialogEditDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(pricingProgramDetailSelect!=null){
				pricingProgramDetailCrud=pricingProgramDetailSelect.clone();
				current.executeScript("PF('dlg1').show();");
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.showDialogEditDetail:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdateDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(pricingProgramDetailCrud !=null && pricingProgramCrud !=null && pricingProgramCrud.getId() !=0){
				Product product=pricingProgramDetailCrud.getProduct();
				double unitPrice=pricingProgramDetailCrud.getUnit_price();
				if(product !=null && unitPrice !=0.0){
					PricingProgramDetailReqInfo t=new PricingProgramDetailReqInfo(pricingProgramDetailCrud);
					if(pricingProgramDetailCrud.getId()==0){
						if(allowSave(new Date())){
							pricingProgramDetailCrud.setCreated_date(new Date());
							pricingProgramDetailCrud.setCreated_by(account.getMember().getName());
							int check=pricingProgramDetailService.insert(t);
							if(check==0){
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
								listPricingProgramDetail.add(0, pricingProgramDetailCrud);
								createNewDetail();
							}else if(check>0){
								current.executeScript(
										"swaldesigntimer('Cảnh báo!', 'Chi tiết chương trình đơn giá đã tồn tại !','warning',2000);");
							}else{
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Lưu thất bại!','error',2000);");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						if(allowUpdate(new Date())){
							pricingProgramDetailCrud.setLast_modifed_by(account.getMember().getName());
							pricingProgramDetailCrud.setLast_modifed_date(new Date());
							int check=pricingProgramDetailService.update(t);
							if(check !=-1){
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								listPricingProgramDetail.set(listPricingProgramDetail.indexOf(pricingProgramDetailCrud),pricingProgramDetailCrud);
							}else if(check>0){
								current.executeScript(
										"swaldesigntimer('Cảnh báo!', 'Chi tiết chương trình đơn giá đã tồn tại !','warning',2000);");
							}else{
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin chi tiết chương trình đơn giá không đầy đủ, điền đủ thông tin chứa(*)!','warning',2000);");
				}
			}else{
				current.executeScript(
						"swaldesigntimer('Thất bại!', 'Thông tin không đầy đủ hoặc chương trình đơn giá không tồn tại!','error',2000);");
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.saveOrUpdateDetail:"+e.getMessage(),e);
		}
	}
	private void createNewDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(pricingProgramCrud !=null && pricingProgramCrud.getId()!=0){
				pricingProgramDetailCrud=new PricingProgramDetail();
				pricingProgramDetailCrud.setPricing_program(pricingProgramCrud);
			}else{
				current.executeScript("PF('dlg1').hide();");
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.createNewDetail:"+e.getMessage(),e);
		}
	}
	public void exprotExcel(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if (listPricingProgramDetail != null && listPricingProgramDetail.size() > 0) {
				List<Object[]> results = new ArrayList<Object[]>();
				Object[] title = { "masoctdg","masoctdgc","tungay","denngay","ghichu","masp","tensp","soluong","dongia","loinhuan"};
				results.add(title);
				String masoctdh=pricingProgramCrud.getProgram_code();
				String masoctdgc=pricingProgramCrud.getParent_pricing_program() == null ? "" :pricingProgramCrud.getParent_pricing_program().getProgram_code();
				String tungay=ToolTimeCustomer.convertDateToString(pricingProgramCrud.getEffective_date(),"dd/MM/yyyy");
				String denngay= pricingProgramCrud.getExpiry_date()==null ? "" : ToolTimeCustomer.convertDateToString(pricingProgramCrud.getExpiry_date(),"dd/MM/yyyy");
				String ghichu=pricingProgramCrud.getNote() == null ? "" : pricingProgramCrud.getNote();
				for (PricingProgramDetail it : listPricingProgramDetail) {
					Object[] row = {masoctdh,masoctdgc,tungay,denngay,ghichu,it.getProduct().getProduct_code(),it.getProduct().getProduct_name(),it.getQuantity(),it.getUnit_price(),it.getRevenue_per_ton()};
					results.add(row);
				}
				ToolReport.printReportExcelRaw(results,
						"chi_tiet_chuong_trinh_don_gia_"+masoctdh);
			} else {
				notify.message("Không có dữ liệu");
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.exprotExcel:"+e.getMessage(),e);
		}
	}
	public void exportPDF(){
		PrimeFaces current = PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (listPricingProgramDetail != null && listPricingProgramDetail.size() > 0) {
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/pinbaogia.jasper");
				Map<String, Object> importParam = new HashMap<String, Object>();
				importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/gfx/lixco_logo.png"));
				List<ParamReportDetail> listConfig=paramReportDetailService.findByParamReports_param_name("inbaogia");
				for(ParamReportDetail p:listConfig){
					importParam.put(p.getKey(), p.getValue());
				}
				int day=ToolTimeCustomer.getDayM(ngaybaogia);
				int month=ToolTimeCustomer.getMonthM(ngaybaogia);
				int year=ToolTimeCustomer.getYearM(ngaybaogia);
				String ngay=day==-1 ? "00" : String.format("%02d",day);
				String thang=month==-1 ? "00" : String.format("%02d",month);
				String nam=year==-1 ? "0000" : String.format("%04d", year);
				importParam.put("title_ngaybaogia", "TP. Hồ Chí Minh, Ngày "+ngay+" tháng "+thang+" năm "+nam);
				String tgad="từ "+ToolTimeCustomer.convertDateToStringShort(pricingProgramCrud.getEffective_date())+" đến "+(pricingProgramCrud.getExpiry_date() == null ? "" :
					ToolTimeCustomer.convertDateToStringShort(pricingProgramCrud.getExpiry_date()));
				importParam.put("tg_ap_dung",tgad);
				importParam.put("baogiaso", baogiaso);
				importParam.put("tenkhachhang", tenkhachhang);
				JRDataSource beanDataSource = new JRBeanCollectionDataSource(listPricingProgramDetail);
				importParam.put("listData", beanDataSource);
				JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
				byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
				String ba = Base64.getEncoder().encodeToString(data);
				current.executeScript("utility.printPDF('" + ba + "')");
				// InputStream stream = new ByteArrayInputStream(data);
				// StreamedContent media = new DefaultStreamedContent(stream,
				// "application/pdf");
				// SessionHelper.getInstance().<StreamedContent>setSession("mediadelivery",
				// media, request);
				// current.executeScript("PF('showpdf').show();");
			} else {
				notify.message("Không có dữ liệu");
			}
		} catch (Exception e) {
			logger.error("SupervisionBean.printPDF:" + e.getMessage(), e);
		}
	}
	public void copyCTDG(){
		try{
			if(pricingProgramCrud !=null){
				pricingProgramCrud.setId(0);
				pricingProgramCrud.setProgram_code(pricingProgramService.initPricingProgramCode());
				pricingProgramCrud.setNote(null);
				pricingProgramCrud.setCreated_by(null);
				pricingProgramCrud.setCreated_date(null);
				pricingProgramCrud.setLast_modifed_by(null);
				pricingProgramCrud.setLast_modifed_date(null);
			}
			for(PricingProgramDetail t:listPricingProgramDetail){
				t.setId(0);
				t.setPricing_program(null);
				t.setCreated_by(null);
				t.setCreated_date(null);
				t.setLast_modifed_by(null);
				t.setLast_modifed_date(null);
			}
			
		}catch(Exception e){
			logger.error("PricingProgramBean.copyCTDG:"+e.getMessage(),e);
		}
	}
	public void deleteDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(pricingProgramDetailSelect !=null){
				if(allowDelete(new Date())){
					if(pricingProgramDetailService.deleteById(pricingProgramDetailSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listPricingProgramDetail.remove(pricingProgramDetailSelect);
					}else{
						current.executeScript(
								"swaldesigntimer('Thất bại!', 'Không được xóa!','error',2000);");
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
				}
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo', 'Chọn một dòng chi tiết để xóa!','warning',2000);");
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.deleteDetail:"+e.getMessage(),e);
		}
	}
	public void viewFileExcelForm(){
		try{
			
		}catch(Exception e){
			logger.error("PricingProgramBean.viewFileExcelForm:"+e.getMessage(),e);
		}
	}
	public void showDialogUpload(){
		PrimeFaces current=PrimeFaces.current();
		try{
			current.executeScript("PF('dlgup1').show();");
		}catch(Exception e){
			logger.error("PricingProgramBean.showDialogUpload:"+e.getMessage(),e);
		}
	}
	public void showDialogUploadCDKH(){
		PrimeFaces current=PrimeFaces.current();
		try{
			current.executeScript("PF('dlgup2').show();");
		}catch(Exception e){
			logger.error("PricingProgramBean.showDialogUpload:"+e.getMessage(),e);
		}
	}
	public void loadExcelCTDG(FileUploadEvent event){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			UploadedFile part=event.getFile();
			if(part !=null){
				Workbook workBook = getWorkbook(new ByteArrayInputStream(part.getContents()),part.getFileName());
				Sheet firstSheet = workBook.getSheetAt(0);
				Iterator<Row> rows = firstSheet.iterator();
				Map<String, PricingProgram> map=new LinkedHashMap<String,PricingProgram>();
				while (rows.hasNext()) {
					rows.next();
					rows.remove();
					break;
				}
				lv1:while (rows.hasNext()) {
					Row row = rows.next();
					Iterator<Cell> cells = row.cellIterator();
					String mactdg=null;
					PricingProgramDetail detail=new PricingProgramDetail();
					while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try{
							    mactdg = Objects.toString(getCellValue(cell),null);
							    if(mactdg==null || !"".equals(mactdg)){
							    	if(!map.containsKey(mactdg)){
							    		PricingProgram program=new PricingProgram();
							    		program.setList_pricing_program_detail(new ArrayList<>());
							    		map.put(mactdg, program);
							    	}
							    	map.get(mactdg).setProgram_code(mactdg);
							    }else{
							    	continue lv1;
							    }
							}catch(Exception e){}
							break;
						case 1:
							try{
								String ngayHL=Objects.toString(getCellValue(cell));
								if(ngayHL !=null && !"".equals(ngayHL)){
									map.get(mactdg).setEffective_date(ToolTimeCustomer.convertStringToDate(ngayHL,"dd/MM/yyyy"));
								}else{
									continue lv1;
								}
							}catch(Exception e){
							}
							break;
						case 2:
							try {
								String ngayKT=Objects.toString(getCellValue(cell),null);
								map.get(mactdg).setExpiry_date(ToolTimeCustomer.convertStringToDate(ngayKT, "dd/MM/yyyy"));
							}catch(Exception e) {
							}
							break;
						case 3:
							try{
								String masp=Objects.toString(getCellValue(cell),null);
								if(masp !=null && !"".equals(masp)){
									ProductReqInfo pinfo=new ProductReqInfo();
									productService.selectByCode(masp, pinfo);
									if(pinfo.getProduct() !=null){
										detail.setProduct(pinfo.getProduct());
										map.get(mactdg).getList_pricing_program_detail().add(detail);
									}else{
										continue lv1;
									}
								}else{
									continue lv1;
								}
							}catch (Exception e) {
							}
							break;
						case 4:
							try{
								String dongia=Objects.toString(getCellValue(cell),"0");
								detail.setUnit_price(Double.parseDouble(dongia));
							}catch (Exception e) {
							}
							break;
						case 5:
							try{
								String soluong=Objects.toString(getCellValue(cell),"0");
								detail.setQuantity(Double.parseDouble(soluong));
							}catch (Exception e) {
							}
							break;
						}
						
					}
				}
				for (Map.Entry<String, PricingProgram> entry : map.entrySet()) {
					String key = entry.getKey();
					PricingProgram  value=entry.getValue();
					// check key đã tồn tại chưa.
					PricingProgramReqInfo ppr = new PricingProgramReqInfo();
					pricingProgramService.selectByCode(key, ppr);
					PricingProgram program=ppr.getPricing_program();
					List<PricingProgramDetail> listDetail=value.getList_pricing_program_detail();
					if (program != null) {
						program.setEffective_date(value.getEffective_date());
						program.setExpiry_date(value.getExpiry_date());
						//cập nhật lại chương trình đơn giá.
						pricingProgramService.update(new PricingProgramReqInfo(program));
						if (rewrite) {
							// xóa tất cả chi tiết chương trình đơn giá.
							pricingProgramDetailService.deleteAll(program.getId());
							for(PricingProgramDetail pd:listDetail){
								pd.setPricing_program(program);
								pd.setCreated_date(new Date());
								pd.setCreated_by(account.getMember().getName());
								//kiểm tra đã tồn tại sản phẩm trong chuong trình đơn giá đó chưa.
								if(pricingProgramDetailService.checkExsits(pd.getProduct().getId(), 0, program.getId()) ==0){
									pricingProgramDetailService.insert(new PricingProgramDetailReqInfo(pd));
								}else{
									pricingProgramDetailService.updateByPredicate(pd.getProduct().getId(), program.getId(),pd.getUnit_price(),pd.getRevenue_per_ton(), pd.getQuantity());
								}
							}
						}else{
							for(PricingProgramDetail pd:listDetail){
								pd.setPricing_program(program);
								pd.setCreated_date(new Date());
								pd.setCreated_by(account.getMember().getName());
								if(pricingProgramDetailService.checkExsits(pd.getProduct().getId(), 0, program.getId()) ==0){
									pricingProgramDetailService.insert(new PricingProgramDetailReqInfo(pd));
								}else{
									//cập nhật lại số lượng đơn giá
									pricingProgramDetailService.updateByPredicate(pd.getProduct().getId(), program.getId(),pd.getUnit_price(),pd.getRevenue_per_ton(), pd.getQuantity());
								}
							}
						}
					}else{
						//lưu chương tình đơn giá.
						value.setList_pricing_program_detail(null);
						value.setCreated_date(new Date());
						value.setCreated_by(account.getMember().getName());
						pricingProgramService.insert(new PricingProgramReqInfo(value));
						for(PricingProgramDetail pd:listDetail){
							pd.setPricing_program(value);
							pd.setCreated_date(new Date());
							pd.setCreated_by(account.getMember().getName());
							if(pricingProgramDetailService.checkExsits(pd.getProduct().getId(), 0, value.getId()) ==0){
								pricingProgramDetailService.insert(new PricingProgramDetailReqInfo(pd));
							}else{
								//cập nhật lại số lượng đơn giá
								pricingProgramDetailService.updateByPredicate(pd.getProduct().getId(),value.getId(),pd.getUnit_price(),pd.getRevenue_per_ton(), pd.getQuantity());
							}
						}
					}
				}
				notify.success();
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.loadExcelCTDG:"+e.getMessage(),e);
		}
	}
	public void loadExcelCDCTDG(FileUploadEvent event){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		PrimeFaces current=PrimeFaces.current();
		try{
			UploadedFile part=event.getFile();
			if(part !=null){
				Workbook workBook = getWorkbook(new ByteArrayInputStream(part.getContents()),part.getFileName());
				Sheet firstSheet = workBook.getSheetAt(0);
				Iterator<Row> rows = firstSheet.iterator();
				List<CustomerPricingProgram> listCustomerPricingProgram=new ArrayList<>();
				while (rows.hasNext()) {
					rows.next();
					rows.remove();
					break;
				}
				lv1:while (rows.hasNext()) {
					Row row = rows.next();
					Iterator<Cell> cells = row.cellIterator();
					CustomerPricingProgram lix=new CustomerPricingProgram();
					while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();
						
						switch (columnIndex) {
						case 0:
							try{
								//ma khach hang
								String makh = Objects.toString(getCellValue(cell),null);
								if(makh==null || !"".equals(makh)){
									CustomerReqInfo c=new CustomerReqInfo();
									customerService.selectByCode(makh, c);
									if(c.getCustomer() !=null){
										lix.setCustomer(c.getCustomer());
									}else{
										// Thông báo mã không hợp lệ
										current.executeScript(
												"swaldesigntimer('Cảnh báo!', 'Mã khách hàng:"+makh+" không tồn tại','error',2000);");
										return;
									}
								}else{
									continue lv1;
								}
							}catch(Exception e){}
							break;
						case 1:
							try{
								String mact=Objects.toString(getCellValue(cell));
								if(mact !=null && !"".equals(mact)){
									PricingProgramReqInfo tp=new PricingProgramReqInfo();
								    pricingProgramService.selectByCode(mact,tp);
								    if(tp.getPricing_program() !=null){
								    	lix.setPricing_program(tp.getPricing_program());
								    }else{
								    	// Thông báo mã không hợp lệ
										current.executeScript(
												"swaldesigntimer('Cảnh báo!', 'Mã chương trình:"+mact+" không tồn tại','error',2000);");
										return;
								    }
								}else{
									continue lv1;
								}
							}catch(Exception e){
							}
							break;
						case 2:
							try{
								String ngayHL=Objects.toString(getCellValue(cell));
								if(ngayHL !=null && !"".equals(ngayHL)){
									Date ngayDD=ToolTimeCustomer.convertStringToDate(ngayHL,"dd/MM/yyyy");
									if(ngayDD !=null){
										lix.setEffective_date(ngayDD);
									}else{
										continue lv1;
									}
								}else{
									continue lv1;
								}
							}catch(Exception e){
							}
							break;
						case 3:
							try{
								String ngayKT=Objects.toString(getCellValue(cell));
								if(ngayKT !=null && !"".equals(ngayKT)){
									Date ngayKTDD=ToolTimeCustomer.convertStringToDate(ngayKT,"dd/MM/yyyy");
									if(ngayKTDD !=null){
										lix.setExpiry_date(ngayKTDD);
									}
								}
							}catch(Exception e){
							}
							break;
						}
						
					}
					listCustomerPricingProgram.add(lix);
				}
				for(CustomerPricingProgram it:listCustomerPricingProgram){
					it.setCreated_by(account.getMember().getName());
					it.setCreated_date(new Date());
					if(customerPricingProgramService.insert(new CustomerPricingProgramReqInfo(it))!=-2){
						//silent
					}
				}
				notify.success();
				
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.loadExcelCTDG:"+e.getMessage(),e);
		}
	}
	private Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();

		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();

		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue();
		}

		return null;
	}
	private Workbook getWorkbook(InputStream inputStream, String nameFile) throws IOException {
		Workbook workbook = null;
		if (nameFile.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (nameFile.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}

		return workbook;
	}
	public List<Product> completeProduct(String text){
		try{
			List<Product> list=new ArrayList<Product>();
			productService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("PricingProgramBean.completeProduct:"+e.getMessage(),e);
		}
		return null;
	}
	public List<PricingProgram> completePricingProgram(String text){
		try{
			List<PricingProgram> list=new ArrayList<PricingProgram>();
			pricingProgramService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("PricingProgramBean.completePricingProgram:"+e.getMessage(),e);
		}
		return null;
	}
	public List<CustomerTypes> completeCustomerTypes(String text){
		try{
			List<CustomerTypes> list=new  ArrayList<CustomerTypes>();
			customerTypesService.findLike(FormatHandler.getInstance().converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("PricingProgramBean.autoComplete:"+e.getMessage(),e);
		}
		return null;
	}
	public void showDialogSetting(){
		PrimeFaces current=PrimeFaces.current();
		try{
			current.executeScript("PF('dlg2').show();");
			
		}catch(Exception e){
			logger.error("PricingProgramBean.showDialogCustomerPricingProgram:"+e.getMessage(),e);
		}
	}
	public void showDialogInBaoGia(){
		PrimeFaces current=PrimeFaces.current();
		try{
			baogiaso=null;
			baogiasogoc=null;
			ngaybaogia=new Date();
			current.executeScript("PF('dlg4').show();");
		}catch(Exception e){
			logger.error("PricingProgramBean.showDialogInBaoGia:"+e.getMessage(),e);
		}
	}
	public void inBaoGia(){
		try{
			
		}catch(Exception e){
			logger.error("PricingProgramBean.inBaoGia:"+e.getMessage(),e);
		}
	}
	public void innitSourceCustomer(){
		try{
			if(customerTypesSetting !=null){
				List<Customer> list=new ArrayList<Customer>();
				customerService.selectAllByCustomerTypes(customerTypesSetting!=null ?customerTypesSetting.getId() :0, list);
				list.removeAll(modelCustomerSetting.getTarget());
				modelCustomerSetting.setSource(list);
			}
			
		}catch(Exception e){
			logger.error("PricingProgramBean.innitTagertCustomer:"+e.getMessage(),e);
		}
	}
	
	public void onTranfer(TransferEvent event){
		try{
//			if(event.isAdd()){
//				modelCustomerSetting.getTarget().addAll((List<Customer>) event.getItems());
//			}else{
//				modelCustomerSetting.getSource().addAll((List<Customer>) event.getItems());
//				
//			}
		}catch(Exception e){
			logger.error("PricingProgramBean.onTranfer:"+e.getMessage(),e);
		}
	}
	public void searchCustomerSetting(){
		try{
			if(textSearch !=null && !"".equals(textSearch)){
				List<Customer> list=new ArrayList<Customer>();
				customerService.complete(formatHandler.converViToEn(textSearch), customerTypesSetting, list);
				list.removeAll(modelCustomerSetting.getTarget());
				modelCustomerSetting.setSource(list);
			} 
		}catch(Exception e){
			logger.error("PricingProgramBean.searchCustomer:"+e.getMessage(),e);
		}
	}
	public List<Customer> completeCustomerForCustomerPricing(String text){
		try{
			List<Customer> list=new ArrayList<>();
		    customerService.complete(formatHandler.converViToEn(text), customerTypesSearch3, list);
		    return list;
		}catch(Exception e){
			logger.error("PricingProgramBean.completeCustomerByType:"+e.getMessage(),e);
		}
		return null;
		
	}
	public List<Customer> completeCustomer(String text){
		try{
			List<Customer> list=new ArrayList<>();
			customerService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch (Exception e) {
			logger.error("PricingProgramBean.completeCustomer:"+e.getMessage(),e);
		}
		return null;
	}
	public void settingCustomers(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(allowSave(new Date())){
				List<Customer> listCustomer=modelCustomerSetting.getTarget();
				if(listCustomer !=null && listCustomer.size()>0 && pricingProgramSetting !=null){
					int[] a={0,0,0};
					for(Customer c:listCustomer){
						CustomerPricingProgram cp=new CustomerPricingProgram();
						cp.setCustomer(c);
						cp.setPricing_program(pricingProgramSetting);
						cp.setCreated_by(account.getMember().getName());
						cp.setCreated_date(new Date());
						cp.setEffective_date(pricingProgramSetting.getEffective_date());
						cp.setExpiry_date(pricingProgramSetting.getExpiry_date());
						CustomerPricingProgramReqInfo t=new CustomerPricingProgramReqInfo();
						t.setCustomer_pricing_program(cp);//wrap container
						int chk=customerPricingProgramService.insert(t);
						switch (chk) {
							case 0:
								a[0]++;
								break;
							case -2:
								a[1]++;
								break;
							default:
								a[2]++;
								break;
						}
					
					}
					searchCustomerPricingProgram();
					StringBuilder noidung=new StringBuilder();
					noidung.append("Thông tin cài đặt CTDG:"+pricingProgramSetting.getProgram_code());
					noidung.append("<br/>Khách hàng cài đặt thành công: <b style=\"color:red;\">"+a[0]+"</b>");
					noidung.append("<br/>Khách hàng đã được cài đặt trước đó(trùng): <b style=\"color:red;\">"+a[1]+"</b>");
					noidung.append("<br/>Khách hàng cài đặt thất bại: <b style=\"color:red;\">"+a[2]+"</b>");
					current.executeScript("swaldesignclose('Thông báo','"+noidung.toString()+"','info');");
					
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ, điền thông tin chứa(*)','warning',2000);");
				}
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Tài khoản này không được phân quyền thực hiện hoặc tháng đã khoá!','error',2000);");
			}
		}catch (Exception e) {
			logger.error("PricingProgramBean.settingCustomers:"+e.getMessage(),e);
		}
	}
	public void showDialogUploadExcel(){
		try{
			
		}catch(Exception e){
			logger.error("PricingProgramBean.showDialogUploadExcel:"+e.getMessage(),e);
		}
	}
	public void showDialogEditCustomerPricingProgramSet(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(customerPricingProgramSetSelect !=null && pricingProgramCrud !=null && pricingProgramCrud.getId() !=0){
				customerPricingProgramSetCrud=customerPricingProgramSetSelect.clone();
				current.executeScript("PF('dlgset3').show();");
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo', 'Chương trình đơn giá không tồn tại','warning',2000);");
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.showDialogEditCustomerPricingProgramSet:"+e.getMessage(),e);
		}
	}
	public void showDialogCustomerPricingProgramSet(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(pricingProgramCrud !=null && pricingProgramCrud.getId() !=0){
				customerPricingProgramSetCrud=new CustomerPricingProgram();
				customerPricingProgramSetCrud.setPricing_program(pricingProgramCrud);
				customerPricingProgramSetCrud.setEffective_date(pricingProgramCrud.getEffective_date());
				customerPricingProgramSetCrud.setExpiry_date(pricingProgramCrud.getExpiry_date());
				current.executeScript("PF('dlgset3').show();");
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo', 'Chương trình đơn giá không tồn tại','warning',2000);");
			}
		}catch(Exception e){
			logger.error("PricingProgramBean.showDialogCustomerPricingProgramSet:"+e.getMessage(),e);
		}
	}
	
	public PricingProgram getPricingProgramCrud() {
		return pricingProgramCrud;
	}

	public void setPricingProgramCrud(PricingProgram pricingProgramCrud) {
		this.pricingProgramCrud = pricingProgramCrud;
	}

	public PricingProgram getPricingProgramSelect() {
		return pricingProgramSelect;
	}

	public void setPricingProgramSelect(PricingProgram pricingProgramSelect) {
		this.pricingProgramSelect = pricingProgramSelect;
	}

	public List<PricingProgram> getListPricingProgram() {
		return listPricingProgram;
	}

	public void setListPricingProgram(List<PricingProgram> listPricingProgram) {
		this.listPricingProgram = listPricingProgram;
	}

	public PricingProgramDetail getPricingProgramDetailCrud() {
		return pricingProgramDetailCrud;
	}

	public void setPricingProgramDetailCrud(PricingProgramDetail pricingProgramDetailCrud) {
		this.pricingProgramDetailCrud = pricingProgramDetailCrud;
	}

	public PricingProgramDetail getPricingProgramDetailSelect() {
		return pricingProgramDetailSelect;
	}

	public void setPricingProgramDetailSelect(PricingProgramDetail pricingProgramDetailSelect) {
		this.pricingProgramDetailSelect = pricingProgramDetailSelect;
	}

	public List<PricingProgramDetail> getListPricingProgramDetail() {
		return listPricingProgramDetail;
	}

	public void setListPricingProgramDetail(List<PricingProgramDetail> listPricingProgramDetail) {
		this.listPricingProgramDetail = listPricingProgramDetail;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getProgramCode() {
		return programCode;
	}

	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}

	public String getMainProductCode() {
		return mainProductCode;
	}

	public void setMainProductCode(String mainProductCode) {
		this.mainProductCode = mainProductCode;
	}

	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public PricingProgram getParentPricingProgram() {
		return parentPricingProgram;
	}

	public void setParentPricingProgram(PricingProgram parentPricingProgram) {
		this.parentPricingProgram = parentPricingProgram;
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

	public int getPageSize2() {
		return pageSize2;
	}

	public void setPageSize2(int pageSize2) {
		this.pageSize2 = pageSize2;
	}

	public NavigationInfo getNavigationInfo2() {
		return navigationInfo2;
	}

	public void setNavigationInfo2(NavigationInfo navigationInfo2) {
		this.navigationInfo2 = navigationInfo2;
	}

	public List<Integer> getListRowPerPage2() {
		return listRowPerPage2;
	}

	public void setListRowPerPage2(List<Integer> listRowPerPage2) {
		this.listRowPerPage2 = listRowPerPage2;
	}

	public Product getProduct2() {
		return product2;
	}

	public void setProduct2(Product product2) {
		this.product2 = product2;
	}

	public FormatHandler getFormatHandler() {
		return formatHandler;
	}

	public void setFormatHandler(FormatHandler formatHandler) {
		this.formatHandler = formatHandler;
	}

	public List<CustomerTypes> getListCustomerTypes() {
		return listCustomerTypes;
	}

	public void setListCustomerTypes(List<CustomerTypes> listCustomerTypes) {
		this.listCustomerTypes = listCustomerTypes;
	}

	public CustomerPricingProgram getCustomerPricingProgramCrud() {
		return customerPricingProgramCrud;
	}

	public void setCustomerPricingProgramCrud(CustomerPricingProgram customerPricingProgramCrud) {
		this.customerPricingProgramCrud = customerPricingProgramCrud;
	}

	public CustomerPricingProgram getCustomerPricingProgramSelect() {
		return customerPricingProgramSelect;
	}

	public void setCustomerPricingProgramSelect(CustomerPricingProgram customerPricingProgramSelect) {
		this.customerPricingProgramSelect = customerPricingProgramSelect;
	}

	public List<CustomerPricingProgram> getListCustomerPricingProgram() {
		return listCustomerPricingProgram;
	}

	public void setListCustomerPricingProgram(List<CustomerPricingProgram> listCustomerPricingProgram) {
		this.listCustomerPricingProgram = listCustomerPricingProgram;
	}

	public int getPageSize3() {
		return pageSize3;
	}

	public void setPageSize3(int pageSize3) {
		this.pageSize3 = pageSize3;
	}

	public NavigationInfo getNavigationInfo3() {
		return navigationInfo3;
	}

	public void setNavigationInfo3(NavigationInfo navigationInfo3) {
		this.navigationInfo3 = navigationInfo3;
	}

	public List<Integer> getListRowPerPage3() {
		return listRowPerPage3;
	}

	public void setListRowPerPage3(List<Integer> listRowPerPage3) {
		this.listRowPerPage3 = listRowPerPage3;
	}

	public CustomerTypes getCustomerTypesSetting() {
		return customerTypesSetting;
	}

	public void setCustomerTypesSetting(CustomerTypes customerTypesSetting) {
		this.customerTypesSetting = customerTypesSetting;
	}

	public DualListModel<Customer> getModelCustomerSetting() {
		return modelCustomerSetting;
	}

	public void setModelCustomerSetting(DualListModel<Customer> modelCustomerSetting) {
		this.modelCustomerSetting = modelCustomerSetting;
	}

	public PricingProgram getPricingProgramSetting() {
		return pricingProgramSetting;
	}

	public void setPricingProgramSetting(PricingProgram pricingProgramSetting) {
		this.pricingProgramSetting = pricingProgramSetting;
	}

	public String getTextSearch() {
		return textSearch;
	}

	public void setTextSearch(String textSearch) {
		this.textSearch = textSearch;
	}

	public CustomerTypes getCustomerTypesSearch3() {
		return customerTypesSearch3;
	}

	public void setCustomerTypesSearch3(CustomerTypes customerTypesSearch3) {
		this.customerTypesSearch3 = customerTypesSearch3;
	}

	public String getProgramCodeSearch3() {
		return programCodeSearch3;
	}

	public void setProgramCodeSearch3(String programCodeSearch3) {
		this.programCodeSearch3 = programCodeSearch3;
	}

	public Customer getCustomerSearch3() {
		return customerSearch3;
	}

	public void setCustomerSearch3(Customer customerSearch3) {
		this.customerSearch3 = customerSearch3;
	}

	public Date getFromDateSearch3() {
		return fromDateSearch3;
	}

	public void setFromDateSearch3(Date fromDateSearch3) {
		this.fromDateSearch3 = fromDateSearch3;
	}

	public Date getToDateSearch3() {
		return toDateSearch3;
	}

	public void setToDateSearch3(Date toDateSearch3) {
		this.toDateSearch3 = toDateSearch3;
	}

	public String getBaogiaso() {
		return baogiaso;
	}

	public void setBaogiaso(String baogiaso) {
		this.baogiaso = baogiaso;
	}

	public String getBaogiasogoc() {
		return baogiasogoc;
	}

	public void setBaogiasogoc(String baogiasogoc) {
		this.baogiasogoc = baogiasogoc;
	}

	public String getTenkhachhang() {
		return tenkhachhang;
	}

	public void setTenkhachhang(String tenkhachhang) {
		this.tenkhachhang = tenkhachhang;
	}

	public Date getNgaybaogia() {
		return ngaybaogia;
	}

	public void setNgaybaogia(Date ngaybaogia) {
		this.ngaybaogia = ngaybaogia;
	}

	public boolean isRewrite() {
		return rewrite;
	}

	public void setRewrite(boolean rewrite) {
		this.rewrite = rewrite;
	}

	public List<CustomerPricingProgram> getListCustomerPricingProgramSet() {
		return listCustomerPricingProgramSet;
	}

	public void setListCustomerPricingProgramSet(List<CustomerPricingProgram> listCustomerPricingProgramSet) {
		this.listCustomerPricingProgramSet = listCustomerPricingProgramSet;
	}

	public CustomerPricingProgram getCustomerPricingProgramSetSelect() {
		return customerPricingProgramSetSelect;
	}

	public void setCustomerPricingProgramSetSelect(CustomerPricingProgram customerPricingProgramSetSelect) {
		this.customerPricingProgramSetSelect = customerPricingProgramSetSelect;
	}

	public CustomerPricingProgram getCustomerPricingProgramSetCrud() {
		return customerPricingProgramSetCrud;
	}

	public void setCustomerPricingProgramSetCrud(CustomerPricingProgram customerPricingProgramSetCrud) {
		this.customerPricingProgramSetCrud = customerPricingProgramSetCrud;
	}
}
