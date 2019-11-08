package org.carlspring.strongbox.janusgraph.graph;

import org.apache.cassandra.service.CassandraDaemon;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedProperties;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.neo4j.driver.v1.Driver;
import org.opencypher.gremlin.neo4j.driver.GremlinDatabase;
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
                                 CassandraEmbeddedProperties cassandraEmbeddedProperties)
    {
        return JanusGraphFactory.build()
                                .set("storage.backend", "cql")
                                .set("storage.hostname", "127.0.0.1")
                                .set("storage.port", cassandraEmbeddedProperties.getPort())
                                .set("gremlin.graph", "org.janusgraph.core.JanusGraphFactoryGraphTraversalSource")
                                .open();
    }

    @Bean
    public GraphTraversalSource traversalSource(JanusGraph jg)
    {
        return jg.traversal();
    }

    @Bean
    public Driver neoj4Driver(GraphTraversalSource g)
    {
        return GremlinDatabase.driver(g);
    }

}
