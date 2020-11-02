package lixco.com.reportInfo;

public class IEInvoiceListByContNoReport {
	private String city_name;
	private String customer_name;
	private String ft_container;
	private String container_no;
	private String arrival_place;
	private int container_number;
	private double quantity_export;
	public IEInvoiceListByContNoReport() {
	}
	public IEInvoiceListByContNoReport(String city_name, String customer_name, String ft_container, String container_no,
			String arrival_place, int container_number, double quantity_export) {
		this.city_name = city_name;
		this.customer_name = customer_name;
		this.ft_container = ft_container;
		this.container_no = container_no;
		this.arrival_place = arrival_place;
		this.container_number = container_number;
		this.quantity_export = quantity_export;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getFt_container() {
		return ft_container;
	}
	public void setFt_container(String ft_container) {
		this.ft_container = ft_container;
	}
	public String getContainer_no() {
		return container_no;
	}
	public void setContainer_no(String container_no) {
		this.container_no = container_no;
	}
	public String getArrival_place() {
		return arrival_place;
	}
	public void setArrival_place(String arrival_place) {
		this.arrival_place = arrival_place;
	}
	public int getContainer_number() {
		return container_number;
	}
	public void setContainer_number(int container_number) {
		this.container_number = container_number;
	}
	public double getQuantity_export() {
		return quantity_export;
	}
	public void setQuantity_export(double quantity_export) {
		this.quantity_export = quantity_export;
	}
}
