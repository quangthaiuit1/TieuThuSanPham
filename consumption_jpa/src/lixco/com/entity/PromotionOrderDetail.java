package lixco.com.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name="promotionorderdetail")
public class PromotionOrderDetail implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created_date;
	private String created_by;
	@Temporal(TemporalType.TIMESTAMP)
	private Date last_modifed_date;
	private String last_modifed_by;
	private String productdh_code;//mã sản phẩm đơn hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private Product product;//sản phẩm khuyến mãi
	private double quantity;//số lượng
	private double unit_price;//đơn giá
	private double total_amount;//số tiền
	private String specification;//qui cách thông số <=> mã số
	private String productkm_code;//mã sản phẩm khuyến mãi .
	private String note;//ghi chú
	private int order_number;//số thứ tự hd
	private boolean owe;// nợ hay không.
	@ManyToOne(fetch=FetchType.LAZY)
	private OrderDetail order_detail;
	private String batch_code;
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
	public String getProductdh_code() {
		return productdh_code;
	}
	public void setProductdh_code(String productdh_code) {
		this.productdh_code = productdh_code;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
	}
	public double getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	public String getProductkm_code() {
		return productkm_code;
	}
	public void setProductkm_code(String productkm_code) {
		this.productkm_code = productkm_code;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public int getOrder_number() {
		return order_number;
	}
	public void setOrder_number(int order_number) {
		this.order_number = order_number;
	}
	public boolean isOwe() {
		return owe;
	}
	public void setOwe(boolean owe) {
		this.owe = owe;
	}
	public OrderDetail getOrder_detail() {
		return order_detail;
	}
	public void setOrder_detail(OrderDetail order_detail) {
		this.order_detail = order_detail;
	}
	public String getBatch_code() {
		return batch_code;
	}
	public void setBatch_code(String batch_code) {
		this.batch_code = batch_code;
	}
	@Override
	public PromotionOrderDetail clone() throws CloneNotSupportedException {
		return (PromotionOrderDetail)super.clone();
	}
}
