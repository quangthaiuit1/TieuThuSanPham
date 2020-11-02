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
import javax.persistence.Transient;
@Entity
@Table(name="orderdetail")
public class OrderDetail implements Serializable,Cloneable{
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
	@ManyToOne(fetch=FetchType.LAZY)
	private Product product;// sản phẩm
	private double box_quantity;//số lượng thùng
	private double realbox_quantity;//số lượng thực xuất  thực tế
	private double quantity;//số lượng
	private double unit_price;//đơn giá
	private double total_amount;//số tiền
	private String note;//qui cách text thông số 
	private int promotion_forms;// hình thức khuyến mãi
	@ManyToOne(fetch=FetchType.LAZY)
	private OrderLix order_lix;
	@Transient
	private boolean flag_up=false;
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
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public double getBox_quantity() {
		return box_quantity;
	}
	public void setBox_quantity(double box_quantity) {
		this.box_quantity = box_quantity;
	}
	public double getRealbox_quantity() {
		return realbox_quantity;
	}
	public void setRealbox_quantity(double realbox_quantity) {
		this.realbox_quantity = realbox_quantity;
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
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public int getPromotion_forms() {
		return promotion_forms;
	}
	public void setPromotion_forms(int promotion_forms) {
		this.promotion_forms = promotion_forms;
	}
	public OrderLix getOrder_lix() {
		return order_lix;
	}
	public void setOrder_lix(OrderLix order_lix) {
		this.order_lix = order_lix;
	}
	public boolean isFlag_up() {
		return flag_up;
	}
	public void setFlag_up(boolean flag_up) {
		this.flag_up = flag_up;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getBatch_code() {
		return batch_code;
	}
	public void setBatch_code(String batch_code) {
		this.batch_code = batch_code;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderDetail other = (OrderDetail) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public OrderDetail clone() throws CloneNotSupportedException {
		return (OrderDetail)super.clone();
	}
	
}
