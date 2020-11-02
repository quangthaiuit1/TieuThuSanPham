package lixco.com.reqInfo;

import lixco.com.entity.FreightContract;

public class FreightContractReqInfo {
	private FreightContract freight_contract=null;

	public FreightContractReqInfo() {
	}
	

	public FreightContractReqInfo(FreightContract freight_contract) {
		this.freight_contract = freight_contract;
	}


	public FreightContract getFreight_contract() {
		return freight_contract;
	}

	public void setFreight_contract(FreightContract freight_contract) {
		this.freight_contract = freight_contract;
	}
	
}
