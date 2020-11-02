package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.FreightContract;
import lixco.com.reqInfo.FreightContractReqInfo;

public interface IFreightContractService {
	public int search(String json,PagingInfo page,List<FreightContract> list);
	public int insert(FreightContractReqInfo t);
	public int update(FreightContractReqInfo t);
	public int selectById(long id,FreightContractReqInfo t);
	public int deleteById(long id);
	public String initOrderCode();
	public int selectByOrderCode(String contractCode,FreightContractReqInfo t);
}
