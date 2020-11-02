package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.PricingProgramDetail;
import lixco.com.reqInfo.PricingProgramDetailReqInfo;

public interface IPricingProgramDetailService {
	public int selectAllByPricingProgram(long programId,List<PricingProgramDetail> list);
	public int search(String json,PagingInfo page,List<PricingProgramDetail> list);
	public int insert(PricingProgramDetailReqInfo info);
	public int update(PricingProgramDetailReqInfo info);
	public int selectById(long id,PricingProgramDetailReqInfo info);
	public int deleteById(long id);
	public int checkExsits(long productId,long id,long programId);
	public int deleteAll(long programId);
	public int updateByPredicate(long productId, long programId,double unitPrice, double revenue,double quantity);
	public int findSettingPricing(long programId,long productId,PricingProgramDetailReqInfo t);
	public int findSettingPricingChild(long parentProgramId,long productId,PricingProgramDetailReqInfo t);
}
