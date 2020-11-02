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

import lixco.com.common.JsonParserUtil;
import lixco.com.common.SessionHelper;
import lixco.com.entity.CustomerChannel;
import lixco.com.entity.CustomerTypes;
import lixco.com.interfaces.ICustomerChannelService;
import lixco.com.interfaces.ICustomerTypesService;
import lixco.com.reqInfo.CustomerChannelReqInfo;
import lixco.com.reqInfo.CustomerTypesReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class CustomerTypesBean extends AbstractBean{
	private static final long serialVersionUID = -386708537069595621L;
	@Inject
	private Logger logger;
	@Inject
	private ICustomerTypesService customerTypesService;
	@Inject
	private ICustomerChannelService customerChannelService;
	private CustomerTypes customerTypesSelect;
	private CustomerTypes customerTypesCrud;
	private List<CustomerTypes> listCustomerTypes;
	private String code;
	private String name;
	private Account account;
	private CustomerChannel customerChannel;
	private List<CustomerChannel> listCustomerChannel;
	@Override
	protected void initItem() {
		try{
			search();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			createNew();
			listCustomerChannel=new ArrayList<>();
			customerChannelService.selectAll(listCustomerChannel);
		}catch(Exception e){
			logger.error("CustomerTypesBean.initItem:"+e.getMessage(),e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void search(){
		try{
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("code", code);
			jsonInfo.addProperty("name", name);
			jsonInfo.addProperty("channel_id", customerChannel ==null ? 0 :customerChannel.getId());
			JsonObject json=new JsonObject();
			json.add("customer_types_info", jsonInfo);
			listCustomerTypes=new ArrayList<CustomerTypes>();
			customerTypesService.search(JsonParserUtil.getGson().toJson(json), listCustomerTypes);
		}catch(Exception e){
			logger.error("CustomerTypesBean.search:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdate(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(customerTypesCrud !=null){
				String code=customerTypesCrud.getCode();
				String name=customerTypesCrud.getName();
				if(code !=null && code !="" && name !=null && name !="" && customerTypesCrud.getCustomer_channel() !=null){
					CustomerTypesReqInfo t=new CustomerTypesReqInfo(customerTypesCrud);
					if(customerTypesCrud.getId()==0){
						if(allowSave(new Date())){
							//check code
							if(customerTypesService.checkCustomerTypeCode(code, 0)==0){
								customerTypesCrud.setCreated_date(new Date());
								customerTypesCrud.setCreated_by(account.getMember().getName());
								if(customerTypesService.insert(t)!=-1){
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listCustomerTypes.add(0, customerTypesCrud);
								}else{
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã loại khách hàng đã tồn tại!','warning',2000);");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						if(allowUpdate(new Date())){
							//check code
							if(customerTypesService.checkCustomerTypeCode(code, customerTypesCrud.getId())==0){
								customerTypesCrud.setLast_modifed_date(new Date());
								customerTypesCrud.setLast_modifed_by(account.getMember().getName());
								if(customerTypesService.update(t) !=-1){
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									listCustomerTypes.set(listCustomerTypes.indexOf(customerTypesCrud),customerTypesCrud);
								}else{
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã loại khách hàng đã tồn tại!','warning',2000);");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ, điền đủ thông tin chứa(*)!','warning',2000);");
				}
			}
		}catch(Exception e){
			logger.error("CustomerTypesBean.saveOrUpdate:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		customerTypesCrud=new CustomerTypes();
	}
	public void showEdit(){
		customerTypesCrud=customerTypesSelect;
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(customerTypesSelect !=null){
				if(allowDelete(new Date())){
					if(customerTypesService.deleteById(customerTypesSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listCustomerTypes.remove(customerTypesSelect);
					}else{
						current.executeScript(
								"swaldesigntimer('Thất bại!', 'Không xóa được!','error',2000);");
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
			logger.error("CustomerTypesBean.delete:"+e.getMessage(),e);
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
				List<CustomerTypes> listCustomerTypesTemp = new ArrayList<CustomerTypes>();
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
					CustomerTypes lix = new CustomerTypes();
					lv2: while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								// mã loại khách hàng
								String malkh = Objects.toString(getCellValue(cell), null);
								if (malkh != null && !"".equals(malkh)) {
									lix.setCode(malkh);
								} else {
									break lv2;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								// tên loại khách hàng
								String name = Objects.toString(getCellValue(cell), null);
								lix.setName(name);
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								// kenh khách hàng
								String kenh = Objects.toString(getCellValue(cell), "0");
								if(kenh !=null && !"".equals(kenh)){
									CustomerChannelReqInfo cc=new CustomerChannelReqInfo();
									customerChannelService.selectById(Long.parseLong(kenh), cc);
									lix.setCustomer_channel(cc.getCustomer_channel());
								}
							} catch (Exception e) {
							}
							break;

						}
					}
					listCustomerTypesTemp.add(lix);
				}
				workBook = null;// free
				for (CustomerTypes it : listCustomerTypesTemp) {
					CustomerTypesReqInfo t = new CustomerTypesReqInfo();
					customerTypesService.selectByCode(it.getCode(), t);
					CustomerTypes p = t.getCustomer_types();
					t.setCustomer_types(it);
					if (p != null) {
						it.setId(p.getId());
						it.setLast_modifed_by(account.getMember().getName());
						it.setLast_modifed_date(new Date());
						customerTypesService.update(t);
					} else {
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						customerTypesService.insert(t);
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

	public CustomerTypes getCustomerTypesSelect() {
		return customerTypesSelect;
	}

	public void setCustomerTypesSelect(CustomerTypes customerTypesSelect) {
		this.customerTypesSelect = customerTypesSelect;
	}

	public CustomerTypes getCustomerTypesCrud() {
		return customerTypesCrud;
	}

	public void setCustomerTypesCrud(CustomerTypes customerTypesCrud) {
		this.customerTypesCrud = customerTypesCrud;
	}

	public List<CustomerTypes> getListCustomerTypes() {
		return listCustomerTypes;
	}

	public void setListCustomerTypes(List<CustomerTypes> listCustomerTypes) {
		this.listCustomerTypes = listCustomerTypes;
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CustomerChannel> getListCustomerChannel() {
		return listCustomerChannel;
	}

	public void setListCustomerChannel(List<CustomerChannel> listCustomerChannel) {
		this.listCustomerChannel = listCustomerChannel;
	}

	public CustomerChannel getCustomerChannel() {
		return customerChannel;
	}

	public void setCustomerChannel(CustomerChannel customerChannel) {
		this.customerChannel = customerChannel;
	}
	
}
