package lixco.com.reqInfo;

import java.util.ArrayList;
import java.util.List;

import lixco.com.entity.GoodsReceiptNotePos;
import lixco.com.entity.ProductPos;

public class WrapGoodsReceiptNotePosReqInfo implements Cloneable {
	private GoodsReceiptNotePos goods_receipt_note_pos=null;
	private List<WrapGoodsReceiptNotePosDetailReqInfo> list_goods_receipt_note_pos_detail=null;
	private String username;
	
	public WrapGoodsReceiptNotePosReqInfo() {
	}

	public WrapGoodsReceiptNotePosReqInfo(GoodsReceiptNotePos goods_receipt_note_pos,
			List<WrapGoodsReceiptNotePosDetailReqInfo> list_goods_receipt_note_pos_detail,String username) {
		this.goods_receipt_note_pos = goods_receipt_note_pos;
		this.list_goods_receipt_note_pos_detail = list_goods_receipt_note_pos_detail;
		this.username=username;
	}

	public GoodsReceiptNotePos getGoods_receipt_note_pos() {
		return goods_receipt_note_pos;
	}

	public void setGoods_receipt_note_pos(GoodsReceiptNotePos goods_receipt_note_pos) {
		this.goods_receipt_note_pos = goods_receipt_note_pos;
	}

	public List<WrapGoodsReceiptNotePosDetailReqInfo> getList_goods_receipt_note_pos_detail() {
		return list_goods_receipt_note_pos_detail;
	}

	public void setList_goods_receipt_note_pos_detail(
			List<WrapGoodsReceiptNotePosDetailReqInfo> list_goods_receipt_note_pos_detail) {
		this.list_goods_receipt_note_pos_detail = list_goods_receipt_note_pos_detail;
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public WrapGoodsReceiptNotePosReqInfo clone() throws CloneNotSupportedException {
		WrapGoodsReceiptNotePosReqInfo cloned = (WrapGoodsReceiptNotePosReqInfo) super.clone();
		cloned .setGoods_receipt_note_pos(cloned.getGoods_receipt_note_pos().clone());
		List<WrapGoodsReceiptNotePosDetailReqInfo> listDetail=cloned.getList_goods_receipt_note_pos_detail();
		if(listDetail !=null){
			List<WrapGoodsReceiptNotePosDetailReqInfo> listTemp=new ArrayList<>();
			for(WrapGoodsReceiptNotePosDetailReqInfo d:listDetail){
				listTemp.add(d.clone());
				List<ProductPos> listPP=d.getList_product_pos();
				if(listPP !=null){
					List<ProductPos> listPPTemp=new ArrayList<>();
					for(ProductPos p:listPP){
						listPPTemp.add(p.clone());
					}
					d.setList_product_pos(listPPTemp);
				}
			}
			cloned.setList_goods_receipt_note_pos_detail(listTemp);
		}
		return cloned;
	}
	
}
