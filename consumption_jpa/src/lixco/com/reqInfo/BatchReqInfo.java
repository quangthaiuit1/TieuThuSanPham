package lixco.com.reqInfo;

import lixco.com.entity.Batch;

public class BatchReqInfo {
	private Batch batch=null;

	public BatchReqInfo(Batch batch) {
		this.batch = batch;
	}

	public BatchReqInfo() {
	}

	public Batch getBatch() {
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}
	
}
