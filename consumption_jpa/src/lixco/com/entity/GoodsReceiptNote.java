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
@Table(name="goodsreceiptnote")
//phiếu nhập kho
public class GoodsReceiptNote implements Serializable,Cloneable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created_date;
	private String created_by;
	private long created_by_id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date last_modifed_date;
	private String last_modifed_by;
	private String voucher_code;// mã chứng từ
	@Column(unique=true,nullable=false)
	private String receipt_code;
	@Temporal(TemporalType.DATE)
	private Date import_date;// ngày nhập kho
	@ManyToOne(fetch=FetchType.LAZY)
	private Customer customer;// khách hành
	@ManyToOne(fetch=FetchType.LAZY)
	private IECategories ie_categories;// danh mục xuất nhập
	@ManyToOne(fetch=FetchType.LAZY)
	private Warehouse warehouse;// kho 
	private String vcnb_invoice_code;// mã hóa đơn vcnb
	private String license_plate;// biển số xe
	private String batch_code;//mã lô hàng
	private String movement_commands;//lệnh điều động
	private String note;//lý do nhập
	@OneToMany(fetch=FetchType.LAZY,mappedBy="goods_receipt_note")
	private List<GoodsReceiptNoteDetail> list_goods_receipt_note_detail;// danh chi tiết phiếu nhập
	private int status;// 0 phiếu tạm,1 phiếu hoàn thành
	private String tk_1;////tài khoản nợ
	private String tk_2;//tài khoản có 
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
	public long getCreated_by_id() {
		return created_by_id;
	}
	public void setCreated_by_id(long created_by_id) {
		this.created_by_id = created_by_id;
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
	public Date getImport_date() {
		return import_date;
	}
	public void setImport_date(Date import_date) {
		this.import_date = import_date;
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
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	public String getVcnb_invoice_code() {
		return vcnb_invoice_code;
	}
	public void setVcnb_invoice_code(String vcnb_invoice_code) {
		this.vcnb_invoice_code = vcnb_invoice_code;
	}
	public String getLicense_plate() {
		return license_plate;
	}
	public void setLicense_plate(String license_plate) {
		this.license_plate = license_plate;
	}
	public String getBatch_code() {
		return batch_code;
	}
	public void setBatch_code(String batch_code) {
		this.batch_code = batch_code;
	}
	public String getMovement_commands() {
		return movement_commands;
	}
	public void setMovement_commands(String movement_commands) {
		this.movement_commands = movement_commands;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public List<GoodsReceiptNoteDetail> getList_goods_receipt_note_detail() {
		return list_goods_receipt_note_detail;
	}
	public void setList_goods_receipt_note_detail(List<GoodsReceiptNoteDetail> list_goods_receipt_note_detail) {
		this.list_goods_receipt_note_detail = list_goods_receipt_note_detail;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getReceipt_code() {
		return receipt_code;
	}
	public void setReceipt_code(String receipt_code) {
		this.receipt_code = receipt_code;
	}
	public String getTk_1() {
		return tk_1;
	}
	public void setTk_1(String tk_1) {
		this.tk_1 = tk_1;
	}
	public String getTk_2() {
		return tk_2;
	}
	public void setTk_2(String tk_2) {
		this.tk_2 = tk_2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoodsReceiptNote other = (GoodsReceiptNote) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public GoodsReceiptNote clone() throws CloneNotSupportedException {
		return (GoodsReceiptNote) super.clone();
	}
	
}
