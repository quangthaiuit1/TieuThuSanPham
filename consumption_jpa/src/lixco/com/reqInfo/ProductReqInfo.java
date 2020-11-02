package lixco.com.reqInfo;

import lixco.com.entity.Product;

public class ProductReqInfo {
	private Product product=null;

	public ProductReqInfo() {
	}

	public ProductReqInfo(Product product) {
		this.product = product;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
}
