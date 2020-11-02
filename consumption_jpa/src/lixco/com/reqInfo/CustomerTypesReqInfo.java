package lixco.com.reqInfo;

import lixco.com.entity.CustomerTypes;

public class CustomerTypesReqInfo {
	private CustomerTypes customer_types=null;

	public CustomerTypesReqInfo() {
	}

	public CustomerTypesReqInfo(CustomerTypes customer_types) {
		this.customer_types = customer_types;
	}

	public CustomerTypes getCustomer_types() {
		return customer_types;
	}

	public void setCustomer_types(CustomerTypes customer_types) {
		this.customer_types = customer_types;
	}
	
}
