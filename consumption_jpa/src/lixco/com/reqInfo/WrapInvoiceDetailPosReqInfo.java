package lixco.com.reqInfo;

import java.util.ArrayList;
import java.util.List;

import lixco.com.entity.ExportBatchPos;
import lixco.com.entity.InvoiceDetailPos;

public class WrapInvoiceDetailPosReqInfo implements Cloneable{
	private InvoiceDetailPos invoice_detail_pos=null;
	private List<ExportBatchPos> list_export_batch_pos=null;
	public WrapInvoiceDetailPosReqInfo() {
	}
	public WrapInvoiceDetailPosReqInfo(InvoiceDetailPos invoice_detail_pos,List<ExportBatchPos> list_export_batch_pos) {
		this.invoice_detail_pos = invoice_detail_pos;
		this.list_export_batch_pos = list_export_batch_pos;
	}
	public InvoiceDetailPos getInvoice_detail_pos() {
		return invoice_detail_pos;
	}
	public void setInvoice_detail_pos(InvoiceDetailPos invoice_detail_pos) {
		this.invoice_detail_pos = invoice_detail_pos;
	}
	public List<ExportBatchPos> getList_export_batch_pos() {
		return list_export_batch_pos;
	}
	public void setList_export_batch_pos(List<ExportBatchPos> list_export_batch_pos) {
		this.list_export_batch_pos = list_export_batch_pos;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WrapInvoiceDetailPosReqInfo other = (WrapInvoiceDetailPosReqInfo) obj;
		if (invoice_detail_pos == null) {
			if (other.invoice_detail_pos != null)
				return false;
		} else if (!invoice_detail_pos.equals(other.invoice_detail_pos))
			return false;
		return true;
	}
	@Override
	public WrapInvoiceDetailPosReqInfo clone() throws CloneNotSupportedException {
		try{
			WrapInvoiceDetailPosReqInfo detail=new WrapInvoiceDetailPosReqInfo();
			detail.setInvoice_detail_pos(invoice_detail_pos.clone());
			if(list_export_batch_pos !=null){
				List<ExportBatchPos> list=new ArrayList<ExportBatchPos>();
				detail.setList_export_batch_pos(list);
				for(ExportBatchPos ex:list_export_batch_pos){
					list.add(ex.clone());
				}
			}
			return detail;
		}catch(Exception e){
			
		}
		return null;
	}
	
}
