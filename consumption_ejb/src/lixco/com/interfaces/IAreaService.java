package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.Area;
import lixco.com.reqInfo.AreaReqInfo;


public interface IAreaService {
	public int selectAll(List<Area> list);
	public int insert(AreaReqInfo t);
	public int update(AreaReqInfo t);
	public int selectById(long id,AreaReqInfo t);
	public int deleteById(long id);
	public int checkAreaCode(String code,long areaId);
	public int selectByCode(String code,AreaReqInfo t);
}
