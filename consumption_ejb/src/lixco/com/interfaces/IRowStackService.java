package lixco.com.interfaces;

import lixco.com.reqInfo.RowStackReqInfo;

import java.util.List;

import lixco.com.entity.RowStack;

public interface IRowStackService {
	public int insert(RowStackReqInfo t);
	public int update(RowStackReqInfo t);
	public int deleteById(long id);
	public int selectById(long id,RowStackReqInfo t);
	public int selectByWarehouse(long warehouseId,List<RowStack> list);
	
}
