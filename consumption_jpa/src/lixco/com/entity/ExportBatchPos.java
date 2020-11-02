package lixco.com.entity;

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
@Table(name="exportbatchpos")
public class ExportBatchPos implements Cloneable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String created_by;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created_date;
	private String last_modifed_by;
	@Temporal(TemporalType.TIMESTAMP)
	private Date last_modifed_date;
	@ManyToOne(fetch=FetchType.LAZY)
	private InvoiceDetailPos invoice_detail_pos;
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductPos product_pos;
	private double quantity_export_box;//số lượng thùng xuất
	@Transient
	private boolean select;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public InvoiceDetailPos getInvoice_detail_pos() {
		return invoice_detail_pos;
	}
	public void setInvoice_detail_pos(InvoiceDetailPos invoice_detail_pos) {
		this.invoice_detail_pos = invoice_detail_pos;
	}
	public ProductPos getProduct_pos() {
		return product_pos;
	}
	public void setProduct_pos(ProductPos product_pos) {
		this.product_pos = product_pos;
	}
	public double getQuantity_export_box() {
		return quantity_export_box;
	}
	public void setQuantity_export_box(double quantity_export_box) {
		this.quantity_export_box = quantity_export_box;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public String getLast_modifed_by() {
		return last_modifed_by;
	}
	public void setLast_modifed_by(String last_modifed_by) {
		this.last_modifed_by = last_modifed_by;
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
		ExportBatchPos other = (ExportBatchPos) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public ExportBatchPos clone() throws CloneNotSupportedException {
		return (ExportBatchPos) super.clone();
	}
	
}
