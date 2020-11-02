package lixco.com.reqInfo;

import lixco.com.entity.GoodsReceiptNotePosDetail;

public class GoodsReceiptNotePosDetailReqInfo {
	private GoodsReceiptNotePosDetail goods_receipt_note_pos_detail=null;

	public GoodsReceiptNotePosDetailReqInfo() {
	}

	public GoodsReceiptNotePosDetailReqInfo(GoodsReceiptNotePosDetail goods_receipt_note_pos_detail) {
		this.goods_receipt_note_pos_detail = goods_receipt_note_pos_detail;
	}

	public GoodsReceiptNotePosDetail getGoods_receipt_note_pos_detail() {
		return goods_receipt_note_pos_detail;
	}

	public void setGoods_receipt_note_pos_detail(GoodsReceiptNotePosDetail goods_receipt_note_pos_detail) {
		this.goods_receipt_note_pos_detail = goods_receipt_note_pos_detail;
	}
	
}
