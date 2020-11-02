package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.Product;
import lixco.com.reqInfo.ProductReqInfo;

public interface IProductService {
	public int selectAll(List<Product> list);
	public int search(String json,PagingInfo page,List<Product> list);
	public int insert(ProductReqInfo info);
	public int update(ProductReqInfo info);
	public int selectById(long id,ProductReqInfo info);
	public int deleteById(long id);
	public int findLike(String text,int size,List<Product> list);
	public int checkProductCode(String code,long productId);
	public int complete(String text,List<Product> list);
	public int complete2(String text,List<Product> list);
	public int complete3(String text,List<Product> list);
	public int selectByCode(String code,ProductReqInfo reqInfo);
}
