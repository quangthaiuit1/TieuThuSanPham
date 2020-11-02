package lixco.com.entity;

import java.io.Serializable;
import java.util.Date;

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
@Table(name="productpos")
public class ProductPos implements Serializable,Cloneable {
	//dùng để track xếp vị trí sản phẩm vào kho
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne(fetch=FetchType.LAZY)
	private Pos pos;//vị trí kho
	private String created_by;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created_date;
	private String last_modifed_by;
	@Temporal(TemporalType.TIMESTAMP)
	private Date last_modifed_date;
	private double quantity_import;//số lượng thùng nhập vào vị trí đó
	private double quantity_export;//số lượng thùng xuất tại vị trí đó
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "receipt_note_pos_detail_id", nullable = false)
	private GoodsReceiptNotePosDetail goods_receipt_note_pos_detail;//thuộc chi tiết phiếu nhập nào
	@ManyToOne(fetch=FetchType.LAZY)
	private BatchPos batch_pos;// lô hàng
	private int status;// 0 số lượng vị trí không được sử dụng,1 được sử dụng
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Pos getPos() {
		return pos;
	}
	public void setPos(Pos pos) {
		this.pos = pos;
	}
	public double getQuantity_import() {
		return quantity_import;
	}
	public void setQuantity_import(double quantity_import) {
		this.quantity_import = quantity_import;
	}
	public double getQuantity_export() {
		return quantity_export;
	}
	public void setQuantity_export(double quantity_export) {
		this.quantity_export = quantity_export;
	}
	public GoodsReceiptNotePosDetail getGoods_receipt_note_pos_detail() {
		return goods_receipt_note_pos_detail;
	}
	public void setGoods_receipt_note_pos_detail(GoodsReceiptNotePosDetail goods_receipt_note_pos_detail) {
		this.goods_receipt_note_pos_detail = goods_receipt_note_pos_detail;
	}
	public BatchPos getBatch_pos() {
		return batch_pos;
	}
	public void setBatch_pos(BatchPos batch_pos) {
		this.batch_pos = batch_pos;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getLast_modifed_by() {
		return last_modifed_by;
	}
	public void setLast_modifed_by(String last_modifed_by) {
		this.last_modifed_by = last_modifed_by;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public Date getLast_modifed_date() {
		return last_modifed_date;
	}
	public void setLast_modifed_date(Date last_modifed_date) {
		this.last_modifed_date = last_modifed_date;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductPos other = (ProductPos) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public ProductPos clone() throws CloneNotSupportedException {
		return (ProductPos)super.clone();
	}
	
}
