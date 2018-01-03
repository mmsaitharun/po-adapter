package oneapp.workbox.poadapter.util;

/**
 * <code>InvalidInputFault</code> is to indicate application that the parameters
 * passed to the method is invalid w.r.t its implementation.
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
public class InvalidInputFault extends Exception {

	private static final long serialVersionUID = -5879608670346636459L;
	private MessageUIDto faultInfo;

	public InvalidInputFault(String faultMessage) {
		super(faultMessage);
		faultInfo = new MessageUIDto();
		faultInfo.setMessage(faultMessage);
	}

	public InvalidInputFault(String message, MessageUIDto faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
	}

	public InvalidInputFault(String message, MessageUIDto faultInfo, Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public MessageUIDto getFaultInfo() {
		return faultInfo;
	}
}