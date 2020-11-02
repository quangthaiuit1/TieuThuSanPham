package lixco.com.reqInfo;

import lixco.com.entity.CustomerPromotionProgram;

public class CustomerPromotionProgramReqInfo {
	private CustomerPromotionProgram customer_promotion_program=null;

	public CustomerPromotionProgramReqInfo() {
	}

	public CustomerPromotionProgramReqInfo(CustomerPromotionProgram customer_promotion_program) {
		this.customer_promotion_program = customer_promotion_program;
	}

	public CustomerPromotionProgram getCustomer_promotion_program() {
		return customer_promotion_program;
	}

	public void setCustomer_promotion_program(CustomerPromotionProgram customer_promotion_program) {
		this.customer_promotion_program = customer_promotion_program;
	}
	
}
