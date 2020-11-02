package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.GoodsReceiptNote;
import lixco.com.reqInfo.GoodsReceiptNoteReqInfo;

public interface IGoodsReceiptNoteService {
	public int search(String json,PagingInfo page,List<GoodsReceiptNote> list);
	public int insert(GoodsReceiptNoteReqInfo t);
	public int update(GoodsReceiptNoteReqInfo t);
	public int selectById(long id,GoodsReceiptNoteReqInfo t);
	public int deleteById(long id);

}
