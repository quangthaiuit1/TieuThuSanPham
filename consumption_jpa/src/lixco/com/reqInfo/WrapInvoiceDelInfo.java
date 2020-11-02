package lixco.com.reqInfo;

public class WrapInvoiceDelInfo {
	private long invoice_id;
	private long member_id;
	private String member_name;
	
	public WrapInvoiceDelInfo() {
	}
	
	public WrapInvoiceDelInfo(long invoice_id, long member_id, String member_name) {
		this.invoice_id = invoice_id;
		this.member_id = member_id;
		this.member_name = member_name;
	}

	public long getInvoice_id() {
		return invoice_id;
	}
	public void setInvoice_id(long invoice_id) {
		this.invoice_id = invoice_id;
	}
	public long getMember_id() {
		return member_id;
	}
	public void setMember_id(long member_id) {
		this.member_id = member_id;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	
}
