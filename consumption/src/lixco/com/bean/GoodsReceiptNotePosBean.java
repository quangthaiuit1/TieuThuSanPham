package lixco.com.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.google.gson.JsonObject;

import lixco.com.common.FormatHandler;
import lixco.com.common.JsonParserUtil;
import lixco.com.common.NavigationInfo;
import lixco.com.common.PagingInfo;
import lixco.com.common.SessionHelper;
import lixco.com.common.ToolTimeCustomer;
import lixco.com.entity.Customer;
import lixco.com.entity.FloorData;
import lixco.com.entity.GoodsReceiptNotePos;
import lixco.com.entity.GoodsReceiptNotePosDetail;
import lixco.com.entity.IECategories;
import lixco.com.entity.Pos;
import lixco.com.entity.Product;
import lixco.com.entity.ProductPos;
import lixco.com.entity.Warehouse;
import lixco.com.interfaces.ICustomerService;
import lixco.com.interfaces.IGoodsReceiptNotePosService;
import lixco.com.interfaces.IIECategoriesService;
import lixco.com.interfaces.IPosService;
import lixco.com.interfaces.IProcessLogicGoodsReceiptNotePosService;
import lixco.com.interfaces.IProductPosService;
import lixco.com.interfaces.IProductService;
import lixco.com.interfaces.IWarehouseService;
import lixco.com.reqInfo.GoodsReceiptNotePosReqInfo;
import lixco.com.reqInfo.Message;
import lixco.com.reqInfo.PosReqInfo;
import lixco.com.reqInfo.PosSelect;
import lixco.com.reqInfo.ProductReqInfo;
import lixco.com.reqInfo.WarehouseReqInfo;
import lixco.com.reqInfo.WrapGoodsReceiptNoteDetailFile;
import lixco.com.reqInfo.WrapGoodsReceiptNotePosDetailReqInfo;
import lixco.com.reqInfo.WrapGoodsReceiptNotePosReqInfo;
import lixco.com.reqInfo.WrapPosReqInfo;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import trong.lixco.com.account.servicepublics.Account;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;
@Named
@ViewScoped
public class GoodsReceiptNotePosBean  extends AbstractBean {

	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IProcessLogicGoodsReceiptNotePosService processLogicGoodsReceiptNotePosService;
	@Inject
	private IGoodsReceiptNotePosService goodsReceiptNotePosService;
	@Inject
	private IProductService productService;
	@Inject
	private ICustomerService customerService;
	@Inject
	private IIECategoriesService ieCategoriesService;
	@Inject
	private IWarehouseService warehouseService;
	@Inject
	private IPosService posService;
	@Inject
	private IProductPosService productPosService;
	private GoodsReceiptNotePos goodsReceiptNotePosCrud;
	private GoodsReceiptNotePos goodsReceiptNotePosSelect;
	private List<GoodsReceiptNotePos> listGoodsReceiptNotePos;
	private WrapGoodsReceiptNotePosDetailReqInfo wrapGoodsReceiptNotePosDetailCrud;
	private WrapGoodsReceiptNotePosDetailReqInfo wrapGoodsReceiptNotePosDetailSelect;
	private List<WrapGoodsReceiptNotePosDetailReqInfo> listWrapGoodsReceiptNotePosDetailReqInfo;
	private List<WrapGoodsReceiptNotePosDetailReqInfo> listWrapGoodsReceiptNotePosDetailFilter;
	private List<IECategories> listIECategories;
	/*search phiếu nhập*/
	private Date fromDateSearch;
	private Date toDateSearch;
	private Customer customerSearch;
	private IECategories ieCategoriesSearch;
	private int statusSearch;
	private int pageSize;
	private NavigationInfo navigationInfo;
	private List<Integer> listRowPerPage;
	private FormatHandler formatHandler;
	private Account account;
	/*Thông tin search*/
	private Product productSearch;
	private String batchCodeSearch;
	/*vị trí kho*/
	private WrapGoodsReceiptNotePosDetailReqInfo wrapGoodsReceiptNotePosDetaiSettingPos;
	private int rowStackSelect;
	private List<Integer> listRowStack;
	private Warehouse warehouseSelect;
	private List<Warehouse> listWarehouse;
	private List<FloorData> listFloorData;
	private List<PosSelect> listPosSelect;
	@Override
	protected void initItem() {
		try{
			formatHandler=FormatHandler.getInstance();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			account = SessionHelper.getInstance().getSession("account", request, Account.class);
			createNew();
			createNewDetail();
			listIECategories=new ArrayList<>();
			ieCategoriesService.selectAll(listIECategories);
			navigationInfo = new NavigationInfo();
			navigationInfo.setCurrentPage(1);
			initRowPerPage();
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			Date currentDate=new Date();
			fromDateSearch=ToolTimeCustomer.getDateMinCustomer(ToolTimeCustomer.getMonthM(currentDate), ToolTimeCustomer.getYearM(currentDate));
			//for test data
			fromDateSearch=ToolTimeCustomer.getDateMinCustomer(6, 2020);
			statusSearch=-1;
			searchReceiptNote();
			listWarehouse=new ArrayList<>();
			warehouseService.selectAll(listWarehouse);
			listRowStack=new ArrayList<>();
			listPosSelect=new ArrayList<>();
		}catch(Exception e){
			logger.error("GoodsReceiptNotePosBean.initItem:"+e.getMessage(),e);
		}
	}
	public void settingPos(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(listPosSelect !=null && listPosSelect.size()>0){
				List<ProductPos> list=new ArrayList<>();
				for(PosSelect p:listPosSelect){
					list.addAll(p.getListProductPos());
				}
				wrapGoodsReceiptNotePosDetaiSettingPos.setList_product_pos(list);
				notify.success("Đặt vị trí thành công!");
				PrimeFaces.current().ajax().update("menuformid:tabview1:tablect:"+listWrapGoodsReceiptNotePosDetailReqInfo.indexOf(wrapGoodsReceiptNotePosDetaiSettingPos)+":tableInner");
			}
		}catch (Exception e) {
			logger.error("GoodsReceiptNotePosBean.settingPos:"+e.getMessage(),e);
		}
	}
	public void addListPos(Pos p,boolean value){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			p.setWarehouse(warehouseSelect);
			//nếu check là true.
			if(value){
				for(PosSelect f:listPosSelect){
					if(f.getWarehouse().equals(warehouseSelect)){
						List<ProductPos> list=f.getListProductPos();
						for(ProductPos ppc:list){
							if(ppc.getPos().equals(p)){
								notify.warning("Vị trí thêm đã tồn tại");
								return;
							}
						}
						GoodsReceiptNotePosDetail receiptNoteDetail=wrapGoodsReceiptNotePosDetaiSettingPos.getGoods_receipt_note_pos_detail();
						ProductPos pp=new ProductPos();
						pp.setGoods_receipt_note_pos_detail(receiptNoteDetail);
						pp.setBatch_pos(receiptNoteDetail.getBatch_pos());
						//tính toán số thùng đặt tại vị trí đó
						pp.setQuantity_import(0);
						pp.setPos(p);
						f.getListProductPos().add(pp);
						return;
					}
				}
				PosSelect psNew=new PosSelect(warehouseSelect,new ArrayList<>());
				ProductPos pp=new ProductPos();
				pp.setGoods_receipt_note_pos_detail(wrapGoodsReceiptNotePosDetaiSettingPos.getGoods_receipt_note_pos_detail());
				pp.setBatch_pos(wrapGoodsReceiptNotePosDetaiSettingPos.getGoods_receipt_note_pos_detail().getBatch_pos());
				pp.setQuantity_import(0);
				pp.setPos(p);
				psNew.getListProductPos().add(pp);
				listPosSelect.add(psNew);
				
			}
		}catch (Exception e) {
			logger.error("GoodsReceiptNotePosBean.addListPos:"+e.getMessage(),e);
		}
		PrimeFaces.current().ajax().update("menuformid:tabview1:tablect:"+listWrapGoodsReceiptNotePosDetailReqInfo.indexOf(wrapGoodsReceiptNotePosDetaiSettingPos)+":tableInner");
	}
	public int totalPalletUse(long posId){
		try{
			return productPosService.getTotalPalletPutPos(posId);
		}catch(Exception e){
			logger.error("GoodsReceiptNotePosBean.caculateQuantityBoxPutPos:"+e.getMessage(),e);
		}
		return 0;
	}
	public double totalBoxQuantityProductPos(WrapGoodsReceiptNotePosDetailReqInfo item,List<ProductPos> list){
		try{
			double sum=0;
			for(ProductPos p:list){
				sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(p.getQuantity_import())).doubleValue();
			}
			return sum;
		}catch(Exception e){
			logger.error("GoodsReceiptNotePosBean.totalBoxQuantityProductPos:"+e.getMessage(),e);
		}
		return 0;
	}
	public void checkBoxQuantityImport(WrapGoodsReceiptNotePosDetailReqInfo w,ProductPos item){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			double sum=0;
			for(ProductPos p:w.getList_product_pos()){
				sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(p.getQuantity_import())).doubleValue();
			}
			if(sum> w.getGoods_receipt_note_pos_detail().getQuantity()){
				item.setQuantity_import(0);
				notify.warning("Số lượng lớn hơn số lượng chi tiết phiếu nhập!");
			}
			sum=0;
			for(ProductPos p:w.getList_product_pos()){
				sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(p.getQuantity_import())).doubleValue();
			}
			w.getGoods_receipt_note_pos_detail().setQuantity_real(sum);
			 PrimeFaces.current().ajax().update("menuformid:tabview1:tablect:"+listWrapGoodsReceiptNotePosDetailReqInfo.indexOf(w)+":slxep");
		} catch (Exception e) {
			logger.error("GoodsReceiptNotePosBean.checkBoxQuantityImport:"+e.getMessage(),e);
		}
	}
	public int totalPalletUse(double boxInPallet,double quantityImport,long posId,long productPosId){
		try{
			if(boxInPallet !=0){
				int pallet=(int) Math.ceil(quantityImport/boxInPallet);
				if(posId==0){
					pallet+=productPosService.getTotalPalletPutPos(posId);
				}else{
					pallet+=productPosService.getTotalPalletPutPosOutPP(posId, productPosId);
				}
				return pallet;
			}
		}catch(Exception e){
			logger.error("GoodsReceiptNotePosBean.caculateQuantityBoxPutPos:"+e.getMessage(),e);
		}
		return 0;
	}
	public void removeSelectPos(Pos p){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			for(PosSelect f:listPosSelect){
				if(f.getWarehouse().equals(p.getWarehouse())){
					List<ProductPos> list=f.getListProductPos();
					for(int i=0; i<list.size();i++){
						ProductPos temp=list.get(i);
						if(temp.getPos().equals(p)){
							if(temp.getId()==0){
							   f.getListProductPos().remove(i);
							}else{
								Message message=new Message();
								int code=processLogicGoodsReceiptNotePosService.deleteProductPos(temp.getId(),message );
								if(code==0){
									notify.success("Xóa thành công!");
									 ProductPos pss=f.getListProductPos().remove(i);
									 wrapGoodsReceiptNotePosDetaiSettingPos.getList_product_pos().remove(pss);
									 PrimeFaces.current().ajax().update("menuformid:tabview1:tablect:"+listWrapGoodsReceiptNotePosDetailReqInfo.indexOf(wrapGoodsReceiptNotePosDetaiSettingPos)+":tableInner");
								}else{
									String m=message.getUser_message()+" \\n"+message.getInternal_message();
									PrimeFaces.current().executeScript(
											"swaldesignclose('Thất bại', '"+m+"','warning');");
								}
							}
							return;
						}
					}
				}
			}
		}catch (Exception e) {
			logger.error("GoodsReceiptNotePosBean.removeSelectPos:"+e.getMessage(),e);
		}
	}
	public void loadListPos(){
		try{
			if(warehouseSelect !=null){
				listRowStack=new ArrayList<>();
				posService.selectRowStack(warehouseSelect.getId(), listRowStack);
				List<Pos> listPosData=new ArrayList<>();
				listFloorData=new ArrayList<>();
				if(listRowStack.size()>0){
				  rowStackSelect=listRowStack.get(0);
				  posService.selectByRowStack(rowStackSelect, warehouseSelect.getId(), listPosData);
				  if(listPosData.size()>0){
					  FloorData first=new FloorData(listPosData.get(0).getFloorb(), new ArrayList<>());
					  first.getListPos().add(listPosData.get(0));
					  listFloorData.add(first);
					  listPosData.remove(0);
					  for(Pos p: listPosData){
						  int floorb=p.getFloorb();
						  FloorData lastCurr=listFloorData.get(listFloorData.size()-1);
						  if(floorb==lastCurr.getFloor()){
							  lastCurr.getListPos().add(p);
						  }else{
							  FloorData data=new FloorData(floorb, new ArrayList<>());
							  data.getListPos().add(p);
							  listFloorData.add(data);
						  }
					  }
				  }
				}
			}
		}catch (Exception e) {
			logger.error("GoodsReceiptNotePosBean.loadListPos:"+e.getMessage(),e);
		}
	}
	public void showDialogPos(WrapGoodsReceiptNotePosDetailReqInfo t){
		PrimeFaces current=PrimeFaces.current();
		try {
			wrapGoodsReceiptNotePosDetaiSettingPos=t;
			listPosSelect=new ArrayList<>();
			List<ProductPos> listProductPos=t.getList_product_pos();
			if(listProductPos !=null){
				for(ProductPos p:listProductPos){
					Warehouse wh=p.getPos().getWarehouse();
					boolean add=true;
					for(PosSelect ps:listPosSelect){
						if(wh.equals(ps.getWarehouse())){
							ps.getListProductPos().add(p);
							add=false;
							break;
						}
					}
					if(add){
						PosSelect psnew=new PosSelect(wh, new ArrayList<>());
						psnew.getListProductPos().add(p);
						listPosSelect.add(psnew);
					}
				}
			}
			loadListPos();
			current.executeScript("PF('dlg1').show();");
		} catch (Exception e) {
			logger.error("GoodsReceiptNotePosBean.showDialogPos:"+e.getMessage(),e);
		}
	}
	public void addGoodsReceiptNotePosDetail(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(allowSave(new Date())){
				GoodsReceiptNotePosDetail detail=wrapGoodsReceiptNotePosDetailCrud.getGoods_receipt_note_pos_detail();
				if (detail.getProduct() != null && detail.getBatch_code() !=null && !"".equals(detail.getBatch_code())) {
//						//kiểm tra sản phẩm đã tồn tại trong danh sách chưa nếu chưa thì thêm vào
//						for(WrapGoodsReceiptNotePosDetailReqInfo f:listWrapGoodsReceiptNotePosDetailReqInfo){
//							if(f.getGoods_receipt_note_pos_detail().getProduct().equals(detail.getProduct())){
//								current.executeScript(
//										"swaldesigntimer('Thất bại', 'Sản phẩm đã được thêm vào!','warning',2000);");
//								wrapGoodsReceiptNotePosDetailCrud = new WrapGoodsReceiptNotePosDetailReqInfo();
//								return;
//							}
//						}
						detail.setQuantity(1);
						detail.setCreated_by(account.getMember().getName());
						detail.setCreated_date(new Date());
						WrapGoodsReceiptNotePosDetailReqInfo w=new WrapGoodsReceiptNotePosDetailReqInfo();
						w.setGoods_receipt_note_pos_detail(detail.clone());
						w.setList_product_pos(null);
						listWrapGoodsReceiptNotePosDetailReqInfo.add(0, w);
					
				}else{
					if(detail.getProduct()==null){
						current.executeScript(
								"swaldesigntimer('Cảnh báo!', 'Chưa nhập sản phẩm để thêm vào!','warning',2000);");
					}
					if( detail.getBatch_code() ==null || "".equals(detail.getBatch_code())){
						current.executeScript(
								"swaldesigntimer('Cảnh báo!', 'Chưa nhập mã lô hàng!','warning',2000);");
					}
				}
				// reset
				wrapGoodsReceiptNotePosDetailCrud.setGoods_receipt_note_pos_detail(new GoodsReceiptNotePosDetail());
			}else{
				current.executeScript(
						"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','error',2000);");
			}
		}catch(Exception e){
			// reset
			wrapGoodsReceiptNotePosDetailCrud = new WrapGoodsReceiptNotePosDetailReqInfo();
			logger.error("GoodsReceiptNotePosBean.addGoodsReceiptNotePosDetail:"+e.getMessage(),e);
		}
		current.executeScript("utility.expandTable('.ui-row-toggler');");
	}
	public void deleteGoodsReceiptNotePosDetail(WrapGoodsReceiptNotePosDetailReqInfo f){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		PrimeFaces current=PrimeFaces.current();
		try{
			GoodsReceiptNotePosDetail detail=f.getGoods_receipt_note_pos_detail();
			if(detail.getId()==0){
				listWrapGoodsReceiptNotePosDetailReqInfo.remove(f);
				notify.success("Xóa thành công!");
			}else{
				//thực hiện delete trong transation
				Message messages=new Message();
				int code=processLogicGoodsReceiptNotePosService.deleteGoodsReciptNoteDetailMaster(detail.getId(),messages);
				if(code>=0){
					notify.success("Xóa thành công!");
				}else{
					String m=messages.getUser_message()+" \n"+messages.getInternal_message();
					current.executeScript(
							"swaldesignclose('Thất bại', '"+m+"','warning');");
				}
			}
		}catch (Exception e) {
			logger.error("GoogsReceiptNotePosBean.deleteGoodsReceiptNotePosDetail:"+e.getMessage(),e);
		}
	}
	public void deleteProductPos(List<ProductPos> list,ProductPos item){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		PrimeFaces current=PrimeFaces.current();
		try{
			if(item.getId()==0){
				for(ProductPos p:list){
					if(p.getPos().equals(item.getPos())){
						if(list.remove(p)){
						  notify.success("Xóa thành công!");
						}
						break;
					}
				}
			}else{
				//xóa trong database
				Message messages=new Message();
				int code=processLogicGoodsReceiptNotePosService.deleteProductPos(item.getId(), messages);
				if(code==0){
					notify.success("Xóa thành công!");
					list.remove(item);
				}else{
					String m=messages.getUser_message()+" \\n"+messages.getInternal_message();
					current.executeScript(
							"swaldesignclose('Thất bại', '"+m+"','warning');");
				}
				
			}
		}catch (Exception e) {
			logger.error("GoogsReceiptNotePosBean.deleteProductPos:"+e.getMessage(),e);
		}
	}
	public void searchReceiptNote(){
		try{
			/*{ goods_receipt_note_pos:{from_date:'',to_date:'',customer_id:0,ie_categories_id:0,status:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listGoodsReceiptNotePos=new ArrayList<>();
			PagingInfo page=new PagingInfo();
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 :customerSearch.getId());
			jsonInfo.addProperty("ie_categories_id", ieCategoriesSearch == null ? 0 :ieCategoriesSearch.getId());
			jsonInfo.addProperty("status", statusSearch);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",1);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("goods_receipt_note_pos", jsonInfo);
			json.add("page", jsonPage);
			goodsReceiptNotePosService.search(JsonParserUtil.getGson().toJson(json), page, listGoodsReceiptNotePos);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(1);
		}catch(Exception e){
			logger.error("GoogsReceiptNotePosBean.searchReceiptNotePos:"+e.getMessage(),e);
		}
	}
	private void initRowPerPage() {
		try {
			listRowPerPage = new ArrayList<Integer>();
			listRowPerPage.add(90);
			listRowPerPage.add(180);
			listRowPerPage.add(240);
			pageSize = listRowPerPage.get(0);
		} catch (Exception e) {
			logger.error("GoogsReceiptNotePosBean.initRowPerPage:"+e.getMessage(),e);
		}
	}
	public void paginatorChange(int currentPage) {
		try {
			/*{ goods_receipt_note_pos:{from_date:'',to_date:'',customer_id:0,ie_categories_id:0,status:-1}, page:{page_index:0, page_size:0}}*/
			navigationInfo.setLimit(pageSize);
			navigationInfo.setMaxIndices(5);
			listGoodsReceiptNotePos = new ArrayList<>();
			PagingInfo page = new PagingInfo();
			// thông tin phân trang
			JsonObject jsonInfo=new JsonObject();
			jsonInfo.addProperty("from_date", ToolTimeCustomer.convertDateToString(fromDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("to_date", ToolTimeCustomer.convertDateToString(toDateSearch, "dd/MM/yyyy"));
			jsonInfo.addProperty("customer_id", customerSearch ==null ? 0 :customerSearch.getId());
			jsonInfo.addProperty("ie_categories_id", ieCategoriesSearch == null ? 0 :ieCategoriesSearch.getId());
			jsonInfo.addProperty("status", statusSearch);
			JsonObject jsonPage=new JsonObject();
			jsonPage.addProperty("page_index",currentPage);
			jsonPage.addProperty("page_size", pageSize);
			JsonObject json=new JsonObject();
			json.add("goods_receipt_note_pos", jsonInfo);
			json.add("page", jsonPage);
			goodsReceiptNotePosService.search(JsonParserUtil.getGson().toJson(json), page, listGoodsReceiptNotePos);
			navigationInfo.setTotalRecords((int) page.getTotalRow());
			navigationInfo.setCurrentPage(currentPage);
		} catch (Exception e) {
			logger.error("GoogsReceiptNotePosBean.paginatorChange:" + e.getMessage(), e);
		}
	}
	public void paginationRowStack(boolean next){
		try{
			if(warehouseSelect !=null){
				//get listRowStack(int[]) order by
				List<Integer> list=new ArrayList<>();
				posService.selectRowStack(warehouseSelect.getId(), list);
				int index=list.indexOf(rowStackSelect);
				int last=list.size()-1;
				if(next){
					if(index==last){
						index=0;
					}else{
						index++;
					}
				}else{
					if(index==0){
						index=last;
					}else{
						index--;
					}
				}
				rowStackSelect=list.get(index);
				List<Pos> listPosData=new ArrayList<>();
				listFloorData=new ArrayList<>();
				posService.selectByRowStack(rowStackSelect, warehouseSelect.getId(), listPosData);
				if(listPosData.size()>0){
					  FloorData first=new FloorData(listPosData.get(0).getFloorb(), new ArrayList<>());
					  first.getListPos().add(listPosData.get(0));
					  listFloorData.add(first);
					  listPosData.remove(0);
					  for(Pos p: listPosData){
						  int floorb=p.getFloorb();
						  FloorData lastCurr=listFloorData.get(listFloorData.size()-1);
						  if(floorb==lastCurr.getFloor()){
							 lastCurr.getListPos().add(p);
						  }else{
							 FloorData data=new FloorData(floorb, new ArrayList<>());
							 data.getListPos().add(p);
							 listFloorData.add(data);
						 }
					  }
				
			    }
			}
		}catch (Exception e) {
			logger.error("GoogsReceiptNotePosBean.paginationRowStack:" + e.getMessage(), e);
		}
	}
	public void createNew(){
		try{
			goodsReceiptNotePosCrud=new GoodsReceiptNotePos();
			goodsReceiptNotePosCrud.setCreated_by(account.getMember().getName());
			goodsReceiptNotePosCrud.setImport_date(new Date());
			listWrapGoodsReceiptNotePosDetailReqInfo=new ArrayList<>();
			wrapGoodsReceiptNotePosDetailCrud=new WrapGoodsReceiptNotePosDetailReqInfo(new GoodsReceiptNotePosDetail(),null);
		}catch (Exception e) {
			logger.error("GoogsReceiptNotePosBean.createNew:"+e.getMessage(),e);
		}
		
	}
	public void createNewDetail(){
		try{
			wrapGoodsReceiptNotePosDetailCrud=new WrapGoodsReceiptNotePosDetailReqInfo(new GoodsReceiptNotePosDetail(),null);
		}catch (Exception e) {
			logger.error("GoogsReceiptNotePosBean.createNewDetail:"+e.getMessage(),e);
		}
	}
	public List<Product> completeProduct(String text) {
		try {
			List<Product> list = new ArrayList<Product>();
			productService.complete2(formatHandler.converViToEn(text), list);
			return list;
		} catch (Exception e) {
			logger.error("GoogsReceiptNotePosBean.completeProduct:" + e.getMessage(), e);
		}
		return null;
	}
	public List<Customer> completeCustomer(String text){
		try{
			List<Customer> list = new ArrayList<Customer>();
			customerService.complete(formatHandler.converViToEn(text), list);
			return list;
		}catch(Exception e){
			logger.error("GoogsReceiptNotePosBean.completeCustomer:" + e.getMessage(), e);
		}
		return null;
	}
	public void deleteGoodsReceiptNotePos(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		PrimeFaces current=PrimeFaces.current();
		try{
			if(goodsReceiptNotePosSelect !=null && goodsReceiptNotePosSelect.getId()!=0){
				//delete detail
				Message messages=new Message();
				int code=processLogicGoodsReceiptNotePosService.deleteGoodsReceiptNoteMaster(goodsReceiptNotePosSelect.getId(), messages);
				if(code>=0){
					notify.success("Xóa thành công!");
					listGoodsReceiptNotePos.remove(goodsReceiptNotePosSelect);
				}else{
					String m=messages.getUser_message()+" \\n"+messages.getInternal_message();
					current.executeScript(
							"swaldesignclose('Thất bại', '"+m+"','warning');");
				}
			}
		}catch(Exception e){
			logger.error("GoogsReceiptNotePosBean.deleteGoodsReceiptNotePos:" + e.getMessage(), e);
		}
	}
	public void saveOrUpdateReceiptNotePosTemp(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(goodsReceiptNotePosCrud !=null){
				if(listWrapGoodsReceiptNotePosDetailReqInfo.size()>0){
					WrapGoodsReceiptNotePosReqInfo t=new WrapGoodsReceiptNotePosReqInfo();
					
					//set trạng thái lưu tạm
					goodsReceiptNotePosCrud.setStatus(0);
					//tạo packed dữ liệu 
					t.setGoods_receipt_note_pos(goodsReceiptNotePosCrud);
					t.setList_goods_receipt_note_pos_detail(listWrapGoodsReceiptNotePosDetailReqInfo);
					t.setUsername(account.getMember().getName());
					WrapGoodsReceiptNotePosReqInfo cloned=t.clone();
					Message messages=new Message();
					//check info
					Customer customer=goodsReceiptNotePosCrud.getCustomer();
					IECategories categories=goodsReceiptNotePosCrud.getIe_categories();
					Date importDate=goodsReceiptNotePosCrud.getImport_date();
					if(customer !=null && categories !=null && importDate !=null){
						if(goodsReceiptNotePosCrud.getId()==0){
							if(allowSave(new Date())){
								goodsReceiptNotePosCrud.setCreated_by(account.getMember().getName());
								goodsReceiptNotePosCrud.setCreated_date(new Date());
								int code=processLogicGoodsReceiptNotePosService.saveOrUpdateGoodsReceiptNotePosService(cloned, messages, 0);
								if(code>=0){
									current.executeScript("swaldesigntimer('Thành công!', 'Lưu thành công!','success',2000);");
									//tải lại 
									GoodsReceiptNotePosReqInfo req=new GoodsReceiptNotePosReqInfo();
									goodsReceiptNotePosService.selectById(cloned.getGoods_receipt_note_pos().getId(), req);
									goodsReceiptNotePosCrud=req.getGoods_receipt_note_pos();
									listGoodsReceiptNotePos.add(0, goodsReceiptNotePosCrud.clone());
									  //load chi tiết phiếu nhập
									  listWrapGoodsReceiptNotePosDetailReqInfo=new ArrayList<>();
									  List<GoodsReceiptNotePosDetail> listTemp=new ArrayList<>();
									  goodsReceiptNotePosService.selectByReceiptNotePos(goodsReceiptNotePosCrud.getId(),listTemp);
									  //load danh sách lo hàng và vị trí của lô hàng đó trong kho
									  for(GoodsReceiptNotePosDetail dt:listTemp){
										  WrapGoodsReceiptNotePosDetailReqInfo w=new WrapGoodsReceiptNotePosDetailReqInfo();
										  w.setGoods_receipt_note_pos_detail(dt);
										  List<ProductPos> list=new ArrayList<>();
										  goodsReceiptNotePosService.selectListProductPos(dt.getId(), list);
										  w.setList_product_pos(list);
										  dt.setBatch_code(dt.getBatch_pos().getBatch_code());
										  double sum=0;
										  for(ProductPos p:list){
											sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(p.getQuantity_import())).doubleValue();
										  }
										  dt.setQuantity_real(sum);
										  listWrapGoodsReceiptNotePosDetailReqInfo.add(w);
									  }
								}else{
									String m=messages.getUser_message()+" \\n"+messages.getInternal_message();
									current.executeScript(
											"swaldesignclose('Thất bại', '"+m+"','warning');");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
							}
						}else{
							if(allowUpdate(new Date())){
								//update đưa vào trong trans
								int code=processLogicGoodsReceiptNotePosService.saveOrUpdateGoodsReceiptNotePosService(cloned, messages, 0);
								if(code>=0){
									current.executeScript("swaldesigntimer('Thành công!', 'Lưu thành công!','success',2000);");
									//tải lại 
									GoodsReceiptNotePosReqInfo req=new GoodsReceiptNotePosReqInfo();
									goodsReceiptNotePosService.selectById(cloned.getGoods_receipt_note_pos().getId(), req);
									goodsReceiptNotePosCrud=req.getGoods_receipt_note_pos();
									listGoodsReceiptNotePos.set(listGoodsReceiptNotePos.indexOf(goodsReceiptNotePosCrud), goodsReceiptNotePosCrud.clone());
									  //load chi tiết phiếu nhập
									  listWrapGoodsReceiptNotePosDetailReqInfo=new ArrayList<>();
									  List<GoodsReceiptNotePosDetail> listTemp=new ArrayList<>();
									  goodsReceiptNotePosService.selectByReceiptNotePos(goodsReceiptNotePosCrud.getId(),listTemp);
									  //load danh sách lo hàng và vị trí của lô hàng đó trong kho
									  for(GoodsReceiptNotePosDetail dt:listTemp){
										  WrapGoodsReceiptNotePosDetailReqInfo w=new WrapGoodsReceiptNotePosDetailReqInfo();
										  w.setGoods_receipt_note_pos_detail(dt);
										  List<ProductPos> list=new ArrayList<>();
										  goodsReceiptNotePosService.selectListProductPos(dt.getId(), list);
										  w.setList_product_pos(list);
										  dt.setBatch_code(dt.getBatch_pos().getBatch_code());
										  double sum=0;
										  for(ProductPos p:list){
											sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(p.getQuantity_import())).doubleValue();
										  }
										  dt.setQuantity_real(sum);
										  listWrapGoodsReceiptNotePosDetailReqInfo.add(w);
									  }
								}else{
									String m=messages.getUser_message()+" \\n"+messages.getInternal_message();
									current.executeScript(
											"swaldesignclose('Thất bại', '"+m+"','warning');");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
							}
						}
					}else{
						current.executeScript(
								"swaldesigntimer('Cảnh báo!', 'Thông tin phiếu nhập không đầy đủ!','warning',2000);");
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Danh sách chi tiết phiếu nhập không có!','warning',2000);");
				}
			}
		}catch(Exception e){
			logger.error("GoogsReceiptNotePosBean.saveOrUpdateReceiptNotePosTemp:" + e.getMessage(), e);
		}
		//chạy thử nghiệp
		current.executeScript("utility.expandTable('.ui-row-toggler');");
	}
	public void saveOrUpdateReceiptNotePos(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(goodsReceiptNotePosCrud !=null){
				if(listWrapGoodsReceiptNotePosDetailReqInfo.size()>0){
					WrapGoodsReceiptNotePosReqInfo t=new WrapGoodsReceiptNotePosReqInfo();
					
					//set trạng thái lưu tạm
					goodsReceiptNotePosCrud.setStatus(0);
					//tạo packed dữ liệu 
					t.setGoods_receipt_note_pos(goodsReceiptNotePosCrud);
					t.setList_goods_receipt_note_pos_detail(listWrapGoodsReceiptNotePosDetailReqInfo);
					Message messages=new Message();
					//check info
					Customer customer=goodsReceiptNotePosCrud.getCustomer();
					IECategories categories=goodsReceiptNotePosCrud.getIe_categories();
					Date importDate=goodsReceiptNotePosCrud.getImport_date();
					if(customer !=null && categories !=null && importDate !=null){
						WrapGoodsReceiptNotePosReqInfo cloneObj=t.clone();
						if(goodsReceiptNotePosCrud.getId()==0){
							if(allowSave(new Date())){
								goodsReceiptNotePosCrud.setCreated_by(account.getMember().getName());
								goodsReceiptNotePosCrud.setCreated_date(new Date());
								int code=processLogicGoodsReceiptNotePosService.saveOrUpdateGoodsReceiptNotePosService(cloneObj, messages, 1);
								if(code>=0){
									current.executeScript("swaldesigntimer('Thành công!', 'Lưu thành công!','success',2000);");
									//tải lại 
									GoodsReceiptNotePosReqInfo req=new GoodsReceiptNotePosReqInfo();
									goodsReceiptNotePosService.selectById(cloneObj.getGoods_receipt_note_pos().getId(), req);
									goodsReceiptNotePosCrud=req.getGoods_receipt_note_pos();
									listGoodsReceiptNotePos.add(0, goodsReceiptNotePosCrud.clone());
									  //load chi tiết phiếu nhập
									  listWrapGoodsReceiptNotePosDetailReqInfo=new ArrayList<>();
									  List<GoodsReceiptNotePosDetail> listTemp=new ArrayList<>();
									  goodsReceiptNotePosService.selectByReceiptNotePos(goodsReceiptNotePosCrud.getId(),listTemp);
									  //load danh sách lo hàng và vị trí của lô hàng đó trong kho
									  for(GoodsReceiptNotePosDetail dt:listTemp){
										  WrapGoodsReceiptNotePosDetailReqInfo w=new WrapGoodsReceiptNotePosDetailReqInfo();
										  w.setGoods_receipt_note_pos_detail(dt);
										  List<ProductPos> list=new ArrayList<>();
										  goodsReceiptNotePosService.selectListProductPos(dt.getId(), list);
										  w.setList_product_pos(list);
										  dt.setBatch_code(dt.getBatch_pos().getBatch_code());
										  double sum=0;
										  for(ProductPos p:list){
											sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(p.getQuantity_import())).doubleValue();
										  }
										  dt.setQuantity_real(sum);
										  listWrapGoodsReceiptNotePosDetailReqInfo.add(w);
									  }
								}else{
									String m=messages.getUser_message()+" \\n"+messages.getInternal_message();
									current.executeScript(
											"swaldesignclose('Thất bại', '"+m+"','warning');");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
							}
						}else{
							if(allowUpdate(new Date())){
								//update đưa vào trong trans
								int code=processLogicGoodsReceiptNotePosService.saveOrUpdateGoodsReceiptNotePosService(cloneObj, messages, 1);
								if(code>=0){
									current.executeScript("swaldesigntimer('Thành công!', 'Lưu thành công!','success',2000);");
									//tải lại 
									GoodsReceiptNotePosReqInfo req=new GoodsReceiptNotePosReqInfo();
									goodsReceiptNotePosService.selectById(cloneObj.getGoods_receipt_note_pos().getId(), req);
									goodsReceiptNotePosCrud=req.getGoods_receipt_note_pos();
									listGoodsReceiptNotePos.set(listGoodsReceiptNotePos.indexOf(goodsReceiptNotePosCrud), goodsReceiptNotePosCrud.clone());
									  //load chi tiết phiếu nhập
									  listWrapGoodsReceiptNotePosDetailReqInfo=new ArrayList<>();
									  List<GoodsReceiptNotePosDetail> listTemp=new ArrayList<>();
									  goodsReceiptNotePosService.selectByReceiptNotePos(goodsReceiptNotePosCrud.getId(),listTemp);
									  //load danh sách lo hàng và vị trí của lô hàng đó trong kho
									  for(GoodsReceiptNotePosDetail dt:listTemp){
										  WrapGoodsReceiptNotePosDetailReqInfo w=new WrapGoodsReceiptNotePosDetailReqInfo();
										  w.setGoods_receipt_note_pos_detail(dt);
										  List<ProductPos> list=new ArrayList<>();
										  goodsReceiptNotePosService.selectListProductPos(dt.getId(), list);
										  w.setList_product_pos(list);
										  dt.setBatch_code(dt.getBatch_pos().getBatch_code());
										  double sum=0;
										  for(ProductPos p:list){
											sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(p.getQuantity_import())).doubleValue();
										  }
										  dt.setQuantity_real(sum);
										  listWrapGoodsReceiptNotePosDetailReqInfo.add(w);
									  }
								}else{
									String m=messages.getUser_message()+" \\n"+messages.getInternal_message();
									current.executeScript(
											"swaldesignclose('Thất bại', '"+m+"','warning');");
								}
							}else{
								current.executeScript(
										"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
							}
						}
					}else{
						current.executeScript(
								"swaldesigntimer('Cảnh báo!', 'Thông tin phiếu nhập không đầy đủ!','warning',2000);");
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Danh sách chi tiết phiếu nhập không có!','warning',2000);");
				}
			}
		}catch(Exception e){
			logger.error("GoogsReceiptNotePosBean.saveOrUpdateReceiptNotePos:" + e.getMessage(), e);
		}
		//chạy thử nghiệp
		current.executeScript("utility.expandTable('.ui-row-toggler');");
	}
	
	public void loadGoodsReceiptNotePos(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(goodsReceiptNotePosSelect !=null){
			  goodsReceiptNotePosCrud=goodsReceiptNotePosSelect.clone();
			  //load chi tiết phiếu nhập
			  listWrapGoodsReceiptNotePosDetailReqInfo=new ArrayList<>();
			  List<GoodsReceiptNotePosDetail> listTemp=new ArrayList<>();
			  goodsReceiptNotePosService.selectByReceiptNotePos(goodsReceiptNotePosCrud.getId(),listTemp);
			  //load danh sách lo hàng và vị trí của lô hàng đó trong kho
			  for(GoodsReceiptNotePosDetail dt:listTemp){
				  WrapGoodsReceiptNotePosDetailReqInfo w=new WrapGoodsReceiptNotePosDetailReqInfo();
				  w.setGoods_receipt_note_pos_detail(dt);
				  List<ProductPos> list=new ArrayList<>();
				  goodsReceiptNotePosService.selectListProductPos(dt.getId(), list);
				  w.setList_product_pos(list);
				  dt.setBatch_code(dt.getBatch_pos().getBatch_code());
				  listWrapGoodsReceiptNotePosDetailReqInfo.add(w);
				  double sum=0;
				  for(ProductPos p:list){
					sum=BigDecimal.valueOf(sum).add(BigDecimal.valueOf(p.getQuantity_import())).doubleValue();
				  }
				  dt.setQuantity_real(sum);
			  }
			}
		}catch(Exception e){
			logger.error("GoogsReceiptNotePosBean.loadGoodsReceiptNotePos:" + e.getMessage(), e);
		}
		current.executeScript("utility.expandTable('.ui-row-toggler');");
	}
	public void exportGoodsReceiptPosNew(){
		PrimeFaces current=PrimeFaces.current();
		try{
			if(goodsReceiptNotePosCrud !=null){
				 String reportPath = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/reports/phieunhapsp.jasper");
				 Map<String, Object> importParam = new HashMap<String, Object>();
				 importParam.put("import_date",ToolTimeCustomer.convertDateToString(goodsReceiptNotePosCrud.getImport_date(),"dd/MM/yyyy"));
				 importParam.put("note", goodsReceiptNotePosCrud.getNote());
				 importParam.put("customer_name", goodsReceiptNotePosCrud.getCustomer().getCustomer_name());
				 importParam.put("license_plate", goodsReceiptNotePosCrud.getLicense_plate());
				 importParam.put("voucher_code", goodsReceiptNotePosCrud.getVoucher_code());
				 List<GoodsReceiptNotePosDetail> listDetail=new ArrayList<>();
				 for(WrapGoodsReceiptNotePosDetailReqInfo w:listWrapGoodsReceiptNotePosDetailReqInfo){
					 listDetail.add(w.getGoods_receipt_note_pos_detail());
				 }
				 importParam.put("listDetail", listDetail);
				 JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, new JREmptyDataSource());
				 byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
				 String ba = Base64.getEncoder().encodeToString(data);
				 current.executeScript("utility.printPDF('" + ba + "')");
			}
		}catch(Exception e){
			logger.error("GoogsReceiptNotePosBean.exportGoodsReceiptPosNew:" + e.getMessage(), e);
		}
	}
	private Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();

		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();

		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue();
		}

		return null;
	}
	private Workbook getWorkbook(InputStream inputStream, String nameFile) throws IOException {
		Workbook workbook = null;
		if (nameFile.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (nameFile.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}

		return workbook;
	}
	public void loadFileExcel(FileUploadEvent event){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try {
			if(goodsReceiptNotePosCrud !=null){
				//check info
				Customer customer=goodsReceiptNotePosCrud.getCustomer();
				IECategories categories=goodsReceiptNotePosCrud.getIe_categories();
				Date importDate=goodsReceiptNotePosCrud.getImport_date();
				if(customer !=null && categories !=null && importDate !=null){
					if (event.getFile() != null) {
						UploadedFile part = event.getFile();
						byte[] byteFile = event.getFile().getContents();
						Map<WrapGoodsReceiptNoteDetailFile,List<WrapPosReqInfo>> mapData = new LinkedHashMap<>();
						Workbook workBook = getWorkbook(new ByteArrayInputStream(byteFile), part.getFileName());
						Sheet firstSheet = workBook.getSheetAt(0);
						Iterator<Row> rows = firstSheet.iterator();
						while (rows.hasNext()) {
							rows.next();
							rows.remove();
							break;
						}
						lv1:while (rows.hasNext()) {
							Row row = rows.next();
							Iterator<Cell> cells = row.cellIterator();
							WrapGoodsReceiptNoteDetailFile lix = new WrapGoodsReceiptNoteDetailFile();
							WarehouseReqInfo w=new WarehouseReqInfo();
							PosReqInfo p=new PosReqInfo();
							 while (cells.hasNext()) {
								Cell cell = cells.next();
								int columnIndex = cell.getColumnIndex();
		
								switch (columnIndex) {
								case 0:
									try {
										//masp
										String masp = Objects.toString(getCellValue(cell), null);
										if (masp != null && !"".equals(masp)) {
											ProductReqInfo pr=new ProductReqInfo();
											productService.selectByCode(masp, pr);
											if(pr.getProduct() !=null){
												lix.setProduct(pr.getProduct());
											}
										} else {
											continue lv1;
										}
									} catch (Exception e) {
									}
									break;
								case 2:
									try {
										//số lượngtổng
										String malohang = Objects.toString(getCellValue(cell), null);
										if(malohang==null || !"".equals(malohang)){
											continue lv1;
										}
										lix.setBatch_code(malohang.trim());
									} catch (Exception e) {
									}
									break;
								case 3:
									try {
										//số lượngtổng
										String soluongtong = Objects.toString(getCellValue(cell), "0");
										lix.setQuantity(Double.parseDouble(soluongtong));
									} catch (Exception e) {
									}
									break;
								case 4:
									try {
										//đơn giá
										String makho = Objects.toString(getCellValue(cell),null);
										warehouseService.selectByCode(makho.trim(), w);
										if(w.getWarehouse()==null){
											continue lv1;
										}
									} catch (Exception e) {
									}
									break;
								case 5:
									try{
										String vtkho=Objects.toString(getCellValue(cell),null);
										posService.selectByWarehouse(w.getWarehouse().getCode(), vtkho.trim(), p);
										if(p.getPos() ==null){
											continue lv1;
										}
									}catch (Exception e) {
									}
									break;
								case 6:
									try{
										String soluongdatstr=Objects.toString(getCellValue(cell),"0");
										double sldat=Double.parseDouble(soluongdatstr);
										WrapPosReqInfo wp=new WrapPosReqInfo();
										wp.setPos(p.getPos());
										wp.setQuantity(sldat);
										if(!mapData.containsKey(lix)){
											mapData.put(lix, new ArrayList<>());
										}
										mapData.get(lix).add(wp);
									}catch (Exception e) {
									}
									break;
									
								}
							}
						}
						workBook = null;// free
						listWrapGoodsReceiptNotePosDetailReqInfo=new ArrayList<>();
						for (Map.Entry<WrapGoodsReceiptNoteDetailFile, List<WrapPosReqInfo>> entry : mapData.entrySet()) {
							WrapGoodsReceiptNoteDetailFile key=entry.getKey();
							List<WrapPosReqInfo> value=entry.getValue();
							WrapGoodsReceiptNotePosDetailReqInfo t=new WrapGoodsReceiptNotePosDetailReqInfo();
							GoodsReceiptNotePosDetail detail=new GoodsReceiptNotePosDetail();
							detail.setCreated_date(new Date());
							detail.setCreated_by(account.getMember().getName());
							detail.setProduct(key.getProduct());
							detail.setQuantity(key.getQuantity());
							detail.setBatch_code(key.getBatch_code());
							t.setGoods_receipt_note_pos_detail(detail);
							List<ProductPos> listPP=new ArrayList<>();
							t.setList_product_pos(listPP);
							for (WrapPosReqInfo it : value) {
								ProductPos ppos=new ProductPos();
								ppos.setPos(it.getPos());
								ppos.setQuantity_import(it.getQuantity());
								listPP.add(ppos);
								
							}
						}
						WrapGoodsReceiptNotePosReqInfo last=new WrapGoodsReceiptNotePosReqInfo();
						last.setGoods_receipt_note_pos(goodsReceiptNotePosCrud);
						last.setList_goods_receipt_note_pos_detail(listWrapGoodsReceiptNotePosDetailReqInfo);
						Message messages=new Message();
						if(processLogicGoodsReceiptNotePosService.saveOrUpdateGoodsReceiptNotePosService(last, messages, goodsReceiptNotePosCrud.getStatus())>=0){
						   notify.success();
						}else{
							notify.warning("Thất bại!");
						}
					}
				}else{
					notify.warning("Thong tin phiếu nhập không đầy đủ!");
				}
			}
		} catch (Exception e) {
			logger.error("GoodsReceiptNotePosBean.loadFileExcel:" + e.getMessage(), e);
		}
	}
	public void showDialogUpload(){
		PrimeFaces current = PrimeFaces.current();
		current.executeScript("PF('dlgup1').show();");
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	public ICustomerService getCustomerService() {
		return customerService;
	}
	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}
	public GoodsReceiptNotePos getGoodsReceiptNotePosCrud() {
		return goodsReceiptNotePosCrud;
	}
	public void setGoodsReceiptNotePosCrud(GoodsReceiptNotePos goodsReceiptNotePosCrud) {
		this.goodsReceiptNotePosCrud = goodsReceiptNotePosCrud;
	}
	public GoodsReceiptNotePos getGoodsReceiptNotePosSelect() {
		return goodsReceiptNotePosSelect;
	}
	public void setGoodsReceiptNotePosSelect(GoodsReceiptNotePos goodsReceiptNotePosSelect) {
		this.goodsReceiptNotePosSelect = goodsReceiptNotePosSelect;
	}
	public List<GoodsReceiptNotePos> getListGoodsReceiptNotePos() {
		return listGoodsReceiptNotePos;
	}
	public void setListGoodsReceiptNotePos(List<GoodsReceiptNotePos> listGoodsReceiptNotePos) {
		this.listGoodsReceiptNotePos = listGoodsReceiptNotePos;
	}
	public WrapGoodsReceiptNotePosDetailReqInfo getWrapGoodsReceiptNotePosDetailCrud() {
		return wrapGoodsReceiptNotePosDetailCrud;
	}
	public void setWrapGoodsReceiptNotePosDetailCrud(
			WrapGoodsReceiptNotePosDetailReqInfo wrapGoodsReceiptNotePosDetailCrud) {
		this.wrapGoodsReceiptNotePosDetailCrud = wrapGoodsReceiptNotePosDetailCrud;
	}
	public WrapGoodsReceiptNotePosDetailReqInfo getWrapGoodsReceiptNotePosDetailSelect() {
		return wrapGoodsReceiptNotePosDetailSelect;
	}
	public void setWrapGoodsReceiptNotePosDetailSelect(
			WrapGoodsReceiptNotePosDetailReqInfo wrapGoodsReceiptNotePosDetailSelect) {
		this.wrapGoodsReceiptNotePosDetailSelect = wrapGoodsReceiptNotePosDetailSelect;
	}
	public List<WrapGoodsReceiptNotePosDetailReqInfo> getListWrapGoodsReceiptNotePosDetailReqInfo() {
		return listWrapGoodsReceiptNotePosDetailReqInfo;
	}
	public void setListWrapGoodsReceiptNotePosDetailReqInfo(
			List<WrapGoodsReceiptNotePosDetailReqInfo> listWrapGoodsReceiptNotePosDetailReqInfo) {
		this.listWrapGoodsReceiptNotePosDetailReqInfo = listWrapGoodsReceiptNotePosDetailReqInfo;
	}
	public List<IECategories> getListIECategories() {
		return listIECategories;
	}
	public void setListIECategories(List<IECategories> listIECategories) {
		this.listIECategories = listIECategories;
	}
	public Date getFromDateSearch() {
		return fromDateSearch;
	}
	public void setFromDateSearch(Date fromDateSearch) {
		this.fromDateSearch = fromDateSearch;
	}
	public Date getToDateSearch() {
		return toDateSearch;
	}
	public void setToDateSearch(Date toDateSearch) {
		this.toDateSearch = toDateSearch;
	}
	public Customer getCustomerSearch() {
		return customerSearch;
	}
	public void setCustomerSearch(Customer customerSearch) {
		this.customerSearch = customerSearch;
	}
	public IECategories getIeCategoriesSearch() {
		return ieCategoriesSearch;
	}
	public void setIeCategoriesSearch(IECategories ieCategoriesSearch) {
		this.ieCategoriesSearch = ieCategoriesSearch;
	}
	public int getStatusSearch() {
		return statusSearch;
	}
	public void setStatusSearch(int statusSearch) {
		this.statusSearch = statusSearch;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public NavigationInfo getNavigationInfo() {
		return navigationInfo;
	}
	public void setNavigationInfo(NavigationInfo navigationInfo) {
		this.navigationInfo = navigationInfo;
	}
	public List<Integer> getListRowPerPage() {
		return listRowPerPage;
	}
	public void setListRowPerPage(List<Integer> listRowPerPage) {
		this.listRowPerPage = listRowPerPage;
	}
	public FormatHandler getFormatHandler() {
		return formatHandler;
	}
	public void setFormatHandler(FormatHandler formatHandler) {
		this.formatHandler = formatHandler;
	}
	public Product getProductSearch() {
		return productSearch;
	}
	public void setProductSearch(Product productSearch) {
		this.productSearch = productSearch;
	}
	public String getBatchCodeSearch() {
		return batchCodeSearch;
	}
	public void setBatchCodeSearch(String batchCodeSearch) {
		this.batchCodeSearch = batchCodeSearch;
	}
	public List<Integer> getListRowStack() {
		return listRowStack;
	}
	public void setListRowStack(List<Integer> listRowStack) {
		this.listRowStack = listRowStack;
	}
	public Warehouse getWarehouseSelect() {
		return warehouseSelect;
	}
	public void setWarehouseSelect(Warehouse warehouseSelect) {
		this.warehouseSelect = warehouseSelect;
	}
	public List<Warehouse> getListWarehouse() {
		return listWarehouse;
	}
	public void setListWarehouse(List<Warehouse> listWarehouse) {
		this.listWarehouse = listWarehouse;
	}
	public int getRowStackSelect() {
		return rowStackSelect;
	}
	public void setRowStackSelect(int rowStackSelect) {
		this.rowStackSelect = rowStackSelect;
	}
	public List<FloorData> getListFloorData() {
		return listFloorData;
	}
	public void setListFloorData(List<FloorData> listFloorData) {
		this.listFloorData = listFloorData;
	}
	public List<PosSelect> getListPosSelect() {
		return listPosSelect;
	}
	public void setListPosSelect(List<PosSelect> listPosSelect) {
		this.listPosSelect = listPosSelect;
	}
	public WrapGoodsReceiptNotePosDetailReqInfo getWrapGoodsReceiptNotePosDetaiSettingPos() {
		return wrapGoodsReceiptNotePosDetaiSettingPos;
	}
	public void setWrapGoodsReceiptNotePosDetaiSettingPos(
			WrapGoodsReceiptNotePosDetailReqInfo wrapGoodsReceiptNotePosDetaiSettingPos) {
		this.wrapGoodsReceiptNotePosDetaiSettingPos = wrapGoodsReceiptNotePosDetaiSettingPos;
	}
	public List<WrapGoodsReceiptNotePosDetailReqInfo> getListWrapGoodsReceiptNotePosDetailFilter() {
		return listWrapGoodsReceiptNotePosDetailFilter;
	}
	public void setListWrapGoodsReceiptNotePosDetailFilter(
			List<WrapGoodsReceiptNotePosDetailReqInfo> listWrapGoodsReceiptNotePosDetailFilter) {
		this.listWrapGoodsReceiptNotePosDetailFilter = listWrapGoodsReceiptNotePosDetailFilter;
	}
}
