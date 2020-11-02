package lixco.com.reqInfo;

import lixco.com.entity.Carrier;

public class CarrierReqInfo {
	private Carrier carrier=null;

	public CarrierReqInfo() {
	}
	public CarrierReqInfo(Carrier carrier) {
		this.carrier=carrier;
	}

	public Carrier getCarrier() {
		return carrier;
	}

	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}
	

}
