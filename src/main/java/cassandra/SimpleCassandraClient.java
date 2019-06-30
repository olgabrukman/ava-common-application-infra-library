package cassandra;

import com.datastax.driver.core.*;
import logger.AppLogger;

import java.io.Closeable;

public class SimpleCassandraClient implements Closeable {
    private static final AppLogger logger = AppLogger.getLogger(SimpleCassandraClient.class);

    private Cluster cluster;
    private Session session;

    public SimpleCassandraClient() throws Exception {

        String node = "";//CassandraHost
        int port = 0;
        int timeoutSeconds = 0;
        cluster = Cluster.builder().withPort(port).addContactPoint(node).build();
        cluster.getConfiguration().getSocketOptions().setReadTimeoutMillis(timeoutSeconds * 1000);
        Metadata metadata = cluster.getMetadata();
        logger.debug("Connected to cluster: {}", metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            logger.debug("Data center {} host: {} rack: {}", host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect();
    }

    public void close() {
        session.close();
        session = null;
        cluster.close();
        cluster = null;
    }

    public PreparedStatement prepare(String cql) {
        logger.debug("preparing csql {}", cql);
        return session.prepare(cql);
    }

    public ResultSet execute(Statement statement) throws Exception {
        return session.execute(statement);
    }

    public ResultSet execute(String cql) {
        logger.debug("executing csql {}", cql);
        return session.execute(cql);
    }
}
