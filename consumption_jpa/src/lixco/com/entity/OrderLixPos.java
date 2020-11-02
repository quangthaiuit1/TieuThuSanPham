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
@Table(name="orderlixpos")
public class OrderLixPos implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created_date;
	private long created_by_id;
	private String created_by;
	@Temporal(TemporalType.TIMESTAMP)
	private Date last_modifed_date;
	private String last_modifed_by;
	@Column(unique=true)
	private String order_code;//mã đơn hàng
	private String voucher_code;// số chứng từ.
	@Temporal(TemporalType.TIMESTAMP)
	private Date order_date;//ngày đặt hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private Customer customer;// khánh hàng
	@Temporal(TemporalType.TIMESTAMP)
	private Date delivery_date;//ngày giao hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private PromotionProgram promotion_program;//chương trình khuyến mãi
	@ManyToOne(fetch=FetchType.LAZY)
	private PricingProgram pricing_program; //chương trình định giá
	@ManyToOne(fetch=FetchType.LAZY)
	private PaymentMethod payment_method;// phương thức thanh toán
	@ManyToOne(fetch=FetchType.LAZY)
	private Car car;//xe
	private String invoice_no;//số hóa đơn
	private String contract_no;//số hợp đồng.
	@ManyToOne(fetch=FetchType.LAZY)
	private FreightContract freight_contract;//hợp đồng vận chuyển
	private String serie_no;//số serie
	private String cus_voucher;// chứng từ khách hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private Warehouse warehouse;// nhà kho
	private double tax_value;// hệ số thuế
	@ManyToOne(fetch=FetchType.LAZY)
	private IECategories ie_categories;// danh mục xuất nhập
	@ManyToOne(fetch=FetchType.LAZY)
	private DeliveryPricing delivery_pricing;//đơn giá vận chuyển
	private boolean delivered;//đã giao
	private String reason_not_delivered;//lý do không giao hàng
	private String note;
	private String note2;
	private String po_no;//số po
	@Temporal(TemporalType.TIMESTAMP)
	private Date s_up_goods_date;//ngày giờ bắt đầulên hàng
	@Temporal(TemporalType.TIMESTAMP)
	private Date c_up_goods_date;//ngày giờ lên hàng xong
	@OneToMany(fetch=FetchType.LAZY,mappedBy="order_lix_pos")
	private List<OrderDetailPos> list_order_detail_pos;
	@Transient
	private boolean flag_up;
	private int status;//0 phiếu tạm,1 đang giao hàng, 2 hoàn thành, 3 kết thúc đơn hàng
	public long getCreated_by_id() {
		return created_by_id;
	}
	public void setCreated_by_id(long created_by_id) {
		this.created_by_id = created_by_id;
	}
	public  long getId() {
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
	public String getOrder_code() {
		return order_code;
	}
	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}
	public String getVoucher_code() {
		return voucher_code;
	}
	public void setVoucher_code(String voucher_code) {
		this.voucher_code = voucher_code;
	}
	public Date getOrder_date() {
		return order_date;
	}
	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Date getDelivery_date() {
		return delivery_date;
	}
	public void setDelivery_date(Date delivery_date) {
		this.delivery_date = delivery_date;
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
	public PaymentMethod getPayment_method() {
		return payment_method;
	}
	public void setPayment_method(PaymentMethod payment_method) {
		this.payment_method = payment_method;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public String getInvoice_no() {
		return invoice_no;
	}
	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
	}
	public String getContract_no() {
		return contract_no;
	}
	public void setContract_no(String contract_no) {
		this.contract_no = contract_no;
	}
	public String getSerie_no() {
		return serie_no;
	}
	public void setSerie_no(String serie_no) {
		this.serie_no = serie_no;
	}
	public String getCus_voucher() {
		return cus_voucher;
	}
	public void setCus_voucher(String cus_voucher) {
		this.cus_voucher = cus_voucher;
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
	public IECategories getIe_categories() {
		return ie_categories;
	}
	public void setIe_categories(IECategories ie_categories) {
		this.ie_categories = ie_categories;
	}
	public boolean isDelivered() {
		return delivered;
	}
	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}
	public String getReason_not_delivered() {
		return reason_not_delivered;
	}
	public void setReason_not_delivered(String reason_not_delivered) {
		this.reason_not_delivered = reason_not_delivered;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getNote2() {
		return note2;
	}
	public void setNote2(String note2) {
		this.note2 = note2;
	}
	public String getPo_no() {
		return po_no;
	}
	public void setPo_no(String po_no) {
		this.po_no = po_no;
	}
	public Date getS_up_goods_date() {
		return s_up_goods_date;
	}
	public void setS_up_goods_date(Date s_up_goods_date) {
		this.s_up_goods_date = s_up_goods_date;
	}
	public Date getC_up_goods_date() {
		return c_up_goods_date;
	}
	public void setC_up_goods_date(Date c_up_goods_date) {
		this.c_up_goods_date = c_up_goods_date;
	}
	public List<OrderDetailPos> getList_order_detail_pos() {
		return list_order_detail_pos;
	}
	public void setList_order_detail_pos(List<OrderDetailPos> list_order_detail_pos) {
		this.list_order_detail_pos = list_order_detail_pos;
	}
	public FreightContract getFreight_contract() {
		return freight_contract;
	}
	public void setFreight_contract(FreightContract freight_contract) {
		this.freight_contract = freight_contract;
	}
	public DeliveryPricing getDelivery_pricing() {
		return delivery_pricing;
	}
	public void setDelivery_pricing(DeliveryPricing delivery_pricing) {
		this.delivery_pricing = delivery_pricing;
	}
	public boolean isFlag_up() {
		return flag_up;
	}
	public void setFlag_up(boolean flag_up) {
		this.flag_up = flag_up;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderLixPos other = (OrderLixPos) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public OrderLixPos clone() throws CloneNotSupportedException {
		return (OrderLixPos) super.clone();
	}
	
}
