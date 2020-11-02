package lixco.com.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import lixco.com.common.FormatHandler;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Customer;
import lixco.com.entity.GoodsReceiptNote;
import lixco.com.entity.GoodsReceiptNoteDetail;
import lixco.com.entity.GoodsReceiptNotePosDetail;
import lixco.com.entity.IECategories;
import lixco.com.entity.Product;
import lixco.com.entity.Warehouse;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.IGoodsReceiptNoteDetailService;
import lixco.com.interfaces.IGoodsReceiptNoteService;
import lixco.com.interfaces.IIECategoriesService;
import lixco.com.interfaces.IProcessLogicGoodsReceiptNoteService;
import lixco.com.interfaces.IProductService;
import lixco.com.interfaces.IWarehouseService;
import lixco.com.reqInfo.GoodsReceiptNoteReqInfo;
import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.ProductReqInfo;
import lixco.com.reqInfo.WrapGoodsReceiptNoteReqInfo;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class GoodsReceiptNoteBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IProcessLogicGoodsReceiptNoteService processLogicGoodsReceiptNoteService;
	@Inject
	private IGoodsReceiptNoteService goodsReceiptNoteService;
	@Inject
	private IGoodsReceiptNoteDetailService goodsReceiptNoteDetailService;
	@Inject
	private IProductService productService;
	@Inject
	private ICustomerService customerService;
	@Inject
	private IIECategoriesService ieCategoriesService;
	@Inject
	private IWarehouseService warehouseService;
	private GoodsReceiptNote goodsReceiptNoteCrud;
	private GoodsReceiptNote goodsReceiptNoteSelect;
	private List<GoodsReceiptNote> listGoodsReceiptNote;
	private GoodsReceiptNoteDetail goodsReceiptNoteDetailCrud;
	private GoodsReceiptNoteDetail goodsReceiptNoteDetailSelect;
	private List<GoodsReceiptNoteDetail> listGoodsReceiptNoteDetail;
	private List<IECategories> listIECategories;
	private List<Warehouse> listWarehouse;
	/*search phiếu nhập*/
	private Date fromDateSearch;
	private Date toDateSearch;
	private Customer customerSearch;
	private IECategories ieCategoriesSearch;
	private Warehouse warehouseSearch;
	private int statusSearch;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	private FormatHandler formatHandler;
	private Account account;
	/*Thông tin search*/
	private Product productSearch;
	private String batchCodeSearch;

	@Override
	protected void initItem() {
		try{
			formatHandler=FormatHandler.getInstance();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			createNew();
			createNewDetail();
			listIECategories=new ArrayList<>();
			ieCategoriesService.selectAll(listIECategories);
			listWarehouse=new ArrayList<>();
			warehouseService.selectAll(listWarehouse);
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			Date currentDate=new Date();
			fromDateSearch=ToolTimeCustomer.getDateMinCustomer(ToolTimeCustomer.getMonthM(currentDate), ToolTimeCustomer.getYearM(currentDate));
			statusSearch=-1;
			searchReceiptNote();
		}catch(Exception e){
			logger.error("GoogsReceiptNoteBean.initItem:"+e.getMessage(),e);
		}
	}
	public void addGoodsReceiptNoteDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(goodsReceiptNoteCrud !=null && goodsReceiptNoteDetailCrud !=null){
				String batchCode=goodsReceiptNoteDetailCrud.getBatch_code();
				if(allowSave(new Date())){
					if (goodsReceiptNoteDetailCrud.getProduct() != null && batchCode !=null && !"".equals(batchCode)) {
						goodsReceiptNoteDetailCrud.setQuantity(1);
						goodsReceiptNoteDetailCrud.setCreated_by(account.getMember().getName());
						goodsReceiptNoteDetailCrud.setCreated_date(new Date());
						listGoodsReceiptNoteDetail.add(0, goodsReceiptNoteDetailCrud.clone());
					}else{
						if(goodsReceiptNoteDetailCrud.getProduct()==null){
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Chưa nhập sản phẩm để thêm vào!','warning',2000);");
						}
						if( batchCode ==null || "".equals(batchCode)){
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Chưa nhập mã lô hàng!','warning',2000);");
						}
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
				}
				goodsReceiptNoteDetailCrud= new GoodsReceiptNoteDetail();
				goodsReceiptNoteDetailCrud.setBatch_code(batchCode);
			}
		}catch(Exception e){
			// reset
			goodsReceiptNoteDetailCrud = new GoodsReceiptNoteDetail();
			logger.error("GoogsReceiptNoteBean.addGoodsReceiptNoteDetail:"+e.getMessage(),e);
		}
	}
	public void deleteGoodsReceiptNoteDetail(GoodsReceiptNoteDetail f){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		PrimeFaces current=PrimeFaces.current();
		try{
			if(f.getId()==0){
				listGoodsReceiptNoteDetail.remove(f);
				notify.success("Xóa thành công!");
			}else{
				//thực hiện delete trong transation
				Message message=new Message();
				int code=processLogicGoodsReceiptNoteService.deleteGoodsReceiptNoteDetailMaster(f.getId(),message);
				if(code>0){
					notify.success("Xóa thành công!");
					listGoodsReceiptNoteDetail.remove(f);
				}else{
					String m=message.getUser_message()+" \n"+message.getInternal_message();
					current.executeScript(
							"swaldesignclose('Thất bại', '"+m+"','warning');");
				}
			}
		}catch (Exception e) {
			logger.error("GoogsReceiptNoteBean.deleteGoodsReceiptNoteDetail:"+e.getMessage(),e);
		}
	}
	public void searchReceiptNote(){
		try{
			/*{ goods_receipt_note:{from_date:'',to_date:'',customer_id:0,ie_categories_id:0,warehouse_id:0,status:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listGoodsReceiptNote=new ArrayList<>();
			PagingInfo page=new PagingInfo();
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 :customerSearch.getId());
			jsonInfo.addProperty("ie_categories_id", ieCategoriesSearch == null ? 0 :ieCategoriesSearch.getId());
			jsonInfo.addProperty("warehouse_id", warehouseSearch ==null ? 0 :warehouseSearch.getId());
			jsonInfo.addProperty("status", statusSearch);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("goods_receipt_note", jsonInfo);
			json.add("page", jsonPage);
			goodsReceiptNoteService.search(JsonParserUtil.getGson().toJson(json), page, listGoodsReceiptNote);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		}catch(Exception e){
			logger.error("GoogsReceiptNoteBean.searchReceiptNote:"+e.getMessage(),e);
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
			logger.error("GoogsReceiptNoteBean.initRowPerPage:"+e.getMessage(),e);
		}
	}
	public void paginatorChange(int currentPage) {
		try {
			/*{ goods_receipt_note:{from_date:'',to_date:'',customer_id:0,ie_categories_id:0,warehouse_id:0,status:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listGoodsReceiptNote = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 :customerSearch.getId());
			jsonInfo.addProperty("ie_categories_id", ieCategoriesSearch == null ? 0 :ieCategoriesSearch.getId());
			jsonInfo.addProperty("warehouse_id", warehouseSearch ==null ? 0 :warehouseSearch.getId());
			jsonInfo.addProperty("status", statusSearch);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("goods_receipt_note", jsonInfo);
			json.add("page", jsonPage);
			goodsReceiptNoteService.search(JsonParserUtil.getGson().toJson(json), page, listGoodsReceiptNote);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("GoogsReceiptNoteBean.paginatorChange:" + e.getMessage(), e);
		}
	}
	public void createNew(){
		try{
			goodsReceiptNoteCrud=new GoodsReceiptNote();
			goodsReceiptNoteCrud.setCreated_by(account.getMember().getName());
			goodsReceiptNoteCrud.setImport_date(new Date());
			listGoodsReceiptNoteDetail=new ArrayList<>();
			goodsReceiptNoteDetailCrud=new GoodsReceiptNoteDetail();
		}catch (Exception e) {
			logger.error("GoogsReceiptNoteBean.createNew:"+e.getMessage(),e);
		}
		
	}
	public void createNewDetail(){
		try{
			goodsReceiptNoteDetailCrud=new GoodsReceiptNoteDetail();
		}catch (Exception e) {
			logger.error("GoogsReceiptNoteBean.createNewDetail:"+e.getMessage(),e);
		}
	}
	public List<Product> completeProduct(String text) {
		try {
			List<Product> list = new ArrayList<Product>();
			productService.complete(formatHandler.converViToEn(text), list);
			return list;
		} catch (Exception e) {
			logger.error("GoogsReceiptNoteBean.completeProduct:" + e.getMessage(), e);
		}
		return null;
	}
	public List<Customer> completeCustomer(String text){
		try{
			List<Customer> list = new ArrayList<Customer>();
			customerService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("GoogsReceiptNoteBean.completeCustomer:" + e.getMessage(), e);
		}
		return null;
	}
	public void deleteGoodsReceiptNote(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		PrimeFaces current=PrimeFaces.current();
		try{
			if(goodsReceiptNoteSelect !=null && goodsReceiptNoteSelect.getId()!=0){
				//delete detail
				Message messages=new Message();
				int code=processLogicGoodsReceiptNoteService.deleteGoodsReceiptNoteMaster(goodsReceiptNoteSelect.getId(), messages);
				if(code>=0){
					notify.success("Xóa thành công!");
					listGoodsReceiptNote.remove(goodsReceiptNoteSelect);
				}else{
					String m=messages.getUser_message()+" \\n"+messages.getInternal_message();
					current.executeScript(
							"swaldesignclose('Thất bại', '"+m+"','warning');");
				}
			}
		}catch(Exception e){
			logger.error("GoogsReceiptNoteBean.deleteGoodsReceiptNote:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdateReceiptNote(int status){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(goodsReceiptNoteCrud !=null){
				if(listGoodsReceiptNoteDetail.size()>0){
					WrapGoodsReceiptNoteReqInfo t=new WrapGoodsReceiptNoteReqInfo();
					goodsReceiptNoteCrud.setStatus(status);
					t.setGoods_receipt_note(goodsReceiptNoteCrud);
					t.setList_goods_receipt_note_detail(listGoodsReceiptNoteDetail);
					t.setMember_name(account.getMember().getName());
					t.setMember_id(account.getMember().getId());
					Message message=new Message();
					//check info
					Customer customer=goodsReceiptNoteCrud.getCustomer();
					IECategories categories=goodsReceiptNoteCrud.getIe_categories();
					Warehouse warehouse=goodsReceiptNoteCrud.getWarehouse();
					Date importDate=goodsReceiptNoteCrud.getImport_date();
					if(customer !=null && categories !=null && warehouse !=null && importDate !=null){
						WrapGoodsReceiptNoteReqInfo cloned=t.clone();
						if(goodsReceiptNoteCrud.getId()==0){
							if(allowSave(new Date())){
								int code=processLogicGoodsReceiptNoteService.saveOrUpdateGoodsReceiptNoteService(cloned, message);
								switch (code) {
								case -1:
									String m=message.getUser_message()+" \\n"+message.getInternal_message();
									current.executeScript(
											"swaldesignclose('Thất bại', '"+m+"','warning');");
									break;
								default:
									current.executeScript("swaldesigntimer('Thành công!', 'Lưu thành công!','success',2000);");
									//tải lại 
									GoodsReceiptNoteReqInfo req=new GoodsReceiptNoteReqInfo();
									goodsReceiptNoteService.selectById(cloned.getGoods_receipt_note().getId(), req);
									goodsReceiptNoteCrud=req.getGoods_receipt_note();
									listGoodsReceiptNote.add(0, goodsReceiptNoteCrud.clone());
									//load danh sách chi tiết
									listGoodsReceiptNoteDetail=new ArrayList<>();
									goodsReceiptNoteDetailService.selectByReceiptNote(goodsReceiptNoteCrud.getId(), listGoodsReceiptNoteDetail);
									for(GoodsReceiptNoteDetail dt:listGoodsReceiptNoteDetail){
										dt.setBatch_code(dt.getBatch().getBatch_code());
									}
									break;
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
							}
						}else{
							if(allowUpdate(new Date())){
								//update đưa vào trong trans
								int code=processLogicGoodsReceiptNoteService.saveOrUpdateGoodsReceiptNoteService(cloned, message);
								switch (code) {
								case -1:
									String m=message.getUser_message()+" \\n"+message.getInternal_message();
									current.executeScript(
											"swaldesignclose('Thất bại', '"+m+"','warning');");
									break;
								default:
									current.executeScript("swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									//tải lại 
									GoodsReceiptNoteReqInfo req=new GoodsReceiptNoteReqInfo();
									goodsReceiptNoteService.selectById(cloned.getGoods_receipt_note().getId(), req);
									goodsReceiptNoteCrud=req.getGoods_receipt_note();
									listGoodsReceiptNote.set(listGoodsReceiptNote.indexOf(goodsReceiptNoteCrud), goodsReceiptNoteCrud.clone());
									//load danh sách chi tiết
									listGoodsReceiptNoteDetail=new ArrayList<>();
									goodsReceiptNoteDetailService.selectByReceiptNote(goodsReceiptNoteCrud.getId(), listGoodsReceiptNoteDetail);
									for(GoodsReceiptNoteDetail dt:listGoodsReceiptNoteDetail){
										dt.setBatch_code(dt.getBatch().getBatch_code());
									}
									break;
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
							}
						}
					}else{
						current.executeScript(
								"swaldesigntimer('Cảnh báo!', 'Thông tin phiếu nhập không đầy đủ!','warning',2000);");
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Danh sách chi tiết phiếu nhập không có!','warning',2000);");
				}
			}
		}catch(Exception e){
			logger.error("GoogsReceiptNoteBean.saveOrUpdateReceiptNote:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdateReceiptNoteDetail(){
		try{
			
		}catch(Exception e){
			logger.error("GoogsReceiptNoteBean.saveOrUpdateReceiptNote:" + e.getMessage(), e);
		}
	}
	public void loadGoodsReceiptNote(){
		try{
			if(goodsReceiptNoteSelect !=null){
			  goodsReceiptNoteCrud=goodsReceiptNoteSelect.clone();
			  //load chi tiết phiếu nhập
			  listGoodsReceiptNoteDetail=new ArrayList<>();
			  goodsReceiptNoteDetailService.selectByReceiptNote(goodsReceiptNoteCrud.getId(),listGoodsReceiptNoteDetail);
			  for(GoodsReceiptNoteDetail dt:listGoodsReceiptNoteDetail){
				  dt.setBatch_code(dt.getBatch().getBatch_code());
			  }
			}
		}catch(Exception e){
			logger.error("GoogsReceiptNoteBean.loadGoodsReceiptNote:" + e.getMessage(), e);
		}
	}
	public void exportGoodsReceiptNew(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(goodsReceiptNoteCrud !=null){
				 String reportPath = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/reports/phieunhapsp.jasper");
				 Map<String, Object> importParam = new HashMap<String, Object>();
				 importParam.put("import_date",ToolTimeCustomer.convertDateToString(goodsReceiptNoteCrud.getImport_date(),"dd/MM/yyyy"));
				 importParam.put("note", goodsReceiptNoteCrud.getNote());
				 importParam.put("customer_name", goodsReceiptNoteCrud.getCustomer().getCustomer_name());
				 importParam.put("license_plate", goodsReceiptNoteCrud.getLicense_plate());
				 importParam.put("voucher_code", goodsReceiptNoteCrud.getVoucher_code());
				 importParam.put("listDetail", listGoodsReceiptNoteDetail);
				 JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
				 byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
				 String ba = Base64.getEncoder().encodeToString(data);
				 current.executeScript("utility.printPDF('" + ba + "')");
			}
		}catch(Exception e){
			logger.error("GoogsReceiptNoteBean.exportGoodsReceiptNew:" + e.getMessage(), e);
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
	private Workbook getWorkbook(InputStream inputStream, String nameFile) throws IOException {
		Workbook workbook = null;
		if (nameFile.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (nameFile.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}

		return workbook;
	}
	public void loadFileExcel(FileUploadEvent event){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		PrimeFaces current=PrimeFaces.current();
		try {
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<GoodsReceiptNoteDetail> listDetail = new ArrayList<>();
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
					GoodsReceiptNoteDetail lix = new GoodsReceiptNoteDetail();
					 while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								//masp
								String masp = Objects.toString(getCellValue(cell), null);
								if (masp != null && !"".equals(masp)) {
									ProductReqInfo p=new ProductReqInfo();
									productService.selectByCode(masp, p);
									if(p.getProduct() !=null){
										lix.setProduct(p.getProduct());
									}
								} else {
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								//số lượng
								String soluong = Objects.toString(getCellValue(cell), "0");
								lix.setQuantity(Double.parseDouble(soluong));
							} catch (Exception e) {
							}
							break;
						}
					}
					 listDetail.add(lix);
				}
				workBook = null;// free
				listGoodsReceiptNoteDetail=new ArrayList<>();
				for (GoodsReceiptNoteDetail it : listDetail) {
					for(GoodsReceiptNoteDetail q:listGoodsReceiptNoteDetail){
						if(it.getProduct().equals(q.getProduct())){
							current.executeScript(
									"swaldesigntimer('Thất bại', 'Sản phẩm bị trùng!','warning',2000);");
							return;
						}
					}
					listGoodsReceiptNoteDetail.add(it);
				}
				notify.success();
			}
		} catch (Exception e) {
			logger.error("GoodsReceiptNoteBean.loadFileExcel:" + e.getMessage(), e);
		}
	}
	public void showDialogUpload(){
		PrimeFaces current = PrimeFaces.current();
		current.executeScript("PF('dlgup1').show();");
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	public GoodsReceiptNoteDetail getGoodsReceiptNoteDetailCrud() {
		return goodsReceiptNoteDetailCrud;
	}

	public void setGoodsReceiptNoteDetailCrud(GoodsReceiptNoteDetail goodsReceiptNoteDetailCrud) {
		this.goodsReceiptNoteDetailCrud = goodsReceiptNoteDetailCrud;
	}

	public GoodsReceiptNoteDetail getGoodsReceiptNoteDetailSelect() {
		return goodsReceiptNoteDetailSelect;
	}

	public void setGoodsReceiptNoteDetailSelect(GoodsReceiptNoteDetail goodsReceiptNoteDetailSelect) {
		this.goodsReceiptNoteDetailSelect = goodsReceiptNoteDetailSelect;
	}

	public List<GoodsReceiptNoteDetail> getListGoodsReceiptNoteDetail() {
		return listGoodsReceiptNoteDetail;
	}

	public void setListGoodsReceiptNoteDetail(List<GoodsReceiptNoteDetail> listGoodsReceiptNoteDetail) {
		this.listGoodsReceiptNoteDetail = listGoodsReceiptNoteDetail;
	}
	public GoodsReceiptNote getGoodsReceiptNoteCrud() {
		return goodsReceiptNoteCrud;
	}
	public void setGoodsReceiptNoteCrud(GoodsReceiptNote goodsReceiptNoteCrud) {
		this.goodsReceiptNoteCrud = goodsReceiptNoteCrud;
	}
	public GoodsReceiptNote getGoodsReceiptNoteSelect() {
		return goodsReceiptNoteSelect;
	}
	public void setGoodsReceiptNoteSelect(GoodsReceiptNote goodsReceiptNoteSelect) {
		this.goodsReceiptNoteSelect = goodsReceiptNoteSelect;
	}
	public List<GoodsReceiptNote> getListGoodsReceiptNote() {
		return listGoodsReceiptNote;
	}
	public void setListGoodsReceiptNote(List<GoodsReceiptNote> listGoodsReceiptNote) {
		this.listGoodsReceiptNote = listGoodsReceiptNote;
	}
	public List<IECategories> getListIECategories() {
		return listIECategories;
	}
	public void setListIECategories(List<IECategories> listIECategories) {
		this.listIECategories = listIECategories;
	}
	public FormatHandler getFormatHandler() {
		return formatHandler;
	}
	public void setFormatHandler(FormatHandler formatHandler) {
		this.formatHandler = formatHandler;
	}
	public List<Warehouse> getListWarehouse() {
		return listWarehouse;
	}
	public void setListWarehouse(List<Warehouse> listWarehouse) {
		this.listWarehouse = listWarehouse;
	}
	public Date getFromDateSearch() {
		return fromDateSearch;
	}
	public void setFromDateSearch(Date fromDateSearch) {
		this.fromDateSearch = fromDateSearch;
	}
	public Date getToDateSearch() {
		return toDateSearch;
	}
	public void setToDateSearch(Date toDateSearch) {
		this.toDateSearch = toDateSearch;
	}
	public Customer getCustomerSearch() {
		return customerSearch;
	}
	public void setCustomerSearch(Customer customerSearch) {
		this.customerSearch = customerSearch;
	}
	public IECategories getIeCategoriesSearch() {
		return ieCategoriesSearch;
	}
	public void setIeCategoriesSearch(IECategories ieCategoriesSearch) {
		this.ieCategoriesSearch = ieCategoriesSearch;
	}
	public Warehouse getWarehouseSearch() {
		return warehouseSearch;
	}
	public void setWarehouseSearch(Warehouse warehouseSearch) {
		this.warehouseSearch = warehouseSearch;
	}
	public int getStatusSearch() {
		return statusSearch;
	}
	public void setStatusSearch(int statusSearch) {
		this.statusSearch = statusSearch;
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
	public Product getProductSearch() {
		return productSearch;
	}
	public void setProductSearch(Product productSearch) {
		this.productSearch = productSearch;
	}
	public String getBatchCodeSearch() {
		return batchCodeSearch;
	}
	public void setBatchCodeSearch(String batchCodeSearch) {
		this.batchCodeSearch = batchCodeSearch;
	}
}
