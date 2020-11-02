package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.Warehouse;
import lixco.com.reqInfo.WarehouseReqInfo;

public interface IWarehouseService {
	public int selectAll(List<Warehouse> list);
	public int insert(WarehouseReqInfo t);
	public int update(WarehouseReqInfo t);
	public int selectById(long id,WarehouseReqInfo t);
	public int deleteById(long id);
	public int checkCode(String code, long id);
	public int selectByCodeOld(String code, WarehouseReqInfo t);
	public int selectByCode(String code, WarehouseReqInfo t);
	public String initCode();

}
