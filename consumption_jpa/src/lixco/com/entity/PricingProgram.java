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
@Table(name="pricingprogram")
public class PricingProgram implements Serializable,Cloneable {
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
	private String program_code;
	private String voucher_code;//mã chứng từ
	@Temporal(TemporalType.TIMESTAMP)
	private Date effective_date;//ngày hiệu lực
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiry_date;//ngày kết thúc
	private String note;//ghi chú
	private boolean disable;// không còn sử dụng 
	@OneToMany(fetch=FetchType.LAZY,mappedBy="pricing_program")
	private List<PricingProgramDetail> list_pricing_program_detail;// list chi tiết chương trình đơn giá
	@ManyToOne(fetch=FetchType.LAZY)
	private PricingProgram parent_pricing_program;// chương trình đơn giá cha
	@Temporal(TemporalType.TIMESTAMP)
	private Date update_time;
	
	public PricingProgram() {
	}
	
	public PricingProgram(long id, String program_code, Date effective_date, Date expiry_date) {
		this.id = id;
		this.program_code = program_code;
		this.effective_date = effective_date;
		this.expiry_date = expiry_date;
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
	public String getProgram_code() {
		return program_code;
	}
	public void setProgram_code(String program_code) {
		this.program_code = program_code;
	}
	public String getVoucher_code() {
		return voucher_code;
	}
	public void setVoucher_code(String voucher_code) {
		this.voucher_code = voucher_code;
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
	public List<PricingProgramDetail> getList_pricing_program_detail() {
		return list_pricing_program_detail;
	}
	public void setList_pricing_program_detail(List<PricingProgramDetail> list_pricing_program_detail) {
		this.list_pricing_program_detail = list_pricing_program_detail;
	}
	public PricingProgram getParent_pricing_program() {
		return parent_pricing_program;
	}
	public void setParent_pricing_program(PricingProgram parent_pricing_program) {
		this.parent_pricing_program = parent_pricing_program;
	}
	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PricingProgram other = (PricingProgram) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public PricingProgram clone() throws CloneNotSupportedException {
		return (PricingProgram)super.clone();
	}
	
}
