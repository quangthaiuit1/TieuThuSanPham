package lixco.com.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
@Entity
@Table(name="exportbatch")
public class ExportBatch  implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne(fetch=FetchType.LAZY)
	private InvoiceDetail invoice_detail;
	@ManyToOne(fetch=FetchType.LAZY)
	private Batch batch;
	private double quantity;//số lượng xuất
	@Transient
	private boolean select;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public InvoiceDetail getInvoice_detail() {
		return invoice_detail;
	}
	public void setInvoice_detail(InvoiceDetail invoice_detail) {
		this.invoice_detail = invoice_detail;
	}
	public Batch getBatch() {
		return batch;
	}
	public void setBatch(Batch batch) {
		this.batch = batch;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportBatch other = (ExportBatch) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public ExportBatch clone() throws CloneNotSupportedException {
		return (ExportBatch) super.clone();
	}
}
