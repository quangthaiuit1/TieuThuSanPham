package lixco.com.reqInfo;

import lixco.com.entity.InvoiceDetailPos;

public class InvoiceDetailPosReqInfo {
	private InvoiceDetailPos invoice_detail_pos=null;

	public InvoiceDetailPosReqInfo() {
	}

	public InvoiceDetailPosReqInfo(InvoiceDetailPos invoice_detail_pos) {
		this.invoice_detail_pos = invoice_detail_pos;
	}

	public InvoiceDetailPos getInvoice_detail_pos() {
		return invoice_detail_pos;
	}

	public void setInvoice_detail_pos(InvoiceDetailPos invoice_detail_pos) {
		this.invoice_detail_pos = invoice_detail_pos;
	}

	
}
