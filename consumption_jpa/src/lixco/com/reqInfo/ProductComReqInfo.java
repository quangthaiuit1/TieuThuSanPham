package lixco.com.reqInfo;

import lixco.com.entity.ProductCom;

public class ProductComReqInfo {
	private ProductCom product_com=null;

	public ProductComReqInfo() {
	}

	public ProductComReqInfo(ProductCom product_com) {
		this.product_com = product_com;
	}

	public ProductCom getProduct_com() {
		return product_com;
	}

	public void setProduct_com(ProductCom product_com) {
		this.product_com = product_com;
	}
	
}
