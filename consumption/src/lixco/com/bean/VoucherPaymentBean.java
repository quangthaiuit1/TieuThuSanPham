package lixco.com.bean;

import java.util.ArrayList;
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
import lixco.com.entity.Contract;
import lixco.com.entity.Currency;
import lixco.com.entity.Customer;
import lixco.com.entity.VoucherPayment;
import lixco.com.interfaces.IContractService;
import lixco.com.interfaces.ICurrencyService;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.IVoucherPaymentService;
import lixco.com.reqInfo.VoucherPaymentReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class VoucherPaymentBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IVoucherPaymentService voucherPaymentService;
	@Inject
	private ICustomerService customerService;
	@Inject 
	private IContractService contractService;
	@Inject
	private ICurrencyService currencyService;
	private List<VoucherPayment> listVoucherPayment;
	private VoucherPayment voucherPaymentCrud;
	private VoucherPayment voucherPaymentSelect;
	private List<Currency> listCurrency;
	/*search*/
	private String voucherCode;
	private Date paymentDate;
	private String contractVoucherCode;
	private Customer paymentCustomer;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	private Account account;
	private FormatHandler formatHandler;
	
	
	@Override
	protected void initItem() {
		try{
			listCurrency=new ArrayList<>();
			currencyService.selectAll(listCurrency);
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			search();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			voucherPaymentCrud = new VoucherPayment();
			formatHandler=FormatHandler.getInstance();
		}catch (Exception e) {
			logger.error("VoucherPaymentBean.initItem:"+e.getMessage(),e);
		}
	}
	public void search(){
		try{
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listVoucherPayment = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			/*{ voucher_payment:{voucher_code:'',payment_date:'',contact:{voucher_code:''},payment_customer_id:0,receiver_customer_id:0}, page:{page_index:0, page_size:0}}*/
			JsonObject jsonVoucherPayment=new JsonObject();
			jsonVoucherPayment.addProperty("voucher_code", voucherCode);
			jsonVoucherPayment.addProperty("payment_date", ToolTimeCustomer.convertDateToString(paymentDate, "dd/MM/yyyy"));
			jsonVoucherPayment.addProperty("payment_customer_id", paymentCustomer==null ? 0: paymentCustomer.getId());
			JsonObject jsonContract=new JsonObject();
			jsonContract.addProperty("voucher_code",contractVoucherCode);
			jsonVoucherPayment.add("contract", jsonContract);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("voucher_payment", jsonVoucherPayment);
			json.add("page", jsonPage);
			voucherPaymentService.search(JsonParserUtil.getGson().toJson(json), page, listVoucherPayment);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		}catch (Exception e) {
			logger.error("VoucherPaymentBean.search:"+e.getMessage(),e);
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
			logger.error("VoucherPaymentBean.initRowPerPage:" + e.getMessage(), e);
		}
	}
	public void paginatorChange(int currentPage) {
		try {
			/*{ voucher_payment:{voucher_code:'',payment_date:'',contact:{voucher_code:''},payment_customer_id:0,receiver_customer_id:0}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listVoucherPayment = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonVoucherPayment=new JsonObject();
			jsonVoucherPayment.addProperty("voucher_code", voucherCode);
			jsonVoucherPayment.addProperty("payment_date", ToolTimeCustomer.convertDateToString(paymentDate, "dd/MM/yyyy"));
			jsonVoucherPayment.addProperty("payment_customer_id", paymentCustomer==null ? 0: paymentCustomer.getId());
			JsonObject jsonContract=new JsonObject();
			jsonContract.addProperty("voucher_code",contractVoucherCode);
			jsonVoucherPayment.add("contract", jsonContract);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("voucher_payment", jsonVoucherPayment);
			json.add("page", jsonPage);
			voucherPaymentService.search(JsonParserUtil.getGson().toJson(json), page, listVoucherPayment);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("VoucherPaymentBean.paginatorChange:" + e.getMessage(), e);
		}
	}
	public List<Customer> completeCustomer(String text){
		try{
			List<Customer> list=new ArrayList<>();
			customerService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch (Exception e) {
			logger.error("VoucherPaymentBean.completeCustomer:" + e.getMessage(), e);
		}
		return null;
	}
	public List<Contract> completeContract(String text){
		try{
			List<Contract> list=new ArrayList<>();
			contractService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch (Exception e) {
			logger.error("VoucherPaymentBean.completeContract:" + e.getMessage(), e);
		}
		return null;
	}
	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (voucherPaymentCrud != null) {
				String voucherCode = voucherPaymentCrud.getVoucher_code();
				Date paymentDate=voucherPaymentCrud.getPayment_date();
				Currency currency=voucherPaymentCrud.getCurrency();
				Customer paymentCustomer=voucherPaymentCrud.getPayment_customer();
				Customer receiverCustomer=voucherPaymentCrud.getReceiver_customer();
				if (voucherCode != null && !"".equals(voucherCode) && paymentDate != null && currency != null && paymentCustomer != null && receiverCustomer != null){
					VoucherPaymentReqInfo t = new VoucherPaymentReqInfo(voucherPaymentCrud);
					if (voucherPaymentCrud.getId() == 0) {
						if (allowSave(new Date())) {
							voucherPaymentCrud.setCreated_date(new Date());
							voucherPaymentCrud.setCreated_by(account.getMember().getName());
							if (voucherPaymentService.insert(t) != -1) {
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
								listVoucherPayment.add(0, voucherPaymentCrud.clone());
								voucherPaymentCrud=new VoucherPayment();
							} else {
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
							}
							
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					} else {
						// check code update đã tồn tại chưa
						if (allowUpdate(new Date())) {
							voucherPaymentCrud.setLast_modifed_by(account.getMember().getName());
							voucherPaymentCrud.setLast_modifed_date(new Date());
							if (voucherPaymentService.update(t) != -1) {
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								listVoucherPayment.set(listVoucherPayment.indexOf(voucherPaymentCrud), voucherPaymentCrud);
							} else {
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ, điền đủ thông tin chứa(*)!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("VoucherPaymentBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showDialogEdit() {
		PrimeFaces current = PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (voucherPaymentSelect != null) {
				voucherPaymentCrud = voucherPaymentSelect.clone();
				current.executeScript("PF('dlg1').show();");
			} else {
				notify.message("Chọn dòng để chỉnh sửa!");
			}
		} catch (Exception e) {
			logger.error("VoucherPaymentBean.showDialogEdit:" + e.getMessage(), e);
		}
	}

	public void showDialog() {
		PrimeFaces current = PrimeFaces.current();
		try {
			voucherPaymentCrud = new VoucherPayment();
			current.executeScript("PF('dlg1').show();");
		} catch (Exception e) {
			logger.error("VoucherPaymentBean.showDialog:" + e.getMessage(), e);
		}
	}
	public void delete() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (voucherPaymentSelect != null) {
				if (allowDelete(new Date())) {
					if (voucherPaymentService.deleteById(voucherPaymentSelect.getId()) != -1) {
						current.executeScript("swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listVoucherPayment.remove(voucherPaymentSelect);
					} else {
						current.executeScript("swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
					}
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
				}
			} else {
				current.executeScript("swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xóa!','warning',2000);");
			}
		} catch (Exception e) {
			logger.error("VoucherPaymentBean.delete:" + e.getMessage(), e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public List<VoucherPayment> getListVoucherPayment() {
		return listVoucherPayment;
	}
	public void setListVoucherPayment(List<VoucherPayment> listVoucherPayment) {
		this.listVoucherPayment = listVoucherPayment;
	}
	public VoucherPayment getVoucherPaymentCrud() {
		return voucherPaymentCrud;
	}
	public void setVoucherPaymentCrud(VoucherPayment voucherPaymentCrud) {
		this.voucherPaymentCrud = voucherPaymentCrud;
	}
	public VoucherPayment getVoucherPaymentSelect() {
		return voucherPaymentSelect;
	}
	public void setVoucherPaymentSelect(VoucherPayment voucherPaymentSelect) {
		this.voucherPaymentSelect = voucherPaymentSelect;
	}
	public String getVoucherCode() {
		return voucherCode;
	}
	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getContractVoucherCode() {
		return contractVoucherCode;
	}
	public void setContractVoucherCode(String contractVoucherCode) {
		this.contractVoucherCode = contractVoucherCode;
	}
	public Customer getPaymentCustomer() {
		return paymentCustomer;
	}
	public void setPaymentCustomer(Customer paymentCustomer) {
		this.paymentCustomer = paymentCustomer;
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
	public List<Currency> getListCurrency() {
		return listCurrency;
	}
	public void setListCurrency(List<Currency> listCurrency) {
		this.listCurrency = listCurrency;
	}
}
