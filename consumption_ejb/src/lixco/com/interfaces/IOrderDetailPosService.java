package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.OrderDetailPos;
import lixco.com.reqInfo.OrderDetailPosReqInfo;

public interface IOrderDetailPosService {
	public int selectByOrder(long orderId,List<OrderDetailPos> list);
	public int insert(OrderDetailPosReqInfo info);
	public int update(OrderDetailPosReqInfo info);
	public int selectById(long id,OrderDetailPosReqInfo info);
	public int deleteById(long id);
	public int checkExsits(long productId,long id,long orderId);
	public int deleteAll(long orderId);
}
