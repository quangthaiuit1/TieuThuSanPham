package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.PromotionProgram;
import lixco.com.reqInfo.PromotionProgramReqInfo;

public interface IPromotionProgramService {
	public int selectAll(List<PromotionProgram> list);
	public int search(String json,PagingInfo page,List<PromotionProgram> list);
	public int insert(PromotionProgramReqInfo t);
	public int update(PromotionProgramReqInfo t);
	public int selectById(long id,PromotionProgramReqInfo t);
	public int deleteById(long id);
	public String initPromotionProgramCode();
	public int findLike(String text,int size,List<PromotionProgram> list);
	public int complete(String text,List<PromotionProgram> list);
	public int selectByCode(String programCode,PromotionProgramReqInfo t);
}
