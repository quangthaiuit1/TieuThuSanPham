package lixco.com.reqInfo;

import lixco.com.entity.PromotionOrderDetail;

public class PromotionOrderDetailReqInfo {
	private PromotionOrderDetail promotion_order_detail=null;

	public PromotionOrderDetailReqInfo() {
	}

	public PromotionOrderDetailReqInfo(PromotionOrderDetail promotion_order_detail) {
		this.promotion_order_detail = promotion_order_detail;
	}

	public PromotionOrderDetail getPromotion_order_detail() {
		return promotion_order_detail;
	}

	public void setPromotion_order_detail(PromotionOrderDetail promotion_order_detail) {
		this.promotion_order_detail = promotion_order_detail;
	}
	
}
