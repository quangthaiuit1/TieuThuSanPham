package lixco.com.reqInfo;

import java.util.List;

import lixco.com.entity.InvoiceDetailPos;

public class WrapDataInvoiceDetailPos {
	private InvoiceDetailPos invoice_detail_pos=null;
	private List<WrapExportDataPosReqInfo> list_wrap_export_data_pos=null;
	private String employee_name=null;
	
	public WrapDataInvoiceDetailPos() {
	}
	
	public WrapDataInvoiceDetailPos(InvoiceDetailPos invoice_detail_pos,List<WrapExportDataPosReqInfo> list_wrap_export_data_pos) {
		this.invoice_detail_pos = invoice_detail_pos;
		this.list_wrap_export_data_pos = list_wrap_export_data_pos;
	}

	public InvoiceDetailPos getInvoice_detail_pos() {
		return invoice_detail_pos;
	}
	public void setInvoice_detail_pos(InvoiceDetailPos invoice_detail_pos) {
		this.invoice_detail_pos = invoice_detail_pos;
	}
	public List<WrapExportDataPosReqInfo> getList_wrap_export_data_pos() {
		return list_wrap_export_data_pos;
	}
	public void setList_wrap_export_data_pos(List<WrapExportDataPosReqInfo> list_wrap_export_data_pos) {
		this.list_wrap_export_data_pos = list_wrap_export_data_pos;
	}

	public String getEmployee_name() {
		return employee_name;
	}

	public void setEmployee_name(String employee_name) {
		this.employee_name = employee_name;
	}
	
	
}
