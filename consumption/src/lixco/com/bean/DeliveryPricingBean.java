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
import lixco.com.entity.Customer;
import lixco.com.entity.CustomerTypes;
import lixco.com.entity.DeliveryPricing;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.ICustomerTypesService;
import lixco.com.interfaces.IDeliveryPricingService;
import lixco.com.reqInfo.CustomerReqInfo;
import lixco.com.reqInfo.DeliveryPricingReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class DeliveryPricingBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IDeliveryPricingService deliveryPricingService;
	@Inject
	private ICustomerTypesService customerTypesService;
	@Inject
	private ICustomerService customerService;
	private DeliveryPricing deliveryPricingCrud;
	private CustomerTypes customerTypesCrud;
	private DeliveryPricing deliveryPricingSelect;
	private List<DeliveryPricing> listDeliveryPricing;
	private List<CustomerTypes> listCustomerTypes;
	/*Search place*/
	private CustomerTypes customerTypesSearch;
	private Customer customerSearch;
	private String placeCodeSearch;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	private FormatHandler formatHandler;
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
			deliveryPricingCrud=new DeliveryPricing();
			formatHandler=FormatHandler.getInstance();
		} catch (Exception e) {
			logger.error("PlaceDeliveryBean.initItem:" + e.getMessage(), e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	public void search() {
		try {
			/*{ delivery_pricing_info:{customer_id:0,place_code:'',disable:-1}, page:{page_index:0, page_size:0}}*/
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listDeliveryPricing = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 : customerSearch.getId());
			jsonInfo.addProperty("place_code",placeCodeSearch);
			jsonInfo.addProperty("disable", -1);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("delivery_pricing_info", jsonInfo);
			json.add("page", jsonPage);
			deliveryPricingService.search(JsonParserUtil.getGson().toJson(json), page, listDeliveryPricing);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		} catch (Exception e) {
			logger.error("PlaceDeliveryBean.search:" + e.getMessage(), e);
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
			logger.error("PlaceDeliveryBean.initRowPerPage:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			/*{ delivery_pricing_info:{customer_id:0,place_code:'',disable:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listDeliveryPricing = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 : customerSearch.getId());
			jsonInfo.addProperty("place_code",placeCodeSearch);
			jsonInfo.addProperty("disable", -1);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("delivery_pricing_info", jsonInfo);
			json.add("page", jsonPage);
			deliveryPricingService.search(JsonParserUtil.getGson().toJson(json), page, listDeliveryPricing);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("PlaceDeliveryBean.paginatorChange:" + e.getMessage(), e);
		}
	}

	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (deliveryPricingCrud != null) {
				Customer cus = deliveryPricingCrud.getCustomer();
				String placeArrived = deliveryPricingCrud.getPlace_arrived();
				String placeCode=deliveryPricingCrud.getPlace_code();
				if (cus != null && placeArrived !=null && !"".equals(placeArrived) && placeCode !=null && !"".equals(placeCode)) {
					DeliveryPricingReqInfo t = new DeliveryPricingReqInfo(deliveryPricingCrud);
					if (deliveryPricingCrud.getId() == 0) {
						//check code đã tồn tại chưa
						if(allowSave(new Date())){
							if(deliveryPricingService.checkCode(deliveryPricingCrud.getPlace_code(),0)==0){
								deliveryPricingCrud.setCreated_date(new Date());
								deliveryPricingCrud.setCreated_by(account.getMember().getName());
								if(deliveryPricingService.insert(t)!=-1){
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listDeliveryPricing.add(0, deliveryPricingCrud.clone());
									createdNew();
								}else{
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã nơi vận chuyển đã tồn tại!','warning',2000);");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						//check code update đã tồn tại chưa
						if(deliveryPricingService.checkCode(deliveryPricingCrud.getPlace_code(), deliveryPricingCrud.getId())==0){
							if(allowUpdate(new Date())){
								deliveryPricingCrud.setLast_modifed_by(account.getMember().getName());
								deliveryPricingCrud.setLast_modifed_date(new Date());
								if(deliveryPricingService.update(t)!=-1){
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									listDeliveryPricing.set(listDeliveryPricing.indexOf(deliveryPricingCrud),t.getDelivery_pricing());
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
									"swaldesigntimer('Cảnh báo', 'Mã nơi vận chuyển đã tồn tại!','warning',2000);");
						}
					}
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ, điền đủ thông tin chứa(*)!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("PlaceDeliveryBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}
	public void showDialog(){
		PrimeFaces current=PrimeFaces.current();
		try{
			deliveryPricingCrud=new DeliveryPricing();
			current.executeScript("PF('dlg1').show();");
		}catch(Exception e){
			logger.error("PlaceDeliveryBean.showDialog:"+e.getMessage(),e);
		}
	}
	public void showDialogEdit(){
		PrimeFaces current=PrimeFaces.current();
		try{
			deliveryPricingCrud=deliveryPricingSelect.clone();
			current.executeScript("PF('dlg1').show();");
		}catch(Exception e){
			logger.error("PlaceDeliveryBean.showDialogEdit:"+e.getMessage(),e);
		}
	}
	public void createdNew(){
		try{
			deliveryPricingCrud=new DeliveryPricing();
		}catch(Exception e){
			logger.error("PlaceDeliveryBean.createdNew:"+e.getMessage(),e);
		}
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(deliveryPricingCrud !=null){
				if(allowDelete(new Date())){
					if(customerService.deleteById(deliveryPricingCrud.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listDeliveryPricing.remove(deliveryPricingCrud);
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
			logger.error("PlaceDeliveryBean.delete:"+e.getMessage(),e);
		}
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
				List<DeliveryPricing> listPlaceDeliveryTemp = new ArrayList<>();
				Workbook workBook = getWorkbook(new ByteArrayInputStream(byteFile), part.getFileName());
				Sheet firstSheet = workBook.getSheetAt(0);
				Iterator<Row> rows = firstSheet.iterator();
				while (rows.hasNext()) {
					rows.next();
					rows.remove();
					break;
				}
				lv1:while (rows.hasNext()) {
					Row row = rows.next();
					Iterator<Cell> cells = row.cellIterator();
					DeliveryPricing lix = new DeliveryPricing();
					while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								// mã khách hàng
								String makh = Objects.toString(getCellValue(cell), null);
								if(makh !=null && !"".equals(makh)){
									CustomerReqInfo c=new CustomerReqInfo();
									customerService.selectByCode(makh, c );
									if(c.getCustomer()==null){
										continue lv1;
									}else{
										lix.setCustomer(c.getCustomer());
									}
								}else{
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								// mã nơi vc
								String manvc = Objects.toString(getCellValue(cell), null);
								if(manvc !=null && !"".equals(manvc)){
									lix.setPlace_code(manvc);
								}else{
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								// địa chỉ
								String diachi = Objects.toString(getCellValue(cell), null);
								lix.setAddress(diachi);
							} catch (Exception e) {
							}
							break;
						case 3:
							try {
								// số km
								String km = Objects.toString(getCellValue(cell), null);
								lix.setKm(Double.parseDouble(km));
							} catch (Exception e) {
							}
							break;
						case 4:
							try {
								//Đơn giá
								String unitPrice = Objects.toString(getCellValue(cell), null);
								lix.setUnit_price(Double.parseDouble(unitPrice));
							} catch (Exception e) {
							}
							break;
						case 5:
							try {
								//Đơn giá k su dung
								String ksd = Objects.toString(getCellValue(cell), null);
								lix.setDisable(Boolean.parseBoolean(ksd));
							} catch (Exception e) {
							}
							break;
						case 6:
							try {
								//Nơi đến
								String placeArried = Objects.toString(getCellValue(cell), null);
								if(placeArried !=null && !"".equals(placeArried)){
									lix.setPlace_arrived(placeArried);
								}
							} catch (Exception e) {
							}
							break;
						}
					}
					listPlaceDeliveryTemp.add(lix);
				}
				workBook = null;// free
				for (DeliveryPricing it : listPlaceDeliveryTemp) {
					DeliveryPricingReqInfo t = new DeliveryPricingReqInfo();
					deliveryPricingService.selectByPlaceCode(it.getPlace_code(), t);
					DeliveryPricing p = t.getDelivery_pricing();
					t.setDelivery_pricing(it);
					if (p != null) {
						it.setId(p.getId());
						it.setLast_modifed_by(account.getMember().getName());
						it.setLast_modifed_date(new Date());
						deliveryPricingService.update(t);
					} else {
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						deliveryPricingService.insert(t);
					}
				}
				search();
				notify.success();
			}
		} catch (Exception e) {
			logger.error("PlaceDeliveryBean.loadExcel:" + e.getMessage(), e);
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
	public List<Customer> completeCustomerSearch(String text){
		try{
			List<Customer> list=new ArrayList<>();
			if(customerTypesSearch !=null){
				customerService.complete(formatHandler.converViToEn(text), customerTypesSearch, list);
			}else{
				customerService.complete(formatHandler.converViToEn(text), list);
			}
			return list;
		}catch(Exception e){
			logger.error("PlaceDeliveryBean.completeCustomerSearch:" + e.getMessage(), e);
		}
		return null;
	}
	public List<Customer> completeCustomerCrud(String text){
		try{
			List<Customer> list=new ArrayList<>();
			if(customerTypesCrud !=null){
				customerService.complete(formatHandler.converViToEn(text), customerTypesCrud, list);
			}else{
				customerService.complete(formatHandler.converViToEn(text), list);
			}
			return list;
		}catch(Exception e){
			logger.error("PlaceDeliveryBean.completeCustomerSearch:" + e.getMessage(), e);
		}
		return null;
	}

	public DeliveryPricing getDeliveryPricingCrud() {
		return deliveryPricingCrud;
	}

	public void setDeliveryPricingCrud(DeliveryPricing deliveryPricingCrud) {
		this.deliveryPricingCrud = deliveryPricingCrud;
	}

	public CustomerTypes getCustomerTypesCrud() {
		return customerTypesCrud;
	}

	public void setCustomerTypesCrud(CustomerTypes customerTypesCrud) {
		this.customerTypesCrud = customerTypesCrud;
	}

	public DeliveryPricing getDeliveryPricingSelect() {
		return deliveryPricingSelect;
	}

	public void setDeliveryPricingSelect(DeliveryPricing deliveryPricingSelect) {
		this.deliveryPricingSelect = deliveryPricingSelect;
	}

	public List<DeliveryPricing> getListDeliveryPricing() {
		return listDeliveryPricing;
	}

	public void setListDeliveryPricing(List<DeliveryPricing> listDeliveryPricing) {
		this.listDeliveryPricing = listDeliveryPricing;
	}

	public List<CustomerTypes> getListCustomerTypes() {
		return listCustomerTypes;
	}

	public void setListCustomerTypes(List<CustomerTypes> listCustomerTypes) {
		this.listCustomerTypes = listCustomerTypes;
	}

	public CustomerTypes getCustomerTypesSearch() {
		return customerTypesSearch;
	}

	public void setCustomerTypesSearch(CustomerTypes customerTypesSearch) {
		this.customerTypesSearch = customerTypesSearch;
	}

	public Customer getCustomerSearch() {
		return customerSearch;
	}

	public void setCustomerSearch(Customer customerSearch) {
		this.customerSearch = customerSearch;
	}

	public String getPlaceCodeSearch() {
		return placeCodeSearch;
	}

	public void setPlaceCodeSearch(String placeCodeSearch) {
		this.placeCodeSearch = placeCodeSearch;
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
}
