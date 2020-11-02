package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.HarborCategory;
import lixco.com.reqInfo.HarborCategoryReqInfo;

public interface IHarborCategoryService {
	public int selectAll(List<HarborCategory> list);
	public int insert(HarborCategoryReqInfo t);
	public int update(HarborCategoryReqInfo t);
	public int selectById(long id,HarborCategoryReqInfo t);
	public int deleteById(long id);
	public int search(int harborType,List<HarborCategory> list);
	public int initCode(HarborCategory t);
	public int selectByCode(String code,HarborCategoryReqInfo t);
}
