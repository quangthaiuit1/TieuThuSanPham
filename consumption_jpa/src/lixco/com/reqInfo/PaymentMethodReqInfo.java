package lixco.com.reqInfo;

import lixco.com.entity.PaymentMethod;

public class PaymentMethodReqInfo {
	private PaymentMethod payment_method=null;

	public PaymentMethodReqInfo() {
	}

	public PaymentMethodReqInfo(PaymentMethod payment_method) {
		this.payment_method = payment_method;
	}

	public PaymentMethod getPayment_method() {
		return payment_method;
	}

	public void setPayment_method(PaymentMethod payment_method) {
		this.payment_method = payment_method;
	}
	
}
