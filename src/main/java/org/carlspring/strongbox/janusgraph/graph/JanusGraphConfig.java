package org.carlspring.strongbox.janusgraph.graph;

import java.io.IOException;

import org.apache.cassandra.service.CassandraDaemon;
import org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedProperties;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(JanusGraphConfig.class);

    @Bean(destroyMethod = "close")
    public JanusGraph janusGraph(CassandraDaemon cassandra,
                                 CassandraEmbeddedProperties cassandraEmbeddedProperties)
        throws IOException
    {
        JanusGraph janusGraph = JanusGraphFactory.build()
                                                 .set("storage.backend", "cql")
                                                 .set("storage.hostname", "127.0.0.1")
                                                 .set("storage.port", cassandraEmbeddedProperties.getPort())
                                                 .set("gremlin.graph", "org.janusgraph.core.JanusGraphFactoryGraphTraversalSource")
                                                 .open();

        return janusGraph;
    }

    @Bean
    public JanusGraphManagement janusGraphManagement(JanusGraph janusGraph)
    {
        JanusGraphManagement janusGraphManagement = janusGraph.openManagement();

        logger.info(janusGraphManagement.printSchema());

        return janusGraphManagement;
    }
}
