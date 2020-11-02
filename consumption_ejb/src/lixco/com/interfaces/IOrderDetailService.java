package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.OrderDetail;
import lixco.com.reqInfo.OrderDetailReqInfo;

public interface IOrderDetailService {
	public int selectByOrder(long orderId,List<OrderDetail> list);
	public int insert(OrderDetailReqInfo info);
	public int update(OrderDetailReqInfo info);
	public int selectById(long id,OrderDetailReqInfo info);
	public int deleteById(long id);
	public int checkExsits(long productId,long id,long orderId);
	public int deleteAll(long orderId);
}
