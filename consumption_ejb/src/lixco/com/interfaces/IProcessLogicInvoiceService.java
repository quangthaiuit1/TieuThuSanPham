package lixco.com.interfaces;

import java.sql.SQLException;

import javax.transaction.SystemException;

import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.WrapDataInvoiceDetail;
import lixco.com.reqInfo.WrapDelExportBatchReqInfo;
import lixco.com.reqInfo.WrapDelInvoiceDetailReqInfo;
import lixco.com.reqInfo.WrapIEInvocieReqInfo;
import lixco.com.reqInfo.WrapInvoiceDelInfo;
import lixco.com.reqInfo.WrapInvoiceReqInfo;
import lixco.com.reqInfo.WrapOrderLixReqInfo;

public interface IProcessLogicInvoiceService {
	public int deleteInvoiceMaster(WrapInvoiceDelInfo t,Message message) throws IllegalStateException, SystemException, SQLException;
	public int deleteInvoiceDetailMaster(WrapDelInvoiceDetailReqInfo t, Message message) throws IllegalStateException, SystemException, SQLException;
	public int saveOrUpdateMaster(WrapInvoiceReqInfo t, StringBuilder messages) throws IllegalStateException, SystemException, SQLException;
	public int createInvoiceByOrderLix(WrapOrderLixReqInfo t,StringBuilder messages) throws IllegalStateException, SystemException, SQLException;
	public int createInvoiceByIEInvoice(WrapIEInvocieReqInfo t, Message message) throws SQLException, IllegalStateException, SecurityException, SystemException;
	public int deleteExportBatch(WrapDelExportBatchReqInfo t,Message message)throws SQLException, IllegalStateException, SecurityException, SystemException;
	public int saveListWrapExportData(WrapDataInvoiceDetail t,Message message)throws SQLException, IllegalStateException, SecurityException, SystemException;
}
