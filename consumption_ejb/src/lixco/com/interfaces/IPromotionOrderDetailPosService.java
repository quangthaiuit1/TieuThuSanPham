package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.PromotionOrderDetailPos;
import lixco.com.reqInfo.PromotionOrderDetailPosReqInfo;

public interface IPromotionOrderDetailPosService {
	public int selectByOrder(long orderId,List<PromotionOrderDetailPos> list);
	public int insert(PromotionOrderDetailPosReqInfo t);
	public int update(PromotionOrderDetailPosReqInfo t);
	public int selectById(long id,PromotionOrderDetailPosReqInfo t);
	public int deleteById(long id);
	public int deleteAll(long orderId);
	public int selectBy(String json,List<PromotionOrderDetailPos> list);
	public int deleteBy(long orderDetailPosId);
}
