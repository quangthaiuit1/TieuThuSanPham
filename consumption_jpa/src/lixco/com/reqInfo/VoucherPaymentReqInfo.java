package lixco.com.reqInfo;

import lixco.com.entity.VoucherPayment;

public class VoucherPaymentReqInfo {
	private VoucherPayment voucher_payment=null;

	public VoucherPaymentReqInfo() {
	}

	public VoucherPaymentReqInfo(VoucherPayment voucher_payment) {
		this.voucher_payment = voucher_payment;
	}

	public VoucherPayment getVoucher_payment() {
		return voucher_payment;
	}

	public void setVoucher_payment(VoucherPayment voucher_payment) {
		this.voucher_payment = voucher_payment;
	}
	
}
