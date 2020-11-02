package lixco.com.reqInfo;

import lixco.com.entity.PromotionProgramDetail;

public class PromotionProgramDetailReqInfo {
	private PromotionProgramDetail promotion_program_detail=null;

	public PromotionProgramDetailReqInfo() {
	}

	public PromotionProgramDetailReqInfo(PromotionProgramDetail promotion_program_detail) {
		this.promotion_program_detail = promotion_program_detail;
	}

	public PromotionProgramDetail getPromotion_program_detail() {
		return promotion_program_detail;
	}

	public void setPromotion_program_detail(PromotionProgramDetail promotion_program_detail) {
		this.promotion_program_detail = promotion_program_detail;
	}
	
}
