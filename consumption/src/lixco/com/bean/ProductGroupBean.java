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

import lixco.com.entity.ProductGroup;
import lixco.com.interfaces.IProductGroupService;
import lixco.com.reqInfo.ProductGroupReqInfo;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class ProductGroupBean extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IProductGroupService productGroupService;
	private ProductGroup productGroupCrud;
	private ProductGroup productGroupSelect;
	private List<ProductGroup> listProductGroup;
	private String code;
	private String name;

	@Override
	protected void initItem() {
		try{
			productGroupCrud=new ProductGroup();
			search();
		}catch(Exception e){
			logger.error("ProductGroupBean.initItem:"+e.getMessage(),e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void search(){
		try{
			listProductGroup=new ArrayList<ProductGroup>();
			productGroupService.search(code, name, listProductGroup);
		}catch (Exception e) {
			logger.error("ProductGroupBean.search:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		productGroupCrud=new ProductGroup();
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(productGroupCrud !=null){
				if(allowDelete(new Date())){
					if(productGroupService.deleteById(productGroupCrud.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listProductGroup.remove(productGroupCrud);
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
			logger.error("ProductGroupBean.delete:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (productGroupCrud != null) {
				String code = productGroupCrud.getCode();
				String name = productGroupCrud.getName();
				if (code != null && !"".equals(code) && name != null && !"".equals(name)) {
					ProductGroupReqInfo t = new ProductGroupReqInfo(productGroupCrud);
					if (productGroupCrud.getId() == 0) {
						// check code đã tồn tại chưa
						if (allowSave(new Date())) {
							if (productGroupService.checkCode(code, 0) == 0) {
								if (productGroupService.insert(t) != -1) {
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listProductGroup.add(0, productGroupCrud);
								} else {
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							} else {
								current.executeScript("swaldesigntimer('Cảnh báo', 'Mã đã tồn tại!','warning',2000);");
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					} else {
						// check code update đã tồn tại chưa
						if (allowUpdate(new Date())) {
							if (productGroupService.checkCode(code, productGroupCrud.getId()) == 0) {
								if (productGroupService.update(t) != -1) {
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									listProductGroup.set(listProductGroup.indexOf(productGroupCrud), productGroupCrud);
								} else {
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							} else {
								current.executeScript("swaldesigntimer('Cảnh báo', 'Mã đã tồn tại!','warning',2000);");
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
			logger.error("ProductGroupBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}
	public void showEdit(){
		try{
			productGroupCrud=productGroupSelect;
		}catch(Exception e){
			logger.error("ProductGroupBean.showEdit:"+e.getMessage(),e);
		}
	}
	public void showDialogUpload(){
		try{
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('uploadpdffile').show();");
		}catch(Exception e){
			logger.error("ProductGroupBean.showDialogUpload:"+e.getMessage(),e);
		}
	}

	public void loadExcel(FileUploadEvent event) {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<ProductGroup> listProductGroupTemp = new ArrayList<>();
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
					ProductGroup lix = new ProductGroup();
					lv2: while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								// mã nhóm sản phẩm
								String mansp = Objects.toString(getCellValue(cell), null);
								if (mansp != null && !"".equals(mansp)) {
									lix.setCode(mansp);
								} else {
									break lv2;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								// tên nhóm sản phẩm
								String name = Objects.toString(getCellValue(cell), null);
								lix.setName(name);
							} catch (Exception e) {
							}
							break;

						}
					}
					listProductGroupTemp.add(lix);

				}
				workBook = null;// free
				for (ProductGroup it : listProductGroupTemp) {
					ProductGroupReqInfo t = new ProductGroupReqInfo();
					productGroupService.selectByCode(it.getCode(), t);
					ProductGroup p = t.getProduct_Group();
					t.setProduct_Group(it);
					if (p != null) {
						it.setId(p.getId());
						productGroupService.update(t);
					} else {
						productGroupService.insert(t);
					}
				}
				search();
				notify.success();

			}
		} catch (Exception e) {
			logger.error("ProductGroupBean.loadExcel:" + e.getMessage(), e);
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

	public ProductGroup getProductGroupCrud() {
		return productGroupCrud;
	}

	public void setProductGroupCrud(ProductGroup productGroupCrud) {
		this.productGroupCrud = productGroupCrud;
	}

	public ProductGroup getProductGroupSelect() {
		return productGroupSelect;
	}

	public void setProductGroupSelect(ProductGroup productGroupSelect) {
		this.productGroupSelect = productGroupSelect;
	}

	public List<ProductGroup> getListProductGroup() {
		return listProductGroup;
	}

	public void setListProductGroup(List<ProductGroup> listProductGroup) {
		this.listProductGroup = listProductGroup;
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
	
}
