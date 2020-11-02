package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.PricingProgram;
import lixco.com.reqInfo.PricingProgramReqInfo;

public interface IPricingProgramService {
	public int selectAll(List<PricingProgram> list);
	public int search(String json,PagingInfo page,List<PricingProgram> list);
	public int insert(PricingProgramReqInfo info);
	public int update(PricingProgramReqInfo info);
	public int selectById(long id,PricingProgramReqInfo info);
	public int deleteById(long id);
	public int getDateMaxSubPricingProgram(long parent_program_id,StringBuilder list);
	public String initPricingProgramCode();
	public int findLike(String text,int size,List<PricingProgram> list);
	public int complete(String text,List<PricingProgram> list);
	public int selectByCode(String code,PricingProgramReqInfo t);
}
