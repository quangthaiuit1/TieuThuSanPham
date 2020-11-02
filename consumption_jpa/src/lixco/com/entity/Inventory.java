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
@Table(name="inventory")
public class Inventory implements Serializable{
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
	private Product product;
	private double opening_balance;//số dư đầu kỳ
	private double opening_amount;// số tiền đầu đầu
	private double import_quantity;//số lượng nhập
	private double import_amount;//số tiền nhập
	private double export_qauntity;//số lượng xuất
	private double export_amount;//số tiền xuất
	private double closing_balance;// số dư cuối kỳ
	private double closing_amount;//số tiền cuối kỳ
	private int inventory_month;// tháng
	private int inventory_year;//năm
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
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public double getOpening_balance() {
		return opening_balance;
	}
	public void setOpening_balance(double opening_balance) {
		this.opening_balance = opening_balance;
	}
	public double getOpening_amount() {
		return opening_amount;
	}
	public void setOpening_amount(double opening_amount) {
		this.opening_amount = opening_amount;
	}
	public double getImport_quantity() {
		return import_quantity;
	}
	public void setImport_quantity(double import_quantity) {
		this.import_quantity = import_quantity;
	}
	public double getImport_amount() {
		return import_amount;
	}
	public void setImport_amount(double import_amount) {
		this.import_amount = import_amount;
	}
	public double getExport_qauntity() {
		return export_qauntity;
	}
	public void setExport_qauntity(double export_qauntity) {
		this.export_qauntity = export_qauntity;
	}
	public double getExport_amount() {
		return export_amount;
	}
	public void setExport_amount(double export_amount) {
		this.export_amount = export_amount;
	}
	public double getClosing_balance() {
		return closing_balance;
	}
	public void setClosing_balance(double closing_balance) {
		this.closing_balance = closing_balance;
	}
	public double getClosing_amount() {
		return closing_amount;
	}
	public void setClosing_amount(double closing_amount) {
		this.closing_amount = closing_amount;
	}
	public int getInventory_month() {
		return inventory_month;
	}
	public void setInventory_month(int inventory_month) {
		this.inventory_month = inventory_month;
	}
	public int getInventory_year() {
		return inventory_year;
	}
	public void setInventory_year(int inventory_year) {
		this.inventory_year = inventory_year;
	}
}
