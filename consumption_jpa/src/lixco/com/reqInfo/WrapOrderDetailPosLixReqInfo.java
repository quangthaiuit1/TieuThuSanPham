package lixco.com.reqInfo;


import lixco.com.entity.OrderDetailPos;

public class WrapOrderDetailPosLixReqInfo implements Cloneable{
	private OrderDetailPos order_detail_pos=null;
	private double quantity;//số lượng thùng xuất
	private double quantity_batch;
	
	public WrapOrderDetailPosLixReqInfo() {
	}
	public WrapOrderDetailPosLixReqInfo(OrderDetailPos order_detail_pos) {
		this.order_detail_pos = order_detail_pos;
	}
	public OrderDetailPos getOrder_detail_pos() {
		return order_detail_pos;
	}
	public void setOrder_detail_pos(OrderDetailPos order_detail_pos) {
		this.order_detail_pos = order_detail_pos;
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
	public WrapOrderDetailPosLixReqInfo clone() throws CloneNotSupportedException {
		try{
			WrapOrderDetailPosLixReqInfo cloned=new WrapOrderDetailPosLixReqInfo();
			cloned.setOrder_detail_pos(order_detail_pos==null ? null : order_detail_pos.clone());
			cloned.setQuantity(quantity);
			cloned.setQuantity_batch(quantity_batch);
			return cloned;
		}catch (Exception e) {
		}
		return null;
	}
}
