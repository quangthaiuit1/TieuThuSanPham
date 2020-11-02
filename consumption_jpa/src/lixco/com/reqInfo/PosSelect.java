package lixco.com.reqInfo;

import java.util.List;

import lixco.com.entity.ProductPos;
import lixco.com.entity.Warehouse;

public class PosSelect {
	private Warehouse warehouse;
	private List<ProductPos> listProductPos;
	
	public PosSelect() {
	}
	public PosSelect(Warehouse warehouse, List<ProductPos> listProductPos) {
		this.warehouse = warehouse;
		this.listProductPos = listProductPos;
	}
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	public List<ProductPos> getListProductPos() {
		return listProductPos;
	}
	public void setListProductPos(List<ProductPos> listProductPos) {
		this.listProductPos = listProductPos;
	}
	
}
