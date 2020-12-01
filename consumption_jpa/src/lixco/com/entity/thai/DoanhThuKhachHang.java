package lixco.com.entity.thai;

public class DoanhThuKhachHang {
	private String tenkhachhang;
	private double kybaocao;
	private double kysosanh;
	private double tangtruong;

	public DoanhThuKhachHang() {
		super();
	}

	public DoanhThuKhachHang(String tenkhachhang, double kybaocao, double kysosanh, double tangtruong) {
		super();
		this.tenkhachhang = tenkhachhang;
		this.kybaocao = kybaocao;
		this.kysosanh = kysosanh;
		this.tangtruong = tangtruong;
	}

	public String getTenkhachhang() {
		return tenkhachhang;
	}

	public void setTenkhachhang(String tenkhachhang) {
		this.tenkhachhang = tenkhachhang;
	}

	public double getKybaocao() {
		return kybaocao;
	}

	public void setKybaocao(double kybaocao) {
		this.kybaocao = kybaocao;
	}

	public double getKysosanh() {
		return kysosanh;
	}

	public void setKysosanh(double kysosanh) {
		this.kysosanh = kysosanh;
	}

	public double getTangtruong() {
		return tangtruong;
	}

	public void setTangtruong(double tangtruong) {
		this.tangtruong = tangtruong;
	}
}
