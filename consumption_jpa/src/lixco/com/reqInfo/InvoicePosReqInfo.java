package lixco.com.reqInfo;

import lixco.com.entity.InvoicePos;

public class InvoicePosReqInfo {
	private InvoicePos invoice_pos=null;

	public InvoicePosReqInfo() {
	}

	public InvoicePosReqInfo(InvoicePos invoice_pos) {
		this.invoice_pos = invoice_pos;
	}

	public InvoicePos getInvoice_pos() {
		return invoice_pos;
	}

	public void setInvoice_pos(InvoicePos invoice_pos) {
		this.invoice_pos = invoice_pos;
	}
}
