package trong.lixco.com.entity;


import java.util.Date;

import javax.persistence.Entity;

@Entity
public class Employee extends AbstractEntity{
	
	private String hoten;
	private String toSx;
	private double hsDiem;
	private String sanLuongTo;
	private double luongSP;
	private double luongATVSLD;
	private double luongKPI;
	private double tongLuongNgay;
	private double luongLuyKeTuDauThang;
	private Date ngayHienTai;
	private String codeEmployee;
	private String codeEmployeeOld;
	private String created_by_name;
	private String created_by_code;
	private String code_department;
	private Long request_by_id;
	private Long department_id;
	private double phucap_ca3;
	private double diemNgay;
	private double diemCa3;
	private String codeTo;
	public String getHoten() {
		return hoten;
	}
	public void setHoten(String hoten) {
		this.hoten = hoten;
	}
	public String getToSx() {
		return toSx;
	}
	public void setToSx(String toSx) {
		this.toSx = toSx;
	}
	public double getHsDiem() {
		return hsDiem;
	}
	public void setHsDiem(double hsDiem) {
		this.hsDiem = hsDiem;
	}
	public String getSanLuongTo() {
		return sanLuongTo;
	}
	public void setSanLuongTo(String sanLuongTo) {
		this.sanLuongTo = sanLuongTo;
	}
	public double getLuongSP() {
		return luongSP;
	}
	public void setLuongSP(double luongSP) {
		this.luongSP = luongSP;
	}
	public double getLuongATVSLD() {
		return luongATVSLD;
	}
	public void setLuongATVSLD(double luongATVSLD) {
		this.luongATVSLD = luongATVSLD;
	}
	public double getLuongKPI() {
		return luongKPI;
	}
	public void setLuongKPI(double luongKPI) {
		this.luongKPI = luongKPI;
	}
	public double getTongLuongNgay() {
		return tongLuongNgay;
	}
	public void setTongLuongNgay(double tongLuongNgay) {
		this.tongLuongNgay = tongLuongNgay;
	}
	public double getLuongLuyKeTuDauThang() {
		return luongLuyKeTuDauThang;
	}
	public void setLuongLuyKeTuDauThang(double luongLuyKeTuDauThang) {
		this.luongLuyKeTuDauThang = luongLuyKeTuDauThang;
	}
	public Date getNgayHienTai() {
		return ngayHienTai;
	}
	public void setNgayHienTai(Date ngayHienTai) {
		this.ngayHienTai = ngayHienTai;
	}
	public String getCodeEmployee() {
		return codeEmployee;
	}
	public void setCodeEmployee(String codeEmployee) {
		this.codeEmployee = codeEmployee;
	}
	public String getCreated_by_name() {
		return created_by_name;
	}
	public void setCreated_by_name(String created_by_name) {
		this.created_by_name = created_by_name;
	}
	public String getCreated_by_code() {
		return created_by_code;
	}
	public void setCreated_by_code(String created_by_code) {
		this.created_by_code = created_by_code;
	}
	public String getCode_department() {
		return code_department;
	}
	public void setCode_department(String code_department) {
		this.code_department = code_department;
	}
	public Long getRequest_by_id() {
		return request_by_id;
	}
	public void setRequest_by_id(Long request_by_id) {
		this.request_by_id = request_by_id;
	}
	public Long getDepartment_id() {
		return department_id;
	}
	public void setDepartment_id(Long department_id) {
		this.department_id = department_id;
	}
	public double getPhucap_ca3() {
		return phucap_ca3;
	}
	public void setPhucap_ca3(double phucap_ca3) {
		this.phucap_ca3 = phucap_ca3;
	}
	public String getCodeEmployeeOld() {
		return codeEmployeeOld;
	}
	public void setCodeEmployeeOld(String codeEmployeeOld) {
		this.codeEmployeeOld = codeEmployeeOld;
	}
	public double getDiemNgay() {
		return diemNgay;
	}
	public void setDiemNgay(double diemNgay) {
		this.diemNgay = diemNgay;
	}
	public double getDiemCa3() {
		return diemCa3;
	}
	public void setDiemCa3(double diemCa3) {
		this.diemCa3 = diemCa3;
	}
	public String getCodeTo() {
		return codeTo;
	}
	public void setCodeTo(String codeTo) {
		this.codeTo = codeTo;
	}

}
