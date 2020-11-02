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
import lixco.com.entity.Area;
import lixco.com.entity.City;
import lixco.com.entity.Country;
import lixco.com.interfaces.IAreaService;
import lixco.com.interfaces.ICityService;
import lixco.com.interfaces.ICountryService;
import lixco.com.reqInfo.AreaReqInfo;
import lixco.com.reqInfo.CityReqInfo;
import lixco.com.reqInfo.CountryReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class CityBean extends AbstractBean {
	private static final long serialVersionUID = 1233308831878395451L;
	@Inject
	private Logger logger;
	@Inject
	private ICityService cityService;
	@Inject
	private ICountryService countryService;
	@Inject
	private IAreaService areaService;
	private City cityCrud;
	private City citySelect;
	private List<City> listCity;
	private String cityCode;
	private String cityName;
	private Country country;
	private Area area;
	private List<Area> listArea;
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
			listArea=new ArrayList<Area>();
			areaService.selectAll(listArea);
			cityCrud=new City();
		}catch(Exception e){
			logger.error("CityBean.initItem:"+e.getMessage(),e);
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
			listCity = new ArrayList<City>();
			PagingInfo page = new PagingInfo();
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("city_code", cityCode);
			jsonInfo.addProperty("city_name", cityName);
			jsonInfo.addProperty("country_id", country != null ? country.getId() : 0);
			jsonInfo.addProperty("area_id", area != null ? area.getId() : 0);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("city_info", jsonInfo);
			json.add("page", jsonPage);
			cityService.search(JsonParserUtil.getGson().toJson(json), page, listCity);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		} catch (Exception e) {
			logger.error("CityBean.search:" + e.getMessage(), e);
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
			logger.error("CityBean.initRowPerPage:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listCity = new ArrayList<City>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("city_code",cityCode);
			jsonInfo.addProperty("city_name", cityName);
			jsonInfo.addProperty("country_id", country != null ? country.getId() : 0);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("city_info", jsonInfo);
			json.add("page", jsonPage);
			cityService.search(JsonParserUtil.getGson().toJson(json), page, listCity);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("CityBean.paginatorChange:" + e.getMessage(), e);
		}
	}
	public List<Country> completeCountry(String text){
		try{
			List<Country> list=new ArrayList<>();
			countryService.complete(FormatHandler.getInstance().converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("CityBean.completeCountry:" + e.getMessage(), e);
		}
		return null;
	}

	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (cityCrud != null) {
				String cityCode = cityCrud.getCity_code();
				String cityName = cityCrud.getCity_name();
				Country country = cityCrud.getCountry();
				Area area=cityCrud.getArea();
				if (cityCode != null && cityCode != "" && cityName != null && cityName != "" && country != null && area !=null) {
					CityReqInfo t = new CityReqInfo(cityCrud);
					if (cityCrud.getId() == 0) {
						//check code đã tồn tại chưa
						if(allowSave(new Date())){
							if(cityService.checkCityCode(cityCode,0)==0){
								cityCrud.setCreated_date(new Date());
								cityCrud.setCreated_by(account.getMember().getName());
								if(cityService.insert(t)!=-1){
									current.executeScript("swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listCity.add(0,cityCrud);
								}else{
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã thành phố đã tồn tại!','warning',2000);");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						//check code update đã tồn tại chưa
						if(cityService.checkCityCode(cityCode, cityCrud.getId())==0){
							if(allowUpdate(new Date())){
								cityCrud.setLast_modifed_by(account.getMember().getName());
								cityCrud.setLast_modifed_date(new Date());
								if(cityService.update(t)!=-1){
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									listCity.set(listCity.indexOf(cityCrud),cityCrud);
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
									"swaldesigntimer('Cảnh báo', 'Mã thành phố đã tồn tại!','warning',2000);");
						}
					}
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin thành phố không đầy đủ, điền đủ thông tin chứa(*)!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("CityBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}
	public void showEdit(){
		try{
			cityCrud=citySelect;
		}catch(Exception e){
			logger.error("CityBean.showEdit:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		cityCrud=new City();
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(citySelect !=null){
				if(allowDelete(new Date())){
					if(cityService.deleteById(citySelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listCity.remove(citySelect);
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
			logger.error("CityBean.delete:"+e.getMessage(),e);
		}
	}
	public void showDialogUpload(){
		try{
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('uploadpdffile').show();");
		}catch(Exception e){
			logger.error("CityBean.showDialogUpload:"+e.getMessage(),e);
		}
	}
	public void loadExcel(FileUploadEvent event){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<City> listCityTemp = new ArrayList<City>();
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
					City lix = new City();
					lv2: while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								// mã thành phố
								String mtp = Objects.toString(getCellValue(cell), null);
								if (mtp != null && !"".equals(mtp)) {
									lix.setCity_code(mtp);
								} else {
									break lv2;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								// tên thành phố
								String tentp = Objects.toString(getCellValue(cell), null);
								lix.setCity_name(tentp);
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								// mã khu vực
								String makv = Objects.toString(getCellValue(cell), null);
								if(makv !=null && !"".equals(makv)){
									AreaReqInfo af=new AreaReqInfo();
									areaService.selectByCode(makv,af);
									lix.setArea(af.getArea());
								}
							} catch (Exception e) {
							}
							break;
						case 6:
							try {
								// mã quốc gia
								String maqg = Objects.toString(getCellValue(cell), null);
								if(maqg !=null && !"".equals(maqg)){
									CountryReqInfo cf=new CountryReqInfo();
									countryService.selectByCode(maqg,cf);
									lix.setCountry(cf.getCountry());
								}
							} catch (Exception e) {
							}
							break;
						
						}
					}
					listCityTemp.add(lix);
				}
				workBook = null;// free
				for (City it : listCityTemp) {
					CityReqInfo t = new CityReqInfo();
					cityService.selectByCode(it.getCity_code(), t);
					City p = t.getCity();
					t.setCity(it);
					if (p != null) {
						it.setId(p.getId());
						it.setLast_modifed_by(account.getMember().getName());
						it.setLast_modifed_date(new Date());
						cityService.update(t);
					} else {
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						cityService.insert(t);
					}
				}
				search();
				notify.success();
			}
		}catch(Exception e){
			logger.error("CityBean.loadExcel:"+e.getMessage(),e);
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
	public City getCityCrud() {
		return cityCrud;
	}

	public void setCityCrud(City cityCrud) {
		this.cityCrud = cityCrud;
	}

	public City getCitySelect() {
		return citySelect;
	}

	public void setCitySelect(City citySelect) {
		this.citySelect = citySelect;
	}

	public List<City> getListCity() {
		return listCity;
	}

	public void setListCity(List<City> listCity) {
		this.listCity = listCity;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
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

	public List<Area> getListArea() {
		return listArea;
	}

	public void setListArea(List<Area> listArea) {
		this.listArea = listArea;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
}
