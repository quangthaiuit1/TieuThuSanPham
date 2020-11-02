package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.CustomerChannel;
import lixco.com.reqInfo.CustomerChannelReqInfo;

public interface ICustomerChannelService {
	public int selectAll(List<CustomerChannel> list);
	public int insert(CustomerChannelReqInfo t);
	public int update(CustomerChannelReqInfo t);
	public int selectById(long id, CustomerChannelReqInfo t);
	public int deleteById(long id);
}
