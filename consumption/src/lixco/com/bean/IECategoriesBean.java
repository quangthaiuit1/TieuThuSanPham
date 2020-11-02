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
import lixco.com.entity.IECategories;
import lixco.com.interfaces.IIECategoriesService;
import lixco.com.reqInfo.IECategoriesReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named(value="iECategoriesBean")
@ViewScoped
public class IECategoriesBean extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IIECategoriesService ieCategoriesService;
	private IECategories ieCategoriesCrud;
	private IECategories ieCategoriesSelect;
	private List<IECategories> listIECategories;
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
			logger.error("IECategoriesBean.initItem:"+e.getMessage(),e);
		}
	}

	public void search(){
		try{
			listIECategories=new ArrayList<IECategories>();
			ieCategoriesService.selectAll(listIECategories);
		}catch (Exception e) {
			logger.error("IECategoriesBean.search:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		ieCategoriesCrud=new IECategories();
		ieCategoriesCrud.setCode(ieCategoriesService.initCode());
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(ieCategoriesSelect !=null){
				if(allowDelete(new Date())){
					if(ieCategoriesService.deleteById(ieCategoriesSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listIECategories.remove(ieCategoriesSelect);
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
			logger.error("IECategoriesBean.delete:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (ieCategoriesCrud != null) {
				String code = ieCategoriesCrud.getCode();
				String content = ieCategoriesCrud.getContent();
				if (code != null && !"".equals(code) && content != null && !"".equals(content)) {
					IECategoriesReqInfo t = new IECategoriesReqInfo(ieCategoriesCrud);
					if (ieCategoriesCrud.getId() == 0) {
						// check code đã tồn tại chưa
						if (allowSave(new Date())) {
							int chk=ieCategoriesService.insert(t);
							switch (chk) {
							case 0:
								listIECategories.add(0, ieCategoriesCrud.clone());
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
							int chk=ieCategoriesService.update(t);
							switch (chk) {
							case 0:
								listIECategories.set(listIECategories.indexOf(ieCategoriesCrud), t.getIe_categories());
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
			logger.error("IECategoriesBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showEdit() {
		try {
			ieCategoriesCrud = ieCategoriesSelect.clone();
		} catch (Exception e) {
			logger.error("IECategoriesBean.showEdit:" + e.getMessage(), e);
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
				List<IECategories> listIECategoriesTemp = new ArrayList<IECategories>();
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
					IECategories lix = new IECategories();
					 while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								//mã nhập xuất old
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
								//Nội dung
								String content = Objects.toString(getCellValue(cell), null);
								lix.setContent(content);
							} catch (Exception e) {
							}
							break;
						}
					}
					 listIECategoriesTemp.add(lix);
				}
				workBook = null;// free
				for (IECategories it : listIECategoriesTemp) {
					IECategoriesReqInfo t = new IECategoriesReqInfo();
					ieCategoriesService.selectByCodeOld(it.getOld_code(), t);
					IECategories p = t.getIe_categories();
					t.setIe_categories(it);
					if (p != null) {
						it.setId(p.getId());
						ieCategoriesService.update(t);
					} else {
						//init code new 
						String code=ieCategoriesService.initCode();
						it.setCode(code);
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						ieCategoriesService.insert(t);
					}
				}
				search();
				notify.success();
			}
		} catch (Exception e) {
			logger.error("IECategoriesBean.loadExcel:" + e.getMessage(), e);
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

	@Override
	protected Logger getLogger() {
		return logger;
	}

	public IECategories getIeCategoriesCrud() {
		return ieCategoriesCrud;
	}

	public void setIeCategoriesCrud(IECategories ieCategoriesCrud) {
		this.ieCategoriesCrud = ieCategoriesCrud;
	}

	public IECategories getIeCategoriesSelect() {
		return ieCategoriesSelect;
	}

	public void setIeCategoriesSelect(IECategories ieCategoriesSelect) {
		this.ieCategoriesSelect = ieCategoriesSelect;
	}

	public List<IECategories> getListIECategories() {
		return listIECategories;
	}

	public void setListIECategories(List<IECategories> listIECategories) {
		this.listIECategories = listIECategories;
	}
}
