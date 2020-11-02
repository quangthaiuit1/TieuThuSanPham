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

import lixco.com.common.SessionHelper;
import lixco.com.entity.Stocker;
import lixco.com.interfaces.IStockerService;
import lixco.com.reqInfo.StockerReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;

@Named
@ViewScoped
public class StockerBean extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IStockerService stockerService;
	private Stocker stockerCrud;
	private Stocker stockerSelect;
	private List<Stocker> listStocker;
	private String text;
	private int disable;
	private Account account;
	@Override
	protected void initItem() {
		try{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			stockerCrud=new Stocker();
			disable=-1;
			search();
		}catch (Exception e) {
			logger.error("StockerBean.initItem:"+e.getMessage(),e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void search(){
		try{
			listStocker=new ArrayList<Stocker>();
			stockerService.search(text, disable, listStocker);
		}catch (Exception e) {
			logger.error("StockerBean.search:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		stockerCrud=new Stocker();
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(stockerCrud !=null){
				if(allowDelete(new Date())){
					if(stockerService.deleteById(stockerSelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listStocker.remove(stockerSelect);
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
			logger.error("StockerBean.delete:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (stockerCrud != null) {
				String code = stockerCrud.getStocker_code();
				String name = stockerCrud.getStocker_name();
				if (code != null && !"".equals(code) && name != null && !"".equals(name)) {
					StockerReqInfo t = new StockerReqInfo(stockerCrud);
					if (stockerCrud.getId() == 0) {
						stockerCrud.setCreated_by(account.getMember().getCode());
						stockerCrud.setCreated_date(new Date());
						if (allowSave(new Date())) {
							int chk=stockerService.insert(t);
							switch (chk) {
							case 0:
								listStocker.add(0, stockerCrud.clone());
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
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
							stockerCrud.setLast_modifed_by(account.getMember().getName());
							stockerCrud.setLast_modifed_date(new Date());
							int chk=stockerService.update(t);
							switch (chk) {
							case 0:
								listStocker.set(listStocker.indexOf(stockerCrud),t.getStocker());
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
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
			logger.error("StockerBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showEdit() {
		try {
			stockerCrud = stockerSelect.clone();
		} catch (Exception e) {
			logger.error("StockerBean.showEdit:" + e.getMessage(), e);
		}

	}

	public Stocker getStockerCrud() {
		return stockerCrud;
	}

	public void setStockerCrud(Stocker stockerCrud) {
		this.stockerCrud = stockerCrud;
	}

	public Stocker getStockerSelect() {
		return stockerSelect;
	}

	public void setStockerSelect(Stocker stockerSelect) {
		this.stockerSelect = stockerSelect;
	}

	public List<Stocker> getListStocker() {
		return listStocker;
	}

	public void setListStocker(List<Stocker> listStocker) {
		this.listStocker = listStocker;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getDisable() {
		return disable;
	}

	public void setDisable(int disable) {
		this.disable = disable;
	}

}
