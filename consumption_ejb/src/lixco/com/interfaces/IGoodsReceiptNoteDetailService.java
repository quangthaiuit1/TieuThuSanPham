package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.GoodsReceiptNoteDetail;
import lixco.com.reqInfo.GoodsReceiptNoteDetailReqInfo;

public interface IGoodsReceiptNoteDetailService {
	public int search(String json,PagingInfo page,List<GoodsReceiptNoteDetail> list);
	public int insert(GoodsReceiptNoteDetailReqInfo t);
	public int update(GoodsReceiptNoteDetailReqInfo t);
	public int selectById(long id,GoodsReceiptNoteDetailReqInfo t);
	public int deleteById(long id);
	public int selectByCode(String code,List<GoodsReceiptNoteDetail> list);
	public int selectByReceiptNote(long receiptId,List<GoodsReceiptNoteDetail> list);
}
