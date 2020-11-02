package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.PromotionProductGroup;
import lixco.com.reqInfo.PromotionProductGroupReqInfo;


public interface IPromotionProductGroupService {
	public int selectAll(List<PromotionProductGroup> list);
	public int insert(PromotionProductGroupReqInfo t);
	public int update(PromotionProductGroupReqInfo t);
	public int selectById(long id,PromotionProductGroupReqInfo t);
	public int selectByCode(String code,PromotionProductGroupReqInfo t);
	public int deleteById(long id);
	public int search(String json,List<PromotionProductGroup> list);
	public int deleteAll();
	public int complete(String text,int size,List<PromotionProductGroup> list);
}
