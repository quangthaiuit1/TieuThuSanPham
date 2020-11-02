package lixco.com.interfaces;

import java.sql.SQLException;

import javax.transaction.SystemException;

import lixco.com.reqInfo.GoodsReceiptNoteDetailReqInfo;
import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.WrapGoodsReceiptNoteReqInfo;

public interface IProcessLogicGoodsReceiptNoteService {
	public int deleteGoodsReceiptNoteMaster(long id,Message message) throws IllegalStateException, SystemException, SQLException;
	public int deleteGoodsReceiptNoteDetailMaster(long id,Message message) throws IllegalStateException, SystemException, SQLException;
	public int insertGoodsReceiptNoteDetail(GoodsReceiptNoteDetailReqInfo t,StringBuilder message) throws IllegalStateException, SystemException, SQLException;
	public int updateGoodsReceiptNoteDetail(GoodsReceiptNoteDetailReqInfo t,StringBuilder message) throws IllegalStateException, SystemException, SQLException;
	public int saveOrUpdateGoodsReceiptNoteService(WrapGoodsReceiptNoteReqInfo t, Message message) throws IllegalStateException, SystemException, SQLException;
}
