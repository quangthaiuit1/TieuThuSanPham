package lixco.com.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
import org.primefaces.model.UploadedFile;

import com.google.gson.JsonObject;

import lixco.com.common.FormatHandler;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.entity.City;
import lixco.com.entity.Customer;
import lixco.com.entity.CustomerTypes;
import lixco.com.interfaces.ICityService;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.ICustomerTypesService;
import lixco.com.reqInfo.CityReqInfo;
import lixco.com.reqInfo.CustomerReqInfo;
import lixco.com.reqInfo.CustomerTypesReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class CustomerBean extends AbstractBean {
	private static final long serialVersionUID = 6248587419234517987L;
	@Inject
	private Logger logger;
	@Inject
	private ICustomerService customerService;
	@Inject
	private ICustomerTypesService customerTypesService;
	@Inject
	private ICityService cityService;
	private Customer customerSelect;
	private Customer customerCrud;
	private List<Customer> listCustomer;
	private List<CustomerTypes> listCustomerTypes;
	private String customerCode;
	private String customerName;
	private String taxCode;
	private String cellPhone;
	private String homePhone;
	private CustomerTypes customerTypes;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	private Account account;

	@Override
	protected void initItem() {
		try {
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			search();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			listCustomerTypes=new ArrayList<CustomerTypes>();
			customerTypesService.selectAll(listCustomerTypes);
			customerCrud=new Customer();
			
		} catch (Exception e) {
			logger.error("CustomerBean.initItem:" + e.getMessage(), e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	public void search() {
		try {
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listCustomer = new ArrayList<Customer>();
			PagingInfo page = new PagingInfo();
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("customer_code", customerCode);
			jsonInfo.addProperty("customer_name", customerName);
			jsonInfo.addProperty("tax_code", taxCode);
			jsonInfo.addProperty("cell_phone", cellPhone);
			jsonInfo.addProperty("home_phone", homePhone);
			jsonInfo.addProperty("customer_types_id", customerTypes != null ? customerTypes.getId() : 0);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("customer_info", jsonInfo);
			json.add("page", jsonPage);
			customerService.search(JsonParserUtil.getGson().toJson(json), page, listCustomer);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		} catch (Exception e) {
			logger.error("CustomerBean.search:" + e.getMessage(), e);
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
			logger.error("CustomerBean.initRowPerPage:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listCustomer = new ArrayList<Customer>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("customer_code", customerCode);
			jsonInfo.addProperty("customer_name", customerName);
			jsonInfo.addProperty("tax_code", taxCode);
			jsonInfo.addProperty("cell_phone", cellPhone);
			jsonInfo.addProperty("home_phone", homePhone);
			jsonInfo.addProperty("customer_types_id", customerTypes != null ? customerTypes.getId() : 0);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("customer_info", jsonInfo);
			json.add("page", jsonPage);
			customerService.search(JsonParserUtil.getGson().toJson(json), page, listCustomer);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("CustomerBean.paginatorChange:" + e.getMessage(), e);
		}
	}

	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (customerCrud != null) {
				String customerCode = customerCrud.getCustomer_code();
				String customerName = customerCrud.getCustomer_name();
				CustomerTypes type = customerCrud.getCustomer_types();
				if (customerCode != null && customerCode != "" && customerName != null && customerName != ""
						&& type != null) {
					CustomerReqInfo t = new CustomerReqInfo(customerCrud);
					if (customerCrud.getId() == 0) {
						//check code đã tồn tại chưa
						if(allowSave(new Date())){
							if(customerService.checkCustomerCode(customerCode,0)==0){
								customerCrud.setCreated_date(new Date());
								customerCrud.setCreated_by(account.getMember().getName());
								if(customerService.insert(t)!=-1){
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listCustomer.add(0, customerCrud);
									createdNew();
								}else{
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã khách hàng đã tồn tại!','warning',2000);");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						//check code update đã tồn tại chưa
						if(customerService.checkCustomerCode(customerCode, customerCrud.getId())==0){
							if(allowUpdate(new Date())){
								customerCrud.setLast_modifed_by(account.getMember().getName());
								customerCrud.setLast_modifed_date(new Date());
								if(customerService.update(t)!=-1){
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									listCustomer.set(listCustomer.indexOf(customerCrud),customerCrud);
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
									"swaldesigntimer('Cảnh báo', 'Mã khách hàng đã tồn tại!','warning',2000);");
						}
					}
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin khách hàng không đầy đủ, điền đủ thông tin chứa(*)!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("CustomerBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}
	public void showDialog(){
		PrimeFaces current=PrimeFaces.current();
		try{
			customerCrud=new Customer();
			current.executeScript("PF('dlg1').show();");
		}catch(Exception e){
			logger.error("CustomerBean.showDialog:"+e.getMessage(),e);
		}
	}
	public void showDialogEdit(){
		PrimeFaces current=PrimeFaces.current();
		try{
			customerCrud=customerSelect;
			current.executeScript("PF('dlg1').show();");
		}catch(Exception e){
			logger.error("CustomerBean.showDialogEdit:"+e.getMessage(),e);
		}
	}
	public void createdNew(){
		try{
			customerCrud=new Customer();
		}catch(Exception e){
			logger.error("CustomerBean.createdNew:"+e.getMessage(),e);
		}
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(customerSelect !=null){
				if(allowDelete(new Date())){
					if(customerService.deleteById(customerSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listCustomer.remove(customerSelect);
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
			logger.error("CustomerBean.delete:"+e.getMessage(),e);
		}
	}
	public List<CustomerTypes> autoComplete(String text){
		try{
			List<CustomerTypes> list=new  ArrayList<CustomerTypes>();
			customerTypesService.findLike(FormatHandler.getInstance().converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("CustomerBean.autoComplete:"+e.getMessage(),e);
		}
		return null;
	}
	public List<City> completeCity(String text){
		try{
			List<City> list=new ArrayList<City>();
			cityService.findLike(text, 120, list);
			return list;
		}catch(Exception e){
			logger.error("CustomerBean.completeCity:"+e.getMessage(),e);
		}
		return null;
	}
	public void showDialogUpload(){
		PrimeFaces current = PrimeFaces.current();
		current.executeScript("PF('uploadpdffile').show();");
	}

	public void loadExcel(FileUploadEvent event) {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<Customer> listCustomerTemp = new ArrayList<Customer>();
				Workbook workBook = getWorkbook(new ByteArrayInputStream(byteFile), part.getFileName());
				Sheet firstSheet = workBook.getSheetAt(0);
				Iterator<Row> rows = firstSheet.iterator();
				while (rows.hasNext()) {
					rows.next();
					rows.remove();
					break;
				}
				while (rows.hasNext()) {
					Row row = rows.next();
					Iterator<Cell> cells = row.cellIterator();
					Customer lix = new Customer();
					lv2: while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								// mã số thuế
								String masothue = Objects.toString(getCellValue(cell), null);
								lix.setTax_code(masothue);
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								// mã khách hàng
								String makh = Objects.toString(getCellValue(cell), null);
								if(makh !=null && !"".equals(makh)){
									lix.setCustomer_code(makh);
								}else{
									break lv2;
								}
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								// tên khách hàng
								String tenkh = Objects.toString(getCellValue(cell), null);
								lix.setCustomer_name(tenkh);
							} catch (Exception e) {
							}
							break;
						case 3:
							try {
								// địa chỉ khách hàng
								String diachi = Objects.toString(getCellValue(cell), null);
								lix.setAddress(diachi);
							} catch (Exception e) {
							}
							break;
						case 4:
							try {
								// điện thoại khách hàng
								String cellphone = Objects.toString(getCellValue(cell), null);
								lix.setCell_phone(cellphone);
							} catch (Exception e) {
							}
							break;
						case 5:
							try {
								// fax khách hàng
								String fax = Objects.toString(getCellValue(cell), null);
								lix.setFax(fax);
							} catch (Exception e) {
							}
							break;
						case 6:
							try {
								// mã thành phố
								String matp = Objects.toString(getCellValue(cell), null);
								if(matp !=null && !"".equals(matp)){
									CityReqInfo cf=new CityReqInfo();
									cityService.selectByCode(matp, cf);
									lix.setCity(cf.getCity());
								}
							} catch (Exception e) {
							}
							break;
						case 7:
							try {
								// tài khoản 
								String taikhoan = Objects.toString(getCellValue(cell), null);
								lix.setBank_info(taikhoan);
							} catch (Exception e) {
							}
							break;
						case 8:
							try {
								// đơn vị
								String donvi = Objects.toString(getCellValue(cell), null);
								lix.setCompany_name(donvi);
							} catch (Exception e) {
							}
							break;
						case 9:
							try {
								// mã loại khách hàng
								String maloaikh = Objects.toString(getCellValue(cell), null);
								if(maloaikh !=null && !"".equals(maloaikh)){
									CustomerTypesReqInfo ctf=new CustomerTypesReqInfo();
									customerTypesService.selectByCode(maloaikh,ctf);
									lix.setCustomer_types(ctf.getCustomer_types());
								}
							} catch (Exception e) {
							}
							break;
						case 10:
							try {
								// mã số khách hàng 
								String masokhf = Objects.toString(getCellValue(cell), null);
								lix.setCustomer_dfcode(masokhf);
							} catch (Exception e) {
							}
							break;
						case 11:
							try {
								// số ngày nợ
								String masokh = Objects.toString(getCellValue(cell), "0");
								lix.setDays_debt_quantity(Double.parseDouble(masokh));
							} catch (Exception e) {
							}
							break;
							// trước có kiểu memo cho trường lưu ý
						case 13:
							try {
								// địa điểm giao hàng
								String ddgh = Objects.toString(getCellValue(cell), null);
								lix.setLocation_delivery(ddgh);
							} catch (Exception e) {
							}
							break;
						case 14:
							try {
								//tỉ lệ khuyến khích tiêu thụ sản phẩm tphcm
								String tlkk = Objects.toString(getCellValue(cell), "0");
								lix.setEncourage_rate(Double.parseDouble(tlkk));
							} catch (Exception e) {
							}
							break;
						case 15:
							try {
								//tỉ lệ hoa hồng bột giặt
								String tlhh = Objects.toString(getCellValue(cell), "0");
								lix.setCommission_value(Double.parseDouble(tlhh));
							} catch (Exception e) {
							}
							break;
						case 16:
							try {
								//số tiền npp chuyển
								String tnppc = Objects.toString(getCellValue(cell), "0");
								lix.setAmount_npp(Double.parseDouble(tnppc));
							} catch (Exception e) {
							}
							break;
						case 17:
							try {
								//mã số nhà cung cấp
								String masoncc = Objects.toString(getCellValue(cell), null);
								lix.setSupplier_code(masoncc);
							} catch (Exception e) {
							}
							break;
						case 18:
							try {
								//khách hàng nghỉ bán
								String dis = Objects.toString(getCellValue(cell),null);
								lix.setDisable(Boolean.parseBoolean(dis));
							} catch (Exception e) {
							}
							break;
						case 19:
							try {
								//hoa hồng ntrl
								String tlhhntr = Objects.toString(getCellValue(cell),"0");
								lix.setCommission_ntrl_value(Double.parseDouble(tlhhntr));
							} catch (Exception e) {
							}
							break;
						case 20:
							try {
								//không in tên khách hàng trên hóa đơn
								String kin = Objects.toString(getCellValue(cell),null);
								lix.setNot_print_customer_name(Boolean.parseBoolean(kin));
							} catch (Exception e) {
							}
							break;
						case 21:
							try {
								//email
								String email = Objects.toString(getCellValue(cell),null);
								lix.setEmail(email);
							} catch (Exception e) {
							}
							break;
						}
					}
					listCustomerTemp.add(lix);
				}
				workBook = null;// free
				for (Customer it : listCustomerTemp) {
					CustomerReqInfo t = new CustomerReqInfo();
					customerService.selectByCode(it.getCustomer_code(), t);
					Customer p = t.getCustomer();
					t.setCustomer(it);
					if (p != null) {
						it.setId(p.getId());
						it.setLast_modifed_by(account.getMember().getName());
						it.setLast_modifed_date(new Date());
						customerService.update(t);
					} else {
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						customerService.insert(t);
					}
				}
				search();
				notify.success();
			}
		} catch (Exception e) {
			logger.error("ProductTypeBean.loadExcel:" + e.getMessage(), e);
		}
	}
	private Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
		Workbook workbook = null;
		if (excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}

		return workbook;
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
	public Customer getCustomerSelect() {
		return customerSelect;
	}

	public void setCustomerSelect(Customer customerSelect) {
		this.customerSelect = customerSelect;
	}

	public Customer getCustomerCrud() {
		return customerCrud;
	}

	public void setCustomerCrud(Customer customerCrud) {
		this.customerCrud = customerCrud;
	}

	public List<Customer> getListCustomer() {
		return listCustomer;
	}

	public void setListCustomer(List<Customer> listCustomer) {
		this.listCustomer = listCustomer;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public CustomerTypes getCustomerTypes() {
		return customerTypes;
	}

	public void setCustomerTypes(CustomerTypes customerTypes) {
		this.customerTypes = customerTypes;
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

	public List<CustomerTypes> getListCustomerTypes() {
		return listCustomerTypes;
	}

	public void setListCustomerTypes(List<CustomerTypes> listCustomerTypes) {
		this.listCustomerTypes = listCustomerTypes;
	}
}
