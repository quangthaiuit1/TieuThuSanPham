package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.Pos;
import lixco.com.reqInfo.PosReqInfo;

public interface IPosService {
	public int insert(PosReqInfo t);
	public int update(PosReqInfo t);
	public int deleteById(long id);
	public int selectByFloor(long floorId,List<Pos> list);
	public int selectById(long id,PosReqInfo t);
	public int selectByWarehouse(String warehouseCode,String pos_code,PosReqInfo t);
	public int selectRowStack(long warehouseId,List<Integer> list);
	public int selectByRowStack(int rowStack,long warehouseId,List<Pos> list);
}
