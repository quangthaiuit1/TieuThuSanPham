package lixco.com.reqInfo;

import lixco.com.entity.CustomerPricingProgram;

public class CustomerPricingProgramReqInfo {
	private CustomerPricingProgram customer_pricing_program=null;

	public CustomerPricingProgramReqInfo() {
	}

	public CustomerPricingProgramReqInfo(CustomerPricingProgram customer_pricing_program) {
		this.customer_pricing_program = customer_pricing_program;
	}

	public CustomerPricingProgram getCustomer_pricing_program() {
		return customer_pricing_program;
	}

	public void setCustomer_pricing_program(CustomerPricingProgram customer_pricing_program) {
		this.customer_pricing_program = customer_pricing_program;
	}
	

}
