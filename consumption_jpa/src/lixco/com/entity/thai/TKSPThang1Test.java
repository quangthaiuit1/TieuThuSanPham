package lixco.com.entity.thai;

public class TKSPThang1Test {
	private String loaiSanPham;
	private long loaiSanPhamId;
	private String sanPham;
	private long sanPhamId;
	private long soLuong;
	private double thanhTien;
	private double thueTGGT;
	private double tongTien;
	private String loaiXuatNhap;
	private long loaiXuatNhapId;
	private long invoiceId;

	
	public TKSPThang1Test() {
		super();
	}

	public TKSPThang1Test(String loaiSanPham, long loaiSanPhamId, String sanPham, long sanPhamId, long soLuong,
			double thanhTien, double thueTGGT, double tongTien, String loaiXuatNhap, long loaiXuatNhapId) {
		super();
		this.loaiSanPham = loaiSanPham;
		this.loaiSanPhamId = loaiSanPhamId;
		this.sanPham = sanPham;
		this.sanPhamId = sanPhamId;
		this.soLuong = soLuong;
		this.thanhTien = thanhTien;
		this.thueTGGT = thueTGGT;
		this.tongTien = tongTien;
		this.loaiXuatNhap = loaiXuatNhap;
		this.loaiXuatNhapId = loaiXuatNhapId;
	}

	public TKSPThang1Test(String loaiSanPham, long loaiSanPhamId, String sanPham, long sanPhamId, long soLuong,
			double thanhTien, double thueTGGT, double tongTien, String loaiXuatNhap, long loaiXuatNhapId,
			long invoiceId) {
		super();
		this.loaiSanPham = loaiSanPham;
		this.loaiSanPhamId = loaiSanPhamId;
		this.sanPham = sanPham;
		this.sanPhamId = sanPhamId;
		this.soLuong = soLuong;
		this.thanhTien = thanhTien;
		this.thueTGGT = thueTGGT;
		this.tongTien = tongTien;
		this.loaiXuatNhap = loaiXuatNhap;
		this.loaiXuatNhapId = loaiXuatNhapId;
		this.invoiceId = invoiceId;
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

	public String getLoaiSanPham() {
		return loaiSanPham;
	}

	public void setLoaiSanPham(String loaiSanPham) {
		this.loaiSanPham = loaiSanPham;
	}

	public String getSanPham() {
		return sanPham;
	}

	public void setSanPham(String sanPham) {
		this.sanPham = sanPham;
	}

	public long getLoaiSanPhamId() {
		return loaiSanPhamId;
	}

	public void setLoaiSanPhamId(long loaiSanPhamId) {
		this.loaiSanPhamId = loaiSanPhamId;
	}

	public long getSanPhamId() {
		return sanPhamId;
	}

	public void setSanPhamId(long sanPhamId) {
		this.sanPhamId = sanPhamId;
	}

	public long getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(long soLuong) {
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