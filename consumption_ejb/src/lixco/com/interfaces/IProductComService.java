package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.ProductCom;
import lixco.com.reqInfo.ProductComReqInfo;

public interface IProductComService {
	public int selectAll(List<ProductCom> list);
	public int search(String json,PagingInfo page,List<ProductCom> list);
	public int insert(ProductComReqInfo t);
	public int update(ProductComReqInfo t);
	public int selectById(long id,ProductComReqInfo t);
	public int deleteById(long id);
	public int findLike(String text,int pageSize,List<ProductCom> list);
	public int checkProductComCode(String code,long pComId);
	public int selectByCode(String code,ProductComReqInfo t);
}
