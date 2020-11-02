package lixco.com.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name="invoice")
public class Invoice implements Serializable,Cloneable{//hóa đơn chính
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created_date;
	private String created_by;
	private long created_by_id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date last_modifed_date;
	private String last_modifed_by;
	@Column(unique=true)
	private String invoice_code;//mã hóa đơn
	private String voucher_code;//mã chứng từ
	@Temporal(TemporalType.DATE)
	private Date invoice_date;//ngày hóa đơn
	@ManyToOne(fetch=FetchType.LAZY)
	private Customer customer;// khánh hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private Car car;// xe
	@ManyToOne(fetch=FetchType.LAZY)
	private PaymentMethod payment_method;// hình thức thanh toán
	@ManyToOne(fetch=FetchType.LAZY)
	private DeliveryPricing delivery_pricing;//đơn giá vận chuyển
	@ManyToOne(fetch=FetchType.LAZY)
	private FreightContract freight_contract;//hợp đồng vận chuyển
	private String department_name;//bộ phận
	private String note;// ghi chú hóa đơn
	@ManyToOne(fetch=FetchType.LAZY)
	private IECategories ie_categories;//Danh mục xuất nhập
	private String ie_reason;//lý do xuất nhập
	@ManyToOne(fetch=FetchType.LAZY)
	private Warehouse warehouse;//mã kho 
	private double tax_value;//hệ số thuế
	private double tax_edit;//điều chỉnh thuế
	@ManyToOne(fetch=FetchType.LAZY)
	private Contract contract;//hợp đồng mua bán
	private String contract_no;//số hợp đồng mua bán
	private String invoice_serie;//serie hóa đơn
	private String order_voucher;//số đơn hàng (số chứng từ đơn hàng)
	private String order_code;//mã đơn hàng
	private String movement_commands_no;//số lệnh điều động
	private boolean payment;// đã thanh toán
	private boolean timeout;//ngoài giờ
	@ManyToOne(fetch=FetchType.LAZY)
	private PromotionProgram promotion_program;// chương trình khuyến mãi
	@ManyToOne(fetch=FetchType.LAZY)
	private PricingProgram pricing_program;//chương trình đơn giá
	@ManyToOne(fetch=FetchType.LAZY)
	private HarborCategory harbor_category;//cảng đến
	@ManyToOne(fetch=FetchType.LAZY)
	private Stocker stocker;//thủ kho
	@ManyToOne(fetch=FetchType.LAZY)
	private Stevedore stevedore;//bốc xếp
	@ManyToOne(fetch=FetchType.LAZY)
	private FormUpGoods form_up_goods;//hình thức lên hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private OrderLix order_lix;//đơn hàng
	private String po_no;//số po
	private String note2;
	@ManyToOne(fetch=FetchType.LAZY)
	private Carrier carrier;//người vận chuyển
	private String transport_content;//nội dung vận chuyển
	@Temporal(TemporalType.TIMESTAMP)
	private Date delivery_date;
	private int time_up_goods;//thời gian lên hàng số phút
	private String e_invoice_id;// id hóa đơn điện tử
	private String lookup_code;// mã tra cứu
	private String content_qd;//nội dung chuyển đổi
	@OneToMany(fetch=FetchType.LAZY,mappedBy="invoice")
	private List<InvoiceDetail> list_invoice_detail;
	private int status;//0 lưu tạm,1 hoàn thành
	private double discount;//chiếc khấu
	private boolean ipromotion;
	@ManyToOne(fetch=FetchType.LAZY)
	private Invoice invoice_own;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public Date getLast_modifed_date() {
		return last_modifed_date;
	}
	public void setLast_modifed_date(Date last_modifed_date) {
		this.last_modifed_date = last_modifed_date;
	}
	public String getLast_modifed_by() {
		return last_modifed_by;
	}
	public void setLast_modifed_by(String last_modifed_by) {
		this.last_modifed_by = last_modifed_by;
	}
	public String getInvoice_code() {
		return invoice_code;
	}
	public void setInvoice_code(String invoice_code) {
		this.invoice_code = invoice_code;
	}
	public String getVoucher_code() {
		return voucher_code;
	}
	public void setVoucher_code(String voucher_code) {
		this.voucher_code = voucher_code;
	}
	public Date getInvoice_date() {
		return invoice_date;
	}
	public void setInvoice_date(Date invoice_date) {
		this.invoice_date = invoice_date;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public PaymentMethod getPayment_method() {
		return payment_method;
	}
	public void setPayment_method(PaymentMethod payment_method) {
		this.payment_method = payment_method;
	}
	public DeliveryPricing getDelivery_pricing() {
		return delivery_pricing;
	}
	public void setDelivery_pricing(DeliveryPricing delivery_pricing) {
		this.delivery_pricing = delivery_pricing;
	}
	public FreightContract getFreight_contract() {
		return freight_contract;
	}
	public void setFreight_contract(FreightContract freight_contract) {
		this.freight_contract = freight_contract;
	}
	public String getDepartment_name() {
		return department_name;
	}
	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public IECategories getIe_categories() {
		return ie_categories;
	}
	public void setIe_categories(IECategories ie_categories) {
		this.ie_categories = ie_categories;
	}
	public String getIe_reason() {
		return ie_reason;
	}
	public void setIe_reason(String ie_reason) {
		this.ie_reason = ie_reason;
	}
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	public double getTax_value() {
		return tax_value;
	}
	public void setTax_value(double tax_value) {
		this.tax_value = tax_value;
	}
	public double getTax_edit() {
		return tax_edit;
	}
	public void setTax_edit(double tax_edit) {
		this.tax_edit = tax_edit;
	}
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public String getInvoice_serie() {
		return invoice_serie;
	}
	public void setInvoice_serie(String invoice_serie) {
		this.invoice_serie = invoice_serie;
	}
	public String getOrder_voucher() {
		return order_voucher;
	}
	public void setOrder_voucher(String order_voucher) {
		this.order_voucher = order_voucher;
	}
	public String getMovement_commands_no() {
		return movement_commands_no;
	}
	public void setMovement_commands_no(String movement_commands_no) {
		this.movement_commands_no = movement_commands_no;
	}
	public boolean isPayment() {
		return payment;
	}
	public void setPayment(boolean payment) {
		this.payment = payment;
	}
	public boolean isTimeout() {
		return timeout;
	}
	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}
	public PromotionProgram getPromotion_program() {
		return promotion_program;
	}
	public void setPromotion_program(PromotionProgram promotion_program) {
		this.promotion_program = promotion_program;
	}
	public PricingProgram getPricing_program() {
		return pricing_program;
	}
	public void setPricing_program(PricingProgram pricing_program) {
		this.pricing_program = pricing_program;
	}
	public HarborCategory getHarbor_category() {
		return harbor_category;
	}
	public void setHarbor_category(HarborCategory harbor_category) {
		this.harbor_category = harbor_category;
	}
	public Stocker getStocker() {
		return stocker;
	}
	public void setStocker(Stocker stocker) {
		this.stocker = stocker;
	}
	public Stevedore getStevedore() {
		return stevedore;
	}
	public void setStevedore(Stevedore stevedore) {
		this.stevedore = stevedore;
	}
	public FormUpGoods getForm_up_goods() {
		return form_up_goods;
	}
	public void setForm_up_goods(FormUpGoods form_up_goods) {
		this.form_up_goods = form_up_goods;
	}
	public OrderLix getOrder_lix() {
		return order_lix;
	}
	public void setOrder_lix(OrderLix order_lix) {
		this.order_lix = order_lix;
	}
	public String getPo_no() {
		return po_no;
	}
	public void setPo_no(String po_no) {
		this.po_no = po_no;
	}
	public String getNote2() {
		return note2;
	}
	public void setNote2(String note2) {
		this.note2 = note2;
	}
	public Carrier getCarrier() {
		return carrier;
	}
	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}
	public String getTransport_content() {
		return transport_content;
	}
	public void setTransport_content(String transport_content) {
		this.transport_content = transport_content;
	}
	public Date getDelivery_date() {
		return delivery_date;
	}
	public void setDelivery_date(Date delivery_date) {
		this.delivery_date = delivery_date;
	}
	public int getTime_up_goods() {
		return time_up_goods;
	}
	public void setTime_up_goods(int time_up_goods) {
		this.time_up_goods = time_up_goods;
	}
	public String getE_invoice_id() {
		return e_invoice_id;
	}
	public void setE_invoice_id(String e_invoice_id) {
		this.e_invoice_id = e_invoice_id;
	}
	public String getLookup_code() {
		return lookup_code;
	}
	public void setLookup_code(String lookup_code) {
		this.lookup_code = lookup_code;
	}
	public List<InvoiceDetail> getList_invoice_detail() {
		return list_invoice_detail;
	}
	public void setList_invoice_detail(List<InvoiceDetail> list_invoice_detail) {
		this.list_invoice_detail = list_invoice_detail;
	}
	public String getContent_qd() {
		return content_qd;
	}
	public void setContent_qd(String content_qd) {
		this.content_qd = content_qd;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public String getContract_no() {
		return contract_no;
	}
	public void setContract_no(String contract_no) {
		this.contract_no = contract_no;
	}
	public String getOrder_code() {
		return order_code;
	}
	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}
	public long getCreated_by_id() {
		return created_by_id;
	}
	public void setCreated_by_id(long created_by_id) {
		this.created_by_id = created_by_id;
	}
	public boolean isIpromotion() {
		return ipromotion;
	}
	public void setIpromotion(boolean ipromotion) {
		this.ipromotion = ipromotion;
	}
	public Invoice getInvoice_own() {
		return invoice_own;
	}
	public void setInvoice_own(Invoice invoice_own) {
		this.invoice_own = invoice_own;
	}
	@Override
	public Invoice clone() throws CloneNotSupportedException {
		return (Invoice)super.clone();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Invoice other = (Invoice) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
