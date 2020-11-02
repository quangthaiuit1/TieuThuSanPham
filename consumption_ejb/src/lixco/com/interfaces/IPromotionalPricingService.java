package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.PromotionalPricing;
import lixco.com.reqInfo.PromotionalPricingReqInfo;

public interface IPromotionalPricingService {
	public int search(String json,PagingInfo page,List<PromotionalPricing> list);
	public int insert(PromotionalPricingReqInfo t);
	public int update(PromotionalPricingReqInfo t);
	public int selectById(long id,PromotionalPricingReqInfo t);
	public int deleteById(long id);
	public int checkExsits(PromotionalPricing p);
	public int deleteAll();
	public int findSettingPromotionalPricing(String json,PromotionalPricingReqInfo t);
	
}
