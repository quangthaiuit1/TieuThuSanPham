package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.InvoiceDetail;
import lixco.com.reqInfo.InvoiceDetailReqInfo;

public interface IInvoiceDetailService {
	public int selectByOrder(long invoiceId,List<InvoiceDetail> list);
	public int selectById(long id,InvoiceDetailReqInfo t);
	public int selectByInvoiceDetailMain(long idMain,List<InvoiceDetail> list);
}
