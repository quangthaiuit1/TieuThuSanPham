package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.ExportBatch;
import lixco.com.entity.ExportBatchPos;
import lixco.com.entity.Invoice;
import lixco.com.reqInfo.InvoiceReqInfo;

public interface IInvoiceService {
	public int search(String json,PagingInfo page,List<Invoice> list);
	public int selectById(long id,InvoiceReqInfo t);
	public int selectByInvoiceOwn(long idOwn,InvoiceReqInfo t);
//	public int processingContract(List<Long> listProductId,Date effectiveDate,Date expiryDate,List<Object[]> list);
	public int processingContract(String json,List<Object[]> list);
	public int getListExportBatch(long invoiceDetailId, List<ExportBatch> list);
	
}
