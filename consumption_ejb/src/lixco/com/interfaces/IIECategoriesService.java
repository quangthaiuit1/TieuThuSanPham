package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.IECategories;
import lixco.com.reqInfo.IECategoriesReqInfo;

public interface IIECategoriesService {
	public int selectAll(List<IECategories> list);
	public int insert(IECategoriesReqInfo t);
	public int update(IECategoriesReqInfo t);
	public int selectById(long id,IECategoriesReqInfo t);
	public int deleteById(long id);
	public int checkCode(String code, long id);
	public int selectByCodeOld(String code, IECategoriesReqInfo t);
	public int selectByCode(String code, IECategoriesReqInfo t);
	public String initCode();
}
