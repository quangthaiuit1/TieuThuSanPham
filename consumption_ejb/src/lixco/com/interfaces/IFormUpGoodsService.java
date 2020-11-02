package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.FormUpGoods;
import lixco.com.reqInfo.FormUpGoodsReqInfo;

public interface IFormUpGoodsService {
	public int selectAll(List<FormUpGoods> list);
	public int insert(FormUpGoodsReqInfo t);
	public int update(FormUpGoodsReqInfo t);
	public int selectById(long id,FormUpGoodsReqInfo t);
	public int deleteById(long id);
}
