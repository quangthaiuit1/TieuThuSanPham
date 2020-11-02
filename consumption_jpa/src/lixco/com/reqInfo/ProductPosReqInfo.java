package lixco.com.reqInfo;

import lixco.com.entity.ProductPos;

public class ProductPosReqInfo {
	private ProductPos product_pos=null;
	public ProductPosReqInfo() {
	}
	public ProductPosReqInfo(ProductPos product_pos) {
		this.product_pos = product_pos;
	}
	public ProductPos getProduct_pos() {
		return product_pos;
	}
	public void setProduct_pos(ProductPos product_pos) {
		this.product_pos = product_pos;
	}

	
	
}
