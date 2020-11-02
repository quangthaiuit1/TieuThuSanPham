package lixco.com.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name="invoicedetailpos")
public class InvoiceDetailPos implements Serializable,Cloneable{
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
	private Product product;//sản phẩm
	private double quantity;// số lượng
	private double unit_price;// đơn giá
	private double real_quantity;//số lượng nhập xuất thực tế
	private String note;//ghi chú
	private String productdh_code;//mã sản phẩm đơn hàng
	@Column(insertable=false, updatable=false)
	private Long detail_own_id;// ckey hdbh
	private double foreign_unit_price;//đơn giá ngoại tệ
	private double total_foreign_amount;//số tiền ngoại tệ
	@ManyToOne(fetch=FetchType.LAZY)
	private InvoicePos invoice_pos;
	private boolean ex_order;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="detail_own_id")
	private InvoiceDetailPos invoice_detail_pos_own;//áp dụng cho trường hợp sản phẩm khuyển mãi thuộc chi tiết hóa đơn chính nào.
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
	public double getReal_quantity() {
		return real_quantity;
	}
	public void setReal_quantity(double real_quantity) {
		this.real_quantity = real_quantity;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getProductdh_code() {
		return productdh_code;
	}
	public void setProductdh_code(String productdh_code) {
		this.productdh_code = productdh_code;
	}
	public Long getDetail_own_id() {
		return detail_own_id;
	}
	public void setDetail_own_id(Long detail_own_id) {
		this.detail_own_id = detail_own_id;
	}
	public double getForeign_unit_price() {
		return foreign_unit_price;
	}
	public void setForeign_unit_price(double foreign_unit_price) {
		this.foreign_unit_price = foreign_unit_price;
	}
	public double getTotal_foreign_amount() {
		return total_foreign_amount;
	}
	public void setTotal_foreign_amount(double total_foreign_amount) {
		this.total_foreign_amount = total_foreign_amount;
	}
	public InvoicePos getInvoice_pos() {
		return invoice_pos;
	}
	public void setInvoice_pos(InvoicePos invoice_pos) {
		this.invoice_pos = invoice_pos;
	}
	public boolean isEx_order() {
		return ex_order;
	}
	public void setEx_order(boolean ex_order) {
		this.ex_order = ex_order;
	}
	public InvoiceDetailPos getInvoice_detail_pos_own() {
		return invoice_detail_pos_own;
	}
	public void setInvoice_detail_pos_own(InvoiceDetailPos invoice_detail_pos_own) {
		this.invoice_detail_pos_own = invoice_detail_pos_own;
	}
	@Override
	public InvoiceDetailPos clone() throws CloneNotSupportedException {
		return (InvoiceDetailPos)super.clone();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InvoiceDetailPos other = (InvoiceDetailPos) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
