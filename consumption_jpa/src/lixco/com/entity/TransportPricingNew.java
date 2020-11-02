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
@Table(name="transportpricingnew")
// đơn giá vận chuyển cho khách hàng
public class TransportPricingNew implements Serializable{
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
	private Customer customer;
	private  String destination;//nơi đến 
	private String des_print_bill;//nơi đến in trên bill;
	private double mileage;//số km
	private double unit_price_per_ton;// đơn giá mỗi tấn vận chuyển
	private double disable;//không sử dụng
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
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getDes_print_bill() {
		return des_print_bill;
	}
	public void setDes_print_bill(String des_print_bill) {
		this.des_print_bill = des_print_bill;
	}
	public double getMileage() {
		return mileage;
	}
	public void setMileage(double mileage) {
		this.mileage = mileage;
	}
	public double getUnit_price_per_ton() {
		return unit_price_per_ton;
	}
	public void setUnit_price_per_ton(double unit_price_per_ton) {
		this.unit_price_per_ton = unit_price_per_ton;
	}
	public double getDisable() {
		return disable;
	}
	public void setDisable(double disable) {
		this.disable = disable;
	}
	
	
	

}
