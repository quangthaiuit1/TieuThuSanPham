package lixco.com.reportInfo;

public class IEVanningReport {
	private long product_type_id;
	private String product_type_en_name;
	private String product_en_name;
	private String container_no;
	
	public IEVanningReport() {
	}
	public IEVanningReport(long product_type_id, String product_type_en_name, String product_en_name,String container_no) {
		this.product_type_id = product_type_id;
		this.product_type_en_name = product_type_en_name;
		this.product_en_name = product_en_name;
		this.container_no=container_no;
	}

	public long getProduct_type_id() {
		return product_type_id;
	}
	public void setProduct_type_id(long product_type_id) {
		this.product_type_id = product_type_id;
	}
	public String getProduct_type_en_name() {
		return product_type_en_name;
	}
	public void setProduct_type_en_name(String product_type_en_name) {
		this.product_type_en_name = product_type_en_name;
	}
	public String getProduct_en_name() {
		return product_en_name;
	}
	public void setProduct_en_name(String product_en_name) {
		this.product_en_name = product_en_name;
	}
	public String getContainer_no() {
		return container_no;
	}
	public void setContainer_no(String container_no) {
		this.container_no = container_no;
	}
	
}
