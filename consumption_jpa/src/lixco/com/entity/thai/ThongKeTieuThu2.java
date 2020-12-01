package lixco.com.entity.thai;

public class ThongKeTieuThu2 {
	private long product_type_id;
	private String product_type_name;
	private long ie_categories_id;
	private String ie_categories_name;
	private double sl_kybaocao;
	private double sl_kySosanh;
	private double doanhthukybaocao;
	private double doanhthukysosanh;
	public ThongKeTieuThu2() {
	}
	
	public ThongKeTieuThu2(long product_type_id, String product_type_name, long ie_categories_id,
			String ie_categories_name, double sl_kybaocao, double doanhthukybaocao) {
		this.product_type_id = product_type_id;
		this.product_type_name = product_type_name;
		this.ie_categories_id = ie_categories_id;
		this.ie_categories_name = ie_categories_name;
		this.sl_kybaocao = sl_kybaocao;
		this.doanhthukybaocao = doanhthukybaocao;
	}

	public ThongKeTieuThu2(long product_type_id, String product_type_name, long ie_categories_id,
			String ie_categories_name, double sl_kybaocao, double sl_kySosanh, double doanhthukybaocao,
			double doanhthukysosanh) {
		this.product_type_id = product_type_id;
		this.product_type_name = product_type_name;
		this.ie_categories_id = ie_categories_id;
		this.ie_categories_name = ie_categories_name;
		this.sl_kybaocao = sl_kybaocao;
		this.sl_kySosanh = sl_kySosanh;
		this.doanhthukybaocao = doanhthukybaocao;
		this.doanhthukysosanh = doanhthukysosanh;
	}
	public long getProduct_type_id() {
		return product_type_id;
	}
	public void setProduct_type_id(long product_type_id) {
		this.product_type_id = product_type_id;
	}
	public String getProduct_type_name() {
		return product_type_name;
	}
	public void setProduct_type_name(String product_type_name) {
		this.product_type_name = product_type_name;
	}
	public long getIe_categories_id() {
		return ie_categories_id;
	}
	public void setIe_categories_id(long ie_categories_id) {
		this.ie_categories_id = ie_categories_id;
	}
	public String getIe_categories_name() {
		return ie_categories_name;
	}
	public void setIe_categories_name(String ie_categories_name) {
		this.ie_categories_name = ie_categories_name;
	}
	public double getSl_kybaocao() {
		return sl_kybaocao;
	}
	public void setSl_kybaocao(double sl_kybaocao) {
		this.sl_kybaocao = sl_kybaocao;
	}
	public double getSl_kySosanh() {
		return sl_kySosanh;
	}
	public void setSl_kySosanh(double sl_kySosanh) {
		this.sl_kySosanh = sl_kySosanh;
	}
	public double getDoanhthukybaocao() {
		return doanhthukybaocao;
	}
	public void setDoanhthukybaocao(double doanhthukybaocao) {
		this.doanhthukybaocao = doanhthukybaocao;
	}
	public double getDoanhthukysosanh() {
		return doanhthukysosanh;
	}
	public void setDoanhthukysosanh(double doanhthukysosanh) {
		this.doanhthukysosanh = doanhthukysosanh;
	}
	
	
}
