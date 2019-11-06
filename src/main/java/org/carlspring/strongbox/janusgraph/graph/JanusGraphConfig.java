package org.carlspring.strongbox.janusgraph.graph;

import java.io.IOException;

import org.apache.cassandra.service.CassandraDaemon;
import org.apache.cassandra.service.EmbeddedCassandraService;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * @author Przemyslaw Fusik
 */
@Configuration
@ComponentScan
public class JanusGraphConfig
{

    private static final Logger logger = LoggerFactory.getLogger(JanusGraphConfig.class);

    @Value("classpath:conf/jgex-cql.properties")
    private Resource janusGraphConfigurationResource;

    @Bean(destroyMethod = "close")
    public JanusGraph janusGraph(CassandraDaemon cassandra)
        throws IOException
    {
        JanusGraph janusGraph = JanusGraphFactory.open(janusGraphConfigurationResource.getFile().getAbsolutePath());

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
