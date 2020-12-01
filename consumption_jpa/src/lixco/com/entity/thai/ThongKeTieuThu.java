package lixco.com.entity.thai;

public class ThongKeTieuThu {

	private String loaiXuatNhap;
	private long loaiXuatNhapId;
	private long loaiSanPhamId;
	private double slKyBaocao;
	private double slKySosanh;
	private double doanhthuKyBaocao;
	private double doanhthuKySosanh;
	private String loaiSanPham;

	public ThongKeTieuThu(String loaiXuatNhap, long loaiXuatNhapId, long loaiSanPhamId, double slKyBaocao,
			double doanhthuKyBaocao, String loaiSanPham) {
		super();
		this.loaiXuatNhap = loaiXuatNhap;
		this.loaiXuatNhapId = loaiXuatNhapId;
		this.loaiSanPhamId = loaiSanPhamId;
		this.slKyBaocao = slKyBaocao;
		this.doanhthuKyBaocao = doanhthuKyBaocao;
		this.loaiSanPham = loaiSanPham;
	}

	public ThongKeTieuThu(String loaiXuatNhap, long loaiXuatNhapId, long loaiSanPhamId, double slKyBaocao,
			double slKySosanh, double doanhthuKyBaocao, double doanhthuKySosanh, String loaiSanPham) {
		super();
		this.loaiXuatNhap = loaiXuatNhap;
		this.loaiXuatNhapId = loaiXuatNhapId;
		this.loaiSanPhamId = loaiSanPhamId;
		this.slKyBaocao = slKyBaocao;
		this.slKySosanh = slKySosanh;
		this.doanhthuKyBaocao = doanhthuKyBaocao;
		this.doanhthuKySosanh = doanhthuKySosanh;
		this.loaiSanPham = loaiSanPham;
	}

	public ThongKeTieuThu() {
		super();
	}

	public String getLoaiXuatNhap() {
		return loaiXuatNhap;
	}

	public void setLoaiXuatNhap(String loaiXuatNhap) {
		this.loaiXuatNhap = loaiXuatNhap;
	}

	public void setLoaiXuatNhapId(int loaiXuatNhapId) {
		this.loaiXuatNhapId = loaiXuatNhapId;
	}

	public void setLoaiSanPhamId(int loaiSanPhamId) {
		this.loaiSanPhamId = loaiSanPhamId;
	}

	public String getLoaiSanPham() {
		return loaiSanPham;
	}

	public long getLoaiXuatNhapId() {
		return loaiXuatNhapId;
	}

	public void setLoaiXuatNhapId(long loaiXuatNhapId) {
		this.loaiXuatNhapId = loaiXuatNhapId;
	}

	public long getLoaiSanPhamId() {
		return loaiSanPhamId;
	}

	public void setLoaiSanPhamId(long loaiSanPhamId) {
		this.loaiSanPhamId = loaiSanPhamId;
	}

	public void setLoaiSanPham(String loaiSanPham) {
		this.loaiSanPham = loaiSanPham;
	}

	public double getSlKyBaocao() {
		return slKyBaocao;
	}

	public void setSlKyBaocao(double slKyBaocao) {
		this.slKyBaocao = slKyBaocao;
	}

	public double getSlKySosanh() {
		return slKySosanh;
	}

	public void setSlKySosanh(double slKySosanh) {
		this.slKySosanh = slKySosanh;
	}

	public double getDoanhthuKyBaocao() {
		return doanhthuKyBaocao;
	}

	public void setDoanhthuKyBaocao(double doanhthuKyBaocao) {
		this.doanhthuKyBaocao = doanhthuKyBaocao;
	}

	public double getDoanhthuKySosanh() {
		return doanhthuKySosanh;
	}

	public void setDoanhthuKySosanh(double doanhthuKySosanh) {
		this.doanhthuKySosanh = doanhthuKySosanh;
	}
}
