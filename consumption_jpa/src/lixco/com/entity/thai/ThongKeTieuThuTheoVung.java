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
