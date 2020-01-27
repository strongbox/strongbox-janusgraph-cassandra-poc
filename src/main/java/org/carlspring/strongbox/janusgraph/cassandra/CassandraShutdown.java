package org.carlspring.strongbox.janusgraph.cassandra;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.cassandra.service.CassandraDaemon;
import org.apache.cassandra.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * @author ankit.tomar
 */
@Configuration
public class CassandraShutdown
{

    private static final Logger logger = LoggerFactory.getLogger(CassandraShutdown.class);

    @Inject
    private CassandraDaemon cassandraDaemon;


    @PreDestroy
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
