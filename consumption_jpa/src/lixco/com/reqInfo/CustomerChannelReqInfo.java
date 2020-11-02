package lixco.com.reqInfo;

import lixco.com.entity.CustomerChannel;

public class CustomerChannelReqInfo {
	private CustomerChannel customer_channel=null;
	public CustomerChannelReqInfo() {
	}
	public CustomerChannelReqInfo(CustomerChannel customer_channel) {
		this.customer_channel = customer_channel;
	}
	public CustomerChannel getCustomer_channel() {
		return customer_channel;
	}

	public void setCustomer_channel(CustomerChannel customer_channel) {
		this.customer_channel = customer_channel;
	}
}
