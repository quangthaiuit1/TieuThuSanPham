package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.City;
import lixco.com.reqInfo.CityReqInfo;

public interface ICityService {
	public int selectAll(List<City> list);
	public int search(String json,PagingInfo page,List<City> list);
	public int insert(CityReqInfo t);
	public int update(CityReqInfo t);
	public int selectById(long id,CityReqInfo t);
	public int deleteById(long id);
	public int checkCityCode(String code,long cityId);
	public int findLike(String text,int size,List<City> list);
	public int selectByCode(String code,CityReqInfo t);
	public int complete(String text,List<City> list);
}
