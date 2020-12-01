package lixco.com.entity.thai;

public class SanLuongCuaKhachHang {
	private String tenKhachHang;
	private String loaiSanPham;
	private double kyBaoCao;
	private double KySoSanh;
	private int tangTruong;

	public String getTenKhachHang() {
		return tenKhachHang;
	}

	public void setTenKhachHang(String tenKhachHang) {
		this.tenKhachHang = tenKhachHang;
	}

	public String getLoaiSanPham() {
		return loaiSanPham;
	}

	public void setLoaiSanPham(String loaiSanPham) {
		this.loaiSanPham = loaiSanPham;
	}

	public double getKyBaoCao() {
		return kyBaoCao;
	}

	public void setKyBaoCao(double kyBaoCao) {
		this.kyBaoCao = kyBaoCao;
	}

	public double getKySoSanh() {
		return KySoSanh;
	}

	public void setKySoSanh(double kySoSanh) {
		KySoSanh = kySoSanh;
	}

	public int getTangTruong() {
		return tangTruong;
	}

	public void setTangTruong(int tangTruong) {
		this.tangTruong = tangTruong;
	}
}
