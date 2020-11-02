package lixco.com.reqInfo;

import java.util.ArrayList;
import java.util.List;

import lixco.com.entity.OrderLix;
import lixco.com.entity.PromotionOrderDetail;

public class WrapOrderLixReqInfo implements Cloneable{
	private OrderLix order_lix=null;
	private String member_name=null;
	private long member_id;
	private List<WrapOrderDetailLixReqInfo> list_wrap_order_detail=null;
	private List<PromotionOrderDetail> list_promotion_order_detail=null;
	
	public WrapOrderLixReqInfo() {
	}

	public OrderLix getOrder_lix() {
		return order_lix;
	}

	public void setOrder_lix(OrderLix order_lix) {
		this.order_lix = order_lix;
	}

	public String getMember_name() {
		return member_name;
	}

	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}

	public long getMember_id() {
		return member_id;
	}

	public void setMember_id(long member_id) {
		this.member_id = member_id;
	}

	public List<WrapOrderDetailLixReqInfo> getList_wrap_order_detail() {
		return list_wrap_order_detail;
	}

	public void setList_wrap_order_detail(List<WrapOrderDetailLixReqInfo> list_wrap_order_detail) {
		this.list_wrap_order_detail = list_wrap_order_detail;
	}

	public List<PromotionOrderDetail> getList_promotion_order_detail() {
		return list_promotion_order_detail;
	}

	public void setList_promotion_order_detail(List<PromotionOrderDetail> list_promotion_order_detail) {
		this.list_promotion_order_detail = list_promotion_order_detail;
	}
	@Override
	public WrapOrderLixReqInfo clone() throws CloneNotSupportedException {
		try{
			WrapOrderLixReqInfo cloned=new WrapOrderLixReqInfo();
			cloned.setOrder_lix(order_lix==null? null : order_lix.clone());
			cloned.setMember_id(member_id);
			cloned.setMember_name(member_name);
			if(list_wrap_order_detail !=null){
				List<WrapOrderDetailLixReqInfo> list1=new ArrayList<WrapOrderDetailLixReqInfo>();
				for(WrapOrderDetailLixReqInfo w1:list_wrap_order_detail){
					list1.add(w1.clone());
				}
				cloned.setList_wrap_order_detail(list1);
			}
			if(list_promotion_order_detail !=null){
				List<PromotionOrderDetail> list2=new ArrayList<>();
				for(PromotionOrderDetail p:list_promotion_order_detail){
					list2.add(p.clone());
				}
				cloned.setList_promotion_order_detail(list2);
			}
			return cloned;
		}catch (Exception e) {
		}
		
		return null;
	}
}
