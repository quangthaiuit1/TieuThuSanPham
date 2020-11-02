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
@Table(name="freightcontractdetail")
public class FreightContractDetail implements Serializable,Cloneable{// chi tiết hợp đồng vận chuyển
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
	private ProductType  product_type;//loại sản phẩm
	private double quantity;//số lượng
	private double unit_price;//đơn giá 
	private double total_amount;//tổng cộng
	private double sup_unit_price;//đơn giá hổ trợ
	private boolean gc;// sản phẩm gc
	@ManyToOne(fetch=FetchType.LAZY)
	private FreightContract freight_contract;//hợp đồng vận chuyển
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
	public ProductType getProduct_type() {
		return product_type;
	}
	public void setProduct_type(ProductType product_type) {
		this.product_type = product_type;
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
	public double getSup_unit_price() {
		return sup_unit_price;
	}
	public void setSup_unit_price(double sup_unit_price) {
		this.sup_unit_price = sup_unit_price;
	}
	public boolean isGc() {
		return gc;
	}
	public void setGc(boolean gc) {
		this.gc = gc;
	}
	public FreightContract getFreight_contract() {
		return freight_contract;
	}
	public void setFreight_contract(FreightContract freight_contract) {
		this.freight_contract = freight_contract;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FreightContractDetail other = (FreightContractDetail) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public FreightContractDetail clone() throws CloneNotSupportedException {
		return (FreightContractDetail) super.clone();
	}
}
