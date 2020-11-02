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

import lixco.com.common.SessionHelper;
import lixco.com.entity.CarOwner;
import lixco.com.interfaces.ICarOwnerService;
import lixco.com.reqInfo.CarOwnerReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class CarOwnerBean  extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private ICarOwnerService carOwnerService;
	private CarOwner carOwnerCrud;
	private CarOwner carOwnerSelect;
	private List<CarOwner> listCarOwner;
	private Account account;
	@Override
	protected void initItem() {
		try{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			search();
			carOwnerCrud=new CarOwner();
		}catch (Exception e) {
			logger.error("CarOwnerBean.initItem:"+e.getMessage(),e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void search(){
		try{
			listCarOwner=new ArrayList<CarOwner>();
			carOwnerService.selectAll(listCarOwner);
		}catch (Exception e) {
			logger.error("CarOwnerBean.search:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		carOwnerCrud=new CarOwner();
		carOwnerCrud.setCode(carOwnerService.initCode());
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(carOwnerSelect !=null){
				if(allowDelete(new Date())){
					if(carOwnerService.deleteById(carOwnerSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listCarOwner.remove(carOwnerSelect);
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
			logger.error("CarOwnerBean.delete:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (carOwnerCrud != null) {
				String code = carOwnerCrud.getCode();
				String name = carOwnerCrud.getName();
				if (code != null && !"".equals(code) && name != null && !"".equals(name)) {
					CarOwnerReqInfo t = new CarOwnerReqInfo(carOwnerCrud);
					if (carOwnerCrud.getId() == 0) {
						// check code đã tồn tại chưa
						if (allowSave(new Date())) {
							int chk=carOwnerService.insert(t);
							switch (chk) {
							case 0:
								listCarOwner.add(0, carOwnerCrud.clone());
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
								break;
							case -2:
								//duplicate code
								current.executeScript("swaldesigntimer('Cảnh báo', 'Mã đã tồn tại!','warning',2000);");
								break;

							default:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Thêm thất bại!','warning',2000);");
								break;
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					} else {
						// check code update đã tồn tại chưa
						if (allowUpdate(new Date())) {
							int chk=carOwnerService.update(t);
							switch (chk) {
							case 0:
								listCarOwner.set(listCarOwner.indexOf(carOwnerCrud),t.getCar_owner());
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								break;
							case -2:
								current.executeScript("swaldesigntimer('Cảnh báo', 'Mã đã tồn tại!','warning',2000);");
								break;
							default:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Cập nhật thất bại!','error',2000);");
								break;
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ, điền đủ thông tin chứa(*)!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("CarOwnerBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showEdit() {
		try {
			carOwnerCrud = carOwnerSelect.clone();
		} catch (Exception e) {
			logger.error("CarOwnerBean.showEdit:" + e.getMessage(), e);
		}

	}
	public void showDialogUpload(){
		PrimeFaces current = PrimeFaces.current();
		current.executeScript("PF('dlgup1').show();");
	}

	public void loadExcel(FileUploadEvent event) {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<CarOwner> listCarOwner = new ArrayList<CarOwner>();
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
					CarOwner lix = new CarOwner();
					 while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								//machuxe old
								String maold = Objects.toString(getCellValue(cell), null);
								if (maold != null && !"".equals(maold)) {
									lix.setOld_code(maold);
								} else {
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								//tên chủ xe
								String name = Objects.toString(getCellValue(cell), null);
								lix.setName(name);
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								// sodt
								String std = Objects.toString(getCellValue(cell), null);
								lix.setCell_phone(std);
							} catch (Exception e) {
							}
							break;
						case 3:
							try {
								// dia chỉ
								String dc = Objects.toString(getCellValue(cell), null);
								lix.setAddress(dc);
							} catch (Exception e) {
							}
							break;

						}
					}
					listCarOwner.add(lix);
				}
				workBook = null;// free
				for (CarOwner it : listCarOwner) {
					CarOwnerReqInfo t = new CarOwnerReqInfo();
					carOwnerService.selectByCodeOld(it.getOld_code(), t);
					CarOwner p = t.getCar_owner();
					t.setCar_owner(it);
					if (p != null) {
						it.setId(p.getId());
						carOwnerService.update(t);
					} else {
						//init code new 
						String code=carOwnerService.initCode();
						it.setCode(code);
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						carOwnerService.insert(t);
					}
				}
				search();
				notify.success();
			}
		} catch (Exception e) {
			logger.error("CarOwnerBean.loadExcel:" + e.getMessage(), e);
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

	public CarOwner getCarOwnerCrud() {
		return carOwnerCrud;
	}

	public void setCarOwnerCrud(CarOwner carOwnerCrud) {
		this.carOwnerCrud = carOwnerCrud;
	}

	public CarOwner getCarOwnerSelect() {
		return carOwnerSelect;
	}

	public void setCarOwnerSelect(CarOwner carOwnerSelect) {
		this.carOwnerSelect = carOwnerSelect;
	}

	public List<CarOwner> getListCarOwner() {
		return listCarOwner;
	}

	public void setListCarOwner(List<CarOwner> listCarOwner) {
		this.listCarOwner = listCarOwner;
	}

}
