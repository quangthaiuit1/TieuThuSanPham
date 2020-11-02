package lixco.com.reportInfo;

public class IEExportReport {
	private long product_id;
	private String product_name;
	private String unit;
	private double specification;
	private double quantity_export;
	private double unit_price;
	private double total_amount;
	
	public IEExportReport() {
	}
	
	public IEExportReport(long product_id,String product_name, String unit, double specification, double quantity_export,double unit_price, double total_amount) {
		this.product_id=product_id;
		this.product_name = product_name;
		this.unit = unit;
		this.specification = specification;
		this.quantity_export = quantity_export;
		this.unit_price = unit_price;
		this.total_amount = total_amount;
	}
	public long getProduct_id() {
		return product_id;
	}
	public void setProduct_id(long product_id) {
		this.product_id = product_id;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getSpecification() {
		return specification;
	}
	public void setSpecification(double specification) {
		this.specification = specification;
	}
	public double getQuantity_export() {
		return quantity_export;
	}
	public void setQuantity_export(double quantity_export) {
		this.quantity_export = quantity_export;
	}
	public double getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
	}
	public double getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}
}
