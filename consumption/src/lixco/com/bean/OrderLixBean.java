package lixco.com.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import com.google.gson.JsonObject;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;

import lixco.com.common.FormatHandler;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Car;
import lixco.com.entity.Customer;
import lixco.com.entity.CustomerTypes;
import lixco.com.entity.DeliveryPricing;
import lixco.com.entity.FreightContract;
import lixco.com.entity.IECategories;
import lixco.com.entity.OrderDetail;
import lixco.com.entity.OrderLix;
import lixco.com.entity.PaymentMethod;
import lixco.com.entity.PricingProgram;
import lixco.com.entity.PricingProgramDetail;
import lixco.com.entity.Product;
import lixco.com.entity.PromotionOrderDetail;
import lixco.com.entity.PromotionProgramDetail;
import lixco.com.entity.TransportPricingNew;
import lixco.com.entity.Warehouse;
import lixco.com.interfaces.IBatchService;
import lixco.com.interfaces.ICarService;
import lixco.com.interfaces.ICustomerPricingProgramService;
import lixco.com.interfaces.ICustomerPromotionProgramService;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.ICustomerTypesService;
import lixco.com.interfaces.IDeliveryPricingService;
import lixco.com.interfaces.IFreightContractService;
import lixco.com.interfaces.IIECategoriesService;
import lixco.com.interfaces.IOrderDetailService;
import lixco.com.interfaces.IOrderLixService;
import lixco.com.interfaces.IPaymentMethodService;
import lixco.com.interfaces.IPricingProgramDetailService;
import lixco.com.interfaces.IPricingProgramService;
import lixco.com.interfaces.IProcessLogicInvoiceService;
import lixco.com.interfaces.IProductService;
import lixco.com.interfaces.IPromotionOrderDetailService;
import lixco.com.interfaces.IPromotionProgramDetailService;
import lixco.com.interfaces.IPromotionProgramService;
import lixco.com.interfaces.IPromotionalPricingService;
import lixco.com.interfaces.IWarehouseService;
import lixco.com.reqInfo.CustomerPricingProgramReqInfo;
import lixco.com.reqInfo.CustomerPromotionProgramReqInfo;
import lixco.com.reqInfo.OrderDetailReqInfo;
import lixco.com.reqInfo.OrderLixReqInfo;
import lixco.com.reqInfo.PricingProgramDetailReqInfo;
import lixco.com.reqInfo.ProductReqInfo;
import lixco.com.reqInfo.PromotionOrderDetailReqInfo;
import lixco.com.reqInfo.PromotionalPricingReqInfo;
import lixco.com.reqInfo.WrapOrderDetailLixReqInfo;
import lixco.com.reqInfo.WrapOrderLixReqInfo;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class OrderLixBean extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IOrderLixService orderLixService;
	@Inject
	private IProductService productService;
	@Inject
	private ICustomerService customerService;
	@Inject
	private ICustomerTypesService customerTypesService;
	@Inject
	private IPaymentMethodService paymentMethodService;
	@Inject
	private IIECategoriesService iECategoriesService;
	@Inject
	private IWarehouseService warehouseService;
	@Inject
	private IPromotionProgramService promotionProgramService;
	@Inject
	private IPricingProgramService pricingProgramService;
	@Inject
	private ICustomerPromotionProgramService customerPromotionProgramService;
	@Inject 
	private ICustomerPricingProgramService customerPricingProgramService;
	@Inject
	private IPromotionProgramDetailService promotionProgramDetailService;
	@Inject
	private IPricingProgramDetailService pricingProgramDetailService;
	@Inject
	private ICarService carService;
	@Inject
	private IFreightContractService freightContractService;
	@Inject
	private IDeliveryPricingService deliveryPricingService;
	@Inject
	private IOrderDetailService orderDetailService;
	@Inject
	private IPromotionOrderDetailService promotionOrderDetailService;
	@Inject
	private IPromotionalPricingService promotionalPricingService;
	@Inject
	private IBatchService batchService;
	@Inject
	private IProcessLogicInvoiceService processLogicInvoiceService;
	private List<IECategories> listIECategories;
	private List<PaymentMethod> listPaymentMethod;
	private List<Warehouse> listWarehouse;
	private List<CustomerTypes> listCustomerTypes;
	private OrderLix orderLixCrud;
	private TransportPricingNew transportPricingNewCrud;
	private OrderLix orderLixSelect;
	private List<OrderLix> listOrderLix;
	private OrderDetail orderDetailCrud;
	private OrderDetail orderDetailPick;
	private List<PromotionProgramDetail> listPromotionProgramDetailCrud;
	private OrderDetail orderDetailSelect;
	private List<OrderDetail> listOrderDetail;
	private List<PromotionOrderDetail> listPromotionOrderDetail;
	private Date fromDateFCFilter;
	private Date toDateFCFilter;
	private CustomerTypes customerTypesFCFilter;
	private Customer customerFcFilter;
	private String contractNoFCFilter;
	private List<FreightContract> listFreightContractSelect;
	private int pageSizeHDVC;
	private NavigationInfo navigationInfoHDVC;
	private List<Integer> listRowPerPageHDVC;
	/*search orderlix*/
	private Date fromDateSearch;
	private Date toDateSearch;
	private Customer customerSearch;
	private String orderCodeSearch;
	private String voucherCodeSearch;
	private IECategories iECategoriesSearch;
	private String poNoSearch;
	private int deliveredSearch;
	private int statusSearch;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	/*search chi tiết*/
	private Account account;
	private FormatHandler formatHandler;
	private boolean tabOrder;
	/*hóa đơn*/
	private List<WrapOrderDetailLixReqInfo> listWrapOrderDetailLixReqInfo;
	private List<PromotionOrderDetail> listPromotionOrderDetailInvoice;
	
	
	@Override
	protected void initItem() {
		try{
			formatHandler=FormatHandler.getInstance();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			listIECategories=new ArrayList<IECategories>();
			iECategoriesService.selectAll(listIECategories);
			listPaymentMethod=new ArrayList<>();
			paymentMethodService.selectAll(listPaymentMethod);
			listWarehouse=new ArrayList<>();
			warehouseService.selectAll(listWarehouse);
			listCustomerTypes=new ArrayList<>();
			customerTypesService.selectAll(listCustomerTypes);
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			Date currentDate=new Date();
			fromDateSearch=ToolTimeCustomer.getDateMinCustomer(ToolTimeCustomer.getMonthM(currentDate), ToolTimeCustomer.getYearM(currentDate));
			deliveredSearch=-1;
			statusSearch=-1;
			search();
			createdNew();
			tabOrder=true;
			listWrapOrderDetailLixReqInfo=new ArrayList<>();
			listPromotionOrderDetailInvoice=new ArrayList<>();
		}catch(Exception e){
			logger.error("OrderLixBean.initItem:"+e.getMessage(),e);
		}
	}
	public void deleteOrderLix(){
		try{
			
		}catch(Exception e){
			logger.error("OrderLixBean.deleteOrderLix:"+e.getMessage(),e);
		}
		
	}
	public void  tabOrderOnClick(){
			this.tabOrder=true;
	}
	public void  tabInvoiceOnClick(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			this.tabOrder=false;
			listWrapOrderDetailLixReqInfo=new ArrayList<>();
			listPromotionOrderDetailInvoice=new ArrayList<>();
			//kiểm tra hóa đơn có tồn tại không
			if(orderLixCrud !=null && !orderLixCrud.isFlag_up() && orderLixCrud.getId()!=0 ){
				if(orderLixCrud.getStatus()==2 || orderLixCrud.getStatus()==3){
					notify.warning("Đơn hàng đã hoàn thành hoặc đã hủy!");
				}else{
					for(OrderDetail dt:listOrderDetail){
						WrapOrderDetailLixReqInfo t=new WrapOrderDetailLixReqInfo(dt);
						listWrapOrderDetailLixReqInfo.add(t);
						//lấy tồn theo lô hàng
						t.setQuantity_batch(batchService.getQuantityRemaining(t.getOrder_detail().getProduct().getId()));
					}
				}
				
			}else{
				notify.warning("Đơn hàng chưa lưu không tạo hóa đơn được");
			}
		}catch(Exception e){
			logger.error("OrderLixBean.tabInvoiceOnClick:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdateOrderLix(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(orderLixCrud !=null){
				//kiểm tra thông tin có đầy đủ không
				IECategories ie=orderLixCrud.getIe_categories();
				Date ngayDH=orderLixCrud.getOrder_date();
				Warehouse wh=orderLixCrud.getWarehouse();
				Customer customer=orderLixCrud.getCustomer();
				if(ie !=null && ngayDH !=null && wh!=null && customer !=null){
					OrderLixReqInfo t=new OrderLixReqInfo();
					t.setOrder_lix(orderLixCrud);
					if(orderLixCrud.getId()==0){
						if(allowSave(new Date())){
							orderLixCrud.setCreated_by(account.getMember().getName());
							orderLixCrud.setCreated_date(new Date());
							orderLixCrud.setCreated_by_id(account.getMember().getId());
							int chk=orderLixService.insert(t);
							switch (chk) {
								case 0:
									//cập nhật lại flag false
									t.getOrder_lix().setFlag_up(false);
									//check trường hợp copy;
									if(listOrderDetail !=null && listOrderDetail.size() >0){
										for(OrderDetail dt:listOrderDetail){
											OrderDetailReqInfo olf=new OrderDetailReqInfo();
											olf.setOrder_detail(dt);
											dt.setCreated_date(new Date());
											dt.setCreated_by(account.getMember().getName());
											orderDetailService.insert(olf);
										}
										current.executeScript(
												"swaldesigntimer('Thành công!', 'Sao chép thành công!','success',2000);");
									}
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Lưu đơn hàng thành công!','success',2000);");
									listOrderLix.add(0, orderLixCrud.clone());
									break;
								case -2:
									current.executeScript(
											"swaldesigntimer('Cảnh báo', 'Mã đơn hàng đã tồn tại!','warning',2000);");
								break;
								default:
									current.executeScript(
											"swaldesigntimer('Thất bại', 'Lưu đơn hàng thất bại!','warning',2000);");
									break;
							}
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						if(allowUpdate(new Date())){
							orderLixCrud.setLast_modifed_by(account.getMember().getName());
							orderLixCrud.setLast_modifed_date(new Date());
							// check flag nếu flag true (đơn hàng có sự thay đổi chương trình đơn giá hoặc khuyến mãi) thì thực hiện các bước sau 
							if(orderLixCrud.isFlag_up()){
								//cập nhật lại đơn hàng
								int chk=orderLixService.update(t);
								if(chk==0){
									//cập nhật lại flag false
									t.getOrder_lix().setFlag_up(false);
									listOrderLix.set(listOrderLix.indexOf(orderLixCrud),t.getOrder_lix());
									orderLixCrud.setFlag_up(false);
									//cập nhật lại chi tiết đơn hàng 
									if(listOrderDetail !=null){
										for(OrderDetail dt:listOrderDetail){
											OrderDetailReqInfo olf=new OrderDetailReqInfo();
											olf.setOrder_detail(dt);
											//set ngày modified
											dt.setLast_modifed_date(new Date());
											dt.setLast_modifed_by(account.getMember().getName());
											orderDetailService.update(olf);
											listOrderDetail.set(listOrderDetail.indexOf(dt), olf.getOrder_detail());
										}
									}
									//delete tất cả sản phẩm khuyến mãi
									promotionOrderDetailService.deleteAll(t.getOrder_lix().getId());
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
								}else if(chk==-2){
									current.executeScript(
											"swaldesigntimer('Cảnh báo', 'Mã đơn hàng đã tồn tại!','warning',2000);");
								}else{
									current.executeScript(
											"swaldesigntimer('Thất bại', 'Cập nhật đơn hàng thất bại!','warning',2000);");
								}
							}else{
								//bình thường 
								int chk=orderLixService.update(t);
								switch (chk) {
									case 0:
										current.executeScript(
												"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
										listOrderLix.set(listOrderLix.indexOf(t.getOrder_lix()),t.getOrder_lix());
										break;
									case -2:
										current.executeScript(
												"swaldesigntimer('Cảnh báo', 'Mã đơn hàng đã tồn tại!','warning',2000);");
										break;
	
									default:
										current.executeScript(
												"swaldesigntimer('Thất bại', 'Cập nhật đơn hàng thất bại!','warning',2000);");
										break;
								}
							}
							
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
						
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin không đầy đủ, điền đầy đủ thông tin chứa (*)','warning',2500);");
				}
			}
		}catch(Exception e){
			logger.error("OrderLixBean.saveOrUpdateOrderLix:"+e.getMessage(),e);
		}
	}
	public void capNhapThongTinDonGiaKM(){
		try{
			if(orderLixCrud !=null && orderLixCrud.getCustomer() !=null && orderLixCrud.getOrder_date() !=null){
				orderLixCrud.setFlag_up(true);
				  /*{order_date:'',customer_id:0}*/
				  //cập nhật chương trình đơn giá cho đơn hàng
				  JsonObject json=new JsonObject();
				  json.addProperty("order_date", ToolTimeCustomer.convertDateToString(orderLixCrud.getOrder_date(),"dd/MM/yyyy"));
				  json.addProperty("customer_id",orderLixCrud.getCustomer().getId());
				  CustomerPricingProgramReqInfo t=new CustomerPricingProgramReqInfo();
				  customerPricingProgramService.selectForCustomer(JsonParserUtil.getGson().toJson(json),t);
				  /*cập nhật chương trình khuyến mãi cho khách hàng*/
				  CustomerPromotionProgramReqInfo t1=new CustomerPromotionProgramReqInfo();
				  customerPromotionProgramService.selectForCustomer(JsonParserUtil.getGson().toJson(json),t1);
//				  PromotionProgram promotionProgram=t1.getCustomer_promotion_program().getPromotion_program();
				  //nạp chương trình đơn giá
				  if(t.getCustomer_pricing_program() !=null){
					  PricingProgram pricingProgram=t.getCustomer_pricing_program().getPricing_program();
				     orderLixCrud.setPricing_program(pricingProgram);
				     if(listOrderDetail !=null && listOrderDetail.size() >0){
						  //cập nhật đơn giá sản phẩm trong đơn hàng 
						  for(OrderDetail d:listOrderDetail){
							  d.setOrder_lix(orderLixCrud);
							  //cập nhật đơn giá sản phẩm
							  PricingProgramDetailReqInfo dt= new PricingProgramDetailReqInfo();
							  pricingProgramDetailService.findSettingPricingChild(pricingProgram.getId(), d.getProduct().getId(), dt);
							  if(dt.getPricing_program_detail()==null){
								  pricingProgramDetailService.findSettingPricing(pricingProgram.getId(),d.getProduct().getId() , dt);
							  }
							  PricingProgramDetail ppd=dt.getPricing_program_detail();
							  d.setUnit_price(ppd==null ? 0: ppd.getUnit_price());
							  d.setTotal_amount(d.getBox_quantity()*d.getProduct().getSpecification()*d.getUnit_price());
							  d.setPromotion_forms(0);// reset lại hình thức khuyến mãi
						  }
					  }
				  }else{
					  orderLixCrud.setPricing_program(null);
					  if(listOrderDetail !=null){
						  for(OrderDetail d:listOrderDetail){
							  //cập nhật đơn giá sản phẩm
							  d.setUnit_price(0);
							  d.setTotal_amount(0);
							  d.setPromotion_forms(0);// reset lại hình thức khuyến mãi
						  }
					  }
				  }
				  /*nạp chương trình khuyến mãi*/
				  if(t1.getCustomer_promotion_program()!=null){
				    orderLixCrud.setPromotion_program(t1.getCustomer_promotion_program().getPromotion_program());
				  }else{
					  orderLixCrud.setPromotion_program(null);
				  }
				  //cập nhật sản phẩm khuyến mãi trong đơn hàng
				  listPromotionOrderDetail=new ArrayList<>();
			}
		}catch(Exception e){
			logger.error("OrderLixBean.capNhapThongTinDonGiaKM:"+e.getMessage(),e);
		}
	}
	public void customerChange(){
		try{
			 if(orderLixCrud !=null){
				  orderLixCrud.setFlag_up(true);
				  Customer obj=orderLixCrud.getCustomer();
				  /*{order_date:'',customer_id:0}*/
				  //cập nhật chương trình đơn giá cho đơn hàng
				  JsonObject json=new JsonObject();
				  json.addProperty("order_date", ToolTimeCustomer.convertDateToString(orderLixCrud.getOrder_date(),"dd/MM/yyyy"));
				  json.addProperty("customer_id",obj.getId());
				  CustomerPricingProgramReqInfo t=new CustomerPricingProgramReqInfo();
				  customerPricingProgramService.selectForCustomer(JsonParserUtil.getGson().toJson(json),t);
				  /*cập nhật chương trình khuyến mãi cho khách hàng*/
				  CustomerPromotionProgramReqInfo t1=new CustomerPromotionProgramReqInfo();
				  customerPromotionProgramService.selectForCustomer(JsonParserUtil.getGson().toJson(json),t1);
//				  PromotionProgram promotionProgram=t1.getCustomer_promotion_program().getPromotion_program();
				  //tìm địa điểm đơn giá giao hàng
				  JsonObject json1=new JsonObject();
				  json1.addProperty("customer_id", obj.getId());
				  List<DeliveryPricing> list=new ArrayList<>();
				  deliveryPricingService.seletcBy(JsonParserUtil.getGson().toJson(json1), list);
				  //nạp chương trình đơn giá
				  if(t.getCustomer_pricing_program() !=null){
					     PricingProgram pricingProgram=t.getCustomer_pricing_program().getPricing_program();
					     orderLixCrud.setPricing_program(pricingProgram);
					     if(listOrderDetail !=null && listOrderDetail.size() >0){
							  //cập nhật đơn giá sản phẩm trong đơn hàng 
							  for(OrderDetail d:listOrderDetail){
								  d.setOrder_lix(orderLixCrud);
								  //cập nhật đơn giá sản phẩm
								  PricingProgramDetailReqInfo dt= new PricingProgramDetailReqInfo();
								  pricingProgramDetailService.findSettingPricingChild(pricingProgram.getId(), d.getProduct().getId(), dt);
								  if(dt.getPricing_program_detail()==null){
									  pricingProgramDetailService.findSettingPricing(pricingProgram.getId(),d.getProduct().getId() , dt);
								  }
								  PricingProgramDetail ppd=dt.getPricing_program_detail();
								  d.setUnit_price(ppd==null ? 0: ppd.getUnit_price());
								  d.setTotal_amount(d.getBox_quantity()*d.getProduct().getSpecification()*d.getUnit_price());
								  d.setPromotion_forms(0);// reset lại hình thức khuyến mãi
							  }
						  }
				  }else{
					  orderLixCrud.setPricing_program(null);
					  if(listOrderDetail !=null){
						  for(OrderDetail d:listOrderDetail){
							  //cập nhật đơn giá sản phẩm
							  d.setUnit_price(0);
							  d.setTotal_amount(0);
							  d.setPromotion_forms(0);// reset lại hình thức khuyến mãi
						  }
					  }
				  }
				  /*nạp chương trình khuyến mãi*/
				  if(t1.getCustomer_promotion_program()!=null){
				    orderLixCrud.setPromotion_program(t1.getCustomer_promotion_program().getPromotion_program());
				  }else{
					  orderLixCrud.setPromotion_program(null);
				  }
				  // nạp địa điểm giao hàng và đơn giá giao hàng
				  if(list.size() >0){
					  orderLixCrud.setDelivery_pricing(list.get(0));
				  }else{
					  orderLixCrud.setDelivery_pricing(null);
				  }
				  //cập nhật sản phẩm khuyến mãi trong đơn hàng
				  listPromotionOrderDetail=new ArrayList<>();
				 
			  }
		}catch (Exception e) {
			logger.error("OrderLixBean.customerChange:"+e.getMessage(),e);
		}
	}
	public void rowFreightContractClick(SelectEvent event){
		PrimeFaces current=PrimeFaces.current();
		try{
			FreightContract obj = (FreightContract) event.getObject();
			if(orderLixCrud !=null){
				
			}
		}catch(Exception e){
			logger.error("OrderLixBean.rowFreightContractClick:"+e.getMessage(),e);
		}
	}
	public List<Customer> completeCustomer(String text){
		try{
			List<Customer> list=new ArrayList<>();
			customerService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("OrderLixBean.completeCustomer:"+e.getMessage(),e);
		}
		return null;
	}
	public List<Customer> completeCustomerFC(String text){
		try{
			List<Customer> list=new ArrayList<>();
			if(customerTypesFCFilter !=null){
				customerService.complete(formatHandler.converViToEn(text),customerTypesFCFilter, list);
			}else{
				customerService.complete(formatHandler.converViToEn(text), list);
			}
			return list;
		}catch(Exception e){
			logger.error("OrderLixBean.completeCustomerFC:"+e.getMessage(),e);
		}
		return null;
	}
	public List<Product> completeProduct(String text){
		try{
			List<Product> list=new ArrayList<>();
			productService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("OrderLixBean.completeProduct:"+e.getMessage(),e);
		}
		return null;
	}
	public void search(){
		try{
			/*{ order_info:{from_date:'',to_date:'',customer_id:0,order_code:'',voucher_code:'',ie_categories_id:0,po_no:'',delivered:-1}, page:{page_index:0, page_size:0}}*/
//			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listOrderLix=new ArrayList<OrderLix>();
			PagingInfo page=new PagingInfo();
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 :customerSearch.getId());
			jsonInfo.addProperty("order_code", orderCodeSearch);
			jsonInfo.addProperty("voucher_code", voucherCodeSearch);
			jsonInfo.addProperty("ie_categories_id", iECategoriesSearch==null ? 0 :iECategoriesSearch.getId());
			jsonInfo.addProperty("po_no", poNoSearch);
			jsonInfo.addProperty("delivered",deliveredSearch);
			jsonInfo.addProperty("status",statusSearch);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("order_info", jsonInfo);
			json.add("page", jsonPage);
			orderLixService.search(JsonParserUtil.getGson().toJson(json), page, listOrderLix);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		}catch(Exception e){
			logger.error("OrderLixBean.search:"+e.getMessage(),e);
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
			logger.error("OrderLixBean.initRowPerPage:" + e.getMessage(), e);
		}
	}
	private void initRowPerPageHDVC() {
		try {
			listRowPerPageHDVC = new ArrayList<Integer>();
			listRowPerPageHDVC.add(90);
			listRowPerPageHDVC.add(180);
			listRowPerPageHDVC.add(240);
			pageSizeHDVC = listRowPerPageHDVC.get(0);
		} catch (Exception e) {
			logger.error("OrderLixBean.initRowPerPage:" + e.getMessage(), e);
		}
	}
	public void paginatorChange(int currentPage) {
		try {
			/*{ order_info:{from_date:'',to_date:'',customer_id:0,order_code:'',voucher_code:'',ie_categories_id:0,po_no:'',delivered:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listOrderLix = new ArrayList<OrderLix>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 :customerSearch.getId());
			jsonInfo.addProperty("order_code", orderCodeSearch);
			jsonInfo.addProperty("voucher_code", voucherCodeSearch);
			jsonInfo.addProperty("ie_categories_id", iECategoriesSearch==null ? 0 :iECategoriesSearch.getId());
			jsonInfo.addProperty("po_no", poNoSearch);
			jsonInfo.addProperty("delivered",deliveredSearch);
			jsonInfo.addProperty("status",statusSearch);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("order_info", jsonInfo);
			json.add("page", jsonPage);
			orderLixService.search(JsonParserUtil.getGson().toJson(json), page, listOrderLix);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("OrderLixBean.paginatorChange:" + e.getMessage(), e);
		}
	}
	public void showEditOrderLix(){
		try{
			if(orderLixSelect !=null ){
				orderLixCrud=orderLixSelect.clone();
				//show thông tin chi tiết đơn hàng
				listOrderDetail=new ArrayList<>();
				orderDetailService.selectByOrder(orderLixCrud.getId(), listOrderDetail);
				//show thông tin sản phẩm khuyến khuyến mãi của đơn hàng
				listPromotionOrderDetail=new ArrayList<>();
				promotionOrderDetailService.selectByOrder(orderLixCrud.getId(),listPromotionOrderDetail);
				sumOrderLix();
				if(!tabOrder){
					tabInvoiceOnClick();
				}
			}
		}catch(Exception e){
			logger.error("OrderLixBean.showEditOrderLix:" + e.getMessage(), e);
		}
	}
	public List<Car> completeCar(String text){
		try{
			if(text !=null && !"".equals(text)){
				List<Car> list=new ArrayList<>();
				carService.complete(formatHandler.converViToEn(text), list);
				return list;
			}
		}catch(Exception e){
			logger.error("OrderLixBean.completeCar:" + e.getMessage(), e);
		}
		return null;
	}
	public void createdNew(){
		try{
			orderLixCrud=new OrderLix();
			orderLixCrud.setOrder_date(ToolTimeCustomer.getFirstDateOfDay(new  Date()));
			orderLixCrud.setCreated_by(account.getMember().getName());
			listOrderDetail=new ArrayList<>();
			listPromotionOrderDetail=new ArrayList<>();
			listWrapOrderDetailLixReqInfo=new ArrayList<>();
			listPromotionOrderDetailInvoice=new ArrayList<>();
		}catch(Exception e){
			logger.error("OrderLixBean.createNew:" + e.getMessage(), e);
		}
	}
	public void kiemTraSoLuong(WrapOrderDetailLixReqInfo t){
		try{
			OrderDetail d=t.getOrder_detail();
			double slcon=d.getBox_quantity()-d.getRealbox_quantity();
			if(t.getQuantity()>slcon){
				t.setQuantity(0);
			}
		}catch (Exception e) {
			logger.error("OrderLixBean.kiemTraSoLuong:" + e.getMessage(), e);
		}
	}
	public void createdInvoide(){
		PrimeFaces current=PrimeFaces.current();
		try{
			//chuẩn bị dữ liệu
			if(listWrapOrderDetailLixReqInfo !=null && listWrapOrderDetailLixReqInfo.size()>0){
				WrapOrderLixReqInfo t=new WrapOrderLixReqInfo();
				t.setMember_id(account.getMember().getId());
				t.setMember_name(account.getMember().getName());
				t.setOrder_lix(orderLixCrud.clone());
				List<WrapOrderDetailLixReqInfo> list=new ArrayList<>();
				t.setList_wrap_order_detail(list);
				t.setList_promotion_order_detail(listPromotionOrderDetailInvoice);
				for(WrapOrderDetailLixReqInfo w:listWrapOrderDetailLixReqInfo){
					if(w.getQuantity() >0){
					   list.add(w);
					}
				}
				StringBuilder messages=new StringBuilder();
				WrapOrderLixReqInfo cloned=t.clone();
				int ck=processLogicInvoiceService.createInvoiceByOrderLix(cloned, messages);
				if(ck==0){
					current.executeScript(
							"swaldesigntimer('Thành công!', 'Sao chép thành công!','success',2000);");
				}else{
					current.executeScript(
							"swaldesigntimer('Thất bại!', '"+messages.toString()+"!','warning',2000);");
				}
				//refesh lại order;
				OrderLixReqInfo orderLixReqInfo=new OrderLixReqInfo();
				orderLixService.selectById(cloned.getOrder_lix().getId(),orderLixReqInfo);
				listOrderLix.set(listOrderLix.indexOf(orderLixReqInfo.getOrder_lix()), orderLixReqInfo.getOrder_lix());
				orderLixCrud=orderLixReqInfo.getOrder_lix();
				//load lại detail orderlix;
				listOrderDetail=new ArrayList<>();
				orderDetailService.selectByOrder(orderLixCrud.getId(), listOrderDetail);
				//show thông tin sản phẩm khuyến khuyến mãi của đơn hàng
				listPromotionOrderDetail=new ArrayList<>();
				promotionOrderDetailService.selectByOrder(orderLixCrud.getId(),listPromotionOrderDetail);
				sumOrderLix();
				if(!tabOrder){
					tabInvoiceOnClick();
				}
				
				
				
			}
		}catch(Exception e){
			logger.error("OrderLixBean.kiemTraSoLuong:" + e.getMessage(), e);
		}
	}
	public void destroyOrder(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(orderLixCrud!=null && orderLixCrud.getId()!=0){
				orderLixCrud.setStatus(3);// kết thúc hóa đơn
				OrderLixReqInfo t=new OrderLixReqInfo(orderLixCrud);
				if(orderLixService.update(t)==0){
					notify.success("Kết thúc hóa đơn thành công!");
					listOrderLix.set(listOrderLix.indexOf(orderLixCrud),t.getOrder_lix());
				}else{
					notify.success("Thất bại!");
				}
			}
		}catch(Exception e){
			logger.error("OrderLixBean.destroyOrder:" + e.getMessage(), e);
		}
	}
	public void caculatePromotionInvoice(){
		try{
			listPromotionOrderDetailInvoice=new ArrayList<>();
			if(listWrapOrderDetailLixReqInfo!=null){
				for(WrapOrderDetailLixReqInfo t:listWrapOrderDetailLixReqInfo){
					if (t.getQuantity() > 0) {
						// tính khuyến mãi
						JsonObject js = new JsonObject();
						js.addProperty("product_id", t.getOrder_detail().getProduct().getId());
						js.addProperty("promotion_program_id", orderLixCrud.getPromotion_program().getId());
						js.addProperty("promotion_form", t.getOrder_detail().getPromotion_forms());
						List<PromotionProgramDetail> list = new ArrayList<>();
						promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(js), list);
						lv1: for (PromotionProgramDetail ppd : list) {
							PromotionOrderDetail pod = new PromotionOrderDetail();
							pod.setOrder_detail(t.getOrder_detail());
							Product pr=ppd.getPromotion_product();
							pod.setProduct(pr);
							// tim nạp đơn giá sản phẩm khuyến mãi
							PromotionalPricingReqInfo ppr = new PromotionalPricingReqInfo();
							JsonObject json = new JsonObject();
							json.addProperty("order_date", ToolTimeCustomer.convertDateToString(
									t.getOrder_detail().getOrder_lix().getOrder_date(), "dd/MM/yyyy"));
							json.addProperty("product_id", ppd.getPromotion_product().getId());
							promotionalPricingService.findSettingPromotionalPricing(JsonParserUtil.getGson().toJson(json), ppr);
							double quantity=BigDecimal.valueOf(t.getQuantity()).multiply(BigDecimal.valueOf(ppd.getPromotion_quantity())).divide(BigDecimal.valueOf(ppd.getBox_quatity())).doubleValue();
							pod.setQuantity(quantity);
							if (ppr.getPromotional_pricing() != null) {
								pod.setUnit_price(ppr.getPromotional_pricing().getUnit_price());
								double totalAmount=BigDecimal.valueOf(pod.getUnit_price()).multiply(BigDecimal.valueOf(pod.getQuantity())).doubleValue();
								pod.setTotal_amount(totalAmount);
							}
							for (PromotionOrderDetail p : listPromotionOrderDetailInvoice) {
								if (p.getProduct().equals(pod.getProduct())) {
									p.setQuantity(Double.sum(p.getQuantity(), pod.getQuantity()));
									continue lv1;
								}
							}
							listPromotionOrderDetailInvoice.add(pod);
						}
					}
				}
			}
		}catch(Exception e){
			
		}
	}
	public void showEditOrderDetail(){
		PrimeFaces current = PrimeFaces.current();
		try {
			if (orderDetailSelect != null) {
				if(!orderLixCrud.isFlag_up()){
					orderDetailCrud = orderDetailSelect;
					orderDetailCrud.setOrder_lix(orderLixCrud);
					// load danh sách sản phẩm chương trình khuyến mãi
					listPromotionProgramDetailCrud=new ArrayList<>();
					if (orderLixCrud.getPromotion_program() != null) {
						// load danh sách sản phẩm chương trình khuyến mãi
						/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
						JsonObject json = new JsonObject();
						json.addProperty("product_id", orderDetailCrud.getProduct().getId());
						json.addProperty("promotion_program_id", orderLixCrud.getPromotion_program().getId());
						promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(json),
								listPromotionProgramDetailCrud);
					}
					current.executeScript("PF('dlgc1').show();");
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Đơn hàng chưa được lưu!','warning',2000);");
				}
			}
		} catch (Exception e) {
			logger.error("OrderLixBean.showEditOrderDetail:" + e.getMessage(), e);
		}
	}
	public void showDialogPickHDVC(){
		PrimeFaces current=PrimeFaces.current();
		try{
			fromDateFCFilter=ToolTimeCustomer.getDateMinCustomer(ToolTimeCustomer.getMonthCurrent(), ToolTimeCustomer.getYearCurrent());
			toDateFCFilter=ToolTimeCustomer.getDateMaxCustomer(ToolTimeCustomer.getMonthCurrent(), ToolTimeCustomer.getYearCurrent());
			contractNoFCFilter=null;
			customerFcFilter=null;
			customerTypesFCFilter=null;
			navigationInfoHDVC = new NavigationInfo();
			navigationInfoHDVC.setCurrentPage(1);
			initRowPerPageHDVC();
			navigationInfoHDVC.setLimit(pageSizeHDVC);
			navigationInfoHDVC.setMaxIndices(5);
			current.executeScript("PF('dlg2').show();");
		}catch(Exception e){
			logger.error("OrderLixBean.showDialogPickHDVC:" + e.getMessage(), e);
		}
	}
	public void searchHDVC(){
		try{
			/*{freight_contract_info:{contract_no:'',from_date:'',to_date:'',customer_id:0,car_id:0,payment_method_id:0,payment:-1}, page:{page_index:0, page_size:0}}*/
			initRowPerPageHDVC();
			navigationInfoHDVC.setLimit(pageSizeHDVC);
			navigationInfoHDVC.setMaxIndices(5);
			listFreightContractSelect=new ArrayList<>();
			PagingInfo page=new PagingInfo();
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("contract_no", contractNoFCFilter);
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateFCFilter, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateFCFilter, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerFcFilter ==null ? 0 :customerFcFilter.getId());
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",1);
			jsonPage.addProperty("page_size", pageSizeHDVC);
			JsonObject json=new JsonObject();
			json.add("freight_contract_info", jsonInfo);
			json.add("page", jsonPage);
			freightContractService.search(JsonParserUtil.getGson().toJson(json), page, listFreightContractSelect);
			navigationInfoHDVC.setTotalRecords((int) page.getTotalRow());
			navigationInfoHDVC.setCurrentPage(1);
		}catch(Exception e){
			logger.error("OrderLixBean.searchHDVC:" + e.getMessage(), e);
		}
	}
	public void paginatorChangeHDVC(int currentPage) {
		try {
			/*{freight_contract_info:{contract_no:'',from_date:'',to_date:'',customer_id:0,car_id:0,payment_method_id:0,payment:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfoHDVC.setLimit(pageSizeHDVC);
			navigationInfoHDVC.setMaxIndices(5);
			listFreightContractSelect = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("contract_no", contractNoFCFilter);
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateFCFilter, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateFCFilter, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerFcFilter ==null ? 0 :customerFcFilter.getId());
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",currentPage);
			jsonPage.addProperty("page_size", pageSizeHDVC);
			JsonObject json=new JsonObject();
			json.add("freight_contract_info", jsonInfo);
			json.add("page", jsonPage);
			freightContractService.search(JsonParserUtil.getGson().toJson(json), page, listFreightContractSelect);
			navigationInfoHDVC.setTotalRecords((int) page.getTotalRow());
			navigationInfoHDVC.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("OrderLixBean.paginatorChangeHDVC:" + e.getMessage(), e);
		}
	}
	public void closeDialogPickHDVC(){
		
		try{
			listFreightContractSelect=null;
			contractNoFCFilter=null;
			customerFcFilter=null;
			customerTypesFCFilter=null;
			listRowPerPageHDVC=null;
			navigationInfoHDVC=null;
		}catch(Exception e){
			logger.error("OrderLixBean.closeDialogPickHDVC:" + e.getMessage(), e);
		}
	}
	public void showDialogOrderDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(orderLixCrud !=null && orderLixCrud.getId() !=0){
				if(!orderLixCrud.isFlag_up()){
					orderDetailCrud=new OrderDetail();
					orderDetailCrud.setOrder_lix(orderLixCrud);
					listPromotionProgramDetailCrud=null;
					current.executeScript("PF('dlgc1').show();");
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Đơn hàng chưa được lưu!','warning',2000);");
				}
			}
		}catch(Exception e){
			logger.error("OrderLixBean.showDialogOrderDetail:" + e.getMessage(), e);
		}
	}
	public void showDialogPromotionPick(OrderDetail item){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		PrimeFaces current=PrimeFaces.current();
		try{
			if(item.getOrder_lix().getPromotion_program() !=null ){
				if(!orderLixCrud.isFlag_up()){
					orderDetailPick=item;
					listPromotionProgramDetailCrud=new ArrayList<>();
					//load danh sách sản phẩm chương trình khuyến mãi
					/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
					JsonObject json=new JsonObject();
					json.addProperty("product_id", item.getProduct().getId());
					json.addProperty("promotion_program_id", orderLixCrud.getPromotion_program().getId());
					promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(json),listPromotionProgramDetailCrud );
					current.executeScript("PF('dlgc2').show();");
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Đơn hàng chưa được lưu!','warning',2000);");
				}
			}else{
				notify.message("Chưa cài chương trình khuyến mãi");
			}
		}catch(Exception e){
			logger.error("OrderLixBean.showDialogPromotionPick:" + e.getMessage(), e);
		}
	}
	public void pickPromotion(PromotionProgramDetail item){
		PrimeFaces current=PrimeFaces.current();
		Notify notify=new Notify(FacesContext.getCurrentInstance());
		try{
			if(orderDetailPick !=null){
				if(allowUpdate(new Date())){
					orderDetailPick.setPromotion_forms(item.getPromotion_form());
					if(orderDetailService.update(new OrderDetailReqInfo(orderDetailPick))==0){
						/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
						//xóa sản phẩm khuyến mãi cũ
						int chk=promotionOrderDetailService.deleteBy(orderDetailPick.getId());
						if(chk>=0){
							JsonObject js=new JsonObject();
							js.addProperty("product_id", item.getProduct().getId());
							js.addProperty("promotion_program_id", item.getPromotion_program().getId());
							js.addProperty("promotion_form", item.getPromotion_form());
							List<PromotionProgramDetail> list=new ArrayList<>();
							promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(js), list);
							for(PromotionProgramDetail ppd:list){
								PromotionOrderDetail pod=new PromotionOrderDetail();
								pod.setOrder_detail(orderDetailPick);
								pod.setProduct(ppd.getPromotion_product());
								//tim nạp đơn giá sản phẩm khuyến mãi
								PromotionalPricingReqInfo ppr=new PromotionalPricingReqInfo();
								JsonObject json=new JsonObject();
								json.addProperty("order_date",ToolTimeCustomer.convertDateToString(orderDetailPick.getOrder_lix().getOrder_date(), "dd/MM/yyyy"));
								json.addProperty("product_id",ppd.getPromotion_product().getId());
								promotionalPricingService.findSettingPromotionalPricing(JsonParserUtil.getGson().toJson(json), ppr);
								double quantity=BigDecimal.valueOf(orderDetailPick.getBox_quantity()).multiply(BigDecimal.valueOf(ppd.getPromotion_quantity())).subtract(BigDecimal.valueOf(ppd.getBox_quatity())).doubleValue();
								pod.setQuantity(quantity);
								if(ppr.getPromotional_pricing() !=null){
									pod.setUnit_price(ppr.getPromotional_pricing().getUnit_price());
									double totalAmount=BigDecimal.valueOf(pod.getUnit_price()).multiply(BigDecimal.valueOf(pod.getQuantity())).doubleValue();
									pod.setTotal_amount(totalAmount);
								}
								pod.setCreated_date(new Date());
								pod.setCreated_by(account.getMember().getName());
								promotionOrderDetailService.insert(new PromotionOrderDetailReqInfo(pod));
							}
							//load lai sản phẩm khuyến mãi đơn hàng
							listPromotionOrderDetail=new ArrayList<>();
							promotionOrderDetailService.selectByOrder(orderDetailPick.getOrder_lix().getId(), listPromotionOrderDetail);
							notify.success();
						}
					}else{
						current.executeScript(
								"swaldesigntimer('Cảnh báo!', 'Không cập nhật được hình thức khuyến mãi!','error',2000);");
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
				}
			}
		}catch(Exception e){
			logger.error("OrderLixBean.pickPromotion:" + e.getMessage(), e);
		}
	}
	public void loadInfo(){
		try{
			listPromotionProgramDetailCrud=new ArrayList<>();
			if(orderLixCrud!=null && orderDetailCrud !=null && orderDetailCrud.getProduct() !=null){
				if(orderLixCrud.getPromotion_program() !=null){
					//load danh sách sản phẩm chương trình khuyến mãi
					/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
					JsonObject json=new JsonObject();
					json.addProperty("product_id", orderDetailCrud.getProduct().getId());
					json.addProperty("promotion_program_id", orderLixCrud.getPromotion_program().getId());
					promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(json),listPromotionProgramDetailCrud );
				}
				if(orderLixCrud.getPricing_program() !=null){
					//load danh sách sản phẩm ctkm
					PricingProgramDetailReqInfo t=new PricingProgramDetailReqInfo();
					pricingProgramDetailService.findSettingPricingChild(orderLixCrud.getPricing_program().getId(), orderDetailCrud.getProduct().getId(), t);
					if(t.getPricing_program_detail()==null){
						pricingProgramDetailService.findSettingPricing(orderLixCrud.getPricing_program().getId(), orderDetailCrud.getProduct().getId(), t);
					}
					if(t.getPricing_program_detail() !=null){
						orderDetailCrud.setUnit_price(t.getPricing_program_detail().getUnit_price());
					}else{
						orderDetailCrud.setUnit_price(0);
					}
				}
				ProductReqInfo pr=new ProductReqInfo();
				productService.selectById(orderDetailCrud.getProduct().getId(),pr);
				double quantity=BigDecimal.valueOf(orderDetailCrud.getRealbox_quantity()).multiply(BigDecimal.valueOf(pr.getProduct().getSpecification())).doubleValue();
				orderDetailCrud.setQuantity(quantity);
			}
		}catch(Exception e){
			logger.error("OrderLixBean.loadPromotionProgramDetailCrud:" + e.getMessage(), e);
		}
	}
	public void selectPromotionForm(PromotionProgramDetail item){
		try{
			if(orderDetailCrud !=null){
				orderDetailCrud.setPromotion_forms(item.getPromotion_form());
			}
		}catch(Exception e){
			logger.error("OrderLixBean.selectPromotionForm:" + e.getMessage(), e);
		}
	}

	public void changeBoxOrderDetail(){
		try{
			if(orderDetailCrud !=null){
//				orderDetailCrud.setRealbox_quantity(orderDetailCrud.getBox_quantity());
				if(orderDetailCrud.getProduct() !=null){
					ProductReqInfo pr=new ProductReqInfo();
					productService.selectById(orderDetailCrud.getProduct().getId(),pr);
					double quantity=BigDecimal.valueOf(orderDetailCrud.getBox_quantity()).multiply(BigDecimal.valueOf(pr.getProduct().getSpecification())).doubleValue();
					orderDetailCrud.setQuantity(quantity);
				}else{
					orderDetailCrud.setQuantity(0);
				}
			}
		}catch(Exception e){
			logger.error("OrderLixBean.changeBoxOrderDetail:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdateOrderDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(orderDetailCrud !=null){
				//kiểm tra thông tin có đầy đủ không
				Product product=orderDetailCrud.getProduct();
				double box=orderDetailCrud.getBox_quantity();
				OrderLix orderLix=orderDetailCrud.getOrder_lix();
				if(product!=null && box !=0){
					OrderDetailReqInfo t=new OrderDetailReqInfo();
					t.setOrder_detail(orderDetailCrud);
					if(orderDetailCrud.getId()==0){
						if(allowSave(new Date())){
							orderDetailCrud.setCreated_by(account.getMember().getName());
							orderDetailCrud.setCreated_date(new Date());
							
							int chk=orderDetailService.insert(t);
							switch (chk) {
							case 0:
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
								listOrderDetail.add(0,orderDetailCrud);
								
								//tính sản phẩm khuyến mãi.
								if(orderDetailCrud.getPromotion_forms() != 0){
									/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
									JsonObject js=new JsonObject();
									js.addProperty("product_id", product.getId());
									js.addProperty("promotion_program_id", orderLix.getPromotion_program().getId());
									js.addProperty("promotion_form", orderDetailCrud.getPromotion_forms());
									List<PromotionProgramDetail> list=new ArrayList<>();
									promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(js), list);
									for(PromotionProgramDetail ppd:list){
										PromotionOrderDetail pod=new PromotionOrderDetail();
										pod.setOrder_detail(orderDetailCrud);
										pod.setProduct(ppd.getPromotion_product());
										//tim nạp đơn giá sản phẩm khuyến mãi
										double quantity=BigDecimal.valueOf(box).multiply(BigDecimal.valueOf(ppd.getPromotion_quantity())).divide(BigDecimal.valueOf(ppd.getBox_quatity())).doubleValue();
										pod.setQuantity(quantity);
										pod.setCreated_date(new Date());
										pod.setCreated_by(account.getMember().getName());
										promotionOrderDetailService.insert(new PromotionOrderDetailReqInfo(pod));
									}
									//load lai sản phẩm khuyến mãi đơn hàng
									listPromotionOrderDetail=new ArrayList<>();
									promotionOrderDetailService.selectByOrder(orderLix.getId(), listPromotionOrderDetail);
								}
								createNewOrderDetail();
								break;
							case -2:
								current.executeScript(
										"swaldesigntimer('Cảnh báo!','Sản phẩm đã tồn tại trong đơn hàng!','warning',2000);");
								break;
							default:
								current.executeScript(
										"swaldesigntimer('Thất bại!', 'Lưu thất bại!','error',2000);");
								break;
							}
							
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}else{
						if(allowUpdate(new Date())){
							orderDetailCrud.setLast_modifed_by(account.getMember().getName());
							orderDetailCrud.setLast_modifed_date(new Date());
							OrderDetailReqInfo pre=new OrderDetailReqInfo();
							orderDetailService.selectById(orderDetailCrud.getId(), pre);
							int chk=orderDetailService.update(t);
							switch (chk) {
								case 0:
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									OrderDetail orderDetailPre=pre.getOrder_detail();
									//delete  sản phẩm khuyến mãi
									promotionOrderDetailService.deleteBy(orderDetailPre.getId());
									//cập nhật sản phẩm khuyến mãi mới
									//tính sản phẩm khuyến mãi.
									if(orderDetailCrud.getPromotion_forms() != 0){
										/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
										JsonObject js=new JsonObject();
										js.addProperty("product_id", product.getId());
										js.addProperty("promotion_program_id", orderLix.getPromotion_program().getId());
										js.addProperty("promotion_form", orderDetailCrud.getPromotion_forms());
										List<PromotionProgramDetail> list=new ArrayList<>();
										promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(js), list);
										for(PromotionProgramDetail ppd:list){
											PromotionOrderDetail pod=new PromotionOrderDetail();
											pod.setOrder_detail(orderDetailPre);
											pod.setProduct(ppd.getPromotion_product());
											//tim nạp đơn giá sản phẩm khuyến mãi
											double quantity=BigDecimal.valueOf(box).multiply(BigDecimal.valueOf(ppd.getPromotion_quantity())).divide(BigDecimal.valueOf(ppd.getBox_quatity())).doubleValue();
											pod.setQuantity(quantity);
											pod.setCreated_date(new Date());
											pod.setCreated_by(account.getMember().getName());
											promotionOrderDetailService.insert(new PromotionOrderDetailReqInfo(pod));
										}
										//load lai sản phẩm khuyến mãi đơn hàng
										listPromotionOrderDetail=new ArrayList<>();
										promotionOrderDetailService.selectByOrder(orderLix.getId(), listPromotionOrderDetail);
									}
									listOrderDetail.set(listOrderDetail.indexOf(orderDetailCrud),t.getOrder_detail());
									break;
								case -2:
									current.executeScript(
											"swaldesigntimer('Cảnh báo!','Sản phẩm đã tồn tại trong đơn hàng!','warning',2000);");
									break;
								default:
									current.executeScript(
											"swaldesigntimer('Thất bại!', 'Cập nhật thất bại!','error',2000);");
									break;
							}
							
						}else{
							current.executeScript(
									"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
						}
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Thông tin chi tiết đơn hàng không đầy đủ, điền đủ thông tin chứa(*)','warning',2000);");
				}
				sumOrderLix();
			}
		}catch(Exception e){
			logger.error("OrderLixBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}
	public void createNewOrderDetail(){
		try{
			
			orderDetailCrud=new OrderDetail();
			orderDetailCrud.setOrder_lix(orderLixCrud);
			listPromotionProgramDetailCrud=null;
			
		}catch(Exception e){
			logger.error("OrderLixBean.createNewOrderDetail:" + e.getMessage(), e);
		}
	}
	public void deleteOrderLixDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(orderDetailSelect !=null){
				if(allowDelete(new Date())){
					if(orderDetailService.deleteById(orderDetailSelect.getId())>0){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						
						//tính lại sản phẩm khuyến mãi
						int chk=promotionOrderDetailService.deleteBy(orderDetailSelect.getId());
						listPromotionOrderDetail=new ArrayList<>();
						promotionOrderDetailService.selectByOrder(orderDetailSelect.getOrder_lix().getId(), listPromotionOrderDetail);
						listOrderDetail.remove(orderDetailSelect);
						sumOrderLix();
					}else{
						current.executeScript(
								"swaldesigntimer('Thất bại!', 'Không được xóa!','error',2000);");
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
				}
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo', 'Chưa chọn một dòng chi tiết để xóa!','warning',2000);");
			}
		}catch (Exception e) {
			logger.error("OrderLixBean.deleteOrderLixDetail:" + e.getMessage(), e);
		}
	}
	public void calPromotionProduct(){
		try{
			if(listOrderDetail !=null && listOrderDetail.size()>0){
				for(OrderDetail d:listOrderDetail){
					
				}
			}
		}catch(Exception e){
			logger.error("OrderLixBean.calSanPhamKhuyenMai:" + e.getMessage(), e);
		}
	}
	public double sumOrderLix(){
		try{
			if(listOrderDetail !=null && listOrderDetail.size()>0){
				double totalevent = listOrderDetail.stream().mapToDouble(f -> f.getQuantity()*f.getUnit_price()).sum();
				return totalevent;
			}
		}catch(Exception e){
			logger.error("OrderLixBean.calSanPhamKhuyenMai:" + e.getMessage(), e);
		}
		return 0;
	}
	public void copyOrderLix(){
		try{
			if(orderLixCrud !=null && orderLixCrud.getId()!=0 && !orderLixCrud.isFlag_up()){
				orderLixCrud.setId(0);
				orderLixCrud.setOrder_code(null);
				orderLixCrud.setVoucher_code(null);
				for(OrderDetail d:listOrderDetail){
					d.setId(0);
					// reference chó nó tự động inject Id
					d.setOrder_lix(orderLixCrud);
					d.setPromotion_forms(0);
					d.setNote(null);
					d.setLast_modifed_by(null);
					d.setLast_modifed_date(null);
				}
				listPromotionOrderDetail=new ArrayList<>();
			}
		}catch(Exception e){
			logger.error("OrderLixBean.copyOrderLix:" + e.getMessage(), e);
		}
	}
	public void exportPDF(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(orderLixCrud !=null && orderLixCrud.getId() >0){
				String reportPath ="";
				String reportPath2 ="";
				String reportPath3 ="";
				if(listOrderDetail !=null && listOrderDetail.size()>10){
					 reportPath = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/reports/donhang.jasper");
					 reportPath2 = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/reports/donhang2.jasper");
					 reportPath3 = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/reports/donhang3.jasper");
				}else{
					reportPath = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/reports/donhanghz.jasper");
					 reportPath2 = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/reports/donhanghz2.jasper");
					 
				}
				Map<String, Object> importParam = new HashMap<String, Object>();
				importParam.put("voucher_code",orderLixCrud.getVoucher_code());
				importParam.put("order_date",ToolTimeCustomer.convertDateToString(orderLixCrud.getOrder_date(),"dd/MM/yyyy"));
				importParam.put("license_plate", orderLixCrud.getCar() == null ? "" :orderLixCrud.getCar().getLicense_plate());
				String customerName=orderLixCrud.getCustomer().getCustomer_name();
				//slipt 40 ký tự
				String line1="";
				String line2="";
				if(customerName.length() >40){
					String customer40=customerName.substring(0,40);
					char last= customerName.charAt(40);
					if(last!=' '){
						//tìm last space trong đoạn 40
						int space=customer40.lastIndexOf(' ');
						line1=customer40.substring(0,space);
						line2=customerName.substring(space);
					}else{
						line1=customer40;
						line2=customerName.substring(40);
					}
				}else{
					line1=customerName;
				}
				importParam.put("customer_name1", line1);
				importParam.put("customer_name2", line2);
				importParam.put("customer_code",orderLixCrud.getCustomer().getCustomer_code());
				importParam.put("note", orderLixCrud.getNote());
				importParam.put("listOrderDetailN", listOrderDetail);
				importParam.put("listPromotionOrderDetailN", listPromotionOrderDetail);
 				JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
				byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
				List<byte[]> listByte=new ArrayList<>();
				listByte.add(data);
				JasperPrint jasperPrint2 = JasperFillManager.fillReport(reportPath2, importParam, new JREmptyDataSource());
				byte[] data2 = JasperExportManager.exportReportToPdf(jasperPrint2);
				listByte.add(data2);
				if(!"".equals(reportPath3)){
					JasperPrint jasperPrint3 = JasperFillManager.fillReport(reportPath3, importParam, new JREmptyDataSource());
					byte[] data3 = JasperExportManager.exportReportToPdf(jasperPrint3);
					listByte.add(data3);
				}
				byte[] lastData=mergePDF(listByte);
				String ba = Base64.getEncoder().encodeToString(lastData);
				current.executeScript("utility.printPDF('" + ba + "')");
			}
		}catch (Exception e) {
			logger.error("OrderLixBean.exportPDF:" + e.getMessage(), e);
		}
	}

	private  byte[] mergePDF(List<byte[]> pdfFilesAsByteArray) throws DocumentException, IOException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Document document = null;
		PdfCopy writer = null;
		for (byte[] pdfByteArray : pdfFilesAsByteArray) {

			try {
				PdfReader reader = new PdfReader(pdfByteArray);
				int numberOfPages = reader.getNumberOfPages();

				if (document == null) {
					document = new Document(reader.getPageSizeWithRotation(1));
					writer = new PdfCopy(document, outStream); // new
					document.open();
				}
				PdfImportedPage page;
				for (int i = 0; i < numberOfPages;) {
					++i;
					page = writer.getImportedPage(reader, i);
					writer.addPage(page);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		document.close();
		outStream.close();
		return outStream.toByteArray();

	}
	@Override
	protected Logger getLogger() {
		return logger;
	}

	public OrderLix getOrderLixCrud() {
		return orderLixCrud;
	}

	public void setOrderLixCrud(OrderLix orderLixCrud) {
		this.orderLixCrud = orderLixCrud;
	}

	public OrderLix getOrderLixSelect() {
		return orderLixSelect;
	}

	public void setOrderLixSelect(OrderLix orderLixSelect) {
		this.orderLixSelect = orderLixSelect;
	}

	public List<OrderLix> getListOrderLix() {
		return listOrderLix;
	}

	public void setListOrderLix(List<OrderLix> listOrderLix) {
		this.listOrderLix = listOrderLix;
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

	public String getOrderCodeSearch() {
		return orderCodeSearch;
	}

	public void setOrderCodeSearch(String orderCodeSearch) {
		this.orderCodeSearch = orderCodeSearch;
	}

	public String getVoucherCodeSearch() {
		return voucherCodeSearch;
	}

	public void setVoucherCodeSearch(String voucherCodeSearch) {
		this.voucherCodeSearch = voucherCodeSearch;
	}

	public IECategories getiECategoriesSearch() {
		return iECategoriesSearch;
	}

	public void setiECategoriesSearch(IECategories iECategoriesSearch) {
		this.iECategoriesSearch = iECategoriesSearch;
	}

	public String getPoNoSearch() {
		return poNoSearch;
	}

	public void setPoNoSearch(String poNoSearch) {
		this.poNoSearch = poNoSearch;
	}

	public int getDeliveredSearch() {
		return deliveredSearch;
	}

	public void setDeliveredSearch(int deliveredSearch) {
		this.deliveredSearch = deliveredSearch;
	}
	public List<IECategories> getListIECategories() {
		return listIECategories;
	}
	public void setListIECategories(List<IECategories> listIECategories) {
		this.listIECategories = listIECategories;
	}
	public List<PaymentMethod> getListPaymentMethod() {
		return listPaymentMethod;
	}
	public void setListPaymentMethod(List<PaymentMethod> listPaymentMethod) {
		this.listPaymentMethod = listPaymentMethod;
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
	public List<Warehouse> getListWarehouse() {
		return listWarehouse;
	}
	public void setListWarehouse(List<Warehouse> listWarehouse) {
		this.listWarehouse = listWarehouse;
	}
	public List<CustomerTypes> getListCustomerTypes() {
		return listCustomerTypes;
	}
	public void setListCustomerTypes(List<CustomerTypes> listCustomerTypes) {
		this.listCustomerTypes = listCustomerTypes;
	}
	public FormatHandler getFormatHandler() {
		return formatHandler;
	}
	public void setFormatHandler(FormatHandler formatHandler) {
		this.formatHandler = formatHandler;
	}
	public TransportPricingNew getTransportPricingNewCrud() {
		return transportPricingNewCrud;
	}
	public void setTransportPricingNewCrud(TransportPricingNew transportPricingNewCrud) {
		this.transportPricingNewCrud = transportPricingNewCrud;
	}
	public OrderDetail getOrderDetailCrud() {
		return orderDetailCrud;
	}
	public void setOrderDetailCrud(OrderDetail orderDetailCrud) {
		this.orderDetailCrud = orderDetailCrud;
	}
	public OrderDetail getOrderDetailSelect() {
		return orderDetailSelect;
	}
	public void setOrderDetailSelect(OrderDetail orderDetailSelect) {
		this.orderDetailSelect = orderDetailSelect;
	}
	public List<OrderDetail> getListOrderDetail() {
		return listOrderDetail;
	}
	public void setListOrderDetail(List<OrderDetail> listOrderDetail) {
		this.listOrderDetail = listOrderDetail;
	}
	public List<PromotionOrderDetail> getListPromotionOrderDetail() {
		return listPromotionOrderDetail;
	}
	public void setListPromotionOrderDetail(List<PromotionOrderDetail> listPromotionOrderDetail) {
		this.listPromotionOrderDetail = listPromotionOrderDetail;
	}
	public List<FreightContract> getListFreightContractSelect() {
		return listFreightContractSelect;
	}
	public void setListFreightContractSelect(List<FreightContract> listFreightContractSelect) {
		this.listFreightContractSelect = listFreightContractSelect;
	}
	public Date getFromDateFCFilter() {
		return fromDateFCFilter;
	}
	public void setFromDateFCFilter(Date fromDateFCFilter) {
		this.fromDateFCFilter = fromDateFCFilter;
	}
	public Date getToDateFCFilter() {
		return toDateFCFilter;
	}
	public void setToDateFCFilter(Date toDateFCFilter) {
		this.toDateFCFilter = toDateFCFilter;
	}
	public Customer getCustomerFcFilter() {
		return customerFcFilter;
	}
	public void setCustomerFcFilter(Customer customerFcFilter) {
		this.customerFcFilter = customerFcFilter;
	}
	public String getContractNoFCFilter() {
		return contractNoFCFilter;
	}
	public void setContractNoFCFilter(String contractNoFCFilter) {
		this.contractNoFCFilter = contractNoFCFilter;
	}
	public CustomerTypes getCustomerTypesFCFilter() {
		return customerTypesFCFilter;
	}
	public void setCustomerTypesFCFilter(CustomerTypes customerTypesFCFilter) {
		this.customerTypesFCFilter = customerTypesFCFilter;
	}
	public int getPageSizeHDVC() {
		return pageSizeHDVC;
	}
	public void setPageSizeHDVC(int pageSizeHDVC) {
		this.pageSizeHDVC = pageSizeHDVC;
	}
	public NavigationInfo getNavigationInfoHDVC() {
		return navigationInfoHDVC;
	}
	public void setNavigationInfoHDVC(NavigationInfo navigationInfoHDVC) {
		this.navigationInfoHDVC = navigationInfoHDVC;
	}
	public List<Integer> getListRowPerPageHDVC() {
		return listRowPerPageHDVC;
	}
	public void setListRowPerPageHDVC(List<Integer> listRowPerPageHDVC) {
		this.listRowPerPageHDVC = listRowPerPageHDVC;
	}
	public List<PromotionProgramDetail> getListPromotionProgramDetailCrud() {
		return listPromotionProgramDetailCrud;
	}
	public void setListPromotionProgramDetailCrud(List<PromotionProgramDetail> listPromotionProgramDetailCrud) {
		this.listPromotionProgramDetailCrud = listPromotionProgramDetailCrud;
	}
	public boolean isTabOrder() {
		return tabOrder;
	}
	public void setTabOrder(boolean tabOrder) {
		this.tabOrder = tabOrder;
	}
	public List<WrapOrderDetailLixReqInfo> getListWrapOrderDetailLixReqInfo() {
		return listWrapOrderDetailLixReqInfo;
	}
	public void setListWrapOrderDetailLixReqInfo(List<WrapOrderDetailLixReqInfo> listWrapOrderDetailLixReqInfo) {
		this.listWrapOrderDetailLixReqInfo = listWrapOrderDetailLixReqInfo;
	}
	public List<PromotionOrderDetail> getListPromotionOrderDetailInvoice() {
		return listPromotionOrderDetailInvoice;
	}
	public void setListPromotionOrderDetailInvoice(List<PromotionOrderDetail> listPromotionOrderDetailInvoice) {
		this.listPromotionOrderDetailInvoice = listPromotionOrderDetailInvoice;
	}
	public int getStatusSearch() {
		return statusSearch;
	}
	public void setStatusSearch(int statusSearch) {
		this.statusSearch = statusSearch;
	}
}
