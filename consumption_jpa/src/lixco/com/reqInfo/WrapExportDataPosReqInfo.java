package lixco.com.reqInfo;


import lixco.com.entity.ExportBatchPos;

public class WrapExportDataPosReqInfo {
	private ExportBatchPos export_batch_pos=null;
	private double quantity_export_box;
	private boolean select;
	public WrapExportDataPosReqInfo() {
	}
	
	public ExportBatchPos getExport_batch_pos() {
		return export_batch_pos;
	}
	public WrapExportDataPosReqInfo(ExportBatchPos export_batch_pos,double quantity_export_box) {
		this.export_batch_pos = export_batch_pos;
		this.quantity_export_box = quantity_export_box;
	}

	public void setExport_batch_pos(ExportBatchPos export_batch_pos) {
		this.export_batch_pos = export_batch_pos;
	}
	public double getQuantity_export_box() {
		return quantity_export_box;
	}
	public void setQuantity_export_box(double quantity_export_box) {
		this.quantity_export_box = quantity_export_box;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}
	
}
