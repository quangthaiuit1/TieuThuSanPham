package lixco.com.entity.thai;

//thong ke tieu thu cua khach hang
public class ThongKeTieuThuKH {
	private String tenkhachhang;
	// doanh thu ky bao cao
	private double kybaocao;
	// doanh thu ky so sanh
	private double kysosanh;
	// tang truong %
	private double tangtruong;

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
