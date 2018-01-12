package tomketao.fd_phase_2.util.httpclientpool;

import java.io.IOException;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

/**
 * Http client connection pool.
 * 
 * @author Infogroup
 */
public class FDHttpClientConnectionPoolImpl implements FDHttpClientConnectionPool {
    private IdleConnectionMonitorThread idleConnectionMonitorThread;
    private int connectionCountInPool;
    private int connectionCountPerRoute;
    private final PoolingHttpClientConnectionManager connManager;
    private final ConnectionKeepAliveStrategy connectionKeepAliveStrategy;
    private final CloseableHttpClient httpClient;

    public int getConnectionCountInPool() {
        return connectionCountInPool;
    }

    public void setConnectionCountInPool(final int connectionCountInPool) {
        this.connectionCountInPool = connectionCountInPool;
    }

    public int getConnectionCountPerRoute() {
        return connectionCountPerRoute;
    }

    public void setConnectionCountPerRoute(final int connectionCountPerRoute) {
        this.connectionCountPerRoute = connectionCountPerRoute;
    }

    public FDHttpClientConnectionPoolImpl(final int connectionCountInPool, final int connectionCountPerRoute) {
        this.connectionCountInPool = connectionCountInPool;
        this.connectionCountPerRoute = connectionCountPerRoute;

        connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(getConnectionCountInPool());
        connManager.setDefaultMaxPerRoute(getConnectionCountPerRoute());

        connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                // honors 'keep-alive' header from, e.g., SmartyStreet:
                HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement e = it.nextElement();
                    String value = e.getValue();
                    if (("timeout").equalsIgnoreCase(e.getName()) && value != null) {
                        try {
                            return Long.parseLong(value) * 1000;
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }

                // otherwise keep alive for 60 seconds:
                return 60000;
            }
        };

        httpClient = HttpClients.custom().setConnectionManager(connManager)
                .setKeepAliveStrategy(connectionKeepAliveStrategy).build();

        idleConnectionMonitorThread = new IdleConnectionMonitorThread(connManager);
        idleConnectionMonitorThread.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.infogroup.mini.framework.util.httpclientpool.MiniHttpClientConnectionPool
     * #getHttpGetResponse(org.apache.http.client.methods.HttpGet)
     */
    @Override
    public CloseableHttpResponse getHttpGetResponse(final HttpGet httpget) throws IOException {
        CloseableHttpResponse response;
        response = httpClient.execute(httpget, HttpClientContext.create());
        return response;
    }

    @Override
    public CloseableHttpResponse getHttpPutResponse(final HttpPut httpput) throws IOException {
        CloseableHttpResponse response;
        response = httpClient.execute(httpput, HttpClientContext.create());
        return response;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.infogroup.mini.framework.util.httpclientpool.MiniHttpClientConnectionPool
     * #getHttpPostResponse(org.apache.http.client.methods.HttpPost)
     */
    @Override
    public CloseableHttpResponse getHttpPostResponse(final HttpPost httppost) throws IOException {
        CloseableHttpResponse response;
        response = httpClient.execute(httppost, HttpClientContext.create());
        return response;
    }

    public void destroy() {

        idleConnectionMonitorThread.shutdown();

        try {
            idleConnectionMonitorThread.join(30000); // 30 seconds at most
        } catch (InterruptedException e) {
        }

        connManager.shutdown();
    }
}
