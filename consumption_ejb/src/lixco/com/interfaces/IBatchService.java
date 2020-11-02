package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.Batch;
import lixco.com.entity.ExportBatch;
import lixco.com.reqInfo.BatchReqInfo;

public interface IBatchService {
	public int insert(BatchReqInfo t);
	public int update(BatchReqInfo t);
	public int deleteById(long id);
	public int selectById(long id,BatchReqInfo t);
	public int exportAutoBatch(long productId,double quantity);
	public int exportManualBatch(List<Batch> list);
	public double getQuantityRemaining(long productId);
	public int selectByCode(String batch_code,BatchReqInfo t);
	public int exportBatchByInvoiceDetail(long productId, long invoiceDetailId, List<ExportBatch> listExportBatch,List<Batch> listBatch);

}
