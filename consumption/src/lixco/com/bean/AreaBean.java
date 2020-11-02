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
import lixco.com.entity.Area;
import lixco.com.interfaces.IAreaService;
import lixco.com.interfaces.IProcessLogicGoodsReceiptNoteService;
import lixco.com.reqInfo.AreaReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class AreaBean extends AbstractBean{
	private static final long serialVersionUID = -7808980188611689010L;
	@Inject
	private Logger logger;
	@Inject
	private IAreaService areaService;
	@Inject
	private IProcessLogicGoodsReceiptNoteService processLogicGoodsReceiptNoteService;
	private Area areaCrud;
	private Area areaSelect;
	private List<Area> listArea;
	private Account account;
	@Override
	protected void initItem() {
		try{
			search();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			createNew();
		}catch(Exception e){
			logger.error("AreaBean.initItem:"+e.getMessage(),e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void search(){
		try{
			listArea=new ArrayList<Area>();
			areaService.selectAll(listArea);
		}catch(Exception e){
			logger.error("AreaBean.search:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdate(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(areaCrud !=null){
				String code=areaCrud.getArea_code();
				String name=areaCrud.getArea_name();
				if(code !=null && code !="" && name !=null && name !=""){
					AreaReqInfo t=new AreaReqInfo(areaCrud);
					if(areaCrud.getId()==0){
						if(allowSave(new Date())){
							//check code
							if(areaService.checkAreaCode(code, 0)==0){
								areaCrud.setCreated_date(new Date());
								areaCrud.setCreated_by(account.getMember().getName());
								if(areaService.insert(t)!=-1){
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listArea.add(0, areaCrud);
								}else{
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã khu vực đã tồn tại!','warning',2000);");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						if(allowUpdate(new Date())){
							//check code
							if(areaService.checkAreaCode(code, areaCrud.getId())==0){
								areaCrud.setLast_modifed_date(new Date());
								areaCrud.setLast_modifed_by(account.getMember().getName());
								if(areaService.update(t) !=-1){
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									listArea.set(listArea.indexOf(areaCrud),areaCrud);
								}else{
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã khu vực đã tồn tại!','warning',2000);");
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
			logger.error("AreaBean.saveOrUpdate:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		areaCrud=new Area();
	}
	public void showEdit(){
		areaCrud=areaSelect;
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(areaSelect !=null){
				if(allowDelete(new Date())){
					if(areaService.deleteById(areaSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listArea.remove(areaSelect);
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
			logger.error("AreaBean.delete:"+e.getMessage(),e);
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
				List<Area> listAreaTemp = new ArrayList<Area>();
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
					Area lix = new Area();
					lv2: while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								// mã khu vực
								String makv = Objects.toString(getCellValue(cell), null);
								if (makv != null && !"".equals(makv)) {
									lix.setArea_code(makv);
								} else {
									break lv2;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								// tên khu vực
								String namekv = Objects.toString(getCellValue(cell), null);
								lix.setArea_name(namekv);
							} catch (Exception e) {
							}
							break;
						}
					}
					listAreaTemp.add(lix);
				}
				workBook = null;// free
				for (Area it : listAreaTemp) {
					AreaReqInfo t = new AreaReqInfo();
					areaService.selectByCode(it.getArea_code(), t);
					Area p = t.getArea();
					t.setArea(it);
					if (p != null) {
						it.setId(p.getId());
						it.setLast_modifed_by(account.getMember().getName());
						it.setLast_modifed_date(new Date());
						areaService.update(t);
					} else {
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						areaService.insert(t);
					}
				}
				search();
				notify.success();
			}
		} catch (Exception e) {
			logger.error("AreaBean.loadExcel:"+e.getMessage(),e);
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

	public Area getAreaCrud() {
		return areaCrud;
	}

	public void setAreaCrud(Area areaCrud) {
		this.areaCrud = areaCrud;
	}

	public Area getAreaSelect() {
		return areaSelect;
	}

	public void setAreaSelect(Area areaSelect) {
		this.areaSelect = areaSelect;
	}

	public List<Area> getListArea() {
		return listArea;
	}

	public void setListArea(List<Area> listArea) {
		this.listArea = listArea;
	}
}
