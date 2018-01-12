package tomketao.fd_phase_2.util.httpclientpool.exception;

import tomketao.fd_phase_2.exception.FDException;


/**
 * Mini solr exception.
 * 
 * @author Infogroup
 * @since 1.0
 */
public class FDHttpClientConnectionPoolException extends FDException {
    public static final String POST_ERROR = "Post method exception";
    public static final String GET_ERROR = "Get method exception";
    private static final long serialVersionUID = 1L;

    public FDHttpClientConnectionPoolException(final String message) {
        super(message);
    }

}
