package lixco.com.reqInfo;

import lixco.com.entity.ProductGroup;

public class ProductGroupReqInfo {
	private ProductGroup product_group =null;

	public ProductGroupReqInfo() {
	}

	public ProductGroupReqInfo(ProductGroup product_Group) {
		this.product_group = product_Group;
	}

	public ProductGroup getProduct_Group() {
		return product_group;
	}

	public void setProduct_Group(ProductGroup product_Group) {
		this.product_group = product_Group;
	}
	
}
