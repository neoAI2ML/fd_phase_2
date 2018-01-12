package tomketao.fd_phase_2.exception;

public class FDException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public FDException() {
        super();
    }

    public FDException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FDException(final Throwable cause) {
        super(cause);
    }

    public FDException(final String msg) {
        super(msg);
    }

}