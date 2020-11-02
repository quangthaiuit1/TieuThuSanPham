package lixco.com.reqInfo;

import lixco.com.entity.PromotionProgram;

public class PromotionProgramReqInfo {
	private PromotionProgram promotion_program=null;

	public PromotionProgramReqInfo() {
	}

	public PromotionProgramReqInfo(PromotionProgram promotion_program) {
		this.promotion_program = promotion_program;
	}

	public PromotionProgram getPromotion_program() {
		return promotion_program;
	}

	public void setPromotion_program(PromotionProgram promotion_program) {
		this.promotion_program = promotion_program;
	}
	
}
