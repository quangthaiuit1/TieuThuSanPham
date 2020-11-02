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
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Carrier;
import lixco.com.entity.HarborCategory;
import lixco.com.entity.PricingProgram;
import lixco.com.entity.PricingProgramDetail;
import lixco.com.entity.PromotionProductGroup;
import lixco.com.entity.PromotionalPricing;
import lixco.com.entity.Stocker;
import lixco.com.interfaces.ICarrierService;
import lixco.com.interfaces.ICountryService;
import lixco.com.interfaces.ICustomerPricingProgramService;
import lixco.com.interfaces.IHarborCategoryService;
import lixco.com.interfaces.IPricingProgramDetailService;
import lixco.com.interfaces.IPricingProgramService;
import lixco.com.interfaces.IProductService;
import lixco.com.interfaces.IPromotionProductGroupService;
import lixco.com.interfaces.IPromotionalPricingService;
import lixco.com.interfaces.IStockerService;
import lixco.com.reqInfo.CarrierReqInfo;
import lixco.com.reqInfo.CountryReqInfo;
import lixco.com.reqInfo.HarborCategoryReqInfo;
import lixco.com.reqInfo.PricingProgramDetailReqInfo;
import lixco.com.reqInfo.PricingProgramReqInfo;
import lixco.com.reqInfo.ProductReqInfo;
import lixco.com.reqInfo.PromotionProductGroupReqInfo;
import lixco.com.reqInfo.PromotionalPricingReqInfo;
import lixco.com.reqInfo.StockerReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class DataSettingBean extends AbstractBean{
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IPricingProgramService pricingProgramService;
	@Inject
	private IPricingProgramDetailService pricingProgramDetailService;
	@Inject
	private ICustomerPricingProgramService customerPricingProgramService;
	@Inject
	private IProductService productService;
	@Inject
	private IPromotionalPricingService promotionalPricingService;
	@Inject
	private IPromotionProductGroupService promotionProductGroupService;
	@Inject 
	private IHarborCategoryService harborCategoryService;
	@Inject
	private ICarrierService carrierService;
	@Inject
	private IStockerService stockerService;
	@Inject
	private ICountryService countryService;
	private Account account;

	@Override
	protected void initItem() {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);

		} catch (Exception e) {

		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void showDialogNapCTDG(){
		PrimeFaces current=PrimeFaces.current();
		current.executeScript("PF('ctdg').show();");
	}
	public void showDialogNapCTCTDG(){
		try{
			PrimeFaces current=PrimeFaces.current();
			current.executeScript("PF('ctctdg').show();");
		}catch(Exception e){
			
		}
	}
	public void showDialogNapDonGiaKM(){
		try{
			PrimeFaces current=PrimeFaces.current();
			current.executeScript("PF('dongiakm').show();");
		}catch(Exception e){
			
		}
	}
	public void showDialogNhomSPKM(){
		try{
			PrimeFaces current=PrimeFaces.current();
			current.executeScript("PF('nhomspkm').show();");
		}catch(Exception e){
			
		}
	}
	public void showDialogHarbor(){
		try{
			PrimeFaces current=PrimeFaces.current();
			current.executeScript("PF('harbor').show();");
		}catch(Exception e){
			
		}
	}
	public void showDialogCarrier(){
		try{
			PrimeFaces current=PrimeFaces.current();
			current.executeScript("PF('carrier').show();");
		}catch(Exception e){
			
		}
	}
	public void showDialogStocker(){
		try{
			PrimeFaces current=PrimeFaces.current();
			current.executeScript("PF('stocker').show();");
		}catch(Exception e){
			
		}
	}
	public void napCTDonGia(FileUploadEvent event){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<PricingProgram> listPricingProgramTemp = new ArrayList<PricingProgram>();
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
					PricingProgram lix = new PricingProgram();
					lv2: while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								// mã chương trình
								String mact = Objects.toString(getCellValue(cell), null);
								if (mact != null && !"".equals(mact)) {
									lix.setProgram_code(mact);
								} else {
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								//từ ngày
								String tungay = Objects.toString(getCellValue(cell), null);
								if(tungay !=null && !"".equals(tungay)){
									Date tn=ToolTimeCustomer.convertStringToDate(tungay, "dd/MM/yyyy");
									if(tn ==null){
										System.out.println(tungay);
										continue lv1;
									}else{
										lix.setEffective_date(tn);
									}
								}
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								// denngay
								String denngay = Objects.toString(getCellValue(cell), null);
								if(denngay !=null && !"".equals(denngay)){
									Date dn=ToolTimeCustomer.convertStringToDate(denngay,"dd/MM/yyyy");
									if(dn ==null){
										System.out.println(denngay);
									}else{
										lix.setExpiry_date(dn);
									}
									
								}
							} catch (Exception e) {
							}
							break;
						case 3:
							try {
								//mã chương trình chính
								String mactc = Objects.toString(getCellValue(cell),null);
								if(mactc !=null && !"".equals(mactc)){
									PricingProgramReqInfo parent=new PricingProgramReqInfo();
									pricingProgramService.selectByCode(mactc, parent);
									lix.setParent_pricing_program(parent.getPricing_program());
									
								}
							} catch (Exception e) {
							}
							break;
						case 4:
							try {
								//update time
								String ut = Objects.toString(getCellValue(cell), null);
								if(ut !=null && !"".equals(ut)){
									Date updatetime=ToolTimeCustomer.convertStringToDate(ut,"dd/MM/yyyy HH:mm");
									if(updatetime ==null){
										System.out.println(ut);
									}
									lix.setUpdate_time(updatetime);
									
								}
							} catch (Exception e) {
							}
							break;

						}
					}
					listPricingProgramTemp.add(lix);
				}
				workBook = null;// free
				for (PricingProgram it : listPricingProgramTemp) {
					PricingProgramReqInfo t = new PricingProgramReqInfo();
					pricingProgramService.selectByCode(it.getProgram_code(), t);
					PricingProgram p = t.getPricing_program();
					t.setPricing_program(it);
					if (p != null) {
						it.setId(p.getId());
//						it.setLast_modifed_by(account.getMember().getName());
//						it.setLast_modifed_date(new Date());
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						pricingProgramService.update(t);
					} else {
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						pricingProgramService.insert(t);
					}
				}
				notify.success();
			}
		}catch(Exception e){
			logger.error("DataSettingBean.napCTDonGia:"+e.getMessage(),e);
		}
	}
	public void napCTCTDonGia(FileUploadEvent event){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<PricingProgramDetail> listPricingProgramDetailTemp = new ArrayList<PricingProgramDetail>();
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
					PricingProgramDetail lix = new PricingProgramDetail();
					lv2: while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();
						
						switch (columnIndex) {
						case 0:
							try {
								// mã chương trình
								String mact = Objects.toString(getCellValue(cell), null);
								if (mact != null && !"".equals(mact)) {
									//find chương trình
									PricingProgramReqInfo ppr=new PricingProgramReqInfo();
									pricingProgramService.selectByCode(mact, ppr);
									if(ppr.getPricing_program() !=null){
										lix.setPricing_program(ppr.getPricing_program());
									}else{
										continue lv1;
									}
								} else {
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								//mã sản phẩm
								String masp = Objects.toString(getCellValue(cell), null);
								if(masp !=null && !"".equals(masp)){
									// tim sản phẩm
									ProductReqInfo preq=new ProductReqInfo();
									productService.selectByCode(masp,preq );
									if(preq.getProduct() !=null){
										lix.setProduct(preq.getProduct());
									}else{
										continue lv1;
									}
								}else{
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								// số lượng
								String soluong = Objects.toString(getCellValue(cell), "0");
								lix.setQuantity(Double.parseDouble(soluong));
							} catch (Exception e) {
							}
							break;
						case 3:
							try {
								//đơn giá
								String dongia = Objects.toString(getCellValue(cell), "0");
								lix.setUnit_price(Double.parseDouble(dongia));
							} catch (Exception e) {
							}
							break;
						case 4:
							try {
								//update time
								String ut = Objects.toString(getCellValue(cell), null);
								if(ut !=null && !"".equals(ut)){
									Date updatetime=ToolTimeCustomer.convertStringToDate(ut,"dd/MM/yyyy HH:mm");
									if(updatetime ==null){
										System.out.println(ut);
									}
									lix.setUpdate_time(updatetime);
									
								}
							} catch (Exception e) {
							}
							break;
						case 5:
							try {
								//lợi nhuận
								String loinhuan = Objects.toString(getCellValue(cell), "0");
								lix.setRevenue_per_ton(Double.parseDouble(loinhuan));
							} catch (Exception e) {
							}
							break;
							
						}
					}
					listPricingProgramDetailTemp.add(lix);
				}
				workBook = null;// free
				for (PricingProgramDetail it : listPricingProgramDetailTemp) {
					pricingProgramDetailService.insert(new PricingProgramDetailReqInfo(it));
				}
				notify.success();
			}
		}catch(Exception e){
			logger.error("DataSettingBean.napCTCTDonGia:"+e.getMessage(),e);
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
	public void napDonGiaKM(FileUploadEvent event) {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<PromotionalPricing> listPromotionalPricingTemp = new ArrayList<>();
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
					PromotionalPricing lix = new PromotionalPricing();
					lv2: while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								String masp = Objects.toString(getCellValue(cell), null);
								if (masp != null && !"".equals(masp)) {
									ProductReqInfo p=new ProductReqInfo();
									productService.selectByCode(masp,p);
									if(p.getProduct()==null){
										continue lv1;
									}
									lix.setProduct(p.getProduct());
								} else {
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								//đơn giá
								String dongia = Objects.toString(getCellValue(cell), null);
								lix.setUnit_price(Double.parseDouble(dongia));
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								//ngày hiệu lực
								String dateHL = Objects.toString(getCellValue(cell), null);
								lix.setEffective_date(ToolTimeCustomer.convertStringToDate(dateHL, "dd/MM/yyyy"));
							} catch (Exception e) {
							}
							break;
						case 3:
							try {
								//ngày hết hạn
								String ngayHH = Objects.toString(getCellValue(cell), null);
								lix.setExpiry_date(ToolTimeCustomer.convertStringToDate(ngayHH, "dd/MM/yyyy"));
							} catch (Exception e) {
							}
							break;
						}
					}
					listPromotionalPricingTemp.add(lix);
				}
				workBook = null;// free
				//delete all
				promotionalPricingService.deleteAll();
				for (PromotionalPricing it : listPromotionalPricingTemp) {
					it.setCreated_by(account.getMember().getName());
					it.setCreated_date(new Date());
					promotionalPricingService.insert(new PromotionalPricingReqInfo(it));
				}
				notify.success();

			}
		} catch (Exception e) {
			logger.error("ProductBean.loadExcel:" + e.getMessage(), e);
		}
	}
	public void napNhomSPKM(FileUploadEvent event) {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<PromotionProductGroup> listPromotionProductGroupTemp = new ArrayList<>();
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
					PromotionProductGroup lix = new PromotionProductGroup();
					lv2: while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();
						
						switch (columnIndex) {
						case 0:
							try {
								String manhomsp = Objects.toString(getCellValue(cell), null);
								if (manhomsp != null && !"".equals(manhomsp)) {
									lix.setCode(manhomsp);
								} else {
									continue lv1;
								}
							} catch (Exception e) {
								continue lv1;
							}
							break;
						case 1:
							try {
								String tennhomsp = Objects.toString(getCellValue(cell), null);
								lix.setName(tennhomsp);
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								String dvt = Objects.toString(getCellValue(cell), null);
								lix.setUnit(dvt);
							} catch (Exception e) {
							}
							break;
						case 3:
							try {
								//số lượng carton
								String slCarton = Objects.toString(getCellValue(cell), null);
								lix.setCarton_quantity(Double.parseDouble(Objects.toString(slCarton, "0")));
							} catch (Exception e) {
							}
							break;
						case 4:
							try {
								//đơn vị carton
								String dvCarton = Objects.toString(getCellValue(cell), null);
								lix.setCarton_unit(dvCarton);
							} catch (Exception e) {
							}
							break;
						}
					}
					listPromotionProductGroupTemp.add(lix);
				}
				workBook = null;// free
				//delete all
				promotionProductGroupService.deleteAll();
				for (PromotionProductGroup it : listPromotionProductGroupTemp) {
					promotionProductGroupService.insert(new PromotionProductGroupReqInfo(it));
				}
				notify.success();
				
			}
		} catch (Exception e) {
			logger.error("ProductBean.loadExcel:" + e.getMessage(), e);
		}
	}
	public void loadHarborExcel(FileUploadEvent event) {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<HarborCategory> listHarbor = new ArrayList<HarborCategory>();
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
					HarborCategory lix = new HarborCategory();
					 while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								//code
								String code = Objects.toString(getCellValue(cell), null);
								if (code != null && !"".equals(code)) {
									lix.setHarbor_code_old(code);
								} else {
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								//tên cảng
								String name = Objects.toString(getCellValue(cell), null);
								lix.setHarbor_name(name);
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								// địa chỉ
								String diachi = Objects.toString(getCellValue(cell), null);
								lix.setAddress(diachi);
							} catch (Exception e) {
							}
							break;
						case 3:
							try {
								// type
								int type = Integer.parseInt(Objects.toString(getCellValue(cell), "0"));
								lix.setHarbor_type(type);
							} catch (Exception e) {
							}
							break;
						 case 4:
							 try{
								 String countryCode=Objects.toString(getCellValue(cell),null);
								 if(countryCode !=null && !"".equals(countryCode)){
									 CountryReqInfo creqinfo=new CountryReqInfo();
									 countryService.selectByCode(countryCode.trim(), creqinfo);
									 lix.setCountry(creqinfo.getCountry());
								 }
							 }catch (Exception e) {
							}
						}
					}
					 listHarbor.add(lix);
				}
				workBook = null;// free
				for (HarborCategory it : listHarbor) {
					HarborCategoryReqInfo t = new HarborCategoryReqInfo();
					
					harborCategoryService.selectByCode(it.getHarbor_code(), t);
					HarborCategory p = t.getHarbor_category();
					t.setHarbor_category(it);
					if (p != null) {
						it.setId(p.getId());
						it.setLast_modifed_by(account.getMember().getName());
						it.setLast_modifed_date(new Date());
						harborCategoryService.update(t);
					} else {
						//init code new 
						harborCategoryService.initCode(it);
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						harborCategoryService.insert(t);
					}
				}
				notify.success();
			}
		} catch (Exception e) {
			logger.error("DataSettingBean.loadExcel:" + e.getMessage(), e);
		}
	}
	public void napExcelCarrier(FileUploadEvent event) {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<Carrier> listCarrier = new ArrayList<Carrier>();
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
					Carrier lix = new Carrier();
					while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();
						
						switch (columnIndex) {
						case 0:
							try {
								//code
								String code = Objects.toString(getCellValue(cell), null);
								if (code != null && !"".equals(code)) {
									lix.setCarrier_code(code);
								} else {
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								//tên người vận chuyển
								String name = Objects.toString(getCellValue(cell), null);
								lix.setCarrier_name(name);
							} catch (Exception e) {
							}
							break;
						}
					}
					listCarrier.add(lix);
				}
				workBook = null;// free
				for (Carrier it : listCarrier) {
					CarrierReqInfo t = new CarrierReqInfo();
					carrierService.selectByCode(it.getCarrier_code(), t);
					Carrier p = t.getCarrier();
					t.setCarrier(it);
					if (p != null) {
						it.setId(p.getId());
						carrierService.update(t);
					} else {
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						carrierService.insert(t);
					}
				}
				notify.success();
			}
		} catch (Exception e) {
			logger.error("DataSettingBean.napExcelCarrier:" + e.getMessage(), e);
		}
	}
	public void napExcelStocker(FileUploadEvent event) {
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (event.getFile() != null) {
				UploadedFile part = event.getFile();
				byte[] byteFile = event.getFile().getContents();
				List<Stocker> listStocker = new ArrayList<Stocker>();
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
					Stocker lix = new Stocker();
					while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();
						
						switch (columnIndex) {
						case 0:
							try {
								//code
								String code = Objects.toString(getCellValue(cell), null);
								if (code != null && !"".equals(code)) {
									lix.setStocker_code(code);
								} else {
									continue lv1;
								}
							} catch (Exception e) {
							}
							break;
						case 1:
							try {
								//tên thủ kho
								String name = Objects.toString(getCellValue(cell), null);
								lix.setStocker_name(name);
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								//nghĩ việc
								String dis = Objects.toString(getCellValue(cell), null);
								lix.setDisable(Boolean.parseBoolean(dis));
							} catch (Exception e) {
							}
							break;
						}
					}
					listStocker.add(lix);
				}
				workBook = null;// free
				for (Stocker it : listStocker) {
					StockerReqInfo t = new StockerReqInfo();
					stockerService.selectByCode(it.getStocker_code(), t);
					Stocker p = t.getStocker();
					t.setStocker(it);
					if (p != null) {
						it.setId(p.getId());
						stockerService.update(t);
					} else {
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						stockerService.insert(t);
					}
				}
				notify.success();
			}
		} catch (Exception e) {
			logger.error("DataSettingBean.napExcelCarrier:" + e.getMessage(), e);
		}
	}

}
