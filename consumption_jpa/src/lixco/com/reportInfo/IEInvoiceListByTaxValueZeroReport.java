package lixco.com.reportInfo;

import java.util.Date;

public class IEInvoiceListByTaxValueZeroReport {
	private String contract_voucher_code;// số chứng từ hợp đồng
	private Date contract_date;//ngày hợp đồng
	private String customer_name;// tên khách hàng
	private String properties;// tính chất
	private String country_name;// quốc gia nhập khẩu
	private double contract_foreign_total_amount;// số tiền 
	private double contract_total_amount;
	private Date payment_period;
	private String declaration_code;
	private Date declaration_date;
	private String goods;// hàng hóa
	private double declaration_quantity;
	private double declaration_foreign_total_amount;
	private double declaration_total_amount;
	private String method_of_transportation;
	private String voucher_code;
	private Date invoice_date;
	private double quantity_export;
	private String material_name;
	private double foreign_total_amount;
	private double total_amount;
	private String payment;
	private String payment_voucher;
	private Date payment_voucher_date;
	private double payment_voucher_foreign_total_amount;
	private double payment_voucher_total_amount;
	private String payment_report;
	
	public IEInvoiceListByTaxValueZeroReport() {
	}
	
	public IEInvoiceListByTaxValueZeroReport(String contract_voucher_code, Date contract_date, String customer_name
            , String country_name,double contract_foreign_total_amount, double contract_total_amount,
			 String declaration_code, double declaration_quantity,
			double declaration_foreign_total_amount, double declaration_total_amount,
			String voucher_code, Date invoice_date, double quantity_export,
			double foreign_total_amount, double total_amount, String payment,
			 double payment_voucher_foreign_total_amount, double payment_voucher_total_amount) {
		this.contract_voucher_code = contract_voucher_code;
		this.contract_date = contract_date;
		this.customer_name = customer_name;
		this.country_name = country_name;
		this.contract_foreign_total_amount = contract_foreign_total_amount;
		this.contract_total_amount = contract_total_amount;
		this.declaration_code = declaration_code;
		this.declaration_quantity = declaration_quantity;
		this.declaration_foreign_total_amount = declaration_foreign_total_amount;
		this.declaration_total_amount = declaration_total_amount;
		this.voucher_code = voucher_code;
		this.invoice_date = invoice_date;
		this.quantity_export = quantity_export;
		this.foreign_total_amount = foreign_total_amount;
		this.total_amount = total_amount;
		this.payment = payment;
		this.payment_voucher_foreign_total_amount = payment_voucher_foreign_total_amount;
		this.payment_voucher_total_amount = payment_voucher_total_amount;
	}

	public String getContract_voucher_code() {
		return contract_voucher_code;
	}
	public void setContract_voucher_code(String contract_voucher_code) {
		this.contract_voucher_code = contract_voucher_code;
	}
	public Date getContract_date() {
		return contract_date;
	}
	public void setContract_date(Date contract_date) {
		this.contract_date = contract_date;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	public String getCountry_name() {
		return country_name;
	}
	public void setCountry_name(String country_name) {
		this.country_name = country_name;
	}
	public double getContract_foreign_total_amount() {
		return contract_foreign_total_amount;
	}
	public void setContract_foreign_total_amount(double contract_foreign_total_amount) {
		this.contract_foreign_total_amount = contract_foreign_total_amount;
	}
	public double getContract_total_amount() {
		return contract_total_amount;
	}
	public void setContract_total_amount(double contract_total_amount) {
		this.contract_total_amount = contract_total_amount;
	}
	public Date getPayment_period() {
		return payment_period;
	}
	public void setPayment_period(Date payment_period) {
		this.payment_period = payment_period;
	}
	public String getDeclaration_code() {
		return declaration_code;
	}
	public void setDeclaration_code(String declaration_code) {
		this.declaration_code = declaration_code;
	}
	public Date getDeclaration_date() {
		return declaration_date;
	}
	public void setDeclaration_date(Date declaration_date) {
		this.declaration_date = declaration_date;
	}
	public String getGoods() {
		return goods;
	}
	public void setGoods(String goods) {
		this.goods = goods;
	}
	public double getDeclaration_quantity() {
		return declaration_quantity;
	}
	public void setDeclaration_quantity(double declaration_quantity) {
		this.declaration_quantity = declaration_quantity;
	}
	public double getDeclaration_foreign_total_amount() {
		return declaration_foreign_total_amount;
	}
	public void setDeclaration_foreign_total_amount(double declaration_foreign_total_amount) {
		this.declaration_foreign_total_amount = declaration_foreign_total_amount;
	}
	public double getDeclaration_total_amount() {
		return declaration_total_amount;
	}
	public void setDeclaration_total_amount(double declaration_total_amount) {
		this.declaration_total_amount = declaration_total_amount;
	}
	public String getMethod_of_transportation() {
		return method_of_transportation;
	}
	public void setMethod_of_transportation(String method_of_transportation) {
		this.method_of_transportation = method_of_transportation;
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
	public double getQuantity_export() {
		return quantity_export;
	}
	public void setQuantity_export(double quantity_export) {
		this.quantity_export = quantity_export;
	}
	public String getMaterial_name() {
		return material_name;
	}
	public void setMaterial_name(String material_name) {
		this.material_name = material_name;
	}
	public double getForeign_total_amount() {
		return foreign_total_amount;
	}
	public void setForeign_total_amount(double foreign_total_amount) {
		this.foreign_total_amount = foreign_total_amount;
	}
	public double getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}
	public String getPayment() {
		return payment;
	}
	public void setPayment(String payment) {
		this.payment = payment;
	}
	public String getPayment_voucher() {
		return payment_voucher;
	}
	public void setPayment_voucher(String payment_voucher) {
		this.payment_voucher = payment_voucher;
	}
	public Date getPayment_voucher_date() {
		return payment_voucher_date;
	}
	public void setPayment_voucher_date(Date payment_voucher_date) {
		this.payment_voucher_date = payment_voucher_date;
	}
	public double getPayment_voucher_foreign_total_amount() {
		return payment_voucher_foreign_total_amount;
	}
	public void setPayment_voucher_foreign_total_amount(double payment_voucher_foreign_total_amount) {
		this.payment_voucher_foreign_total_amount = payment_voucher_foreign_total_amount;
	}
	public double getPayment_voucher_total_amount() {
		return payment_voucher_total_amount;
	}
	public void setPayment_voucher_total_amount(double payment_voucher_total_amount) {
		this.payment_voucher_total_amount = payment_voucher_total_amount;
	}
	public String getPayment_report() {
		return payment_report;
	}
	public void setPayment_report(String payment_report) {
		this.payment_report = payment_report;
	}
}
