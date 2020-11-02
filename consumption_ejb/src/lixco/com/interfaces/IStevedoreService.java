package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.Stevedore;
import lixco.com.reqInfo.StevedoreReqInfo;

public interface IStevedoreService {
	public int selectAll(List<Stevedore> list);
	public int insert(StevedoreReqInfo t);
	public int update(StevedoreReqInfo t);
	public int selectById(long id,StevedoreReqInfo t);
	public int deleteById(long id);
}
