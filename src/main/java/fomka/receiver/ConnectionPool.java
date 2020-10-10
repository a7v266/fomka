package fomka.receiver;

import javax.mail.MessagingException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionPool {

    private final ConnectionConfig config;
    private final AtomicBoolean shutdown;

    public ConnectionPool(ConnectionConfig config) {
        this.config = config;
        shutdown = new AtomicBoolean();
    }

    public Connection getConnection() throws MessagingException, InterruptedException {
        if (shutdown.get()) {
            throw new IllegalStateException("Connection pool shutdown");
        }
        return new Connection(config);
    }


    public void shutdown() {
        shutdown.set(true);
    }
}
