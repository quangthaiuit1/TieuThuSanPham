package trong.lixco.com.entity;


import java.util.List;

import javax.persistence.*;

@Entity
public class ParamReport extends AbstractEntity {
	private String namReport;

	@OneToMany(mappedBy = "paramReport")
	private List<ParamReportDetail> paramReportDetails;

	public String getNamReport() {
		return namReport;
	}

	public void setNamReport(String namReport) {
		this.namReport = namReport;
	}

	public List<ParamReportDetail> getParamReportDetails() {
		return paramReportDetails;
	}

	public void setParamReportDetails(List<ParamReportDetail> paramReportDetails) {
		this.paramReportDetails = paramReportDetails;
	}

	@Override
	public String toString() {
		return "ParamReport [namReport=" + namReport + ", id=" + id + ", createdDate=" + createdDate + ", modifiedDate="
				+ modifiedDate + ", createdUser=" + createdUser + ", note=" + note + "]";
	}
}
