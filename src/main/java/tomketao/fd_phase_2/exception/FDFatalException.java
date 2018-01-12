package tomketao.fd_phase_2.exception;

public class FDFatalException extends FDException {
	
	private static final long serialVersionUID = 1L;
	
	public FDFatalException() {
        super();
    }

    public FDFatalException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FDFatalException(final Throwable cause) {
        super(cause);
    }

    public FDFatalException(final String msg) {
        super(msg);
    }

}
