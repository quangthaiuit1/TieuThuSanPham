package lixco.com.interfaces;

import java.util.Date;
import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.OrderLix;
import lixco.com.reqInfo.OrderLixReqInfo;

public interface IOrderLixService {
	public int search(String json,PagingInfo page,List<OrderLix> list);
	public int insert(OrderLixReqInfo t);
	public int update(OrderLixReqInfo t);
	public int selectById(long id,OrderLixReqInfo t);
	public int deleteById(long id);
	public String initOrderCode();
	public String initVoucherCode(Date orderDate,long id);
	public int selectByOrderCode(String orderCode,OrderLixReqInfo t);
	public int checkByCode(String code,long id);
}
