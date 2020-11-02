package lixco.com.interfaces;

import java.sql.SQLException;

import javax.transaction.SystemException;

import lixco.com.reqInfo.InvoiceDetailPosReqInfo;
import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.WrapDataInvoiceDetailPos;
import lixco.com.reqInfo.WrapInvoicePosReqInfo;
import lixco.com.reqInfo.WrapOrderLixPosReqInfo;

public interface IProcessLogicInvoicePosService {
	public int deleteInvoicePosMaster(long id,StringBuilder messages) throws IllegalStateException, SystemException, SQLException;
	public int deleteInvoiceDetailPosMaster(long id,StringBuilder messages) throws IllegalStateException, SystemException, SQLException;
	public int insertInvoicePosDetail(InvoiceDetailPosReqInfo t,StringBuilder messages) throws IllegalStateException, SystemException, SQLException;
	public int updateInvoicePosDetail(InvoiceDetailPosReqInfo t,StringBuilder messages) throws IllegalStateException, SystemException, SQLException;
	public int saveOrUpdateMasterTemp(WrapInvoicePosReqInfo t, StringBuilder messages) throws IllegalStateException, SystemException, SQLException;
	public int saveOrUpdateMaster(WrapInvoicePosReqInfo t, StringBuilder messages) throws IllegalStateException, SystemException, SQLException;
	public int selectInvoiceDetailPosById(long id,InvoiceDetailPosReqInfo t)throws IllegalStateException, SystemException, SQLException;
	public int selectInvoicePosById(long id,WrapInvoicePosReqInfo t)throws IllegalStateException, SystemException, SQLException;
	public int createInvoicePosByOrder(WrapOrderLixPosReqInfo t,StringBuilder messages) throws IllegalStateException, SystemException, SQLException;
	public int saveListWrapExportDataPos(WrapDataInvoiceDetailPos t,Message message) throws IllegalStateException, SystemException, SQLException;
}
