package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.GoodsReceiptNotePos;
import lixco.com.entity.GoodsReceiptNotePosDetail;
import lixco.com.entity.ProductPos;
import lixco.com.reqInfo.GoodsReceiptNotePosReqInfo;

public interface IGoodsReceiptNotePosService {
	public int search(String json,PagingInfo page,List<GoodsReceiptNotePos> list);
	public int selectById(long id,GoodsReceiptNotePosReqInfo t);
	public int searchDetail(String json,PagingInfo page,List<GoodsReceiptNotePosDetail> list);
	public int selectByReceiptNotePos(long receiptId,List<GoodsReceiptNotePosDetail> list);
	public int selectListProductPos(long receiptDetailId,List<ProductPos> list);
}
