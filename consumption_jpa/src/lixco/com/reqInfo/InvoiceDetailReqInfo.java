package lixco.com.reqInfo;

import lixco.com.entity.InvoiceDetail;

public class InvoiceDetailReqInfo {
	private InvoiceDetail invoice_detail=null;

	public InvoiceDetailReqInfo() {
	}

	public InvoiceDetailReqInfo(InvoiceDetail invoice_detail) {
		this.invoice_detail = invoice_detail;
	}

	public InvoiceDetail getInvoice_detail() {
		return invoice_detail;
	}

	public void setInvoice_detail(InvoiceDetail invoice_detail) {
		this.invoice_detail = invoice_detail;
	}
	
}
