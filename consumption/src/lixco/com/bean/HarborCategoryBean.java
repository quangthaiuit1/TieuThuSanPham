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
import lixco.com.entity.HarborCategory;
import lixco.com.interfaces.IHarborCategoryService;
import lixco.com.reqInfo.HarborCategoryReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
@Named
@ViewScoped
public class HarborCategoryBean extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IHarborCategoryService harborCategoryService;
	private HarborCategory harborCategoryCrud;
	private HarborCategory harborCategorySelect;
	private List<HarborCategory> listHarborCategory;
	private int harborType;
	private Account account;
	@Override
	protected void initItem() {
		try{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			harborCategoryCrud=new HarborCategory();
			harborType=-1;
			search();
		}catch (Exception e) {
			logger.error("HarborCategoryBean.initItem:"+e.getMessage(),e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public void search(){
		try{
			listHarborCategory=new ArrayList<HarborCategory>();
			harborCategoryService.search(harborType, listHarborCategory);
		}catch (Exception e) {
			logger.error("HarborCategoryBean.search:"+e.getMessage(),e);
		}
	}
	public void createNew(){
		harborCategoryCrud=new HarborCategory();
		harborCategoryService.initCode(harborCategoryCrud);
	}
	public void delete(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(harborCategorySelect !=null){
				if(allowDelete(new Date())){
					if(harborCategoryService.deleteById(harborCategorySelect.getId())!=-1){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listHarborCategory.remove(harborCategorySelect);
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
			logger.error("HarborCategoryBean.delete:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (harborCategoryCrud != null) {
				String code = harborCategoryCrud.getHarbor_code();
				String name = harborCategoryCrud.getHarbor_name();
				if (code != null && !"".equals(code) && name != null && !"".equals(name)) {
					HarborCategoryReqInfo t = new HarborCategoryReqInfo(harborCategoryCrud);
					if (harborCategoryCrud.getId() == 0) {
						harborCategoryCrud.setCreated_by(account.getMember().getCode());
						harborCategoryCrud.setCreated_date(new Date());
						if (allowSave(new Date())) {
							int chk=harborCategoryService.insert(t);
							switch (chk) {
							case 0:
								listHarborCategory.add(0, harborCategoryCrud.clone());
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
							harborCategoryCrud.setLast_modifed_by(account.getMember().getName());
							harborCategoryCrud.setLast_modifed_date(new Date());
							int chk=harborCategoryService.update(t);
							switch (chk) {
							case 0:
								listHarborCategory.set(listHarborCategory.indexOf(harborCategoryCrud),t.getHarbor_category());
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
			logger.error("HarborCategoryBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showEdit() {
		try {
			harborCategoryCrud = harborCategorySelect.clone();
		} catch (Exception e) {
			logger.error("CarOwnerBean.showEdit:" + e.getMessage(), e);
		}

	}

	public HarborCategory getHarborCategoryCrud() {
		return harborCategoryCrud;
	}

	public void setHarborCategoryCrud(HarborCategory harborCategoryCrud) {
		this.harborCategoryCrud = harborCategoryCrud;
	}

	public HarborCategory getHarborCategorySelect() {
		return harborCategorySelect;
	}

	public void setHarborCategorySelect(HarborCategory harborCategorySelect) {
		this.harborCategorySelect = harborCategorySelect;
	}

	public List<HarborCategory> getListHarborCategory() {
		return listHarborCategory;
	}

	public void setListHarborCategory(List<HarborCategory> listHarborCategory) {
		this.listHarborCategory = listHarborCategory;
	}

	public int getHarborType() {
		return harborType;
	}

	public void setHarborType(int harborType) {
		this.harborType = harborType;
	}

	
}
