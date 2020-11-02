package lixco.com.reqInfo;

import lixco.com.entity.Stocker;

public class StockerReqInfo {
	private Stocker stocker=null;

	public StockerReqInfo() {
	}

	public StockerReqInfo(Stocker stocker) {
		this.stocker = stocker;
	}

	public Stocker getStocker() {
		return stocker;
	}

	public void setStocker(Stocker stocker) {
		this.stocker = stocker;
	}
	
}
