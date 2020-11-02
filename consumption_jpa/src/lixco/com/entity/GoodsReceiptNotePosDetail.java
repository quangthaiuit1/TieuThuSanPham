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
@Table(name="goodsReceiptnoteposdetail")
public class GoodsReceiptNotePosDetail implements Serializable,Cloneable{
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
	private GoodsReceiptNotePos goods_receipt_note_pos;
	private double quantity;//số lượng tổng chưa chia vị trí
	@Transient
	private double quantity_real;
	@ManyToOne(fetch=FetchType.LAZY)
	private BatchPos batch_pos;//lô hàng
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
	public GoodsReceiptNotePos getGoods_receipt_note_pos() {
		return goods_receipt_note_pos;
	}
	public void setGoods_receipt_note_pos(GoodsReceiptNotePos goods_receipt_note_pos) {
		this.goods_receipt_note_pos = goods_receipt_note_pos;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public BatchPos getBatch_pos() {
		return batch_pos;
	}
	public void setBatch_pos(BatchPos batch_pos) {
		this.batch_pos = batch_pos;
	}
	public String getBatch_code() {
		return batch_code;
	}
	public void setBatch_code(String batch_code) {
		this.batch_code = batch_code;
	}
	public double getQuantity_real() {
		return quantity_real;
	}
	public void setQuantity_real(double quantity_real) {
		this.quantity_real = quantity_real;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoodsReceiptNotePosDetail other = (GoodsReceiptNotePosDetail) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public GoodsReceiptNotePosDetail clone() throws CloneNotSupportedException {
		return (GoodsReceiptNotePosDetail)super.clone();
	}
}
