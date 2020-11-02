package lixco.com.reqInfo;

import lixco.com.entity.ExportBatch;

public class WrapExportDataReqInfo {
	private ExportBatch export_batch;
	private double quantity_export;
	private boolean select;
	
	public WrapExportDataReqInfo() {
	}
	public WrapExportDataReqInfo(ExportBatch export_batch, double quantity_export, boolean select) {
		this.export_batch = export_batch;
		this.quantity_export = quantity_export;
		this.select = select;
	}

	public ExportBatch getExport_batch() {
		return export_batch;
	}

	public void setExport_batch(ExportBatch export_batch) {
		this.export_batch = export_batch;
	}
	public double getQuantity_export() {
		return quantity_export;
	}
	public void setQuantity_export(double quantity_export) {
		this.quantity_export = quantity_export;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	
}
