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

import lixco.com.common.FormatHandler;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Product;
import lixco.com.entity.PromotionalPricing;
import lixco.com.interfaces.IProductService;
import lixco.com.interfaces.IPromotionalPricingService;
import lixco.com.reqInfo.PromotionalPricingReqInfo;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class PromotionalPricingBean  extends AbstractBean{
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IPromotionalPricingService promotionalPricingService;
	@Inject
	private IProductService productService;
	private PromotionalPricing promotionalPricingCrud;
	private PromotionalPricing promotionalPricingSelect;
	private List<PromotionalPricing> listPromotionalPricing;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	/*search*/
	private Product productSearch;
	private Date fromDateSearch;
	private Date toDateSearch;
	private Account account;
	private FormatHandler formatHandler;
	@Override
	protected void initItem() {
		try{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			formatHandler=FormatHandler.getInstance();
			int month=ToolTimeCustomer.getMonthCurrent();
			int year=ToolTimeCustomer.getYearCurrent();
			fromDateSearch=ToolTimeCustomer.getDateMinCustomer(month, year);
			toDateSearch=ToolTimeCustomer.getDateMaxCustomer(month, year);
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			search();
		}catch (Exception e) {
			logger.error("PromotionalPricingBean.initItem:"+e.getMessage(),e);
		}
	}
	public void search() {
		try {
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listPromotionalPricing = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			JsonObject jsonInfo = new JsonObject();
			/*{promotional_pricing_info:{product_id:0,from_date:'',to_date:''}, page:{page_index:0, page_size:0}}*/
			jsonInfo.addProperty("product_id",productSearch == null ? 0 :productSearch.getId());
			jsonInfo.addProperty("from_date",ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date",ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", 1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("promotional_pricing_info", jsonInfo);
			json.add("page", jsonPage);
			promotionalPricingService.search(JsonParserUtil.getGson().toJson(json), page, listPromotionalPricing);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		} catch (Exception e) {
			logger.error("PromotionalPricingBean.search:" + e.getMessage(), e);
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
			logger.error("PromotionalPricingBean.initRowPerPage:" + e.getMessage(), e);
		}
	}

	public void paginatorChange(int currentPage) {
		try {
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listPromotionalPricing = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo = new JsonObject();
			/*{promotional_pricing_info:{product_id:0,from_date:'',to_date:''}, page:{page_index:0, page_size:0}}*/
			jsonInfo.addProperty("product_id",productSearch == null ? 0 :productSearch.getId());
			jsonInfo.addProperty("from_date",ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date",ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			JsonObject jsonPage = new JsonObject();
			jsonPage.addProperty("page_index", currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json = new JsonObject();
			json.add("promotional_pricing_info", jsonInfo);
			json.add("page", jsonPage);
			promotionalPricingService.search(JsonParserUtil.getGson().toJson(json), page, listPromotionalPricing);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("PromotionalPricingBean.paginatorChange:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdate() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (promotionalPricingCrud != null) {
				Product product= promotionalPricingCrud.getProduct();
				Date effectiveDate = promotionalPricingCrud.getEffective_date();
				Date expriryDate=promotionalPricingCrud.getExpiry_date();
				if (product != null && effectiveDate != null &&  expriryDate != null) {
					PromotionalPricingReqInfo t = new PromotionalPricingReqInfo(promotionalPricingCrud);
					if (promotionalPricingCrud.getId() == 0) {
						if (allowSave(new Date())) {
							promotionalPricingCrud.setCreated_by(account.getMember().getName());
							promotionalPricingCrud.setCreated_date(new Date());
							int chk=promotionalPricingService.insert(t);
							switch (chk) {
							case 0:
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
								listPromotionalPricing.add(0,promotionalPricingCrud.clone());
								break;
							case -2:
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Sản phẩm đã tồn tại!','warning',2000);");
								break;
							default:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Lưu đơn giá sản phẩm thất bại!','error',2000);");
								break;
							}
						} else {
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					} else {
						if (allowUpdate(new Date())) {
							promotionalPricingCrud.setLast_modifed_by(account.getMember().getName());
							promotionalPricingCrud.setLast_modifed_date(new Date());
							int chk=promotionalPricingService.update(t);
							switch (chk) {
							case 0:
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								listPromotionalPricing.set(listPromotionalPricing.indexOf(promotionalPricingCrud),promotionalPricingCrud);
								break;
							case -2:
								current.executeScript(
										"swaldesigntimer('Cảnh báo', 'Sản phẩm đã tồn tại!','warning',2000);");
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
				} else {
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ, điền đủ thông tin chứa(*)!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("PromotionalPricingBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}

	public void showDialogEdit() {
		PrimeFaces current = PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if (promotionalPricingSelect != null) {
				promotionalPricingCrud = promotionalPricingSelect.clone();
				current.executeScript("PF('dlg1').show();");
			} else {
				notify.message("Chọn dòng để chỉnh sửa!");
			}
		} catch (Exception e) {
			logger.error("PromotionalPricingBean.showDialogEdit:" + e.getMessage(), e);
		}
	}

	public void showDialog() {
		PrimeFaces current = PrimeFaces.current();
		try {
			promotionalPricingCrud = new PromotionalPricing();
			current.executeScript("PF('dlg1').show();");
		} catch (Exception e) {
			logger.error("promotionalPricingSelect.showDialog:" + e.getMessage(), e);
		}
	}
	public void delete() {
		PrimeFaces current = PrimeFaces.current();
		try {
			if (promotionalPricingSelect != null) {
				if (allowDelete(new Date())) {
					if (promotionalPricingService.deleteById(promotionalPricingSelect.getId()) != -1) {
						current.executeScript("swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						listPromotionalPricing.remove(promotionalPricingSelect);
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
			logger.error("promotionalPricingSelect.delete:" + e.getMessage(), e);
		}
	}

	public List<Product> completeProduct(String text) {
		try {
			List<Product> list = new ArrayList<Product>();
			productService.findLike(FormatHandler.getInstance().converViToEn(text), 120, list);
			return list;
		} catch (Exception e) {
			logger.error("promotionalPricingSelect.completeProduct:" + e.getMessage(), e);
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

	@Override
	protected Logger getLogger() {
		return logger;
	}

	public PromotionalPricing getPromotionalPricingCrud() {
		return promotionalPricingCrud;
	}

	public void setPromotionalPricingCrud(PromotionalPricing promotionalPricingCrud) {
		this.promotionalPricingCrud = promotionalPricingCrud;
	}

	public PromotionalPricing getPromotionalPricingSelect() {
		return promotionalPricingSelect;
	}

	public void setPromotionalPricingSelect(PromotionalPricing promotionalPricingSelect) {
		this.promotionalPricingSelect = promotionalPricingSelect;
	}

	public List<PromotionalPricing> getListPromotionalPricing() {
		return listPromotionalPricing;
	}

	public void setListPromotionalPricing(List<PromotionalPricing> listPromotionalPricing) {
		this.listPromotionalPricing = listPromotionalPricing;
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
	public FormatHandler getFormatHandler() {
		return formatHandler;
	}
	public void setFormatHandler(FormatHandler formatHandler) {
		this.formatHandler = formatHandler;
	}
}
