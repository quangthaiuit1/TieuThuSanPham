package lixco.com.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;

import lixco.com.entity.Floor;
import lixco.com.entity.Pos;
import lixco.com.entity.RowStack;
import lixco.com.entity.Warehouse;
import lixco.com.interfaces.IFloorService;
import lixco.com.interfaces.IPosService;
import lixco.com.interfaces.IRowStackService;
import lixco.com.interfaces.IWarehouseService;
import lixco.com.reqInfo.FloorReqInfo;
import lixco.com.reqInfo.PosReqInfo;
import lixco.com.reqInfo.QuickDataInfo;
import lixco.com.reqInfo.RowStackReqInfo;
import lixco.com.reqInfo.WarehouseReqInfo;
import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.util.Notify;

@Named
@ViewScoped
public class ManagerWarehouseBean  extends AbstractBean {
	private static final long serialVersionUID = 1L;
	@Inject
	private Logger logger;
	@Inject
	private IWarehouseService warehouseService;
	@Inject
	private IRowStackService rowStackService;
	@Inject
	private IPosService posService;
	@Inject
	private IFloorService floorService;
	private List<Warehouse> listWarehouse;
	private Warehouse warehouseSelect;
	private List<QuickDataInfo> listQuickDataInfo;
//	private List<Row>
	private boolean quick;
	/*Kho hàng*/
	private Warehouse warehouseCrud;
	/*Dãy kho hàng*/
	private List<RowStack> listRowStackCrud;
	/*Tầng của kho*/
	private List<RowStack> listRowStack;
	private RowStack rowStackSelect;
	private List<Floor> listFloorCrud;
	/*Vị trí của tầng*/
	private List<Floor> listFloor;
	private Floor  floorSelect;
	private List<Pos> listPosCrud;
	
	
	@Override
	protected void initItem() {
		try{
			listWarehouse=new ArrayList<>();
			warehouseService.selectAll(listWarehouse);
			initQuickData();
			quick=false;
			initRowStack();
		}catch(Exception e){
			logger.error("ManagerWarehouseBean.initItem:"+e.getMessage(),e);
		}
	}
	public void deleteRowStack(RowStack t){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(allowDelete(new Date())){
				if(rowStackService.deleteById(t.getId())>0){
					//xóa thành công
					notify.success("Xóa thành công!");
					//load lại selectbox dãy tab tầng 
					listRowStack=new ArrayList<>();
					rowStackService.selectByWarehouse(warehouseSelect.getId(), listRowStack);
				}else{
					notify.warning("Không xóa được!");
				}
			}else{
				notify.warning("Tài khoản bạn không được phân quyền để làm làm việc này!");
				return;
			}
			listRowStackCrud.remove(t);
		}catch (Exception e) {
			logger.error("ManagerWarehouseBean.deleteRowStack:"+e.getMessage(),e);
		}
	}
	public void deleteFloor(Floor t){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(allowDelete(new Date())){
				if(floorService.deleteById(t.getId())>0){
					//xóa thành công
					notify.success("Xóa thành công!");
					//load lại selectbox tầng tab vị trí 
					listFloor=new ArrayList<>();
					floorService.selectByRow(rowStackSelect.getId(), listFloor);
				}else{
					notify.warning("Không xóa được!");
				}
			}else{
				notify.warning("Tài khoản bạn không được phân quyền để làm làm việc này!");
				return;
			}
			listFloorCrud.remove(t);
		}catch (Exception e) {
			logger.error("ManagerWarehouseBean.deleteFloor:"+e.getMessage(),e);
		}
	}
	public void deletePos(Pos t){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(allowDelete(new Date())){
				if(floorService.deleteById(t.getId())>0){
					//xóa thành công
					notify.success("Xóa thành công!");
					//load lại pos
					
				}else{
					notify.warning("Không xóa được!");
				}
			}else{
				notify.warning("Tài khoản bạn không được phân quyền để làm làm việc này!");
				return;
			}
			//xóa ra khỏi list
			listPosCrud.remove(t);
		}catch (Exception e) {
			logger.error("ManagerWarehouseBean.deleteFloor:"+e.getMessage(),e);
		}
	}
	public void addNewRowPos(){
		try{
			listPosCrud.add(new Pos());
		}catch(Exception e){
			logger.error("ManagerWarehouseBean.addNewRowPos:"+e.getMessage(),e);
		}
	}
	public void initPos(){
		try{
			listPosCrud=new ArrayList<>();
			if(floorSelect !=null){
				//get pos by floor;
				posService.selectByFloor(floorSelect.getId(), listPosCrud);
			}
			if(listPosCrud.size()==0){
				listPosCrud.add(new Pos());
				listPosCrud.add(new Pos());
				listPosCrud.add(new Pos());
			}
		}catch(Exception e){
			logger.error("ManagerWarehouseBean.initPos:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdatePos(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(listPosCrud !=null && listPosCrud.size()>0){
				if(floorSelect !=null && floorSelect.getId() !=0){
					boolean update=allowUpdate(new Date());
					for(Pos p:listPosCrud){
						String code=p.getPos_code();
						String name=p.getPos_name();
						if(code !=null &&!"".equals(code) && name !=null && !"".equals(name)){
							p.setPos_code(code.trim());
							PosReqInfo t=new PosReqInfo(p);
							generalBarcode(p);
							if(p.getId()==0){
								//lưu tầng kho
								p.setFloor(floorSelect);
								posService.insert(t);
							}else{
								if(update){
									posService.update(t);
								}
							}
						}
					}
					notify.success("Thành công");
					//tải lại danh sách
					initPos();
				}else{
					notify.warning("Chưa chọn tầng kho hàng!");
				}
			}
		}catch(Exception e){
			logger.error("ManagerWarehouseBean.saveOrUpdatePos:"+e.getMessage(),e);
		}
	}
	public void addNewRowFloor(){
		try{
			listFloorCrud.add(new Floor());
		}catch(Exception e){
			logger.error("ManagerWarehouseBean.addNewRowFloor:"+e.getMessage(),e);
		}
	}
	public void addNewRowStack(){
		try{
			listRowStackCrud.add(new RowStack());
		}catch (Exception e) {
			logger.error("ManagerWarehouseBean.addNewRowStack:"+e.getMessage(),e);
		}
	}
	public void initFloor(){
		try{
			listFloorCrud=new ArrayList<>();
			listFloor=new ArrayList<>();
			if(rowStackSelect !=null){
				//get floor by rowstack;
				floorService.selectByRow(rowStackSelect.getId(), listFloorCrud);
				//sao chép dữ liệu cho selectbox floor tab vị trí
				for(Floor f:listFloorCrud){
					listFloor.add(f.clone());
				}
			}
			if(listFloorCrud.size()==0){
				listFloorCrud.add(new Floor());
				listFloorCrud.add(new Floor());
				listFloorCrud.add(new Floor());
			}
			//init vị trí
			floorSelect=null;
			listPosCrud=new ArrayList<>();
			listPosCrud.add(new Pos());
			listPosCrud.add(new Pos());
			listPosCrud.add(new Pos());
		}catch(Exception e){
			logger.error("ManagerWarehouseBean.initFloor:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdateFloor(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(listFloorCrud !=null && listFloorCrud.size()>0){
				if(rowStackSelect !=null && rowStackSelect.getId() !=0){
					boolean update=allowUpdate(new Date());
					for(Floor f:listFloorCrud){
						String code=f.getFloor_code();
						String name=f.getFloor_name();
						if(code !=null &&!"".equals(code) && name !=null && !"".equals(name)){
							f.setFloor_code(code.trim());
							FloorReqInfo t=new FloorReqInfo(f);
							if(f.getId()==0){
								//lưu tầng kho
								f.setRow_stack(rowStackSelect);
								floorService.insert(t);
							}else{
								if(update){
									floorService.update(t);
								}
							}
						}
					}
					notify.success("Thành công");
					//tải lại danh sách
					listFloorCrud=new ArrayList<>();
					floorService.selectByRow(rowStackSelect.getId(), listFloorCrud);
					//tải lại danh sách selectbox tầng tab vị trí
					listFloor=new ArrayList<>();
					for(Floor f:listFloorCrud){
						listFloor.add(f.clone());
					}
				}else{
					notify.warning("Chưa chọn dãy kho hàng!");
				}
			}
		}catch(Exception e){
			logger.error("ManagerWarehouseBean.saveOrUpdateFloor:"+e.getMessage(),e);
		}
	}
	public void saveOrUpdateRowStack(){
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(listRowStackCrud !=null && listRowStackCrud.size()>0){
				if(warehouseSelect !=null && warehouseSelect.getId()!=0){
					boolean update=allowUpdate(new Date());
					for(RowStack r:listRowStackCrud){
						String code=r.getRow_code();
						String name=r.getRow_name();
						if(code !=null &&!"".equals(code) && name !=null && !"".equals(name)){
							r.setRow_code(code.trim());
							RowStackReqInfo t=new RowStackReqInfo(r);
							if(r.getId()==0){
								//lưu dãy kho
								r.setWarehouse(warehouseSelect);
								rowStackService.insert(t);
							}else{
								if(update){
									rowStackService.update(t);
								}
							}
						}
					}
					notify.success("Thành công");
					//tải lại danh sách
					listRowStackCrud=new ArrayList<>();
					rowStackService.selectByWarehouse(warehouseSelect.getId(), listRowStackCrud);
					//load lại selectbox dãy tab tầng
					listRowStack=new ArrayList<>();
					for(RowStack r:listRowStackCrud){
						listRowStack.add(r.clone());
					}
				}else{
					notify.warning("Chưa chọn kho hàng!");
				}
			}
		}catch (Exception e) {
			logger.error("ManagerWarehouseBean.saveOrUpdateRowStack:"+e.getMessage(),e);
		}
	}
	public void initRowStack(){
		try{
			listRowStackCrud=new ArrayList<>();
			listRowStack=new ArrayList<>();
			if(warehouseSelect !=null){
				//get rowstack by warehouse;
				rowStackService.selectByWarehouse(warehouseSelect.getId(), listRowStackCrud);
				//clone listRowStackCrud
				for(RowStack r:listRowStackCrud){
					listRowStack.add(r.clone());
				}
			}
			if(listRowStackCrud.size()==0){
				listRowStackCrud.add(new RowStack());
				listRowStackCrud.add(new RowStack());
				listRowStackCrud.add(new RowStack());
			}
			//init floor
			rowStackSelect=null;
			listFloorCrud=new ArrayList<>();
			listFloorCrud.add(new Floor());
			listFloorCrud.add(new Floor());
			listFloorCrud.add(new Floor());
			//init vị trí
			floorSelect=null;
			listPosCrud=new ArrayList<>();
			listPosCrud.add(new Pos());
			listPosCrud.add(new Pos());
			listPosCrud.add(new Pos());
		}catch(Exception e){
			logger.error("ManagerWarehouseBean.initRowStack:"+e.getMessage(),e);
		}
	}
	public void showDialogAddWarehouse(){
		PrimeFaces current=PrimeFaces.current();
		try{
			warehouseCrud=new Warehouse();
			warehouseCrud.setCode(warehouseService.initCode());
			current.executeScript("PF('dlg1').show();");
		}catch (Exception e) {
			logger.error("ManagerWarehouseBean.showDialogAddWarehouse:"+e.getMessage(),e);
		}
	}
	public void saveWareHouse(){
		PrimeFaces current=PrimeFaces.current();
		Notify notify = new Notify(FacesContext.getCurrentInstance());
		try{
			if(warehouseCrud!=null){
				if(allowSave(new Date())){
					String code=warehouseCrud.getCode();
					String name=warehouseCrud.getName();
					if(code !=null && !"".equals(code) && name !=null && !"".equals(name)){
						WarehouseReqInfo t=new WarehouseReqInfo(warehouseCrud);
						if(warehouseService.insert(t)==0){
							listWarehouse.add(t.getWarehouse());
							notify.success("Thêm kho hàng thành công!");
						}else{
							notify.warning("Mã đã tồn tại!");
						}
					}else{
						notify.warning("Thông tin không đầy đủ!");
					}
				}else{
					current.executeScript(
							"swaldesigntimer('Cảnh báo!', 'Tài khoản này không có quyền thực hiện hoặc tháng đã khoá!','warning',2000);");
				}
			}
		}catch (Exception e) {
			logger.error("ManagerWarehouseBean.saveWareHouse:"+e.getMessage(),e);
		}
	}
	public void quickOnClick(){
		try{
			quick=!quick;
			if(warehouseSelect!=null && warehouseSelect.getId()!=0){
				//load quick data
				
				
			}else{
				initQuickData();
			}
		}catch (Exception e) {
			logger.error("ManagerWarehouseBean.quickOnClick:"+e.getMessage(),e);
		}
	}
	private void initQuickData(){
		try{
			listQuickDataInfo=new ArrayList<>();
			if(warehouseSelect !=null && warehouseSelect.getId()!=0){
				List<RowStack> list=new ArrayList<>();
				
			}
			listQuickDataInfo.add(new QuickDataInfo());
			listQuickDataInfo.add(new QuickDataInfo());
			listQuickDataInfo.add(new QuickDataInfo());
		}catch (Exception e) {
			logger.error("ManagerWarehouseBean.initItem:"+e.getMessage(),e);
		}
	}
	public void generalBarcode(Pos it){
		try{
			if(it!=null){
				it.setBarcode(null);
				String code=it.getPos_code();
				if(rowStackSelect !=null && rowStackSelect.getId() !=0 && floorSelect !=null && floorSelect.getId() !=0 && code !=null && !"".equals(code)){
					it.setBarcode(rowStackSelect.getRow_code()+"-"+floorSelect.getFloor_code()+"-"+code);
				}
			}
		}catch(Exception e){
			logger.error("ManagerWarehouseBean.generalBarcode:"+e.getMessage(),e);
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	public List<Warehouse> getListWarehouse() {
		return listWarehouse;
	}

	public void setListWarehouse(List<Warehouse> listWarehouse) {
		this.listWarehouse = listWarehouse;
	}

	public Warehouse getWarehouseSelect() {
		return warehouseSelect;
	}

	public void setWarehouseSelect(Warehouse warehouseSelect) {
		this.warehouseSelect = warehouseSelect;
	}

	public List<QuickDataInfo> getListQuickDataInfo() {
		return listQuickDataInfo;
	}

	public void setListQuickDataInfo(List<QuickDataInfo> listQuickDataInfo) {
		this.listQuickDataInfo = listQuickDataInfo;
	}
	public boolean isQuick() {
		return quick;
	}
	public void setQuick(boolean quick) {
		this.quick = quick;
	}
	public Warehouse getWarehouseCrud() {
		return warehouseCrud;
	}
	public void setWarehouseCrud(Warehouse warehouseCrud) {
		this.warehouseCrud = warehouseCrud;
	}
	public List<RowStack> getListRowStackCrud() {
		return listRowStackCrud;
	}
	public void setListRowStackCrud(List<RowStack> listRowStackCrud) {
		this.listRowStackCrud = listRowStackCrud;
	}
	public List<RowStack> getListRowStack() {
		return listRowStack;
	}
	public void setListRowStack(List<RowStack> listRowStack) {
		this.listRowStack = listRowStack;
	}
	public List<Floor> getListFloorCrud() {
		return listFloorCrud;
	}
	public void setListFloorCrud(List<Floor> listFloorCrud) {
		this.listFloorCrud = listFloorCrud;
	}
	public RowStack getRowStackSelect() {
		return rowStackSelect;
	}
	public void setRowStackSelect(RowStack rowStackSelect) {
		this.rowStackSelect = rowStackSelect;
	}
	public List<Floor> getListFloor() {
		return listFloor;
	}
	public void setListFloor(List<Floor> listFloor) {
		this.listFloor = listFloor;
	}
	public Floor getFloorSelect() {
		return floorSelect;
	}
	public void setFloorSelect(Floor floorSelect) {
		this.floorSelect = floorSelect;
	}
	public List<Pos> getListPosCrud() {
		return listPosCrud;
	}
	public void setListPosCrud(List<Pos> listPosCrud) {
		this.listPosCrud = listPosCrud;
	}
}
