package lixco.com.reqInfo;

import lixco.com.entity.Pos;

public class WrapPosReqInfo {
	private Pos pos=null;
	private double quantity;
	
	public WrapPosReqInfo() {
	}
	public WrapPosReqInfo(Pos pos, double quantity) {
		this.pos = pos;
		this.quantity = quantity;
	}
	public Pos getPos() {
		return pos;
	}
	public void setPos(Pos pos) {
		this.pos = pos;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
}
