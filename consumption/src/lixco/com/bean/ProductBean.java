package lixco.com.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.faces.component.UIComponent;
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
import lixco.com.entity.Product;
import lixco.com.entity.ProductCom;
import lixco.com.entity.ProductGroup;
import lixco.com.entity.ProductType;
import lixco.com.entity.PromotionProductGroup;
import lixco.com.interfaces.IProductComService;
import lixco.com.interfaces.IProductGroupService;
import lixco.com.interfaces.IProductService;
import lixco.com.interfaces.IProductTypeService;
import lixco.com.interfaces.IPromotionProductGroupService;
import lixco.com.reqInfo.ProductComReqInfo;
import lixco.com.reqInfo.ProductGroupReqInfo;
import lixco.com.reqInfo.ProductReqInfo;
import lixco.com.reqInfo.ProductTypeReqInfo;
import lixco.com.reqInfo.PromotionProductGroupReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class ProductBean extends AbstractBean {
	private static final long serialVersionUID = -7927381135900704460L;
	@Inject
	private Logger logger;
	@Inject
	private IProductService productService;
	@Inject
	private IProductComService productComService;
	@Inject
	private IProductTypeService productTypeService;
	@Inject
	private IProductGroupService productGroupService;
	@Inject
	private IPromotionProductGroupService promotionProductGroupService;
	private Product productCrud;
	private Product productSelect;
	private List<Product> listProduct;
	private List<ProductType> listProductType;
	private List<ProductGroup> listProductGroup;
	private String productCode;
	private String productName;
	private String leverCode;
	private ProductCom productCom;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	private Account account;

	@Override
	protected void initItem() {
		try {
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			search();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			productCrud = new Product();
			listProductType=new ArrayList<>();
			productTypeService.selectAll(listProductType);
			listProductGroup=new ArrayList<>();
			productGroupService.selectAll(listProductGroup);
		} catch (Exception e) {
			logger.error("ProductBean.initItem:" + e.getMessage(), e);
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
			listProduct = new ArrayList<Product>();
			PagingInfo page = new PagingInfo();
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("product_code", FormatHandler.getInstance().converViToEn(productCode));
			jsonInfo.addProperty("product_name", FormatHandler.getInstance().converViToEn(productName));
			jsonInfo.addProperty("lever_code", FormatHandler.getInstance().converViToEn(leverCode));
			jsonInfo.addProperty("product_com_id", productCom != null ? productCom.getId() : 0);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("product_info", jsonInfo);
			json.add("page", jsonPage);
			productService.search(JsonParserUtil.getGson().toJson(json), page, listProduct);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		} catch (Exception e) {
			logger.error("ProductBean.search:" + e.getMessage(), e);
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
			logger.error("ProductBean.initRowPerPage:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listProduct = new ArrayList<Product>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("product_code", FormatHandler.getInstance().converViToEn(productCode));
			jsonInfo.addProperty("product_name", FormatHandler.getInstance().converViToEn(productName));
			jsonInfo.addProperty("lever_code", FormatHandler.getInstance().converViToEn(leverCode));
			jsonInfo.addProperty("product_com_id", productCom != null ? productCom.getId() : 0);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("product_info", jsonInfo);
			json.add("page", jsonPage);
			productService.search(JsonParserUtil.getGson().toJson(json), page, listProduct);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("ProductBean.paginatorChange:" + e.getMessage(), e);
		}
	}

	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (productCrud != null) {
				String productCode = productCrud.getProduct_code();
				String productName = productCrud.getProduct_name();
				ProductCom productCom = productCrud.getProduct_com();
				if (productCode != null && productCode != "" && productName != null && productName != ""
						&& productCom != null) {
					ProductReqInfo t = new ProductReqInfo(productCrud);
					if (productCrud.getId() == 0) {
						// check code đã tồn tại chưa
						if (allowSave(new Date())) {
							if (productService.checkProductCode(productCode, 0) == 0) {
								productCrud.setCreated_date(new Date());
								productCrud.setCreated_by(account.getMember().getName());
								if (productService.insert(t) != -1) {
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
									listProduct.add(0, productCrud);
									createNew();
								} else {
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							} else {
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã sản phẩm đã tồn tại!','warning',2000);");
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					} else {
						// check code update đã tồn tại chưa
						if (allowUpdate(new Date())) {
							if (productService.checkProductCode(productCode, productCrud.getId()) == 0) {
								productCrud.setLast_modifed_by(account.getMember().getName());
								productCrud.setLast_modifed_date(new Date());
								if (productService.update(t) != -1) {
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									listProduct.set(listProduct.indexOf(productCrud), productCrud);
								} else {
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
								}
							} else {
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã sản phẩm đã tồn tại!','warning',2000);");
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ, điền đủ thông tin chứa(*)!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("ProductBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showDialogEdit() {
		PrimeFaces current = PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (productSelect != null) {
				productCrud = productSelect.clone();
				current.executeScript("PF('dlg1').show();");
			} else {
				notify.message("Chọn dòng để chỉnh sửa!");
			}
		} catch (Exception e) {
			logger.error("ProductBean.showDialogEdit:" + e.getMessage(), e);
		}
	}

	public void showDialog() {
		PrimeFaces current = PrimeFaces.current();
		try {
			productCrud = new Product();
			current.executeScript("PF('dlg1').show();");
		} catch (Exception e) {
			logger.error("ProductBean.showDialog:" + e.getMessage(), e);
		}
	}

	public void createNew() {
		productCrud = new Product();
	}

	public void delete() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (productSelect != null) {
				if (allowDelete(new Date())) {
					if (productService.deleteById(productSelect.getId()) != -1) {
						current.executeScript("swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listProduct.remove(productSelect);
					} else {
						current.executeScript("swaldesigntimer('Thất bại!', 'Lỗi hệ thống!','error',2000);");
					}
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
				}
			} else {
				current.executeScript("swaldesigntimer('Cảnh báo!', 'Chưa chọn dòng để xóa!','warning',2000);");
			}
		} catch (Exception e) {
			logger.error("ProductBean.delete:" + e.getMessage(), e);
		}
	}

	public List<ProductCom> autoCompleteProductCom(String text) {
		try {
			List<ProductCom> list = new ArrayList<ProductCom>();
			productComService.findLike(FormatHandler.getInstance().converViToEn(text), 120, list);
			for (ProductCom p : list) {
				p.setProduct_brand(null);
				p.setProduct_norm(null);
			}
			return list;
		} catch (Exception e) {
			logger.error("ProductBean.autoCompleteProductCom:" + e.getMessage(), e);
		}
		return null;
	}

	public List<Product> completeProduct(String text) {
		try {
			List<Product> list = new ArrayList<Product>();
			productService.findLike(FormatHandler.getInstance().converViToEn(text), 120, list);
			return list;
		} catch (Exception e) {
			logger.error("ProductBean.completeProduct:" + e.getMessage(), e);
		}
		return null;
	}
	public List<PromotionProductGroup> completePromotionProductGroup(String text) {
		try {
			Object size = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("sizep");
			List<PromotionProductGroup> list = new ArrayList<PromotionProductGroup>();
			promotionProductGroupService.complete(FormatHandler.getInstance().converViToEn(text), Integer.parseInt(Objects.toString(size,"0")), list);
			return list;
		} catch (Exception e) {
			logger.error("ProductBean.completePromotionProductGroup:" + e.getMessage(), e);
		}
		return null;
	}
	public void showDialogUpload() {
		try {
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('uploadpdffile').show();");
		} catch (Exception e) {
			logger.error("ProductBean.showDialogUpload:" + e.getMessage(), e);
		}
	}

	public void loadExcel(FileUploadEvent event){
			Notify notify = new Notify(FacesContext.getCurrentInstance());
			try {
				if (event.getFile() != null) {
					UploadedFile part = event.getFile();
					byte[] byteFile = event.getFile().getContents();
					List<Product> listProductTemp = new ArrayList<Product>();
					Workbook workBook = getWorkbook(new ByteArrayInputStream(byteFile),part.getFileName());
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
						Product lix = new Product();
						lv2:while (cells.hasNext()) {
							Cell cell = cells.next();
							int columnIndex = cell.getColumnIndex();

							switch (columnIndex) {
							case 0:
								try{
									// tên sản phẩm
									String masp = Objects.toString(getCellValue(cell),null);
									if(masp !=null && !"".equals(masp)){
										lix.setProduct_code(masp);
									}else{
										break lv2;
									}
								}catch(Exception e){}
								break;
							case 1:
								try{
									// tên sản phẩm tiếng việt
									String name=Objects.toString(getCellValue(cell),null);
									lix.setProduct_name(name);
								}catch(Exception e){
								}
								break;
							case 2:
								try{
									// tên sản phẩm tiếng anh
									String name_en=Objects.toString(getCellValue(cell),null);
									lix.setEn_name(name_en);
								}catch(Exception e){
								}
								break;
							case 3:
								try{
									// tên khai báo hải quan
									String customs_name=Objects.toString(getCellValue(cell),null);
									lix.setCustoms_name(customs_name);
								}catch(Exception e){}
								break;
							case 4:
								try{
									String manhomsp=Objects.toString(getCellValue(cell),null);
									if(manhomsp !=null && !"".equals(manhomsp)){
										ProductGroupReqInfo pg=new ProductGroupReqInfo();
										productGroupService.selectByCode(manhomsp, pg);
										lix.setProduct_group(pg.getProduct_Group());
									}
								}catch (Exception e) {
								}
								break;
							case 5:
								
								try{
									String manloaisp=Objects.toString(getCellValue(cell),null);
									if(manloaisp !=null && !"".equals(manloaisp)){
										ProductTypeReqInfo pt=new ProductTypeReqInfo();
										productTypeService.selectByCode(manloaisp, pt);
										lix.setProduct_type(pt.getProduct_type());
									}
								}catch (Exception e) {
								}
								break;
							case 6:
								try{
									// đơn vị tính
									String dvt=Objects.toString(getCellValue(cell),null);
									lix.setUnit(dvt);
								}catch(Exception e){}
								break;
							case 7:
								try{
									// hệ số quy đổi
									String hsqd=Objects.toString(getCellValue(cell),"0");
									lix.setFactor(Double.parseDouble(hsqd));
								}catch(Exception e){}
								break;
							case 9:
								try{
									// sản phẩm com
									String mspcom=Objects.toString(getCellValue(cell),null);
									if(mspcom !=null && !"".equals(mspcom)){
										ProductComReqInfo com=new ProductComReqInfo();
										productComService.selectByCode(mspcom, com);
										lix.setProduct_com(com.getProduct_com());
									}
								}catch(Exception e){}
								break;
							case 10:
								try{
									// qui cách đóng gói
									String slsp=Objects.toString(getCellValue(cell),"0");
									lix.setSpecification(Double.parseDouble(slsp));
								}catch(Exception e){}
								break;
							case 11:
								try{
									// đơn vị bao bì
									String dvtbb=Objects.toString(getCellValue(cell),null);
									lix.setPacking_unit(dvtbb);
								}catch(Exception e){}
								break;
							case 12:
								try{
									// trọng lượng thùng
									String tlthung=Objects.toString(getCellValue(cell),"0");
									lix.setTare(Double.parseDouble(tlthung));
								}catch(Exception e){}
								break;
							case 13:
								try{
									// nhóm sản phẩm khuyến mãi
									String manspkm=Objects.toString(getCellValue(cell),null);
									if(manspkm !=null && !"".equals(manspkm)){
										PromotionProductGroupReqInfo km=new PromotionProductGroupReqInfo();
										promotionProductGroupService.selectByCode(manspkm, km);
										lix.setPromotion_product_group(km.getPromotion_product_group());
									}
								}catch(Exception e){}
								break;
							case 14:
								try{
									// số lượng dự trữ
									String sldutru=Objects.toString(getCellValue(cell),"0");
									lix.setReserve_quantity(Double.parseDouble(sldutru));
								}catch(Exception e){}
								break;
							case 15:
								try{
									// mã sản phẩm lever
									String levercode=Objects.toString(getCellValue(cell),null);
									lix.setLever_code(levercode);
								}catch(Exception e){}
								break;
							case 16:
								try{
									// không còn sử dụng
									String disable=Objects.toString(getCellValue(cell),"0");
									lix.setDisable(Boolean.parseBoolean(disable));
								}catch(Exception e){}
								break;
							case 17:
								try{
									// xuất khẩu
									String xk=Objects.toString(getCellValue(cell),"0");
									lix.setTypep(Boolean.parseBoolean(xk));
								}catch(Exception e){}
								break;
							case 18:
								try{
									//thông tin ghi trên packaing list
									String info=Objects.toString(getCellValue(cell),null);
									lix.setProduct_info(info);
								}catch(Exception e){}
								break;
							case 19:
								try{
									//sản phẩm khuyến mãi đính kèm trực tiếp trên sản phẩm.
									String maspkm=Objects.toString(getCellValue(cell),null);
									ProductReqInfo prop=new ProductReqInfo();
									productService.selectByCode(maspkm, prop);
									lix.setPromotion_product(prop.getProduct());
								}catch(Exception e){
								}
								break;
							case 20:
								try{
									//số lượng thùng trên pallet
									String box=Objects.toString(getCellValue(cell),"0");
									lix.setBox_quantity(Double.parseDouble(box));
								}catch(Exception e){}
								break;
							}
						}
						listProductTemp.add(lix);
					}
					workBook = null;// free
					for (Product it : listProductTemp) {
						ProductReqInfo t=new ProductReqInfo();
						productService.selectByCode(it.getProduct_code(),t);
						Product p=t.getProduct();
						t.setProduct(it);
						if (p != null) {
							it.setId(p.getId());
							it.setLast_modifed_date(new Date());
							it.setCreated_by(account.getMember().getName());
							productService.update(t);
						} else {
							it.setCreated_date(new Date());
							it.setCreated_by(account.getMember().getName());
							productService.insert(t);
						}
					}
					search();
					notify.success();

				}
		}catch(Exception e){
			logger.error("ProductBean.loadExcel:"+e.getMessage(),e);
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

	public Product getProductCrud() {
		return productCrud;
	}

	public void setProductCrud(Product productCrud) {
		this.productCrud = productCrud;
	}

	public Product getProductSelect() {
		return productSelect;
	}

	public void setProductSelect(Product productSelect) {
		this.productSelect = productSelect;
	}

	public List<Product> getListProduct() {
		return listProduct;
	}

	public void setListProduct(List<Product> listProduct) {
		this.listProduct = listProduct;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getLeverCode() {
		return leverCode;
	}

	public void setLeverCode(String leverCode) {
		this.leverCode = leverCode;
	}

	public ProductCom getProductCom() {
		return productCom;
	}

	public void setProductCom(ProductCom productCom) {
		this.productCom = productCom;
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

	public List<ProductType> getListProductType() {
		return listProductType;
	}

	public void setListProductType(List<ProductType> listProductType) {
		this.listProductType = listProductType;
	}

	public List<ProductGroup> getListProductGroup() {
		return listProductGroup;
	}

	public void setListProductGroup(List<ProductGroup> listProductGroup) {
		this.listProductGroup = listProductGroup;
	}
}
