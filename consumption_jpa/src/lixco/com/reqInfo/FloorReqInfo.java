package lixco.com.reqInfo;

import lixco.com.entity.Floor;

public class FloorReqInfo {
	private Floor floor=null;

	public FloorReqInfo() {
	}

	public FloorReqInfo(Floor floor) {
		this.floor = floor;
	}

	public Floor getFloor() {
		return floor;
	}

	public void setFloor(Floor floor) {
		this.floor = floor;
	}
	
}
