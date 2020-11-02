package lixco.com.entity;

public class ContractReqInfo {
	private Contract contract =null;

	public ContractReqInfo() {
	}

	public ContractReqInfo(Contract contract) {
		this.contract = contract;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
}
