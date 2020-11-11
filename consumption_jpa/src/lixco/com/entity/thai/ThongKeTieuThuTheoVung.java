package lixco.com.entity.thai;

import java.util.List;

public class ThongKeTieuThuTheoVung {
	private String khuvuc;
	private List<ThanhPhoSanPham> sanphamtheotp;

	public String getKhuvuc() {
		return khuvuc;
	}

	public void setKhuvuc(String khuvuc) {
		this.khuvuc = khuvuc;
	}

	public List<ThanhPhoSanPham> getSanphamtheotp() {
		return sanphamtheotp;
	}

	public void setSanphamtheotp(List<ThanhPhoSanPham> sanphamtheotp) {
		this.sanphamtheotp = sanphamtheotp;
	}

}

class ThanhPhoSanPham {
	private String thanhpho;
	private List<SanPham> sanphams;

	public String getThanhpho() {
		return thanhpho;
	}

	public void setThanhpho(String thanhpho) {
		this.thanhpho = thanhpho;
	}

	public List<SanPham> getSanphams() {
		return sanphams;
	}

	public void setSanphams(List<SanPham> sanphams) {
		this.sanphams = sanphams;
	}
}

class SanPham {
	private String tensanpham;
	private double kybaocao;
	private double kysosanh;

	public String getTensanpham() {
		return tensanpham;
	}

	public void setTensanpham(String tensanpham) {
		this.tensanpham = tensanpham;
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
}
