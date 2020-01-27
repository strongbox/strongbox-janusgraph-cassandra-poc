package org.carlspring.strongbox.janusgraph.graph;

import java.lang.reflect.Field;

import org.apache.cassandra.service.CassandraDaemon;
import org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedProperties;
import org.carlspring.strongbox.janusgraph.cassandra.CassandraShutdown;

import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Przemyslaw Fusik
 */
@Configuration
@ComponentScan
public class JanusGraphConfig
{

    @Bean(destroyMethod = "close")
    public JanusGraph janusGraph(CassandraDaemon cassandra,
                                 CassandraEmbeddedProperties cassandraEmbeddedProperties,
                                 CassandraShutdown cassandraShutdown)
        throws NoSuchFieldException,
        IllegalAccessException
    {
        JanusGraph graph = JanusGraphFactory.build()
                                            .set("storage.backend", "cql")
                                            .set("storage.hostname", "127.0.0.1")
                                            .set("storage.port", cassandraEmbeddedProperties.getPort())
                                            .set("storage.cql.keyspace", "jgex")
                                            .set("tx.log-tx", true)
                                            .open();

        // Remove JanusGraph shutdown hook to allow Spring context shutdown hook
        // to shutdown embedded components in an orderly fashion.
        Field shutdownHookField = graph.getClass().getDeclaredField("shutdownHook");
        shutdownHookField.setAccessible(true);

        Runtime.getRuntime().removeShutdownHook((Thread) shutdownHookField.get(graph));
        shutdownHookField.set(graph, null);

        return graph;
    }

}
