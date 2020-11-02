package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.IEInvoice;
import lixco.com.entity.IEInvoiceDetail;
import lixco.com.reportInfo.IECommercialInvoiceReportInfo;
import lixco.com.reportInfo.IEExportReport;
import lixco.com.reportInfo.IEInvoiceCustomerBillNoDetailReport;
import lixco.com.reportInfo.IEInvoiceHarborBillNoDetailReport;
import lixco.com.reportInfo.IEInvoiceListByContNoReport;
import lixco.com.reportInfo.IEInvoiceListByTaxValueZeroReport;
import lixco.com.reportInfo.IEInvoiceOrderListByProductCodeReport;
import lixco.com.reportInfo.IEInvoiceOrderListByVoucherReport;
import lixco.com.reportInfo.IEInvoiceReport;
import lixco.com.reportInfo.IEOrderFormReport;
import lixco.com.reportInfo.IEPackingListReport;
import lixco.com.reportInfo.IEProformaInvocieReportInfo;
import lixco.com.reportInfo.IEVanningReport;
import lixco.com.reqInfo.IEInvoiceDetailReqInfo;
import lixco.com.reqInfo.IEInvoiceReqInfo;

public interface IIEInvoiceService {
	public int insert(IEInvoiceReqInfo t);
	public int update(IEInvoiceReqInfo t);
	public int search(String json,PagingInfo page,List<IEInvoice> list);
	public int deleteById(long id);
	public int selectById(long id,IEInvoiceReqInfo t);
	public int insertDetail(IEInvoiceDetailReqInfo t);
	public int updateDetail(IEInvoiceDetailReqInfo t);
	public int selectIEInvoideDetailByInvoice(long iEInvoiceId,List<IEInvoiceDetail> list);
	public int deleteDetailById(long id);
	public int selectDetailById(long id,IEInvoiceDetailReqInfo t);
	public int selectCommercialInvoiceReport(long iEInvoiceId, List<IECommercialInvoiceReportInfo> list);
	public int selectCommercialInvoiceReport(String json,List<IECommercialInvoiceReportInfo> list);
	public int selectOrderFormReport(long iEInvoiceId,List<IEOrderFormReport> list);
	public int selectIEProformaInvoiceReport(long iEInvoiceId, List<IEProformaInvocieReportInfo> list);
	public int selectIEProformaInvoiceReport(String json, List<IEProformaInvocieReportInfo> list);
	public int selectIEPackingListReport(long iEInvoiceId, List<IEPackingListReport> list);
	public int selectIEPackingListReport(String json,List<IEPackingListReport> list);
	public int selectIEVanningReport(long iEInvoiceId,List<IEVanningReport> list);
	public int selectIEVanningReport(String json,List<IEVanningReport> list);
	public int selectIEInvoiceReport(long iEInvoiceId,List<IEInvoiceReport> list);
	public int selectIEExportReport(long iEInvoiceId,List<IEExportReport> list);
	public int selectIEInvoiceOrderListByVoucherReport(String json,List<IEInvoiceOrderListByVoucherReport> list);
	public int selectIEInvoiceOrderListByProductCodeReport(String json,List<IEInvoiceOrderListByProductCodeReport> list);
	public int selectIEInvoicListByContNoReport(String json,List<IEInvoiceListByContNoReport> list);
	public int selectIEInvoiceListByTaxValueZeroReport(String json,List<IEInvoiceListByTaxValueZeroReport> list);
	public int selectIEInvoiceHarborBillNoDetailReport(String json,List<IEInvoiceHarborBillNoDetailReport> list);
	public int selectIEInvoiceCustomerBillNoDetailReport(String json,List<IEInvoiceCustomerBillNoDetailReport> list);
	
	
}
