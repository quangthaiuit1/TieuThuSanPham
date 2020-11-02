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
import lixco.com.entity.OrderDetailPos;
import lixco.com.entity.OrderLixPos;
import lixco.com.entity.PaymentMethod;
import lixco.com.entity.PricingProgram;
import lixco.com.entity.PricingProgramDetail;
import lixco.com.entity.Product;
import lixco.com.entity.PromotionOrderDetailPos;
import lixco.com.entity.PromotionProgramDetail;
import lixco.com.entity.TransportPricingNew;
import lixco.com.entity.Warehouse;
import lixco.com.interfaces.IBatchPosService;
import lixco.com.interfaces.ICarService;
import lixco.com.interfaces.ICustomerPricingProgramService;
import lixco.com.interfaces.ICustomerPromotionProgramService;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.ICustomerTypesService;
import lixco.com.interfaces.IDeliveryPricingService;
import lixco.com.interfaces.IFreightContractService;
import lixco.com.interfaces.IIECategoriesService;
import lixco.com.interfaces.IOrderDetailPosService;
import lixco.com.interfaces.IOrderLixPosService;
import lixco.com.interfaces.IPaymentMethodService;
import lixco.com.interfaces.IPricingProgramDetailService;
import lixco.com.interfaces.IProcessLogicInvoicePosService;
import lixco.com.interfaces.IProductService;
import lixco.com.interfaces.IPromotionOrderDetailPosService;
import lixco.com.interfaces.IPromotionProgramDetailService;
import lixco.com.interfaces.IPromotionalPricingService;
import lixco.com.interfaces.IWarehouseService;
import lixco.com.reqInfo.CustomerPricingProgramReqInfo;
import lixco.com.reqInfo.CustomerPromotionProgramReqInfo;
import lixco.com.reqInfo.OrderDetailPosReqInfo;
import lixco.com.reqInfo.OrderLixPosReqInfo;
import lixco.com.reqInfo.PricingProgramDetailReqInfo;
import lixco.com.reqInfo.ProductReqInfo;
import lixco.com.reqInfo.PromotionOrderDetailPosReqInfo;
import lixco.com.reqInfo.PromotionalPricingReqInfo;
import lixco.com.reqInfo.WrapOrderDetailPosLixReqInfo;
import lixco.com.reqInfo.WrapOrderLixPosReqInfo;
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
public class OrderLixPosBean extends AbstractBean  {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IOrderLixPosService orderLixPosService;
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
	private IOrderDetailPosService orderDetailPosService;
	@Inject
	private IPromotionOrderDetailPosService promotionOrderDetailPosService;
	@Inject
	private IPromotionalPricingService promotionalPricingService;
	@Inject
	private IBatchPosService batchPosService;
	@Inject
	private IProcessLogicInvoicePosService processLogicInvoicePosService;
	private List<IECategories> listIECategories;
	private List<PaymentMethod> listPaymentMethod;
	private List<Warehouse> listWarehouse;
	private List<CustomerTypes> listCustomerTypes;
	private OrderLixPos orderLixPosCrud;
	private TransportPricingNew transportPricingNewCrud;
	private OrderLixPos orderLixPosSelect;
	private List<OrderLixPos> listOrderLixPos;
	private OrderDetailPos orderDetailPosCrud;
	private OrderDetailPos orderDetailPosPick;
	private List<PromotionProgramDetail> listPromotionProgramDetailCrud;
	private OrderDetailPos orderDetailPosSelect;
	private List<OrderDetailPos> listOrderDetailPos;
	private List<PromotionOrderDetailPos> listPromotionOrderDetailPos;
	private CustomerTypes customerTypesFilter;
	private String keySearchCustomer;
	private List<Customer> listCustomerSelect;
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
	private Product productSearch2;
	private Account account;
	private FormatHandler formatHandler;
	private boolean tabOrder;
	/*hóa đơn*/
	private List<WrapOrderDetailPosLixReqInfo> listWrapOrderDetailPosLixReqInfo;
	private List<PromotionOrderDetailPos> listPromotionOrderDetailPosInvoice;
	
	
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
			listWrapOrderDetailPosLixReqInfo=new ArrayList<>();
			listPromotionOrderDetailPosInvoice=new ArrayList<>();
		}catch(Exception e){
			logger.error("OrderLixPosBean.initItem:"+e.getMessage(),e);
		}
	}
	public void deleteOrderLix(){
		try{
			
		}catch(Exception e){
			logger.error("OrderLixPosBean.deleteOrderLix:"+e.getMessage(),e);
		}
		
	}
	public void  tabOrderOnClick(){
			this.tabOrder=true;
	}
	public void  tabInvoiceOnClick(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			this.tabOrder=false;
			listWrapOrderDetailPosLixReqInfo=new ArrayList<>();
			listPromotionOrderDetailPosInvoice=new ArrayList<>();
			//kiểm tra hóa đơn có tồn tại không
			if(orderLixPosCrud !=null && !orderLixPosCrud.isFlag_up() && orderLixPosCrud.getId()!=0 ){
				if(orderLixPosCrud.getStatus()==2 || orderLixPosCrud.getStatus()==3){
					notify.warning("Đơn hàng đã hoàn thành hoặc đã hủy!");
				}else{
					for(OrderDetailPos dt:listOrderDetailPos){
						WrapOrderDetailPosLixReqInfo t=new WrapOrderDetailPosLixReqInfo(dt);
						listWrapOrderDetailPosLixReqInfo.add(t);
						//lấy tồn theo lô hàng
						t.setQuantity_batch(batchPosService.getQuantityRemaining(t.getOrder_detail_pos().getProduct().getId()));
					}
				}
				
			}else{
				notify.warning("Đơn hàng chưa lưu không tạo hóa đơn được");
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.tabInvoiceOnClick:"+e.getMessage(),e);
		}
	}
	public void searchCustomer(){
		try{
			listCustomerSelect=new ArrayList<>();
			if(customerTypesFilter !=null){
				customerService.complete(keySearchCustomer.trim(),customerTypesFilter, listCustomerSelect);
			}else{
				customerService.complete(keySearchCustomer.trim(), listCustomerSelect);
			}
		}catch(Exception e){
			logger.error("OrderLixBean.searchCustomer:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdateOrderLix(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(orderLixPosCrud !=null){
				//kiểm tra thông tin có đầy đủ không
				IECategories ie=orderLixPosCrud.getIe_categories();
				Date ngayDH=orderLixPosCrud.getOrder_date();
				Warehouse wh=orderLixPosCrud.getWarehouse();
				Customer customer=orderLixPosCrud.getCustomer();
				if(ie !=null && ngayDH !=null && wh!=null && customer !=null){
					OrderLixPosReqInfo t=new OrderLixPosReqInfo();
					t.setOrder_lix_pos(orderLixPosCrud);
					if(orderLixPosCrud.getId()==0){
						if(allowSave(new Date())){
							orderLixPosCrud.setCreated_by(account.getMember().getName());
							orderLixPosCrud.setCreated_by_id(account.getMember().getId());
							orderLixPosCrud.setCreated_date(new Date());
							int chk=orderLixPosService.insert(t);
							switch (chk) {
								case 0:
									//cập nhật lại flag false
									t.getOrder_lix_pos().setFlag_up(false);
									//check trường hợp copy;
									if(listOrderDetailPos !=null && listOrderDetailPos.size() >0){
										for(OrderDetailPos dt:listOrderDetailPos){
											OrderDetailPosReqInfo olf=new OrderDetailPosReqInfo();
											olf.setOrder_detail_pos(dt);
											dt.setCreated_date(new Date());
											dt.setCreated_by(account.getMember().getName());
											orderDetailPosService.insert(olf);
										}
										current.executeScript(
												"swaldesigntimer('Thành công!', 'Sao chép thành công!','success',2000);");
									}
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Lưu đơn hàng thành công!','success',2000);");
									listOrderLixPos.add(0, orderLixPosCrud.clone());
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
							orderLixPosCrud.setLast_modifed_by(account.getMember().getName());
							orderLixPosCrud.setLast_modifed_date(new Date());
							// check flag nếu flag true (đơn hàng có sự thay đổi chương trình đơn giá hoặc khuyến mãi) thì thực hiện các bước sau 
							if(orderLixPosCrud.isFlag_up()){
								//cập nhật lại đơn hàng
								int chk=orderLixPosService.update(t);
								if(chk==0){
									//cập nhật lại flag false
									t.getOrder_lix_pos().setFlag_up(false);
									listOrderLixPos.set(listOrderLixPos.indexOf(orderLixPosCrud),t.getOrder_lix_pos());
									orderLixPosCrud.setFlag_up(false);
									//cập nhật lại chi tiết đơn hàng 
									if(listOrderDetailPos !=null){
										for(OrderDetailPos dt:listOrderDetailPos){
											OrderDetailPosReqInfo olf=new OrderDetailPosReqInfo();
											olf.setOrder_detail_pos(dt);
											//set ngày modified
											dt.setLast_modifed_date(new Date());
											dt.setLast_modifed_by(account.getMember().getName());
											orderDetailPosService.update(olf);
											listOrderDetailPos.set(listOrderDetailPos.indexOf(dt), olf.getOrder_detail_pos());
										}
									}
									//delete tất cả sản phẩm khuyến mãi
									promotionOrderDetailPosService.deleteAll(t.getOrder_lix_pos().getId());
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
								int chk=orderLixPosService.update(t);
								switch (chk) {
									case 0:
										current.executeScript(
												"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
										listOrderLixPos.set(listOrderLixPos.indexOf(t.getOrder_lix_pos()),t.getOrder_lix_pos());
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
			logger.error("OrderLixPosBean.saveOrUpdateOrderLix:"+e.getMessage(),e);
		}
	}
	public void capNhapThongTinDonGiaKM(){
		try{
			if(orderLixPosCrud !=null && orderLixPosCrud.getCustomer() !=null && orderLixPosCrud.getOrder_date() !=null){
				orderLixPosCrud.setFlag_up(true);
				  /*{order_date:'',customer_id:0}*/
				  //cập nhật chương trình đơn giá cho đơn hàng
				  JsonObject json=new JsonObject();
				  json.addProperty("order_date", ToolTimeCustomer.convertDateToString(orderLixPosCrud.getOrder_date(),"dd/MM/yyyy"));
				  json.addProperty("customer_id",orderLixPosCrud.getCustomer().getId());
				  CustomerPricingProgramReqInfo t=new CustomerPricingProgramReqInfo();
				  customerPricingProgramService.selectForCustomer(JsonParserUtil.getGson().toJson(json),t);
				  /*cập nhật chương trình khuyến mãi cho khách hàng*/
				  CustomerPromotionProgramReqInfo t1=new CustomerPromotionProgramReqInfo();
				  customerPromotionProgramService.selectForCustomer(JsonParserUtil.getGson().toJson(json),t1);
//				  PromotionProgram promotionProgram=t1.getCustomer_promotion_program().getPromotion_program();
				  //nạp chương trình đơn giá
				  if(t.getCustomer_pricing_program() !=null){
					  PricingProgram pricingProgram=t.getCustomer_pricing_program().getPricing_program();
					  orderLixPosCrud.setPricing_program(pricingProgram);
				     if(listOrderDetailPos !=null && listOrderDetailPos.size() >0){
						  //cập nhật đơn giá sản phẩm trong đơn hàng 
						  for(OrderDetailPos d:listOrderDetailPos){
							  d.setOrder_lix_pos(orderLixPosCrud);
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
					  orderLixPosCrud.setPricing_program(null);
					  if(listOrderDetailPos !=null){
						  for(OrderDetailPos d:listOrderDetailPos){
							  //cập nhật đơn giá sản phẩm
							  d.setUnit_price(0);
							  d.setTotal_amount(0);
							  d.setPromotion_forms(0);// reset lại hình thức khuyến mãi
						  }
					  }
				  }
				  /*nạp chương trình khuyến mãi*/
				  if(t1.getCustomer_promotion_program()!=null){
					  orderLixPosCrud.setPromotion_program(t1.getCustomer_promotion_program().getPromotion_program());
				  }else{
					  orderLixPosCrud.setPromotion_program(null);
				  }
				  //cập nhật sản phẩm khuyến mãi trong đơn hàng
				  listPromotionOrderDetailPos=new ArrayList<>();
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.capNhapThongTinDonGiaKM:"+e.getMessage(),e);
		}
	}
	public void rowCustomerClick(SelectEvent event){
		PrimeFaces current=PrimeFaces.current();
		try{
			  Customer obj = (Customer) event.getObject();
			  if(orderLixPosCrud !=null){
				  orderLixPosCrud.setFlag_up(true);
				  orderLixPosCrud.setCustomer(obj);
				  /*{order_date:'',customer_id:0}*/
				  //cập nhật chương trình đơn giá cho đơn hàng
				  JsonObject json=new JsonObject();
				  json.addProperty("order_date", ToolTimeCustomer.convertDateToString(orderLixPosCrud.getOrder_date(),"dd/MM/yyyy"));
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
					     orderLixPosCrud.setPricing_program(pricingProgram);
					     if(listOrderDetailPos !=null && listOrderDetailPos.size() >0){
							  //cập nhật đơn giá sản phẩm trong đơn hàng 
							  for(OrderDetailPos d:listOrderDetailPos){
								  d.setOrder_lix_pos(orderLixPosCrud);
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
					  orderLixPosCrud.setPricing_program(null);
					  if(listOrderDetailPos !=null){
						  for(OrderDetailPos d:listOrderDetailPos){
							  //cập nhật đơn giá sản phẩm
							  d.setUnit_price(0);
							  d.setTotal_amount(0);
							  d.setPromotion_forms(0);// reset lại hình thức khuyến mãi
						  }
					  }
				  }
				  /*nạp chương trình khuyến mãi*/
				  if(t1.getCustomer_promotion_program()!=null){
					  orderLixPosCrud.setPromotion_program(t1.getCustomer_promotion_program().getPromotion_program());
				  }else{
					  orderLixPosCrud.setPromotion_program(null);
				  }
				  // nạp địa điểm giao hàng và đơn giá giao hàng
				  if(list.size() >0){
					  orderLixPosCrud.setDelivery_pricing(list.get(0));
				  }else{
					  orderLixPosCrud.setDelivery_pricing(null);
				  }
				  //cập nhật sản phẩm khuyến mãi trong đơn hàng
				  listPromotionOrderDetailPos=new ArrayList<>();
				 
			  }
			  closeDialogPickKH();
			  current.executeScript("PF('dlg1').hide();");
			  
		}catch(Exception e){
			logger.error("OrderLixPosBean.rowCustomerClick:"+e.getMessage(),e);
		}
	}
	public void rowFreightContractClick(SelectEvent event){
		PrimeFaces current=PrimeFaces.current();
		try{
			FreightContract obj = (FreightContract) event.getObject();
			if(orderLixPosCrud !=null){
				
			}
		}catch(Exception e){
			logger.error("OrderLixBean.rowFreightContractClick:"+e.getMessage(),e);
		}
	}
	public List<Customer> completeCustomer(String text){
		try{
			List<Customer> list=new ArrayList<>();
			if(customerTypesFilter !=null){
				customerService.complete(formatHandler.converViToEn(text),customerTypesFilter, list);
			}else{
				customerService.complete(formatHandler.converViToEn(text), list);
			}
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
			listOrderLixPos=new ArrayList<OrderLixPos>();
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
			orderLixPosService.search(JsonParserUtil.getGson().toJson(json), page, listOrderLixPos);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		}catch(Exception e){
			logger.error("OrderLixPosBean.search:"+e.getMessage(),e);
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
			logger.error("OrderLixPosBean.initRowPerPage:" + e.getMessage(), e);
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
			logger.error("OrderLixPosBean.initRowPerPage:" + e.getMessage(), e);
		}
	}
	public void paginatorChange(int currentPage) {
		try {
			/*{ order_info:{from_date:'',to_date:'',customer_id:0,order_code:'',voucher_code:'',ie_categories_id:0,po_no:'',delivered:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listOrderLixPos = new ArrayList<OrderLixPos>();
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
			orderLixPosService.search(JsonParserUtil.getGson().toJson(json), page, listOrderLixPos);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("OrderLixBean.paginatorChange:" + e.getMessage(), e);
		}
	}
	public void showEditOrderLix(){
		try{
			if(orderLixPosSelect !=null ){
				orderLixPosCrud=orderLixPosSelect.clone();
				//show thông tin chi tiết đơn hàng
				listOrderDetailPos=new ArrayList<>();
				orderDetailPosService.selectByOrder(orderLixPosCrud.getId(), listOrderDetailPos);
				//show thông tin sản phẩm khuyến khuyến mãi của đơn hàng
				listPromotionOrderDetailPos=new ArrayList<>();
				promotionOrderDetailPosService.selectByOrder(orderLixPosCrud.getId(),listPromotionOrderDetailPos);
				sumOrderLix();
				if(!tabOrder){
					tabInvoiceOnClick();
				}
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.showEditOrderLix:" + e.getMessage(), e);
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
			logger.error("OrderLixPosBean.completeCar:" + e.getMessage(), e);
		}
		return null;
	}
	public void createdNew(){
		try{
			orderLixPosCrud=new OrderLixPos();
			orderLixPosCrud.setOrder_date(ToolTimeCustomer.getFirstDateOfDay(new  Date()));
			orderLixPosCrud.setCreated_by(account.getMember().getName());
			listOrderDetailPos=new ArrayList<>();
			listPromotionOrderDetailPos=new ArrayList<>();
			listWrapOrderDetailPosLixReqInfo=new ArrayList<>();
			listPromotionOrderDetailPosInvoice=new ArrayList<>();
		}catch(Exception e){
			logger.error("OrderLixPosBean.createNew:" + e.getMessage(), e);
		}
	}
	public void kiemTraSoLuong(WrapOrderDetailPosLixReqInfo t){
		try{
			OrderDetailPos d=t.getOrder_detail_pos();
			double slcon=BigDecimal.valueOf(d.getBox_quantity()).subtract(BigDecimal.valueOf(d.getRealbox_quantity())).doubleValue();
			if(t.getQuantity()>slcon){
				t.setQuantity(0);
			}
		}catch (Exception e) {
			logger.error("OrderLixPosBean.kiemTraSoLuong:" + e.getMessage(), e);
		}
	}
	public void createdInvoide(){
		PrimeFaces current=PrimeFaces.current();
		try{
			//chuẩn bị dữ liệu
			if(listWrapOrderDetailPosLixReqInfo !=null && listWrapOrderDetailPosLixReqInfo.size()>0){
				caculatePromotionInvoice();
				WrapOrderLixPosReqInfo t=new WrapOrderLixPosReqInfo();
				t.setCreated_by(account.getMember().getName());
				t.setCreated_by_id(account.getMember().getId());
				t.setOrder_lix_pos(orderLixPosCrud.clone());
				List<WrapOrderDetailPosLixReqInfo> list=new ArrayList<>();
				t.setList_wrap_order_detail_pos(list);
				t.setList_promotion_order_detail_pos(listPromotionOrderDetailPosInvoice);
				for(WrapOrderDetailPosLixReqInfo w:listWrapOrderDetailPosLixReqInfo){
					if(w.getQuantity() >0){
					   list.add(w);
					}
				}
				StringBuilder messages=new StringBuilder();
				WrapOrderLixPosReqInfo cloned=t.clone();
				int ck=processLogicInvoicePosService.createInvoicePosByOrder(cloned, messages);
				if(ck==0){
					current.executeScript(
							"swaldesigntimer('Thành công!', 'Sao chép thành công!','success',2000);");
				}else{
					current.executeScript(
							"swaldesigntimer('Thất bại!', '"+messages.toString()+"!','warning',2000);");
				}
				//refesh lại order;
				OrderLixPosReqInfo orderLixReqInfo=new OrderLixPosReqInfo();
				orderLixPosService.selectById(cloned.getOrder_lix_pos().getId(),orderLixReqInfo);
				listOrderLixPos.set(listOrderLixPos.indexOf(orderLixReqInfo.getOrder_lix_pos()), orderLixReqInfo.getOrder_lix_pos());
				orderLixPosCrud=orderLixReqInfo.getOrder_lix_pos().clone();
				//load lại detail orderlix;
				listOrderDetailPos=new ArrayList<>();
				orderDetailPosService.selectByOrder(orderLixPosCrud.getId(), listOrderDetailPos);
				//show thông tin sản phẩm khuyến khuyến mãi của đơn hàng
				listPromotionOrderDetailPos=new ArrayList<>();
				promotionOrderDetailPosService.selectByOrder(orderLixPosCrud.getId(),listPromotionOrderDetailPos);
				sumOrderLix();
				if(!tabOrder){
					tabInvoiceOnClick();
				}
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.kiemTraSoLuong:" + e.getMessage(), e);
		}
	}
	public void destroyOrder(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(orderLixPosCrud!=null && orderLixPosCrud.getId()!=0){
				orderLixPosCrud.setStatus(3);// kết thúc hóa đơn
				if(orderLixPosService.update(new OrderLixPosReqInfo(orderLixPosCrud))==0){
					notify.success("Kết thúc hóa đơn thành công!");
				}else{
					notify.success("Thất bại!");
				}
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.destroyOrder:" + e.getMessage(), e);
		}
	}
	public void caculatePromotionInvoice(){
		try{
			listPromotionOrderDetailPosInvoice=new ArrayList<>();
			if(listWrapOrderDetailPosLixReqInfo!=null){
				for(WrapOrderDetailPosLixReqInfo t:listWrapOrderDetailPosLixReqInfo){
					if (t.getQuantity() > 0) {
						// tính khuyến mãi
						JsonObject js = new JsonObject();
						js.addProperty("product_id", t.getOrder_detail_pos().getProduct().getId());
						js.addProperty("promotion_program_id", orderLixPosCrud.getPromotion_program().getId());
						js.addProperty("promotion_form", t.getOrder_detail_pos().getPromotion_forms());
						List<PromotionProgramDetail> list = new ArrayList<>();
						promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(js), list);
						lv1: for (PromotionProgramDetail ppd : list) {
							PromotionOrderDetailPos pod = new PromotionOrderDetailPos();
							pod.setOrder_detail_pos(t.getOrder_detail_pos());
							Product pr=ppd.getPromotion_product();
							pod.setProduct(pr);
							// tim nạp đơn giá sản phẩm khuyến mãi
							PromotionalPricingReqInfo ppr = new PromotionalPricingReqInfo();
							JsonObject json = new JsonObject();
							json.addProperty("order_date", ToolTimeCustomer.convertDateToString(
									t.getOrder_detail_pos().getOrder_lix_pos().getOrder_date(), "dd/MM/yyyy"));
							json.addProperty("product_id", ppd.getPromotion_product().getId());
							promotionalPricingService
									.findSettingPromotionalPricing(JsonParserUtil.getGson().toJson(json), ppr);
							double quantity=BigDecimal.valueOf(t.getQuantity()).multiply(BigDecimal.valueOf(ppd.getPromotion_quantity())).divide(BigDecimal.valueOf(ppd.getBox_quatity())).doubleValue();
							pod.setQuantity(quantity);
							if (ppr.getPromotional_pricing() != null) {
								pod.setUnit_price(ppr.getPromotional_pricing().getUnit_price());
								pod.setTotal_amount(pod.getUnit_price() * pod.getQuantity());
							}
							for (PromotionOrderDetailPos p : listPromotionOrderDetailPosInvoice) {
								if (p.getProduct().equals(pod.getProduct())) {
									p.setQuantity(Double.sum(p.getQuantity(), pod.getQuantity()));
									continue lv1;
								}
							}
							listPromotionOrderDetailPosInvoice.add(pod);
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
			if (orderDetailPosSelect != null) {
				if(!orderLixPosCrud.isFlag_up()){
					orderDetailPosCrud = orderDetailPosSelect;
					orderDetailPosCrud.setOrder_lix_pos(orderLixPosCrud);
					// load danh sách sản phẩm chương trình khuyến mãi
					listPromotionProgramDetailCrud=new ArrayList<>();
					if (orderLixPosCrud.getPromotion_program() != null) {
						// load danh sách sản phẩm chương trình khuyến mãi
						/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
						JsonObject json = new JsonObject();
						json.addProperty("product_id", orderDetailPosCrud.getProduct().getId());
						json.addProperty("promotion_program_id", orderLixPosCrud.getPromotion_program().getId());
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
			logger.error("OrderLixPosBean.showEditOrderDetail:" + e.getMessage(), e);
		}
	}
	public void showDialogPickKH(){
		PrimeFaces current=PrimeFaces.current();
		try{
			customerTypesFilter=null;
			keySearchCustomer=null;
			listCustomerSelect=null;
			current.executeScript("PF('dlg1').show();");
		}catch(Exception e){
			logger.error("OrderLixPosBean.showDialogPickKH:" + e.getMessage(), e);
		}
	}
	public void showDialogPickHDVC(){
		PrimeFaces current=PrimeFaces.current();
		try{
			fromDateFCFilter=ToolTimeCustomer.getDateMinCustomer(ToolTimeCustomer.getMonthCurrent(), ToolTimeCustomer.getYearCurrent());
			toDateFCFilter=ToolTimeCustomer.getDateMaxCustomer(ToolTimeCustomer.getMonthCurrent(), ToolTimeCustomer.getYearCurrent());
			contractNoFCFilter=null;
			keySearchCustomer=null;
			listCustomerSelect=null;
			customerFcFilter=null;
			customerTypesFCFilter=null;
			navigationInfoHDVC = new NavigationInfo();
			navigationInfoHDVC.setCurrentPage(1);
			initRowPerPageHDVC();
			navigationInfoHDVC.setLimit(pageSizeHDVC);
			navigationInfoHDVC.setMaxIndices(5);
			current.executeScript("PF('dlg2').show();");
		}catch(Exception e){
			logger.error("OrderLixPosBean.showDialogPickHDVC:" + e.getMessage(), e);
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
			logger.error("OrderLixPosBean.searchHDVC:" + e.getMessage(), e);
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
			logger.error("OrderLixPosBean.paginatorChangeHDVC:" + e.getMessage(), e);
		}
	}
	public void closeDialogPickKH(){
		
		try{
			customerTypesFilter=null;
			keySearchCustomer=null;
			listCustomerSelect=null;
		}catch(Exception e){
			logger.error("OrderLixPosBean.closeDialogPickKH:" + e.getMessage(), e);
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
			logger.error("OrderLixPosBean.closeDialogPickHDVC:" + e.getMessage(), e);
		}
	}
	public void showDialogOrderDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(orderLixPosCrud !=null && orderLixPosCrud.getId() !=0){
				if(!orderLixPosCrud.isFlag_up()){
					orderDetailPosCrud=new OrderDetailPos();
					orderDetailPosCrud.setOrder_lix_pos(orderLixPosCrud);
					listPromotionProgramDetailCrud=null;
					current.executeScript("PF('dlgc1').show();");
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo', 'Đơn hàng chưa được lưu!','warning',2000);");
				}
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.showDialogOrderDetail:" + e.getMessage(), e);
		}
	}
	public void showDialogPromotionPick(OrderDetailPos item){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		PrimeFaces current=PrimeFaces.current();
		try{
			if(item.getOrder_lix_pos().getPromotion_program() !=null ){
				if(!orderLixPosCrud.isFlag_up()){
					orderDetailPosPick=item;
					listPromotionProgramDetailCrud=new ArrayList<>();
					//load danh sách sản phẩm chương trình khuyến mãi
					/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
					JsonObject json=new JsonObject();
					json.addProperty("product_id", item.getProduct().getId());
					json.addProperty("promotion_program_id", orderLixPosCrud.getPromotion_program().getId());
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
			logger.error("OrderLixPosBean.showDialogPromotionPick:" + e.getMessage(), e);
		}
	}
	public void pickPromotion(PromotionProgramDetail item){
		PrimeFaces current=PrimeFaces.current();
		Notify notify=new Notify(FacesContext.getCurrentInstance());
		try{
			if(orderDetailPosPick !=null){
				if(allowUpdate(new Date())){
					orderDetailPosPick.setPromotion_forms(item.getPromotion_form());
					if(orderDetailPosService.update(new OrderDetailPosReqInfo(orderDetailPosPick))==0){
						/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
						//xóa sản phẩm khuyến mãi cũ
						int chk=promotionOrderDetailPosService.deleteBy(orderDetailPosPick.getId());
						if(chk>=0){
							JsonObject js=new JsonObject();
							js.addProperty("product_id", item.getProduct().getId());
							js.addProperty("promotion_program_id", item.getPromotion_program().getId());
							js.addProperty("promotion_form", item.getPromotion_form());
							List<PromotionProgramDetail> list=new ArrayList<>();
							promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(js), list);
							for(PromotionProgramDetail ppd:list){
								PromotionOrderDetailPos pod=new PromotionOrderDetailPos();
								pod.setOrder_detail_pos(orderDetailPosPick);
								pod.setProduct(ppd.getPromotion_product());
								//tim nạp đơn giá sản phẩm khuyến mãi
								PromotionalPricingReqInfo ppr=new PromotionalPricingReqInfo();
								JsonObject json=new JsonObject();
								json.addProperty("order_date",ToolTimeCustomer.convertDateToString(orderDetailPosPick.getOrder_lix_pos().getOrder_date(), "dd/MM/yyyy"));
								json.addProperty("product_id",ppd.getPromotion_product().getId());
								promotionalPricingService.findSettingPromotionalPricing(JsonParserUtil.getGson().toJson(json), ppr);
								pod.setQuantity(orderDetailPosPick.getBox_quantity()*ppd.getPromotion_quantity()/ppd.getBox_quatity());
								if(ppr.getPromotional_pricing() !=null){
									pod.setUnit_price(ppr.getPromotional_pricing().getUnit_price());
									pod.setTotal_amount(pod.getUnit_price()*pod.getQuantity());
								}
								pod.setCreated_date(new Date());
								pod.setCreated_by(account.getMember().getName());
								promotionOrderDetailPosService.insert(new PromotionOrderDetailPosReqInfo(pod));
							}
							//load lai sản phẩm khuyến mãi đơn hàng
							listPromotionOrderDetailPos=new ArrayList<>();
							promotionOrderDetailPosService.selectByOrder(orderDetailPosPick.getOrder_lix_pos().getId(), listPromotionOrderDetailPos);
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
			logger.error("OrderLixPosBean.pickPromotion:" + e.getMessage(), e);
		}
	}
	public void loadInfo(){
		try{
			listPromotionProgramDetailCrud=new ArrayList<>();
			if(orderLixPosCrud!=null && orderDetailPosCrud !=null && orderDetailPosCrud.getProduct() !=null){
				if(orderLixPosCrud.getPromotion_program() !=null){
					//load danh sách sản phẩm chương trình khuyến mãi
					/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
					JsonObject json=new JsonObject();
					json.addProperty("product_id", orderDetailPosCrud.getProduct().getId());
					json.addProperty("promotion_program_id", orderLixPosCrud.getPromotion_program().getId());
					promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(json),listPromotionProgramDetailCrud );
				}
				if(orderLixPosCrud.getPricing_program() !=null){
					//load danh sách sản phẩm ctkm
					PricingProgramDetailReqInfo t=new PricingProgramDetailReqInfo();
					pricingProgramDetailService.findSettingPricingChild(orderLixPosCrud.getPricing_program().getId(), orderDetailPosCrud.getProduct().getId(), t);
					if(t.getPricing_program_detail()==null){
						pricingProgramDetailService.findSettingPricing(orderLixPosCrud.getPricing_program().getId(), orderDetailPosCrud.getProduct().getId(), t);
					}
					if(t.getPricing_program_detail() !=null){
						orderDetailPosCrud.setUnit_price(t.getPricing_program_detail().getUnit_price());
					}else{
						orderDetailPosCrud.setUnit_price(0);
					}
				}
				ProductReqInfo pr=new ProductReqInfo();
				productService.selectById(orderDetailPosCrud.getProduct().getId(),pr);
				double quantity=BigDecimal.valueOf(orderDetailPosCrud.getRealbox_quantity()).multiply(BigDecimal.valueOf(pr.getProduct().getSpecification())).doubleValue();
				orderDetailPosCrud.setQuantity(quantity);
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.loadPromotionProgramDetailCrud:" + e.getMessage(), e);
		}
	}
	public void selectPromotionForm(PromotionProgramDetail item){
		try{
			if(orderDetailPosCrud !=null){
				orderDetailPosCrud.setPromotion_forms(item.getPromotion_form());
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.selectPromotionForm:" + e.getMessage(), e);
		}
	}

	public void changeBoxOrderDetail(){
		try{
			if(orderDetailPosCrud !=null){
//				orderDetailCrud.setRealbox_quantity(orderDetailCrud.getBox_quantity());
				if(orderDetailPosCrud.getProduct() !=null){
					ProductReqInfo pr=new ProductReqInfo();
					productService.selectById(orderDetailPosCrud.getProduct().getId(),pr);
					double quantity=BigDecimal.valueOf(orderDetailPosCrud.getBox_quantity()).multiply(BigDecimal.valueOf(pr.getProduct().getSpecification())).doubleValue();
					orderDetailPosCrud.setQuantity(quantity);
				}else{
					orderDetailPosCrud.setQuantity(0);
				}
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.changeBoxOrderDetail:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdateOrderDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(orderDetailPosCrud !=null){
				//kiểm tra thông tin có đầy đủ không
				Product product=orderDetailPosCrud.getProduct();
				double box=orderDetailPosCrud.getBox_quantity();
				OrderLixPos orderLixPos=orderDetailPosCrud.getOrder_lix_pos();
				if(product!=null && box !=0){
					OrderDetailPosReqInfo t=new OrderDetailPosReqInfo();
					t.setOrder_detail_pos(orderDetailPosCrud);
					if(orderDetailPosCrud.getId()==0){
						if(allowSave(new Date())){
							orderDetailPosCrud.setCreated_by(account.getMember().getName());
							orderDetailPosCrud.setCreated_date(new Date());
							
							int chk=orderDetailPosService.insert(t);
							switch (chk) {
							case 0:
								current.executeScript(
										"swaldesigntimer('Thành công!', 'Thêm thành công!','success',2000);");
								listOrderDetailPos.add(0,orderDetailPosCrud);
								
								//tính sản phẩm khuyến mãi.
								if(orderDetailPosCrud.getPromotion_forms() != 0){
									/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
									JsonObject js=new JsonObject();
									js.addProperty("product_id", product.getId());
									js.addProperty("promotion_program_id", orderLixPos.getPromotion_program().getId());
									js.addProperty("promotion_form", orderDetailPosCrud.getPromotion_forms());
									List<PromotionProgramDetail> list=new ArrayList<>();
									promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(js), list);
									for(PromotionProgramDetail ppd:list){
										PromotionOrderDetailPos pod=new PromotionOrderDetailPos();
										pod.setOrder_detail_pos(orderDetailPosCrud);
										pod.setProduct(ppd.getPromotion_product());
										//tìm nạp đơn giá sản phẩm khuyến mãi
										double quantity=BigDecimal.valueOf(box).multiply(BigDecimal.valueOf(ppd.getPromotion_quantity())).divide(BigDecimal.valueOf(ppd.getBox_quatity())).doubleValue();
										pod.setQuantity(quantity);
										pod.setCreated_date(new Date());
										pod.setCreated_by(account.getMember().getName());
										promotionOrderDetailPosService.insert(new PromotionOrderDetailPosReqInfo(pod));
									}
									//load lai sản phẩm khuyến mãi đơn hàng
									listPromotionOrderDetailPos=new ArrayList<>();
									promotionOrderDetailPosService.selectByOrder(orderLixPos.getId(), listPromotionOrderDetailPos);
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
							orderDetailPosCrud.setLast_modifed_by(account.getMember().getName());
							orderDetailPosCrud.setLast_modifed_date(new Date());
							OrderDetailPosReqInfo pre=new OrderDetailPosReqInfo();
							orderDetailPosService.selectById(orderDetailPosCrud.getId(), pre);
							int chk=orderDetailPosService.update(t);
							switch (chk) {
								case 0:
									current.executeScript(
											"swaldesigntimer('Thành công!', 'Cập nhật thành công!','success',2000);");
									OrderDetailPos orderDetailPre=pre.getOrder_detail_pos();
									//delete chi tiết sản phẩm khuyến mãi
									promotionOrderDetailPosService.deleteBy(orderDetailPre.getId());
									//cập nhật sản phẩm khuyến mãi mới
									//tính sản phẩm khuyến mãi.
									if(orderDetailPosCrud.getPromotion_forms() != 0){
										/*{product_id:0,promotion_product_id:0,promotion_program_id:0,promotion_form:0}*/
										JsonObject js=new JsonObject();
										js.addProperty("product_id", product.getId());
										js.addProperty("promotion_program_id", orderLixPos.getPromotion_program().getId());
										js.addProperty("promotion_form", orderDetailPosCrud.getPromotion_forms());
										List<PromotionProgramDetail> list=new ArrayList<>();
										promotionProgramDetailService.selectBy(JsonParserUtil.getGson().toJson(js), list);
										for(PromotionProgramDetail ppd:list){
											PromotionOrderDetailPos pod=new PromotionOrderDetailPos();
											pod.setOrder_detail_pos(orderDetailPre);
											pod.setProduct(ppd.getPromotion_product());
											//tim nạp đơn giá sản phẩm khuyến mãi
											double quantity=BigDecimal.valueOf(box).multiply(BigDecimal.valueOf(ppd.getPromotion_quantity())).divide(BigDecimal.valueOf(ppd.getBox_quatity())).doubleValue();
											pod.setQuantity(quantity);
											pod.setCreated_date(new Date());
											pod.setCreated_by(account.getMember().getName());
											promotionOrderDetailPosService.insert(new PromotionOrderDetailPosReqInfo(pod));
										}
										//load lai sản phẩm khuyến mãi đơn hàng
										listPromotionOrderDetailPos=new ArrayList<>();
										promotionOrderDetailPosService.selectByOrder(orderLixPos.getId(), listPromotionOrderDetailPos);
									}
									listOrderDetailPos.set(listOrderDetailPos.indexOf(orderDetailPosCrud),t.getOrder_detail_pos());
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
			logger.error("OrderLixPosBean.saveOrUpdate:" + e.getMessage(), e);
		}
	}
	public void createNewOrderDetail(){
		try{
			
			orderDetailPosCrud=new OrderDetailPos();
			orderDetailPosCrud.setOrder_lix_pos(orderLixPosCrud);
			listPromotionProgramDetailCrud=null;
			
		}catch(Exception e){
			logger.error("OrderLixPosBean.createNewOrderDetail:" + e.getMessage(), e);
		}
	}
	public void deleteOrderLixDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(orderDetailPosSelect !=null){
				if(allowDelete(new Date())){
					if(orderDetailPosService.deleteById(orderDetailPosSelect.getId())>0){
						current.executeScript(
								"swaldesigntimer('Thành công!', 'Xóa thành công!','success',2000);");
						
						//tính lại sản phẩm khuyến mãi
						int chk=promotionOrderDetailPosService.deleteBy(orderDetailPosSelect.getId());
						listPromotionOrderDetailPos=new ArrayList<>();
						promotionOrderDetailPosService.selectByOrder(orderDetailPosSelect.getOrder_lix_pos().getId(), listPromotionOrderDetailPos);
						listOrderDetailPos.remove(orderDetailPosSelect);
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
			logger.error("OrderLixPosBean.deleteOrderLixDetail:" + e.getMessage(), e);
		}
	}
	public void calPromotionProduct(){
		try{
			if(listOrderDetailPos !=null && listOrderDetailPos.size()>0){
				for(OrderDetailPos d:listOrderDetailPos){
					
				}
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.calSanPhamKhuyenMai:" + e.getMessage(), e);
		}
	}
	public double sumOrderLix(){
		try{
			if(listOrderDetailPos !=null && listOrderDetailPos.size()>0){
				double totalevent = listOrderDetailPos.stream().mapToDouble(f -> f.getQuantity()*f.getUnit_price()).sum();
				return totalevent;
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.calSanPhamKhuyenMai:" + e.getMessage(), e);
		}
		return 0;
	}
	public void copyOrderLix(){
		try{
			if(orderLixPosCrud !=null && orderLixPosCrud.getId()!=0 && !orderLixPosCrud.isFlag_up()){
				orderLixPosCrud.setId(0);
				orderLixPosCrud.setOrder_code(null);
				orderLixPosCrud.setVoucher_code(null);
				for(OrderDetailPos d:listOrderDetailPos){
					d.setId(0);
					// reference chó nó tự động inject Id
					d.setOrder_lix_pos(orderLixPosCrud);
					d.setPromotion_forms(0);
					d.setNote(null);
					d.setLast_modifed_by(null);
					d.setLast_modifed_date(null);
				}
				listPromotionOrderDetailPos=new ArrayList<>();
			}
		}catch(Exception e){
			logger.error("OrderLixPosBean.copyOrderLix:" + e.getMessage(), e);
		}
	}
	public void exportPDF(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(orderLixPosCrud !=null && orderLixPosCrud.getId() >0){
				String reportPath ="";
				String reportPath2 ="";
				String reportPath3 ="";
				if(listOrderDetailPos !=null && listOrderDetailPos.size()>10){
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
				importParam.put("voucher_code",orderLixPosCrud.getVoucher_code());
				importParam.put("order_date",ToolTimeCustomer.convertDateToString(orderLixPosCrud.getOrder_date(),"dd/MM/yyyy"));
				importParam.put("license_plate", orderLixPosCrud.getCar() == null ? "" :orderLixPosCrud.getCar().getLicense_plate());
				String customerName=orderLixPosCrud.getCustomer().getCustomer_name();
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
				importParam.put("customer_code",orderLixPosCrud.getCustomer().getCustomer_code());
				importParam.put("note", orderLixPosCrud.getNote());
				importParam.put("listOrderDetailN", listOrderDetailPos);
				importParam.put("listPromotionOrderDetailN", listPromotionOrderDetailPos);
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
			logger.error("OrderLixPosBean.exportPDF:" + e.getMessage(), e);
		}
	}

	public static byte[] mergePDF(List<byte[]> pdfFilesAsByteArray) throws DocumentException, IOException {

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
	public OrderLixPos getOrderLixPosCrud() {
		return orderLixPosCrud;
	}
	public void setOrderLixPosCrud(OrderLixPos orderLixPosCrud) {
		this.orderLixPosCrud = orderLixPosCrud;
	}
	public TransportPricingNew getTransportPricingNewCrud() {
		return transportPricingNewCrud;
	}
	public void setTransportPricingNewCrud(TransportPricingNew transportPricingNewCrud) {
		this.transportPricingNewCrud = transportPricingNewCrud;
	}
	public OrderLixPos getOrderLixPosSelect() {
		return orderLixPosSelect;
	}
	public void setOrderLixPosSelect(OrderLixPos orderLixPosSelect) {
		this.orderLixPosSelect = orderLixPosSelect;
	}
	public List<OrderLixPos> getListOrderLixPos() {
		return listOrderLixPos;
	}
	public void setListOrderLixPos(List<OrderLixPos> listOrderLixPos) {
		this.listOrderLixPos = listOrderLixPos;
	}
	public OrderDetailPos getOrderDetailPosCrud() {
		return orderDetailPosCrud;
	}
	public void setOrderDetailPosCrud(OrderDetailPos orderDetailPosCrud) {
		this.orderDetailPosCrud = orderDetailPosCrud;
	}
	public OrderDetailPos getOrderDetailPosPick() {
		return orderDetailPosPick;
	}
	public void setOrderDetailPosPick(OrderDetailPos orderDetailPosPick) {
		this.orderDetailPosPick = orderDetailPosPick;
	}
	public List<PromotionProgramDetail> getListPromotionProgramDetailCrud() {
		return listPromotionProgramDetailCrud;
	}
	public void setListPromotionProgramDetailCrud(List<PromotionProgramDetail> listPromotionProgramDetailCrud) {
		this.listPromotionProgramDetailCrud = listPromotionProgramDetailCrud;
	}
	public OrderDetailPos getOrderDetailPosSelect() {
		return orderDetailPosSelect;
	}
	public void setOrderDetailPosSelect(OrderDetailPos orderDetailPosSelect) {
		this.orderDetailPosSelect = orderDetailPosSelect;
	}
	public List<OrderDetailPos> getListOrderDetailPos() {
		return listOrderDetailPos;
	}
	public void setListOrderDetailPos(List<OrderDetailPos> listOrderDetailPos) {
		this.listOrderDetailPos = listOrderDetailPos;
	}
	public List<PromotionOrderDetailPos> getListPromotionOrderDetailPos() {
		return listPromotionOrderDetailPos;
	}
	public void setListPromotionOrderDetailPos(List<PromotionOrderDetailPos> listPromotionOrderDetailPos) {
		this.listPromotionOrderDetailPos = listPromotionOrderDetailPos;
	}
	public List<PromotionOrderDetailPos> getListPromotionOrderDetailPosInvoice() {
		return listPromotionOrderDetailPosInvoice;
	}
	public void setListPromotionOrderDetailPosInvoice(List<PromotionOrderDetailPos> listPromotionOrderDetailPosInvoice) {
		this.listPromotionOrderDetailPosInvoice = listPromotionOrderDetailPosInvoice;
	}
	public CustomerTypes getCustomerTypesFilter() {
		return customerTypesFilter;
	}
	public void setCustomerTypesFilter(CustomerTypes customerTypesFilter) {
		this.customerTypesFilter = customerTypesFilter;
	}
	public String getKeySearchCustomer() {
		return keySearchCustomer;
	}
	public void setKeySearchCustomer(String keySearchCustomer) {
		this.keySearchCustomer = keySearchCustomer;
	}
	public List<Customer> getListCustomerSelect() {
		return listCustomerSelect;
	}
	public void setListCustomerSelect(List<Customer> listCustomerSelect) {
		this.listCustomerSelect = listCustomerSelect;
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
	public CustomerTypes getCustomerTypesFCFilter() {
		return customerTypesFCFilter;
	}
	public void setCustomerTypesFCFilter(CustomerTypes customerTypesFCFilter) {
		this.customerTypesFCFilter = customerTypesFCFilter;
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
	public List<FreightContract> getListFreightContractSelect() {
		return listFreightContractSelect;
	}
	public void setListFreightContractSelect(List<FreightContract> listFreightContractSelect) {
		this.listFreightContractSelect = listFreightContractSelect;
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
	public Product getProductSearch2() {
		return productSearch2;
	}
	public void setProductSearch2(Product productSearch2) {
		this.productSearch2 = productSearch2;
	}
	public FormatHandler getFormatHandler() {
		return formatHandler;
	}
	public void setFormatHandler(FormatHandler formatHandler) {
		this.formatHandler = formatHandler;
	}
	public boolean isTabOrder() {
		return tabOrder;
	}
	public void setTabOrder(boolean tabOrder) {
		this.tabOrder = tabOrder;
	}
	public List<WrapOrderDetailPosLixReqInfo> getListWrapOrderDetailPosLixReqInfo() {
		return listWrapOrderDetailPosLixReqInfo;
	}
	public void setListWrapOrderDetailPosLixReqInfo(List<WrapOrderDetailPosLixReqInfo> listWrapOrderDetailPosLixReqInfo) {
		this.listWrapOrderDetailPosLixReqInfo = listWrapOrderDetailPosLixReqInfo;
	}
	public int getStatusSearch() {
		return statusSearch;
	}
	public void setStatusSearch(int statusSearch) {
		this.statusSearch = statusSearch;
	}
	
}
