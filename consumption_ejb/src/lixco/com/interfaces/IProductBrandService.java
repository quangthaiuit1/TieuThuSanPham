package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.ProductBrand;
import lixco.com.reqInfo.ProductBrandReqInfo;

public interface IProductBrandService {
	public int selectAll(List<ProductBrand> list);
	public int search(String json,PagingInfo page,List<ProductBrand> list);
	public int insert(ProductBrandReqInfo t);
	public int update(ProductBrandReqInfo t);
	public int selectById(long id,ProductBrandReqInfo t);
	public int deleteById(long id);
	public int findLike(String text,int size,List<ProductBrand> list);
	public int checkProductBrandCode(String code,long productBrandId);
	public int selectByCode(String code,ProductBrandReqInfo t);
}
