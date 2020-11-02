package lixco.com.reqInfo;

import lixco.com.entity.OrderDetail;

public class OrderDetailReqInfo {
	private OrderDetail order_detail=null;

	public OrderDetailReqInfo() {
	}

	public OrderDetailReqInfo(OrderDetail order_detail) {
		this.order_detail = order_detail;
	}

	public OrderDetail getOrder_detail() {
		return order_detail;
	}

	public void setOrder_detail(OrderDetail order_detail) {
		this.order_detail = order_detail;
	}
	
}
