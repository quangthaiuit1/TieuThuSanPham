package lixco.com.reportInfo;

public class IEInvoiceCustomerBillNoDetailReport {
	private String container_no;
	private String product_en_name;
	private long product_type_id;
	private String product_type_en_name;
	private String unit;
	private double specification;
	private double tare;
	private double factor;
	private double quantity_export;
	public IEInvoiceCustomerBillNoDetailReport() {
	}
	public IEInvoiceCustomerBillNoDetailReport(String container_no,long product_type_id, String product_en_name, String product_type_en_name, String unit, double specification, double tare,
			double factor, double quantity_export) {
		this.container_no = container_no;
		this.product_en_name = product_en_name;
		this.product_type_id = product_type_id;
		this.product_type_en_name = product_type_en_name;
		this.unit = unit;
		this.specification = specification;
		this.tare = tare;
		this.factor = factor;
		this.quantity_export = quantity_export;
	}
	public String getContainer_no() {
		return container_no;
	}
	public void setContainer_no(String container_no) {
		this.container_no = container_no;
	}
	public String getProduct_en_name() {
		return product_en_name;
	}
	public void setProduct_en_name(String product_en_name) {
		this.product_en_name = product_en_name;
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
	public double getTare() {
		return tare;
	}
	public void setTare(double tare) {
		this.tare = tare;
	}
	public double getFactor() {
		return factor;
	}
	public void setFactor(double factor) {
		this.factor = factor;
	}
	public double getQuantity_export() {
		return quantity_export;
	}
	public void setQuantity_export(double quantity_export) {
		this.quantity_export = quantity_export;
	}
}
