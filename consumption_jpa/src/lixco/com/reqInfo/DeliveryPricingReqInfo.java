package lixco.com.reqInfo;

import lixco.com.entity.DeliveryPricing;

public class DeliveryPricingReqInfo {
	private DeliveryPricing delivery_pricing =null;

	public DeliveryPricingReqInfo() {
	}

	public DeliveryPricingReqInfo(DeliveryPricing delivery_pricing) {
		this.delivery_pricing = delivery_pricing;
	}

	public DeliveryPricing getDelivery_pricing() {
		return delivery_pricing;
	}

	public void setDelivery_pricing(DeliveryPricing delivery_pricing) {
		this.delivery_pricing = delivery_pricing;
	}

	
	
}
