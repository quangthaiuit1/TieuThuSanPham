package lixco.com.reqInfo;

import lixco.com.entity.PricingProgramDetail;

public class PricingProgramDetailReqInfo {
	private PricingProgramDetail pricing_program_detail=null;

	public PricingProgramDetailReqInfo() {
	}

	public PricingProgramDetailReqInfo(PricingProgramDetail pricing_program_detail) {
		this.pricing_program_detail = pricing_program_detail;
	}

	public PricingProgramDetail getPricing_program_detail() {
		return pricing_program_detail;
	}

	public void setPricing_program_detail(PricingProgramDetail pricing_program_detail) {
		this.pricing_program_detail = pricing_program_detail;
	}
	
}
