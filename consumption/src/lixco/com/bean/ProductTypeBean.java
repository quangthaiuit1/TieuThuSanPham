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

import lixco.com.entity.ProductType;
import lixco.com.interfaces.IProductTypeService;
import lixco.com.reqInfo.ProductTypeReqInfo;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;


@Named
@ViewScoped
public class ProductTypeBean  extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IProductTypeService productTypeService;
	private ProductType produtTypeCrud;
	private ProductType productTypeSelect;
	private List<ProductType> listProductType;
	private String code;
	private String name;
	@Override
	protected void initItem() {
		try{
			search();
			produtTypeCrud=new ProductType();
		}catch (Exception e) {
			logger.error("ProductTypeBean.initItem:"+e.getMessage(),e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void search(){
		try{
			listProductType=new ArrayList<ProductType>();
			productTypeService.search(code, name, listProductType);
		}catch (Exception e) {
			logger.error("ProductTypeBean.search:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		produtTypeCrud=new ProductType();
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(productTypeSelect !=null){
				if(allowDelete(new Date())){
					if(productTypeService.deleteById(productTypeSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listProductType.remove(productTypeSelect);
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
			logger.error("ProductTypeBean.delete:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (produtTypeCrud != null) {
				String code = produtTypeCrud.getCode();
				String name = produtTypeCrud.getName();
				if (code != null && !"".equals(code) && name != null && !"".equals(name)) {
					ProductTypeReqInfo t = new ProductTypeReqInfo(produtTypeCrud);
					if (produtTypeCrud.getId() == 0) {
						// check code đã tồn tại chưa
						if (allowSave(new Date())) {
							if (productTypeService.checkCode(code, 0) == 0) {
								if (productTypeService.insert(t) != -1) {
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listProductType.add(0, produtTypeCrud);
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
							if (productTypeService.checkCode(code, produtTypeCrud.getId()) == 0) {
								if (productTypeService.update(t) != -1) {
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									listProductType.set(listProductType.indexOf(produtTypeCrud), produtTypeCrud);
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
			logger.error("ProductTypeBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showEdit() {
		produtTypeCrud = productTypeSelect;

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
				List<ProductType> listProductTypeTemp = new ArrayList<ProductType>();
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
					ProductType lix = new ProductType();
					lv2: while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								// mã loại sản phẩm
								String malsp = Objects.toString(getCellValue(cell), null);
								if (malsp != null && !"".equals(malsp)) {
									lix.setCode(malsp);
								} else {
									break lv2;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								// tên loại sản phẩm
								String name_type = Objects.toString(getCellValue(cell), null);
								lix.setName(name_type);
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								// tên loại sản phẩm eng
								String name_ = Objects.toString(getCellValue(cell), null);
								lix.setEn_name(name_);
							} catch (Exception e) {
							}
							break;

						}
					}
					listProductTypeTemp.add(lix);
				}
				workBook = null;// free
				for (ProductType it : listProductTypeTemp) {
					ProductTypeReqInfo t = new ProductTypeReqInfo();
					productTypeService.selectByCode(it.getCode(), t);
					ProductType p = t.getProduct_type();
					t.setProduct_type(it);
					if (p != null) {
						it.setId(p.getId());
						productTypeService.update(t);
					} else {
						productTypeService.insert(t);
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

	public ProductType getProdutTypeCrud() {
		return produtTypeCrud;
	}

	public void setProdutTypeCrud(ProductType produtTypeCrud) {
		this.produtTypeCrud = produtTypeCrud;
	}

	public ProductType getProductTypeSelect() {
		return productTypeSelect;
	}

	public void setProductTypeSelect(ProductType productTypeSelect) {
		this.productTypeSelect = productTypeSelect;
	}

	public List<ProductType> getListProductType() {
		return listProductType;
	}

	public void setListProductType(List<ProductType> listProductType) {
		this.listProductType = listProductType;
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
