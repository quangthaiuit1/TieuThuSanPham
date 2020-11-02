package lixco.com.reqInfo;


public class WrapIEInvocieReqInfo {
	private long  ie_invoice_id;
	private String created_by;
	private long created_by_id;
	
	public WrapIEInvocieReqInfo() {
	}

	public WrapIEInvocieReqInfo(long ie_invoice_id, String created_by, long created_by_id) {
		this.ie_invoice_id = ie_invoice_id;
		this.created_by = created_by;
		this.created_by_id = created_by_id;
	}

	public long getIe_invoice_id() {
		return ie_invoice_id;
	}

	public void setIe_invoice_id(long ie_invoice_id) {
		this.ie_invoice_id = ie_invoice_id;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public long getCreated_by_id() {
		return created_by_id;
	}

	public void setCreated_by_id(long created_by_id) {
		this.created_by_id = created_by_id;
	}
	
}
