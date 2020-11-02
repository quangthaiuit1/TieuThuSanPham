package lixco.com.reqInfo;

import java.util.List;

import lixco.com.entity.GoodsReceiptNotePosDetail;
import lixco.com.entity.ProductPos;

public class WrapGoodsReceiptNotePosDetailReqInfo implements Cloneable{
	private GoodsReceiptNotePosDetail goods_receipt_note_pos_detail=null;
	private List<ProductPos> list_product_pos=null;
	
	public WrapGoodsReceiptNotePosDetailReqInfo() {
	}
	
	public WrapGoodsReceiptNotePosDetailReqInfo(GoodsReceiptNotePosDetail goods_receipt_note_pos_detail,
			List<ProductPos> list_product_pos) {
		this.goods_receipt_note_pos_detail = goods_receipt_note_pos_detail;
		this.list_product_pos = list_product_pos;
	}

	public GoodsReceiptNotePosDetail getGoods_receipt_note_pos_detail() {
		return goods_receipt_note_pos_detail;
	}
	public void setGoods_receipt_note_pos_detail(GoodsReceiptNotePosDetail goods_receipt_note_pos_detail) {
		this.goods_receipt_note_pos_detail = goods_receipt_note_pos_detail;
	}
	public List<ProductPos> getList_product_pos() {
		return list_product_pos;
	}
	public void setList_product_pos(List<ProductPos> list_product_pos) {
		this.list_product_pos = list_product_pos;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WrapGoodsReceiptNotePosDetailReqInfo other = (WrapGoodsReceiptNotePosDetailReqInfo) obj;
		if (goods_receipt_note_pos_detail == null) {
			if (other.goods_receipt_note_pos_detail != null)
				return false;
		} else if (!goods_receipt_note_pos_detail.equals(other.goods_receipt_note_pos_detail)){
			return false;
		}
		if(goods_receipt_note_pos_detail !=null && goods_receipt_note_pos_detail.getId()==0 && other.goods_receipt_note_pos_detail!=null && other.goods_receipt_note_pos_detail.getId()==0){
			
			if(!goods_receipt_note_pos_detail.getProduct().equals(other.goods_receipt_note_pos_detail.getProduct()) 
					|| !goods_receipt_note_pos_detail.getBatch_code().equals(other.goods_receipt_note_pos_detail.getBatch_code())){
				return false;
			}
			
		}
		return true;
	}
	@Override
	public WrapGoodsReceiptNotePosDetailReqInfo  clone() throws CloneNotSupportedException {
		return (WrapGoodsReceiptNotePosDetailReqInfo)super.clone();
	}
	
}
