package lixco.com.reqInfo;

import lixco.com.entity.ContractDetail;

public class ProcessContract {
	private ContractDetail contract_detail=null;
	private double quantity_process_kg;
	private double quantity_remain;
	public ProcessContract() {
	}
	public ProcessContract(ContractDetail contract_detail, double quantity_process_kg) {
		this.contract_detail = contract_detail;
		this.quantity_process_kg = quantity_process_kg;
	}
	public ContractDetail getContract_detail() {
		return contract_detail;
	}
	public void setContract_detail(ContractDetail contract_detail) {
		this.contract_detail = contract_detail;
	}
	public double getQuantity_process_kg() {
		return quantity_process_kg;
	}
	public void setQuantity_process_kg(double quantity_process_kg) {
		this.quantity_process_kg = quantity_process_kg;
	}
	public double getQuantity_remain() {
		return quantity_remain;
	}
	public void setQuantity_remain(double quantity_remain) {
		this.quantity_remain = quantity_remain;
	}
	
	
	
}
