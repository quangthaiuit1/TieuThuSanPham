package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.CustomerPricingProgram;
import lixco.com.reqInfo.CustomerPricingProgramReqInfo;

public interface ICustomerPricingProgramService {
	public int search(String json,PagingInfo page,List<CustomerPricingProgram> list);
	public int insert(CustomerPricingProgramReqInfo t);
	public int update(CustomerPricingProgramReqInfo t);
	public int selectById(long id,CustomerPricingProgramReqInfo t);
	public int deleteById(long id);
	public int selectAll(long programId,List<CustomerPricingProgram> list);
	public int selectBy(long programId,long customerId,CustomerPricingProgramReqInfo t);
	public int selectForCustomer(String json,CustomerPricingProgramReqInfo t);
}
