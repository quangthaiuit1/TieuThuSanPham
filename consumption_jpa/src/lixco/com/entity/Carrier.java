package lixco.com.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name="carrier")
public class Carrier implements Serializable,Cloneable{// người vận chuyển
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
	private String carrier_code;
	private String carrier_name;
	private String note;
	
	public Carrier() {
	}
	public Carrier(long id, String carrier_code, String carrier_name) {
		this.id = id;
		this.carrier_code = carrier_code;
		this.carrier_name = carrier_name;
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
	public String getCarrier_code() {
		return carrier_code;
	}
	public void setCarrier_code(String carrier_code) {
		this.carrier_code = carrier_code;
	}
	public String getCarrier_name() {
		return carrier_name;
	}
	public void setCarrier_name(String carrier_name) {
		this.carrier_name = carrier_name;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	@Override
	public Carrier clone() throws CloneNotSupportedException {
		return (Carrier) super.clone();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Carrier other = (Carrier) obj;
		if (id != other.id)
			return false;
		return true;
	}
	

}
