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
import lixco.com.entity.CarType;
import lixco.com.interfaces.ICarTypeService;
import lixco.com.reqInfo.CarTypeReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class CarTypeBean extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private ICarTypeService carTypeService;
	private CarType carTypeCrud;
	private CarType carTypeSelect;
	private List<CarType> listCarType;
	private Account account;

	@Override
	protected void initItem() {
		try{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			search();
			createNew();
		}catch (Exception e) {
			logger.error("CarTypeBean.initItem:"+e.getMessage(),e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void search(){
		try{
			listCarType=new ArrayList<CarType>();
			carTypeService.selectAll(listCarType);
		}catch (Exception e) {
			logger.error("CarTypeBean.search:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		carTypeCrud=new CarType();
		carTypeCrud.setCode(carTypeService.initCode());
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(carTypeSelect !=null){
				if(allowDelete(new Date())){
					if(carTypeService.deleteById(carTypeSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listCarType.remove(carTypeSelect);
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
			logger.error("CarTypeBean.delete:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (carTypeCrud != null) {
				String code = carTypeCrud.getCode();
				String name = carTypeCrud.getName();
				if (code != null && !"".equals(code) && name != null && !"".equals(name)) {
					CarTypeReqInfo t = new CarTypeReqInfo(carTypeCrud);
					if (carTypeCrud.getId() == 0) {
						// check code đã tồn tại chưa
						if (allowSave(new Date())) {
							int chk=carTypeService.insert(t);
							switch (chk) {
							case 0:
								listCarType.add(0, carTypeCrud.clone());
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
							int chk=carTypeService.update(t);
							switch (chk) {
							case 0:
								listCarType.set(listCarType.indexOf(carTypeCrud), t.getCar_type());
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
			logger.error("CarTypeBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showEdit() {
		try {
			carTypeCrud = carTypeSelect.clone();
		} catch (Exception e) {
			logger.error("CarTypeBean.showEdit:" + e.getMessage(), e);
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
				List<CarType> listCarType = new ArrayList<CarType>();
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
					CarType lix = new CarType();
					 while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								//ma old
								String manhomold = Objects.toString(getCellValue(cell), null);
								if (manhomold != null && !"".equals(manhomold)) {
									lix.setOld_code(manhomold);
								} else {
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								//tên nhóm xe
								String name = Objects.toString(getCellValue(cell), null);
								lix.setName(name);
							} catch (Exception e) {
							}
							break;
						}
					}
					listCarType.add(lix);
				}
				workBook = null;// free
				for (CarType it : listCarType) {
					CarTypeReqInfo t = new CarTypeReqInfo();
					carTypeService.selectByCodeOld(it.getOld_code(), t);
					CarType p = t.getCar_type();
					t.setCar_type(it);
					if (p != null) {
						it.setId(p.getId());
						carTypeService.update(t);
					} else {
						//init code new 
						String code=carTypeService.initCode();
						it.setCode(code);
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						carTypeService.insert(t);
					}
				}
				search();
				notify.success();
			}
		} catch (Exception e) {
			logger.error("CarTypeBean.loadExcel:" + e.getMessage(), e);
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

	public CarType getCarTypeCrud() {
		return carTypeCrud;
	}

	public void setCarTypeCrud(CarType carTypeCrud) {
		this.carTypeCrud = carTypeCrud;
	}

	public CarType getCarTypeSelect() {
		return carTypeSelect;
	}

	public void setCarTypeSelect(CarType carTypeSelect) {
		this.carTypeSelect = carTypeSelect;
	}

	public List<CarType> getListCarType() {
		return listCarType;
	}

	public void setListCarType(List<CarType> listCarType) {
		this.listCarType = listCarType;
	}

}
