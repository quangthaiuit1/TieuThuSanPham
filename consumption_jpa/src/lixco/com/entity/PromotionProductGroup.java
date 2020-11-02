package lixco.com.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="promotionproductgroup")
public class PromotionProductGroup implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String code;
	private String name;
	private String unit;
	private double carton_quantity;
	private String carton_unit;
	
	public PromotionProductGroup() {
	}
	
	public PromotionProductGroup(long id, String code, String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PromotionProductGroup other = (PromotionProductGroup) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getCarton_quantity() {
		return carton_quantity;
	}

	public void setCarton_quantity(double carton_quantity) {
		this.carton_quantity = carton_quantity;
	}

	public String getCarton_unit() {
		return carton_unit;
	}

	public void setCarton_unit(String carton_unit) {
		this.carton_unit = carton_unit;
	}

	@Override
	public PromotionProductGroup clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (PromotionProductGroup) super.clone();
	}
	
}
