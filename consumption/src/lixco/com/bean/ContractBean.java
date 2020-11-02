package lixco.com.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
import lixco.com.entity.Contract;
import lixco.com.entity.ContractDetail;
import lixco.com.entity.ContractReqInfo;
import lixco.com.entity.Currency;
import lixco.com.entity.Customer;
import lixco.com.entity.Product;
import lixco.com.entity.VoucherPayment;
import lixco.com.interfaces.IContractService;
import lixco.com.interfaces.ICurrencyService;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.IInvoicePosService;
import lixco.com.interfaces.IProductService;
import lixco.com.interfaces.IVoucherPaymentService;
import lixco.com.reqInfo.ContractDetailReqInfo;
import lixco.com.reqInfo.ProcessContract;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;
import trong.lixco.com.util.ToolReport;

@Named
@ViewScoped
public class ContractBean extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private IContractService contractService;
	@Inject
	private ICustomerService customerService;
	@Inject
	private Logger logger;
	@Inject
	private ICurrencyService currencyService;
	@Inject
	private IProductService productService;
	@Inject
	private IInvoicePosService invoicePosService;
	@Inject
	private IVoucherPaymentService voucherPaymentService;
	private Contract contractCrud;
	private Contract contractSelect;
	private List<Contract> listContract;
	private ContractDetail contractDetailCrud;
	private ContractDetail contractDetailSelect;
	private List<ContractDetail> listContractDetail;
	private List<ContractDetail> listContractDetailFillter;
	private List<Currency> listCurrency;
	private List<ProcessContract> listProcessContract;
	/*search*/
	private Customer customerSearch;
	private String voucherCodeSearch;
	private Date fromDateSearch;
	private Date toDateSearch;
	private int liquidatedSearch;
	
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	private Account account;
	private FormatHandler formatHandler;
	
	@Override
	protected void initItem() {
		try{
			formatHandler=FormatHandler.getInstance();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			//init contract crud
			contractCrud=new Contract();
			contractCrud.setContract_date(new Date());
			int year=ToolTimeCustomer.getYearCurrent();
			fromDateSearch=ToolTimeCustomer.convertStringToDate("01/01/"+year,"dd/MM/yyyy");
			toDateSearch=ToolTimeCustomer.convertStringToDate("01/12/"+year,"dd/MM/yyyy");
			liquidatedSearch=-1;
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			search();
			listCurrency=new ArrayList<>();
			currencyService.selectAll(listCurrency);
			createNew();
		}catch (Exception e) {
			logger.error("ContractBean.initItem:"+e.getMessage(),e);
		}
	}
	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void updateContractDetail(ContractDetail item){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(item!=null && item.getId() !=0){
				if(allowUpdate(new Date())){
					item.setLast_modifed_by(account.getMember().getName());
					item.setLast_modifed_date(new Date());
					if(contractService.updateDetail(new ContractDetailReqInfo(item))==0){
						notify.success("Thành công!");
					}else{
						notify.warning("Không cập nhật được trạng thái!");
					}
				}else{
					notify.warning("Tài khoản chưa được phân quyền!");
				}
			}
		}catch (Exception e) {
			logger.error("ContractBean.updateContractDetail:"+e.getMessage(),e);
		}
	}
	public void updateContract(Contract item){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(allowUpdate(new Date())){
				item.setLast_modifed_by(account.getMember().getName());
				item.setLast_modifed_date(new Date());
				if(contractService.update(new ContractReqInfo(item))==0){
					notify.success("Thành công!");
				}else{
					notify.warning("Không cập nhật được trạng thái!");
				}
			}else{
				notify.warning("Tài khoản chưa được phân quyền!");
			}
		}catch (Exception e) {
			logger.error("ContractBean.updateContract:"+e.getMessage(),e);
		}
	}
	public void deleteContract(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(contractSelect !=null && contractSelect.getId()!=0){
				if(allowDelete(new Date())){
					if(contractService.deleteById(contractSelect.getId())>0){
						listContract.remove(contractSelect);
						notify.success("Xóa thành công!");
					}else{
						notify.warning("Xóa thất bại!");
					}
 				}else{
					notify.warning("Tài khoản chưa được phân quyền!");
				}
			}else{
				notify.warning("Chưa chọn dòng để xóa!");
			}
		}catch (Exception e) {
			logger.error("ContractBean.deleteContract:"+e.getMessage(),e);
		}
	}
	public void deleteContactDetail(ContractDetail item){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(item !=null && item.getId()!=0){
				if(allowDelete(new Date())){
					if(contractService.deleteDetailById(item.getId())>0){
						listContractDetail.remove(item);
						notify.success("Xóa thành công!");
					}else{
						notify.warning("Xóa thất bại!");
					}
				}else{
					notify.warning("Tài khoản chưa được phân quyền!");
				}
			}else{
				notify.warning("Chưa chọn dòng chi tiết để xóa!");
			}
		}catch (Exception e) {
			logger.error("ContractBean.deleteContactDetail:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		try{
			contractCrud=new Contract();
			contractCrud.setVoucher_code(contractService.initVoucherCode());
			listContractDetail=new ArrayList<>();
		}catch (Exception e) {
			logger.error("ContractBean.createNew:"+e.getMessage(),e);
		}
	}
	public void exportExcel(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (contractCrud != null && contractCrud.getId()!=0 && listContractDetail !=null && listContractDetail.size()>0) {
				List<Object[]> results = new ArrayList<Object[]>();
				Object[] title = { "SOHD", "NGÀY HIỆU LỰC","NGÀY HẾT HẠN","MAKH","TÊN KHÁCH HÀNG","MASP","TÊN SẢN PHẨM", "KHUYẾN MÃI", "CKMAI", "SỐ LƯỢNG HĐ",
						"SỐ LƯỢNG KG", "ĐÃ THỰC HIỆN","CÒN LẠI","ĐƠN GIÁ","SỐ TIỀN" };
				results.add(title);
				String voucherCode=contractCrud.getVoucher_code();
				String effectiveDate=ToolTimeCustomer.convertDateToString(contractCrud.getEffective_date(),"dd/MM/yyyy");
				String expriryDate=ToolTimeCustomer.convertDateToString(contractCrud.getExpiry_date(),"dd/MM/yyyy");
				String customerCode=contractCrud.getCustomer().getCustomer_code();
				String customerName=contractCrud.getCustomer().getCustomer_name();
				for (ContractDetail it : listContractDetail) {
					Product product=it.getProduct();
					double soLuongKg=formatHandler.roundCus(it.getQuantity() * product.getFactor(),1000);
					double soluongKgThucHien=0;
					double soLuongKgConLai=BigDecimal.valueOf(soLuongKg).subtract(BigDecimal.valueOf(soluongKgThucHien)).doubleValue();
					Object[] row = { voucherCode,effectiveDate, expriryDate,customerCode,customerName,product.getProduct_code(),product.getProduct_name()
							,Boolean.toString(it.isPromotion()),"",it.getQuantity(),soLuongKg,0,soLuongKgConLai,it.getUnit_price(),it.getUnit_price()*it.getQuantity()};
					results.add(row);
				}
				String titleEx="hop_dong_san_pham_"+voucherCode;
				ToolReport.printReportExcelRaw(results,
						titleEx);
			} else {
				notify.message("Không có dữ liệu");
			}
		} catch (Exception e) {
			logger.error("ContractBean.exportExcel:" + e.getMessage(), e);
		}
	}
	public void copyContract(){
		try{
			if(contractCrud!=null && contractCrud.getId()!=0 && listContractDetail !=null && listContractDetail.size()>0){
				contractCrud.setId(0);
				contractCrud.setContract_code(null);
				contractCrud.setVoucher_code(contractService.initVoucherCode());
				for(ContractDetail item:listContractDetail){
					item.setId(0);
					item.setContract(null);
					item.setLast_modifed_by(null);
					item.setLast_modifed_date(null);
				}
			}
		}catch (Exception e) {
			logger.error("ContractBean.copyContract:"+e.getMessage(),e);
		}
	}
	public double sumContract(){
		double sum=0;
		try{
			if(listContractDetail !=null && listContractDetail.size()>0){
				for(ContractDetail item:listContractDetail){
					BigDecimal thanhtien=BigDecimal.valueOf(item.getQuantity()).multiply(BigDecimal.valueOf(item.getUnit_price()));
					sum=BigDecimal.valueOf(sum).add(thanhtien).doubleValue();
				}
			}
		}catch (Exception e) {
			logger.error("ContractBean.sumContract:"+e.getMessage(),e);
		}
		return sum;
	}
	
	public boolean  filterContract(Object value, Object filter, Locale locale){
		try{
			  String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
		        if (filterText == null || filterText.equals("")) {
		            return true;
		        }
		        String productName = (String) value;
		        return productName.contains(filterText);
		}catch (Exception e) {
			logger.error("ContractBean.filterContract:"+e.getMessage(),e);
		}
		return false;
	}
	public void showContract(){
		try{
			if(contractSelect!=null){
				contractCrud=contractSelect.clone();
				//load chi tiết hợp đồng.
				listContractDetail=new ArrayList<>();
				contractService.selectContractDetailByContractId(contractSelect.getId(), listContractDetail);
			}
		}catch (Exception e) {
			logger.error("ContractBean.showContract:"+e.getMessage(),e);
		}
	}
	public void search(){
		try{
			/*{contract:{customer_id:0,voucher_code:'',from_date:'',to_date:'',liquidated:-1},page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listContract=new ArrayList<>();
			PagingInfo page=new PagingInfo();
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 :customerSearch.getId());
			jsonInfo.addProperty("voucher_code", voucherCodeSearch);
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch,"dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("liquidated", liquidatedSearch);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("contract", jsonInfo);
			json.add("page", jsonPage);
			contractService.search(JsonParserUtil.getGson().toJson(json), page, listContract);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		}catch(Exception e){
			logger.error("ContractBean.search:"+e.getMessage(),e);
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
			logger.error("ContractBean.initRowPerPage:" + e.getMessage(), e);
		}
	}
	public void paginatorChange(int currentPage) {
		try {
			/*{contract:{customer_id:0,voucher_code:'',from_date:'',to_date:'',liquidated:-1},page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listContract = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 :customerSearch.getId());
			jsonInfo.addProperty("voucher_code", voucherCodeSearch);
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch,"dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("liquidated", liquidatedSearch);
			//thông tin trang
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("contract", jsonInfo);
			json.add("page", jsonPage);
			contractService.search(JsonParserUtil.getGson().toJson(json),page,listContract);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("ContractBean.paginatorChange:" + e.getMessage(), e);
		}
	}
	public List<Customer> completeCustomer(String text){
		try{
			List<Customer> list = new ArrayList<Customer>();
			customerService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch (Exception e) {
			logger.error("ContractBean.completeCustomer:" + e.getMessage(), e);
		}
		return null;
	}
	public List<Product> completeProduct(String text){
		try{
			List<Product> list=new ArrayList<>();
			productService.complete3(formatHandler.converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("ContractBean.completeProduct:"+e.getMessage(),e);
		}
		return null;
	}
	public void saveOrUpdate(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(contractCrud != null){
				//kiểm tra dữ liệu có đầy đủ không 
				Customer customer = contractCrud.getCustomer();
				Date contractDate = contractCrud.getContract_date();
				Date effectiveDate=contractCrud.getEffective_date();
				if(customer!=null && effectiveDate !=null && contractDate !=null){
					ContractReqInfo t=new ContractReqInfo(contractCrud);
					if(contractCrud.getId()==0){
						if(allowSave(new Date())){
							contractCrud.setCreated_date(new Date());
							contractCrud.setCreated_by(account.getMember().getName());
							int code=contractService.insert(t);
							switch (code) {
							case 0:
								//nếu thành công thêm hợp đồng vào danh sách
								listContract.add(0,t.getContract());
								if(listContractDetail !=null && listContractDetail.size()>0){
									//trường hợp copy phiếu
									ContractDetailReqInfo td=new ContractDetailReqInfo();
									for(ContractDetail d:listContractDetail){
										if(d.getId()==0){
											td.setContract_detail(d);
											d.setContract(t.getContract());
										    d.setCreated_by(account.getMember().getName());
										    d.setCreated_date(new Date());
										    d.setLast_modifed_by(null);
										    d.setLast_modifed_date(null);
										    contractService.insertDetail(td);
										}
									}
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Sao chép hợp đồng thành công!','success',2000);");
								}else{
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Tạo hợp đồng thành công!','success',2000);");
								}
								break;
							case -2:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Số hợp đồng bị trùng!','warning',2000);");
								break;

							default:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Không lưu được!','warning',2000);");
								break;
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
						}
					}else{
						if(allowUpdate(new Date())){
							contractCrud.setLast_modifed_by(account.getMember().getName());
							contractCrud.setLast_modifed_date(new Date());
							int code=contractService.update(t);
							switch (code) {
							case 0:
								//nếu cập nhật thành công thì cập nhật lại danh sách.
								listContract.set(listContract.indexOf(contractCrud),t.getContract());
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								break;
							case -2:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Số hợp đồng bị trùng!','warning',2000);");
								break;

							default:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Cập nhật thất bại!','warning',2000);");
								break;
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
						}
					}
				}else{
					current.executeScript("swaldesigntimer('Cảnh báo!','Thông tin không đầy đủ!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("ContractBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}
	public void debtTracking(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(contractCrud !=null && contractCrud.getId() !=0){
				JsonObject json=new JsonObject();
				json.addProperty("contract_id",contractCrud.getId());
				List<VoucherPayment> list=new ArrayList<>();
			    voucherPaymentService.getVoucherPaymentBy(JsonParserUtil.getGson().toJson(json), list);
			    if(list.size()>0){
			    	String reportPath = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/reports/hopdong/theodoicongno.jasper");
			    	Map<String, Object> importParam = new HashMap<String, Object>();
			    	importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/gfx/lixco_logo.png"));
			    	importParam.put("customer_name", contractCrud.getCustomer().getCustomer_name());
			    	importParam.put("voucher_code", contractCrud.getVoucher_code());
			    	importParam.put("total_quantity", sumContract());
			    	importParam.put("list_detail", list);
			    	JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
					byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
					String ba = Base64.getEncoder().encodeToString(data);
					current.executeScript("utility.printPDF('" + ba + "')");
			    }else{
			    	current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Chưa có ủy nhiệm chi nào','warning',2000);");
			    }
			}
		}catch (Exception e) {
			logger.error("ContractBean.debtTracking:" + e.getMessage(), e);
		}
	}
	public void showDialogAddContractDetail(){
	   PrimeFaces current=PrimeFaces.current();
	   Notify notify = new Notify(FacesContext.getCurrentInstance());
       try{
    	   if(contractCrud !=null && contractCrud.getId()!=0){
    		   contractDetailCrud=new ContractDetail();
    		   contractDetailCrud.setContract(contractCrud);
    	     current.executeScript("PF('dlg1').show();");
    	   }else{
    		   notify.warning("Hợp đồng không tồn tại!");
    	   }
		}catch (Exception e) {
			logger.error("ContractBean.showDialogAddContractDetail:" + e.getMessage(), e);
		}
	}
	public void showDialogEditContractDetail(){
		PrimeFaces current=PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(contractCrud!=null && contractCrud.getId()!=0){
				if(contractDetailSelect !=null && contractSelect.getId() !=0){
					  contractDetailCrud=contractDetailSelect.clone();
					  current.executeScript("PF('dlg1').show();");
				}else{
					 notify.warning("Chưa chọn chi tiết hợp đồng để chỉnh sửa!");
				}
			}else{
				notify.warning("Hợp đồng không tồn tại!");
			}
		}catch(Exception e){
			logger.error("ContractBean.showDialogEditContractDetail:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdateDetail(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		PrimeFaces current=PrimeFaces.current();
        try{
			if(contractDetailCrud !=null){
				//kiểm tra số liệu
				Product product=contractDetailCrud.getProduct();
				double quantity=contractDetailCrud.getQuantity();
				if(product !=null && quantity !=0){
					ContractDetailReqInfo t=new ContractDetailReqInfo(contractDetailCrud.clone());
;					if(contractDetailCrud.getId()==0){
						if(allowSave(new Date())){
							contractDetailCrud.setCreated_by(account.getMember().getName());
							contractDetailCrud.setCreated_date(new Date());
							if(contractService.insertDetail(t)==0){
								//thêm vào danh sách.
								listContractDetail.add(0, t.getContract_detail());
								notify.success("Lưu thành công!");
								//refesh lại dialog 
								contractDetailCrud=new ContractDetail();
								contractDetailCrud.setContract(contractCrud);
							}else{
								notify.warning("Lưu thất bại!");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
						}
					}else{
						if(allowUpdate(new Date())){
							contractDetailCrud.setLast_modifed_by(account.getMember().getName());
							contractDetailCrud.setLast_modifed_date(new Date());
							if(contractService.updateDetail(t)==0){
								//cập nhật lại danh sách
								listContractDetail.set(listContractDetail.indexOf(t.getContract_detail()),t.getContract_detail());
								notify.success("Cập nhật thành công!");
							}else{
								notify.warning("Cập nhật thất bại!");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
						}
					}
				}else{
					notify.warning("Thông tin không đầy đủ!");
				}
			}
		}catch (Exception e) {
			logger.error("ContractBean.saveOrUpdateDetail:" + e.getMessage(), e);
		}
	}
	public void checkProcessContract(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			listProcessContract=new ArrayList<>();
			if(contractCrud !=null && contractCrud.getId()!=0 && listContractDetail !=null && listContractDetail.size()>0){
				List<Long> listProductId=new ArrayList<>();
				Map<Long, ProcessContract> map=new LinkedHashMap<>();
				for(ContractDetail d:listContractDetail){
					listProductId.add(d.getProduct().getId());
					map.put(d.getProduct().getId(), new ProcessContract(d, 0));
				}
				List<Object[]> listTemp=new ArrayList<>();
				invoicePosService.processingContract(listProductId, contractCrud.getEffective_date(),contractCrud.getExpiry_date(), listTemp);
				for(Object[] t:listTemp){
					long id=Long.parseLong(Objects.toString(t[0],"0"));
					double quantityProcess=Double.parseDouble(Objects.toString(t[1], "0"));
					if(map.containsKey(id)){
						map.get(id).setQuantity_process_kg(quantityProcess);
					}
				}
				listProcessContract.addAll(map.values()); 
				for(ProcessContract p:listProcessContract){
					ContractDetail contractDetail= p.getContract_detail();
					double quantityKgHD=BigDecimal.valueOf(contractDetail.getQuantity()).multiply(BigDecimal.valueOf(contractDetail.getProduct().getFactor())).doubleValue();
					double quantityRemain=BigDecimal.valueOf(quantityKgHD).subtract(BigDecimal.valueOf(p.getQuantity_process_kg())).doubleValue();
					p.setQuantity_remain(quantityRemain);
				}
			}else{
				notify.warning("Không có dữ liệu!");
			}
		}catch (Exception e) {
			logger.error("ContractBean.checkProcessContract:" + e.getMessage(), e);
		}
	}
	public void printPDFProcessContract(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(listProcessContract !=null && listProcessContract.size()>0){
		    	String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/hopdong/kiemtrathuchienhopdong.jasper");
		    	Map<String, Object> importParam = new HashMap<String, Object>();
		    	importParam.put("logo", FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/gfx/lixco_logo.png"));
		    	importParam.put("customer_name", contractCrud.getCustomer().getCustomer_name());
		    	importParam.put("title", "THEO DÕI HỢP ĐỒNG TIÊU THỤ SẢN PHẨM SỐ "+contractCrud.getVoucher_code());
		    	importParam.put("customer_code", contractCrud.getCustomer().getCustomer_code());
		    	importParam.put("list_data", listProcessContract);
		    	JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
				byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
				String ba = Base64.getEncoder().encodeToString(data);
				current.executeScript("utility.printPDF('" + ba + "')");
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo', 'Không có dữ liệu','warning',2000);");
			}
		}catch (Exception e) {
			logger.error("ContractBean.printPDFProcessContract:" + e.getMessage(), e);
		}
	}
	public Contract getContractCrud() {
		return contractCrud;
	}
	public void setContractCrud(Contract contractCrud) {
		this.contractCrud = contractCrud;
	}
	public Contract getContractSelect() {
		return contractSelect;
	}
	public void setContractSelect(Contract contractSelect) {
		this.contractSelect = contractSelect;
	}
	public List<Contract> getListContract() {
		return listContract;
	}
	public void setListContract(List<Contract> listContract) {
		this.listContract = listContract;
	}
	public Customer getCustomerSearch() {
		return customerSearch;
	}
	public void setCustomerSearch(Customer customerSearch) {
		this.customerSearch = customerSearch;
	}
	public String getVoucherCodeSearch() {
		return voucherCodeSearch;
	}
	public void setVoucherCodeSearch(String voucherCodeSearch) {
		this.voucherCodeSearch = voucherCodeSearch;
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
	public int getLiquidatedSearch() {
		return liquidatedSearch;
	}
	public void setLiquidatedSearch(int liquidatedSearch) {
		this.liquidatedSearch = liquidatedSearch;
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
	public ContractDetail getContractDetailCrud() {
		return contractDetailCrud;
	}
	public void setContractDetailCrud(ContractDetail contractDetailCrud) {
		this.contractDetailCrud = contractDetailCrud;
	}
	public ContractDetail getContractDetailSelect() {
		return contractDetailSelect;
	}
	public void setContractDetailSelect(ContractDetail contractDetailSelect) {
		this.contractDetailSelect = contractDetailSelect;
	}
	public List<ContractDetail> getListContractDetail() {
		return listContractDetail;
	}
	public void setListContractDetail(List<ContractDetail> listContractDetail) {
		this.listContractDetail = listContractDetail;
	}
	public FormatHandler getFormatHandler() {
		return formatHandler;
	}
	public void setFormatHandler(FormatHandler formatHandler) {
		this.formatHandler = formatHandler;
	}
	public List<Currency> getListCurrency() {
		return listCurrency;
	}
	public void setListCurrency(List<Currency> listCurrency) {
		this.listCurrency = listCurrency;
	}
	public List<ContractDetail> getListContractDetailFillter() {
		return listContractDetailFillter;
	}
	public void setListContractDetailFillter(List<ContractDetail> listContractDetailFillter) {
		this.listContractDetailFillter = listContractDetailFillter;
	}
	public List<ProcessContract> getListProcessContract() {
		return listProcessContract;
	}
	public void setListProcessContract(List<ProcessContract> listProcessContract) {
		this.listProcessContract = listProcessContract;
	}
}
