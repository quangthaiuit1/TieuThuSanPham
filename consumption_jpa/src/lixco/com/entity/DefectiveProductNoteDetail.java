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
@Table(name="defectiveproductnotedetail")
public class DefectiveProductNoteDetail implements Serializable{
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
	private double quantity;
	private double unit_price;
	private double total_amount;
	private String specification;//qui cách text/thông số
	private String batch_code;//mã lô hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private Batch batch;//lo hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private DefectiveProductNote defective_product_note;
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
	public double getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	public String getBatch_code() {
		return batch_code;
	}
	public void setBatch_code(String batch_code) {
		this.batch_code = batch_code;
	}
	public DefectiveProductNote getDefective_product_note() {
		return defective_product_note;
	}
	public void setDefective_product_note(DefectiveProductNote defective_product_note) {
		this.defective_product_note = defective_product_note;
	}
	public Batch getBatch() {
		return batch;
	}
	public void setBatch(Batch batch) {
		this.batch = batch;
	}
	
}
