package lixco.com.reqInfo;


import lixco.com.entity.OrderDetail;

public class WrapOrderDetailLixReqInfo implements Cloneable{
	private OrderDetail order_detail=null;
	private double quantity;//số lượng thùng xuất
	private double quantity_batch;
	
	public WrapOrderDetailLixReqInfo() {
	}
	public WrapOrderDetailLixReqInfo(OrderDetail order_detail) {
		this.order_detail = order_detail;
	}

	public OrderDetail getOrder_detail() {
		return order_detail;
	}
	public void setOrder_detail(OrderDetail order_detail) {
		this.order_detail = order_detail;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getQuantity_batch() {
		return quantity_batch;
	}
	public void setQuantity_batch(double quantity_batch) {
		this.quantity_batch = quantity_batch;
	}
	@Override
	public WrapOrderDetailLixReqInfo clone() throws CloneNotSupportedException {
		try{
			WrapOrderDetailLixReqInfo cloned=new WrapOrderDetailLixReqInfo();
			cloned.setOrder_detail(order_detail==null ? null : order_detail.clone());
			cloned.setQuantity(quantity);
			cloned.setQuantity_batch(quantity_batch);
			return cloned;
		}catch (Exception e) {
		}
		return null;
	}
	
	
}
