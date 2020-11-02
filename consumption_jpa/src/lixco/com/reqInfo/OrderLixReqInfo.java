package lixco.com.reqInfo;

import lixco.com.entity.OrderLix;

public class OrderLixReqInfo {
	private OrderLix order_lix=null;

	public OrderLixReqInfo() {
	}

	public OrderLixReqInfo(OrderLix order_lix) {
		this.order_lix = order_lix;
	}

	public OrderLix getOrder_lix() {
		return order_lix;
	}

	public void setOrder_lix(OrderLix order_lix) {
		this.order_lix = order_lix;
	}
	
}
