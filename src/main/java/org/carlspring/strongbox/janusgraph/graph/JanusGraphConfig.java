package org.carlspring.strongbox.janusgraph.graph;

import lombok.extern.slf4j.Slf4j;
import org.apache.cassandra.service.EmbeddedCassandraService;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @author Przemyslaw Fusik
 */
@Slf4j
@Configuration
@ComponentScan
public class JanusGraphConfig
{
    @Value( "classpath:conf/jgex-cql.properties" )
    private Resource janusGraphConfigurationResource;

    @Bean
    public JanusGraph janusGraph( EmbeddedCassandraService cassandra ) throws IOException
    {
        JanusGraph janusGraph = JanusGraphFactory.open( janusGraphConfigurationResource.getFile().getAbsolutePath() );

        JanusGraphManagement janusGraphManagement = janusGraph.openManagement();

        log.info( janusGraphManagement.printSchema() );

        return janusGraph;
    }
}
