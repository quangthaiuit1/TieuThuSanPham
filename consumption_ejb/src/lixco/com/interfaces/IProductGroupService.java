package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.ProductGroup;
import lixco.com.reqInfo.ProductGroupReqInfo;

public interface IProductGroupService {
	public int selectAll(List<ProductGroup> list);
	public int insert(ProductGroupReqInfo t);
	public int update(ProductGroupReqInfo t);
	public int selectById(long id,ProductGroupReqInfo t);
	public int deleteById(long id);
	public int selectByCode(String code,ProductGroupReqInfo t);
	public int checkCode(String code,long id);
	public int search(String code,String name,List<ProductGroup> list);
}
