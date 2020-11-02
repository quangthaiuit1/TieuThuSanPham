package lixco.com.reqInfo;

import lixco.com.entity.IEInvoice;

public class IEInvoiceReqInfo {
	private IEInvoice ie_invoice=null;

	public IEInvoiceReqInfo() {
	}
	public IEInvoiceReqInfo(IEInvoice ie_invoice) {
		this.ie_invoice = ie_invoice;
	}

	public IEInvoice getIe_invoice() {
		return ie_invoice;
	}

	public void setIe_invoice(IEInvoice ie_invoice) {
		this.ie_invoice = ie_invoice;
	}
	
}
