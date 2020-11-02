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
@Table(name="batch")
public class Batch implements Serializable,Cloneable{
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
	private String batch_code;
	@ManyToOne(fetch=FetchType.LAZY)
	private Product product;// sản phẩm
	private double quantity_import;//số lượng
	private double quantity_export;//số lượng xuất
	@Temporal(TemporalType.DATE)
	private Date manufacture_date;//ngày sản xuất 
	@Temporal(TemporalType.DATE)
	private Date expiration_date;//ngày hết hạn
	private int status;//0 lô hàng tạm, 1 lo hàng đã hoàn thành
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

	public String getBatch_code() {
		return batch_code;
	}

	public void setBatch_code(String batch_code) {
		this.batch_code = batch_code;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

	public Date getManufacture_date() {
		return manufacture_date;
	}

	public void setManufacture_date(Date manufacture_date) {
		this.manufacture_date = manufacture_date;
	}

	public Date getExpiration_date() {
		return expiration_date;
	}

	public void setExpiration_date(Date expiration_date) {
		this.expiration_date = expiration_date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public Batch clone() throws CloneNotSupportedException {
		return (Batch) super.clone();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Batch other = (Batch) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
