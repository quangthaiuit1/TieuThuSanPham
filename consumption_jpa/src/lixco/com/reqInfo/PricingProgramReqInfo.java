package lixco.com.reqInfo;

import lixco.com.entity.PricingProgram;

public class PricingProgramReqInfo {
	private PricingProgram pricing_program=null;

	public PricingProgramReqInfo() {
	}

	public PricingProgramReqInfo(PricingProgram pricing_program) {
		this.pricing_program = pricing_program;
	}

	public PricingProgram getPricing_program() {
		return pricing_program;
	}

	public void setPricing_program(PricingProgram pricing_program) {
		this.pricing_program = pricing_program;
	}
	
}
