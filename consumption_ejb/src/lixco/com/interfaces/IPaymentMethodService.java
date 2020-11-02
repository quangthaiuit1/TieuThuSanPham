package lixco.com.interfaces;

import java.util.List;

import lixco.com.entity.PaymentMethod;
import lixco.com.reqInfo.PaymentMethodReqInfo;

public interface IPaymentMethodService {
	public int selectAll(List<PaymentMethod> list);
	public int insert(PaymentMethodReqInfo t);
	public int update(PaymentMethodReqInfo t);
	public int selectById(long id,PaymentMethodReqInfo t);
	public int deleteById(long id);
	public int checkCode(String code, long id);
	public int selectByCodeOld(String code, PaymentMethodReqInfo t);
	public int selectByCode(String code, PaymentMethodReqInfo t);
	public String initCode();
}
