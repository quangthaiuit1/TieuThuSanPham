package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.InvoiceDetailPos;

public interface IInvoiceDetailPosService {
	public int selectByOrder(long invoiceId,List<InvoiceDetailPos> list);
}
