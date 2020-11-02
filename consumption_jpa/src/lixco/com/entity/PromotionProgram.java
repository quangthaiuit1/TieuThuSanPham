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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name="promotionprogram")
public class PromotionProgram implements Serializable,Cloneable{
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
	private String program_code;//mã chương trình
	private String voucher_code;//mã chứng từ
	@Temporal(TemporalType.TIMESTAMP)
	private Date effective_date;//ngày hiệu lực
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiry_date;//ngày kết thúc
	private boolean disable;//không sử dụng
	private String note;
	@OneToMany(fetch=FetchType.LAZY,mappedBy="promotion_program")
	private List<PromotionProgramDetail> list_promotion_program_detail;
	
	public PromotionProgram() {
	}
	public PromotionProgram(long id, String program_code, Date effective_date, Date expiry_date) {
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
	public boolean isDisable() {
		return disable;
	}
	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public List<PromotionProgramDetail> getList_promotion_program_detail() {
		return list_promotion_program_detail;
	}
	public void setList_promotion_program_detail(List<PromotionProgramDetail> list_promotion_program_detail) {
		this.list_promotion_program_detail = list_promotion_program_detail;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PromotionProgram other = (PromotionProgram) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public PromotionProgram clone() throws CloneNotSupportedException {
		return (PromotionProgram)super.clone();
	}

}
