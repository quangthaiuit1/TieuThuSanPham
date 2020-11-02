package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.CarType;
import lixco.com.reqInfo.CarTypeReqInfo;

public interface ICarTypeService {
	public int selectAll(List<CarType> list);
	public int insert(CarTypeReqInfo t);
	public int update(CarTypeReqInfo t);
	public int selectById(long id,CarTypeReqInfo t);
	public int deleteById(long id);
	public int checkCode(String code, long id);
	public int selectByCodeOld(String code, CarTypeReqInfo t);
	public int selectByCode(String code, CarTypeReqInfo t);
	public String initCode();
}
