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
@Table(name="promotionprogramdetail")
public class PromotionProgramDetail implements Serializable,Cloneable{
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
	private Product product;//sản phẩm áp dụng chương trình khuyến mãi
	@ManyToOne(fetch=FetchType.LAZY)
	private PromotionProgram promotion_program;
	@ManyToOne(fetch=FetchType.LAZY)
	private Product promotion_product;//sản phẩm được khuyến mãi 
	private double box_quatity;//số lượng thùng mua
	private double specification;//số đơn vị sản phẩm cái này tự nhập vô ví dụ mua 20 chai được tặng 1 chai
	private double promotion_quantity;//số sản phẩm khuyến mãi
	private int promotion_form;
	
	public PromotionProgramDetail() {
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
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public PromotionProgram getPromotion_program() {
		return promotion_program;
	}
	public void setPromotion_program(PromotionProgram promotion_program) {
		this.promotion_program = promotion_program;
	}
	public Product getPromotion_product() {
		return promotion_product;
	}
	public void setPromotion_product(Product promotion_product) {
		this.promotion_product = promotion_product;
	}
	public double getBox_quatity() {
		return box_quatity;
	}
	public void setBox_quatity(double box_quatity) {
		this.box_quatity = box_quatity;
	}
	public double getPromotion_quantity() {
		return promotion_quantity;
	}
	public void setPromotion_quantity(double promotion_quantity) {
		this.promotion_quantity = promotion_quantity;
	}
	public int getPromotion_form() {
		return promotion_form;
	}
	public void setPromotion_form(int promotion_form) {
		this.promotion_form = promotion_form;
	}
	
	public double getSpecification() {
		return specification;
	}
	public void setSpecification(double specification) {
		this.specification = specification;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PromotionProgramDetail other = (PromotionProgramDetail) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public PromotionProgramDetail clone() throws CloneNotSupportedException {
		try {
			PromotionProgramDetail clonedMyClass = (PromotionProgramDetail) super.clone();
			return clonedMyClass;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return new PromotionProgramDetail();
		}
	}
	
	
}
