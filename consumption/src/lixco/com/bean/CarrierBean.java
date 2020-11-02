package lixco.com.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;

import com.google.gson.JsonObject;

import lixco.com.common.JsonParserUtil;
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.entity.Carrier;
import lixco.com.interfaces.ICarrierService;
import lixco.com.reqInfo.CarrierReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;

@Named
@ViewScoped
public class CarrierBean  extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private ICarrierService carrierService;
	private Carrier carrierCrud;
	private Carrier carrierSelect;
	private List<Carrier> listCarrier;
	private String carrierCode;
	private String carrierName;
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
			carrierCrud=new Carrier();
		}catch(Exception e){
			logger.error("CarrierBean.initItem:"+e.getMessage(),e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void search() {
		try {
			/*{ carrier_info:{carrier_code:'',carrier_name:''}, page:{page_index:0, page_size:0}}*/
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listCarrier = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("carrier_code", carrierCode);
			jsonInfo.addProperty("carrier_name", carrierName);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("carrier_info", jsonInfo);
			json.add("page", jsonPage);
			carrierService.search(JsonParserUtil.getGson().toJson(json), page, listCarrier);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		} catch (Exception e) {
			logger.error("CarrierBean.search:" + e.getMessage(), e);
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
			logger.error("CarrierBean.initRowPerPage:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			/*{ carrier_info:{carrier_code:'',carrier_name:''}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listCarrier = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("carrier_code",carrierCode);
			jsonInfo.addProperty("carrier_name", carrierName);
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("carrier_info", jsonInfo);
			json.add("page", jsonPage);
			carrierService.search(JsonParserUtil.getGson().toJson(json), page, listCarrier);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("CarrierBean.paginatorChange:" + e.getMessage(), e);
		}
	}

	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (carrierCrud != null) {
				String code = carrierCrud.getCarrier_code();
				String name = carrierCrud.getCarrier_name();
				if (code != null && !"".equals(code) && name != null && !"".equals(name)) {
					CarrierReqInfo t = new CarrierReqInfo(carrierCrud);
					if (carrierCrud.getId() == 0) {
						//check code đã tồn tại chưa
						if(allowSave(new Date())){
							carrierCrud.setCreated_date(new Date());
							carrierCrud.setCreated_by(account.getMember().getName());
							if(carrierService.insert(t)!=-1){
								current.executeScript("swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
								listCarrier.add(0,carrierCrud);
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã đã tồn tại!','warning',2000);");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						//check code update đã tồn tại chưa
						if(allowUpdate(new Date())){
							carrierCrud.setLast_modifed_by(account.getMember().getName());
							carrierCrud.setLast_modifed_date(new Date());
							if(carrierService.update(t)!=-1){
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								listCarrier.set(listCarrier.indexOf(carrierCrud),t.getCarrier());
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Mã thành phố đã tồn tại!','warning',2000);");
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
						
					}
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin thành phố không đầy đủ, điền đủ thông tin chứa(*)!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("CarrierBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}
	public void showEdit(){
		try{
			carrierCrud=carrierSelect.clone();
		}catch(Exception e){
			logger.error("CarrierBean.showEdit:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		carrierCrud=new Carrier();
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(carrierSelect !=null){
				if(allowDelete(new Date())){
					if(carrierService.deleteById(carrierSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listCarrier.remove(carrierSelect);
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
			logger.error("CarrierBean.delete:"+e.getMessage(),e);
		}
	}

	public Carrier getCarrierCrud() {
		return carrierCrud;
	}

	public void setCarrierCrud(Carrier carrierCrud) {
		this.carrierCrud = carrierCrud;
	}

	public Carrier getCarrierSelect() {
		return carrierSelect;
	}

	public void setCarrierSelect(Carrier carrierSelect) {
		this.carrierSelect = carrierSelect;
	}

	public List<Carrier> getListCarrier() {
		return listCarrier;
	}

	public void setListCarrier(List<Carrier> listCarrier) {
		this.listCarrier = listCarrier;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
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
