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
import lixco.com.entity.PaymentMethod;
import lixco.com.interfaces.IPaymentMethodService;
import lixco.com.reqInfo.PaymentMethodReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class PaymentMethodBean  extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IPaymentMethodService paymentMethodService;
	private PaymentMethod paymentMethodCrud;
	private PaymentMethod paymentMethodSelect;
	private List<PaymentMethod> listPaymentMethod;
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
			logger.error("PaymentMethodBean.initItem:"+e.getMessage(),e);
		}
	}

	public void search(){
		try{
			listPaymentMethod=new ArrayList<PaymentMethod>();
			paymentMethodService.selectAll(listPaymentMethod);
		}catch (Exception e) {
			logger.error("PaymentMethodBean.search:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		paymentMethodCrud=new PaymentMethod();
		paymentMethodCrud.setMethod_code(paymentMethodService.initCode());
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(paymentMethodSelect !=null){
				if(allowDelete(new Date())){
					if(paymentMethodService.deleteById(paymentMethodSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listPaymentMethod.remove(paymentMethodSelect);
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
			logger.error("PaymentMethodBean.delete:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (paymentMethodCrud != null) {
				String code = paymentMethodCrud.getMethod_code();
				String name = paymentMethodCrud.getMethod_name();
				if (code != null && !"".equals(code) && name != null && !"".equals(name)) {
					PaymentMethodReqInfo t = new PaymentMethodReqInfo(paymentMethodCrud);
					if (paymentMethodCrud.getId() == 0) {
						// check code đã tồn tại chưa
						if (allowSave(new Date())) {
							int chk=paymentMethodService.insert(t);
							switch (chk) {
							case 0:
								listPaymentMethod.add(0, paymentMethodCrud.clone());
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
							int chk=paymentMethodService.update(t);
							switch (chk) {
							case 0:
								listPaymentMethod.set(listPaymentMethod.indexOf(paymentMethodCrud), t.getPayment_method());
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
			logger.error("PaymentMethodBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showEdit() {
		try {
			paymentMethodCrud = paymentMethodSelect.clone();
		} catch (Exception e) {
			logger.error("PaymentMethodBean.showEdit:" + e.getMessage(), e);
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
				List<PaymentMethod> listPaymentMethodTemp = new ArrayList<PaymentMethod>();
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
					PaymentMethod lix = new PaymentMethod();
					 while (cells.hasNext()) {
						Cell cell = cells.next();
						int columnIndex = cell.getColumnIndex();

						switch (columnIndex) {
						case 0:
							try {
								//ma hình thức old
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
								//Tên hình thức thanh toán
								String name = Objects.toString(getCellValue(cell), null);
								lix.setMethod_name(name);
							} catch (Exception e) {
							}
							break;
						case 2:
							try {
								//Hình thức thanh toán
								String payment = Objects.toString(getCellValue(cell), null);
								lix.setPayment(payment);
							} catch (Exception e) {
							}
							break;
						}
					}
					listPaymentMethodTemp.add(lix);
				}
				workBook = null;// free
				for (PaymentMethod it : listPaymentMethodTemp) {
					PaymentMethodReqInfo t = new PaymentMethodReqInfo();
					paymentMethodService.selectByCodeOld(it.getOld_code(), t);
					PaymentMethod p = t.getPayment_method();
					t.setPayment_method(it);
					if (p != null) {
						it.setId(p.getId());
						paymentMethodService.update(t);
					} else {
						//init code new 
						String code=paymentMethodService.initCode();
						it.setMethod_code(code);
						it.setCreated_by(account.getMember().getName());
						it.setCreated_date(new Date());
						paymentMethodService.insert(t);
					}
				}
				search();
				notify.success();
			}
		} catch (Exception e) {
			logger.error("PaymentMethodBean.loadExcel:" + e.getMessage(), e);
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

	public PaymentMethod getPaymentMethodCrud() {
		return paymentMethodCrud;
	}

	public void setPaymentMethodCrud(PaymentMethod paymentMethodCrud) {
		this.paymentMethodCrud = paymentMethodCrud;
	}

	public PaymentMethod getPaymentMethodSelect() {
		return paymentMethodSelect;
	}

	public void setPaymentMethodSelect(PaymentMethod paymentMethodSelect) {
		this.paymentMethodSelect = paymentMethodSelect;
	}

	public List<PaymentMethod> getListPaymentMethod() {
		return listPaymentMethod;
	}

	public void setListPaymentMethod(List<PaymentMethod> listPaymentMethod) {
		this.listPaymentMethod = listPaymentMethod;
	}
	

}
