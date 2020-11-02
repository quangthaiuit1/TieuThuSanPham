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
@Table(name="voucherpayment")
public class VoucherPayment implements Serializable,Cloneable{
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
	private String voucher_code;//số chứng từ.
	@ManyToOne(fetch=FetchType.LAZY)
	private Customer payment_customer;//đơn vị trả tiền
	@ManyToOne(fetch=FetchType.LAZY)
	private Customer receiver_customer;// đơn vị nhận tiền
	@Temporal(TemporalType.DATE)
	private Date payment_date;// ngày
	@ManyToOne(fetch=FetchType.LAZY)
	private Contract contract;// hợp đồng
	@ManyToOne(fetch=FetchType.LAZY)
	private Currency currency;// đơn vị tiền tệ.
	private double total_amount;// số tiền.
	private String note;
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
	public String getVoucher_code() {
		return voucher_code;
	}
	public void setVoucher_code(String voucher_code) {
		this.voucher_code = voucher_code;
	}
	public Customer getPayment_customer() {
		return payment_customer;
	}
	public void setPayment_customer(Customer payment_customer) {
		this.payment_customer = payment_customer;
	}
	public Customer getReceiver_customer() {
		return receiver_customer;
	}
	public void setReceiver_customer(Customer receiver_customer) {
		this.receiver_customer = receiver_customer;
	}
	public Date getPayment_date() {
		return payment_date;
	}
	public void setPayment_date(Date payment_date) {
		this.payment_date = payment_date;
	}
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public double getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VoucherPayment other = (VoucherPayment) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public VoucherPayment clone() throws CloneNotSupportedException {
		return (VoucherPayment) super.clone();
	}
	
}
