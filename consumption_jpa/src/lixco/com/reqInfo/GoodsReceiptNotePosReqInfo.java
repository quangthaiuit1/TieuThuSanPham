package lixco.com.reqInfo;

import lixco.com.entity.GoodsReceiptNotePos;

public class GoodsReceiptNotePosReqInfo {
	private GoodsReceiptNotePos goods_receipt_note_pos=null;

	public GoodsReceiptNotePosReqInfo() {
	}

	public GoodsReceiptNotePosReqInfo(GoodsReceiptNotePos goods_receipt_note_pos) {
		this.goods_receipt_note_pos = goods_receipt_note_pos;
	}

	public GoodsReceiptNotePos getGoods_receipt_note_pos() {
		return goods_receipt_note_pos;
	}

	public void setGoods_receipt_note_pos(GoodsReceiptNotePos goods_receipt_note_pos) {
		this.goods_receipt_note_pos = goods_receipt_note_pos;
	}
	
}
