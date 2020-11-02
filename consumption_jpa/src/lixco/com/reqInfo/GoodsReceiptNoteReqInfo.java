package lixco.com.reqInfo;

import lixco.com.entity.GoodsReceiptNote;

public class GoodsReceiptNoteReqInfo {
	private GoodsReceiptNote goods_receipt_note=null;

	public GoodsReceiptNoteReqInfo() {
	}

	public GoodsReceiptNoteReqInfo(GoodsReceiptNote goods_receipt_note) {
		this.goods_receipt_note = goods_receipt_note;
	}

	public GoodsReceiptNote getGoods_receipt_note() {
		return goods_receipt_note;
	}

	public void setGoods_receipt_note(GoodsReceiptNote goods_receipt_note) {
		this.goods_receipt_note = goods_receipt_note;
	}
	

}
