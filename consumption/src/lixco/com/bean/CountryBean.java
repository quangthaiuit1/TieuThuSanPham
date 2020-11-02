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
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.entity.Country;
import lixco.com.interfaces.ICountryService;
import lixco.com.reqInfo.CountryReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class CountryBean extends AbstractBean {
	private static final long serialVersionUID = 7341835812782558017L;
	@Inject
	private Logger logger;
	@Inject
	private ICountryService countryService;
	private Country countryCrud;
	private Country countrySelect;
	private List<Country> listCountry;
	private String countryCode;
	private String countryName;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	private Account account;
	@Override
	protected void initItem() {
		try{
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			search();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			countryCrud=new Country();
		}catch(Exception e){
			logger.error("CountryBean.initItem:"+e.getMessage(),e);
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
			listCountry = new ArrayList<Country>();
			PagingInfo page = new PagingInfo();
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("country_code", countryCode);
			jsonInfo.addProperty("country_name", countryName);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("country_info", jsonInfo);
			json.add("page", jsonPage);
			countryService.search(JsonParserUtil.getGson().toJson(json), page, listCountry);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		} catch (Exception e) {
			logger.error("CountryBean.search:" + e.getMessage(), e);
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
			logger.error("CountryBean.initRowPerPage:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listCountry = new ArrayList<Country>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("country_code",countryCode);
			jsonInfo.addProperty("country_name", countryName);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("country_info", jsonInfo);
			json.add("page", jsonPage);
			countryService.search(JsonParserUtil.getGson().toJson(json), page, listCountry);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("CountryBean.paginatorChange:" + e.getMessage(), e);
		}
	}

	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (countryCrud != null) {
				String countryCode = countryCrud.getCountry_code();
				String countryName = countryCrud.getCountry_name();
				if (countryCode != null && countryCode != "" && countryName != null && countryName != "" ) {
					CountryReqInfo t = new CountryReqInfo(countryCrud);
					if (countryCrud.getId() == 0) {
						//check code đã tồn tại chưa
						if(allowSave(new Date())){
							if(countryService.checkCountryCode(countryCode,0)==0){
								countryCrud.setCreated_date(new Date());
								countryCrud.setCreated_by(account.getMember().getName());
								if(countryService.insert(t)!=-1){
									current.executeScript("swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listCountry.add(0,countryCrud);
								}else{
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã quốc gia đã tồn tại!','warning',2000);");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						//check code update đã tồn tại chưa
						if(countryService.checkCountryCode(countryCode, countryCrud.getId())==0){
							if(allowUpdate(new Date())){
								countryCrud.setLast_modifed_by(account.getMember().getName());
								countryCrud.setLast_modifed_date(new Date());
								if(countryService.update(t)!=-1){
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									listCountry.set(listCountry.indexOf(countryCrud),countryCrud);
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
									"swaldesigntimer('Cảnh báo', 'Mã quốc gia đã tồn tại!','warning',2000);");
						}
					}
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin thành phố không đầy đủ, điền đủ thông tin chứa(*)!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("CountryBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}
	public void showEdit(){
		try{
			countryCrud=countrySelect;
		}catch(Exception e){
			logger.error("CountryBean.showEdit:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		countryCrud=new Country();
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(countrySelect !=null){
				if(allowDelete(new Date())){
					if(countryService.deleteById(countrySelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listCountry.remove(countrySelect);
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
			logger.error("CountryBean.delete:"+e.getMessage(),e);
		}
	}
	public void showDialogUpload(){
		try{
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('uploadpdffile').show();");
		}catch(Exception e){
			logger.error("CountryBean.showDialogUpload:"+e.getMessage(),e);
		}
	}
	public void loadExcel(FileUploadEvent event){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<Country> listCountryTemp = new ArrayList<Country>();
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
					Country lix = new Country();
					lv2: while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								// mã quốc gia
								String maqg = Objects.toString(getCellValue(cell), null);
								if (maqg != null && !"".equals(maqg)) {
									lix.setCountry_code(maqg);
								} else {
									break lv2;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								// tên quốc gia
								String nameqg = Objects.toString(getCellValue(cell), null);
								lix.setCountry_name(nameqg);
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								// tên quốc gia
								String name_en = Objects.toString(getCellValue(cell), null);
								lix.setEn_name(name_en);
							} catch (Exception e) {
							}
							break;
						}
					}
					listCountryTemp.add(lix);
				}
				workBook = null;// free
				for (Country it : listCountryTemp) {
					CountryReqInfo t = new CountryReqInfo();
					countryService.selectByCode(it.getCountry_code(), t);
					Country p = t.getCountry();
					t.setCountry(it);
					if (p != null) {
						it.setId(p.getId());
						it.setLast_modifed_by(account.getMember().getName());
						it.setLast_modifed_date(new Date());
						countryService.update(t);
					} else {
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						countryService.insert(t);
					}
				}
				search();
				notify.success();
			}
		}catch(Exception e){
			logger.error("CountryBean.loadExcel:"+e.getMessage(),e);
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

	public Country getCountryCrud() {
		return countryCrud;
	}

	public void setCountryCrud(Country countryCrud) {
		this.countryCrud = countryCrud;
	}

	public Country getCountrySelect() {
		return countrySelect;
	}

	public void setCountrySelect(Country countrySelect) {
		this.countrySelect = countrySelect;
	}

	public List<Country> getListCountry() {
		return listCountry;
	}

	public void setListCountry(List<Country> listCountry) {
		this.listCountry = listCountry;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
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
	

}
