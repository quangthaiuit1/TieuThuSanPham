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
@Table(name="defectiveproductnote")
public class DefectiveProductNote implements Serializable{//nhập xuất hàng đổi bể
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
	private String dpn_code;
	private String voucher_code;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dpn_date; 
	@ManyToOne(fetch=FetchType.LAZY)
	private Customer customer;
	@ManyToOne(fetch=FetchType.LAZY)
	private IECategories ie_categories;//danh mục xuất nhập
	@ManyToOne(fetch=FetchType.LAZY)
	private Car car;
	private String note;
	@OneToMany(fetch=FetchType.LAZY,mappedBy="defective_product_note")
	private List<DefectiveProductNoteDetail> list_defective_product_note_detail;
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
	public String getDpn_code() {
		return dpn_code;
	}
	public void setDpn_code(String dpn_code) {
		this.dpn_code = dpn_code;
	}
	public String getVoucher_code() {
		return voucher_code;
	}
	public void setVoucher_code(String voucher_code) {
		this.voucher_code = voucher_code;
	}
	public Date getDpn_date() {
		return dpn_date;
	}
	public void setDpn_date(Date dpn_date) {
		this.dpn_date = dpn_date;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public IECategories getIe_categories() {
		return ie_categories;
	}
	public void setIe_categories(IECategories ie_categories) {
		this.ie_categories = ie_categories;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public List<DefectiveProductNoteDetail> getList_defective_product_note_detail() {
		return list_defective_product_note_detail;
	}
	public void setList_defective_product_note_detail(List<DefectiveProductNoteDetail> list_defective_product_note_detail) {
		this.list_defective_product_note_detail = list_defective_product_note_detail;
	}
}
