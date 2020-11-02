package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.CustomerTypes;
import lixco.com.reqInfo.CustomerTypesReqInfo;

public interface ICustomerTypesService {
	public int selectAll(List<CustomerTypes> list);
	public int search(String json,List<CustomerTypes> list);
	public int insert(CustomerTypesReqInfo info);
	public int update(CustomerTypesReqInfo info);
	public int selectById(long id,CustomerTypesReqInfo info);
	public int deleteById(long id);
	public int checkCustomerTypeCode(String groupCode,long customerTypesId);
	public int findLike(String text,List<CustomerTypes> list);
	public int selectByCode(String code,CustomerTypesReqInfo t);
}
