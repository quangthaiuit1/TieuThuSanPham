package lixco.com.reqInfo;

import java.util.List;

import lixco.com.entity.Invoice;
import lixco.com.entity.InvoiceDetail;

public class WrapInvoiceReqInfo {
	private Invoice invoice=null;
	private List<InvoiceDetail> list_invoice_detail=null;
	
	public WrapInvoiceReqInfo() {
	}
	public WrapInvoiceReqInfo(Invoice invoice, List<InvoiceDetail> list_invoice_detail) {
		this.invoice = invoice;
		this.list_invoice_detail = list_invoice_detail;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	public List<InvoiceDetail> getList_invoice_detail() {
		return list_invoice_detail;
	}
	public void setList_invoice_detail(List<InvoiceDetail> list_invoice_detail) {
		this.list_invoice_detail = list_invoice_detail;
	}
	
}
