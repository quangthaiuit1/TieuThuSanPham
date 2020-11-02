package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.PromotionOrderDetail;
import lixco.com.reqInfo.PromotionOrderDetailReqInfo;

public interface IPromotionOrderDetailService {
	public int selectByOrder(long orderId,List<PromotionOrderDetail> list);
	public int insert(PromotionOrderDetailReqInfo t);
	public int update(PromotionOrderDetailReqInfo t);
	public int selectById(long id,PromotionOrderDetailReqInfo t);
	public int deleteById(long id);
	public int deleteAll(long orderId);
	public int selectBy(String json,List<PromotionOrderDetail> list);
	public int deleteBy(long orderDetailId);
}
