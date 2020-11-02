package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.CustomerPromotionProgram;
import lixco.com.reqInfo.CustomerPromotionProgramReqInfo;

public interface ICustomerPromotionProgramService {
	public int search(String json,PagingInfo page,List<CustomerPromotionProgram> list);
	public int insert(CustomerPromotionProgramReqInfo t);
	public int update(CustomerPromotionProgramReqInfo t);
	public int selectById(long id,CustomerPromotionProgramReqInfo t);
	public int deleteById(long id);
	public int selectAll(long programId,List<CustomerPromotionProgram> list);
	public int selectBy(long programId,long customerId,CustomerPromotionProgramReqInfo t);
	public int selectForCustomer(String json,CustomerPromotionProgramReqInfo t);
}
