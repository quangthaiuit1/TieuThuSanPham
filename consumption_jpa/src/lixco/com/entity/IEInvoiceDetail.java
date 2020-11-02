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
@Table(name="ieinvoicedetail")
public class IEInvoiceDetail implements Serializable,Cloneable{
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
	private Product product;
	private double quantity_export;//số lượng xuất khẩu
	private double quantity;//số lượng làm tròn lên 2 chữ số của quantity export
	private double total_foreign_amount;// số tiền ngoại tệ 
	private double foreign_unit_price;//đơn giá ngoại tệ
	private double total_export_foreign_amount;//số tiền ngoại tệ xuất khẩu
	private double unit_price;// đơn giá
	private double total_amount;//số tiền
	private String order_no;// mã số đơn hàng
	private String container_no;// mã số container
	private String ft_container;// kích thước container
	private boolean locked;//khóa 
	private boolean printed;// in hay k
	private int container_number;// số container
	private String batch_code;// mã lô hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private IEInvoice ie_invoice;// hóa đơn bên nhập khẩu
	private String arrival_place;
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
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public double getQuantity_export() {
		return quantity_export;
	}
	public void setQuantity_export(double quantity_export) {
		this.quantity_export = quantity_export;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getTotal_foreign_amount() {
		return total_foreign_amount;
	}
	public void setTotal_foreign_amount(double total_foreign_amount) {
		this.total_foreign_amount = total_foreign_amount;
	}
	public double getForeign_unit_price() {
		return foreign_unit_price;
	}
	public void setForeign_unit_price(double foreign_unit_price) {
		this.foreign_unit_price = foreign_unit_price;
	}
	public double getTotal_export_foreign_amount() {
		return total_export_foreign_amount;
	}
	public void setTotal_export_foreign_amount(double total_export_foreign_amount) {
		this.total_export_foreign_amount = total_export_foreign_amount;
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
	public String getOrder_no() {
		return order_no;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	public String getContainer_no() {
		return container_no;
	}
	public void setContainer_no(String container_no) {
		this.container_no = container_no;
	}
	public String getFt_container() {
		return ft_container;
	}
	public void setFt_container(String ft_container) {
		this.ft_container = ft_container;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public boolean isPrinted() {
		return printed;
	}
	public void setPrinted(boolean printed) {
		this.printed = printed;
	}
	public int getContainer_number() {
		return container_number;
	}
	public void setContainer_number(int container_number) {
		this.container_number = container_number;
	}
	public String getBatch_code() {
		return batch_code;
	}
	public void setBatch_code(String batch_code) {
		this.batch_code = batch_code;
	}
	public IEInvoice getIe_invoice() {
		return ie_invoice;
	}
	public void setIe_invoice(IEInvoice ie_invoice) {
		this.ie_invoice = ie_invoice;
	}
	public String getArrival_place() {
		return arrival_place;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public void setArrival_place(String arrival_place) {
		this.arrival_place = arrival_place;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IEInvoiceDetail other = (IEInvoiceDetail) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public IEInvoiceDetail clone() throws CloneNotSupportedException {
		return (IEInvoiceDetail)super.clone();
	}
}
