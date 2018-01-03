package oneapp.workbox.poadapter.util;

/**
 * <code>NonUniqueRecordFault</code> is to indicate application that the query
 * returns more than one record, sometimes its indicates an unique constraint
 * violation in the data source
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
public class NonUniqueRecordFault extends Exception {
	private static final long serialVersionUID = 3878903576084669146L;
	private MessageUIDto faultInfo;

	public NonUniqueRecordFault(String faultMessage) {
		super("Failed due to corrupt data, please contact db admin ");
		faultInfo = new MessageUIDto();
		faultInfo.setMessage(faultMessage);
	}

	public NonUniqueRecordFault(String message, MessageUIDto faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
	}

	public NonUniqueRecordFault(String message, MessageUIDto faultInfo, Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public MessageUIDto getFaultInfo() {
		return faultInfo;
	}
}