package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.Customer;
import lixco.com.entity.CustomerTypes;
import lixco.com.reqInfo.CustomerReqInfo;

public interface ICustomerService {
	public int selectAll(List<Customer> list);
	public int search(String json,PagingInfo page,List<Customer> list);
	public int insert(CustomerReqInfo info);
	public int update(CustomerReqInfo info);
	public int selectById(long id,CustomerReqInfo info);
	public int deleteById(long id);
	public int checkCustomerCode(String customerCode,long customeId);
	public int autoComplete(String text,int size,List<Customer> list);
	public int selectAllByCustomerTypes(long customerTypesId,List<Customer> list);
	public int complete(String text,List<Customer> list);
	public int complete(String text,CustomerTypes customerTypes,List<Customer> list);
	public int selectByCode(String code,CustomerReqInfo t);
}
