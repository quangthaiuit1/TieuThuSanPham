package lixco.com.reqInfo;

public class RowInvoice {
	private int order_num;
	private String product_name;
	private String unit;
	private double quantity;
	private double unit_price;
	private double total_amount;
	
	public RowInvoice() {
	}
	
	public RowInvoice(int order_num, String product_name, String unit, double quantity, double unit_price,
			double total_amount) {
		this.order_num = order_num;
		this.product_name = product_name;
		this.unit = unit;
		this.quantity = quantity;
		this.unit_price = unit_price;
		this.total_amount = total_amount;
	}

	public int getOrder_num() {
		return order_num;
	}
	public void setOrder_num(int order_num) {
		this.order_num = order_num;
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
}
