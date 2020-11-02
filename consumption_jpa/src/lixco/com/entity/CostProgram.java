package lixco.com.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@Table(name="costprogram")
public class CostProgram implements Serializable {
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
	private String program_code;
	@ManyToOne(fetch=FetchType.LAZY)
	private Product product;// sản phẩm
	@ManyToOne(fetch=FetchType.LAZY)
	private CustomerTypes customer_types;// nhóm khách hàng
	@ManyToOne(fetch=FetchType.LAZY)
	private Currency currency;//đơn vị tiên tệ
	private double unit_price;//đơn giá bán
	@Temporal(TemporalType.TIMESTAMP)
	private Date effective_date;//ngày hiệu lực
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiry_date;//ngày hết hạn
	private String note;//ghi chú
	private boolean disable;//không sử dụng
	@OneToMany(fetch=FetchType.LAZY,mappedBy="cost_program")
	private List<CostProgramDetail> list_cost_program_detail;//danh sách chi tiết
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
	public String getProgram_code() {
		return program_code;
	}
	public void setProgram_code(String program_code) {
		this.program_code = program_code;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public CustomerTypes getCustomer_types() {
		return customer_types;
	}
	public void setCustomer_types(CustomerTypes customer_types) {
		this.customer_types = customer_types;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public double getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
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
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public boolean isDisable() {
		return disable;
	}
	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	public List<CostProgramDetail> getList_cost_program_detail() {
		return list_cost_program_detail;
	}
	public void setList_cost_program_detail(List<CostProgramDetail> list_cost_program_detail) {
		this.list_cost_program_detail = list_cost_program_detail;
	}
}
