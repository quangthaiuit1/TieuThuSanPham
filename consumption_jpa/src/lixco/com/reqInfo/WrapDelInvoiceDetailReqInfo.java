package lixco.com.reqInfo;

public class WrapDelInvoiceDetailReqInfo {
	private long member_id;
	private String member_name;
	private long id;
	public WrapDelInvoiceDetailReqInfo() {
	}
	public WrapDelInvoiceDetailReqInfo(long member_id, String member_name, long id) {
		this.member_id = member_id;
		this.member_name = member_name;
		this.id = id;
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
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
