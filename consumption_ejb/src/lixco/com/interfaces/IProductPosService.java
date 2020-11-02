package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.ExportBatchPos;
import lixco.com.entity.ProductPos;
import lixco.com.reqInfo.ProductPosReqInfo;

public interface IProductPosService {
	public int exportPos(long productId,List<ProductPos> list);
	public int exportPosInvoiceDetail(long productId,long invoiceDetailId,List<ExportBatchPos> listExportBatchPos,List<ProductPos> list);
	public int selectById(long id,ProductPosReqInfo t);
	public int getListProducPutPos(long posId,List<Object[]> list);
	public int getTotalPalletPutPos(long posId);
	public int getTotalPalletPutPosOutPP(long posId,long productPosId);
}
