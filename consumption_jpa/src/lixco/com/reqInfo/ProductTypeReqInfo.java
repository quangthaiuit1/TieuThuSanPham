package lixco.com.reqInfo;

import lixco.com.entity.ProductType;

public class ProductTypeReqInfo {
	private ProductType product_type =null;

	public ProductTypeReqInfo() {
	}

	public ProductTypeReqInfo(ProductType product_type) {
		this.product_type = product_type;
	}

	public ProductType getProduct_type() {
		return product_type;
	}

	public void setProduct_type(ProductType product_type) {
		this.product_type = product_type;
	}
	
}
