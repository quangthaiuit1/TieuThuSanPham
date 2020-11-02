package lixco.com.reqInfo;

import lixco.com.entity.Stevedore;

public class StevedoreReqInfo {
	private Stevedore stevedore=null;

	public StevedoreReqInfo() {
	}

	public StevedoreReqInfo(Stevedore stevedore) {
		this.stevedore = stevedore;
	}

	public Stevedore getStevedore() {
		return stevedore;
	}

	public void setStevedore(Stevedore stevedore) {
		this.stevedore = stevedore;
	}
	
}
