package lixco.com.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name="contract")
public class Contract implements Serializable,Cloneable{
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
	@Column(unique=true,nullable=false)
	private String contract_code;//mã hợp đồng
	@Column(unique=true,nullable=false)
	private String voucher_code;//số chứng từ
	@Temporal(TemporalType.DATE)
	private Date contract_date;//ngày hợp đồng
	@Temporal(TemporalType.DATE)
	private Date effective_date;//ngày hiệu lực
	@Temporal(TemporalType.DATE)
	private Date expiry_date;//ngày hết hạn
	@ManyToOne(fetch=FetchType.LAZY)
	private Customer customer;// khánh hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private Currency currency;// đơn vị tiền tệ
	private boolean liquidated;//đã thanh lý
	private String note;
	@OneToMany(fetch=FetchType.LAZY,mappedBy="contract")
	private List<ContractDetail> contract_detail;
	public Contract() {
	}
	
	public Contract(long id, String contract_code, String voucher_code, Customer customer, Currency currency) {
		this.id = id;
		this.contract_code = contract_code;
		this.voucher_code = voucher_code;
		this.customer = customer;
		this.currency = currency;
	}

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
	public String getContract_code() {
		return contract_code;
	}
	public void setContract_code(String contract_code) {
		this.contract_code = contract_code;
	}
	public Date getContract_date() {
		return contract_date;
	}
	public void setContract_date(Date contract_date) {
		this.contract_date = contract_date;
	}
	public Date getEffective_date() {
		return effective_date;
	}
	public void setEffective_date(Date effective_date) {
		this.effective_date = effective_date;
	}
	public Date getExpiry_date() {
		return expiry_date;
	}
	public void setExpiry_date(Date expiry_date) {
		this.expiry_date = expiry_date;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public boolean isLiquidated() {
		return liquidated;
	}
	public void setLiquidated(boolean liquidated) {
		this.liquidated = liquidated;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public List<ContractDetail> getContract_detail() {
		return contract_detail;
	}
	public void setContract_detail(List<ContractDetail> contract_detail) {
		this.contract_detail = contract_detail;
	}
	public String getVoucher_code() {
		return voucher_code;
	}
	public void setVoucher_code(String voucher_code) {
		this.voucher_code = voucher_code;
	}
	@Override
	public Contract clone() throws CloneNotSupportedException {
		return (Contract) super.clone();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contract other = (Contract) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
