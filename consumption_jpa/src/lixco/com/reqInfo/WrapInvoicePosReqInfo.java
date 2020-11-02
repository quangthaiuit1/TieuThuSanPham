package lixco.com.reqInfo;

import java.util.List;

import lixco.com.entity.InvoiceDetailPos;
import lixco.com.entity.InvoicePos;

public class WrapInvoicePosReqInfo {
	private InvoicePos invoice_pos=null;
	private List<InvoiceDetailPos> list_invoice_detail_pos=null;
	
	public WrapInvoicePosReqInfo() {
	}

	public WrapInvoicePosReqInfo(InvoicePos invoice_pos, List<InvoiceDetailPos> list_invoice_detail_pos) {
		this.invoice_pos = invoice_pos;
		this.list_invoice_detail_pos = list_invoice_detail_pos;
	}

	public InvoicePos getInvoice_pos() {
		return invoice_pos;
	}

	public void setInvoice_pos(InvoicePos invoice_pos) {
		this.invoice_pos = invoice_pos;
	}

	public List<InvoiceDetailPos> getList_invoice_detail_pos() {
		return list_invoice_detail_pos;
	}

	public void setList_invoice_detail_pos(List<InvoiceDetailPos> list_invoice_detail_pos) {
		this.list_invoice_detail_pos = list_invoice_detail_pos;
	}
	
}
