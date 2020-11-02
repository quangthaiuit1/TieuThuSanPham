package lixco.com.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="floor")
public class Floor implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String floor_code;
	private String floor_name;
	@ManyToOne(fetch=FetchType.LAZY)
	private RowStack row_stack;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFloor_code() {
		return floor_code;
	}
	public void setFloor_code(String floor_code) {
		this.floor_code = floor_code;
	}
	public String getFloor_name() {
		return floor_name;
	}
	public void setFloor_name(String floor_name) {
		this.floor_name = floor_name;
	}
	public RowStack getRow_stack() {
		return row_stack;
	}
	public void setRow_stack(RowStack row_stack) {
		this.row_stack = row_stack;
	}
	@Override
	public Floor clone() throws CloneNotSupportedException {
		return (Floor) super.clone();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Floor other = (Floor) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
