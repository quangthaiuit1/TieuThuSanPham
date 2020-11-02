package lixco.com.interfaces;

import java.sql.SQLException;

import javax.transaction.SystemException;

import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.WrapGoodsReceiptNotePosReqInfo;

public interface IProcessLogicGoodsReceiptNotePosService {
	public int deleteGoodsReceiptNoteMaster(long id,Message messages) throws IllegalStateException, SystemException, SQLException;
	public int deleteGoodsReciptNoteDetailMaster(long id,Message messages) throws IllegalStateException, SystemException, SQLException;
	public int saveOrUpdateGoodsReceiptNotePosService(WrapGoodsReceiptNotePosReqInfo t, Message messages,int status) throws IllegalStateException, SystemException, SQLException;
	public int deleteProductPos(long id, Message messages) throws IllegalStateException, SystemException, SQLException;
}
