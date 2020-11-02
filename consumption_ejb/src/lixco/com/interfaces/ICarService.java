package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.Car;
import lixco.com.reqInfo.CarReqInfo;

public interface ICarService {
	public int selectAll(List<Car> list);
	public int insert(CarReqInfo t);
	public int update(CarReqInfo t);
	public int selectById(long id,CarReqInfo t);
	public int deleteById(long id);
	public int search(String json,PagingInfo page,List<Car> list);
	public int selectByLicensePlate(String licensePlate,CarReqInfo t);
	public int complete(String text,List<Car> list);
}
