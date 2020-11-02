package lixco.com.interfaces;

import java.util.Date;
import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.OrderLixPos;
import lixco.com.reqInfo.OrderLixPosReqInfo;

public interface IOrderLixPosService {
	public int search(String json,PagingInfo page,List<OrderLixPos> list);
	public int insert(OrderLixPosReqInfo t);
	public int update(OrderLixPosReqInfo t);
	public int selectById(long id,OrderLixPosReqInfo t);
	public int deleteById(long id);
	public String initOrderCode();
	public String initVoucherCode(Date orderDate,long id);
	public int selectByOrderCode(String orderCode,OrderLixPosReqInfo t);
	public int checkByCode(String code,long id);
}
