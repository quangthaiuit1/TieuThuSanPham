package lixco.com.reqInfo;

import java.util.List;

import lixco.com.entity.InvoiceDetail;

public class WrapDataInvoiceDetail {
	private InvoiceDetail invoice_detail=null;
	private List<WrapExportDataReqInfo> list_wrap_export_data=null;
	private String member_name=null;
	
	public WrapDataInvoiceDetail() {
	}
	public WrapDataInvoiceDetail(InvoiceDetail invoice_detail, List<WrapExportDataReqInfo> list_wrap_export_data,
			String member_name) {
		this.invoice_detail = invoice_detail;
		this.list_wrap_export_data = list_wrap_export_data;
		this.member_name = member_name;
	}

	public InvoiceDetail getInvoice_detail() {
		return invoice_detail;
	}
	public void setInvoice_detail(InvoiceDetail invoice_detail) {
		this.invoice_detail = invoice_detail;
	}
	public List<WrapExportDataReqInfo> getList_wrap_export_data() {
		return list_wrap_export_data;
	}
	public void setList_wrap_export_data(List<WrapExportDataReqInfo> list_wrap_export_data) {
		this.list_wrap_export_data = list_wrap_export_data;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
}
