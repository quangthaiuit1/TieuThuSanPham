package lixco.com.reqInfo;

import lixco.com.entity.IEInvoiceDetail;

public class IEInvoiceDetailReqInfo {
	private IEInvoiceDetail ie_invoice_detail=null;

	public IEInvoiceDetailReqInfo() {
	}

	public IEInvoiceDetailReqInfo(IEInvoiceDetail ie_invoice_detail) {
		this.ie_invoice_detail = ie_invoice_detail;
	}

	public IEInvoiceDetail getIe_invoice_detail() {
		return ie_invoice_detail;
	}

	public void setIe_invoice_detail(IEInvoiceDetail ie_invoice_detail) {
		this.ie_invoice_detail = ie_invoice_detail;
	}
	
}
