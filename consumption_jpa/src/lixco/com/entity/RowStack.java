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
@Table(name="rowstack")
public class RowStack implements Serializable,Cloneable{//dãy kho 
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String row_code;
	private String row_name;
	@ManyToOne(fetch=FetchType.LAZY)
	private Warehouse warehouse;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRow_code() {
		return row_code;
	}
	public void setRow_code(String row_code) {
		this.row_code = row_code;
	}
	public String getRow_name() {
		return row_name;
	}
	public void setRow_name(String row_name) {
		this.row_name = row_name;
	}
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	@Override
	public RowStack clone() throws CloneNotSupportedException {
		return (RowStack) super.clone();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RowStack other = (RowStack) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
