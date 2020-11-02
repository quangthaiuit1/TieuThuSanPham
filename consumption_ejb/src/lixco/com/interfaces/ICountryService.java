package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.Country;
import lixco.com.reqInfo.CountryReqInfo;

public interface ICountryService {
	public int selectAll(List<Country> list);
	public int search(String json,PagingInfo page,List<Country> list);
	public int insert(CountryReqInfo t);
	public int update(CountryReqInfo t);
	public int selectById(long id, CountryReqInfo t);
	public int deleteById(long id);
	public int checkCountryCode(String code,long country_id);
	public int selectByCode(String code,CountryReqInfo t);
	public int complete(String text,List<Country> list);
}
