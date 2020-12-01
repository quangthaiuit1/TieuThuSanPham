package lixco.com.entity.thai;

public class CustomerNonEntity {
	private long id;
	private String customer_code;// mã khách hàng
	private String customer_name;// tên khách hàng
	private String cell_phone;

	public CustomerNonEntity(long id, String customer_code, String customer_name, String cell_phone) {
		this.id = id;
		this.customer_code = customer_code;
		this.customer_name = customer_name;
		this.cell_phone = cell_phone;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getCell_phone() {
		return cell_phone;
	}

	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}
}