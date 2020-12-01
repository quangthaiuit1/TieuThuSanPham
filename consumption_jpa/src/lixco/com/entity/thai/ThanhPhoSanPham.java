package lixco.com.entity.thai;

import java.util.List;

public class ThanhPhoSanPham {
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
