package org.carlspring.strongbox.janusgraph.cassandra;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.cassandra.service.CassandraDaemon;
import org.apache.cassandra.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ankit.tomar
 */
public class CassandraShutdown
{

    private static final Logger logger = LoggerFactory.getLogger(CassandraShutdown.class);

    private CassandraDaemon cassandraDaemon;

    public CassandraShutdown(CassandraDaemon cassandraDaemon)
    {
        this.cassandraDaemon = cassandraDaemon;
    }

    public void shutdown()
        throws IOException,
        InterruptedException,
        ExecutionException
    {
        logger.debug("Shutting down cassandra daemon..");
        StorageService.instance.drain();
        cassandraDaemon.deactivate();
    }
}
