package lixco.com.reqInfo;

import lixco.com.entity.Invoice;

public class InvoiceReqInfo {
	private Invoice invoice=null;

	public InvoiceReqInfo() {
	}

	public InvoiceReqInfo(Invoice invoice) {
		this.invoice = invoice;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
}
