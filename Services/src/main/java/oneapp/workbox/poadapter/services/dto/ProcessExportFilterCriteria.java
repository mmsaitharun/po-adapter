package oneapp.workbox.poadapter.services.dto;

import java.util.Date;

public class ProcessExportFilterCriteria {

	private Integer maxResults;
	
	private Date startDateFrom;
	
	private Date startDateTo;

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	public Date getStartDateFrom() {
		return startDateFrom;
	}

	public void setStartDateFrom(Date startDateFrom) {
		this.startDateFrom = startDateFrom;
	}

	public Date getStartDateTo() {
		return startDateTo;
	}

	public void setStartDateTo(Date startDateTo) {
		this.startDateTo = startDateTo;
	}

	@Override
	public String toString() {
		return "ProcessExportFilterCriteria [maxResults=" + maxResults + ", startDateFrom=" + startDateFrom
				+ ", startDateTo=" + startDateTo + "]";
	}

	
	
}
