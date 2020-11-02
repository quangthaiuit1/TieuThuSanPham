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
@Table(name="materialpricingprogramdetail")
public class MaterialPricingProgramDetail implements Serializable{
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
	private String material_code;//mã vật tư
	private String material_name;// tên vật tư
	private String unit;// đơn vị tính
	private String foreign_currency_type;// loại ngoại tệ
	private double foreign_currency_value;// tỉ giá qui đổi tiền ngoại tệ
	private double foreign_unit_price; //đơn giá ngoại
	private double unit_price;// đơn gia việt nam
	@ManyToOne(fetch=FetchType.LAZY)
	private MaterialPricingProgram material_pricing_program;// chương trình đơn giá vật tư
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
	public String getMaterial_code() {
		return material_code;
	}
	public void setMaterial_code(String material_code) {
		this.material_code = material_code;
	}
	public String getMaterial_name() {
		return material_name;
	}
	public void setMaterial_name(String material_name) {
		this.material_name = material_name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getForeign_currency_type() {
		return foreign_currency_type;
	}
	public void setForeign_currency_type(String foreign_currency_type) {
		this.foreign_currency_type = foreign_currency_type;
	}
	public double getForeign_currency_value() {
		return foreign_currency_value;
	}
	public void setForeign_currency_value(double foreign_currency_value) {
		this.foreign_currency_value = foreign_currency_value;
	}
	public double getForeign_unit_price() {
		return foreign_unit_price;
	}
	public void setForeign_unit_price(double foreign_unit_price) {
		this.foreign_unit_price = foreign_unit_price;
	}
	public double getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
	}
	public MaterialPricingProgram getMaterial_pricing_program() {
		return material_pricing_program;
	}
	public void setMaterial_pricing_program(MaterialPricingProgram material_pricing_program) {
		this.material_pricing_program = material_pricing_program;
	}
}
