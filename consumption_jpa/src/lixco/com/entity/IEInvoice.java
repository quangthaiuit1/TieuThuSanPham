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
import javax.persistence.Transient;
@Entity
@Table(name="ieinvoice")
public class IEInvoice implements Serializable,Cloneable{// Xuất xuất khẩu
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
	private String invoice_code;//mã hóa đơn cho hiển thị
	@Column(unique=true)
	private String voucher_code;// mã chứng từ
	@Temporal(TemporalType.DATE)
	private Date invoice_date;//ngày trên hóa đơn
	@ManyToOne(fetch=FetchType.LAZY)
	private Customer customer; //khách hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private Car car;//xe 
	@ManyToOne(fetch=FetchType.LAZY)
	private PaymentMethod payment_method;//phương thức thanh toán
	private boolean time_out;//ngoài giờ
	@ManyToOne(fetch=FetchType.LAZY)
	private IECategories ie_categories;// danh mục nhập xuất
	@ManyToOne(fetch=FetchType.LAZY)
	private Contract contract;//hợp đồng mua bán
	private String department_name;//đơn vị
	private String note;// ghi chú 
	private String ie_reason;//lý do xuất kho
	@ManyToOne(fetch=FetchType.LAZY)
	private Stevedore stevedore;// bốc xếp
	@ManyToOne(fetch=FetchType.LAZY)
	private FormUpGoods form_up_goods;//hình thức lên hàng
	private Date up_goods_date;//ngày lên hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private Warehouse warehouse;// kho xuất hàng
	private double tax_value;// thuế
	@Temporal(TemporalType.TIMESTAMP)
	private Date etd_date;// ngày etd
	private boolean paid;// đã thanh toán 
	private boolean delivered;// đã giao hàng
	private double exchange_rate;// tỉ giá ngoại tệ
	private String bill_no;//số hóa đơn
	private String declaration_code;// mã số tờ khai
	@ManyToOne(fetch=FetchType.LAZY)
	private HarborCategory harbor_category;// bến cảng xuất khẩu
	@ManyToOne(fetch=FetchType.LAZY)
	private Currency currency;// đơn vị tiền tệ
	private String account_b;
	private String reference_no;//
	private String shipped_per;//
	private String term_of_delivery;//thời hạn (diều khoản giao nhận)
	private String shipping_mark;//
	@ManyToOne(fetch=FetchType.LAZY)
	private HarborCategory post_of_tran;//port of transhipment
	private String freight;//cước
	private String driver_name;//tên tài xế 
	private String place_delivery;// địa điểm giao hàng
	private String place_discharge;//địa điểm nhận hàng
	@OneToMany(fetch=FetchType.LAZY, mappedBy="ie_invoice")
	private List<IEInvoiceDetail> list_ie_invoice_detail;
	@ManyToOne(fetch=FetchType.LAZY)
	private Invoice invoice;
	@ManyToOne(fetch=FetchType.LAZY)
	private Stocker stocker;//thủ kho
	private String payment;
	@Transient
	private boolean check;
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
	public boolean isTime_out() {
		return time_out;
	}
	public void setTime_out(boolean time_out) {
		this.time_out = time_out;
	}
	public IECategories getIe_categories() {
		return ie_categories;
	}
	public void setIe_categories(IECategories ie_categories) {
		this.ie_categories = ie_categories;
	}
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
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
	public String getIe_reason() {
		return ie_reason;
	}
	public void setIe_reason(String ie_reason) {
		this.ie_reason = ie_reason;
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
	public Date getUp_goods_date() {
		return up_goods_date;
	}
	public void setUp_goods_date(Date up_goods_date) {
		this.up_goods_date = up_goods_date;
	}
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	public Date getEtd_date() {
		return etd_date;
	}
	public void setEtd_date(Date etd_date) {
		this.etd_date = etd_date;
	}
	public boolean isPaid() {
		return paid;
	}
	public void setPaid(boolean paid) {
		this.paid = paid;
	}
	public boolean isDelivered() {
		return delivered;
	}
	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}
	public double getExchange_rate() {
		return exchange_rate;
	}
	public void setExchange_rate(double exchange_rate) {
		this.exchange_rate = exchange_rate;
	}
	public String getBill_no() {
		return bill_no;
	}
	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}
	public String getDeclaration_code() {
		return declaration_code;
	}
	public void setDeclaration_code(String declaration_code) {
		this.declaration_code = declaration_code;
	}
	public HarborCategory getHarbor_category() {
		return harbor_category;
	}
	public void setHarbor_category(HarborCategory harbor_category) {
		this.harbor_category = harbor_category;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public String getReference_no() {
		return reference_no;
	}
	public void setReference_no(String reference_no) {
		this.reference_no = reference_no;
	}
	public String getShipped_per() {
		return shipped_per;
	}
	public void setShipped_per(String shipped_per) {
		this.shipped_per = shipped_per;
	}
	public String getTerm_of_delivery() {
		return term_of_delivery;
	}
	public void setTerm_of_delivery(String term_of_delivery) {
		this.term_of_delivery = term_of_delivery;
	}
	public String getShipping_mark() {
		return shipping_mark;
	}
	public void setShipping_mark(String shipping_mark) {
		this.shipping_mark = shipping_mark;
	}
	public HarborCategory getPost_of_tran() {
		return post_of_tran;
	}
	public void setPost_of_tran(HarborCategory post_of_tran) {
		this.post_of_tran = post_of_tran;
	}
	
	public String getFreight() {
		return freight;
	}
	public void setFreight(String freight) {
		this.freight = freight;
	}
	public String getDriver_name() {
		return driver_name;
	}
	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}
	public String getPlace_delivery() {
		return place_delivery;
	}
	public void setPlace_delivery(String place_delivery) {
		this.place_delivery = place_delivery;
	}
	public String getPlace_discharge() {
		return place_discharge;
	}
	public void setPlace_discharge(String place_discharge) {
		this.place_discharge = place_discharge;
	}
	public List<IEInvoiceDetail> getList_ie_invoice_detail() {
		return list_ie_invoice_detail;
	}
	public void setList_ie_invoice_detail(List<IEInvoiceDetail> list_ie_invoice_detail) {
		this.list_ie_invoice_detail = list_ie_invoice_detail;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	public Stocker getStocker() {
		return stocker;
	}
	public void setStocker(Stocker stocker) {
		this.stocker = stocker;
	}
	public long getCreated_by_id() {
		return created_by_id;
	}
	public void setCreated_by_id(long created_by_id) {
		this.created_by_id = created_by_id;
	}
	public double getTax_value() {
		return tax_value;
	}
	public void setTax_value(double tax_value) {
		this.tax_value = tax_value;
	}
	public String getAccount_b() {
		return account_b;
	}
	public void setAccount_b(String account_b) {
		this.account_b = account_b;
	}
	public String getPayment() {
		return payment;
	}
	public void setPayment(String payment) {
		this.payment = payment;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IEInvoice other = (IEInvoice) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public IEInvoice clone() throws CloneNotSupportedException {
		return (IEInvoice)super.clone();
	}
	
}
