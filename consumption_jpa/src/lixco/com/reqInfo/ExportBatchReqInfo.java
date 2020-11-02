package lixco.com.reqInfo;

import lixco.com.entity.ExportBatch;

public class ExportBatchReqInfo {
	private ExportBatch export_batch =null;

	public ExportBatchReqInfo() {
	}

	public ExportBatchReqInfo(ExportBatch export_batch) {
		this.export_batch = export_batch;
	}

	public ExportBatch getExport_batch() {
		return export_batch;
	}

	public void setExport_batch(ExportBatch export_batch) {
		this.export_batch = export_batch;
	}
}
