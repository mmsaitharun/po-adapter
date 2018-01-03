package oneapp.workbox.poadapter.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <h1>ResponseDto Class Implementation</h1> No validation parameters are
 * enforced
 * 
 * @author INC00609
 * @version 1.0
 * @since 2017-11-12
 */
@XmlRootElement
public class ResponseDto {
	
	private String status;
	private String statusCode;
	private String message;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "ResponseDto [status=" + status + ", statusCode=" + statusCode + ", message=" + message + "]";
	}

}
