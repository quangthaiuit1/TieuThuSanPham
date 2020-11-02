package lixco.com.reqInfo;

import lixco.com.entity.HarborCategory;

public class HarborCategoryReqInfo {
	private HarborCategory harbor_category=null;

	public HarborCategoryReqInfo() {
	}

	public HarborCategoryReqInfo(HarborCategory harbor_category) {
		this.harbor_category = harbor_category;
	}

	public HarborCategory getHarbor_category() {
		return harbor_category;
	}

	public void setHarbor_category(HarborCategory harbor_category) {
		this.harbor_category = harbor_category;
	}
	
}
