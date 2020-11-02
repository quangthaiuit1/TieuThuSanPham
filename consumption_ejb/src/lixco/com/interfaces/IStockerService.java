package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.Stocker;
import lixco.com.reqInfo.StockerReqInfo;

public interface IStockerService {
	public int selectAll(List<Stocker> list);
	public int insert(StockerReqInfo t);
	public int update(StockerReqInfo t);
	public int selectById(long id,StockerReqInfo t);
	public int deleteById(long id);
	public int search(String text,int disable,List<Stocker> list);
	public int selectByCode(String code,StockerReqInfo t);
}
