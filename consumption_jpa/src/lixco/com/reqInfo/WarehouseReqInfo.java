package lixco.com.reqInfo;

import lixco.com.entity.Warehouse;

public class WarehouseReqInfo {
	private Warehouse warehouse =null;

	public WarehouseReqInfo() {
	}

	public WarehouseReqInfo(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	
}
