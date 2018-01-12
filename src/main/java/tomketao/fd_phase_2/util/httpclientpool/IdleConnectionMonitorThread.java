/**
 * 
 */
package tomketao.fd_phase_2.util.httpclientpool;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Infogroup
 */
public class IdleConnectionMonitorThread extends Thread {
    private final PoolingHttpClientConnectionManager connManager;
    private volatile boolean shutdown = false;

    private final Logger logger = LoggerFactory.getLogger(IdleConnectionMonitorThread.class);

    public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager connManager) {
        super();
        this.connManager = connManager;
        this.setName("idleConnectionMonitorThread");
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    // wait every 60 seconds:
                    wait(60000);
                    long startTime = System.currentTimeMillis();

                    // close expired connections:
                    connManager.closeExpiredConnections();
                    // close connections idle longer than 60 sec:
                    connManager.closeIdleConnections(60, TimeUnit.SECONDS);

                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime > 1000) {
                        logger.info("Milliseconds took to clean up connection pool: " + (endTime - startTime));
                    } else if (logger.isDebugEnabled()) {
                        logger.debug("Milliseconds took to clean up connection pool: " + (endTime - startTime));
                    }
                }
            }
        } catch (InterruptedException e) {
            // terminate
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
