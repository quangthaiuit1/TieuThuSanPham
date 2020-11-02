package lixco.com.reqInfo;

import lixco.com.entity.ProductBrand;

public class ProductBrandReqInfo {
	private ProductBrand product_brand=null;

	public ProductBrandReqInfo() {
	}

	public ProductBrandReqInfo(ProductBrand product_brand) {
		this.product_brand = product_brand;
	}

	public ProductBrand getProduct_brand() {
		return product_brand;
	}

	public void setProduct_brand(ProductBrand product_brand) {
		this.product_brand = product_brand;
	}
	
}
