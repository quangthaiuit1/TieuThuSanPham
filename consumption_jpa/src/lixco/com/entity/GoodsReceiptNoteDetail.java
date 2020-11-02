package lixco.com.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
@Entity
@Table(name="goodsReceiptnotedetail")
public class GoodsReceiptNoteDetail implements Serializable,Cloneable{// chi tiết phiếu nhập kho
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
	@ManyToOne(fetch=FetchType.LAZY)
	private GoodsReceiptNote goods_receipt_note;
	private double quantity;//số lượng
	private String tk_1;////tài khoản nợ
	private String tk_2;//tài khoản có 
	@OneToOne(fetch=FetchType.LAZY)
	private Batch batch;//lô hàng
	@Transient
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
	public GoodsReceiptNote getGoods_receipt_note() {
		return goods_receipt_note;
	}
	public void setGoods_receipt_note(GoodsReceiptNote goods_receipt_note) {
		this.goods_receipt_note = goods_receipt_note;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getTk_1() {
		return tk_1;
	}
	public void setTk_1(String tk_1) {
		this.tk_1 = tk_1;
	}
	public String getTk_2() {
		return tk_2;
	}
	public void setTk_2(String tk_2) {
		this.tk_2 = tk_2;
	}
	public Batch getBatch() {
		return batch;
	}
	public void setBatch(Batch batch) {
		this.batch = batch;
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
		GoodsReceiptNoteDetail other = (GoodsReceiptNoteDetail) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public GoodsReceiptNoteDetail clone() throws CloneNotSupportedException {
		return (GoodsReceiptNoteDetail)super.clone();
	}
	
	
}
