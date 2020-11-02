package lixco.com.reqInfo;

import java.util.ArrayList;
import java.util.List;

import lixco.com.entity.GoodsReceiptNote;
import lixco.com.entity.GoodsReceiptNoteDetail;

public class WrapGoodsReceiptNoteReqInfo implements Cloneable {
	private GoodsReceiptNote goods_receipt_note=null;
	private List<GoodsReceiptNoteDetail> list_goods_receipt_note_detail=null;
	private String member_name;
	private long member_id;
	
	public WrapGoodsReceiptNoteReqInfo(GoodsReceiptNote goods_receipt_note,
			List<GoodsReceiptNoteDetail> list_goods_receipt_note_detail) {
		this.goods_receipt_note = goods_receipt_note;
		this.list_goods_receipt_note_detail = list_goods_receipt_note_detail;
	}
	
	public WrapGoodsReceiptNoteReqInfo() {
	}
	public GoodsReceiptNote getGoods_receipt_note() {
		return goods_receipt_note;
	}

	public void setGoods_receipt_note(GoodsReceiptNote goods_receipt_note) {
		this.goods_receipt_note = goods_receipt_note;
	}

	public List<GoodsReceiptNoteDetail> getList_goods_receipt_note_detail() {
		return list_goods_receipt_note_detail;
	}
	public void setList_goods_receipt_note_detail(List<GoodsReceiptNoteDetail> list_goods_receipt_note_detail) {
		this.list_goods_receipt_note_detail = list_goods_receipt_note_detail;
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
	@Override
	public WrapGoodsReceiptNoteReqInfo clone() throws CloneNotSupportedException {
		WrapGoodsReceiptNoteReqInfo cloned = (WrapGoodsReceiptNoteReqInfo) super.clone();
		cloned.setGoods_receipt_note(cloned.getGoods_receipt_note().clone());
		List<GoodsReceiptNoteDetail> listDetail=cloned.getList_goods_receipt_note_detail();
		if(listDetail !=null){
			List<GoodsReceiptNoteDetail> listTemp=new ArrayList<>();
			for(GoodsReceiptNoteDetail d:listDetail){
				listTemp.add(d.clone());
			}
			cloned.setList_goods_receipt_note_detail(listTemp);
		}
		return cloned;
	}
}
