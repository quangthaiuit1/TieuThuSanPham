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
import lixco.com.entity.Car;
import lixco.com.entity.CarOwner;
import lixco.com.entity.CarType;
import lixco.com.interfaces.ICarOwnerService;
import lixco.com.interfaces.ICarService;
import lixco.com.interfaces.ICarTypeService;
import lixco.com.reqInfo.CarOwnerReqInfo;
import lixco.com.reqInfo.CarReqInfo;
import lixco.com.reqInfo.CarTypeReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class CarBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private ICarService carService;
	@Inject
	private ICarTypeService carTypeService;
	@Inject
	private ICarOwnerService carOwnerService;
	private Car carCrud;
	private Car carSelect;
	private List<Car> listCar;
	private List<CarOwner> listCarOwner;
	private List<CarType> listCarType;
	private CarType carTypeSearch;
	private CarOwner carOwnerSearch;
	private String licensePlateSearch;
	private String phoneNumberSearch;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	private Account account;

	@Override
	protected void initItem() {
		try {
			listCarOwner = new ArrayList<>();
			carOwnerService.selectAll(listCarOwner);
			listCarType = new ArrayList<>();
			carTypeService.selectAll(listCarType);
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			search();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			createNew();
		} catch (Exception e) {
			logger.error("CarBean.initItem:" + e.getMessage(), e);
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
			listCar = new ArrayList<Car>();
			/*
			 * { car_info:{car_owner_id:0,car_type_id:0,license_plate:'',
			 * phone_number:''}, page:{page_index:0, page_size:0}}
			 */
			PagingInfo page = new PagingInfo();
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("car_owner_id", carOwnerSearch == null ? 0 : carOwnerSearch.getId());
			jsonInfo.addProperty("car_type_id", carTypeSearch == null ? 0 : carTypeSearch.getId());
			jsonInfo.addProperty("license_plate", licensePlateSearch);
			jsonInfo.addProperty("phone_number", phoneNumberSearch);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("car_info", jsonInfo);
			json.add("page", jsonPage);
			carService.search(JsonParserUtil.getGson().toJson(json), page, listCar);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		} catch (Exception e) {
			logger.error("CarBean.search:" + e.getMessage(), e);
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
			logger.error("CarBean.initRowPerPage:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			/*
			 * { car_info:{car_owner_id:0,car_type_id:0,license_plate:'',
			 * phone_number:''}, page:{page_index:0, page_size:0}}
			 */
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listCar = new ArrayList<Car>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("car_owner_id", carOwnerSearch == null ? 0 : carOwnerSearch.getId());
			jsonInfo.addProperty("car_type_id", carTypeSearch == null ? 0 : carTypeSearch.getId());
			jsonInfo.addProperty("license_plate", licensePlateSearch);
			jsonInfo.addProperty("phone_number", phoneNumberSearch);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("car_info", jsonInfo);
			json.add("page", jsonPage);
			carService.search(JsonParserUtil.getGson().toJson(json), page, listCar);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("CarBean.paginatorChange:" + e.getMessage(), e);
		}
	}

	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (carCrud != null) {
				String licensePlate = carCrud.getLicense_plate();
				if (licensePlate != null && !"".equals(licensePlate)) {
					CarReqInfo t = new CarReqInfo(carCrud);
					if (carCrud.getId() == 0) {
						// check code đã tồn tại chưa
						if (allowSave(new Date())) {
							carCrud.setCreated_date(new Date());
							carCrud.setCreated_by(account.getMember().getName());
							if (carService.insert(t) != -1) {
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
								listCar.add(0, carCrud.clone());
							} else {
								current.executeScript("swaldesigntimer('Thất bại!', 'Thêm thất bại!','error',2000);");
							}

						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					} else {
						// check code update đã tồn tại chưa
						if (allowUpdate(new Date())) {
							carCrud.setLast_modifed_by(account.getMember().getName());
							carCrud.setLast_modifed_date(new Date());
							if (carService.update(t) != -1) {
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								listCar.set(listCar.indexOf(carCrud), t.getCar());
							} else {
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Cập nhật thất bại!','error',2000);");
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ, điền đủ thông tin chứa(*)','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("CarBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showEdit() {
		try {
			carCrud = carSelect.clone();
		} catch (Exception e) {
			logger.error("CarBean.showEdit:" + e.getMessage(), e);
		}
	}

	public void createNew() {
		carCrud = new Car();
	}

	public void delete() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (carSelect != null) {
				if (allowDelete(new Date())) {
					if (carService.deleteById(carSelect.getId()) != -1) {
						current.executeScript("swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listCar.remove(carSelect);
					} else {
						current.executeScript("swaldesigntimer('Thất bại!', 'Không xóa được!','error',2000);");
					}
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
				}
			} else {
				current.executeScript("swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xóa!','warning',2000);");
			}
		} catch (Exception e) {
			logger.error("CarBean.delete:" + e.getMessage(), e);
		}
	}

	public void showDialogUpload() {
		try {
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('dlgup1').show();");
		} catch (Exception e) {
			logger.error("CarBean.showDialogUpload:" + e.getMessage(), e);
		}
	}

	public void loadExcel(FileUploadEvent event) {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<Car> listCarTemp = new ArrayList<Car>();
				Workbook workBook = getWorkbook(new ByteArrayInputStream(byteFile), part.getFileName());
				Sheet firstSheet = workBook.getSheetAt(0);
				Iterator<Row> rows = firstSheet.iterator();
				while (rows.hasNext()) {
					rows.next();
					rows.remove();
					break;
				}
				lv1: while (rows.hasNext()) {
					Row row = rows.next();
					Iterator<Cell> cells = row.cellIterator();
					Car lix = new Car();
					while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								// biến số xe
								String bienso = Objects.toString(getCellValue(cell), null);
								if (bienso != null && !"".equals(bienso)) {
									lix.setLicense_plate(bienso);
								} else {
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								// n xe
								String driver = Objects.toString(getCellValue(cell), null);
								lix.setDriver(driver);
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								// nhóm xe
								String manhomxe = Objects.toString(getCellValue(cell), null);
								if (manhomxe != null && !"".equals(manhomxe)) {
									CarTypeReqInfo ct = new CarTypeReqInfo();
									carTypeService.selectByCodeOld(manhomxe, ct);
									lix.setCar_type(ct.getCar_type());
								}
							} catch (Exception e) {
							}
							break;
						case 3:
							try {
								// chủ xe
								String chuxe = Objects.toString(getCellValue(cell), null);
								if (chuxe != null && !"".equals(chuxe)) {
									CarOwnerReqInfo co = new CarOwnerReqInfo();
									carOwnerService.selectByCodeOld(chuxe, co);
									lix.setCar_owner(co.getCar_owner());
								}
							} catch (Exception e) {
							}
							break;
						}
					}
					listCarTemp.add(lix);
				}
				workBook = null;// free
				for (Car it : listCarTemp) {
					CarReqInfo t = new CarReqInfo();
					carService.selectByLicensePlate(it.getLicense_plate(), t);
					Car p = t.getCar();
					t.setCar(it);
					if (p != null) {
						it.setId(p.getId());
						it.setLast_modifed_by(account.getMember().getName());
						it.setLast_modifed_date(new Date());
						carService.update(t);
					} else {
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						carService.insert(t);
					}
				}
				search();
				notify.success();
			}
		} catch (Exception e) {
			logger.error("CarBean.loadExcel:" + e.getMessage(), e);
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

	public Car getCarCrud() {
		return carCrud;
	}

	public void setCarCrud(Car carCrud) {
		this.carCrud = carCrud;
	}

	public Car getCarSelect() {
		return carSelect;
	}

	public void setCarSelect(Car carSelect) {
		this.carSelect = carSelect;
	}

	public List<Car> getListCar() {
		return listCar;
	}

	public void setListCar(List<Car> listCar) {
		this.listCar = listCar;
	}

	public List<CarOwner> getListCarOwner() {
		return listCarOwner;
	}

	public void setListCarOwner(List<CarOwner> listCarOwner) {
		this.listCarOwner = listCarOwner;
	}

	public List<CarType> getListCarType() {
		return listCarType;
	}

	public void setListCarType(List<CarType> listCarType) {
		this.listCarType = listCarType;
	}

	public CarType getCarTypeSearch() {
		return carTypeSearch;
	}

	public void setCarTypeSearch(CarType carTypeSearch) {
		this.carTypeSearch = carTypeSearch;
	}

	public CarOwner getCarOwnerSearch() {
		return carOwnerSearch;
	}

	public void setCarOwnerSearch(CarOwner carOwnerSearch) {
		this.carOwnerSearch = carOwnerSearch;
	}

	public String getLicensePlateSearch() {
		return licensePlateSearch;
	}

	public void setLicensePlateSearch(String licensePlateSearch) {
		this.licensePlateSearch = licensePlateSearch;
	}

	public String getPhoneNumberSearch() {
		return phoneNumberSearch;
	}

	public void setPhoneNumberSearch(String phoneNumberSearch) {
		this.phoneNumberSearch = phoneNumberSearch;
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
