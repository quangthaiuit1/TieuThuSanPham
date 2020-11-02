package lixco.com.reportInfo;

public class IEInvoiceReport {
	private long product_id;
	private String product_name;
	private String unit;
	private double quantity_export;
	private double foreign_unit_price;
	private double total_export_foreign_amount;
	
	public IEInvoiceReport() {
	}
	
	public IEInvoiceReport(long product_id, String product_name,String unit, double quantity_export, double foreign_unit_price,
			double total_export_foreign_amount) {
		this.product_id = product_id;
		this.product_name = product_name;
		this.unit=unit;
		this.quantity_export = quantity_export;
		this.foreign_unit_price = foreign_unit_price;
		this.total_export_foreign_amount = total_export_foreign_amount;
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
	public double getQuantity_export() {
		return quantity_export;
	}
	public void setQuantity_export(double quantity_export) {
		this.quantity_export = quantity_export;
	}
	public double getForeign_unit_price() {
		return foreign_unit_price;
	}
	public void setForeign_unit_price(double foreign_unit_price) {
		this.foreign_unit_price = foreign_unit_price;
	}
	public double getTotal_export_foreign_amount() {
		return total_export_foreign_amount;
	}
	public void setTotal_export_foreign_amount(double total_export_foreign_amount) {
		this.total_export_foreign_amount = total_export_foreign_amount;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
}
