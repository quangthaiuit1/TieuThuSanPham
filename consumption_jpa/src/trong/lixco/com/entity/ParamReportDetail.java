package trong.lixco.com.entity;


import javax.persistence.*;



@Entity
public class ParamReportDetail extends AbstractEntity{
	private String keyd;
	private String valued;

	@ManyToOne
	private ParamReport paramReport;

	public String getKey() {
		return keyd;
	}

	public void setKey(String key) {
		this.keyd = key;
	}

	public String getValue() {
		return valued;
	}

	public void setValue(String value) {
		this.valued = value;
	}

	public ParamReport getParamReport() {
		return paramReport;
	}

	public void setParamReport(ParamReport paramReport) {
		this.paramReport = paramReport;
	}

	@Override
	public String toString() {
		return "ParamReportDetail [keyd=" + keyd + ", valued=" + valued + ", paramReport=" + paramReport + ", id=" + id
				+ ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", createdUser=" + createdUser
				+ ", note=" + note + "]";
	}
}
