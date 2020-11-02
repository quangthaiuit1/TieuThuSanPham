package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.CarOwner;
import lixco.com.reqInfo.CarOwnerReqInfo;

public interface ICarOwnerService {
	public int selectAll(List<CarOwner> list);
	public int insert(CarOwnerReqInfo t);
	public int update(CarOwnerReqInfo t);
	public int selectById(long id,CarOwnerReqInfo t);
	public int deleteById(long id);
	public String initCode();
	public int checkCode(String code,long id);
	public int selectByCodeOld(String code,CarOwnerReqInfo t);
	public int selectByCode(String code,CarOwnerReqInfo t);
}
