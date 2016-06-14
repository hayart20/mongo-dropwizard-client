package am.developer.client.exception;

/**
 * Thrown upon an error when REST Client performs an operation.
 * The error type indicates a kind of operation that caused the error.
 * The string message contains detailed information on the error.
 */
public class PortalRestClientException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private ErrorType type;
	
	public PortalRestClientException() {
		super();
	}

	public PortalRestClientException(String message, Throwable cause, ErrorType type) {
		super(message, cause);
		this.setType(type);
	}

	public PortalRestClientException(String message, ErrorType type) {
		super(message);
		this.setType(type);
	}

	public PortalRestClientException(Throwable cause) {
		super(cause);
	}

	public ErrorType getType() {
		return type;
	}

	public void setType(ErrorType type) {
		this.type = type;
	}
	
	
}
