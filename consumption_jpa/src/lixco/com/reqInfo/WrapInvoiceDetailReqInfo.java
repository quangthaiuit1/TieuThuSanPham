package lixco.com.reqInfo;

import java.util.ArrayList;
import java.util.List;

import lixco.com.entity.ExportBatch;
import lixco.com.entity.InvoiceDetail;

public class WrapInvoiceDetailReqInfo implements Cloneable{
	private InvoiceDetail invoice_detail=null;
	private List<ExportBatch> list_export_batch=null;
	public WrapInvoiceDetailReqInfo() {
	}
	public WrapInvoiceDetailReqInfo(InvoiceDetail invoice_detail, List<ExportBatch> list_export_batch) {
		this.invoice_detail = invoice_detail;
		this.list_export_batch = list_export_batch;
	}
	public InvoiceDetail getInvoice_detail() {
		return invoice_detail;
	}
	public void setInvoice_detail(InvoiceDetail invoice_detail) {
		this.invoice_detail = invoice_detail;
	}
	public List<ExportBatch> getList_export_batch() {
		return list_export_batch;
	}
	public void setList_export_batch(List<ExportBatch> list_export_batch) {
		this.list_export_batch = list_export_batch;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((invoice_detail == null) ? 0 : invoice_detail.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WrapInvoiceDetailReqInfo other = (WrapInvoiceDetailReqInfo) obj;
		if (invoice_detail == null) {
			if (other.invoice_detail != null)
				return false;
		} else if (!invoice_detail.equals(other.invoice_detail))
			return false;
		return true;
	}
	@Override
	public WrapInvoiceDetailReqInfo clone() throws CloneNotSupportedException {
		WrapInvoiceDetailReqInfo w=new WrapInvoiceDetailReqInfo();
		w.setInvoice_detail(this.invoice_detail);
		if(list_export_batch !=null){
			List<ExportBatch> list=new ArrayList<ExportBatch>();
			w.setList_export_batch(list);
			for(ExportBatch ex:list_export_batch){
				list.add(ex.clone());
			}
		}
		return w;
	}
}
