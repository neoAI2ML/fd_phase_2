package tomketao.fd_phase_2.util.httpclientpool;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

/**
 * 
 * @author Infogroup
 * 
 */
public interface FDHttpClientConnectionPool {

    CloseableHttpResponse getHttpGetResponse(HttpGet httpget) throws IOException;

    CloseableHttpResponse getHttpPostResponse(HttpPost httppost) throws IOException;

	CloseableHttpResponse getHttpPutResponse(HttpPut httpput)
			throws IOException;

}