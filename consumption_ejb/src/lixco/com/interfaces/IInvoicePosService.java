package lixco.com.interfaces;

import java.util.Date;
import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.ExportBatchPos;
import lixco.com.entity.InvoicePos;

public interface IInvoicePosService {
	public int search(String json,PagingInfo page,List<InvoicePos> list);
	public int getListExportBatchPos(long invoiceDetailId,List<ExportBatchPos> list);
	public int processingContract(List<Long> listProductId,Date effectiveDate,Date expiryDate,List<Object[]> list);
}
