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
@Table(name="freightcontract")
public class FreightContract implements Serializable,Cloneable{// hợp đồng vận chuyển hàng hóa
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
	private String contract_code;//mã hợp đồng
	private String contract_no;//số hợp đồng
	@Temporal(TemporalType.DATE)
	private Date contract_date;//ngày trên hợp đồng
	@ManyToOne(fetch=FetchType.LAZY)
	private Customer customer;//khách hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private Car car;//số xe 
	@ManyToOne(fetch=FetchType.LAZY)
	private PaymentMethod payment_method;//phương thức thanh toán
	@Temporal(TemporalType.TIMESTAMP)
	private Date payment_date;//ngày thanh toán
	private boolean payment;//thanh toán
	private String note;//ghi chú
	private String report_note;//ghi chú trên report
	@OneToMany(fetch=FetchType.LAZY,mappedBy="freight_contract")
	private List<FreightContractDetail> list_freight_contract_detail;//danh sách chi tiết
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
	public String getContract_no() {
		return contract_no;
	}
	public void setContract_no(String contract_no) {
		this.contract_no = contract_no;
	}
	public Date getContract_date() {
		return contract_date;
	}
	public void setContract_date(Date contract_date) {
		this.contract_date = contract_date;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public PaymentMethod getPayment_method() {
		return payment_method;
	}
	public void setPayment_method(PaymentMethod payment_method) {
		this.payment_method = payment_method;
	}
	public Date getPayment_date() {
		return payment_date;
	}
	public void setPayment_date(Date payment_date) {
		this.payment_date = payment_date;
	}
	public boolean isPayment() {
		return payment;
	}
	public void setPayment(boolean payment) {
		this.payment = payment;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getReport_note() {
		return report_note;
	}
	public void setReport_note(String report_note) {
		this.report_note = report_note;
	}
	public List<FreightContractDetail> getList_freight_contract_detail() {
		return list_freight_contract_detail;
	}
	public void setList_freight_contract_detail(List<FreightContractDetail> list_freight_contract_detail) {
		this.list_freight_contract_detail = list_freight_contract_detail;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FreightContract other = (FreightContract) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public FreightContract clone() throws CloneNotSupportedException {
		return (FreightContract)super.clone();
	}
}
