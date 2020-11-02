package lixco.com.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
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
@Table(name="harborcategory")
public class HarborCategory implements Serializable,Cloneable{// danh mục hải cảng
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
	private String harbor_code;//mã cảng
	private String harbor_code_old;//mã cảng cũ
	private String harbor_name;//tên cảng 
	private String address;//địa chỉ
	private int harbor_type;//0 cang xk,1 cảng lever
	@ManyToOne(fetch=FetchType.LAZY)
	private Country country;
	private String port_no;//mã cổng
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
	public String getHarbor_code() {
		return harbor_code;
	}
	public void setHarbor_code(String harbor_code) {
		this.harbor_code = harbor_code;
	}
	public String getHarbor_name() {
		return harbor_name;
	}
	public void setHarbor_name(String harbor_name) {
		this.harbor_name = harbor_name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getHarbor_type() {
		return harbor_type;
	}
	public void setHarbor_type(int harbor_type) {
		this.harbor_type = harbor_type;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public String getPort_no() {
		return port_no;
	}
	public void setPort_no(String port_no) {
		this.port_no = port_no;
	}
	public String getHarbor_code_old() {
		return harbor_code_old;
	}
	public void setHarbor_code_old(String harbor_code_old) {
		this.harbor_code_old = harbor_code_old;
	}
	@Override
	public HarborCategory clone() throws CloneNotSupportedException {
		return (HarborCategory)super.clone();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HarborCategory other = (HarborCategory) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
