package lixco.com.reportInfo;

public class IEProformaInvocieReportInfo {
	private long product_id;
	private String product_en_name;
	private double specification;
	private String unit;
	private long product_type_id;
	private String product_type_en_name;
	private double quantity_export;
	private double foreign_unit_price;
	private double total_export_foreign_amount;
	
	
	
	public IEProformaInvocieReportInfo() {
	}
	


	public IEProformaInvocieReportInfo(long product_id, String product_en_name, double specification, String unit,
			long product_type_id, String product_type_en_name, double quantity_export, double foreign_unit_price,
			double total_export_foreign_amount) {
		this.product_id = product_id;
		this.product_en_name = product_en_name;
		this.specification = specification;
		this.unit = unit;
		this.product_type_id = product_type_id;
		this.product_type_en_name = product_type_en_name;
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


	public String getProduct_en_name() {
		return product_en_name;
	}


	public void setProduct_en_name(String product_en_name) {
		this.product_en_name = product_en_name;
	}


	public double getSpecification() {
		return specification;
	}


	public void setSpecification(double specification) {
		this.specification = specification;
	}


	public String getUnit() {
		return unit;
	}


	public void setUnit(String unit) {
		this.unit = unit;
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


	public double getQuantity_export() {
		return quantity_export;
	}


	public void setQuantity_export(double quantity_export) {
		this.quantity_export = quantity_export;
	}


	public double getTotal_export_foreign_amount() {
		return total_export_foreign_amount;
	}


	public void setTotal_export_foreign_amount(double total_export_foreign_amount) {
		this.total_export_foreign_amount = total_export_foreign_amount;
	}


	public double getForeign_unit_price() {
		return foreign_unit_price;
	}


	public void setForeign_unit_price(double foreign_unit_price) {
		this.foreign_unit_price = foreign_unit_price;
	}

}
