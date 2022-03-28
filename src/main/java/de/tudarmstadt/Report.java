package de.tudarmstadt;

public class Report {
	private Integer companyId;
	private Integer reportId;
	private String reportUri;
	private String companyName;
	private String pdfPath;

	public Report() {
	}

	public Report(Integer companyId, Integer reportId, String reportUri, String companyName) {
		this.companyId = companyId;
		this.reportId = reportId;
		this.reportUri = reportUri;
		this.companyName = companyName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getReportUri() {
		return reportUri;
	}

	public void setReportUri(String reportUri) {
		this.reportUri = reportUri;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}
}