package lixco.com.reportInfo;

import java.util.Date;

public class IEOrderFormReport {
	private String product_code;
	private String  product_name;
	private double specification;
	private double quantity_export;
	private double quantity;
	private double unit_price;
	private double total_amount;
	private String batch_code;
	private Date manufacture_date;
	public IEOrderFormReport() {
	}
	public IEOrderFormReport(String product_code, String product_name, double specification, double quantity_export,
			double quantity, double unit_price, double total_amount,String batch_code) {
		this.product_code = product_code;
		this.product_name = product_name;
		this.specification = specification;
		this.quantity_export = quantity_export;
		this.quantity = quantity;
		this.unit_price = unit_price;
		this.total_amount = total_amount;
		this.batch_code=batch_code;
	}
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
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
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
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
	public String getBatch_code() {
		return batch_code;
	}
	public void setBatch_code(String batch_code) {
		this.batch_code = batch_code;
	}
	public Date getManufacture_date() {
		return manufacture_date;
	}
	public void setManufacture_date(Date manufacture_date) {
		this.manufacture_date = manufacture_date;
	}
	
	
}
