package lixco.com.reqInfo;

import lixco.com.entity.Pos;

public class PosReqInfo {
	private Pos pos=null;

	public PosReqInfo() {
	}

	public PosReqInfo(Pos pos) {
		this.pos = pos;
	}

	public Pos getPos() {
		return pos;
	}

	public void setPos(Pos pos) {
		this.pos = pos;
	}
	
}
