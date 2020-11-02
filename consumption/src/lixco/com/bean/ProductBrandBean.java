package lixco.com.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import com.iamloivx.unicode.Converter;
import com.iamloivx.unicode.vietnamese.ConverterFactory;
import com.iamloivx.unicode.vietnamese.ConverterFactoryImpl;
import com.iamloivx.unicode.vietnamese.VietnameseEncoding.Encoding;

import lixco.com.common.FormatHandler;
import lixco.com.common.ImageHandler;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.entity.ProductBrand;
import lixco.com.interfaces.IProductBrandService;
import lixco.com.reqInfo.ProductBrandReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class ProductBrandBean extends AbstractBean {
	private static final long serialVersionUID = 7751390318581104133L;
	@Inject
	private Logger logger;
	@Inject
	private IProductBrandService productBrandService;
	private ProductBrand productBrandCrud;
	private ProductBrand productBrandSelect;
	private List<ProductBrand> listProductBrand;
	private String pBrandCode;
	private String pBrandName;
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
			productBrandCrud=new ProductBrand();
		}catch(Exception e){
			logger.error("ProductBrandBean.initItem:"+e.getMessage(),e);
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
			listProductBrand = new ArrayList<ProductBrand>();
			PagingInfo page = new PagingInfo();
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("pbrand_code", FormatHandler.getInstance().converViToEn(pBrandCode));
			jsonInfo.addProperty("pbrand_name", FormatHandler.getInstance().converViToEn(pBrandName));
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("pbrand_info", jsonInfo);
			json.add("page", jsonPage);
			productBrandService.search(JsonParserUtil.getGson().toJson(json), page, listProductBrand);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		} catch (Exception e) {
			logger.error("ProductBrandBean.search:" + e.getMessage(), e);
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
			logger.error("ProductBrandBean.initRowPerPage:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listProductBrand = new ArrayList<ProductBrand>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("pbrand_code",FormatHandler.getInstance().converViToEn(pBrandCode));
			jsonInfo.addProperty("pbrand_name", FormatHandler.getInstance().converViToEn(pBrandName));
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("pbrand_info", jsonInfo);
			json.add("page", jsonPage);
			productBrandService.search(JsonParserUtil.getGson().toJson(json), page, listProductBrand);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("ProductBrandBean.paginatorChange:" + e.getMessage(), e);
		}
	}

	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (productBrandCrud != null) {
				String pBrandCode = productBrandCrud.getPbrand_code();
				String pBrandName = productBrandCrud.getPbrand_name();
				if (pBrandCode != null && pBrandCode != "" && pBrandName != null && pBrandName != "") {
					ProductBrandReqInfo t = new ProductBrandReqInfo(productBrandCrud);
					if (productBrandCrud.getId() == 0) {
						// check code đã tồn tại chưa
						if (allowSave(new Date())) {
							if (productBrandService.checkProductBrandCode(pBrandCode, 0) == 0) {
								productBrandCrud.setCreated_date(new Date());
								productBrandCrud.setCreated_by(account.getMember().getName());
								if (productBrandService.insert(t) != -1) {
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listProductBrand.add(0, productBrandCrud);
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
							if (productBrandService.checkProductBrandCode(pBrandCode, productBrandCrud.getId()) == 0) {
								productBrandCrud.setLast_modifed_by(account.getMember().getName());
								productBrandCrud.setLast_modifed_date(new Date());
								if (productBrandService.update(t) != -1) {
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									listProductBrand.set(listProductBrand.indexOf(productBrandCrud), productBrandCrud);
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
			logger.error("ProductBrandBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}
	public void showEdit(){
		try{
			productBrandCrud=productBrandSelect;
		}catch(Exception e){
			logger.error("ProductBrandBean.showEdit:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		productBrandCrud=new ProductBrand();
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(productBrandSelect !=null){
				if(allowDelete(new Date())){
					if(productBrandService.deleteById(productBrandSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listProductBrand.remove(productBrandSelect);
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
			logger.error("ProductBrandBean.delete:"+e.getMessage(),e);
		}
	}
	public void showDialogUpload(){
		try{
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('uploadpdffile').show();");
		}catch(Exception e){
			logger.error("ProductBrandBean.showDialogUpload:"+e.getMessage(),e);
		}
	}
	private String convertString(String str) {
		try {
			ConverterFactory converterFactory = new ConverterFactoryImpl();
			Converter converter = converterFactory.build(Encoding.VNI);
			str = str.replaceAll("\"", "");
			String result = converter.convert(str).toUpperCase().replaceAll("ƯÛ", "Ử");
			return result;
		} catch (Exception e) {
			logger.error("ProductBean.convertString:" + e.getMessage(), e);
		}
		return "";
	}
	private Workbook getWorkbook(FileInputStream inputStream, String excelFilePath) throws IOException {
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
	public void loadExcel(FileUploadEvent event){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		OutputStream outStream = null;
		FileInputStream inputStream = null;
		try {
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				String filename = part.getFileName();
				int index = filename.lastIndexOf(".");
				String type = filename.substring(index);
				String url = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/upload/productbrand");
				String nameNewFile = ImageHandler.getInstance().generateUniqueFileName(type, url);
				File fileSave = new File(url, nameNewFile + "." + type);
				outStream = new FileOutputStream(fileSave);
				outStream.write(byteFile);
				List<ProductBrand> listProductBrand = new ArrayList<ProductBrand>();
				inputStream = new FileInputStream(fileSave);
				Workbook workBook = getWorkbook(inputStream,
						"/resources/upload/productbrand" + "/" + nameNewFile + "." + type);
				Sheet firstSheet = workBook.getSheetAt(0);
				Iterator<Row> rows = firstSheet.iterator();
				ProductBrand lix;
				while (rows.hasNext()) {
					rows.next();
					rows.remove();
					break;
				}
				while (rows.hasNext()) {
					Row row = rows.next();
					Iterator<Cell> cells = row.cellIterator();
					lix = new ProductBrand();
					while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try{
							String pbcode = (String) getCellValue(cell);
							lix.setPbrand_code(pbcode);
							}catch(Exception e){}
							break;
						case 1:
							try{
								String name=(String)getCellValue(cell);
								lix.setPbrand_name(convertString(name));
							}catch(Exception e){
							}
							break;
						case 2:
							try {
								String dvt=(String)getCellValue(cell);
								lix.setUnit(dvt);
							}catch(Exception e) {
							}
							break;
						}
					}
					listProductBrand.add(lix);
				}
				workBook = null;
				for (ProductBrand it : listProductBrand) {
					ProductBrandReqInfo p=new ProductBrandReqInfo();
					productBrandService.selectByCode(it.getPbrand_code(), p);
					ProductBrand temp=p.getProduct_brand();
					p.setProduct_brand(it);
					if (temp!=null) {
						it.setId(temp.getId());
						it.setLast_modifed_date(new Date());
						it.setCreated_by(account.getMember().getName());
						productBrandService.update(p);
					} else {
						it.setCreated_date(new Date());
						it.setCreated_by(account.getMember().getName());
						productBrandService.insert(p);
					}
				}
				
				notify.success();

			}
		} catch (Exception e) {
			logger.error("ProductBrandBean.loadExcel:" + e.getMessage(), e);
			notify.error();
		} finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	public ProductBrand getProductBrandCrud() {
		return productBrandCrud;
	}

	public void setProductBrandCrud(ProductBrand productBrandCrud) {
		this.productBrandCrud = productBrandCrud;
	}

	public ProductBrand getProductBrandSelect() {
		return productBrandSelect;
	}

	public void setProductBrandSelect(ProductBrand productBrandSelect) {
		this.productBrandSelect = productBrandSelect;
	}

	public List<ProductBrand> getListProductBrand() {
		return listProductBrand;
	}

	public void setListProductBrand(List<ProductBrand> listProductBrand) {
		this.listProductBrand = listProductBrand;
	}

	public String getpBrandCode() {
		return pBrandCode;
	}

	public void setpBrandCode(String pBrandCode) {
		this.pBrandCode = pBrandCode;
	}

	public String getpBrandName() {
		return pBrandName;
	}

	public void setpBrandName(String pBrandName) {
		this.pBrandName = pBrandName;
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
