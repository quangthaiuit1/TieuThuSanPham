package lixco.com.reportInfo;

import java.util.Date;

public class IEInvoiceOrderListByProductCodeReport {
	private String voucher_code;
	private Date invoice_date;
	private String contract_voucher_code;
	private String customer_code;
	private String customer_name;
	private String license_plate;
	private String payment_name;
	private String stevedore_content;
	private String warehouse_name;
	private double tax_value;
	private Date etd_date;
	private double exchange_rate;
	private String currency_type;
	private String note;
	private String bill_no;
	private String declaration_code;
	private String post_of_tran_code;
	private String product_code;
	private String product_name;
	private double quantity_export;
	private double foreign_unit_price;
	private double total_foreign_amount;
	private String driver_name;
	
	public IEInvoiceOrderListByProductCodeReport() {
	}
	
	public IEInvoiceOrderListByProductCodeReport(String voucher_code, Date invoice_date, String contract_voucher_code,
			String customer_code, String customer_name, String license_plate, String payment_name,String stevedore_content,
			String warehouse_name, double tax_value, Date etd_date, double exchange_rate, String currency_type,
			String note, String bill_no, String declaration_code, String post_of_tran_code, String product_code,
			String product_name, double quantity_export, double foreign_unit_price, double total_foreign_amount,
			String driver_name) {
		this.voucher_code = voucher_code;
		this.invoice_date = invoice_date;
		this.contract_voucher_code = contract_voucher_code;
		this.customer_code = customer_code;
		this.customer_name = customer_name;
		this.license_plate = license_plate;
		this.payment_name = payment_name;
		this.stevedore_content=stevedore_content;
		this.warehouse_name = warehouse_name;
		this.tax_value = tax_value;
		this.etd_date = etd_date;
		this.exchange_rate = exchange_rate;
		this.currency_type = currency_type;
		this.note = note;
		this.bill_no = bill_no;
		this.declaration_code = declaration_code;
		this.post_of_tran_code = post_of_tran_code;
		this.product_code = product_code;
		this.product_name = product_name;
		this.quantity_export = quantity_export;
		this.foreign_unit_price = foreign_unit_price;
		this.total_foreign_amount = total_foreign_amount;
		this.driver_name = driver_name;
	}

	public String getVoucher_code() {
		return voucher_code;
	}
	public void setVoucher_code(String voucher_code) {
		this.voucher_code = voucher_code;
	}
	public Date getInvoice_date() {
		return invoice_date;
	}
	public void setInvoice_date(Date invoice_date) {
		this.invoice_date = invoice_date;
	}
	public String getContract_voucher_code() {
		return contract_voucher_code;
	}
	public void setContract_voucher_code(String contract_voucher_code) {
		this.contract_voucher_code = contract_voucher_code;
	}
	public String getCustomer_code() {
		return customer_code;
	}
	public void setCustomer_code(String customer_code) {
		this.customer_code = customer_code;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getLicense_plate() {
		return license_plate;
	}
	public void setLicense_plate(String license_plate) {
		this.license_plate = license_plate;
	}
	public String getPayment_name() {
		return payment_name;
	}
	public void setPayment_name(String payment_name) {
		this.payment_name = payment_name;
	}
	public String getStevedore_content() {
		return stevedore_content;
	}

	public void setStevedore_content(String stevedore_content) {
		this.stevedore_content = stevedore_content;
	}

	public String getWarehouse_name() {
		return warehouse_name;
	}
	public void setWarehouse_name(String warehouse_name) {
		this.warehouse_name = warehouse_name;
	}
	public double getTax_value() {
		return tax_value;
	}
	public void setTax_value(double tax_value) {
		this.tax_value = tax_value;
	}
	public Date getEtd_date() {
		return etd_date;
	}
	public void setEtd_date(Date etd_date) {
		this.etd_date = etd_date;
	}
	public double getExchange_rate() {
		return exchange_rate;
	}
	public void setExchange_rate(double exchange_rate) {
		this.exchange_rate = exchange_rate;
	}
	public String getCurrency_type() {
		return currency_type;
	}
	public void setCurrency_type(String currency_type) {
		this.currency_type = currency_type;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getBill_no() {
		return bill_no;
	}
	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}
	public String getDeclaration_code() {
		return declaration_code;
	}
	public void setDeclaration_code(String declaration_code) {
		this.declaration_code = declaration_code;
	}
	public String getPost_of_tran_code() {
		return post_of_tran_code;
	}
	public void setPost_of_tran_code(String post_of_tran_code) {
		this.post_of_tran_code = post_of_tran_code;
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
	public double getTotal_foreign_amount() {
		return total_foreign_amount;
	}
	public void setTotal_foreign_amount(double total_foreign_amount) {
		this.total_foreign_amount = total_foreign_amount;
	}
	public String getDriver_name() {
		return driver_name;
	}
	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}
}
