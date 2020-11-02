package lixco.com.reqInfo;

import lixco.com.entity.Customer;

public class CustomerReqInfo {
	private Customer customer;
	
	public CustomerReqInfo() {
	}
	public CustomerReqInfo(Customer customer) {
		this.customer = customer;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
