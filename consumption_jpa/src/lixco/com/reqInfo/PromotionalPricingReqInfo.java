package lixco.com.reqInfo;

import lixco.com.entity.PromotionalPricing;

public class PromotionalPricingReqInfo {
	private PromotionalPricing promotional_pricing=null;

	public PromotionalPricingReqInfo(PromotionalPricing promotional_pricing) {
		this.promotional_pricing = promotional_pricing;
	}

	public PromotionalPricingReqInfo() {
	}

	public PromotionalPricing getPromotional_pricing() {
		return promotional_pricing;
	}

	public void setPromotional_pricing(PromotionalPricing promotional_pricing) {
		this.promotional_pricing = promotional_pricing;
	}
	
}
