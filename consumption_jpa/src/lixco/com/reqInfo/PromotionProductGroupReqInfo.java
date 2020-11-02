package lixco.com.reqInfo;

import lixco.com.entity.PromotionProductGroup;

public class PromotionProductGroupReqInfo {
	private PromotionProductGroup promotion_product_group=null;

	public PromotionProductGroupReqInfo() {
	}

	public PromotionProductGroupReqInfo(PromotionProductGroup promotion_product_group) {
		this.promotion_product_group = promotion_product_group;
	}

	public PromotionProductGroup getPromotion_product_group() {
		return promotion_product_group;
	}

	public void setPromotion_product_group(PromotionProductGroup promotion_product_group) {
		this.promotion_product_group = promotion_product_group;
	}
	
}
