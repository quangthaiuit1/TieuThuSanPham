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
import lixco.com.common.SessionHelper;
import lixco.com.entity.PromotionProductGroup;
import lixco.com.interfaces.IPromotionProductGroupService;
import lixco.com.reqInfo.PromotionProductGroupReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class PromotionProductGroupBean  extends AbstractBean{
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IPromotionProductGroupService promotionProductGroupService;
	private PromotionProductGroup promotionProductGroupCrud;
	private PromotionProductGroup promotionProductGroupSelect;
	private List<PromotionProductGroup> listPromotionProductGroup;
	/*search*/
	private String codeSearch;
	private String nameSearch;
	private Account account;
	@Override
	protected void initItem() {
		try{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			search();
		}catch (Exception e) {
			logger.error("PromotionProductGroupBean.initItem:"+e.getMessage(),e);
		}
	}
	public void search() {
		try {
			/*{code:'',name:''}*/
			JsonObject json=new JsonObject();
			json.addProperty("code", codeSearch);
			json.addProperty("name", nameSearch);
			listPromotionProductGroup=new ArrayList<>();
			promotionProductGroupService.search(JsonParserUtil.getGson().toJson(json),listPromotionProductGroup);
		} catch (Exception e) {
			logger.error("PromotionProductGroupBean.search:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (promotionProductGroupCrud != null) {
				String code= promotionProductGroupCrud.getCode();
				String name=promotionProductGroupCrud.getName();
				if (code !=null && !"".equals(code) && name !=null && !"".equals(name) ) {
					PromotionProductGroupReqInfo t = new PromotionProductGroupReqInfo(promotionProductGroupCrud);
					if (promotionProductGroupCrud.getId() == 0) {
						if (allowSave(new Date())) {
							int chk=promotionProductGroupService.insert(t);
							if(chk==0){
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
								listPromotionProductGroup.add(0,promotionProductGroupCrud.clone());
							}else{
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Lưu sản phẩm khuyến mãi thất bại!','error',2000);");
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					} else {
						if (allowUpdate(new Date())) {
							int chk=promotionProductGroupService.update(t);
							if(chk==0){
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								listPromotionProductGroup.set(listPromotionProductGroup.indexOf(promotionProductGroupCrud),promotionProductGroupCrud);
							}else{
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Cập nhật thất bại!','error',2000);");
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
			logger.error("PromotionProductGroupBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showDialogEdit() {
		PrimeFaces current = PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (promotionProductGroupSelect != null) {
				promotionProductGroupCrud = promotionProductGroupSelect.clone();
				current.executeScript("PF('dlg1').show();");
			} else {
				notify.message("Chọn dòng để chỉnh sửa!");
			}
		} catch (Exception e) {
			logger.error("PromotionProductGroupBean.showDialogEdit:" + e.getMessage(), e);
		}
	}

	public void showDialog() {
		PrimeFaces current = PrimeFaces.current();
		try {
			promotionProductGroupCrud = new PromotionProductGroup();
			current.executeScript("PF('dlg1').show();");
		} catch (Exception e) {
			logger.error("PromotionProductGroupBean.showDialog:" + e.getMessage(), e);
		}
	}
	public void delete() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (promotionProductGroupSelect != null) {
				if (allowDelete(new Date())) {
					if (promotionProductGroupService.deleteById(promotionProductGroupSelect.getId()) != -1) {
						current.executeScript("swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listPromotionProductGroup.remove(promotionProductGroupSelect);
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
			logger.error("PromotionProductGroupBean.delete:" + e.getMessage(), e);
		}
	}
	@Override
	protected Logger getLogger() {
		return logger;
	}
	public PromotionProductGroup getPromotionProductGroupCrud() {
		return promotionProductGroupCrud;
	}
	public void setPromotionProductGroupCrud(PromotionProductGroup promotionProductGroupCrud) {
		this.promotionProductGroupCrud = promotionProductGroupCrud;
	}
	public PromotionProductGroup getPromotionProductGroupSelect() {
		return promotionProductGroupSelect;
	}
	public void setPromotionProductGroupSelect(PromotionProductGroup promotionProductGroupSelect) {
		this.promotionProductGroupSelect = promotionProductGroupSelect;
	}
	public List<PromotionProductGroup> getListPromotionProductGroup() {
		return listPromotionProductGroup;
	}
	public void setListPromotionProductGroup(List<PromotionProductGroup> listPromotionProductGroup) {
		this.listPromotionProductGroup = listPromotionProductGroup;
	}
	public String getCodeSearch() {
		return codeSearch;
	}
	public void setCodeSearch(String codeSearch) {
		this.codeSearch = codeSearch;
	}
	public String getNameSearch() {
		return nameSearch;
	}
	public void setNameSearch(String nameSearch) {
		this.nameSearch = nameSearch;
	}
	
	
}
