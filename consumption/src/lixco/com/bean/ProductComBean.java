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

import lixco.com.common.FormatHandler;
import lixco.com.common.ImageHandler;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.entity.ProductBrand;
import lixco.com.entity.ProductCom;
import lixco.com.interfaces.IProductBrandService;
import lixco.com.interfaces.IProductComService;
import lixco.com.reqInfo.ProductBrandReqInfo;
import lixco.com.reqInfo.ProductComReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class ProductComBean extends AbstractBean {
	private static final long serialVersionUID = -7261573870814228140L;
	@Inject
	private Logger logger;
	@Inject
	private IProductBrandService productBrandService;
	@Inject
	private IProductComService productComService;
	private ProductCom productComCrud;
	private ProductCom productComSelect;
	private List<ProductCom> listProductCom;
	private String pComCode;
	private String pComName;
	private ProductBrand productBrand;
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
			productComCrud=new ProductCom();
		}catch(Exception e){
			logger.error("ProductComBean.initItem:"+e.getMessage(),e);
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
			listProductCom = new ArrayList<ProductCom>();
			PagingInfo page = new PagingInfo();
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("pcom_code", FormatHandler.getInstance().converViToEn(pComCode));
			jsonInfo.addProperty("pcom_name", FormatHandler.getInstance().converViToEn(pComName));
			jsonInfo.addProperty("product_brand_id",productBrand !=null ? productBrand.getId() :0);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("pcom_info", jsonInfo);
			json.add("page", jsonPage);
			productComService.search(JsonParserUtil.getGson().toJson(json), page, listProductCom);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		} catch (Exception e) {
			logger.error("ProductComBean.search:" + e.getMessage(), e);
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
			logger.error("ProductComBean.initRowPerPage:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listProductCom = new ArrayList<ProductCom>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("pbrand_code",FormatHandler.getInstance().converViToEn(pComCode));
			jsonInfo.addProperty("pbrand_name", FormatHandler.getInstance().converViToEn(pComName));
			jsonInfo.addProperty("product_brand_id",productBrand !=null ? productBrand.getId() :0);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("pcom_info", jsonInfo);
			json.add("page", jsonPage);
			productComService.search(JsonParserUtil.getGson().toJson(json), page, listProductCom);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("ProductComBean.paginatorChange:" + e.getMessage(), e);
		}
	}

	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (productComCrud != null) {
				String pComCode = productComCrud.getPcom_code();
				String pComName = productComCrud.getPcom_name();
				ProductBrand productBrand=productComCrud.getProduct_brand();
				if (pComCode != null && pComCode != "" && pComName != null && pComName != "" && productBrand !=null) {
					ProductComReqInfo t = new ProductComReqInfo(productComCrud);
					if (productComCrud.getId() == 0) {
						// check code đã tồn tại chưa
						if (allowSave(new Date())) {
							if (productComService.checkProductComCode(pComCode, 0) == 0) {
								productComCrud.setCreated_date(new Date());
								productComCrud.setCreated_by(account.getMember().getName());
								if (productComService.insert(t) != -1) {
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listProductCom.add(0, productComCrud);
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
							if (productComService.checkProductComCode(pComCode, productComCrud.getId()) == 0) {
								productComCrud.setLast_modifed_by(account.getMember().getName());
								productComCrud.setLast_modifed_date(new Date());
								if (productComService.update(t) != -1) {
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									listProductCom.set(listProductCom.indexOf(productComCrud), productComCrud);
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
			logger.error("ProductComBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}
	public void showEdit(){
		try{
			productComCrud=productComSelect;
		}catch(Exception e){
			logger.error("ProductComBean.showEdit:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		productComCrud=new ProductCom();
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(productComSelect !=null){
				if(allowDelete(new Date())){
					if(productComService.deleteById(productComSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listProductCom.remove(productComSelect);
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
			logger.error("ProductComBean.delete:"+e.getMessage(),e);
		}
	}
	public List<ProductBrand> autoCompleteProductBrand(String text){
		try{
			List<ProductBrand> list=new ArrayList<ProductBrand>();
			productBrandService.findLike(FormatHandler.getInstance().converViToEn(text), 120,list);
			return list;
		}catch (Exception e) {
			logger.error("ProductComBean.autoCompleteProductBrand:"+e.getMessage(),e);
		}
		return null;
	}
	public void showDialogUpload(){
		try{
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('uploadpdffile').show();");
		}catch(Exception e){
			logger.error("ProductComBean.showDialogUpload:"+e.getMessage(),e);
		}
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
						.getRealPath("/resources/upload/productcom");
				String nameNewFile = ImageHandler.getInstance().generateUniqueFileName(type, url);
				File fileSave = new File(url, nameNewFile + "." + type);
				outStream = new FileOutputStream(fileSave);
				outStream.write(byteFile);
				List<ProductCom> listProductCom = new ArrayList<ProductCom>();
				inputStream = new FileInputStream(fileSave);
				Workbook workBook = getWorkbook(inputStream,
						"/resources/upload/productcom/" + nameNewFile + "." + type);
				Sheet firstSheet = workBook.getSheetAt(0);
				Iterator<Row> rows = firstSheet.iterator();
				ProductCom lix;
				while (rows.hasNext()) {
					rows.next();
					rows.remove();
					break;
				}
				while (rows.hasNext()) {
					Row row = rows.next();
					Iterator<Cell> cells = row.cellIterator();
					lix = new ProductCom();
					lv2:while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try{
							String pccode = (String) getCellValue(cell);
							lix.setPcom_code(pccode);
							}catch(Exception e){}
							break;
						case 1:
							try{
								String name=(String)getCellValue(cell);
								lix.setPcom_name(name);
							}catch(Exception e){
							}
							break;
						case 2:
							try{
								String pbrand=(String) getCellValue(cell);
								ProductBrandReqInfo pb=new ProductBrandReqInfo();
								productBrandService.selectByCode(pbrand, pb);
								if(pb.getProduct_brand() ==null){
									break lv2;
								}
								lix.setProduct_brand(pb.getProduct_brand());
							}catch(Exception e){
							}
							break;
						case 3:
							try{
								String dvt=(String)getCellValue(cell);
								lix.setUnit(dvt);
							}catch(Exception e){}
							break;
//						case 4:
//							try{
//								String madm=(String)getCellValue(cell);
////								ProductNorm pnorm=productNormService.getProductNormByCode(madm);
////								if(pnorm ==null){
////									break lv2;
////								}
////								lix.setProductNorm(pnorm);
//							}catch(Exception e){
//							}
//							break;
						}
					}
					listProductCom.add(lix);
				}
				workBook = null;
				for (ProductCom it : listProductCom) {
					ProductComReqInfo t=new ProductComReqInfo();
					productComService.selectByCode(it.getPcom_code(),t);
					ProductCom pc=t.getProduct_com();
					t.setProduct_com(it);
					if (pc != null) {
						it.setId(pc.getId());
						it.setLast_modifed_date(new Date());
						it.setCreated_by(account.getMember().getName());
						productComService.update(t);
					} else {
						it.setCreated_date(new Date());
						it.setCreated_by(account.getMember().getName());
						productComService.insert(t);
					}
				}
				search();
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

	public ProductCom getProductComCrud() {
		return productComCrud;
	}

	public void setProductComCrud(ProductCom productComCrud) {
		this.productComCrud = productComCrud;
	}

	public ProductCom getProductComSelect() {
		return productComSelect;
	}

	public void setProductComSelect(ProductCom productComSelect) {
		this.productComSelect = productComSelect;
	}

	public List<ProductCom> getListProductCom() {
		return listProductCom;
	}

	public void setListProductCom(List<ProductCom> listProductCom) {
		this.listProductCom = listProductCom;
	}

	public String getpComCode() {
		return pComCode;
	}

	public void setpComCode(String pComCode) {
		this.pComCode = pComCode;
	}

	public String getpComName() {
		return pComName;
	}

	public void setpComName(String pComName) {
		this.pComName = pComName;
	}

	public ProductBrand getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(ProductBrand productBrand) {
		this.productBrand = productBrand;
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
