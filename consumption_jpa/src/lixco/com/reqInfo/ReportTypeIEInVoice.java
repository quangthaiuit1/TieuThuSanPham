package lixco.com.reqInfo;

public class ReportTypeIEInVoice {
	private long id;
	private String name;
	
	public ReportTypeIEInVoice() {
	}
	
	public ReportTypeIEInVoice(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportTypeIEInVoice other = (ReportTypeIEInVoice) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
