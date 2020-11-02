package lixco.com.interfaces;

import java.util.List;

import lixco.com.common.PagingInfo;
import lixco.com.entity.Contract;
import lixco.com.entity.ContractDetail;
import lixco.com.entity.ContractReqInfo;
import lixco.com.reqInfo.ContractDetailReqInfo;

public interface IContractService {
	public int insert(ContractReqInfo contract);
	public int update(ContractReqInfo contract);
	public int selectById(long id,ContractReqInfo t);
	public int deleteById(long id);
	public int search(String json,PagingInfo page,List<Contract> list);
	public int insertDetail(ContractDetailReqInfo t);
	public int updateDetail(ContractDetailReqInfo t);
	public int selectContractDetailByContractId(long contractId,List<ContractDetail> list);
	public int selectByIdDetail(long id,ContractDetailReqInfo t);
	public int deleteDetailById(long id);
	public String initVoucherCode();
	public int complete(String text,List<Contract> list);
	public int selectContractDetail(String json,List<ContractDetail> list);
}
