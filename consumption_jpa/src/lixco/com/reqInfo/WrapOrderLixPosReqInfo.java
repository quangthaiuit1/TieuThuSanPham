package lixco.com.reqInfo;

import java.util.ArrayList;
import java.util.List;

import lixco.com.entity.OrderLixPos;
import lixco.com.entity.PromotionOrderDetailPos;

public class WrapOrderLixPosReqInfo implements Cloneable{
	private OrderLixPos order_lix_pos=null;
	private String created_by=null;
	private long created_by_id;
	private List<WrapOrderDetailPosLixReqInfo> list_wrap_order_detail_pos=null;
	private List<PromotionOrderDetailPos> list_promotion_order_detail_pos=null;
	
	public WrapOrderLixPosReqInfo() {
	}

	public WrapOrderLixPosReqInfo(OrderLixPos order_lix_pos, String created_by,
			List<WrapOrderDetailPosLixReqInfo> list_wrap_order_detail_pos,
			List<PromotionOrderDetailPos> list_promotion_order_detail_pos) {
		this.order_lix_pos = order_lix_pos;
		this.created_by = created_by;
		this.list_wrap_order_detail_pos = list_wrap_order_detail_pos;
		this.list_promotion_order_detail_pos = list_promotion_order_detail_pos;
	}

	public OrderLixPos getOrder_lix_pos() {
		return order_lix_pos;
	}
	public void setOrder_lix_pos(OrderLixPos order_lix_pos) {
		this.order_lix_pos = order_lix_pos;
	}
	public long getCreated_by_id() {
		return created_by_id;
	}

	public void setCreated_by_id(long created_by_id) {
		this.created_by_id = created_by_id;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public List<WrapOrderDetailPosLixReqInfo> getList_wrap_order_detail_pos() {
		return list_wrap_order_detail_pos;
	}

	public void setList_wrap_order_detail_pos(List<WrapOrderDetailPosLixReqInfo> list_wrap_order_detail_pos) {
		this.list_wrap_order_detail_pos = list_wrap_order_detail_pos;
	}

	public List<PromotionOrderDetailPos> getList_promotion_order_detail_pos() {
		return list_promotion_order_detail_pos;
	}

	public void setList_promotion_order_detail_pos(List<PromotionOrderDetailPos> list_promotion_order_detail_pos) {
		this.list_promotion_order_detail_pos = list_promotion_order_detail_pos;
	}
	
	@Override
	public WrapOrderLixPosReqInfo clone() throws CloneNotSupportedException {
		try{
			WrapOrderLixPosReqInfo cloned=new WrapOrderLixPosReqInfo();
			cloned.setOrder_lix_pos(order_lix_pos==null ? null : order_lix_pos.clone());
			cloned.setCreated_by(created_by);
			cloned.setCreated_by_id(created_by_id);
			if(list_wrap_order_detail_pos !=null){
				List<WrapOrderDetailPosLixReqInfo> list1=new ArrayList<>();
				for(WrapOrderDetailPosLixReqInfo w1:list_wrap_order_detail_pos){
					list1.add(w1.clone());
				}
				cloned.setList_wrap_order_detail_pos(list1);
			}
			if(list_promotion_order_detail_pos !=null){
				List<PromotionOrderDetailPos> list2=new ArrayList<>();
				for(PromotionOrderDetailPos p:list_promotion_order_detail_pos){
					list2.add(p.clone());
				}
				cloned.setList_promotion_order_detail_pos(list2);
			}
			return cloned;
		}catch (Exception e) {
		}
		
		return null;
	}
}
