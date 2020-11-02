package lixco.com.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
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
@Table(name="productcom")
public class ProductCom implements Serializable{// loại sản phẩm
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
	@Column(unique=true)
	private String pcom_code;// mã danh mục sản phẩm
	private String pcom_name;//tên danh mục sản phẩm
	private String unit;// đơn vị tính
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductBrand product_brand;// Danh mục brand
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductNorm product_norm;//sản phẩm định mức
	private boolean disable;
	public final long getId() {
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
	public String getPcom_code() {
		return pcom_code;
	}
	public void setPcom_code(String pcom_code) {
		this.pcom_code = pcom_code;
	}
	public String getPcom_name() {
		return pcom_name;
	}
	public void setPcom_name(String pcom_name) {
		this.pcom_name = pcom_name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public ProductBrand getProduct_brand() {
		return product_brand;
	}
	public void setProduct_brand(ProductBrand product_brand) {
		this.product_brand = product_brand;
	}
	public ProductNorm getProduct_norm() {
		return product_norm;
	}
	public void setProduct_norm(ProductNorm product_norm) {
		this.product_norm = product_norm;
	}
	public boolean isDisable() {
		return disable;
	}
	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductCom other = (ProductCom) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
