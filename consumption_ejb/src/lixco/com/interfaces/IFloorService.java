package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.Floor;
import lixco.com.reqInfo.FloorReqInfo;

public interface IFloorService {
	public int insert(FloorReqInfo t);
	public int update(FloorReqInfo t);
	public int deleteById(long id);
	public int selectById(long id,FloorReqInfo t);
	public int selectByRow(long rowId,List<Floor> list);
}
