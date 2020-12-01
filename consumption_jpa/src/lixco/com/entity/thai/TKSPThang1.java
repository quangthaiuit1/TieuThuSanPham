package lixco.com.entity.thai;

public class TKSPThang1 {
	private String loaiSanPham;
	private long loaiSanPhamId;
	private String sanPham;
	private long sanPhamId;
	private double soLuong;
	private double thanhTien;
	private double thueTGGT;
	private double tongTien;
	private long invoiceId;
	private double heSoThue;
	private double donGia;
	private String loaiXuatNhap;
	private long loaiXuatNhapId;
	private double tongSLTheoSP;
	private double tongSLTheoLoaiSP;

	public TKSPThang1() {
		super();
	}

	public TKSPThang1(String loaiSanPham, long loaiSanPhamId, String sanPham, long sanPhamId, double soLuong,
			long invoiceId, double donGia, double heSoThue, String loaiXuatNhap, long loaiXuatNhapId) {
		super();
		this.loaiSanPham = loaiSanPham;
		this.loaiSanPhamId = loaiSanPhamId;
		this.sanPham = sanPham;
		this.sanPhamId = sanPhamId;
		this.soLuong = soLuong;
		this.invoiceId = invoiceId;
		this.heSoThue = heSoThue;
		this.donGia = donGia;
		this.loaiXuatNhap = loaiXuatNhap;
		this.loaiXuatNhapId = loaiXuatNhapId;
	}

	public TKSPThang1(String loaiSanPham, long loaiSanPhamId, String sanPham, long sanPhamId, double soLuong,
			long invoiceId, double donGia, double heSoThue) {
		super();
		this.loaiSanPham = loaiSanPham;
		this.loaiSanPhamId = loaiSanPhamId;
		this.sanPham = sanPham;
		this.sanPhamId = sanPhamId;
		this.soLuong = soLuong;
		this.invoiceId = invoiceId;
		this.heSoThue = heSoThue;
		this.donGia = donGia;
	}

	public TKSPThang1(String loaiSanPham, long loaiSanPhamId, String sanPham, long sanPhamId, double soLuong,
			double thanhTien, double thueTGGT, double tongTien, long invoiceId) {
		super();
		this.loaiSanPham = loaiSanPham;
		this.loaiSanPhamId = loaiSanPhamId;
		this.sanPham = sanPham;
		this.sanPhamId = sanPhamId;
		this.soLuong = soLuong;
		this.thanhTien = thanhTien;
		this.thueTGGT = thueTGGT;
		this.tongTien = tongTien;
		this.invoiceId = invoiceId;
	}

	public double getTongSLTheoSP() {
		return tongSLTheoSP;
	}

	public void setTongSLTheoSP(double tongSLTheoSP) {
		this.tongSLTheoSP = tongSLTheoSP;
	}

	public double getTongSLTheoLoaiSP() {
		return tongSLTheoLoaiSP;
	}

	public void setTongSLTheoLoaiSP(double tongSLTheoLoaiSP) {
		this.tongSLTheoLoaiSP = tongSLTheoLoaiSP;
	}

	public String getLoaiXuatNhap() {
		return loaiXuatNhap;
	}

	public void setLoaiXuatNhap(String loaiXuatNhap) {
		this.loaiXuatNhap = loaiXuatNhap;
	}

	public long getLoaiXuatNhapId() {
		return loaiXuatNhapId;
	}

	public void setLoaiXuatNhapId(long loaiXuatNhapId) {
		this.loaiXuatNhapId = loaiXuatNhapId;
	}

	public double getHeSoThue() {
		return heSoThue;
	}

	public void setHeSoThue(double heSoThue) {
		this.heSoThue = heSoThue;
	}

	public double getDonGia() {
		return donGia;
	}

	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}

	public String getLoaiSanPham() {
		return loaiSanPham;
	}

	public void setLoaiSanPham(String loaiSanPham) {
		this.loaiSanPham = loaiSanPham;
	}

	public long getLoaiSanPhamId() {
		return loaiSanPhamId;
	}

	public void setLoaiSanPhamId(long loaiSanPhamId) {
		this.loaiSanPhamId = loaiSanPhamId;
	}

	public String getSanPham() {
		return sanPham;
	}

	public void setSanPham(String sanPham) {
		this.sanPham = sanPham;
	}

	public long getSanPhamId() {
		return sanPhamId;
	}

	public void setSanPhamId(long sanPhamId) {
		this.sanPhamId = sanPhamId;
	}

	public double getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(double soLuong) {
		this.soLuong = soLuong;
	}

	public double getThanhTien() {
		return thanhTien;
	}

	public void setThanhTien(double thanhTien) {
		this.thanhTien = thanhTien;
	}

	public double getThueTGGT() {
		return thueTGGT;
	}

	public void setThueTGGT(double thueTGGT) {
		this.thueTGGT = thueTGGT;
	}

	public double getTongTien() {
		return tongTien;
	}

	public void setTongTien(double tongTien) {
		this.tongTien = tongTien;
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}

}
