package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.ProductType;
import lixco.com.reqInfo.ProductTypeReqInfo;

public interface IProductTypeService {
	public int selectAll(List<ProductType> list);
	public int insert(ProductTypeReqInfo t);
	public int update(ProductTypeReqInfo t);
	public int selectById(long id,ProductTypeReqInfo t);
	public int deleteById(long id);
	public int selectByCode(String code,ProductTypeReqInfo t);
	public int checkCode(String code,long id);
	public int search(String code,String name,List<ProductType> list);
	public int selectBy(String json,List<ProductType> list);
}
