package lixco.com.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="product")
public class Product implements Serializable,Cloneable{//sản phẩm
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(unique=true)
	private String product_code;
	@Temporal(TemporalType.TIMESTAMP)
	private Date created_date;
	private String created_by;
	@Temporal(TemporalType.TIMESTAMP)
	private Date last_modifed_date;
	private String last_modifed_by;
	private String product_name;// tên sản phẩm 
	private String en_name;// tên tiếng anh
	private String customs_name;//tên hải quan
	private String product_info; //thông tin sản phẩm
	private String unit;//đơn vị tính
	private String lever_code;//mã lever
	private double specification; //qui cách đóng gói.
	private double box_quantity;// số lượng thùng/pallet
	private double factor;// hệ số chuyển đổi
	private double tare;// trong luong bb thuc te
	private String packing_unit;// đơn vị đóng gói
	private boolean typep;//xuất khẩu hay nội đia
	private String other_code;//mã khác
	private double reserve_quantity;//số lượng dự trữ
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="product_com_id")
	private ProductCom product_com;//loại sản phẩm
	@ManyToOne(fetch=FetchType.LAZY)
	private Product promotion_product;//Sản phẩm khuyến mãi
	private boolean disable;//không còn sử dụng
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductType product_type;
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductGroup product_group;
	@ManyToOne(fetch=FetchType.LAZY)
	private PromotionProductGroup promotion_product_group;// nhóm sản phẩm khuyến mãi.
	
	public Product() {
	}
	public Product(long id, String product_code, String product_name) {
		this.id = id;
		this.product_code = product_code;
		this.product_name = product_name;
	}
	public Product(long id, String product_code, String product_name, double box_quantity) {
		super();
		this.id = id;
		this.product_code = product_code;
		this.product_name = product_name;
		this.box_quantity = box_quantity;
	}
	
	public Product(long id, String product_code, String product_name, double specification, double box_quantity,
			double factor, double tare) {
		this.id = id;
		this.product_code = product_code;
		this.product_name = product_name;
		this.specification = specification;
		this.box_quantity = box_quantity;
		this.factor = factor;
		this.tare = tare;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
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
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getEn_name() {
		return en_name;
	}
	public void setEn_name(String en_name) {
		this.en_name = en_name;
	}
	public String getCustoms_name() {
		return customs_name;
	}
	public void setCustoms_name(String customs_name) {
		this.customs_name = customs_name;
	}
	public String getProduct_info() {
		return product_info;
	}
	public void setProduct_info(String product_info) {
		this.product_info = product_info;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getLever_code() {
		return lever_code;
	}
	public void setLever_code(String lever_code) {
		this.lever_code = lever_code;
	}
	public double getSpecification() {
		return specification;
	}
	public void setSpecification(double specification) {
		this.specification = specification;
	}
	public double getBox_quantity() {
		return box_quantity;
	}
	public void setBox_quantity(double box_quantity) {
		this.box_quantity = box_quantity;
	}
	public double getFactor() {
		return factor;
	}
	public void setFactor(double factor) {
		this.factor = factor;
	}
	public double getTare() {
		return tare;
	}
	public void setTare(double tare) {
		this.tare = tare;
	}
	public String getPacking_unit() {
		return packing_unit;
	}
	public void setPacking_unit(String packing_unit) {
		this.packing_unit = packing_unit;
	}
	public boolean isTypep() {
		return typep;
	}
	public void setTypep(boolean typep) {
		this.typep = typep;
	}
	public String getOther_code() {
		return other_code;
	}
	public void setOther_code(String other_code) {
		this.other_code = other_code;
	}
	public double getReserve_quantity() {
		return reserve_quantity;
	}
	public void setReserve_quantity(double reserve_quantity) {
		this.reserve_quantity = reserve_quantity;
	}
	public boolean isDisable() {
		return disable;
	}
	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	public ProductCom getProduct_com() {
		return product_com;
	}
	public void setProduct_com(ProductCom product_com) {
		this.product_com = product_com;
	}
	public Product getPromotion_product() {
		return promotion_product;
	}
	public void setPromotion_product(Product promotion_product) {
		this.promotion_product = promotion_product;
	}
	public ProductType getProduct_type() {
		return product_type;
	}
	public void setProduct_type(ProductType product_type) {
		this.product_type = product_type;
	}
	public ProductGroup getProduct_group() {
		return product_group;
	}
	public void setProduct_group(ProductGroup product_group) {
		this.product_group = product_group;
	}
	public PromotionProductGroup getPromotion_product_group() {
		return promotion_product_group;
	}
	public void setPromotion_product_group(PromotionProductGroup promotion_product_group) {
		this.promotion_product_group = promotion_product_group;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public Product clone() throws CloneNotSupportedException {
		return (Product) super.clone();
	}
	

}
