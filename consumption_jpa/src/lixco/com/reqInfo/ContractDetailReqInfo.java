package lixco.com.reqInfo;

import lixco.com.entity.ContractDetail;

public class ContractDetailReqInfo {
	private ContractDetail contract_detail=null;

	public ContractDetailReqInfo() {
	}
	public ContractDetailReqInfo(ContractDetail contract_detail) {
		this.contract_detail = contract_detail;
	}
	public ContractDetail getContract_detail() {
		return contract_detail;
	}
	public void setContract_detail(ContractDetail contract_detail) {
		this.contract_detail = contract_detail;
	}
	
}
